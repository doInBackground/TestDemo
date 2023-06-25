package com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test04.opengl;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MNGLSurfaceView extends GLSurfaceView {

    private MNRender mMNRender;

    public MNGLSurfaceView(Context context) {
        this(context, null);
    }

    public MNGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        mMNRender = new MNRender(context);
        setRenderer(mMNRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void setYUVData(int width, int height, byte[] y, byte[] u, byte[] v) {
        if (mMNRender != null) {
            mMNRender.setYUVRenderData(width, height, y, u, v);
            requestRender();
        }
    }

}
