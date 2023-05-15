package com.wcl.testdemo.test.test06_audio_video.test01;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;

import androidx.annotation.NonNull;

/**
 * @Author WCL
 * @Date 2023/5/15 15:54
 * @Version
 * @Description 本测试用例使用的是Camera, 很多是过时方法, 故本例仅作编解码参考.
 * YUV的NV21为安卓摄像头默认输出格式. YUV的I420(又称NV12)为通用输出格式.
 * 摄像头捕获的数据,转换成H264码流.网络传输: 需要在每个I帧之前,先传sps pps.
 * webrtc适合视频通话,不适合短视频,直播.
 */
public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    public static final String CAMERA_DATA_H264 = PathUtils.getExternalAppCachePath() + "/camera.h264";
    public static final String CAMERA_DATA_TXT = PathUtils.getExternalAppCachePath() + "/camera.txt";

    private Camera mCamera;
    private Camera.Size mSize;
    private MediaCodec mMediaCodec;
    private volatile int mType;//录制模式.(0:仅预览;1:录制;2:拍照;)
    //缓冲区,尽量弄成全局,不要弄成局部,否则容易栈溢出.
    byte[] mBufferArr;
    byte[] mNv21RotatedArr;//NV21画面旋转90度.
    byte[] mI420Arr;//即通用的I420数据.

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        startPreview();
        initCodec();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        if (null != mCamera) {
            mCamera.stopPreview();
            mCamera.stopFaceDetection();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 摄像头捕获的数据.
     *
     * @param bytes  单帧画面数据
     * @param camera 相机
     */
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (mType == 1) {//录像:将每一帧,落地到视频文件.
            mNv21RotatedArr = new byte[bytes.length];//创建同等大小的数组,接收旋转90度后的图像数据.
            mNv21RotatedArr = portraitData2Raw(bytes);//nv21数据旋转90度.
            byte[] temp = nv21toI420(mNv21RotatedArr);//nv21格式转换成I420格式.

            //准备编码.
            int inIndex = mMediaCodec.dequeueInputBuffer(100000);//返回要用有效数据填充的输入缓冲区的索引，如果当前没有可用的缓冲区，则返回-1。
            if (inIndex >= 0) {
                ByteBuffer byteBuffer = mMediaCodec.getInputBuffer(inIndex);
                byteBuffer.clear();
                byteBuffer.put(temp, 0, temp.length);//放入原始数据(旋转&I420),请求编码.
                mMediaCodec.queueInputBuffer(inIndex, 0, temp.length, 0, 0);
            }

            //获取编码好的数据.
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            int outIndex = mMediaCodec.dequeueOutputBuffer(info, 100000);//获取解码好的输出缓冲区的索引.
            if (outIndex >= 0) {
                ByteBuffer byteBuffer = mMediaCodec.getOutputBuffer(outIndex);//数据
                byte[] ba = new byte[byteBuffer.remaining()];//存放H264数据.
                byteBuffer.get(ba);
                writeBytes(ba);//落地成h264视频文件.
                writeContent(ba);//落地成16进制数据的txt文件,方便查看数据内容.
                mMediaCodec.releaseOutputBuffer(outIndex, false);
            }
        } else if (mType == 2) {//拍照:将当前帧,落地到图片文件.
            mType = 0;
            byte[] rotation = portraitData2Raw(bytes);//nv21数据旋转90度.
            capture(rotation);
        }
        mCamera.addCallbackBuffer(bytes);
    }

    /**
     * 设置录制值模式.
     *
     * @param type (0:仅预览;1:录制;2:拍照;)
     */
    public void setType(int type) {
        this.mType = type;
    }

    //开始预览摄像头.
    private void startPreview() {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);//打开相机.
        Camera.Parameters parameters = mCamera.getParameters();//相机参数集.
        mSize = parameters.getPreviewSize();//参数-尺寸
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//参数设置-自动对焦.
        }
        mCamera.setParameters(parameters);//参数应用.
        try {
            mCamera.setPreviewDisplay(getHolder());//Camera与SurfaceView关联起来!!!
            mCamera.setDisplayOrientation(90);//横着.
            mBufferArr = new byte[mSize.width * mSize.height * 3 / 2];
            mNv21RotatedArr = new byte[mSize.width * mSize.height * 3 / 2];
            mCamera.addCallbackBuffer(mBufferArr);
            mCamera.setPreviewCallbackWithBuffer(this);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化编码器.
    private void initCodec() {
        try {
            LogUtils.i("height: " + mSize.height + ",  width:  " + mSize.width);
            mMediaCodec = MediaCodec.createEncoderByType("video/avc");
            final MediaFormat format = MediaFormat.createVideoFormat("video/avc", mSize.height, mSize.width);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);//帧率
            format.setInteger(MediaFormat.KEY_BIT_RATE, 4000_000);//比特率（比特/秒）
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 2);//2s一个I帧
            mMediaCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mMediaCodec.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //字节数组转16进制数据落地(方便查看数据内容).
    private String writeContent(byte[] array) {
        final char[] HEX_CHAR_TABLE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder();
        //字节转化为16进制的数据.
        for (byte b : array) {
            sb.append(HEX_CHAR_TABLE[(b & 0xf0) >> 4]);//字节(8位)与"0b 1111 0000"相与,再右移4位,取值,转16进制字符.
            sb.append(HEX_CHAR_TABLE[b & 0x0f]);//字节(8位)与"0b 0000 1111"相与,取值,转16进制字符.
        }
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(CAMERA_DATA_TXT, true);
            writer.write(sb.toString());
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    //字节数组转文件落地.
    private void writeBytes(byte[] array) {
        FileOutputStream writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件.
            writer = new FileOutputStream(CAMERA_DATA_H264, true);
            writer.write(array);
            writer.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //安卓摄像头的NV21数据转换为通用的I420数据.
    private byte[] nv21toI420(byte[] nv21) {
        int size = nv21.length;
        mI420Arr = new byte[size];
        int len = size * 2 / 3;
        System.arraycopy(nv21, 0, mI420Arr, 0, len);

        int i = len;
        while (i < size - 1) {
            mI420Arr[i] = nv21[i + 1];
            mI420Arr[i + 1] = nv21[i];
            i += 2;
        }
        return mI420Arr;
    }

    //NV21数据顺时针旋转90度.
    private byte[] portraitData2Raw(byte[] data) {
        int width = mSize.width;
        int height = mSize.height;
        int y_size = width * height;
        int buffer_size = y_size * 3 / 2;
        int i = 0;
        int startPos = (height - 1) * width;
        for (int x = 0; x < width; x++) {
            int offset = startPos;
            for (int y = height - 1; y >= 0; y--) {
                mNv21RotatedArr[i] = data[offset + x];
                i++;
                offset -= width;
            }
        }
        i = buffer_size - 1;
        for (int x = width - 1; x > 0; x = x - 2) {
            int offset = y_size;
            for (int y = 0; y < height / 2; y++) {
                mNv21RotatedArr[i] = data[offset + x];
                i--;
                mNv21RotatedArr[i] = data[offset + (x - 1)];
                i--;
                offset += width;
            }
        }
        return mNv21RotatedArr;
    }

    //将NV21数据保存为一张照片.
    public void capture(byte[] temp) {
        File pictureFile = new File(PathUtils.getExternalAppCachePath() + "/IMG_" + System.currentTimeMillis() + ".jpg");
        if (!pictureFile.exists()) {
            try {
                pictureFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(pictureFile);
                //ImageFormat.NV21 and ImageFormat.YUY2 for now.
                YuvImage image = new YuvImage(temp, ImageFormat.NV21, mSize.height, mSize.width, null);//将NV21保存成YuvImage.
                image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, fos); //YuvImage图像压缩成Jpeg，并得到JPEG数据流.
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
