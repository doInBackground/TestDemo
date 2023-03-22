package com.wcl.testdemo.init;

import android.app.Activity;
import android.app.Application;
import android.os.Build;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.Utils;


/**
 * @Author WCL
 * @Date 2020/4/27 16:10
 * @Version 1.0
 * @Description
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (ProcessUtils.isMainProcess()) { //是主进程
            initLog(); //初始化日志工具.
            initCrash(); //初始化崩溃收集器.
            initAppStatusListener(); //注册App前后台切换监听器.
        }
    }

    //初始化日志.
    private void initLog() {
        //通用配置
        LogUtils.getConfig().setGlobalTag("WCL");//TAG不会做落地保存,是记录在运行内存中的.
//        LogUtils.getConfig().setFilePrefix("log");
        LogUtils.getConfig().setLog2FileSwitch(true);//默认开启日志落地.
        LogUtils.getConfig().setFileFilter(LogUtils.I);//控制日志落地等级.
        LogUtils.getConfig().setSaveDays(7);//设置log可保留天数.
        LogUtils.getConfig().setLogHeadSwitch(AppUtils.isAppDebug());//是否展示日志中可跳转到代码的头部信息.
        LogUtils.getConfig().setBorderSwitch(AppUtils.isAppDebug());//是否展示每条日志的边框图形.
        LogUtils.getConfig().setConsoleFilter(AppUtils.isAppDebug() ? LogUtils.V : LogUtils.I);//设置AS控制台过滤器.
        //设备信息打印.
        LogUtils.v("====================>>Application initApp<<====================",
                "device_model: " + DeviceUtils.getModel(),//设备型号.
                "device_id: " + DeviceUtils.getUniqueDeviceId(),//设备ID.
                "android_version: " + Build.VERSION.RELEASE,//Android系统版本.
                "android_sdk_version: " + Build.VERSION.SDK_INT,//Android API版本.
                "screenW:" + ScreenUtils.getScreenWidth() + " screenH:" + ScreenUtils.getScreenHeight() + " appW:" + ScreenUtils.getAppScreenWidth() + " appH:" + ScreenUtils.getAppScreenHeight(),//屏幕宽高和应用宽高.
                AppUtils.getAppInfo()//APP信息.
        );
    }

    //初始化崩溃收集器.
    private void initCrash() {
        //方式一:(手写)
//            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {//崩溃收集器1.
//                @Override
//                public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
//                    // 得到详细报错信息:
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    PrintWriter pw = new PrintWriter(baos);
//                    throwable.printStackTrace(pw);
//                    Throwable cause = throwable.getCause();
//                    while (cause != null) {
//                        cause.printStackTrace(pw);
//                        cause = cause.getCause();
//                    }
//                    pw.close();
//                    String errMsg = new String(baos.toByteArray()); //得到详细报错信息,可落地处理.
//                    // 崩溃处理: (弹出"APP崩溃提示"界面,友好的提示用户)
//                    if (AppUtils.isAppForeground()) { //APP在前台:启动"崩溃提示界面".
//                        Intent intent = new Intent(AppApplication.this, CrashActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        AppApplication.this.startActivity(intent);
//                        Process.killProcess(Process.myPid()); //杀死崩溃进程.
//                    } else { //APP在后台:退出APP.
//                        AppUtils.exitApp();
//                    }
//                }
//            });
        //方式二: (用库)
        // 正常情况下setDefaultUncaughtExceptionHandler会覆盖之前的崩溃收集器.
        // 该方式中会先保存收集器1,再覆盖,当收集器2被调用时再主动调用一次收集器1,故收集器2应放在后面初始化.
        CrashUtils.init();//崩溃收集器2.
    }

    //注册App前后台切换监听器.
    private void initAppStatusListener() {
        AppUtils.registerAppStatusChangedListener(new Utils.OnAppStatusChangedListener() {
            @Override
            public void onForeground(Activity activity) {
//                LogUtils.i("APP前台:" + activity);
            }

            @Override
            public void onBackground(Activity activity) {
//                LogUtils.i("APP后台:" + activity);
            }
        });
    }

}