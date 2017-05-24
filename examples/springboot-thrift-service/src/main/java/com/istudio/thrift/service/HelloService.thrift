namespace java com.istudio.thrift.service

service HelloService {

    i32 sayInt(1:i32 param)

    string sayString(1:string param)

    bool sayBoolean(1:bool param)

    void sayVoid()
}