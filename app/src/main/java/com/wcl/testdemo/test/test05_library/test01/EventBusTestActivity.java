package com.wcl.testdemo.test.test05_library.test01;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.bean.eventbus.EventLogon;
import com.wcl.testdemo.bean.TestBean;
import com.wcl.testdemo.bean.eventbus.EventLogout;
import com.wcl.testdemo.init.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/11/30 17:06
 * @Version
 * @Description Retrofit测试界面.
 */
public class EventBusTestActivity extends BaseActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_bus_test);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this); //注册"EventBus".
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this); //注销"EventBus".
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://发送[普通]事件[登录].
                /**
                 * 测试结果:
                 * (1)发送该事件时,只会发给当前已注册的"事件订阅者".
                 */
                EventBus.getDefault().post(new EventLogon());
                break;
            case R.id.tv_1://发送[粘性]事件[登录].
                /**
                 * 测试结果:
                 * (1)"粘性事件"并不是只发送给"sticky=true"的"粘性事件订阅者",当前已注册的"普通事件订阅者"只要订阅类型相同也会收到该事件.
                 * (2)"粘性事件"会一直存在,即使有多个新注册的"粘性事件订阅者"多次消费该事件,它还是会一直存在,除非中途调用"removeStickyEvent(event)"才会移除该事件.
                 * (3)多次发送同一个类型的"粘性事件",新注册的"粘性事件订阅者"只会收到最后一次发送的事件.
                 */
                EventBus.getDefault().postSticky(new EventLogon());
                break;
            case R.id.tv_2://发送[粘性]事件[登出].
                /**
                 * 测试结果:
                 * (1)发送第2个其他类型的"粘性事件",不会将之前的"粘性事件"移除,会同时存在.
                 */
                EventBus.getDefault().postSticky(new EventLogout());
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