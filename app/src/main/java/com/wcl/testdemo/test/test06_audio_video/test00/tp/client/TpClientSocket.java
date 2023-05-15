package com.wcl.testdemo.test.test06_audio_video.test00.tp.client;

import com.blankj.utilcode.util.LogUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @Author WCL
 * @Date 2023/4/27 18:05
 * @Version
 * @Description 投屏客户端:Socket.
 */
public class TpClientSocket extends WebSocketClient {

    /**
     * Comment:客户端收到消息的回调.
     */
    private final SocketCallback mSocketCallback;

    public TpClientSocket(SocketCallback socketCallback, URI serverUri) {
        super(serverUri);
        mSocketCallback = socketCallback;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        LogUtils.d("投屏客户端-Socket: onOpen()", serverHandshake);
    }

    @Override
    public void onMessage(String message) {
        LogUtils.d("投屏客户端-Socket: onMessage(String)");
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        LogUtils.d("投屏客户端-Socket: onMessage(ByteBuffer)");
        byte[] buf = new byte[bytes.remaining()];
        bytes.get(buf);
        if (mSocketCallback != null) {
            mSocketCallback.onReceiveData(buf);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LogUtils.d("投屏客户端-Socket: onClose()", code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        LogUtils.e("投屏客户端-Socket: onOpen()", ex);
    }

    /**
     * @Author WCL
     * @Date 2023/4/27 18:05
     * @Version
     * @Description 客户端收到消息的回调.
     */
    public interface SocketCallback {
        void onReceiveData(byte[] data);
    }

}