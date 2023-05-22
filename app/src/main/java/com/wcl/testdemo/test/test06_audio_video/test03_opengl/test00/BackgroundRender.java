package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test00;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.blankj.utilcode.util.LogUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Author WCL
 * @Date 2023/5/17 10:37
 * @Version
 * @Description 设置背景色的渲染器.
 * 三个重写方法都是在GLThread线程被调用的.
 */
public class BackgroundRender implements GLSurfaceView.Renderer {

    /**
     * GLSurfaceView创建后,系统调用这个方法一次。
     * 使用此方法来执行只需要发生一次的操作，比如设置OpenGL的环境参数或初始化的OpenGL图形对象。
     *
     * @param gl10
     * @param eglConfig
     */
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background frame color.
        // 设置清空屏幕用的颜色.
        // 参数:(红,绿,蓝,透明度),取值范围[0, 1].
        GLES20.glClearColor(1.0f, 0f, 0f, 1.0f);//将背景设置为红色.
    }

    /**
     * 系统会在 GLSurfaceView 几何图形发生变化（包括 GLSurfaceView 大小发生变化或设备屏幕方向发生变化）时调用此方法。例如，系统会在设备屏幕方向由纵向变为横向时调用此方法。
     * 渲染窗口大小发生改变或者屏幕方法发生变化时回调.
     *
     * @param gl10
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    /**
     * 系统会在每次重新绘制 GLSurfaceView 时调用此方法。是将图像绘制到GLSurfaceView的主要方法。
     * 调用次数取决于设置的渲染模式,RENDERMODE_WHEN_DIRTY(手动)和RENDERMODE_CONTINUOUSLY(自动).
     *
     * @param gl10
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        LogUtils.d("OpenGL绘制");
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);//在onDrawFrame()中调用glClear(GL_COLOR_BUFFER_BIT)清空屏幕，会调用glClearColor中定义的颜色来填充整个屏幕。
        //执行渲染工作.
        //Redraw background color.
//        GLES20.glClearColor(0f, 1.0f, 0f, 1.0f);
//        GLES20.glClearColor(0f, 0f, 1.0f, 0f);
    }

}
