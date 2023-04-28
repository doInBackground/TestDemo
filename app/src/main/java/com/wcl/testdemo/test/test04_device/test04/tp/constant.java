package com.wcl.testdemo.test.test04_device.test04.tp;

/**
 * @Author WCL
 * @Date 2023/4/27 15:44
 * @Version
 * @Description 常量类.
 */
public interface constant {

    /**
     * Comment:Socket端口号.
     */
    int SOCKET_PORT = 50000;

    /**
     * Comment:录屏宽.
     * 不同手机支持的编码最大分辨率不同.
     */
    int VIDEO_WIDTH = 2160;//服务端客户端两边要统一,否则无法解码,故不能用各自的屏幕宽高.ScreenUtils.getScreenWidth();

    /**
     * Comment:录屏高.
     * 不同手机支持的编码最大分辨率不同.
     */
    int VIDEO_HEIGHT = 3840;//服务端客户端两边要统一,否则无法解码,故不能用各自的屏幕宽高.ScreenUtils.getScreenHeight();

    /**
     * Comment:编解码器的帧率.
     */
    int SCREEN_FRAME_RATE = 20;

    /**
     * Comment:I帧出现的频率.
     */
    int SCREEN_FRAME_INTERVAL = 1;

    /**
     * Comment:MediaCodec超时时长(微秒).
     */
    long CODEC_TIME_OUT = 10000;
}
