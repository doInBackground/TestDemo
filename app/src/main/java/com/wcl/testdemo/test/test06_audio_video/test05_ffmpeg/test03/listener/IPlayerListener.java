package com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.listener;

/**
 * @Author WCL
 * @Date 2023/6/13 14:36
 * @Version
 * @Description
 */
public interface IPlayerListener {

    void onLoad(boolean load);

    void onCurrentTime(int currentTime, int totalTime);

    void onError(int code, String msg);

    void onPause(boolean pause);

    void onDbValue(int db);

    void onComplete();

    String onNext();

}
