package com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.player;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.listener.IPlayerListener;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.listener.OnPreparedListener;

/**
 * @Author WCL
 * @Date 2023/6/13 14:01
 * @Version
 * @Description 是Native和Java沟通的桥梁.
 */
public class NativePlayer {

//    static {
//        System.loadLibrary("native-lib");
//    }

    private String mSource;//数据源
    private OnPreparedListener mOnPreparedListener;
    private IPlayerListener mPlayerListener;

    public NativePlayer() {
    }

    /**
     * 设置FFmpeg监听.
     *
     * @param playerListener 监听器
     */
    public void setPlayerListener(IPlayerListener playerListener) {
        this.mPlayerListener = playerListener;
    }

    /**
     * 设置准备接口回调
     *
     * @param onPreparedListener 监听器
     */
    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.mOnPreparedListener = onPreparedListener;
    }

    /**
     * 设置数据源
     *
     * @param source 声音路径
     */
    public void setSource(String source) {
        this.mSource = source;
    }

    public void prepared() {
        if (TextUtils.isEmpty(mSource)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_prepared(mSource);
            }
        }).start();
    }

    public void start() {
        if (TextUtils.isEmpty(mSource)) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_start();
            }
        }).start();
    }

    public void pause() {
        n_pause();
    }

    public void resume() {
        n_resume();
    }

    public void stop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_stop();
            }
        }).start();
    }

    public void setSpeed(float speed) {
        n_speed(speed);
    }

    public void setVolume(int percent) {
        if (percent >= 0 && percent <= 100) {
            n_volume(percent);
        }
    }


    public void setPitch(float pitch) {
        n_pitch(pitch);
    }


    public void setMute(int mute) {
        n_mute(mute);
    }

    public void seek(int second) {
        n_seek(second);
    }

    /**
     * (c++回调java的方法)
     * 当FFmpeg初始化完成.
     */
    public void onCallPrepared() {
        LogUtils.d("Native回调Java: FFmpeg初始化完成.");
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared();
        }
    }

    /**
     * (c++回调java的方法)
     * 当前播放时间和总时间.
     *
     * @param currentTime
     * @param totalTime
     */
    public void onCallTimeInfo(int currentTime, int totalTime) {
//        LogUtils.d("Native回调Java:", "当前播放时间:" + currentTime + " 总时间:" + totalTime);
        if (mPlayerListener == null) {
            return;
        }
        mPlayerListener.onCurrentTime(currentTime, totalTime);
    }


    private native void n_prepared(String source);

    private native void n_start();

    private native void n_seek(int second);

    private native void n_resume();

    private native void n_pause();

    private native void n_mute(int mute);

    private native void n_volume(int percent);

    private native void n_speed(float speed);

    private native void n_pitch(float pitch);

    private native void n_stop();

}
