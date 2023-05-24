package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;

import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter.CameraFboFilter;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter.RecordFilter;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter.SoulFboFilter;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter.SplitFboFilter;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;

/**
 * @Author WCL
 * @Date 2023/5/24 13:35
 * @Version
 * @Description 摄像头渲染器.
 */
class CameraRender implements GLSurfaceView.Renderer, Preview.OnPreviewOutputUpdateListener, SurfaceTexture.OnFrameAvailableListener {

    private CameraXHelper mCameraXHelper;
    private CameraXGLSurfaceView mCameraXGLSurfaceView;
    private SurfaceTexture mCameraSurfaceTexture;//摄像头纹理.
    private CameraFboFilter mCameraFboFilter;//滤镜类:相机.
    private RecordFilter mRecordFilter;//滤镜类:录制.
    private SoulFboFilter mSoulFboFilter;//滤镜类:灵魂出窍.
    private SplitFboFilter mSplitFboFilter;//滤镜类:分屏.

    private MediaRecorder mRecorder;

    private int[] mTextures;//记录纹理图层ID.
    float[] mtx = new float[16];

    //构造方法.
    public CameraRender(CameraXGLSurfaceView cameraXGLSurfaceView) {
        this.mCameraXGLSurfaceView = cameraXGLSurfaceView;
        LifecycleOwner lifecycleOwner = (LifecycleOwner) cameraXGLSurfaceView.getContext();
        mCameraXHelper = new CameraXHelper(lifecycleOwner, this);//打开摄像头,设置回调(Preview.OnPreviewOutputUpdateListener).
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //提供数据的一方(此处为mCameraSurfaceTexture)接收输入器,向其中填充数据.
        mTextures = new int[1];
        mCameraSurfaceTexture.attachToGLContext(mTextures[0]);//绑定:让SurfaceTexture与GPU图层共享一个数据源.参数(图层ID)取值范围:0-31.
        mCameraSurfaceTexture.setOnFrameAvailableListener(this);//监听摄像头数据回调(SurfaceTexture.OnFrameAvailableListener).
        //滤镜.
        Context context = mCameraXGLSurfaceView.getContext();
        mCameraFboFilter = new CameraFboFilter(context);
        mRecordFilter = new RecordFilter(context);
        mSoulFboFilter = new SoulFboFilter(context);
        mSplitFboFilter = new SplitFboFilter(context);

        mRecorder = new MediaRecorder(mCameraXGLSurfaceView.getContext(), null, EGL14.eglGetCurrentContext(), 480, 640);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mRecordFilter.setSize(width, height);
        mCameraFboFilter.setSize(width, height);
        mSoulFboFilter.setSize(width, height);
        mSplitFboFilter.setSize(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
//        Log.i(TAG, "线程: " + Thread.currentThread().getName());//GLThread 28354.

        mCameraSurfaceTexture.updateTexImage();//SurfaceTexture更新摄像头数据.数据给了GPU.
        mCameraSurfaceTexture.getTransformMatrix(mtx);
        mCameraFboFilter.setTransformMatrix(mtx);

        //有数据的时候给着色器代码中的变量赋值.

        int id = mCameraFboFilter.onDraw(mTextures[0]);//id表示FBO所在图层纹理.
        id = mSoulFboFilter.onDraw(id);//加载新的顶点程序和片元程序显示屏幕,id->fbo->像素详细.
        id = mSplitFboFilter.onDraw(id);
        id = mRecordFilter.onDraw(id);

        //拿到了fbo的引用->编码视频->输出|直播推理.
        //起点数据,主动调用,opengl的函数,录制.
        mRecorder.fireFrame(id, mCameraSurfaceTexture.getTimestamp());
    }

    /**
     * Preview.OnPreviewOutputUpdateListener接口的回调方法.
     * 摄像头预览到数据后会回调该方法.
     *
     * @param output 摄像头预览数据
     */
    @Override
    public void onUpdated(Preview.PreviewOutput output) {
        mCameraSurfaceTexture = output.getSurfaceTexture();//摄像头预览到的数据在这里.
    }

    /**
     * SurfaceTexture.OnFrameAvailableListener接口的回调方法.
     * 监听摄像头数据回调.当有数据过来的时候.
     *
     * @param surfaceTexture SurfaceTexture
     */
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mCameraXGLSurfaceView.requestRender();//摄像头一帧一帧回调时,手动刷新渲染.
    }

    public void startRecord(float speed) {
        try {
            mRecorder.start(speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        mRecorder.stop();
    }

}
