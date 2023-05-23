package com.wcl.testdemo.test.test06_audio_video.test00.tp.service;

import android.media.projection.MediaProjection;

import java.net.InetSocketAddress;

import static com.wcl.testdemo.test.test06_audio_video.test00.tp.constant.SOCKET_PORT;

/**
 * @Author WCL
 * @Date 2023/4/27 15:46
 * @Version
 * @Description 投屏服务端:管理器.
 */
class TpServerManager {

    /**
     * Comment:投屏服务端-Socket.
     */
    private final TpServerSocket mTpServerSocket;
    /**
     * Comment:投屏服务端-编码器.
     */
    private TpServerEncoder mTpServerEncoder;


    public TpServerManager() {
        mTpServerSocket = new TpServerSocket(new InetSocketAddress(SOCKET_PORT));//创建服务端Socket.
    }

    /**
     * 开始录屏,并编码发送.
     * (1)启动服务端Socket.
     * (2)启动编码器.
     *
     * @param mediaProjection 屏幕录制器.
     */
    public void start(MediaProjection mediaProjection) {
        mTpServerSocket.start();
        mTpServerEncoder = new TpServerEncoder(this, mediaProjection);
        mTpServerEncoder.startEncode();
    }

    /**
     * 结束录屏,并停止编码发送.
     * (1)关闭服务端Socket.
     * (2)关闭编码器.
     */
    public void close() {
        try {
            mTpServerSocket.stop();
            mTpServerSocket.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mTpServerEncoder != null) {
            mTpServerEncoder.stopEncode();
        }
    }

    /**
     * 发送数据.
     *
     * @param bytes 编码好的数据.
     */
    public void sendData(byte[] bytes) {
        mTpServerSocket.sendData(bytes);
    }

}
