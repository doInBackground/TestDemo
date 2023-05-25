package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;

import com.blankj.utilcode.util.LogUtils;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter.BeautyFboFilter;
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
public class CameraRender implements GLSurfaceView.Renderer, Preview.OnPreviewOutputUpdateListener, SurfaceTexture.OnFrameAvailableListener {

    private CameraXGLSurfaceView mCameraXGLSurfaceView;
    private final FilterEnum[] mFilterTypeArr;//想要使用的滤镜集合.
    private CameraXHelper mCameraXHelper;
    private SurfaceTexture mCameraSurfaceTexture;//摄像头纹理.
    //滤镜类:
    private CameraFboFilter mCameraFboFilter;//滤镜类0:相机.
    private RecordFilter mRecordFilter;//滤镜类1:画面展示.
    private SoulFboFilter mSoulFboFilter;//滤镜类2:灵魂出窍.
    private SplitFboFilter mSplitFboFilter;//滤镜类3:分屏.
    private BeautyFboFilter mBeautyFboFilter;//滤镜类4:美颜.

    private MediaRecorder mRecorder;

    private int[] mTextures;//记录纹理图层ID.
    float[] mtx = new float[16];

    //构造方法.
    public CameraRender(CameraXGLSurfaceView cameraXGLSurfaceView, FilterEnum[] filterTypeArr) {
        this.mCameraXGLSurfaceView = cameraXGLSurfaceView;
        this.mFilterTypeArr = filterTypeArr;
        LogUtils.d("滤镜类型数组:", filterTypeArr);
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
        for (FilterEnum type : mFilterTypeArr) {
            switch (type) {
                case CAMERA:
                    mCameraFboFilter = new CameraFboFilter(context);
                    break;
                case SHOW:
                    mRecordFilter = new RecordFilter(context);
                    break;
                case SOUL:
                    mSoulFboFilter = new SoulFboFilter(context);
                    break;
                case SPLIT:
                    mSplitFboFilter = new SplitFboFilter(context);
                    break;
                case BEAUTY:
                    mBeautyFboFilter = new BeautyFboFilter(context);
                    break;
            }
        }
        mRecorder = new MediaRecorder(mCameraXGLSurfaceView.getContext(), null, EGL14.eglGetCurrentContext(), 480, 640);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mCameraFboFilter != null) {
            mCameraFboFilter.setSize(width, height);
        }
        if (mRecordFilter != null) {
            mRecordFilter.setSize(width, height);
        }
        if (mSoulFboFilter != null) {
            mSoulFboFilter.setSize(width, height);
        }
        if (mSplitFboFilter != null) {
            mSplitFboFilter.setSize(width, height);
        }
        if (mBeautyFboFilter != null) {
            mBeautyFboFilter.setSize(width, height);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
//        Log.i(TAG, "线程: " + Thread.currentThread().getName());//GLThread 28354.

        mCameraSurfaceTexture.updateTexImage();//SurfaceTexture更新摄像头数据.数据给了GPU.
        mCameraSurfaceTexture.getTransformMatrix(mtx);


        //有数据的时候给着色器代码中的变量赋值.
        int id = -1;
        if (mCameraFboFilter != null) {
            mCameraFboFilter.setTransformMatrix(mtx);
            id = mCameraFboFilter.onDraw(mTextures[0]);//id表示FBO所在图层纹理.
        }
        if (mSoulFboFilter != null) {
            id = mSoulFboFilter.onDraw(id);//加载新的顶点程序和片元程序显示屏幕,id->fbo->像素详细.
        }
        if (mSplitFboFilter != null) {
            id = mSplitFboFilter.onDraw(id);
        }
        if (mBeautyFboFilter != null) {//不能通过一个Boolean值来判断是否开启美颜从而是否调用美颜滤镜的onDraw(),因为美颜滤镜FBO已经创建如果不释放会造成内存泄漏,故关闭美颜就要释放资源.
            id = mBeautyFboFilter.onDraw(id);
        }
        if (mRecordFilter != null) {
            //FboFilter的onDraw()都绘制到FBO当中去了不会显示,此处调用非FboFilter的onDraw()用来显示.
            id = mRecordFilter.onDraw(id);
        }

        //拿到了fbo的引用->编码视频->输出|直播推理. //起点数据,主动调用,opengl的函数,录制.
        mRecorder.fireFrame(id, mCameraSurfaceTexture.getTimestamp());//此处控制是否录制.
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

    /**
     * 开始录制.
     *
     * @param speed 速度
     */
    public void startRecord(float speed) {
        try {
            mRecorder.start(speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制.
     */
    public void stopRecord() {
        mRecorder.stop();
    }

    /**
     * 开启或关闭美颜.
     *
     * @param isChecked 是否开启
     */
    public void enableBeauty(final boolean isChecked) {
        mCameraXGLSurfaceView.queueEvent(new Runnable() {//OpenGL线程来做FBO.

            @Override
            public void run() {
                if (isChecked) {//用户手动开启了美颜:
                    mBeautyFboFilter = new BeautyFboFilter(mCameraXGLSurfaceView.getContext());
                    mBeautyFboFilter.setSize(mCameraXGLSurfaceView.getWidth(), mCameraXGLSurfaceView.getHeight());
                } else {
                    if (mBeautyFboFilter != null) {
                        mBeautyFboFilter.release();
                        mBeautyFboFilter = null;
                    }
                }
            }
        });
    }

    /**
     * @Author WCL
     * @Date 2023/5/25 16:09
     * @Version
     * @Description 枚举:滤镜.
     */
    public enum FilterEnum {
        /**
         * Comment:相机滤镜
         */
        CAMERA,
        /**
         * Comment:画面展示滤镜
         */
        SHOW,
        /**
         * Comment:灵魂出窍滤镜
         */
        SOUL,
        /**
         * Comment:分屏滤镜
         */
        SPLIT,
        /**
         * Comment:美颜滤镜
         */
        BEAUTY
    }

}
