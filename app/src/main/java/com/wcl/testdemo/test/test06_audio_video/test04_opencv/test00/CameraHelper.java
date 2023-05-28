package com.wcl.testdemo.test.test06_audio_video.test04_opencv.test00;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

/**
 * @Author WCL
 * @Date 2023/5/26 16:40
 * @Version
 * @Description 摄像头工具类.
 */
class CameraHelper implements Camera.PreviewCallback {

    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    private int mCameraId;
    private Camera mCamera;
    private byte[] buffer;
    private Camera.PreviewCallback mPreviewCallback;
    private Camera.Size size;

    public CameraHelper(int cameraId) {
        mCameraId = cameraId;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {//data数据是倒的.
        mPreviewCallback.onPreviewFrame(data, camera);
        camera.addCallbackBuffer(buffer);
    }

    /**
     * 开始展示.
     */
    public void startPreview() {
        try {
            //获得camera对象
            mCamera = Camera.open(mCameraId);
            //配置camera的属性
            Camera.Parameters parameters = mCamera.getParameters();
            //设置预览数据格式为nv21
            parameters.setPreviewFormat(ImageFormat.NV21);
            //这是摄像头宽、高
            parameters.setPreviewSize(WIDTH, HEIGHT);
            // 设置摄像头 图像传感器的角度、方向
            mCamera.setParameters(parameters);
            size = parameters.getPreviewSize();
            LogUtils.d("startPreview->  width:" + size.width + " height:" + size.height);
            buffer = new byte[WIDTH * HEIGHT * 3 / 2];
            //数据缓存区
            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallbackWithBuffer(this);
            //设置预览画面
            SurfaceTexture surfaceTexture = new SurfaceTexture(11);
            mCamera.setPreviewTexture(surfaceTexture);
            mCamera.startPreview();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 停止展示.
     */
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);//预览数据回调接口
            mCamera.stopPreview();//停止预览
            mCamera.release();//释放摄像头
            mCamera = null;
        }
    }

    /**
     * 切换摄像头.
     */
    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        stopPreview();
        startPreview();
    }

    /**
     * 获取摄像头ID.
     *
     * @return 摄像头ID
     */
    public int getCameraId() {
        return mCameraId;
    }

    /**
     * 设置展示回调.
     *
     * @param previewCallback 展示回调
     */
    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        mPreviewCallback = previewCallback;
    }

}
