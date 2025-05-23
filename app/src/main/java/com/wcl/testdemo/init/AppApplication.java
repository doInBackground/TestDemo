package com.wcl.testdemo.init;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Process;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.Utils;
import com.wcl.testdemo.utils.JsonConfig;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import cn.jiguang.api.utils.JCollectionAuth;
import cn.jpush.android.api.JPushInterface;


/**
 * @Author WCL
 * @Date 2020/4/27 16:10
 * @Version 1.0
 * @Description Application
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initLog(); //初始化日志工具.
        if (ProcessUtils.isMainProcess()) { //是主进程
            initCrash(); //初始化崩溃收集器.
            initAppStatusListener(); //注册App前后台切换监听器.
            initActivityLifecycleListener();//注册Activity生命周期监听器.
            initJPush();//初始化JPush.
        }
    }

    //初始化日志.
    private void initLog() {
        boolean isDebug = AppUtils.isAppDebug();//是否调试日志.
        JSONObject config = JsonConfig.getInstance().getConfig();//配置文件内容.
        try {
            isDebug = config.getBoolean(JsonConfig.JsonConfigKey.DEBUG);
        } catch (JSONException e) {
//            e.printStackTrace();
        }
        //通用配置
        LogUtils.getConfig().setGlobalTag(ProcessUtils.isMainProcess() ? "WEI-MP" : ("WEI-SP-" + Process.myPid()));//TAG不会做落地保存,是记录在运行内存中的.
//        LogUtils.getConfig().setFilePrefix("log");//设置 log 文件前缀.
        LogUtils.getConfig().setLog2FileSwitch(true);//默认开启日志落地.
        LogUtils.getConfig().setFileFilter(isDebug ? LogUtils.V : LogUtils.I);//控制日志落地等级.
        LogUtils.getConfig().setSaveDays(7);//设置log可保留天数.
        LogUtils.getConfig().setLogHeadSwitch(isDebug);//是否展示日志中可跳转到代码的头部信息.
        LogUtils.getConfig().setBorderSwitch(isDebug);//是否展示每条日志的边框图形.
        LogUtils.getConfig().setConsoleFilter(isDebug ? LogUtils.V : LogUtils.I);//设置AS控制台过滤器.
        //设备信息打印.
        LogUtils.i("====================>>Application onCreate("
                        + (ProcessUtils.isMainProcess() ? "主进程" : "子进程")
                        + "["
                        + ProcessUtils.getCurrentProcessName()
                        + "]"
                        + "["
                        + Process.myPid()
                        + "]"
                        + ")<<====================",
                "debug_or_release: " + (AppUtils.isAppDebug() ? "Debug" : "Release"),//APK是否为Debug包.
                "device_model: " + DeviceUtils.getModel(),//设备型号.
                "device_id: " + DeviceUtils.getUniqueDeviceId(),//设备ID.
                "android_version: " + Build.VERSION.RELEASE,//Android系统版本.
                "android_sdk_version: " + Build.VERSION.SDK_INT,//Android API版本.
                "screenW: " + ScreenUtils.getScreenWidth() + " | screenH: " + ScreenUtils.getScreenHeight() + " | appW: " + ScreenUtils.getAppScreenWidth() + " | appH: " + ScreenUtils.getAppScreenHeight(),//屏幕宽高和应用宽高.
                "app_info: " + AppUtils.getAppInfo(),//APP信息.
                "config: " + config //配置文件内容(应为Json格式).
        );
    }

    //初始化崩溃收集器.
    private void initCrash() {
        //方式1:(手写)
//            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {//崩溃收集器.
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
        //方式2:(手写)
        CricketActivity.watchException(this);//这种处理方式中的崩溃收集器会结束进程,故放在前面,让后面的收集器覆盖它.
        //方式3:(用库)
        // 正常情况下setDefaultUncaughtExceptionHandler会覆盖之前的崩溃收集器.
        // 该方式中会先保存[旧收集器],再用[新收集器]覆盖,当[新收集器]被调用时,先处理完[新收集器]自己的事情后,会再主动调用[旧收集器],故[新收集器]应放在后面初始化.
        CrashUtils.init();//[新崩溃收集器].
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

    //初始化JPush.
    private void initJPush() {
        //JPush SDK 提供的 API 接口，都主要集中在cn.jpush.android.api.JPushInterface类里。
        boolean isPrivacyReady = true; //APP根据是否已弹窗获取隐私授权来赋值.
        if (isPrivacyReady) {
            //初始化SDK.
            JPushInterface.setDebugMode(true);
            JCollectionAuth.setAuth(this, true); //调整点一:初始化代码前增加setAuth调用.
            JPushInterface.init(this);//初始化极光推送.
        } else {
            JCollectionAuth.setAuth(this, false); //后续初始化过程将被拦截.
        }
    }

}