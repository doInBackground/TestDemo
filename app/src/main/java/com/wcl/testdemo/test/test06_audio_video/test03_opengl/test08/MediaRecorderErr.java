package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Author WCL
 * @Date 2023/5/24 14:29
 * @Version
 * @Description 落地视频录制为MP4格式.(落地MP4问题待解决)
 */
@Deprecated
class MediaRecorderErr {

    private MediaCodec mMediaCodec;
    private int mWidth;
    private int mHeight;
    private String mPath;
    private Surface mSurface;
    private Handler mHandler;
    private MediaMuxer mMuxer;
    private EGLContext mGlContext;
    private EGLEnv eglEnv;
    private boolean isStart;
    private Context mContext;
    private long mLastTimeStamp;
    private int track;
    private float mSpeed;

    public MediaRecorderErr(Context context, String path, EGLContext glContext, int width, int height) {
        mContext = context.getApplicationContext();
        mPath = path;
        mWidth = width;
        mHeight = height;
        mGlContext = glContext;
    }

    /**
     * 准备编码录制.
     *
     * @param speed 录制速度
     * @throws IOException
     */
    public void start(float speed) throws IOException {
        mSpeed = speed;
        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);//颜色空间 从 surface当中获得
        format.setInteger(MediaFormat.KEY_BIT_RATE, 1500_000);//码率
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 25);//帧率
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);//关键帧间隔
        mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);//创建编码器
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);//配置编码器

        //此时编码的输入数据不再是byte[]类型了,而是存在GPU中,使用该数据编码时类型投屏时使用MediaProjection.
        mSurface = mMediaCodec.createInputSurface();//MediaCodec提供场地,供数据源塞入数据,MediaCodec再编码数据.

        mMuxer = new MediaMuxer(mPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);//混合器(复用器):将编码的h.264封装为可以播放的视频mp4.

        mMediaCodec.start();//开启编码.

        //重点: OpenGL数据在GPU里面,肯定要调用OpenGL函数.
        //线程:创建OpenGL的环境.
        HandlerThread handlerThread = new HandlerThread("codec-gl");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                eglEnv = new EGLEnv(mContext, mGlContext, mSurface, mWidth, mHeight);
                isStart = true;
            }
        });
    }

    /**
     * 释放.
     */
    public void stop() {
        // 释放
        isStart = false;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                codec(true);
                mMediaCodec.stop();
                mMediaCodec.release();
                mMediaCodec = null;
                mMuxer.stop();
                mMuxer.release();
                eglEnv.release();
                eglEnv = null;
                mMuxer = null;
                mSurface = null;
                mHandler.getLooper().quitSafely();
                mHandler = null;
            }
        });
    }

    /**
     * 编码textureId对应的纹理数据.
     *
     * @param textureId 纹理ID
     * @param timestamp 时间戳
     */
    public void fireFrame(final int textureId, final long timestamp) {
        //主动拉去opengl-fbo数据.
        if (!isStart) {
            return;
        }
        //录制用的opengl已经和handler的线程绑定了,所以需要在这个线程中使用录制的opengl.
        mHandler.post(new Runnable() {
            public void run() {
                //此处为GL线程,可以调用OpenGL方法.
                eglEnv.draw(textureId, timestamp);//调用draw()后渲染到Surface中.
                codec(false);
            }
        });

    }

    //编码.
    private void codec(boolean endOfStream) {
        if (endOfStream) {
            mMediaCodec.signalEndOfInputStream();//给个结束信号.
        }
        while (true) {
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int index = mMediaCodec.dequeueOutputBuffer(bufferInfo, 10_000);
            //编码的地方
            //需要更多数据
            if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) {//如果是结束那直接退出,否则继续循环.
                    break;
                }
            } else if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                //输出格式发生改变  第一次总会调用所以在这里开启混合器
                MediaFormat newFormat = mMediaCodec.getOutputFormat();
                track = mMuxer.addTrack(newFormat);
                mMuxer.start();
            } else if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                //可以忽略
            } else {
                //调整时间戳
                bufferInfo.presentationTimeUs = (long) (bufferInfo.presentationTimeUs / mSpeed);
                //有时候会出现异常 ： timestampUs xxx < lastTimestampUs yyy for Video track
                if (bufferInfo.presentationTimeUs <= mLastTimeStamp) {
                    bufferInfo.presentationTimeUs = (long) (mLastTimeStamp + 1_000_000 / 25 / mSpeed);
                }
                mLastTimeStamp = bufferInfo.presentationTimeUs;
                //正常则 index 获得缓冲区下标
                ByteBuffer encodedData = mMediaCodec.getOutputBuffer(index);
                //如果当前的buffer是配置信息，不管它 不用写出去
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    bufferInfo.size = 0;
                }
                if (bufferInfo.size != 0) {
                    //设置从哪里开始读数据(读出来就是编码后的数据)
                    encodedData.position(bufferInfo.offset);
                    //设置能读数据的总长度
                    encodedData.limit(bufferInfo.offset + bufferInfo.size);
                    //写出为mp4
                    mMuxer.writeSampleData(track, encodedData, bufferInfo);
                }
                // 释放这个缓冲区，后续可以存放新的编码后的数据啦
                mMediaCodec.releaseOutputBuffer(index, false);
                // 如果给了结束信号 signalEndOfInputStream
                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break;
                }
            }
        }
    }

}
