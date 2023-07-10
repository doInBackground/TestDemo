package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.opengl.EGLContext;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Surface;

import com.wcl.testdemo.utils.YuvUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Author WCL
 * @Date 2023/5/24 14:08
 * @Version
 * @Description 编码录制类.
 * 将OpenGL画面信息录制到本地.
 */
class MediaRecorder {

    private MediaCodec mMediaCodec;
    private int mWidth;
    private int mHeight;
    private String mPath;
    private Surface mSurface;
    private Handler mHandler;
    private EGLContext mGlContext;
    private EGLEnv eglEnv;
    private boolean isStart;
    private Context mContext;
    private long startTime;

    public MediaRecorder(Context context, String path, EGLContext glContext, int width, int height) {
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
        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);//颜色空间 从 surface当中获得
        format.setInteger(MediaFormat.KEY_BIT_RATE, 1500_000);//码率
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 25);//帧率
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);//关键帧间隔.
        mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);//创建编码器
        mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);//配置编码器.

        //此时编码的输入数据不再是byte[]类型了,而是存在GPU中,使用该数据编码时类似投屏时使用的MediaProjection.
        mSurface = mMediaCodec.createInputSurface();//MediaCodec提供场地Surface,供数据源塞入数据,MediaCodec再编码数据.

        mMediaCodec.start();//开启编码.

        //重点:
        //此时经过OpenGL处理的像素数据在GPU中,而不再是CPU中的byte[]类型的数据了,所以编码器的编码方法变成了提供一个场地Surface,数据源向里面塞数据,它来编码.
        //所以OpenGL就要与这个场地Surface进行关联,就要调用OpenGL的方法.
        //而OpenGL的方法必须要在EGL环境下调用(GLSurfaceView自带EGL环境),而此处为主线程,所以要自己构建EGL环境.
        //做法:新建线程,在新建线程中构建EGL环境,此后便可以在此新建线程中进行OpenGL方法调用了.
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
        isStart = false;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                codec(true);
                mMediaCodec.stop();
                mMediaCodec.release();
                mMediaCodec = null;
                eglEnv.release();
                eglEnv = null;
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
                eglEnv.draw(textureId, timestamp);//调用draw()后会渲染到Surface(MediaCodec提供的待编码场地)中.
                codec(false);
            }
        });

    }

    //编码.
    private void codec(boolean endOfStream) {
        if (endOfStream) {
            mMediaCodec.signalEndOfInputStream();//给个结束信号.
        }
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int index = mMediaCodec.dequeueOutputBuffer(bufferInfo, 10_000);
//        Log.i(TAG, "run: " + index);
        if (index >= 0) {
            ByteBuffer buffer = mMediaCodec.getOutputBuffer(index);
            MediaFormat mediaFormat = mMediaCodec.getOutputFormat(index);
//            Log.i(TAG, "mediaFormat: " + mediaFormat.toString());
            byte[] outData = new byte[bufferInfo.size];
            buffer.get(outData);
            if (startTime == 0) {
                startTime = bufferInfo.presentationTimeUs / 1000;//微秒转为毫秒.
            }
            YuvUtils.writeContent(outData);
            YuvUtils.writeBytes(outData);
            mMediaCodec.releaseOutputBuffer(index, false);
        }
    }

}
