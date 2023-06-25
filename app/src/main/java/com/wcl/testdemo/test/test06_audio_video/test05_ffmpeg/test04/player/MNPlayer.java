package com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test04.player;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test04.listener.IPlayerListener;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test04.listener.OnPreparedListener;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test04.opengl.MNGLSurfaceView;

/**
 * @Author WCL
 * @Date 2023/6/21 18:28
 * @Version
 * @Description 播放器控制类.
 */
public class MNPlayer {

    private String mSource;//数据源
    private OnPreparedListener mOnPreparedListener;
    private IPlayerListener mIPlayerListener;
    private MNGLSurfaceView mGLSurfaceView;
    private int mDuration = 0;//总时长.

    public MNPlayer() {
    }

    public void setMNGLSurfaceView(MNGLSurfaceView davidView) {
        this.mGLSurfaceView = davidView;
    }

    public void setIPlayerListener(IPlayerListener IPlayerListener) {
        this.mIPlayerListener = IPlayerListener;
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.mOnPreparedListener = onPreparedListener;
    }

    /**
     * 设置数据源
     *
     * @param source
     */
    public void setSource(String source) {
        this.mSource = source;
    }


    /**
     * 操作:检查数据源,初始化native层.
     */
    public void parpared() {
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

    /**
     * 操作:开始解码并播放.
     */
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

    /**
     * 操作:暂停.
     */
    public void pause() {
        n_pause();
    }

    /**
     * 操作:继续.
     */
    public void resume() {
        n_resume();
    }

    /**
     * 操作:停止.
     */
    public void stop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                n_stop();
            }
        }).start();
    }

    /**
     * 操作:seek.
     */
    public void seek(int secds) {
        n_seek(secds);
    }

    /**
     * 操作:播放速度.
     */
    public void setSpeed(float speed) {
        n_speed(speed);
    }

    //操作:音量.
    public void setVolume(int percent) {
        if (percent >= 0 && percent <= 100) {
            n_volume(percent);
        }
    }

    //操作:音调.
    public void setPitch(float pitch) {
        n_pitch(pitch);
    }

    //操作:声道.
    public void setMute(int mute) {
        n_mute(mute);
    }

    /**
     * 获取视频总时长.
     *
     * @return 总时长
     */
    public int getDuration() {
        return mDuration;
    }

    //==============================Native回调方法==============================

    /**
     * c++回调java的方法.
     */
    public void onCallVideoPrepared() {
        LogUtils.d("C++回调(准备完成)");
        if (mOnPreparedListener != null) {
            mOnPreparedListener.onPrepared();
        }
    }


    /**
     * c++回调java的方法.
     *
     * @param currentTime
     * @param totalTime
     */
    public void onCallVideoTimeInfo(int currentTime, int totalTime) {
//        LogUtils.d("C++回调(视频时间信息)", "当前时间:" + currentTime, "总时间:" + totalTime);
        if (mIPlayerListener == null) {
            return;
        }
        mDuration = totalTime;
        mIPlayerListener.onCurrentTime(currentTime, totalTime);
    }

    /**
     * c++回调java的方法.
     *
     * @param width
     * @param height
     * @param y
     * @param u
     * @param v
     */
    public void onCallVideoRenderYUV(int width, int height, byte[] y, byte[] u, byte[] v) {
//        LogUtils.d("C++回调(宽高及YUV数据)");
        //    native 回调 应用层的入口
//        opengl  的java版本
        if (this.mGLSurfaceView != null) {
            this.mGLSurfaceView.setYUVData(width, height, y, u, v);
        }
    }

    /**
     * c++回调java的方法.
     *
     * @param load
     */
    public void onCallVideoLoad(boolean load) {
        LogUtils.d("C++回调(网络不好)");
//        native   网络不行
    }

    //==============================Native方法==============================

    public native void n_prepared(String source);//准备.

    public native void n_start();//开始播放.

    private native void n_seek(int secds);//seek.

    private native void n_resume();//继续.

    private native void n_pause();//暂停.

    private native void n_mute(int mute);//声道.

    private native void n_volume(int percent);//音量.

    private native void n_speed(float speed);//播放速度.

    private native void n_pitch(float pitch);//音调.

    private native void n_stop();//停止.

}
