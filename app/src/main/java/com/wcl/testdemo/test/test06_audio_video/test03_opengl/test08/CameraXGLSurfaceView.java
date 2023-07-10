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
    private CameraRender mRenderer;//渲染器.
    private Speed mSpeed = Speed.MODE_NORMAL;

    public CameraXGLSurfaceView(Context context) {
        super(context);
    }

    public CameraXGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);//(1)OpenGL设置版本.
        mRenderer = new CameraRender(this, sFilterTypeArr);
        setRenderer(mRenderer);//(2)设置渲染器.
        /**
         * 设置渲染方式(有手动和自动两种).
         * 注意必须"setRenderer()设置渲染器"后才能"设置渲染方式setRenderMode()".
         *
         * 渲染方式:
         * RENDERMODE_WHEN_DIRTY: 表示手动渲染,只有在调用requestRender()或者onResume()等方法时,才会回调一次渲染器的onDrawFrame()进行渲染.
         * RENDERMODE_CONTINUOUSLY: 表示自动渲染,大概16ms会自动回调一次渲染器的onDrawFrame()进行渲染.
         */
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//(3)设置渲染方式(有手动和自动两种).
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
        mRenderer.startRecord(speed);
    }

    /**
     * 停止录制.
     */
    public void stopRecord() {
        mRenderer.stopRecord();
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
        mRenderer.enableBeauty(isChecked);
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
