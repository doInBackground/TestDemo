package com.wcl.testdemo.test.test04_device.test04.tp.client;


import android.media.MediaCodec;
import android.media.MediaFormat;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.wcl.testdemo.test.test04_device.test04.tp.constant.CODEC_TIME_OUT;
import static com.wcl.testdemo.test.test04_device.test04.tp.constant.SCREEN_FRAME_INTERVAL;
import static com.wcl.testdemo.test.test04_device.test04.tp.constant.SCREEN_FRAME_RATE;
import static com.wcl.testdemo.test.test04_device.test04.tp.constant.VIDEO_HEIGHT;
import static com.wcl.testdemo.test.test04_device.test04.tp.constant.VIDEO_WIDTH;

/**
 * @Author WCL
 * @Date 2023/4/27 16:54
 * @Version
 * @Description 投屏客户端:解码器.
 */
public class TpClientDecoder {

    /**
     * Comment:Android编解码器.
     */
    private MediaCodec mMediaCodec;

    public TpClientDecoder() {
    }

    /**
     * 初始化解码器.
     *
     * @param surface 展示器
     */
    public void startDecode(Surface surface) {
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, VIDEO_WIDTH, VIDEO_HEIGHT);//创建编解码器的格式器.
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, VIDEO_WIDTH * VIDEO_HEIGHT);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, SCREEN_FRAME_RATE);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, SCREEN_FRAME_INTERVAL);
        try {
            // 配置MediaCodec
            mMediaCodec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC);//创建解码器.
            mMediaCodec.configure(mediaFormat, surface, null, 0);//为解解码器设置格式器,并将展示器Surface传入.
            mMediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解码指定字节数组的编码数据.
     *
     * @param data 编码的数据
     */
    public void decodeData(byte[] data) {
//        ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();//过时做法:拿到DSP芯片中所有空闲的ByteBuffer缓冲区.再循环通过dequeueInputBuffer()查索引,再通过索引在ByteBuffer[]中拿到对应数据.
        //将未解码的数据"读入"解码器中,所以用dequeueInputBuffer();
        int index = mMediaCodec.dequeueInputBuffer(CODEC_TIME_OUT);//返回要用有效数据填充的输入缓冲区的索引，如果当前没有可用的缓冲区，则返回-1。
        if (index >= 0) {
            ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(index);//拿到输入缓冲区.
            inputBuffer.clear();//清空数据.
            inputBuffer.put(data, 0, data.length);//放入数据.
            mMediaCodec.queueInputBuffer(index, 0, data.length, System.currentTimeMillis(), 0);//通知DSP芯片解码.(传进去的数据只需要保证编码顺序就好了)
        }
        //获取解码数据.将解码后的数据从解码器中"写出",所以用dequeueOutputBuffer();
//        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
//        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, CODEC_TIME_OUT);
//        while (outputBufferIndex >= 0) {
//            mMediaCodec.releaseOutputBuffer(outputBufferIndex, true); //渲染/播放完成后,消费方Client再将该buffer放回到output缓冲区队列.
//            outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
//        }
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (true) {
            int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, CODEC_TIME_OUT);//获取解码好的输出缓冲区的索引.
            if (outputBufferIndex < 0) {//没有解码好的数据了.
                break;
            }
            mMediaCodec.releaseOutputBuffer(outputBufferIndex, true);//渲染/播放完成后,消费方Client再将该buffer释放回到output缓冲区队列.
        }
    }

    /**
     * 停止解码.
     * 释放解码器.
     */
    public void stopDecode() {
        if (mMediaCodec != null) {
            mMediaCodec.release();
        }
    }
}
