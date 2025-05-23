package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.wcl.testdemo.R;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.CameraXGLSurfaceView;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.widget.RecordButton;

import com.wcl.testdemo.init.BaseActivity;

/**
 * @Author WCL
 * @Date 2023/5/24 11:13
 * @Version
 * @Description 展示视频OpenGL特效及录制界面.
 */
// TODO: 2023/7/7 OpenGLES使用案例.
public class OpenGLRecordActivity extends BaseActivity implements RecordButton.OnRecordListener, RadioGroup.OnCheckedChangeListener {

    private CameraXGLSurfaceView mCameraXGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gl_record);

        //相机画面展示控件.
        mCameraXGLSurfaceView = findViewById(R.id.camerax_gl_surface_view);

        //录制控件.
        RecordButton btn_record = findViewById(R.id.btn_record);
        btn_record.setOnRecordListener(this);

        //速度控件.
        RadioGroup rgSpeed = findViewById(R.id.rg_speed);
        rgSpeed.setOnCheckedChangeListener(this);

        //美颜.
        ((CheckBox) findViewById(R.id.beauty)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCameraXGLSurfaceView.enableBeauty(isChecked);
            }
        });

        checkPermission();
    }

    /**
     * 当用户开始录制时.
     * RecordButton.OnRecordListener接口回调.
     */
    @Override
    public void onRecordStart() {
        mCameraXGLSurfaceView.startRecord();
    }

    /**
     * 当用户停止录制时.
     * RecordButton.OnRecordListener接口回调.
     */
    @Override
    public void onRecordStop() {
        mCameraXGLSurfaceView.stopRecord();
    }

    /**
     * 当用户选择的速度发生改变时.
     * RadioGroup.OnCheckedChangeListener接口回调.
     *
     * @param group     组控件
     * @param checkedId 点击控件的ID
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.btn_extra_slow:
                mCameraXGLSurfaceView.setSpeed(CameraXGLSurfaceView.Speed.MODE_EXTRA_SLOW);
                break;
            case R.id.btn_slow:
                mCameraXGLSurfaceView.setSpeed(CameraXGLSurfaceView.Speed.MODE_SLOW);
                break;
            case R.id.btn_normal:
                mCameraXGLSurfaceView.setSpeed(CameraXGLSurfaceView.Speed.MODE_NORMAL);
                break;
            case R.id.btn_fast:
                mCameraXGLSurfaceView.setSpeed(CameraXGLSurfaceView.Speed.MODE_FAST);
                break;
            case R.id.btn_extra_fast:
                mCameraXGLSurfaceView.setSpeed(CameraXGLSurfaceView.Speed.MODE_EXTRA_FAST);
                break;
        }
    }

    //申请权限:读写权限及相机权限.
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            }, 1);
        }
    }

}