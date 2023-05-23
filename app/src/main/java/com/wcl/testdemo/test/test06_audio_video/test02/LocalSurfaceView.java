package com.wcl.testdemo.test.test06_audio_video.test02;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import androidx.annotation.NonNull;

/**
 * @Author WCL
 * @Date 2023/5/16 14:05
 * @Version
 * @Description 展示本地视频画面的SurfaceView.
 */
class LocalSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private Camera mCamera;
    private Camera.Size mSize;
    private EncodePushLiveH265 mEncodePushLiveH265;//编码器
    private byte[] mBufferArr;

    public LocalSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        startPreview();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.release();
        }
        if (mEncodePushLiveH265 != null) {
            mEncodePushLiveH265.close();
        }
    }

    //获取到摄像头的原始数据yuv.
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (mEncodePushLiveH265 != null) {
            mEncodePushLiveH265.encode(bytes);//开始编码摄像头数据.
        }
        mCamera.addCallbackBuffer(bytes);
    }

    /**
     * 初始化编码器,以便获取到摄像头数据时可以编码推送.
     *
     * @param serverIP       服务端IP(如果本机作为服务端,则此处应当传空)
     * @param socketCallback Socket回调,用来接收另一端传来的数据
     */
    public void startPush(String serverIP, ISocketLive.SocketCallback socketCallback) {
        mEncodePushLiveH265 = new EncodePushLiveH265(serverIP, socketCallback, mSize.width, mSize.height);
        mEncodePushLiveH265.startLive();
    }

    //开始展示本地摄像头画面.
    private void startPreview() {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        Camera.Parameters parameters = mCamera.getParameters();
        mSize = parameters.getPreviewSize();//尺寸
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//参数设置-自动对焦.
        }
        mCamera.setParameters(parameters);//参数应用.
        try {
            mCamera.setPreviewDisplay(getHolder());//Camera与SurfaceView关联起来!!!
            mCamera.setDisplayOrientation(90);//横着
            mBufferArr = new byte[mSize.width * mSize.height * 3 / 2];
            mCamera.addCallbackBuffer(mBufferArr);
            mCamera.setPreviewCallbackWithBuffer(this);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
