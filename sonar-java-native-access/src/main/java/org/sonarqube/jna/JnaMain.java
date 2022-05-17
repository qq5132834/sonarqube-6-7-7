package org.sonarqube.jna;

public class JnaMain {
    public static void main(String[] args) {
        System.out.println("hello world.");
        //DeleteFile.deleteFile.DeleteFileA("C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\hello.txt");
        SayHello s = SayHello.sayHello;
        s.Hello();
    }
}
