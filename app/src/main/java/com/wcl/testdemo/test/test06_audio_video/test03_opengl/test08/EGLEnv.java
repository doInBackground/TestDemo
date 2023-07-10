package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.view.Surface;

import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter.ScreenFilter;

/**
 * @Author WCL
 * @Date 2023/5/24 13:58
 * @Version
 * @Description 用来构建OpenGL环境的类.GLThread.
 */
class EGLEnv {

    private EGLDisplay mEglDisplay;
    private EGLContext mEglContext;
    private final EGLConfig mEglConfig;
    private final EGLSurface mEglSurface;
    private ScreenFilter mScreenFilter;

    //Surface: Mediacodec提供的场地.
    public EGLEnv(Context context, EGLContext mGlContext, Surface surface, int width, int height) {
        mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);//获得显示窗口,作为OpenGL的绘制目标.
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        //初始化显示窗口.
        int[] version = new int[2];
        if (!EGL14.eglInitialize(mEglDisplay, version, 0, version, 1)) {
            throw new RuntimeException("eglInitialize failed");
        }

        //配置属性选项.
        int[] configAttribs = {
                EGL14.EGL_RED_SIZE, 8, //颜色缓冲区中-红色位数
                EGL14.EGL_GREEN_SIZE, 8, //颜色缓冲区中-绿色位数
                EGL14.EGL_BLUE_SIZE, 8, //颜色缓冲区中-蓝色位数
                EGL14.EGL_ALPHA_SIZE, 8, //颜色缓冲区中-透明度位数
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, //opengl es 2.0
                EGL14.EGL_NONE //结尾标识.
        };
        int[] numConfigs = new int[1];
        EGLConfig[] configs = new EGLConfig[1];//EGL 根据属性选择一个配置.
        if (!EGL14.eglChooseConfig(mEglDisplay, configAttribs, 0, configs, 0, configs.length, numConfigs, 0)) {
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }
        mEglConfig = configs[0];

        int[] context_attrib_list = {EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE};
        mEglContext = EGL14.eglCreateContext(mEglDisplay, mEglConfig, mGlContext, context_attrib_list, 0);

        if (mEglContext == EGL14.EGL_NO_CONTEXT) {//创建EGLContext上下文失败.
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }

        //创建EGLSurface. 类似于录屏推流mediaProjection.createVirtualDisplay().
        int[] surface_attrib_list = {EGL14.EGL_NONE};
        mEglSurface = EGL14.eglCreateWindowSurface(mEglDisplay, mEglConfig, surface, surface_attrib_list, 0);
        if (mEglSurface == null) {
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }

        if (!EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)) {//绑定当前线程的显示器EGLDisplay,虚拟显示设备.
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }
        mScreenFilter = new ScreenFilter(context);//此滤镜仅为了显示到GLSurfaceView中.
        mScreenFilter.setSize(width, height);
    }

    /**
     * 绘制.
     *
     * @param textureId 纹理ID
     * @param timestamp 时间戳
     */
    public void draw(int textureId, long timestamp) {
        mScreenFilter.onDraw(textureId);//显示到屏幕上的GLSurfaceView.
        //因为Surface(MediaCodec编码器提供的接收未编码数据的场地)已经与EGLSurface绑定,数据推到EGLSurface便会推到MediaCodec.
        EGLExt.eglPresentationTimeANDROID(mEglDisplay, mEglSurface, timestamp);//给帧缓冲时间戳.
        EGL14.eglSwapBuffers(mEglDisplay, mEglSurface);//EGLSurface是双缓冲模式.
    }

    /**
     * 环境释放.
     */
    public void release() {
        EGL14.eglDestroySurface(mEglDisplay, mEglSurface);
        EGL14.eglMakeCurrent(mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
        EGL14.eglDestroyContext(mEglDisplay, mEglContext);
        EGL14.eglReleaseThread();
        EGL14.eglTerminate(mEglDisplay);
        mScreenFilter.release();
    }

}
