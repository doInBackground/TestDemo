package com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test02;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
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
 * @Date 2023/6/6 18:10
 * @Version
 * @Description 用FFmpeg软解, AudioTrack播放音频的界面.
 * 若主线程解码/主线程播放,就会有ANR问题.
 */
public class PlayAudioActivity extends AppCompatActivity {

    private final String mAudioFilePath = new File(PathUtils.getExternalAppDataPath(), "audio").getAbsolutePath();//沙箱根路径中,即将要播放的音频文件audio.
    private volatile int mPlayState = 0;//0表示可以播放.

    /**
     * Comment: 音频播放器.
     * Java用AudioTrack,Native层用OpenSL ES.
     */
    private AudioTrack mAudioTrack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_audio);

        Button bt = findViewById(R.id.bt);
        bt.setText("播放音频\n(播放路径:" + mAudioFilePath + ")\n(可手动替换此路径下的文件进行播放)");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAudioTrack != null) {
            mAudioTrack.release();
        }
    }

    /**
     * 用户点击播放音频按钮.
     *
     * @param view PLAY按钮
     */
    public void play(View view) {
        LogUtils.d("用户点击了播放按钮.");
        if (mPlayState == 0) {
            mPlayState = -1;
            if (FileUtils.isFileExists(mAudioFilePath)) {//文件存在.
                play();
            } else {
                boolean isSuccess = ResourceUtils.copyFileFromAssets("cxzh.mp4", mAudioFilePath);//assets拷贝文件到沙箱.
                if (isSuccess) {
                    play();
                } else {
                    mPlayState = 0;
                    ToastUtils.showShort("从assets拷贝文件到沙箱根路径失败!");
                }
            }
        } else {
            ToastUtils.showShort("正在播放..");
        }
    }

    //子线程播放.
    private void play() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //音频播放测试:
                playSound(mAudioFilePath);
//                playSound("http://mpge.5nd.com/2015/2015-11-26/69708/1.mp3");//测试该网络音乐播放成功.
                //音频播放(过时)测试:
//                playSoundDeprecated(mAudioFilePath);
                mPlayState = 0;
            }
        }).start();
    }


    //Native方法: 通过FFmpeg解码指定路径的音频数据.
    public native void playSound(String input);

    //Native方法(过时): 通过FFmpeg解码指定路径的音频数据.
    @Deprecated
    public native void playSoundDeprecated(String input);

    /**
     * Native层回调的方法: 创建AudioTrack.
     *
     * @param sampleRateInHz 采样频率(如:44100)
     * @param nb_channels    通道数(如:2)
     */
    public void createTrack(int sampleRateInHz, int nb_channels) {
        int channelConfig;//通道数.
        if (nb_channels == 1) {
            channelConfig = AudioFormat.CHANNEL_OUT_MONO;//单通道.
        } else if (nb_channels == 2) {
            channelConfig = AudioFormat.CHANNEL_OUT_STEREO;//双通道.
        } else {
            channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        }
        int bufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, AudioFormat.ENCODING_PCM_16BIT);//音频大小只与"采样频率"&"采样位数"&"通道数"有关.
        mAudioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC, //流类型.
                sampleRateInHz, //采样频率.
                channelConfig, //通道数.
                AudioFormat.ENCODING_PCM_16BIT, //采样位数.
                bufferSize, //每次传入数据的缓冲区大小.
                AudioTrack.MODE_STREAM
        );
        mAudioTrack.play();
    }

    /**
     * Native层回调的方法: 通过AudioTrack播放解码后的音频数据.
     * 函数签名: ([BI)V
     *
     * @param buffer 音频数据(pcm数据内容)
     * @param length 数据长度(pcm实际长度)
     */
    public void playTrack(byte[] buffer, int length) {
        if (mAudioTrack != null && mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.write(buffer, 0, length);
        }
    }

}