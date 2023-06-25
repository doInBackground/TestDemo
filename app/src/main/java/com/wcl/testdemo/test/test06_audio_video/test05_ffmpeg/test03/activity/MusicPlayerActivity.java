package com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.musicservice.MusicService;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.musicui.model.MusicData;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.musicui.utils.DisplayUtil;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.musicui.widget.BackgroundAnimationRelativeLayout;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.musicui.widget.DiscView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.musicui.widget.DiscView.DURATION_NEEDLE_ANIMATOR;

/**
 * @Author WCL
 * @Date 2023/6/13 13:36
 * @Version
 * @Description 音乐播放器界面.
 */
public class MusicPlayerActivity extends AppCompatActivity implements DiscView.IDiscChangeListener, View.OnClickListener {

    public static final int MUSIC_MESSAGE = 0;
    public static final String PARAM_MUSIC_LIST = "PARAM_MUSIC_LIST";

    private DiscView mDisc;
    private Toolbar mToolbar;
    private SeekBar mSeekBar;
    private ImageView mIvPlayOrPause, mIvNext, mIvLast;
    private TextView mTvMusicDuration, mTvTotalMusicDuration;
    private BackgroundAnimationRelativeLayout mRootLayout;

    private DisplayUtil mDisplayUtil = new DisplayUtil();
    private MusicReceiver mMusicReceiver = new MusicReceiver();
    private List<MusicData> mMusicDataList = new ArrayList<>();
    private int totalTime;
    private int position;
//    private boolean playState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        initMusicDataList();
        initView();
        initMusicReceiver();
        DisplayUtil.makeStatusBarTransparent(this);
//        checkPermission();
//        new Thread() {
//            @Override
//            public void run() {
//                while (true) {
//                    optMusic(ACTION_OPT_MUSIC_VOLUME);
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }.start();
    }

    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        return false;
    }

    private void initMusicReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PLAY);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PAUSE);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_DURATION);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_COMPLETE);
        intentFilter.addAction(MusicService.ACTION_STATUS_MUSIC_PLAYER_TIME);
        /*注册本地广播*/
        LocalBroadcastManager.getInstance(this).registerReceiver(mMusicReceiver, intentFilter);
    }

    private void initView() {
        mDisc = (DiscView) findViewById(R.id.discview);
        mIvNext = (ImageView) findViewById(R.id.ivNext);
        mIvLast = (ImageView) findViewById(R.id.ivLast);
        mIvPlayOrPause = (ImageView) findViewById(R.id.ivPlayOrPause);
        mTvMusicDuration = (TextView) findViewById(R.id.tvCurrentTime);
        mTvTotalMusicDuration = (TextView) findViewById(R.id.tvTotalTime);
        mSeekBar = (SeekBar) findViewById(R.id.musicSeekBar);
        mRootLayout = (BackgroundAnimationRelativeLayout) findViewById(R.id.rootLayout);
        mToolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(mToolbar);
        mDisc.setPlayInfoListener(this);
        mIvLast.setOnClickListener(this);
        mIvNext.setOnClickListener(this);
        mIvPlayOrPause.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                position = totalTime * progress / 100;
                mTvMusicDuration.setText(mDisplayUtil.duration2Time(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                LogUtils.d("SeekBar:" + position);
                seekTo(position);
            }
        });
        mTvMusicDuration.setText(mDisplayUtil.duration2Time(0));
        mTvTotalMusicDuration.setText(mDisplayUtil.duration2Time(0));
        mDisc.setMusicDataList(mMusicDataList);
    }

    private void playCurrentTime(int currentTime, int totalTime) {
        mSeekBar.setProgress(currentTime * 100 / totalTime);
        this.totalTime = totalTime;
        mTvMusicDuration.setText(DisplayUtil.secdsToDateFormat(currentTime, totalTime));
        mTvTotalMusicDuration.setText(DisplayUtil.secdsToDateFormat(totalTime, totalTime));
    }

    private void initMusicDataList() {
        MusicData musicData1 = new MusicData(R.raw.music1, R.raw.music_ic1, "等你归来", "程响");
        MusicData musicData2 = new MusicData(R.raw.music1, R.raw.music_ic2, "Nightingale", "YANI");
        MusicData musicData3 = new MusicData(R.raw.music1, R.raw.music_ic3, "Cornfield Chase", "Hans Zimmer");
        mMusicDataList.add(musicData1);
        mMusicDataList.add(musicData2);
        mMusicDataList.add(musicData3);
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra(PARAM_MUSIC_LIST, (Serializable) mMusicDataList);
        startService(intent);
    }

    @Override
    public void onMusicTitleChanged(String musicName, String musicAuthor) {
//        LogUtils.d("音乐标题发生改变:" + musicName + "  " + musicAuthor);
        getSupportActionBar().setTitle(musicName);
        getSupportActionBar().setSubtitle(musicAuthor);
    }

    @Override
    public void onMusicPicChanged(int musicPicRes) {
//        LogUtils.d("音乐图片发生改变:" + musicPicRes);
        mDisplayUtil.try2UpdateMusicPicBackground(this, mRootLayout, musicPicRes);
    }

    @Override
    public void onMusicStatusChanged(DiscView.MusicChangedStatus musicChangedStatus) {
        LogUtils.d("音乐状态发生改变:" + musicChangedStatus);
        switch (musicChangedStatus) {
            case PLAY: {
                play();
                break;
            }
            case PAUSE: {
                pause();
                break;
            }
            case NEXT: {//下一首.
                next();
                break;
            }
            case LAST: {//上一首.
                last();
                break;
            }
            case STOP: {//停止.
                stop();
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mIvPlayOrPause) {
//            playState = !playState;//取反.
//            Log.i(TAG, "onClick: ---------" + playState);
//            if (playState) {
//                mIvPlayOrPause.setImageResource(R.drawable.ic_play);
////                pause();
//                mDisc.stop();
//            } else {
//                mIvPlayOrPause.setImageResource(R.drawable.ic_pause);
////                resume();
//                mDisc.play();
//            }
            DiscView.MusicStatus musicStatus = mDisc.getMusicStatus();
            LogUtils.d("当前播放按钮播放状态:" + musicStatus);
            if (musicStatus == DiscView.MusicStatus.PLAY) {//当前正在播放.
                //暂停.
                mDisc.pause();
                pause();
                mIvPlayOrPause.setImageResource(R.drawable.ic_play);//暂停后展示播放键.
            } else {//当前没有播放.
                //播放.
                mDisc.play();
                if (musicStatus == DiscView.MusicStatus.STOP) {
                    play();
                } else if (musicStatus == DiscView.MusicStatus.PAUSE) {
                    resume();
                }
                mIvPlayOrPause.setImageResource(R.drawable.ic_pause);//播放后展示暂停键.
            }
        } else if (v == mIvNext) {
            mDisc.next();
            if (mDisc.getMusicStatus() == DiscView.MusicStatus.PLAY) {
                mIvPlayOrPause.setImageResource(R.drawable.ic_pause);//展示暂停键.
            } else {
                mIvPlayOrPause.setImageResource(R.drawable.ic_play);//展示播放键.
            }
        } else if (v == mIvLast) {
            mDisc.last();
            if (mDisc.getMusicStatus() == DiscView.MusicStatus.PLAY) {
                mIvPlayOrPause.setImageResource(R.drawable.ic_pause);//展示暂停键.
            } else {
                mIvPlayOrPause.setImageResource(R.drawable.ic_play);//展示播放键.
            }
        }

    }

    //请求Service:播放.
    private void play() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PLAY);
    }

    //请求Service:暂停.
    private void pause() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PAUSE);
    }

    //请求Service:继续.
    private void resume() {
        optMusic(MusicService.ACTION_OPT_MUSIC_RESUME);
    }

    //界面控件停止.
    private void stop() {
        mIvPlayOrPause.setImageResource(R.drawable.ic_play);
        mTvMusicDuration.setText(mDisplayUtil.duration2Time(0));
        mTvTotalMusicDuration.setText(mDisplayUtil.duration2Time(0));
        mSeekBar.setProgress(0);
    }

    //请求Service:下一首.
    private void next() {
        mRootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                optMusic(MusicService.ACTION_OPT_MUSIC_NEXT);
            }
        }, DURATION_NEEDLE_ANIMATOR);
        mTvMusicDuration.setText(mDisplayUtil.duration2Time(0));
        mTvTotalMusicDuration.setText(mDisplayUtil.duration2Time(0));
    }

    //请求Service:上一首.
    private void last() {
        mRootLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                optMusic(MusicService.ACTION_OPT_MUSIC_LAST);
            }
        }, DURATION_NEEDLE_ANIMATOR);
        mTvMusicDuration.setText(mDisplayUtil.duration2Time(0));
        mTvTotalMusicDuration.setText(mDisplayUtil.duration2Time(0));
    }

    //请求Service:seek到指定位置.
    private void seekTo(int position) {
        Intent intent = new Intent(MusicService.ACTION_OPT_MUSIC_SEEK_TO);
        intent.putExtra(MusicService.PARAM_MUSIC_SEEK_TO, position);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //向Service发送指令.
    private void optMusic(final String action) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(action));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMusicReceiver);
    }

    //点击事件:左声道.
    public void left(View view) {
        optMusic(MusicService.ACTION_OPT_MUSIC_LEFT);
    }

    //点击事件:右声道.
    public void right(View view) {
        optMusic(MusicService.ACTION_OPT_MUSIC_RIGHT);
    }

    //点击事件:立体声.
    public void center(View view) {
        optMusic(MusicService.ACTION_OPT_MUSIC_CENTER);
    }

    //点击事件:变速不变调.
    public void speed(View view) {
        optMusic(MusicService.ACTION_OPT_MUSIC_SPEED_AN_NO_PITCH);
    }

    //点击事件:变调不变速.
    public void pitch(View view) {
        optMusic(MusicService.ACTION_OPT_MUSIC_SPEED_NO_AN_PITCH);
    }

    //点击事件:变速又变调.
    public void speedpitch(View view) {
        optMusic(MusicService.ACTION_OPT_MUSIC_SPEED_AN_PITCH);
    }

    //点击事件:正常播放.
    public void normalspeedpitch(View view) {
        optMusic(MusicService.ACTION_OPT_MUSIC_SPEED_PITCH_NORMAL);
    }

    private void complete(boolean isOver) {
        if (isOver) {
            mDisc.stop();
        } else {
            mDisc.next();
        }
    }

    /**
     * @Author WCL
     * @Date 2023/6/13 10:22
     * @Version
     * @Description 音乐状态广播接收者.
     */
    class MusicReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MusicService.ACTION_STATUS_MUSIC_PLAY)) {
                mIvPlayOrPause.setImageResource(R.drawable.ic_pause);
                int currentPosition = intent.getIntExtra(MusicService.PARAM_MUSIC_CURRENT_POSITION, 0);
                mSeekBar.setProgress(currentPosition);
                if (mDisc.getMusicStatus() != DiscView.MusicStatus.PLAY) {//!mDisc.isPlaying()
                    mDisc.playOrPause();
                }
            } else if (action.equals(MusicService.ACTION_STATUS_MUSIC_PAUSE)) {
                mIvPlayOrPause.setImageResource(R.drawable.ic_play);
                if (mDisc.getMusicStatus() == DiscView.MusicStatus.PLAY) {//mDisc.isPlaying()
                    mDisc.playOrPause();
                }
            } else if (action.equals(MusicService.ACTION_STATUS_MUSIC_DURATION)) {
                int duration = intent.getIntExtra(MusicService.PARAM_MUSIC_DURATION, 0);
//                updateMusicDurationInfo(duration);
            } else if (action.equals(MusicService.ACTION_STATUS_MUSIC_COMPLETE)) {
                boolean isOver = intent.getBooleanExtra(MusicService.PARAM_MUSIC_IS_OVER, true);
                complete(isOver);
            } else if (action.equals(MusicService.ACTION_STATUS_MUSIC_PLAYER_TIME)) {
                int currentTime = intent.getIntExtra("currentTime", 0);
                int totalTime = intent.getIntExtra("totalTime", 0);
                playCurrentTime(currentTime, totalTime);
            }
        }
    }

}
