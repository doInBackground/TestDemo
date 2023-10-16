package com.wcl.testdemo.test.test00_javabase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.CacheDiskUtils;
import com.blankj.utilcode.util.CacheDoubleStaticUtils;
import com.blankj.utilcode.util.CacheDoubleUtils;
import com.blankj.utilcode.util.CacheMemoryUtils;
import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.test.test00_javabase.test02.SocketTestActivity;
import com.wcl.testdemo.utils.MyTestNativeUtils;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/3/24 15:03
 * @Version
 * @Description 测试: Java基础.
 */
public class JavaBaseActivity extends AppCompatActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;
    private MyTestNativeUtils mMyTestNativeUtils;
    /**
     * Comment:加密前-原始数据.
     */
    private final String mDataAES = "wcl123456789";
    /**
     * Comment:密钥KEY(必须32位).
     * 数字字母:1位.
     * 汉字:3位.
     */
    private final String mKyeAES = "wei01234567899876543210123456789";
    /**
     * Comment:加密后-加密数据.
     */
    private String mResAES = "05D550D8DFBBF14B32247618F7268A2A";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_base);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://二级缓存(将对象,缓存到内存及本地)
                saveObject();
                break;
            case R.id.tv_1://二级缓存(将对象,从缓存中取出)
                getObject();
                break;
            case R.id.tv_2://Socket测试.
                startActivity(new Intent(this, SocketTestActivity.class));
                break;
            case R.id.tv_3:////JNI调用测试.
                //初始化.
                if (mMyTestNativeUtils == null) {
                    mMyTestNativeUtils = new MyTestNativeUtils();
                }
                //测试1:
                String str = mMyTestNativeUtils.stringFromJNI();
                print(str);
                //测试2:
                mMyTestNativeUtils.testLog();
                //测试3:
                mMyTestNativeUtils.logD("从Java层传入字符串,并从Native层打印.");
                //测试4:
                mMyTestNativeUtils.test();
                break;
            case R.id.tv_4://加密.
                mResAES = EncryptUtils.encryptAES2HexString(mDataAES.getBytes(), mKyeAES.getBytes(), "AES/ECB/PKCS5Padding", null);
                print(mDataAES + " 加密后结果: " + mResAES);
                break;
            case R.id.tv_5://解密.
                byte[] bytes = EncryptUtils.decryptHexStringAES(mResAES, mKyeAES.getBytes(), "AES/ECB/PKCS5Padding", null);
                print(mResAES + " 解密后结果: " + new String(bytes));
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

    //二级缓存.
    private void getObject() {
        String appName = CacheDoubleStaticUtils.getString("APP_NAME");
        byte[] arr = CacheDoubleStaticUtils.getBytes("ARR");
        StringBuilder sb = new StringBuilder()
                .append("获取本地存储的二级缓存:")
                .append("\nAPP_NAME: ")
                .append(appName)
                .append("\nARR: ")
                .append(GsonUtils.toJson(arr));
        print(sb.toString());
    }

    //二级缓存.
    private void saveObject() {
    /*CacheDoubleUtils.java(二级缓存工具)中包含了CacheDiskUtils.java(磁盘缓存工具)和CacheMemoryUtils.java(内存缓存工具).
        通过"二级缓存工具"存对象时,会在"磁盘缓存工具"和"内存缓存工具"各存一份.
        通过"二级缓存工具"取对象时,会先在"内存缓存工具"中找,找到就返回该对象.找不到再在"磁盘缓存工具"中找,找到会先拷贝一份到"内存缓存工具"中再返回该对象,找不到就返回之前设置的默认值或null.*/
        //自定义初始化(可省略,即使用默认):
        CacheDiskUtils diskUtils = CacheDiskUtils.getInstance(Utils.getApp().getExternalCacheDir());//[磁盘缓存工具]:无参情况下默认缓存到内部沙箱的缓存目录.
        CacheMemoryUtils memoryUtils = CacheMemoryUtils.getInstance();//[内存缓存工具]:
        CacheDoubleUtils doubleUtils = CacheDoubleUtils.getInstance(memoryUtils, diskUtils);//[二级缓存工具]:
        CacheDoubleStaticUtils.setDefaultCacheDoubleUtils(doubleUtils);//[静态二级缓存工具]设置默认的[二级缓存工具],之后即可通过[静态二级缓存工具]的静态方法操作默认的[二级缓存工具]对应的非静态方法了.
        //使用:
        CacheDoubleStaticUtils.put("APP_NAME", getString(R.string.app_name)); //存字符串.
        CacheDoubleStaticUtils.put("ARR", new byte[]{1, 2, 3}); //存数组.
        print("二级缓存成功,可去路径下查看:\n" + Utils.getApp().getExternalCacheDir().getAbsolutePath());
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