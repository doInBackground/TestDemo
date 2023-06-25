package com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test04.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.musicui.utils.DisplayUtil;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test04.listener.IPlayerListener;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test04.listener.OnPreparedListener;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test04.opengl.MNGLSurfaceView;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test04.player.MNPlayer;

import java.io.File;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @Author WCL
 * @Date 2023/6/20 11:26
 * @Version
 * @Description 视频播放器界面.
 */
public class VideoPlayerActivity extends AppCompatActivity {

    private String mVideoFilePath = new File(PathUtils.getExternalAppDataPath(), "video").getAbsolutePath();//沙箱根路径中,即将要播放的视频文件video.
    private MNPlayer mMNPlayer;
    private TextView mTvTime;
    private MNGLSurfaceView mMNGLSurfaceView;
    private SeekBar mSeekBar;
    private int mPosition;
    private boolean mIsSeek = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        mTvTime = findViewById(R.id.tv_time);
        mMNGLSurfaceView = findViewById(R.id.wlglsurfaceview);
        mSeekBar = findViewById(R.id.seekbar);
//        checkPermission();
        mMNPlayer = new MNPlayer();
        mMNPlayer.setMNGLSurfaceView(mMNGLSurfaceView);
        mMNPlayer.setIPlayerListener(new IPlayerListener() {
            @Override
            public void onLoad(boolean load) {

            }

            @Override
            public void onCurrentTime(int currentTime, int totalTime) {
                if (!mIsSeek && totalTime > 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSeekBar.setProgress(currentTime * 100 / totalTime);
                            mTvTime.setText(DisplayUtil.secdsToDateFormat(currentTime) + "/" + DisplayUtil.secdsToDateFormat(totalTime));
                        }
                    });

                }
            }

            @Override
            public void onError(int code, String msg) {

            }

            @Override
            public void onPause(boolean pause) {

            }

            @Override
            public void onDbValue(int db) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public String onNext() {
                return null;
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPosition = progress * mMNPlayer.getDuration() / 100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsSeek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMNPlayer.seek(mPosition);
                mIsSeek = false;
            }
        });


    }

//    public boolean checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
//            }, 1);
//        }
//        return false;
//    }

    //点击"准备并播放"按钮.
    public void begin(View view) {
        if (!FileUtils.isFileExists(mVideoFilePath)) {//文件不存在.
            boolean isSuccess = ResourceUtils.copyFileFromAssets("demo_h264_368_384.mp4", mVideoFilePath);//assets拷贝文件到沙箱.
            if (!isSuccess) {
                ToastUtils.showShort("从assets拷贝文件到沙箱根路径失败!");
                return;
            }
        }
        mMNPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                mMNPlayer.start();
            }
        });
//       mnPlayer.setSource(new File(Environment.getExternalStorageDirectory(),"input.rmvb").getAbsolutePath());
//       mnPlayer.setSource("rtmp://58.200.131.2:1935/livetv/cctv1");
//        mnPlayer.setSource("http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8");
//        mnPlayer.setSource("http://mn.maliuedu.com/music/input.mp4");
//        wlPlayer.setSource("/mnt/shared/Other/testvideo/楚乔传第一集.mp4");
//        mnPlayer.setSource("/mnt/shared/Other/testvideo/屌丝男士.mov");
//        wlPlayer.setSource("http://ngcdn004.cnr.cn/live/dszs/index12.m3u8");
        mMNPlayer.setSource(mVideoFilePath);
        mMNPlayer.parpared();
    }

    //点击"暂停"按钮.
    public void pause(View view) {
        mMNPlayer.pause();
    }

    //点击"继续"按钮.
    public void resume(View view) {
        mMNPlayer.resume();
    }

    //点击"停止"按钮.
    public void stop(View view) {
        ToastUtils.showShort("释放资源较慢,请稍后..");
        mMNPlayer.stop();
    }

//    public void next(View view) {
//        //wlPlayer.playNext("/mnt/shared/Other/testvideo/楚乔传第一集.mp4");
//    }

    //点击"1.5倍速"按钮.
    public void speed1(View view) {
        mMNPlayer.setSpeed(1.5f);
    }

    //点击"2.0倍速"按钮.
    public void speed2(View view) {
        mMNPlayer.setSpeed(2.0f);
    }

}