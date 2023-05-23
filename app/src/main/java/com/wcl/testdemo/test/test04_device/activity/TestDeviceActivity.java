package com.wcl.testdemo.test.test04_device.activity;

import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.utils.FindIpUtils;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/3/24 15:06
 * @Version
 * @Description 测试: 设备相关.
 */
public class TestDeviceActivity extends AppCompatActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_device);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://局域网发现设备(开启服务端):
                mTvConsole.setText("本机IP: " + NetworkUtils.getIpAddressByWifi());
                FindIpUtils.startService(9999);
                break;
            case R.id.tv_1://局域网发现设备(关闭服务端):
                FindIpUtils.stopService();
                break;
            case R.id.tv_2://局域网发现设备(客户端-寻找-服务端):
                FindIpUtils.startClient(new FindIpUtils.ClientListener() {
                    @Override
                    public void onFind(NsdServiceInfo nsdServiceInfo) {
                        String msg = "成功发现服务端,信息解析完成:"
                                + "\nServiceName = " + nsdServiceInfo.getServiceName()
                                + "\nServiceType = " + nsdServiceInfo.getServiceType()
                                + "\n端口号 = " + nsdServiceInfo.getPort()
                                + "\nHostName = " + nsdServiceInfo.getHost().getHostName()
                                + "\nHostAddress = " + nsdServiceInfo.getHost().getHostAddress()
                                + "\nAddress = " + nsdServiceInfo.getHost().getAddress();
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                print(msg);
                            }
                        });
                    }
                });
                break;
            case R.id.tv_3://局域网发现设备(客户端-结束寻找-服务端):
                FindIpUtils.stopClient();
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


    //一键三连,在三个地方输出打印结果.
    private void print(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            LogUtils.d(msg);
            ToastUtils.showShort(msg);
            mTvConsole.setText(msg);
        }
    }

}