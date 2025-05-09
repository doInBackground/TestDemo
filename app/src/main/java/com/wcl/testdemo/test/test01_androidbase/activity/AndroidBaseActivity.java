package com.wcl.testdemo.test.test01_androidbase.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NotificationUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.SPStaticUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.UtilsTransActivity;
import com.wcl.testdemo.R;
import com.wcl.testdemo.constant.SPKeys;
import com.wcl.testdemo.init.TestActivity;
import com.wcl.testdemo.test.test01_androidbase.test03.SaveFileActivity;
import com.wcl.testdemo.test.test01_androidbase.test08.DialogTestActivity;
import com.wcl.testdemo.utils.FileUtils;
import com.wcl.testdemo.utils.dialog.MyDialogFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import com.wcl.testdemo.init.BaseActivity;

import androidx.core.app.NotificationCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * @Author WCL
 * @Date 2023/3/24 15:04
 * @Version
 * @Description 测试: Android基础.
 */
public class AndroidBaseActivity extends BaseActivity {

    /**
     * Comment: 请求码:通过系统文件选择器,选择文件的结果.
     */
    private final int REQUEST_CODE_FOR_PICK_FILE = 100;
    /**
     * Comment: 请求码:请求[管理所有文件]权限.
     */
    private final int REQUEST_CODE_FOR_SD_PERMISSION = 101;
    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_base);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_FOR_PICK_FILE://文件选择器结果:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        StringBuilder sb = new StringBuilder();
                        if (uri != null) {//(1)选了单个文件:
//                            File tempFile = UriUtils.uri2File(uri);//该工具方法拿不到真实File时,会拷贝一份文件到内部沙箱缓存,再给出拷贝后的路径.
                            File file = FileUtils.copyUri2File(uri,
                                    new File(Utils.getApp().getExternalCacheDir(), "" + System.currentTimeMillis()));
                            sb.append("[")
                                    .append("uri = ")
                                    .append(uri)
                                    .append(", uri-path = ")
                                    .append(uri.getPath())
                                    .append(", 拷贝到: ")
                                    .append(file.getAbsolutePath())
                                    .append("]");
                            print(sb.toString());
                        } else {//(2)选了多个文件:
                            ClipData clipData = data.getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri multipleUri = clipData.getItemAt(i).getUri();
                                File file = FileUtils.copyUri2File(multipleUri,
                                        new File(Utils.getApp().getExternalCacheDir(), "" + System.currentTimeMillis()));
                                sb.append("[")
                                        .append(i)
                                        .append("号, uri = ")
                                        .append(multipleUri)
                                        .append(", uri-path = ")
                                        .append(multipleUri.getPath())
                                        .append(", 拷贝到: ")
                                        .append(file.getAbsolutePath())
                                        .append("]\n");
                            }
                            print(sb.toString());
                        }
                    } else {
                        print("文件选择器回传Intent(data)为空");
                    }
                }
                break;
            case REQUEST_CODE_FOR_SD_PERMISSION://请求[管理所有文件]权限的结果:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && (!Environment.isExternalStorageManager())) {
                    print("[管理所有文件]权限: 用户拒绝.");
                } else {
                    print("[管理所有文件]权限: 用户同意,高版本(30)亦可在SD卡上执行File操作.");
                }
                break;
        }
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://打开系统文件选择器.
                pickFile();
                break;
            case R.id.tv_1://请求[存储]和[定位]权限.
                requestPermission();
                break;
            case R.id.tv_2://请求[管理所有文件]权限.
                requestManageExternalStoragePermission();
                break;
            case R.id.tv_3://文件的持久化保存.
                startActivity(new Intent(this, SaveFileActivity.class));
                break;
            case R.id.tv_4://通过浏览器打开链接.
                openUrlByBrowser();
                break;
            case R.id.tv_5://5s后发送通知.
                testUtilsNotification();
                break;
            case R.id.tv_6://取消所有通知.
                NotificationUtils.cancelAll();
                break;
            case R.id.tv_7://延时崩溃测试.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d("崩溃测试: 程序准备崩溃",
                                "当前进程ID:" + Process.myPid(),
                                "当前进程名:" + ProcessUtils.getCurrentProcessName(),
                                "前台进程名:" + ProcessUtils.getForegroundProcessName(),
                                "后台进程名:" + CollectionUtils.toString(ProcessUtils.getAllBackgroundProcesses())
                        );
//                        //测试直接启动崩溃处理界面,是处于同一进程的:
//                        CricketActivity.startErrorActivity(AndroidBaseActivity.this, "模拟崩溃信息.");
                        //手动制造崩溃:
                        new ArrayList<>().get(0);
                    }
                }, 2000);
                break;
            case R.id.tv_8://Dialog测试.
                startActivity(new Intent(this, DialogTestActivity.class));
                break;
            case R.id.tv_9://切换皮肤主题.
                String themesType = SPStaticUtils.getString(SPKeys.THEMES_TYPE);
                if (TextUtils.equals(themesType, "WHITE") || TextUtils.isEmpty(themesType)) {//默认主题.
                    SPStaticUtils.put(SPKeys.THEMES_TYPE, "BLACK");
                    ToastUtils.showShort("切换为[黑色]主题,重启生效");
                } else if (TextUtils.equals(themesType, "BLACK")) {
                    SPStaticUtils.put(SPKeys.THEMES_TYPE, "WHITE");
                    ToastUtils.showShort("切换为[白色]主题,重启生效");
                }
                break;
            case R.id.tv_10://
                break;
            case R.id.tv_11://
                break;
            case R.id.tv_12://
                break;
            case R.id.tv_13://
                break;
            case R.id.tv_14://
                break;
            case R.id.tv_15://
                break;
            case R.id.tv_16://
                break;
            case R.id.tv_17://
                break;
            case R.id.tv_18://
                break;
            case R.id.tv_19://
                break;
            case R.id.tv_20://
                break;
        }
    }

    //测试:5s后发送通知(状态栏通知|顶部横幅通知|锁屏通知).
    private void testUtilsNotification() {
        if (NotificationUtils.areNotificationsEnabled()) {//有通知权限.
            LogUtils.d("通知测试:有通知权限");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    LogUtils.d("通知测试:定时任务被触发");
                    ToastUtils.showLong("通知测试:通知已发送\n如未收到'横幅通知'和'锁屏通知',请查前往'系统设置'查看通知权限是否授予完整");
                    //测试自己写的Notification通知.
//                    testMyNotification();
                    //测试工具类的Notification通知.
                    NotificationUtils.notify(
                            //参数1(id):表示通知ID(每个通知的ID不同,相同ID会覆盖之前的通知来显示).
                            (int) System.currentTimeMillis(),
                            //参数2(channelConfig):表示通知渠道的配置.
                            new NotificationUtils.ChannelConfig(
                                    "ChannelId",//渠道ID:取消通知时,可通过该ID进行特定取消.
                                    "ChannelName-重要通知",//渠道名:用户在系统设置界面,手动授予通知权限时,显示该渠道的渠道名(用户可针对不同渠道名的通知,进行特定设置,如:是否锁屏展示,是否顶部横幅展示).
                                    NotificationUtils.IMPORTANCE_HIGH//重要性:可选值有IMPORTANCE_MIN|IMPORTANCE_LOW|IMPORTANCE_DEFAULT|IMPORTANCE_HIGH|IMPORTANCE_MAX.
                            ),
                            //参数3(consumer):工具类的Utils.Consumer,其中用来对NotificationCompat.Builder进行处理.
                            new Utils.Consumer<NotificationCompat.Builder>() {
                                @Override
                                public void accept(NotificationCompat.Builder builder) {
                                    //构建跳转Intent.
                                    PendingIntent pendingIntent;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        //以S+为目标（版本31及以上）要求在创建PendingIntent时指定FLAG_IMUTABLE或FLAG_MUTABLE之一。
                                        pendingIntent = PendingIntent.getActivity(AndroidBaseActivity.this, 0, new Intent(AndroidBaseActivity.this, TestActivity.class), PendingIntent.FLAG_IMMUTABLE);
                                    } else {
                                        //pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
                                        pendingIntent = PendingIntent.getActivity(AndroidBaseActivity.this, 0, new Intent(AndroidBaseActivity.this, TestActivity.class), PendingIntent.FLAG_ONE_SHOT);
                                    }
                                    builder.setSmallIcon(R.mipmap.ic_launcher) //小图标(必须提供):设置状态栏内的小图标.
                                            .setWhen(System.currentTimeMillis())  //时间戳(系统提供):可以使用setWhen()替换它或者使用setShowWhen(false)隐藏它.
                                            .setLargeIcon(BitmapFactory.decodeResource(Utils.getApp().getResources(), R.mipmap.ic_launcher)) //大图标(可选内容):设置下拉列表中的大图标,通常仅用于联系人照片,请勿将其用于应用图标.
                                            .setContentTitle("通知标题") //标题(可选内容).
                                            .setContentText("通知内容...") //文本(可选内容).
                                            .setContentIntent(pendingIntent)//点击通知后,跳转到哪.
                                            .setAutoCancel(true);  //点击通知后,消失(或调用notificationManager中的cancel方法).
                                }
                            }
                    );
                }
            }, 5000);
        } else {//无通知权限.
            LogUtils.w("通知测试:无通知权限!");
            PermissionUtils.launchAppDetailsSettings();//打开应用具体设置.
        }
    }

    //测试自己写的Notification通知.
    private void testMyNotification() {
        //1.创建通知管理器
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //2.创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //参数1(id):表示渠道ID,创建Notification时需要传入该渠道ID,才能正确应用.
            //参数2(name):表示渠道名,用户在系统设置界面,手动授予通知权限时,显示该渠道的渠道名(用户可针对不同渠道名的通知,进行特定设置,如:是否锁屏展示,是否顶部横幅展示).
            //参数3(importance):重要性(IMPORTANCE_MIN|IMPORTANCE_LOW|IMPORTANCE_DEFAULT|IMPORTANCE_HIGH|IMPORTANCE_MAX).
            NotificationChannel channel = new NotificationChannel("ChannelId", "ChannelName-重要通知", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        //构建跳转Intent.
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            //以S+为目标（版本31及以上）要求在创建PendingIntent时指定FLAG_IMUTABLE或FLAG_MUTABLE之一。
            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, TestActivity.class), PendingIntent.FLAG_IMMUTABLE);
        } else {
            //pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, TestActivity.class), PendingIntent.FLAG_ONE_SHOT);
        }

        //3.创建通知
        Notification notification = new NotificationCompat.Builder(this, "ChannelId")//参数2(channelId)需要与上面对应.
                .setSmallIcon(R.mipmap.ic_launcher) //小图标(必须提供):设置状态栏内的小图标.
                .setWhen(System.currentTimeMillis())  //时间戳(系统提供):可以使用setWhen()替换它或者使用setShowWhen(false)隐藏它.
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) //大图标(可选内容):设置下拉列表中的大图标,通常仅用于联系人照片,请勿将其用于应用图标.
                .setContentTitle("通知标题") //标题(可选内容).
                .setContentText("通知内容...") //文本(可选内容).
                .setAutoCancel(true) //点击通知后,消失(或调用notificationManager中的cancel方法).
                .setContentIntent(pendingIntent) //点击通知后,跳转到哪.
                .build();
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认通知音.

        //4.使用
        notificationManager.notify(1, notification);//每个通知的id不同.
    }

    //通过浏览器打开链接.
    private void openUrlByBrowser() {
        Intent intent10 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));//http://www.baidu.com
        intent10.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent10);
    }

    //请求[管理所有文件]权限.
    private void requestManageExternalStoragePermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
            print("已获[管理所有文件]权限");
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)//设置标题的图片.
                    .setTitle("权限申请")//设置对话框的标题.
                    .setMessage("本程序需要您同意允许访问所有文件权限")//设置对话框的内容.
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {//积极键.
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivityForResult(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION), REQUEST_CODE_FOR_SD_PERMISSION);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {//消极键.
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            print("[管理所有文件]权限被拒,存储受限");
                        }
                    })
                    .setNeutralButton("按钮", new DialogInterface.OnClickListener() {//中立键.
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setCancelable(false)//设置点击返回和外部不取消.
                    .show();
        }
    }

    //请求[存储]和[定位]权限.
    private void requestPermission() {
        PermissionUtils.permission(PermissionConstants.STORAGE, PermissionConstants.LOCATION)
                .explain(new PermissionUtils.OnExplainListener() {
                    @Override
                    public void explain(@NonNull UtilsTransActivity activity, @NonNull List<String> denied, @NonNull ShouldRequest shouldRequest) {
                        StringBuilder sb = new StringBuilder("\"XXX功能\"")//需依具体情况改变.
                                .append("需要使用到以下权限:\r\n")
                                .append("[存储权限][定位权限]")//需依具体情况改变.
                                .append("\r\n接下来我们即将向您申请这些权限,请您同意以提升您的使用体验.");
                        new MyDialogFragment().initDialog(sb, shouldRequest).show(activity);
                    }
                })
//                .rationale(new PermissionUtils.OnRationaleListener() {//回调时机:第二次及以后被拒.
//                    @Override
//                    public void rationale(@NonNull UtilsTransActivity activity, @NonNull ShouldRequest shouldRequest) {
//                        ToastUtils.showShort("权限又被拒!");
//                        LogUtils.d("权限又被拒!");
//                    }
//                })
                .callback(new PermissionUtils.SingleCallback() {
                    @Override
                    public void callback(boolean isAllGranted, @NonNull List<String> granted, @NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        // isAllGranted:权限是否全部通过.
                        // granted:同意的权限.
                        // deniedForever(权限被永远拒绝了才有内容):被永远拒绝的权限.
                        // denied(只要有被拒绝的权限就有内容):被拒绝的权限.
                        StringBuilder sb = new StringBuilder("权限测试:")
                                .append("\n(1)权限是否全部授予: ")
                                .append(isAllGranted)
                                .append("\n(2)同意的权限: ").append(GsonUtils.toJson(granted))
                                .append("\n(3)默认拒绝的权限: ").append(GsonUtils.toJson(deniedForever))
                                .append("\n(4)本次拒绝的所有权限: ").append(GsonUtils.toJson(denied));
                        print(sb.toString());
                        if (isAllGranted) {//权限全部同意.
//                            ToastUtils.showShort("权限通过");
                        } else {
                            if (deniedForever.size() > 0) {//表示用户点击了"拒绝且不再询问",这时需要跳转设置页允许权限.
//                                ToastUtils.showShort("权限被默认拒绝,功能受限");
                                setPermissionDialog();
                            } else {//表示用户仅点击了"拒绝",不要再弹窗了以免引起用户反感.
//                                ToastUtils.showShort("权限被拒,功能受限");
                            }
                        }
                    }
                })
                .request();
    }

    //前往"设置"界面开启权限的提示.
    private void setPermissionDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)//标题图标.
                .setTitle("权限异常")//标题.
                .setMessage("我们检测到相关权限被您默认拒绝,如确需使用该功能,请前往\"设置\"给予应用相关权限")//内容.
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//积极键.
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

    //打开系统文件选择器.
    private void pickFile() {
        //action和category都是固定不变的.type属性可以用于对文件类型进行过滤,type属性必须要指定，否则会产生崩溃。
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//是否支持多选文件.
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_CODE_FOR_PICK_FILE);
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