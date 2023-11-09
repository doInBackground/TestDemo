package com.wcl.testdemo.init;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.wcl.testdemo.R;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Author WCL
 * @Date 2023/11/3 14:41
 * @Version
 * @Description 程序崩溃处理界面.
 */
public class CricketActivity extends Activity {

    /**
     * Comment:启动本界面时,通过putExtra()传递崩溃信息时的key.
     */
    public static final String EXTRA_KEY_ERR_MSG = "err_msg";
    /**
     * Comment:界面展示的QQ联系方式.
     */
    private static final String QQ = "812858895";
    /**
     * Comment:崩溃信息.
     */
    private String mErrMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.d("崩溃测试: 崩溃收集界面onCreate()", "进程ID:" + Process.myPid());//进程ID:22222 //是一个新的进程,与崩溃进程不同.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cricket);
        //展示崩溃信息1:
        ((TextView) findViewById(R.id.top_msg)).setText(
                AppUtils.getAppName() + " APP遇到错误,请尝试重启程序." +
                        "\n如果多次遇到该问题，请通过以下渠道反馈: " +
                        "\n1. 在线客服: 在线客服" +
                        "\n2. QQ号: " + QQ
        );
        //展示崩溃信息2:
        mErrMsg = getIntent().getStringExtra(EXTRA_KEY_ERR_MSG);//崩溃信息.
        ((TextView) findViewById(R.id.msg)).setText(mErrMsg);
    }

    /**
     * 点击事件:复制QQ号.
     *
     * @param view 点击的控件
     */
    public void clickQQ(View view) {
        copy(QQ);
    }

    //将文字复制到剪切板.
    private void copy(String msg) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(ClipData.newPlainText(msg, msg));
            Toast.makeText(this, "已复制", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "复制失败，请手动输入查询", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击事件:重启程序.
     *
     * @param view 点击的控件
     */
    public void clickRestart(View view) {
//        reboot();
        AppUtils.relaunchApp();
    }

    //重启程序.
    @Deprecated
    private void reboot() {
        if (Build.VERSION.SDK_INT > 28) {//Android 10
            new AlertDialog.Builder(this).setTitle("提示").setMessage("由于系统限制，请稍后手动启动程序").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Process.killProcess(Process.myPid());
                }
            }).show();
        } else {
            Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getApplicationContext().getPackageName());
            PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (mgr != null) {
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); //1秒钟后重启应用.
            } else {
                Toast.makeText(this, "Can't Reboot automatically", Toast.LENGTH_SHORT).show();
            }
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * 点击事件:尝试修复.
     *
     * @param view 点击的控件
     */
    public void clickClean(View view) {
        showClearCacheDialog();
    }

    //弹出提示框,跳转到应用设置界面,让用户手动"清除数据".
    private void showClearCacheDialog() {
        new AlertDialog.Builder(this)
                .setTitle("修复提示")
                .setMessage("如果多次遇到错误，您可以尝试清除数据和缓存进行修复\n（注意，清除数据后需要重新登录）")
                .setPositiveButton("去清除数据", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri packageURI = Uri.parse("package:" + getPackageName());
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    /**
     * 点击事件:发送报告.
     *
     * @param view 点击的控件
     */
    public void clickReport(View view) {
        if (!TextUtils.isEmpty(mErrMsg)) {
            Intent sendIntent = new Intent();
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mErrMsg);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "发送错误报告:"));
        } else {
            Toast.makeText(this, "报告无有效内容!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击事件:退出程序.
     *
     * @param view 点击的控件
     */
    public void clickExit(View view) {
        finish();
        Process.killProcess(Process.myPid());
    }

//*****************************************以上为点击事件处理*****************************************

    /**
     * 注册崩溃处理器.
     *
     * @param context 上下文
     */
    public static void watchException(final Context context) {
        final Thread.UncaughtExceptionHandler def = Thread.getDefaultUncaughtExceptionHandler();//默认处理器.
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                LogUtils.d("崩溃测试: 捕获到了崩溃.", "进程ID:" + Process.myPid());//进程ID:11111
                handleException(context, e);//处理崩溃事件.
                if (def != null) {
                    LogUtils.d("崩溃测试: 默认崩溃处理器不为空.");//def is not null, but we just kill the process!
                    def.uncaughtException(t, e);//回调默认崩溃处理器.
                    // We are the last one, just kill the process.
                } else {
                    LogUtils.d("崩溃测试: 默认崩溃处理器为空.");//def is null, now kill process!
                }
//                try {
//                    LogUtils.d("崩溃测试: 准备测试睡眠2s.", "进程ID:" + Process.myPid());//进程ID:11111
//                    Thread.sleep(2000);
//                    LogUtils.d("崩溃测试: 睡眠结束,准备杀死指定ID进程.杀死后才会创建新的进程启动之前要启动的Activity.", "进程ID:" + Process.myPid());//进程ID:11111
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
                // TODO: 2023/11/3 执行到此时,之前handleException()中同步调用的startActivity()为什么没有执行?
                // TODO: 2023/11/3 为什么要等到此处[杀死进程]后,才会重新创建进程执行startActivity()?
                //[杀死进程].
//                Process.killProcess(Process.myPid());//后台崩溃时,这种方式点击APP图标重新进入APP,为什么会直接进入崩溃前的界面,不合理!
//                System.exit(0);//后台崩溃时,这种方式点击APP图标重新进入APP,为什么会直接进入崩溃前的界面,不合理!
                AppUtils.exitApp();//先finish所有Activity再结束进程.
            }
        });
    }

    //捕获到崩溃后,处理崩溃事件.
    private static void handleException(Context context, Throwable throwable) {
        String msg = dumpError(context, throwable);//获取完整的崩溃日志信息.
//        saveErrorToSDCard(context, msg);//将崩溃日志保存到本地.(工具类已有此功能,故此处暂注释不用)
        if (!foreground()) {//APP在后台:
            LogUtils.d("崩溃测试: APP在后台崩溃,不展示给用户.");
        } else if (shouldShowErrorToUser(throwable)) {//应该展示用户:
            LogUtils.d("崩溃测试: APP在前台崩溃,展示给用户.");
            startErrorActivity(context, msg);
        } else {
            LogUtils.d("崩溃测试: APP在前台崩溃,但这类崩溃,不应该展示给用户.");
        }
    }

    /**
     * 启动崩溃处理界面.
     *
     * @param context 上下文
     * @param msg     崩溃信息
     */
    public static void startErrorActivity(Context context, String msg) {
        Intent cricketIntent = new Intent(context, CricketActivity.class);
        cricketIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cricketIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        cricketIntent.putExtra(EXTRA_KEY_ERR_MSG, msg);
        context.startActivity(cricketIntent);
        LogUtils.d("崩溃测试: 准备启动崩溃处理界面.", "进程ID:" + Process.myPid());//进程ID:11111
    }

    //获取完整的崩溃日志信息.
    private static String dumpError(Context context, Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(new Date()));//时间信息.
        context = context.getApplicationContext();
        sb.append(getCrashHead(context));//崩溃日志头部信息.
        sb.append(throwableToString(throwable));//崩溃日志信息.
        return sb.toString();
    }

    //获取要添加的崩溃日志信息.
    private static String throwableToString(Throwable throwable) {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bout);
        throwable.printStackTrace(writer);
        Throwable cause = throwable.getCause();
        while (cause != null) {
            cause.printStackTrace(writer);
            cause = cause.getCause();
        }
        writer.close();
        return bout.toString();//return new String(bout.toByteArray());
    }

    //获取要添加的崩溃日志头部信息.
    private static String getCrashHead(Context context) {
        String versionName = "N/a";
        String versionCode = "N/a";
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versionCode = String.valueOf(pi.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "\n\n********************************************" +
                "\n设备厂商     : " + Build.MANUFACTURER +// 设备厂商
                "\n设备型号     : " + Build.MODEL +// 设备型号
                "\n系统版本     : " + Build.VERSION.RELEASE +// 系统版本
                "\nSDK版本号    : " + Build.VERSION.SDK_INT +// SDK版本
                "\n程序版本名    : " + versionName +
                "\n程序版本号    : " + versionCode +
                "\n运行状态     : " + (foreground() ? "前台" : "后台") +
                "\n********************************************\n\n";
    }

    //判断APP是否前台运行.
    private static boolean foreground() {
        //API方式判断是否在前台:
//        ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
//        ActivityManager.getMyMemoryState(appProcessInfo);
//        return (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE);
        //工具类方式判断是否在前台:
        return AppUtils.isAppForeground();
    }

    //根据崩溃类型,判断是否应该提醒用户.
    private static boolean shouldShowErrorToUser(Throwable t) {
        if (t == null) {
            return false;
        }
        t.printStackTrace();
        boolean show = true;
        if (t instanceof IllegalStateException) {
            String msg = t.getMessage();
            if (msg != null && msg.startsWith("Not allowed to start service Intent")) {
                show = false;
            }
        } else if (t instanceof ClassNotFoundException) {
            LogUtils.d("崩溃测试: ClassNotFoundException may caused by small:" + t.getMessage());
            show = false;
        } else if (t instanceof ClassCastException) {
            LogUtils.d("崩溃测试: ClassCastException may caused by small update:" + t.getMessage());
            show = false;
        } else if (t instanceof RuntimeException) {
            String msg = t.getMessage();
            if (msg != null && msg.startsWith("Unable to start activity ComponentInfo")) {
                show = false;
            }
        }
        return show;
    }

    //将崩溃日志保存到本地.
    @Deprecated
    private static void saveErrorToSDCard(Context context, String err) {
        BufferedWriter writer = null;
        try {
            File dir = new File(PathUtils.getExternalAppFilesPath() + File.separator + AppUtils.getAppName() + "_crash" + File.separator);
            if (dir.exists() || dir.mkdirs()) {
                String name = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                File file = new File(dir, name + ".txt");
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(err);
                writer.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("崩溃测试: Can't Save Error File:" + e.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
