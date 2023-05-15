package com.wcl.testdemo.test.test06_audio_video.test00.tp.service;

import android.hardware.display.DisplayManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.projection.MediaProjection;
import android.view.Surface;

import java.io.IOException;
import java.nio.ByteBuffer;

import static com.wcl.testdemo.test.test06_audio_video.test00.tp.constant.CODEC_TIME_OUT;
import static com.wcl.testdemo.test.test06_audio_video.test00.tp.constant.SCREEN_FRAME_INTERVAL;
import static com.wcl.testdemo.test.test06_audio_video.test00.tp.constant.SCREEN_FRAME_RATE;
import static com.wcl.testdemo.test.test06_audio_video.test00.tp.constant.VIDEO_HEIGHT;
import static com.wcl.testdemo.test.test06_audio_video.test00.tp.constant.VIDEO_WIDTH;

/**
 * @Author WCL
 * @Date 2023/4/27 16:41
 * @Version
 * @Description 投屏服务端:编码器.
 * 采用H.265编码 H.265/HEVC的编码架构大致上和H.264/AVC的架构相似 H.265又称为HEVC(全称High Efficiency Video Coding，高效率视频编码)
 */
public class TpServerEncoder extends Thread {

    /**
     * Comment:
     * NALU Header中Type为19表示NALU Payload中存储的是I帧信息;
     */
    private static final int TYPE_FRAME_INTERVAL = 19;
    /**
     * Comment:
     * NALU Header中Type为32表示NALU Payload中存储的是VPS信息(H265中特有,H264中无);
     * NALU Header中Type为33表示NALU Payload中存储的是SPS信息;
     * NALU Header中Type为34表示NALU Payload中存储的是PPS信息;
     */
    private static final int TYPE_FRAME_VPS = 32;

    /**
     * Comment:投屏服务端-管理器.
     */
    private final TpServerManager mTpServerManager;
    /**
     * Comment:屏幕录制器.
     */
    private final MediaProjection mMediaProjection;
    /**
     * Comment:Android编解码器.
     */
    private MediaCodec mMediaCodec;
    /**
     * Comment:记录是否继续编码.
     */
    private boolean mPlaying = true;
    /**
     * Comment:记录VPS&PPS&SPS数据(即记录视频信息的NALU数据),需要为直播中每个I帧前添加此信息.
     */
    private byte[] vps_pps_sps;

    public TpServerEncoder(TpServerManager tpServerManager, MediaProjection mediaProjection) {
        mTpServerManager = tpServerManager;
        mMediaProjection = mediaProjection;
    }

    /**
     * 开始编码.
     */
    public void startEncode() {
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_HEVC, VIDEO_WIDTH, VIDEO_HEIGHT);//创建编解码器的格式器.
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, VIDEO_WIDTH * VIDEO_HEIGHT);// 比特率（比特/秒）.
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, SCREEN_FRAME_RATE);// 帧率.
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, SCREEN_FRAME_INTERVAL);// I帧的频率.
        try {
            mMediaCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_HEVC);//创建编码器.
            mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);//为编解码器设置格式器.
            Surface surface = mMediaCodec.createInputSurface();//编码器创建输入展示器.
            mMediaProjection.createVirtualDisplay(//屏幕录制器开始录制,将信息录入到编码器创建的输入展示器上.
                    "screen",
                    VIDEO_WIDTH,
                    VIDEO_HEIGHT,
                    1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    surface,
                    null,
                    null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        start();//开启当前线程.
    }

    //线程run()方法.
    @Override
    public void run() {
        mMediaCodec.start();
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        while (mPlaying) {
            int outPutBufferId = mMediaCodec.dequeueOutputBuffer(bufferInfo, CODEC_TIME_OUT);//返回已成功解码的输出缓冲区的索引.
            if (outPutBufferId >= 0) {
                ByteBuffer byteBuffer = mMediaCodec.getOutputBuffer(outPutBufferId);//获取输出Buffer(里面存储了编码好的H265的数据).
                dealFrame(byteBuffer, bufferInfo);
                mMediaCodec.releaseOutputBuffer(outPutBufferId, false);
            }
        }
    }

    //处理I帧,直播H264流需要在每个I帧前添加VPS&PPS&SPS信息,否则中间进入的客户端无法解析视频流.
    //ByteBuffer中存储的是每个NALU的真实数据.
    //BufferInfo中记录一些NALU的信息.
    private void dealFrame(ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo) {
        //H265的NALU Header占一个字节(8位).
        // 第1位表示禁止位(1表示此NALU编码失败可跳过);
        // 中间6位表示NALU类型(即NALU Payload中存储数据的类型);
        // 最后1位表示视频层(3D效果中用到);
        int offSet;//NALU Header在NALU中所处的字节索引(即判断第几个字节是NALU Header).
        if (byteBuffer.get(2) == 0x01) {//每个NALU是以"0x00 00 01"或"0x00 00 00 01"开头的,此处通过判断索引2处(即第3个字节)字节是否是"01",来确定到底是以哪个开头的.
            offSet = 3;//以"0x00 00 01"分隔符开头的,则紧跟其后的NALU Header的字节索引是3(即第4个字节).
        } else {
            offSet = 4;//以"0x00 00 00 01"分隔符开头的,则紧跟其后的NALU Header的字节索引是4(即第5个字节).
        }
        int type = (byteBuffer.get(offSet) & 0x7E) >> 1;//获取NALU Header中间6位的值,得到NALU类型.
        if (type == TYPE_FRAME_VPS) {//(#)NALU Payload中存储的是VPS.当为VPS时,则此ByteBuffer中包含了VPS&PPS&SPS三种信息!!!
            vps_pps_sps = new byte[bufferInfo.size];//创建数组,用来接收此VPS数据.
            byteBuffer.get(vps_pps_sps);//接收.
            //记录到本地即可,不发送,之后每个I帧前添加此数据.
        } else if (type == TYPE_FRAME_INTERVAL) {//(#)NALU Payload中存储的是I帧.
            final byte[] bytes = new byte[bufferInfo.size];//创建数组,用来接收此I帧数据.
            byteBuffer.get(bytes);//接收.
            byte[] newBytes = new byte[vps_pps_sps.length + bytes.length];//创建新数组,大小是(I帧+VPS).
            System.arraycopy(vps_pps_sps, 0, newBytes, 0, vps_pps_sps.length);//拷贝VPS信息到新数组的前面.
            System.arraycopy(bytes, 0, newBytes, vps_pps_sps.length, bytes.length);//拷贝I帧数据到新数组的后面.
            mTpServerManager.sendData(newBytes);//发送:处理后的I帧.
        } else {//(#)NALU Payload中存储的是其他数据,接收到即发送.
            byte[] bytes = new byte[bufferInfo.size];//创建数组,用来接收此数据.
            byteBuffer.get(bytes);//接收.
            mTpServerManager.sendData(bytes);//发送.
        }
    }

    /**
     * 停止编码.
     * 关闭编解码器,再停止屏幕录制器.
     */
    public void stopEncode() {
        mPlaying = false;
        if (mMediaCodec != null) {
            mMediaCodec.release();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
        }
    }
}
