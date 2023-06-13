package com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.musicservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.listener.IPlayerListener;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.listener.OnPreparedListener;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.player.NativePlayer;

import java.io.File;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * @Author WCL
 * @Date 2023/6/9 10:02
 * @Version
 * @Description 音乐播放服务.
 * Service和Activity可能不在一个进程,故通过广播的方式传输数据.
 */
public class MusicService extends Service {//implements MediaPlayer.OnCompletionListener

    /*操作指令*/
    public static final String ACTION_OPT_MUSIC_PLAY = "ACTION_OPT_MUSIC_PLAY";//请求播放.
    public static final String ACTION_OPT_MUSIC_PAUSE = "ACTION_OPT_MUSIC_PAUSE";//请求暂停.
    public static final String ACTION_OPT_MUSIC_RESUME = "ACTION_OPT_MUSIC_RESUME";//请求继续.
    public static final String ACTION_OPT_MUSIC_NEXT = "ACTION_OPT_MUSIC_NEXT";//请求下一首.
    public static final String ACTION_OPT_MUSIC_LAST = "ACTION_OPT_MUSIC_LAST";//请求上一首.
    public static final String ACTION_OPT_MUSIC_SEEK_TO = "ACTION_OPT_MUSIC_SEEK_TO";//请求seek播放.
    public static final String ACTION_OPT_MUSIC_LEFT = "ACTION_OPT_MUSIC_LEFT";
    public static final String ACTION_OPT_MUSIC_RIGHT = "ACTION_OPT_MUSIC_RIGHT";
    public static final String ACTION_OPT_MUSIC_CENTER = "ACTION_OPT_MUSIC_CENTER";
    public static final String ACTION_OPT_MUSIC_VOLUME = "ACTION_OPT_MUSIC_VOLUME";

    public static final String ACTION_OPT_MUSIC_SPEED_AN_NO_PITCH = "ACTION_OPT_MUSIC_SPEED_AN_NO_PITCH";
    public static final String ACTION_OPT_MUSIC_SPEED_NO_AN_PITCH = "ACTION_OPT_MUSIC_SPEED_NO_AN_PITCH";
    public static final String ACTION_OPT_MUSIC_SPEED_AN_PITCH = "ACTION_OPT_MUSIC_SPEED_AN_PITCH";
    public static final String ACTION_OPT_MUSIC_SPEED_PITCH_NORMAL = "ACTION_OPT_MUSIC_SPEED_PITCH_NORMAL";

    /*状态指令*/
    public static final String ACTION_STATUS_MUSIC_PLAY = "ACTION_STATUS_MUSIC_PLAY";
    public static final String ACTION_STATUS_MUSIC_PAUSE = "ACTION_STATUS_MUSIC_PAUSE";
    public static final String ACTION_STATUS_MUSIC_COMPLETE = "ACTION_STATUS_MUSIC_COMPLETE";
    public static final String ACTION_STATUS_MUSIC_DURATION = "ACTION_STATUS_MUSIC_DURATION";
    public static final String ACTION_STATUS_MUSIC_PLAYER_TIME = "ACTION_STATUS_MUSIC_PLAYER_TIME";
    public static final String PARAM_MUSIC_DURATION = "PARAM_MUSIC_DURATION";
    public static final String PARAM_MUSIC_SEEK_TO = "PARAM_MUSIC_SEEK_TO";
    public static final String PARAM_MUSIC_CURRENT_POSITION = "PARAM_MUSIC_CURRENT_POSITION";
    public static final String PARAM_MUSIC_IS_OVER = "PARAM_MUSIC_IS_OVER";

    private final String mAudioFilePath = new File(PathUtils.getExternalAppDataPath(), "audio_music").getAbsolutePath();//沙箱根路径中,即将要播放的音频文件.
    private final MusicReceiver mMusicReceiver = new MusicReceiver();
    private int mCurrentMusicIndex = 0;
    private NativePlayer mPlayer;
    private final IPlayerListener mPlayerListener = new IPlayerListener() {
        @Override
        public void onLoad(boolean load) {

        }

        @Override
        public void onCurrentTime(int currentTime, int totalTime) {
            Intent intent = new Intent(ACTION_STATUS_MUSIC_PLAYER_TIME);
            intent.putExtra("currentTime", currentTime);
            intent.putExtra("totalTime", totalTime);
            LocalBroadcastManager.getInstance(MusicService.this).sendBroadcast(intent);
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
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initBoardCastReceiver();
        mPlayer = new NativePlayer();
        mPlayer.setPlayerListener(mPlayerListener);
        mPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                LogUtils.i("已传入播放路径且FFmpeg准备好了,即将自动调用start()开始播放.");
                mPlayer.start();
            }
        });
    }

    private void initBoardCastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_OPT_MUSIC_PLAY);
        intentFilter.addAction(ACTION_OPT_MUSIC_PAUSE);
        intentFilter.addAction(ACTION_OPT_MUSIC_RESUME);
        intentFilter.addAction(ACTION_OPT_MUSIC_NEXT);
        intentFilter.addAction(ACTION_OPT_MUSIC_LAST);
        intentFilter.addAction(ACTION_OPT_MUSIC_SEEK_TO);
        intentFilter.addAction(ACTION_OPT_MUSIC_LEFT);
        intentFilter.addAction(ACTION_OPT_MUSIC_RIGHT);
        intentFilter.addAction(ACTION_OPT_MUSIC_VOLUME);
        intentFilter.addAction(ACTION_OPT_MUSIC_CENTER);
        intentFilter.addAction(ACTION_OPT_MUSIC_SPEED_AN_NO_PITCH);
        intentFilter.addAction(ACTION_OPT_MUSIC_SPEED_NO_AN_PITCH);
        intentFilter.addAction(ACTION_OPT_MUSIC_SPEED_AN_PITCH);
        intentFilter.addAction(ACTION_OPT_MUSIC_SPEED_PITCH_NORMAL);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMusicReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMusicReceiver);
    }


//    @Override
//    public void onCompletion(MediaPlayer mp) {
//    }


    //播放音乐.
    private void play(final int index) {
        if (!FileUtils.isFileExists(mAudioFilePath)) {//文件不存在.
            ResourceUtils.copyFileFromRaw(R.raw.music1, mAudioFilePath);//raw拷贝文件到沙箱.
        }
//        mnPlayer.setSource("http://mn.maliuedu.com/music/dengniguilai.mp3");
//        mnPlayer.setSource("http://mpge.5nd.com/2015/2015-11-26/69708/1.mp3");
//        mnPlayer.setSource("rtmp://58.200.131.2:1935/livetv/cctv1");
        mPlayer.setSource(mAudioFilePath);
        mPlayer.prepared();
    }

    /**
     * @Author WCL
     * @Date 2023/6/13 14:13
     * @Version
     * @Description MusicService中的广播接收者.
     */
    private class MusicReceiver extends BroadcastReceiver {

        int i = 0;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.i("MusicService收到指令:" + action);
            if (action.equals(ACTION_OPT_MUSIC_PLAY)) {
                play(mCurrentMusicIndex);
            } else if (action.equals(ACTION_OPT_MUSIC_LAST)) {//上一首.
            } else if (action.equals(ACTION_OPT_MUSIC_NEXT)) {//下一首.
            } else if (action.equals(ACTION_OPT_MUSIC_SEEK_TO)) {
                int position = intent.getIntExtra(MusicService.PARAM_MUSIC_SEEK_TO, 0);
                mPlayer.seek(position);
            } else if (action.equals(ACTION_OPT_MUSIC_RESUME)) {
                mPlayer.resume();
            } else if (action.equals(ACTION_OPT_MUSIC_PAUSE)) {
                mPlayer.pause();
//                mnPlayer.stop();
            } else if (action.equals(ACTION_OPT_MUSIC_RIGHT)) {
                mPlayer.setMute(0);
            } else if (action.equals(ACTION_OPT_MUSIC_LEFT)) {
                mPlayer.setMute(1);
            } else if (action.equals(ACTION_OPT_MUSIC_CENTER)) {
                mPlayer.setMute(2);
            } else if (action.equals(ACTION_OPT_MUSIC_VOLUME)) {
                mPlayer.setVolume(i++);
            } else if (action.equals(ACTION_OPT_MUSIC_SPEED_AN_NO_PITCH)) {
                mPlayer.setSpeed(1.5f);
                mPlayer.setPitch(1.0f);
            } else if (action.equals(ACTION_OPT_MUSIC_SPEED_NO_AN_PITCH)) {
                mPlayer.setPitch(1.5f);
                mPlayer.setSpeed(1.0f);
            } else if (action.equals(ACTION_OPT_MUSIC_SPEED_AN_PITCH)) {
                mPlayer.setSpeed(1.5f);
                mPlayer.setPitch(1.5f);
            } else if (action.equals(ACTION_OPT_MUSIC_SPEED_PITCH_NORMAL)) {
                mPlayer.setSpeed(1.0f);
                mPlayer.setPitch(1.0f);
            }
        }
    }

}
