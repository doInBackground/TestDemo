package com.wcl.testdemo.test.test06_audio_video.test02;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.text.TextUtils;

import com.wcl.testdemo.utils.YuvUtils;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @Author WCL
 * @Date 2023/5/16 10:17
 * @Version
 * @Description 编码器.
 */
class EncodePushLiveH265 {

    private static final int NAL_I = 19;
    private static final int NAL_VPS = 32;

    private ISocketLive mSocketLive;//客户端或服务端Socket.
    private int mWidth = 1080;
    private int mHeight = 1920;
    private MediaCodec mMediaCodec;//编码器.
    private byte[] nv12;//nv21转换成nv12的数据.
    private byte[] yuv;//旋转之后的yuv数据.
    private byte[] vps_sps_pps_buf;//记录下VPS数据,方便加在后续每个I帧前.
    private int mFrameIndex;

    public EncodePushLiveH265(String serverIP, ISocketLive.SocketCallback socketCallback, int width, int height) {
        if (TextUtils.isEmpty(serverIP)) {
            this.mSocketLive = new SocketLiveServer(socketCallback);
        } else {
            this.mSocketLive = new SocketLiveClient(serverIP, socketCallback);
        }
        mSocketLive.start();//建立链接.
        this.mWidth = width;
        this.mHeight = height;
    }

    /**
     * 实例化编码器,准备编码.
     */
    public void startLive() {
        //创建对应编码器.
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC);
            MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, mHeight, mWidth);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 1080 * 1920);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 5); //IDR帧刷新时间
            mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();
            int bufferLength = mWidth * mHeight * 3 / 2;
            nv12 = new byte[bufferLength];
            yuv = new byte[bufferLength];
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 编码帧数据(摄像头调用).
     *
     * @param input 摄像头数据(NV21)
     * @return
     */
    public int encode(byte[] input) {
        nv12 = YuvUtils.nv21toNV12(input);//nv21-nv12
        YuvUtils.portraitData2Raw(nv12, yuv, mWidth, mHeight);//旋转

        int inputBufferIndex = mMediaCodec.dequeueInputBuffer(100000);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = mMediaCodec.getInputBuffer(inputBufferIndex);
            inputBuffer.clear();
            inputBuffer.put(yuv);
            long presentationTimeUs = computePresentationTime(mFrameIndex);
            mMediaCodec.queueInputBuffer(inputBufferIndex, 0, yuv.length, presentationTimeUs, 0);
            mFrameIndex++;
        }
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 100000);
        while (outputBufferIndex >= 0) {
            ByteBuffer outputBuffer = mMediaCodec.getOutputBuffer(outputBufferIndex);
            dealFrame(outputBuffer, bufferInfo);
            mMediaCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mMediaCodec.dequeueOutputBuffer(bufferInfo, 0);
        }
        return 0;
    }

    /**
     * 关闭编码器,并关闭Socket传输.
     */
    public void close() {
        if (mMediaCodec != null) {
            mMediaCodec.release();
        }
        mSocketLive.close();
    }

    //时间戳处理.
    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / 15;
    }

    //处理NAL数据.
    private void dealFrame(ByteBuffer bb, MediaCodec.BufferInfo bufferInfo) {
        int offset = 4;
        if (bb.get(2) == 0x01) {
            offset = 3;
        }
        int type = (bb.get(offset) & 0x7E) >> 1;
        if (type == NAL_VPS) {//VPS
            vps_sps_pps_buf = new byte[bufferInfo.size];
            bb.get(vps_sps_pps_buf);
        } else if (type == NAL_I) {//I帧
            final byte[] bytes = new byte[bufferInfo.size];
            bb.get(bytes);
            byte[] newBuf = new byte[vps_sps_pps_buf.length + bytes.length];
            System.arraycopy(vps_sps_pps_buf, 0, newBuf, 0, vps_sps_pps_buf.length);
            System.arraycopy(bytes, 0, newBuf, vps_sps_pps_buf.length, bytes.length);
            this.mSocketLive.sendData(newBuf);
        } else {
            final byte[] bytes = new byte[bufferInfo.size];
            bb.get(bytes);
            this.mSocketLive.sendData(bytes);
//            LogUtils.v("视频数据:  " + Arrays.toString(bytes));
        }
    }

}
