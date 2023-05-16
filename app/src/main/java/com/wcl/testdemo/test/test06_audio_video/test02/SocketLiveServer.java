package com.wcl.testdemo.test.test06_audio_video.test02;

import com.blankj.utilcode.util.LogUtils;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @Author WCL
 * @Date 2023/5/16 10:27
 * @Version
 * @Description 视频通话-服务端Socket.
 */
public class SocketLiveServer implements ISocketLive {

    /**
     * Comment:连接进来的客户端WebSocket.
     */
    private WebSocket mWebSocket;
    /**
     * Comment:回调接口,回调服务端接收到的客户端数据.
     */
    private ISocketLive.SocketCallback mSocketCallback;

    public SocketLiveServer(ISocketLive.SocketCallback socketCallback) {
        this.mSocketCallback = socketCallback;
    }

    @Override
    public void start() {
        mWebSocketServer.start();
    }

    @Override
    public void close() {
        try {
            mWebSocket.close();
            mWebSocketServer.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void sendData(byte[] bytes) {
        if (mWebSocket != null && mWebSocket.isOpen()) {
            mWebSocket.send(bytes);
        }
    }

    /**
     * Comment:服务端.
     */
    private WebSocketServer mWebSocketServer = new WebSocketServer(new InetSocketAddress(40002)) {
        @Override
        public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
            SocketLiveServer.this.mWebSocket = webSocket;
        }

        @Override
        public void onClose(WebSocket webSocket, int i, String s, boolean b) {
            LogUtils.i("服务端: 关闭socket", i, s, b);
        }

        @Override
        public void onMessage(WebSocket webSocket, String s) {

        }

        @Override
        public void onMessage(WebSocket conn, ByteBuffer bytes) {
            LogUtils.i("服务端: 消息长度-" + bytes.remaining());
            byte[] buf = new byte[bytes.remaining()];//创建数组容器.
            bytes.get(buf);//数组容器接收客户端传来的数据.
            mSocketCallback.callBack(buf);//回调.
        }

        @Override
        public void onError(WebSocket webSocket, Exception e) {
            LogUtils.e("服务端: 出错socket", webSocket, e);
        }

        @Override
        public void onStart() {

        }
    };

}
