package com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.musicui.model;

import java.io.Serializable;

/**
 * @Author WCL
 * @Date 2023/6/10 14:40
 * @Version
 * @Description
 */
public class MusicData implements Serializable {

    /*音乐资源id*/
    private int mMusicRes;
    /*专辑图片id*/
    private int mMusicPicRes;
    /*音乐名称*/
    private String mMusicName;
    /*作者*/
    private String mMusicAuthor;

    public MusicData(int mMusicRes, int mMusicPicRes, String mMusicName, String mMusicAuthor) {
        this.mMusicRes = mMusicRes;
        this.mMusicPicRes = mMusicPicRes;
        this.mMusicName = mMusicName;
        this.mMusicAuthor = mMusicAuthor;
    }

    public int getMusicRes() {
        return mMusicRes;
    }

    public int getMusicPicRes() {
        return mMusicPicRes;
    }

    public String getMusicName() {
        return mMusicName;
    }

    public String getMusicAuthor() {
        return mMusicAuthor;
    }

}
