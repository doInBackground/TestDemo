package com.wcl.testdemo.test.test00_javabase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.bean.eventbus.EventLogon;
import com.wcl.testdemo.bean.eventbus.EventLogout;
import com.wcl.testdemo.test.test00_javabase.test00.JsonTestActivity;
import com.wcl.testdemo.test.test00_javabase.test01.CacheTestActivity;
import com.wcl.testdemo.test.test00_javabase.test02.SocketTestActivity;
import com.wcl.testdemo.test.test00_javabase.test03.EncryptDecryptTestActivity;
import com.wcl.testdemo.utils.MyTestNativeUtils;

import com.wcl.testdemo.init.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/3/24 15:03
 * @Version
 * @Description 测试: Java基础.
 */
public class JavaBaseActivity extends BaseActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;
    private MyTestNativeUtils mMyTestNativeUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_base);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
            case R.id.tv_3://JNI调用测试.
                jniTest();
                break;
            case R.id.tv_4://加密解密测试.
                startActivity(new Intent(this, EncryptDecryptTestActivity.class));
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

    //JNI调用测试.
    private void jniTest() {
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
    }

    //一键三连,在三个地方输出打印结果.
    private void print(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            LogUtils.d(msg);
            ToastUtils.showShort(msg);
            mTvConsole.setText(msg);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(EventLogon event) {//方法名不限,可自定义.
        LogUtils.d("EventBusSubscribe", "订阅者[" + this.getClass().getSimpleName() + "], 粘性事件[" + event.getClass().getSimpleName() + "]");
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEvent(EventLogout event) {//方法名不限,可自定义.
        LogUtils.d("EventBusSubscribe", "订阅者[" + this.getClass().getSimpleName() + "], 粘性事件[" + event.getClass().getSimpleName() + "]");
    }

}