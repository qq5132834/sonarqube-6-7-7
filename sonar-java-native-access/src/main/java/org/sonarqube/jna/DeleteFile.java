package org.sonarqube.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface DeleteFile extends Library {
    DeleteFile deleteFile = Native.load("Kernel32", DeleteFile.class); //调用Kernel32.dll 执行文件删除
    boolean DeleteFileA(String lpFileName);

}
