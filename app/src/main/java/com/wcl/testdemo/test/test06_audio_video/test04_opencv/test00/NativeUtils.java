package com.wcl.testdemo.test.test06_audio_video.test04_opencv.test00;

import android.view.Surface;

/**
 * @Author WCL
 * @Date 2023/5/26 16:07
 * @Version
 * @Description C, C++与Java交互工具类.
 */
public class NativeUtils {

    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 初始化训练数据.
     *
     * @param model 训练数据路径
     */
    public native void init(String model);

    /**
     * 发送摄像头的数据到native层,让OpenCV去识别.
     *
     * @param data     摄像头数据
     * @param w        宽
     * @param h        高
     * @param cameraId 前置或后置摄像头
     */
    public native void postData(byte[] data, int w, int h, int cameraId);

    /**
     * 设置Surface展示画面.
     *
     * @param surface 画布
     */
    public native void setSurface(Surface surface);

}
