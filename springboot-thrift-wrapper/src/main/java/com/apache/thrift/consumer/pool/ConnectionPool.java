package com.apache.thrift.consumer.pool;


import lombok.Getter;
import lombok.Setter;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by zhuangjiesen on 2017/4/30.
 */
@Getter
@Setter
public class ConnectionPool {

    private SocketBuilder socketBuilder;
    private TTransportFactory transportFactory;
    private TProtocolFactory protocolFactory;

    private int minConnections = 5;
    private int maxConnections = 100;

    //线程轮询时间 秒
    private final int recycle_time = 5;

    // 原子类型 连接总数
    private volatile AtomicInteger connectionsCount = new AtomicInteger(0);

    // 等待响应时间 秒
    private int waitTimeout = 300;

    // 线程等待时间 （等待连接池 ） 秒
    private int waitQueueSeconds = 10;

    //保持存活的连接时间 秒 根据 Lru 时间
    private int keepAlive = 5;

    // TProtocol 连接
    private Map<Class, LinkedBlockingQueue<Connection>> blockingQueueMap;

    //公平锁 排队处理
    private final Lock threadLock = new ReentrantLock(true);

    private final Lock createLock = new ReentrantLock(true);


    private ThreadLocal<Connection> protocolLocal = new ThreadLocal<Connection>();


    //回收线程
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public TProtocol getProtocol(Class iface) {

        return new TMultiplexedProtocol(getProtocolInternal(iface), iface.getName());
    }


    //从连接池中获取Protocol
    public TProtocol getProtocolInternal(Class iface) {

        protocolLocal.remove();

        TProtocol protocol = null;

        if (!blockingQueueMap.containsKey(iface)) {
            blockingQueueMap.put(iface, new LinkedBlockingQueue<>());
        }

        LinkedBlockingQueue<Connection> blockingQueue = blockingQueueMap.get(iface);

        Connection connection = blockingQueue.poll();
        if (connection != null) {
            protocol = connection.getProtocol();
            if (protocol != null && (!protocol.getTransport().isOpen())) {
                //取到 protocol 但是已经关闭 重新创建
                protocol = createNewProtocol(iface);
                connection.setProtocol(protocol);
            }

        } else {
            protocol = createNewProtocol(iface);
            if (protocol != null) {
                //创建新的
                connection = new Connection();
                connection.setProtocol(protocol);
                connection.setIface(iface);
            } else {
                //没有就等待队列处理
                threadLock.lock();
                try {
//                    waitTimeout
                    // waitTimeout 等待
                    connection = blockingQueue.poll(waitTimeout, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    threadLock.unlock();
                }
                if (connection == null) {
                    throw new RuntimeException("连接等待超时");
                }

                protocol = connection.getProtocol();
                if (protocol != null && (!protocol.getTransport().isOpen())) {
                    //取到 protocol 但是已经关闭 重新创建
                    protocol = createNewProtocol(connection.getIface());
                    connection.setProtocol(protocol);
                }
            }

        }


        if (connection != null) {
            protocol = connection.getProtocol();
        }
        protocolLocal.set(connection);
        return protocol;
    }


    public void recycleProtocol() {

        Connection connection = protocolLocal.get();

        if (connection != null) {
            TProtocol protocol = connection.getProtocol();

            if (connection != null) {

                // Transport 已经关闭 重新创建
                if (protocol.getTransport() != null && (!protocol.getTransport().isOpen())) {
                    protocol = createNewProtocol(connection.getIface());
                    connection.setProtocol(protocol);
                }

                blockingQueueMap.get(connection.getIface()).add(connection);
            }

            protocolLocal.remove();
        }
    }


    //创建协议
    public TProtocol createNewProtocol(Class iface) {

        TProtocol protocol = null;

        createLock.lock();
        try {
            if (connectionsCount.get() < maxConnections) {

                TSocket socket = socketBuilder.newSocket(iface);
                TTransport transport = transportFactory.getTransport(socket);
                protocol = protocolFactory.getProtocol(transport);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (protocol != null) {
                connectionsCount.incrementAndGet();
                try {
                    protocol.getTransport().open();
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
            }
            createLock.unlock();
        }

        return protocol;
    }


    public void initDefault() {
    }

    //初始化连接池
    public synchronized void init() {

        blockingQueueMap = new HashMap<>();

        //回收线程
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            reducePool();
        }, recycle_time, recycle_time, TimeUnit.SECONDS);
    }

    /*
    * 定时器处理多出的连接长期不适用 lru
    * */
    private void reducePool() {

        for(LinkedBlockingQueue<Connection> blockingQueue : blockingQueueMap.values()) {

            if (connectionsCount.get() > minConnections) {

                Connection Connection = blockingQueue.peek();
                long nowTime = System.currentTimeMillis();

                int count = connectionsCount.get();
                while (Connection != null) {
                    if (connectionsCount.get() <= minConnections) {
                        break;
                    }
                    //遍历完出队列
                    if (count < 0) {
                        break;
                    }

                    long keepAliveTime = keepAlive * 1000;

                    long lru = Connection.getLru();

                    //已经 有 keepAlive 秒没有用到了  连接关闭
                    if ((nowTime - lru) > keepAliveTime) {

                        //System.out.println("开始回收连接...");
                        try {
                            TProtocol protocol = Connection.getProtocol();
                            //关闭连接
                            if (protocol != null && protocol.getTransport() != null && (protocol.getTransport().isOpen())) {
                                protocol.getTransport().close();
                            }
                        } finally {
                            blockingQueue.poll();
                            //连接减一
                            connectionsCount.decrementAndGet();
                        }
                    }

                    Connection = blockingQueue.peek();
                    count--;
                }
            }
        }
    }

    //关闭连接
    public synchronized void onDestroy() {

        for(LinkedBlockingQueue<Connection> blockingQueue : blockingQueueMap.values()) {

            try {

                Connection Connection = blockingQueue.take();
                while (Connection != null) {
                    if (Connection.getProtocol().getTransport().isOpen()) {
                        Connection.getProtocol().getTransport().close();
                    }
                    Connection = blockingQueue.take();
                    connectionsCount.decrementAndGet();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void afterPropertiesSet() throws Exception {
    }
}
