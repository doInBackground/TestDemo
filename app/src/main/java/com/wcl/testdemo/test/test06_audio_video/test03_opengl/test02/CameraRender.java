package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test02;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;

/**
 * @Author WCL
 * @Date 2023/5/18 9:49
 * @Version
 * @Description 摄像头渲染器.
 */
class CameraRender implements GLSurfaceView.Renderer, Preview.OnPreviewOutputUpdateListener, SurfaceTexture.OnFrameAvailableListener {

    private CameraXHelper mCameraXHelper;
    private CameraGLSurfaceView mCameraGLSurfaceView;
    private SurfaceTexture mCameraSurfaceTexture;//摄像头纹理.
    private CameraFilter mCameraFilter;//滤镜类.
    private int[] mTextures;//记录纹理图层ID.
    float[] mtx = new float[16];

    public CameraRender(CameraGLSurfaceView cameraGLSurfaceView) {
        this.mCameraGLSurfaceView = cameraGLSurfaceView;
        LifecycleOwner lifecycleOwner = (LifecycleOwner) cameraGLSurfaceView.getContext();
        mCameraXHelper = new CameraXHelper(lifecycleOwner, this);//打开摄像头,设置回调(Preview.OnPreviewOutputUpdateListener).
    }

    //textures
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //提供数据的一方(此处为mCameraSurfaceTexture)接收输入器,向其中填充数据.
        mTextures = new int[1];
        mCameraSurfaceTexture.attachToGLContext(mTextures[0]);//绑定:让SurfaceTexture与GPU图层共享一个数据源.参数(图层ID)取值范围:0-31.
        mCameraSurfaceTexture.setOnFrameAvailableListener(this);//监听摄像头数据回调(SurfaceTexture.OnFrameAvailableListener).
        mCameraFilter = new CameraFilter(mCameraGLSurfaceView.getContext());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mCameraFilter.setSize(width, height);
    }


    @Override
    public void onDrawFrame(GL10 gl) {
//        Log.i(TAG, "线程: " + Thread.currentThread().getName());//GLThread 28354.

        mCameraSurfaceTexture.updateTexImage();//SurfaceTexture更新摄像头数据.数据给了GPU.
        mCameraSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter.setTransformMatrix(mtx);

        //有数据的时候给着色器代码中的变量赋值.
        mCameraFilter.onDraw(mTextures[0]);
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
        mCameraGLSurfaceView.requestRender();//摄像头一帧一帧回调时,手动刷新渲染.
    }

}
