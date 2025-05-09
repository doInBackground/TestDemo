package com.wcl.testdemo.test.test01_androidbase.test03;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.blankj.utilcode.util.Utils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.wcl.testdemo.init.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/3/29 10:23
 * @Version
 * @Description 测试文件的持久化保存.
 */
public class SaveFileActivity extends BaseActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;
    @BindView(R.id.tv_4)
    TextView mTv4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_file);
        ButterKnife.bind(this);
        mTv4.setText("测试4:\n将[特定系统相册](" + PathUtils.getExternalMoviesPath() + File.separator + AppUtils.getAppName() + ")路径下所有的视频文件拷贝到[沙箱]缓存路径.");
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://获取本地各种路径.
                printPath();
                break;
            case R.id.tv_1://保存图片到[相册路径].
                save2DCIM();
                break;
            case R.id.tv_2://保存视频到[视频路径]
                save2Movies();
                break;
            case R.id.tv_3://保存文件到[下载路径]
                save2Download();
                break;
            case R.id.tv_4://将[特定系统相册](系统相册Movies目录下以本APP名称开头的文件夹)路径下所有的视频文件拷贝到[沙箱]缓存路径.
                copyFromMovies();
                break;
            case R.id.tv_5://
                break;
            case R.id.tv_6://
                break;
            case R.id.tv_7://
                break;
            case R.id.tv_8://
                break;
            case R.id.tv_9://
                break;
            case R.id.tv_10://
                break;
        }
    }

    //将[特定系统相册](系统相册Movies目录下以本APP名称开头的文件夹)路径下所有的视频文件拷贝到[沙箱]缓存路径.
    private void copyFromMovies() {
        StringBuilder sb = new StringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {//Android10(29)及以上:
            String[] projection = {
                    MediaStore.Images.Media._ID, //数据库中的主键.
                    MediaStore.Images.Media.DATA, //文件的绝对路径.
                    MediaStore.Files.FileColumns.MIME_TYPE, //文件类型(可能为空).
                    MediaStore.Files.FileColumns.SIZE, //文件大小.
                    MediaStore.Files.FileColumns.TITLE //文件名称[不带后缀名].
            };
            String selection = MediaStore.Video.Media.DATA + " like ?"; //筛选条件.
            String[] selectionArgs = {PathUtils.getExternalMoviesPath() + File.separator + AppUtils.getAppName() + "%"}; //筛选条件的参数:指定文件夹(系统相册Movies目录下以本APP名称开头的文件夹).
            Cursor cursor = getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, //标识:表示查找视频文件.
                    projection, //查数据库找到某文件后,需要的[文件信息]的数组.其余不需要的[文件信息]不返回,查询未返回的信息会报错.
                    selection,
                    selectionArgs,
                    MediaStore.MediaColumns.DATE_ADDED + " DESC" //排序规则.
            );
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                    String destName = com.blankj.utilcode.util.FileUtils.getFileName(path);
                    sb.append("文件查询:\n(1) 文件ID: ").append(id).append("\n(2) 文件的[相册路径]: ").append(path);
                    Uri uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id); //将数据库ID转为Uri.
                    try {
                        InputStream is = getContentResolver().openInputStream(uri); //相册文件的输入流.
                        File tempFile = new File(PathUtils.getCachePathExternalFirst() + File.separator + AppUtils.getAppName(), destName); //沙箱中视频路径.
                        boolean isSuccess = FileIOUtils.writeFileFromIS(tempFile, is); //将流拷贝到文件.
                        sb.append("\n(3) 从[相册路径]拷贝到[沙箱]是否成功: ").append(isSuccess);
                        if (isSuccess) {
                            sb.append("\n(4) 拷贝到[沙箱]的文件路径: ").append(tempFile.getAbsolutePath());
                        }
                    } catch (FileNotFoundException e) {
                        LogUtils.e("打开流报错:", e);
                        e.printStackTrace();
                    }
                }
                if (sb.length() <= 0) {
                    sb.append("[特定系统相册]不存在或其中无内容.");
                }
            } else {
                sb.append("查询出错!");
            }
        } else {
            sb.append("本测试仅提供给Android10(29)及以上系统.");
        }
        print(sb.toString());
    }

    //保存文件到[下载路径]
    private void save2Download() {
        InputStream is = null;
        try {
            is = Utils.getApp().getAssets().open("demo_h264_368_384.mp4"); //"assets"中文件路径.
            File tempFile = new File(Utils.getApp().getExternalCacheDir(), "demo_h264_368_384.mp4"); //沙箱中文件路径.
            boolean isSuccess = FileIOUtils.writeFileFromIS(tempFile, is); //拷贝.
            if (isSuccess) {
                //***********核心调用(仅下面一行)***********.
                FileUtils.save2Download(tempFile);//从沙箱拷贝到[下载路径].
                print("保存文件到[下载路径],请前往查看.");
            } else {
                print("从\"assets\"中拷贝文件到\"沙箱\"出错!!!");
            }
        } catch (IOException e) {
            print("从\"assets\"中获取文件出错!!!");
            e.printStackTrace();
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

    //保存视频到[视频路径].
    private void save2Movies() {
        InputStream is = null;
        try {
            is = Utils.getApp().getAssets().open("demo_h264_368_384.mp4"); //"assets"中视频路径.
            File tempFile = new File(Utils.getApp().getExternalCacheDir(), "demo_h264_368_384.mp4"); //沙箱中视频路径.
            boolean isSuccess = FileIOUtils.writeFileFromIS(tempFile, is); //拷贝.
            if (isSuccess) {
                //***********核心调用(仅下面一行)***********.
                FileUtils.save2Movies(tempFile);//从沙箱拷贝到[视频相册].
                print("保存视频到相册的[Movies],请前往查看.");
            } else {
                print("从\"assets\"中拷贝视频到\"沙箱\"出错!!!");
            }
        } catch (IOException e) {
            print("从\"assets\"中获取视频出错!!!");
            e.printStackTrace();
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

    //保存图片到相册.
    private void save2DCIM() {
        //***********核心调用(仅下面一行)***********.
        File file = ImageUtils.save2Album(//该工具类API会在相册[DCIM]下,创建指定名称(不指定则默认包名)文件夹,来存放图片.
                ImageUtils.getBitmap(R.mipmap.ic_launcher), //Bitmap对象.
                AppUtils.getAppName(), //"DCIM"下创建的文件夹名称(不传默认为包名).
                Bitmap.CompressFormat.PNG //压缩格式.
        );
        Uri uri = UriUtils.file2Uri(file);
        String msg1 = "保存图片到相册成功:" +
                "\n(1)图片保存地址(File): " + file + // "/storage/emulated/0/DCIM/TestDemo/1677467432067_100.PNG"
                "\n(2)图片保存地址(Uri): " + uri + // "content://com.wcl.testdemo.utilcode.fileprovider/external_path/DCIM/TestDemo/1677467432067_100.PNG"
                "\n(3-1)Uri-Scheme: " + uri.getScheme() + // "content"
                "\n(3-2)Uri-Authority: " + uri.getAuthority() + // "com.wcl.testdemo.utilcode.fileprovider"
                "\n(3-3)Uri-Path: " + uri.getPath(); // "/external_path/DCIM/TestDemo/1677467432067_100.PNG"
        print(msg1);
    }

    //打印本地各种路径.
    private void printPath() {
        String msg0 = "(1)外存路径: " + PathUtils.getExternalStoragePath() + // "/storage/emulated/0"
                "\n(2)外存图片路径: " + PathUtils.getExternalPicturesPath() + // "/storage/emulated/0/Pictures"
                "\n(3)外存相册路径: " + PathUtils.getExternalDcimPath() + // "/storage/emulated/0/DCIM"
                "\n(4)外存视频路径: " + PathUtils.getExternalMoviesPath() + // "/storage/emulated/0/Movies"
                "\n(5)外存下载路径: " + PathUtils.getExternalDownloadsPath() + // "/storage/emulated/0/Download"
                "\n(6)外存文档路径: " + PathUtils.getExternalDocumentsPath() + // "/storage/emulated/0/Documents"
                "\n(7)外存音乐路径: " + PathUtils.getExternalMusicPath() + // "/storage/emulated/0/Music"
                "\n(8)外存铃声路径: " + PathUtils.getExternalRingtonesPath() + // "/storage/emulated/0/Ringtones"
                "\n(9)外存闹铃路径: " + PathUtils.getExternalAlarmsPath() + // "/storage/emulated/0/Alarms"
                "\n(10)外存通知路径: " + PathUtils.getExternalNotificationsPath() + // " /storage/emulated/0/Notifications"
                "\n(11)外存播客路径: " + PathUtils.getExternalPodcastsPath(); // " /storage/emulated/0/Podcasts"
        print(msg0);
    }

    //一键三连,在三个地方输出打印结果.
    private void print(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            LogUtils.d(msg);
            ToastUtils.showShort(msg);
            mTvConsole.setText(msg);
        }
    }

}