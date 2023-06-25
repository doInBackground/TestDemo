package com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test01;

import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @Author WCL
 * @Date 2023/6/6 10:51
 * @Version
 * @Description 用FFmpeg软解播放视频的界面.
 */
public class PlayVideoActivity extends AppCompatActivity {

    private Surface mSurface;
    private String mVideoFilePath = new File(PathUtils.getExternalAppDataPath(), "video").getAbsolutePath();//沙箱根路径中,即将要播放的视频文件video.
    private volatile int mPlayResult = -1;//-1表示可以播放.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        initView();
    }

    //初始化控件.
    private void initView() {
        SurfaceView surfaceView = findViewById(R.id.surface);
        final SurfaceHolder surfaceViewHolder = surfaceView.getHolder();
        surfaceViewHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurface = surfaceViewHolder.getSurface();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        Button btPause = findViewById(R.id.pause);
        btPause.setText("PLAY\n(播放路径:" + mVideoFilePath + ")\n(可手动替换此路径下的文件进行播放)");
    }

    /**
     * 用户点击播放视频按钮.
     *
     * @param view PLAY按钮
     */
    public void play(View view) {
        LogUtils.d("用户点击了播放按钮.");
        if (mPlayResult == -1) {
            mPlayResult = 0;
            if (!FileUtils.isFileExists(mVideoFilePath)) {//文件不存在.
                boolean isSuccess = ResourceUtils.copyFileFromAssets("demo_h264_368_384.mp4", mVideoFilePath);//assets拷贝文件到沙箱.
                if (!isSuccess) {
                    mPlayResult = -1;
                    ToastUtils.showShort("从assets拷贝文件到沙箱根路径失败!");
                    return;
                }
            }
            play();
        } else {
            ToastUtils.showShort("正在播放..");
        }
    }

    //子线程播放.
    private void play() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPlayResult = play(mVideoFilePath, mSurface);
            }
        }).start();
    }

    //调用Native层的播放方法.
    public native int play(String url, Surface surface);

}