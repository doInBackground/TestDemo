package com.wcl.testdemo.utils;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.RomUtils;
import com.blankj.utilcode.util.Utils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author WCL
 * @Date 2020/5/15 16:15
 * @Version 1.0
 * @Description 屏幕相关的工具类.
 */
public final class ScreenUtils {

    /**
     * 沉浸式.
     *
     * @param activity         当前界面
     * @param isHideNavigation 是否隐藏底部导航栏(一般横屏时建议隐藏)
     */
    public static void setImmersive(Activity activity, boolean isHideNavigation) {
//        Activity currentActivity = MyAppUtils.getCurrentActivity();
        Window window = activity.getWindow();
        if (Build.VERSION.SDK_INT >= 21) {//大于5.0
            View decorView = window.getDecorView();
            int option;
            if (isHideNavigation) {//无底部
                option = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            } else {//有底部
                option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            }
            decorView.setSystemUiVisibility(option);
            window.setNavigationBarColor(Color.TRANSPARENT);//导航栏透明:成功.5.0方法?
            window.setStatusBarColor(Color.TRANSPARENT);//状态栏透明:成功.5.0方法?
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//大于4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
//        BarUtils.setNavBarVisibility(activity, !isHideNavigation);//设置导航栏是否可见
//        BarUtils.setNavBarLightMode(activity, false);//设置导航栏是否为浅色模式
//        BarUtils.setNavBarColor(activity, 0x00000000);//设置导航栏颜色
    }

    /**
     * 隐藏虚拟按键，并且全屏.
     *
     * @param activity 当前界面
     */
    public static void setFullScreen(Activity activity) {
////        Activity currentActivity = MyAppUtils.getCurrentActivity();
        Window window = activity.getWindow();
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//兼容性:隐藏状态栏,设置全屏.
//        //隐藏虚拟按键，并且全屏.
//        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { //lower api.
//            View decorView = window.getDecorView();
//            decorView.setSystemUiVisibility(View.GONE);
//        } else if (Build.VERSION.SDK_INT >= 19) {//for new api versions.
//            View decorView = window.getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0刘海屏适配
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;//允许页面延伸到刘海区域.
            window.setAttributes(lp);
        }

        BarUtils.setNavBarVisibility(activity, false);//设置导航栏是否可见
        BarUtils.setNavBarLightMode(activity, false);//设置导航栏是否为浅色模式
        BarUtils.setNavBarColor(activity, 0x00000000);//设置导航栏颜色

        BarUtils.setStatusBarVisibility(activity, false);//设置状态栏是否可见
        BarUtils.setNavBarLightMode(activity, false);//设置状态栏是否为浅色模式
        BarUtils.setStatusBarColor(activity, 0x00000000);//设置状态栏颜色
        BarUtils.transparentStatusBar(activity);//透明状态栏
    }

    /**
     * 设置竖屏.
     *
     * @param activity 当前界面
     */
    public static void setOrientationPortrait(Activity activity) {
//        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置传竖屏.
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);//设置传感器上下竖屏.
    }


    /**
     * 设置横屏.
     *
     * @param activity 当前界面
     * @param mode     1:右横屏 2:左横屏 其他:传感器横屏
     */
    public static void setOrientationLandscape(Activity activity, int mode) {
        switch (mode) {
            case 1:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置右横屏.
                break;
            case 2:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);//设置左横屏.
                break;
            default:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);//设置传感器左右屏横屏(同SCREEN_ORIENTATION_USER_LANDSCAPE).
                break;
        }
    }


    /**
     * 判断是不是竖屏.
     *
     * @return 是否竖屏
     */
    public static boolean isPortraitScreen() {
        if (Utils.getApp().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取屏幕宽高.
     *
     * @return
     */
    public static int[] getScreenWidthAndHeight() {
        //屏幕宽高
//        Activity activity = MyAppUtils.getCurrentActivity();
        Activity activity = ActivityUtils.getTopActivity();
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(metrics);
        } else {
            display.getMetrics(metrics);
        }
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }

    /**
     * 获取状态栏的高度.
     *
     * @return 状态栏的高度.
     */
    public static int getStatusBarHeight() {
        Resources resources = Utils.getApp().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resourceId > 0 ? resources.getDimensionPixelSize(resourceId) : 0;//状态栏高度
        return height;
    }

//    /**
//     * 获取底部虚拟导航栏的高度.(注释勿删)
//     *
//     * @return 部虚拟导航栏的高度.
//     */
//    public static int getNavigationBarHeight() {
//        int navigationBarHeight = 0;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0才有官方刘海屏适配.
//            if (isLiuhai() > 0) {//此处认为刘海屏没有导航栏.
//                return 0;
//            }
//        }
//        Context app = MyAppUtils.getApp();
//        WindowManager windowManager = (WindowManager) app.getSystemService(Context.WINDOW_SERVICE);
//        Display display = windowManager.getDefaultDisplay();
//        DisplayMetrics metrics = new DisplayMetrics();
//        try {
//            @SuppressWarnings("rawtypes")
//            Class displayClass = Class.forName("android.view.Display");
//            @SuppressWarnings("unchecked")
//            Method method = displayClass.getMethod("getRealMetrics", DisplayMetrics.class);//Build.VERSION_CODES.JELLY_BEAN_MR1才有的方法.
//            method.invoke(display, metrics);//执行获取屏幕真实尺寸的方法.
//            if (app.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {//竖屏
//                navigationBarHeight = metrics.heightPixels - display.getHeight();//display.getHeight()是没出虚拟键以前的方法,获取的是不包含底部虚拟导航的高度.
//            } else {//横屏
//                navigationBarHeight = metrics.widthPixels - display.getWidth();//display.getHeight()是没出虚拟键以前的方法,获取的是不包含底部虚拟导航的宽度.
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return navigationBarHeight;
//    }


    /**
     * 获取沉浸式或全屏状态下,屏幕安全区域距离屏幕四边的距离.
     *
     * @return 左上右下四边, 安全区域依次距离屏幕四边的距离.
     */
    public static int[] getSafeArea() {
//        int[] canUse;//可用区域坐标:左上坐标+右下坐标
        int[] safe = new int[4];//安全区域距离屏幕四边的距离
        safe[0] = 0;
        safe[1] = 0;
        safe[2] = 0;
        safe[3] = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0才有官方刘海屏适配方案,此处认为9.0一下没有刘海屏.
//            Activity activity = MyAppUtils.getCurrentActivity();
            Activity activity = ActivityUtils.getTopActivity();
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            WindowInsets rootWindowInsets = decorView.getRootWindowInsets();
            // TODO: 2020/6/4  获取安全区域的方法,再确认一下
            if (rootWindowInsets != null) {
                DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
                if (displayCutout != null) {
                    safe[0] = displayCutout.getSafeInsetLeft();//安全区域距离屏幕左边的距离
                    safe[1] = displayCutout.getSafeInsetTop();//安全区域距离屏幕顶部的距离
                    safe[2] = displayCutout.getSafeInsetRight();//安全区域距离屏幕右部的距离
                    safe[3] = displayCutout.getSafeInsetBottom();//安全区域距离屏幕底部的距离
                    return safe;
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0各个机型单独适配
            try {
//                Activity activity = MyAppUtils.getCurrentActivity();
                Activity activity = ActivityUtils.getTopActivity();
                if (RomUtils.isXiaomi()) {//小米
                    Resources resources = activity.getResources();
                    int resourceIdHeight = resources.getIdentifier("notch_height", "dimen", "android");
                    int notchHeight = resourceIdHeight > 0 ? resources.getDimensionPixelSize(resourceIdHeight) : 0;//刘海高度
                    int resourceIdWidth = resources.getIdentifier("notch_width", "dimen", "android");
                    int notchWidth = resourceIdWidth > 0 ? resources.getDimensionPixelSize(resourceIdWidth) : 0;//刘海宽度
                    if (notchHeight > 0 && notchWidth > 0) {//有刘海
                        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
                            case Surface.ROTATION_0:
                                safe[1] = notchHeight;
                                return safe;
                            case Surface.ROTATION_90:
                                safe[0] = notchHeight;//此处用刘海宽还是高,待真机验证.
                                return safe;
                            case Surface.ROTATION_180://小米此种手机不能180度展示,故不可能进入此分支.
                                safe[3] = notchHeight;
                                return safe;
                            case Surface.ROTATION_270:
                                safe[2] = notchHeight;//此处用刘海宽还是高,待真机验证.
                                return safe;
                        }
                    }
                } else if (RomUtils.isVivo()) {//VIVO
                    Method method = Class.forName("android.util.FtFeature").getMethod("isFeatureSupport", int.class);
                    boolean hasLH = (boolean) method.invoke(null, 0x00000020);//0x00000020表示查询是否有凹槽
                    if (hasLH) {
                        int statusBarHeight = BarUtils.getStatusBarHeight();//状态栏高度(>=刘海高度)
                        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
                            case Surface.ROTATION_0:
                                safe[1] = statusBarHeight;
                                return safe;
                            case Surface.ROTATION_90:
                                safe[0] = statusBarHeight;
                                return safe;
                            case Surface.ROTATION_180:
                                safe[3] = statusBarHeight;
                                return safe;
                            case Surface.ROTATION_270:
                                safe[2] = statusBarHeight;
                                return safe;
                        }
                    }
                } else if (RomUtils.isOppo()) {//OPPO
                    //返回true为凹形屏,可识别OPPO的手机是否为凹形屏.
                    boolean hasLH = activity.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
                    if (hasLH) {
                        switch (activity.getWindowManager().getDefaultDisplay().getRotation()) {
                            case Surface.ROTATION_0:
                                safe[1] = 80;
                                return safe;
                            case Surface.ROTATION_90:
                                safe[0] = 80;
                                return safe;
                            case Surface.ROTATION_180:
                                safe[3] = 80;
                                return safe;
                            case Surface.ROTATION_270:
                                safe[2] = 80;
                                return safe;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {//8.0以下无刘海屏

        }
        return safe;
    }


    /**
     * 获取刘海块的数量.
     *
     * @return
     */
    public static int isLiuhai() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//9.0才有官方刘海屏适配
//            Activity activity = MyAppUtils.getCurrentActivity();
            Activity activity = ActivityUtils.getTopActivity();
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            WindowInsets rootWindowInsets = decorView.getRootWindowInsets();
            if (rootWindowInsets == null) {
                return 0;
            }
            DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
            List<Rect> rectList = displayCutout.getBoundingRects();
            if (rectList == null || rectList.size() == 0) {
                return 0;
            } else {
                return rectList.size();
            }
        } else {//9.0以下国产机有刘海屏,但是没有官方适配标准,需要单独适配.(此处暂看作9.0下无刘海屏)
            return 0;
        }
    }

    /**
     * 获取当前屏幕旋转角度
     *
     * @param activity
     * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
     */
    public static int getDisplayRotation(Activity activity) {
        if (activity == null)
            return 0;
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
        }
        return 0;
    }

}
