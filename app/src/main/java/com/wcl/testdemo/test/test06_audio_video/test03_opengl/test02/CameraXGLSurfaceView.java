package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test02;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * @Author WCL
 * @Date 2023/5/18 10:11
 * @Version
 * @Description 用来展示经过处理的摄像头数据的GLSurfaceView.
 * GLSurfaceView:比普通的SurfaceView多了GLThread 线程.
 */
public class CameraXGLSurfaceView extends GLSurfaceView {

    private CameraRender renderer;

    public CameraXGLSurfaceView(Context context) {
        super(context);
    }

    public CameraXGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);//设置版本.
        renderer = new CameraRender(this);
        setRenderer(renderer);
        /**
         * 刷新方式：
         *     RENDERMODE_WHEN_DIRTY 手动刷新，調用requestRender();
         *     RENDERMODE_CONTINUOUSLY 自動刷新，大概16ms自動回調一次onDrawFrame方法;
         */
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//注意必须在setRenderer()后面.
    }

}
