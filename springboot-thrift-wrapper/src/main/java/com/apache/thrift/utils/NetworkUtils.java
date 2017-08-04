package com.apache.thrift.utils;

import lombok.extern.log4j.Log4j;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Log4j
public class NetworkUtils {

    private static String localAddress;
    private static String hostName;

    public static synchronized String getLocalAddress() {

        if (localAddress == null) {

            try {

                for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements();) {

                    NetworkInterface item = e.nextElement();

                    for (InterfaceAddress address : item.getInterfaceAddresses()) {

                        if (address.getAddress() instanceof Inet4Address) {

                            Inet4Address inet4Address = (Inet4Address) address.getAddress();
                            if(inet4Address.isLoopbackAddress() == false) {

                                localAddress = inet4Address.getHostAddress();
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {

                localAddress = "";

                log.error("get local address failed.", e);
            }

        }

        return localAddress;
    }

    public synchronized static String getLocalHostName() {

        if (hostName == null) {

            try {

                InetAddress addr = InetAddress.getLocalHost();

                hostName = addr.getHostName();
            } catch (Exception e) {

                hostName = "";

                log.error("get local host name failed.", e);
            }
        }

        return hostName;
    }
}
