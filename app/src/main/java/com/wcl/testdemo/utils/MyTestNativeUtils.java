package com.wcl.testdemo.utils;

/**
 * @Author WCL
 * @Date 2023/5/30 14:43
 * @Version
 * @Description 测试与Native层的互调.
 */
public class MyTestNativeUtils {

    static {
        System.loadLibrary("my-test");
    }

    public native String stringFromJNI();

    public native void testLog();

    public native void logD(String msg);

}
