package com.wcl.testdemo.utils;

import android.text.TextUtils;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.PathUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * @Author WCL
 * @Date 2023/8/24 10:08
 * @Version
 * @Description 沙箱配置文件的工具类.
 */
public final class JsonConfig {

    /**
     * Comment:饿汉式单例,线程安全.
     */
    private static final JsonConfig instance = new JsonConfig();
    /**
     * Comment:沙箱配置文件的File对象.
     * PathUtils.getExternalAppDataPath():APP外部沙箱根路径(配置文件的更改方式,如果是"手动"修改,则配置文件放在外部沙箱(方便)).
     * PathUtils.getInternalAppDataPath():APP内部沙箱根路径(配置文件的更改方式,如果是"按钮程序"修改,则配置文件放在内部沙箱(安全)).
     */
    private final File CONFIG_FILE = new File(PathUtils.getExternalAppDataPath(), "config");
    /**
     * Comment:沙箱内配置文件内容的JSONObject对象.
     */
    private JSONObject mConfig;

    //私有化构造.
    private JsonConfig() {
        String configStr = FileIOUtils.readFile2String(CONFIG_FILE);//读取配置文件.
        if (!TextUtils.isEmpty(configStr)) {
            try {
                mConfig = new JSONObject(configStr);
                return;
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
        mConfig = new JSONObject();
    }

    /**
     * 获取本类单例对象.
     *
     * @return 本类单例
     */
    public static JsonConfig getInstance() {
        return instance;
    }

    /**
     * 获取沙箱内的配置文件内容.
     *
     * @return 配置文件(可进行get, put操作)
     */
    public JSONObject getConfig() {
        return mConfig;
    }

    /**
     * 根据内存中最新的配置信息,更新本地沙箱的配置文件.
     *
     * @return 是否更新成功
     */
    public boolean updateConfig() {
        return FileIOUtils.writeFileFromString(CONFIG_FILE, mConfig.toString());
    }

    /**
     * @Author WCL
     * @Date 2023/8/24 10:24
     * @Version
     * @Description 配置文件Key.
     * 如为安全考虑,可将key或value的明文改为密文.
     */
    public interface JsonConfigKey {

        /**
         * Comment:是否Debug模式.
         * 可选值:
         * [true:调试模式(所有日志会落地)]
         * [false:生产模式(只有info级别及以上日志才会落地)]
         */
        String DEBUG = "debug";

        /**
         * Comment:网络环境模式.
         * 可选值:
         * ["IDC":生产环境]
         * ["INSTITUTE_TEST":研究院测试环境]
         * ["TEST":测试环境]
         * ["DEVELOP":开发环境]
         * ["PRE":预生产环境]
         */
        String ENVIRONMENT = "environment";

    }

}
