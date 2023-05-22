package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test02;

import android.os.Bundle;

import com.wcl.testdemo.R;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @Author WCL
 * @Date 2023/5/18 16:41
 * @Version
 * @Description 展示处理后的摄像头数据的GLSurfaceView界面.
 */
public class CameraXActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax);
//        checkPermission();
    }

//    public boolean checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.CAMERA
//            }, 1);
//        }
//        return false;
//    }

}