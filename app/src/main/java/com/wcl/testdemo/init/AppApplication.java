package com.wcl.testdemo.init;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.text.TextUtils;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import androidx.annotation.NonNull;


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
            initActivityLifecycleListener();//注册Activity生命周期监听器.
        }
    }

    //初始化日志.
    private void initLog() {
        boolean isDebugLog = AppUtils.isAppDebug();//是否调试日志.
        //读取配置文件.
//        String configRootPath = PathUtils.getInternalAppDataPath();//APP内部沙箱根路径(配置文件的更改方式,如果是"按钮程序"修改,则配置文件放在内部沙箱(安全)).
        String configRootPath = PathUtils.getExternalAppDataPath();//APP外部沙箱根路径(配置文件的更改方式,如果是"手动"修改,则配置文件放在外部沙箱(方便)).
        String configJson = FileIOUtils.readFile2String(new File(configRootPath, "config"));//读取配置文件.//写入用FileIOUtils.writeFileFromString();
        if (!TextUtils.isEmpty(configJson)) {
            try {
                JSONObject config = new JSONObject(configJson);
                configJson = "(format OK) " + configJson;
                isDebugLog = config.getBoolean("debug");
            } catch (JSONException e) {
                configJson = "(format NO) " + configJson;
//                e.printStackTrace();
            }
        }
        //通用配置
        LogUtils.getConfig().setGlobalTag("WEI");//TAG不会做落地保存,是记录在运行内存中的.
//        LogUtils.getConfig().setFilePrefix("log");//设置 log 文件前缀.
        LogUtils.getConfig().setLog2FileSwitch(true);//默认开启日志落地.
        LogUtils.getConfig().setFileFilter(isDebugLog ? LogUtils.V : LogUtils.I);//控制日志落地等级.
        LogUtils.getConfig().setSaveDays(7);//设置log可保留天数.
        LogUtils.getConfig().setLogHeadSwitch(isDebugLog);//是否展示日志中可跳转到代码的头部信息.
        LogUtils.getConfig().setBorderSwitch(isDebugLog);//是否展示每条日志的边框图形.
        LogUtils.getConfig().setConsoleFilter(isDebugLog ? LogUtils.V : LogUtils.I);//设置AS控制台过滤器.
        //设备信息打印.
        LogUtils.i("====================>>Application initApp<<====================",
                "debug_or_release: " + (AppUtils.isAppDebug() ? "Debug" : "Release"),//APK是否为Debug包.
                "device_model: " + DeviceUtils.getModel(),//设备型号.
                "device_id: " + DeviceUtils.getUniqueDeviceId(),//设备ID.
                "android_version: " + Build.VERSION.RELEASE,//Android系统版本.
                "android_sdk_version: " + Build.VERSION.SDK_INT,//Android API版本.
                "screenW: " + ScreenUtils.getScreenWidth() + " | screenH: " + ScreenUtils.getScreenHeight() + " | appW: " + ScreenUtils.getAppScreenWidth() + " | appH: " + ScreenUtils.getAppScreenHeight(),//屏幕宽高和应用宽高.
                "app_info: " + AppUtils.getAppInfo(),//APP信息.
                "config: " + configJson//配置文件内容(应为Json格式).
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

    //注册Activity生命周期监听器.
    private void initActivityLifecycleListener() {
        ActivityUtils.addActivityLifecycleCallbacks(new Utils.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                super.onActivityResumed(activity);
//                LogUtils.i("Activity-Resumed:" + activity.getLocalClassName());
            }
        });
    }

}