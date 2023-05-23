package com.wcl.testdemo.test.test06_audio_video.test02;

import com.blankj.utilcode.util.LogUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @Author WCL
 * @Date 2023/5/16 10:18
 * @Version
 * @Description 视频通话-客户端Socket.
 */
class SocketLiveClient implements ISocketLive {

    /**
     * Comment:要连接的服务端的IP.
     */
    private final String mServerIP;
    /**
     * Comment:回调接口,回调客户端端接收到的服务端数据.
     */
    private ISocketLive.SocketCallback mSocketCallback;
    /**
     * Comment:客户端WebSocket.
     */
    MyWebSocketClient mMyWebSocketClient;

    public SocketLiveClient(String serverIP, ISocketLive.SocketCallback socketCallback) {
        mServerIP = serverIP;
        this.mSocketCallback = socketCallback;
    }

    @Override
    public void start() {
        try {
//            URI url = new URI("ws://192.168.18.52:40002");
            URI url = new URI("ws://" + mServerIP + ":40002");
            mMyWebSocketClient = new MyWebSocketClient(url);
            mMyWebSocketClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        mMyWebSocketClient.close();
    }

    @Override
    public void sendData(byte[] bytes) {
        if (mMyWebSocketClient != null && (mMyWebSocketClient.isOpen())) {
            mMyWebSocketClient.send(bytes);
        }
    }

    /**
     * @Author WCL
     * @Date 2023/5/16 10:18
     * @Version
     * @Description
     */
    private class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(URI serverURI) {
            super(serverURI);
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            LogUtils.i("客户端: 打开socket");
        }

        @Override
        public void onMessage(String s) {
        }

        @Override
        public void onMessage(ByteBuffer bytes) {
            LogUtils.i("客户端: 消息长度-" + bytes.remaining());
            byte[] buf = new byte[bytes.remaining()];
            bytes.get(buf);
            mSocketCallback.callBack(buf);
        }

        @Override
        public void onClose(int i, String s, boolean b) {
            LogUtils.i("客户端: 关闭socket", i, s, b);
        }

        @Override
        public void onError(Exception e) {
            LogUtils.e("客户端: 出错socket", e);
        }
    }

}
