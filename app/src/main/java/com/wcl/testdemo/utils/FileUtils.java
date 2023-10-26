package com.wcl.testdemo.utils;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * @Author WCL
 * @Date 2023/10/26 14:27
 * @Version
 * @Description 文件保存工具类(适配Android10).
 */
public final class FileUtils {

    /**
     * 将Uri地址对应的源文件,拷贝到目标文件File.
     *
     * @param srcUri     源文件的uri
     * @param targetFile 目标文件的File
     * @return file 目标文件的File或者null
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
     * 保存"图片/GIF"到相册["DCIM/应用名/"].
     * <p>
     * 如果确定源文件仅为图片,则可以使用blankj工具类API:
     * File file = ImageUtils.save2Album(//该工具类API会在相册[DCIM]下,创建指定名称(不指定则默认包名)文件夹,来存放图片.
     * ImageUtils.getBitmap(R.mipmap.ic_launcher), //Bitmap对象.
     * AppUtils.getAppName(), //"DCIM"下创建的文件夹名称(不传默认为包名).
     * Bitmap.CompressFormat.PNG //压缩格式.
     * );
     *
     * @param srcPicFile 图片源文件
     */
    public static void save2DCIM(File srcPicFile) {
        String destName = com.blankj.utilcode.util.FileUtils.getFileName(srcPicFile);//保存系统相册后,文件的名字.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {//Android10(29)以下:
            PermissionUtils.permission(PermissionConstants.STORAGE)//存储权限检查.
                    .callback(new PermissionUtils.SingleCallback() {
                        /**
                         * 权限申请的回调.
                         * @param isAllGranted 申请的权限是否全部同意.
                         * @param granted 同意的权限.
                         * @param deniedForever 被永远拒绝的权限.(权限被永远拒绝了才有内容)
                         * @param denied 被拒绝的权限.(只要有被拒绝的权限就有内容)
                         */
                        @Override
                        public void callback(boolean isAllGranted, @NonNull List<String> granted, @NonNull List<String> deniedForever, @NonNull List<String> denied) {
                            LogUtils.d("列表界面存储权限:", isAllGranted, granted, deniedForever, denied);
                            if (isAllGranted) {//权限全部同意.
                                File destFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), AppUtils.getAppName() + File.separator + destName);
                                boolean isSuccess = com.blankj.utilcode.util.FileUtils.copy(srcPicFile, destFile);
                                if (isSuccess) {
                                    com.blankj.utilcode.util.Utils.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destFile)));
                                }
                            } else {
                                if (deniedForever.size() > 0) {//表示用户点击了"拒绝且不再询问",这时需要跳转设置页允许权限.
                                    setPermissionDialog();
                                } else {//表示用户仅点击了"拒绝",不要再弹窗了以免引起用户反感.
//                                    ToastUtils.showShort("权限被拒,功能受限");
                                }
                            }
                        }
                    })
                    .request();
        } else {
            //组装ContentValues.
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, destName); //(1) DISPLAY_NAME : 文件名.
//            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*"); //(2) MIME_TYPE : 类型.
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + File.separator + AppUtils.getAppName() + File.separator); //(3) RELATIVE_PATH: 文件存储的相对路径.
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
            //操作ContentResolver.
            ContentResolver contentResolver = com.blankj.utilcode.util.Utils.getApp().getContentResolver();
            Uri uri = contentResolver.insert(
                    Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                    values
            );
            if (uri == null) {
//                return false;
                return;
            }
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                //获取输入输出流拷贝文件.
                bis = new BufferedInputStream(new FileInputStream(srcPicFile));
                bos = new BufferedOutputStream(contentResolver.openOutputStream(uri));
                byte[] buffer = new byte[1024];
                int result = -1;
                while ((result = bis.read(buffer)) != -1) {
                    bos.write(buffer);
                }
                //关闭资源
                values.clear();
                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                contentResolver.update(uri, values, null, null);
//                return true;
            } catch (Exception e) {
                Utils.getApp().getContentResolver().delete(uri, null, null);
                e.printStackTrace();
//                return false;
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
     * 保存"视频"到相册["Movies/应用名/"].
     *
     * @param srcVideoFile 视频源文件
     */
    public static void save2Movies(File srcVideoFile) {
        String destName = com.blankj.utilcode.util.FileUtils.getFileName(srcVideoFile);//保存系统相册后,文件的名字.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {//Android10(29)以下:
            PermissionUtils.permission(PermissionConstants.STORAGE)//存储权限检查.
                    .callback(new PermissionUtils.SingleCallback() {
                        /**
                         * 权限申请的回调.
                         * @param isAllGranted 申请的权限是否全部同意.
                         * @param granted 同意的权限.
                         * @param deniedForever 被永远拒绝的权限.(权限被永远拒绝了才有内容)
                         * @param denied 被拒绝的权限.(只要有被拒绝的权限就有内容)
                         */
                        @Override
                        public void callback(boolean isAllGranted, @NonNull List<String> granted, @NonNull List<String> deniedForever, @NonNull List<String> denied) {
                            LogUtils.d("列表界面存储权限:", isAllGranted, granted, deniedForever, denied);
                            if (isAllGranted) {//权限全部同意.
                                File destFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), AppUtils.getAppName() + File.separator + destName);
                                boolean isSuccess = com.blankj.utilcode.util.FileUtils.copy(srcVideoFile, destFile);
                                if (isSuccess) {
                                    com.blankj.utilcode.util.Utils.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destFile)));
                                }
                            } else {
                                if (deniedForever.size() > 0) {//表示用户点击了"拒绝且不再询问",这时需要跳转设置页允许权限.
                                    setPermissionDialog();
                                } else {//表示用户仅点击了"拒绝",不要再弹窗了以免引起用户反感.
//                                    ToastUtils.showShort("权限被拒,功能受限");
                                }
                            }
                        }
                    })
                    .request();
        } else {
            //组装ContentValues.
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.DISPLAY_NAME, destName); //(1) DISPLAY_NAME : 文件名.
//            values.put(MediaStore.Video.Media.MIME_TYPE, "image/*"); //(2) MIME_TYPE : 类型.
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + File.separator + AppUtils.getAppName() + File.separator); //(3) RELATIVE_PATH: 文件存储的相对路径.
            values.put(MediaStore.Video.Media.IS_PENDING, 1);
            //操作ContentResolver.
            ContentResolver contentResolver = Utils.getApp().getContentResolver();
            Uri uri = contentResolver.insert(
                    Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? MediaStore.Video.Media.EXTERNAL_CONTENT_URI : MediaStore.Video.Media.INTERNAL_CONTENT_URI,
                    values
            );
            if (uri == null) {
//                return false;
                return;
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
//                return true;
            } catch (Exception e) {
                Utils.getApp().getContentResolver().delete(uri, null, null);
                e.printStackTrace();
//                return false;
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
     * 保存"文件"到下载目录["Download/应用名/"].
     *
     * @param srcFile 源文件
     */
    public static void save2Download(File srcFile) {
        String destName = com.blankj.utilcode.util.FileUtils.getFileName(srcFile);//保存系统下载目录后,文件的名字.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {//Android10(29)以下:
            PermissionUtils.permission(PermissionConstants.STORAGE)//存储权限检查.
                    .callback(new PermissionUtils.SingleCallback() {
                        /**
                         * 权限申请的回调.
                         * @param isAllGranted 申请的权限是否全部同意.
                         * @param granted 同意的权限.
                         * @param deniedForever 被永远拒绝的权限.(权限被永远拒绝了才有内容)
                         * @param denied 被拒绝的权限.(只要有被拒绝的权限就有内容)
                         */
                        @Override
                        public void callback(boolean isAllGranted, @NonNull List<String> granted, @NonNull List<String> deniedForever, @NonNull List<String> denied) {
                            LogUtils.d("列表界面存储权限:", isAllGranted, granted, deniedForever, denied);
                            if (isAllGranted) {//权限全部同意.
                                File destFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), AppUtils.getAppName() + File.separator + destName);
                                boolean isSuccess = com.blankj.utilcode.util.FileUtils.copy(srcFile, destFile);
                                if (isSuccess) {
                                    Utils.getApp().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(destFile)));
                                }
                            } else {
                                if (deniedForever.size() > 0) {//表示用户点击了"拒绝且不再询问",这时需要跳转设置页允许权限.
                                    setPermissionDialog();
                                } else {//表示用户仅点击了"拒绝",不要再弹窗了以免引起用户反感.
//                                    ToastUtils.showShort("权限被拒,功能受限");
                                }
                            }
                        }
                    })
                    .request();
        } else {
            //组装ContentValues.
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, destName); //(1) DISPLAY_NAME : 文件名.
//            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/*"); //(2) MIME_TYPE : 类型.
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + File.separator + AppUtils.getAppName() + File.separator); //(3) RELATIVE_PATH: 文件存储的相对路径.
            values.put(MediaStore.MediaColumns.IS_PENDING, 1);
            //操作ContentResolver.
            ContentResolver contentResolver = Utils.getApp().getContentResolver();
            Uri uri = contentResolver.insert(
                    Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? MediaStore.Downloads.EXTERNAL_CONTENT_URI : MediaStore.Downloads.INTERNAL_CONTENT_URI,
                    values
            );
            if (uri == null) {
//                return false;
                return;
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
//                return true;
            } catch (Exception e) {
                Utils.getApp().getContentResolver().delete(uri, null, null);
                e.printStackTrace();
//                return false;
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

    //前往"设置"界面开启权限的提示.
    private static void setPermissionDialog() {
        new AlertDialog.Builder(ActivityUtils.getTopActivity())
//                .setIcon(R.mipmap.ic_launcher)//标题图标.
//                .setTitle(R.string.permission_tips)//标题.
                .setMessage("请在设置中开启相关权限，以正常使用功能")//内容.
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {//积极键.
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PermissionUtils.launchAppDetailsSettings();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//消极键.
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        ToastUtils.showShort("权限被拒,功能受限");
                    }
                })
//                .setNeutralButton("按钮", new DialogInterface.OnClickListener() {//中立键.
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                })
                .setCancelable(false)//设置点击返回和外部不取消.
                .show();
    }

}
