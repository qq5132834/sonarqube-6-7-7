package org.sonarqube.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface SayHello extends Library {
    SayHello sayHello = Native.loadLibrary("D:\\development\\vs\\ClassLibrary1\\ClassLibrary1\\bin\\Debug\\netcoreapp3.1\\ClassLibrary1", SayHello.class); //调用Kernel32.dll 执行文件删除
    void Hello();
}
