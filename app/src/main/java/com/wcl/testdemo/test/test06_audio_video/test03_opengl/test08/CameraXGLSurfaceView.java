package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import static com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.CameraRender.FilterEnum.BEAUTY;
import static com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.CameraRender.FilterEnum.CAMERA;
import static com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.CameraRender.FilterEnum.SHOW;

/**
 * @Author WCL
 * @Date 2023/5/18 10:11
 * @Version
 * @Description 用来展示经过处理的摄像头数据的GLSurfaceView.
 * GLSurfaceView:比普通的SurfaceView多了GLThread 线程.
 */
public class CameraXGLSurfaceView extends GLSurfaceView {

    private static CameraRender.FilterEnum[] sFilterTypeArr = {CAMERA, SHOW, BEAUTY};//想要使用的滤镜集合.
    private CameraRender renderer;
    private Speed mSpeed = Speed.MODE_NORMAL;

    public CameraXGLSurfaceView(Context context) {
        super(context);
    }

    public CameraXGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);//设置版本.
        renderer = new CameraRender(this, sFilterTypeArr);
        setRenderer(renderer);
        /**
         * 刷新方式：
         *     RENDERMODE_WHEN_DIRTY 手动刷新，調用requestRender();
         *     RENDERMODE_CONTINUOUSLY 自動刷新，大概16ms自動回調一次onDrawFrame方法;
         */
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//注意必须在setRenderer()后面.
    }

    /**
     * 开始录制.
     */
    public void startRecord() {
        //速度speed小于1就是放慢,大于1就是加快.
        float speed = 1.f;
        switch (mSpeed) {
            case MODE_EXTRA_SLOW:
                speed = 0.3f;
                break;
            case MODE_SLOW:
                speed = 0.5f;
                break;
            case MODE_NORMAL:
                speed = 1.f;
                break;
            case MODE_FAST:
                speed = 2.f;
                break;
            case MODE_EXTRA_FAST:
                speed = 3.f;
                break;
        }
        renderer.startRecord(speed);
    }

    /**
     * 停止录制.
     */
    public void stopRecord() {
        renderer.stopRecord();
    }

    /**
     * 设置速度.
     *
     * @param speed 速度
     */
    public void setSpeed(Speed speed) {
        this.mSpeed = speed;
    }

    /**
     * 开启或关闭美颜.
     *
     * @param isChecked 是否开启
     */
    public void enableBeauty(boolean isChecked) {
        renderer.enableBeauty(isChecked);
    }

    /**
     * 设置想要使用的滤镜集合.
     *
     * @param filterTypeArr 滤镜集合
     */
    public static void setFilterType(CameraRender.FilterEnum[] filterTypeArr) {
        CameraXGLSurfaceView.sFilterTypeArr = filterTypeArr;
    }

    /**
     * @Author WCL
     * @Date 2023/5/24 13:54
     * @Version
     * @Description 枚举: 速度.
     */
    public enum Speed {
        MODE_EXTRA_SLOW, MODE_SLOW, MODE_NORMAL, MODE_FAST, MODE_EXTRA_FAST
    }

}
