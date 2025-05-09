package com.wcl.testdemo.test.test06_audio_video.test01;

import com.wcl.testdemo.init.BaseActivity;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;

/**
 * @Author WCL
 * @Date 2023/5/15 15:53
 * @Version
 * @Description
 */
public class CameraActivity extends BaseActivity {

    private CameraSurfaceView mCameraSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mCameraSurfaceView = findViewById(R.id.surface);
    }

    /**
     * 点击"录制".
     *
     * @param view
     */
    public void record(View view) {
        mCameraSurfaceView.setType(1);
    }

    /**
     * 点击"停止".
     *
     * @param view
     */
    public void stopRecord(View view) {
        mCameraSurfaceView.setType(0);
        ToastUtils.showLong("视频路径:\n" + CameraSurfaceView.CAMERA_DATA_H264);
    }

    /**
     * 点击"拍照".
     *
     * @param view
     */
    public void capture(View view) {
        mCameraSurfaceView.setType(2);
        ToastUtils.showLong("照片路径:\n" + PathUtils.getExternalAppCachePath());
    }

}