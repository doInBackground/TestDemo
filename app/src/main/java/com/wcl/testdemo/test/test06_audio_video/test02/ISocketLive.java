package com.wcl.testdemo.test.test06_audio_video.test02;

/**
 * @Author WCL
 * @Date 2023/5/16 11:00
 * @Version
 * @Description Socket通用方法接口.
 */
interface ISocketLive {

    void start();

    void close();

    void sendData(byte[] bytes);

    /**
     * @Author WCL
     * @Date 2023/5/16 11:04
     * @Version
     * @Description
     */
    interface SocketCallback {
        void callBack(byte[] data);
    }

}
