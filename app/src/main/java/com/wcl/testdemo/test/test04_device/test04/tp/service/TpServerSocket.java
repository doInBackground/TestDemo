package com.wcl.testdemo.test.test04_device.test04.tp.service;

import com.blankj.utilcode.util.LogUtils;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

/**
 * @Author WCL
 * @Date 2023/4/27 16:42
 * @Version
 * @Description 投屏服务端:Socket.
 */
public class TpServerSocket extends WebSocketServer {

    private WebSocket mWebSocket;

    public TpServerSocket(InetSocketAddress inetSocketAddress) {
        super(inetSocketAddress);
    }

    @Override
    public void onStart() {
        LogUtils.d("投屏服务端-Socket: onStart()");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake clientHandshake) {
        LogUtils.d("投屏服务端-Socket: onOpen()", conn, clientHandshake);
        mWebSocket = conn;
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        LogUtils.d("投屏服务端-Socket: onMessage()");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LogUtils.d("投屏服务端-Socket: onClose()");
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        LogUtils.e("投屏服务端-Socket: onError()", conn, ex);
    }


    /**
     * ServerSocket发送数据.
     *
     * @param bytes byte数据
     */
    public void sendData(byte[] bytes) {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            LogUtils.d("投屏服务端-Socket: sendData()");
            mWebSocket.send(bytes);
        }
    }

    /**
     * 关闭ServerSocket.
     */
    public void close() {
        mWebSocket.close();
    }

}