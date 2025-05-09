package com.wcl.testdemo.test.test06_audio_video.test04_opencv.test00;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.wcl.testdemo.R;

import com.wcl.testdemo.init.BaseActivity;

/**
 * @Author WCL
 * @Date 2023/5/26 16:04
 * @Version
 * @Description 人脸识别界面.
 */
public class FaceRecognitionActivity extends BaseActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private final String FACE_FILE_NAME = "lbpcascade_frontalface.xml";
    private NativeUtils mNativeUtils;
    private CameraHelper mCameraHelper;
    int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);

//        checkPermission();
        mNativeUtils = new NativeUtils();

        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);

        mCameraHelper = new CameraHelper(mCameraId);
        mCameraHelper.setPreviewCallback(this);

        ResourceUtils.copyFileFromAssets(FACE_FILE_NAME, PathUtils.getExternalAppDataPath() + "/" + FACE_FILE_NAME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNativeUtils.init(PathUtils.getExternalAppDataPath() + "/" + FACE_FILE_NAME);//初始化模型.
        mCameraHelper.startPreview();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCameraHelper.stopPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        surface ---> native 层进行渲染
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mNativeUtils.setSurface(holder.getSurface());
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mNativeUtils.postData(data, CameraHelper.WIDTH, CameraHelper.HEIGHT, mCameraId);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {//点击屏幕切换摄像头.
            mCameraHelper.switchCamera();
            mCameraId = mCameraHelper.getCameraId();
        }
        return super.onTouchEvent(event);
    }

//    //权限申请.
//    private boolean checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
//        }
//        return false;
//    }

}
