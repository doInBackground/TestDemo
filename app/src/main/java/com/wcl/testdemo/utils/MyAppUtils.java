package com.wcl.testdemo.utils;

import android.os.Build;
import android.os.Process;

/**
 * @Author WCL
 * @Date 2020/4/27 13:13
 * @Version 1.0
 * @Description APP工具类.
 */
public final class MyAppUtils {

    /**
     * 判断是否64位系统.
     *
     * @return 是否64位系统
     */
    public static boolean is64Bit() {
        boolean is64;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0(23)及以上
            is64 = Process.is64Bit();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0(21)及以上
            //方式1:
            String arch = System.getProperty("os.arch");
            if (arch != null && arch.contains("64")) {
                is64 = true;
            } else {
                is64 = false;
            }
//            //方式2:
//            try {
//                Method method = ClassLoader.class.getDeclaredMethod("findLibrary", String.class);
//                Object object = method.invoke(context.getClassLoader(), "art");
//                if (object != null) {
//                    is64 = ((String) object).contains("lib64");
//                } else {
//                    is64 = false;
//                }
//            } catch (Exception e) {
//                is64 = false;
//                e.printStackTrace();
//            }
        } else {//5.0以下只支持32位
            is64 = false;
        }
        return is64;
    }

}
