package com.wcl.testdemo.test.test04_device.test04.tp.client;


import android.view.Surface;

import java.net.URI;
import java.net.URISyntaxException;

import static com.wcl.testdemo.test.test04_device.test04.tp.constant.SOCKET_PORT;

/**
 * @Author WCL
 * @Date 2023/4/27 18:11
 * @Version
 * @Description 投屏客户端:管理器.
 */
public class TpClientManager implements TpClientSocket.SocketCallback {

    private TpClientDecoder mTpClientDecoder;
    private TpClientSocket mSocketClient;

    public void start(String ip, Surface surface) {
        mTpClientDecoder = new TpClientDecoder();
        mTpClientDecoder.startDecode(surface);
        try {
            URI uri = new URI("ws://" + ip + ":" + SOCKET_PORT);// 需要修改为服务端的IP地址与端口
            mSocketClient = new TpClientSocket(this, uri);
            mSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (mSocketClient != null) {
            mSocketClient.close();
        }
        if (mTpClientDecoder != null) {
            mTpClientDecoder.stopDecode();
        }
    }

    @Override
    public void onReceiveData(byte[] data) {
        if (mTpClientDecoder != null) {
            mTpClientDecoder.decodeData(data);
        }
    }
}