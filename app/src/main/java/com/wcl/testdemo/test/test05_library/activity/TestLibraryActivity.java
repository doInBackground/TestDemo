package com.wcl.testdemo.test.test05_library.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.bean.eventbus.EventLogon;
import com.wcl.testdemo.init.BaseActivity;
import com.wcl.testdemo.test.test05_library.test00.RetrofitTestActivity;
import com.wcl.testdemo.test.test05_library.test01.EventBusTestActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/3/24 15:06
 * @Version
 * @Description 测试: 三方库相关.
 */
public class TestLibraryActivity extends BaseActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_library);
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
            case R.id.tv_0://Retrofit测试.
                startActivity(new Intent(this, RetrofitTestActivity.class));
                break;
            case R.id.tv_1://EventBus测试.
                startActivity(new Intent(this, EventBusTestActivity.class));
                break;
            case R.id.tv_2://
                break;
            case R.id.tv_3://
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventLogon event) {//方法名不限,可自定义.
        LogUtils.d("EventBusSubscribe", "订阅者[" + this.getClass().getSimpleName() + "], 事件[" + event.getClass().getSimpleName() + "]");
    }

}