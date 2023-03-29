package com.wcl.testdemo.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class FileUtils {

    /**
     * Uri转File文件.
     *
     * @param srcUri     uri
     * @param targetFile file
     * @return file
     */
    public static File copyUri2File(Uri srcUri, File targetFile) {
        InputStream is = null;
        try {
            is = Utils.getApp().getContentResolver().openInputStream(srcUri);
//            File file = new File(Utils.getApp().getExternalCacheDir(), "" + System.currentTimeMillis());
//            UtilsBridge.writeFileFromIS(file.getAbsolutePath(), is);
            FileIOUtils.writeFileFromIS(targetFile, is);
            return targetFile;
        } catch (FileNotFoundException e) {
            LogUtils.e("流拷贝出错:", e);
            e.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存视频到相册.
     * 保存视频到"DCIM/包名/"目录.
     *
     * @param srcVideoFile 视频文件
     * @param destName     保存到"DCIM/包名/"下时,文件的名字
     * @return 是否成功
     */
    public static boolean save2Movies(File srcVideoFile, String destName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {//Android10(29)以下:
            //此处省略了存储权限检查.
            File destFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), Utils.getApp().getPackageName() + "/" + destName);
            boolean isSuccess = com.blankj.utilcode.util.FileUtils.copy(srcVideoFile, destFile);
            if (isSuccess) {
                Utils.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destFile)));
            }
            return isSuccess;
        } else {
            //组装ContentValues.
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.DISPLAY_NAME, destName); //(1) DISPLAY_NAME : 文件名.
//            values.put(MediaStore.Video.Media.MIME_TYPE, "image/*"); //(2) MIME_TYPE : 类型.
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/" + Utils.getApp().getPackageName() + "/"); //(3) RELATIVE_PATH: 文件存储的相对路径.
            values.put(MediaStore.Video.Media.IS_PENDING, 1);
            //操作ContentResolver.
            ContentResolver contentResolver = Utils.getApp().getContentResolver();
            Uri uri = contentResolver.insert(
                    Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI : MediaStore.Video.Media.INTERNAL_CONTENT_URI,
                    values
            );
            if (uri == null) {
                return false;
            }
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                //获取输入输出流拷贝文件.
                bis = new BufferedInputStream(new FileInputStream(srcVideoFile));
                bos = new BufferedOutputStream(contentResolver.openOutputStream(uri));
                byte[] buffer = new byte[1024];
                int result = -1;
                while ((result = bis.read(buffer)) != -1) {
                    bos.write(buffer);
                }
                //关闭资源
                values.clear();
                values.put(MediaStore.Video.Media.IS_PENDING, 0);
                contentResolver.update(uri, values, null, null);
                return true;
            } catch (Exception e) {
                Utils.getApp().getContentResolver().delete(uri, null, null);
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存文件到下载.
     * 保存文件到"Download/包名/"目录.
     *
     * @param srcFile  需要保存到"Download"目录下的源文件
     * @param destName 保存到"Download/包名/"下时,文件的名字
     * @return 是否成功
     */
    public static boolean save2Download(File srcFile, String destName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {//Android10(29)以下:
            //此处省略了存储权限检查.
            File destFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Utils.getApp().getPackageName() + "/" + destName);
            boolean isSuccess = com.blankj.utilcode.util.FileUtils.copy(srcFile, destFile);
            if (isSuccess) {
                Utils.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destFile)));
            }
            return isSuccess;
        } else {
            //组装ContentValues.
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, destName); //(1) DISPLAY_NAME : 文件名.
//            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/*"); //(2) MIME_TYPE : 类型.
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + Utils.getApp().getPackageName() + "/"); //(3) RELATIVE_PATH: 文件存储的相对路径.
            values.put(MediaStore.MediaColumns.IS_PENDING, 1);
            //操作ContentResolver.
            ContentResolver contentResolver = Utils.getApp().getContentResolver();
            Uri uri = contentResolver.insert(
                    Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? MediaStore.Downloads.EXTERNAL_CONTENT_URI : MediaStore.Downloads.INTERNAL_CONTENT_URI,
                    values
            );
            if (uri == null) {
                return false;
            }
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                //获取输入输出流拷贝文件.
                bis = new BufferedInputStream(new FileInputStream(srcFile));
                bos = new BufferedOutputStream(contentResolver.openOutputStream(uri));
                byte[] buffer = new byte[1024];
                int result = -1;
                while ((result = bis.read(buffer)) != -1) {
                    bos.write(buffer);
                }
                //关闭资源
                values.clear();
                values.put(MediaStore.MediaColumns.IS_PENDING, 0);
                contentResolver.update(uri, values, null, null);
                return true;
            } catch (Exception e) {
                Utils.getApp().getContentResolver().delete(uri, null, null);
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (bis != null) {
                        bis.close();
                    }
                    if (bos != null) {
                        bos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
