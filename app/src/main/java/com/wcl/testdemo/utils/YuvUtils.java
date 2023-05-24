package com.wcl.testdemo.utils;

import com.blankj.utilcode.util.PathUtils;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Author WCL
 * @Date 2023/5/16 9:50
 * @Version
 * @Description YUV数据转换工具类.
 */
public class YuvUtils {

    public static final String TEMP_DATA_H264 = PathUtils.getExternalAppCachePath() + "/temp.h264";
    private static final String TEMP_DATA_TXT = PathUtils.getExternalAppCachePath() + "/temp.txt";
    private static byte[] nv12;

    /**
     * NV21格式数据,转换为NV12(即I420)格式数据.
     *
     * @param nv21Arr 安卓摄像头默认输出的NV21格式数据
     * @return NV12(即I420)格式数据
     */
    public static byte[] nv21toNV12(byte[] nv21Arr) {
        int size = nv21Arr.length;
        nv12 = new byte[size];
        int len = size * 2 / 3;
        System.arraycopy(nv21Arr, 0, nv12, 0, len);
        int i = len;
        while (i < size - 1) {
            nv12[i] = nv21Arr[i + 1];
            nv12[i + 1] = nv21Arr[i];
            i += 2;
        }
        return nv12;
    }

    /**
     * NV12(即I420)数据顺时针旋转90度.
     *
     * @param i420Arr   原始数据的数组
     * @param targetArr 接收旋转后数据的数组
     * @param width     旋转后的宽
     * @param height    旋转后的高
     */
    public static void portraitData2Raw(byte[] i420Arr, byte[] targetArr, int width, int height) {
        int y_len = width * height;
        int uvHeight = height >> 1; // uv数据高为y数据高的一半.
        int k = 0;
        for (int j = 0; j < width; j++) {
            for (int i = height - 1; i >= 0; i--) {
                targetArr[k++] = i420Arr[width * i + j];
            }
        }
        for (int j = 0; j < width; j += 2) {
            for (int i = uvHeight - 1; i >= 0; i--) {
                targetArr[k++] = i420Arr[y_len + width * i + j];
                targetArr[k++] = i420Arr[y_len + width * i + j + 1];
            }
        }
    }

    /**
     * 字节数组数据,追加到缓存H264文件末尾,并落地.
     *
     * @param arr 要追加的字节数组数据
     */
    public static void writeBytes(byte[] arr) {
        FileOutputStream writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件.
            writer = new FileOutputStream(TEMP_DATA_H264, true);
            writer.write(arr);
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

    /**
     * 字节数组数据,转16进制字符,追加到缓存TXT文件末尾,并落地(方便查看数据内容).
     *
     * @param arr 要追加的字节数组数据
     * @return 该字节数组数据的16进制字符内容
     */
    public static String writeContent(byte[] arr) {
        char[] HEX_CHAR_TABLE = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuilder sb = new StringBuilder();
        //字节转化为16进制的数据.
        for (byte b : arr) {
            sb.append(HEX_CHAR_TABLE[(b & 0xf0) >> 4]);//字节(8位)与"0b 1111 0000"相与,再右移4位,取值,转16进制字符.
            sb.append(HEX_CHAR_TABLE[b & 0x0f]);//字节(8位)与"0b 0000 1111"相与,取值,转16进制字符.
        }
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件.
            writer = new FileWriter(TEMP_DATA_TXT, true);
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

}
