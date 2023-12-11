package com.wcl.testdemo.test.test00_javabase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.test.test00_javabase.test00.JsonTestActivity;
import com.wcl.testdemo.test.test00_javabase.test01.CacheTestActivity;
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
            case R.id.tv_0://Json测试.
                startActivity(new Intent(this, JsonTestActivity.class));
                break;
            case R.id.tv_1://缓存测试.
                startActivity(new Intent(this, CacheTestActivity.class));
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


    //一键三连,在三个地方输出打印结果.
    private void print(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            LogUtils.d(msg);
            ToastUtils.showShort(msg);
            mTvConsole.setText(msg);
        }
    }

}