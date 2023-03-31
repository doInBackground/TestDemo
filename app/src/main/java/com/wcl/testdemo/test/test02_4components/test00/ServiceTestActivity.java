package com.wcl.testdemo.test.test02_4components.test00;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/3/31 14:00
 * @Version
 * @Description Service相关测试.
 */
public class ServiceTestActivity extends AppCompatActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    private MyConnection mConn;
    private IDoInBackground mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://[启动Service]
                startService(new Intent(this, MyService.class));
                break;
            case R.id.tv_1://[绑定Service]
                bindService();
                break;
            case R.id.tv_2://[解绑Service]
                unBindService();
                break;
            case R.id.tv_3://[停止Service]
                stopService(new Intent(this, MyService.class));
                break;
            case R.id.tv_4://[播放Service音乐]
                if (mBinder != null) {
                    mBinder.startPlay();
                } else {
                    ToastUtils.showShort("Binder为空,无法调用服务中的方法!!!");
                }
                break;
            case R.id.tv_5://[暂停Service音乐]
                if (mBinder != null) {
                    mBinder.pausePlay();
                } else {
                    ToastUtils.showShort("Binder为空,无法调用服务中的方法!!!");
                }
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

    //解绑Service.
    private void unBindService() {
        if (mConn != null) {
            unbindService(mConn);
            mConn = null;//重复调用unbindService()解绑时会崩溃,故做null判断处理.
            mBinder = null;//解绑后不应该再调用服务中的方法,故置为null.
        }
    }

    //绑定Service.
    private void bindService() {
        if (mConn == null) {
            mConn = new MyConnection();
        }
        bindService(new Intent(this, MyService.class), mConn, BIND_AUTO_CREATE);
    }

    //一键三连,在三个地方输出打印结果.
    private void print(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            LogUtils.d(msg);
            ToastUtils.showShort(msg);
            mTvConsole.setText(msg);
        }
    }

    /**
     * @Author WCL
     * @Date 2023/3/31 14:59
     * @Version
     * @Description Service连接类(Service绑定结果回调类).
     */
    private class MyConnection implements ServiceConnection {
        /**
         * 当与Service成功连接.
         *
         * @param name
         * @param service
         */
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.d("ComponentName = " + name, "IBinder = " + service);
            mBinder = (IDoInBackground) service;
        }

        /**
         * 当与Service连接意外中断时被调用（例如当服务崩溃或被终止时）.
         * 当客户端取消绑定时，系统“绝对不会”调用该方法.
         *
         * @param name
         */
        public void onServiceDisconnected(ComponentName name) {
        }
    }
}