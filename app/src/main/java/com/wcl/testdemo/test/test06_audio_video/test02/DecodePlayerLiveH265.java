package com.wcl.testdemo.test.test06_audio_video.test02;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import com.blankj.utilcode.util.LogUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Author WCL
 * @Date 2023/5/16 10:11
 * @Version
 * @Description 解码器.
 */
public class DecodePlayerLiveH265 {

    private MediaCodec mMediaCodec;//解码器.

    /**
     * 初始化解码器.
     *
     * @param surface 展示器
     */
    public void initDecoder(Surface surface) {
        try {
            mMediaCodec = MediaCodec.createDecoderByType("video/hevc");
            final MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, 1080, 1920);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 1080 * 1920);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5);
            mMediaCodec.configure(format, surface, null, 0);
            mMediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 解码器解码数据.
     *
     * @param data 待解码的数据
     */
    public void decode(byte[] data) {
        LogUtils.i("接收到消息长度: " + data.length);
        int index = mMediaCodec.dequeueInputBuffer(100000);
        if (index >= 0) {
            ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(index);
            inputBuffer.clear();
            inputBuffer.put(data, 0, data.length);
            mMediaCodec.queueInputBuffer(index, 0, data.length, System.currentTimeMillis(), 0);//通知dsp芯片解码,解码的传进去的,只需要保证编码顺序就好了.
        }
        //获取到解码后的数据,编码,ipbn
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 100000);
        while (outputBufferIndex >= 0) {
            mMediaCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
    }

}
