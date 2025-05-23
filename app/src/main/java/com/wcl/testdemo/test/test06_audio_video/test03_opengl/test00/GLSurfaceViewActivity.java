package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test00;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.wcl.testdemo.R;

import com.wcl.testdemo.init.BaseActivity;

/**
 * @Author WCL
 * @Date 2023/5/17 10:26
 * @Version
 * @Description GLSurfaceView界面.
 */
public class GLSurfaceViewActivity extends BaseActivity {

    private GLSurfaceView mGLSurfaceView;
    private static GLSurfaceView.Renderer mRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glsurface_view);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    //初始化控件.
    private void initView() {
        mGLSurfaceView = findViewById(R.id.glSurfaceView);
        mGLSurfaceView.setEGLContextClientVersion(2);//GLContext设置OpenGLES2.0
        mGLSurfaceView.setRenderer(mRenderer);
        /*
        渲染方式:
        RENDERMODE_WHEN_DIRTY: 表示手动渲染,只有在调用requestRender()或者onResume()等方法时才会进行渲染.
        RENDERMODE_CONTINUOUSLY: 表示自动渲染.
        */
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    /**
     * 为 GLSurfaceView界面设置Renderer.
     *
     * @param renderer Renderer
     */
    public static void setRender(GLSurfaceView.Renderer renderer) {
        mRenderer = renderer;
    }

}