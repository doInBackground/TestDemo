package com.wcl.testdemo.test.test00_javabase.test01;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.CacheDiskStaticUtils;
import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.CacheDoubleStaticUtils;
import com.blankj.utilcode.util.CacheDoubleUtils;
import com.blankj.utilcode.util.CacheMemoryStaticUtils;
import com.blankj.utilcode.util.CacheMemoryUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.wcl.testdemo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import com.wcl.testdemo.init.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/12/11 17:47
 * @Version
 * @Description 缓存测试界面
 */
public class CacheTestActivity extends BaseActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;
    /**
     * Comment: 以[外部沙箱根路径]为缓存路径的磁盘缓存对象.
     */
    private CacheDiskUtils mDiskUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_test);
        ButterKnife.bind(this);
        initCacheUtils(); //初始化:默认静态缓存工具.
        mDiskUtils = CacheDiskUtils.getInstance(Objects.requireNonNull(Utils.getApp().getExternalCacheDir().getParentFile())); //磁盘缓存对象.
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://二级缓存(将对象,缓存到内存及本地).
                saveObject();
                break;
            case R.id.tv_1://二级缓存(将对象,从缓存中取出).
                getObject();
                break;
            case R.id.tv_2://磁盘缓存测试(存JSONObject对象).
                saveJSONObject2Disk();
                break;
            case R.id.tv_3://磁盘缓存测试(取JSONObject对象).
                getJSONObjectFromDisk();
                break;
            case R.id.tv_4://
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

    //磁盘缓存测试(取JSONObject对象).
    private void getJSONObjectFromDisk() {
        JSONObject jsonConfig = mDiskUtils.getJSONObject("config"); //取值.
        print(jsonConfig.toString()); //打印.
    }

    //磁盘缓存测试(存JSONObject对象).
    private void saveJSONObject2Disk() {
        JSONObject jsonConfig = new JSONObject();
        try {
            jsonConfig.put("debug", true);
            jsonConfig.put("environment", "UAT");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mDiskUtils.put("config", jsonConfig); //存值.
        print("磁盘缓存成功,可去下面路径查看:\n" + Utils.getApp().getExternalCacheDir().getParent());
    }

    //二级缓存(将对象,从缓存中取出).
    private void getObject() {
        String appName = CacheDoubleStaticUtils.getString("APP_NAME");
        byte[] arr = CacheDoubleStaticUtils.getBytes("ARR");
        StringBuilder sb = new StringBuilder()
                .append("获取本地存储的二级缓存.\n")
                .append("\nAPP_NAME: ")
                .append(appName)
                .append("\nARR: ")
                .append(GsonUtils.toJson(arr));
        print(sb.toString());
    }

    //二级缓存(将对象,缓存到内存及本地).
    private void saveObject() {
        //缓存数据(可存储类型:byte[],String,JSONObject,JSONArray,Bitmap,Drawable,Parcelable,Serializable).
        CacheDoubleStaticUtils.put("ARR", new byte[]{1, 2, 3}); //存字节数组.
        CacheDoubleStaticUtils.put("APP_NAME", getString(R.string.app_name)); //存String.
        print("二级缓存成功,可去下面路径查看:\n" + Utils.getApp().getExternalCacheDir().getAbsolutePath());
    }

    //自定义初始化:默认静态缓存工具(所有初始化都可省略,省略时即使用默认).
    private void initCacheUtils() {
        //(1) TODO: 2023/12/11 内存缓存相关: [内存缓存工具:CacheMemoryUtils]和[静态内存缓存工具:CacheMemoryStaticUtils].
        //内存缓存工具:
        CacheMemoryUtils memoryUtils = CacheMemoryUtils.getInstance();//[内存缓存工具].
        //静态内存缓存工具:
        CacheMemoryStaticUtils.setDefaultCacheMemoryUtils(memoryUtils);//为[静态内存缓存工具]设置默认的[内存缓存工具],之后即可通过[静态内存缓存工具]的静态方法操作默认的[内存缓存工具]对应的非静态方法了.

        //(2) TODO: 2023/12/11 磁盘缓存相关: [磁盘缓存工具:CacheDiskUtils]和[静态磁盘缓存工具:CacheDiskStaticUtils].
        //磁盘缓存工具:
        CacheDiskUtils diskUtils = CacheDiskUtils.getInstance(Utils.getApp().getExternalCacheDir());//无参情况下默认缓存到内部沙箱的缓存目录.
        //静态磁盘缓存工具:
        CacheDiskStaticUtils.setDefaultCacheDiskUtils(diskUtils);//为[静态磁盘缓存工具]设置默认的[磁盘缓存工具],之后即可通过[静态磁盘缓存工具]的静态方法操作默认的[磁盘缓存工具]对应的非静态方法了.

        //(3) TODO: 2023/12/11 二级缓存相关: [二级缓存工具:CacheDoubleUtils]和[静态二级缓存工具:CacheDoubleStaticUtils].
        /*
        CacheDoubleUtils.java(二级缓存工具)中包含了CacheMemoryUtils.java(内存缓存工具)和CacheDiskUtils.java(磁盘缓存工具).
        通过"二级缓存工具"存对象时,会在"磁盘缓存工具"和"内存缓存工具"各存一份.
        通过"二级缓存工具"取对象时,会先在"内存缓存工具"中找,找到就返回该对象.找不到再在"磁盘缓存工具"中找,找到会先拷贝一份到"内存缓存工具"中再返回该对象,找不到就返回之前设置的默认值或null.
        */
        //二级缓存工具:
        CacheDoubleUtils doubleUtils = CacheDoubleUtils.getInstance(memoryUtils, diskUtils);//[二级缓存工具]:
        //静态二级缓存工具:
        CacheDoubleStaticUtils.setDefaultCacheDoubleUtils(doubleUtils);//为[静态二级缓存工具]设置默认的[二级缓存工具],之后即可通过[静态二级缓存工具]的静态方法操作默认的[二级缓存工具]对应的非静态方法了.
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