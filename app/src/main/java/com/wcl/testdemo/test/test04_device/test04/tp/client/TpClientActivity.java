package com.wcl.testdemo.test.test04_device.test04.tp.client;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.utils.ScreenUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @Author WCL
 * @Date 2023/4/27 18:11
 * @Version
 * @Description 投屏客户端:显示界面.
 */
public class TpClientActivity extends AppCompatActivity {

    public static final String INTENT_KEY_SERVICE_IP = "serviceIp";
    /**
     * Comment:投屏客户端-管理器.
     */
    private TpClientManager mTpClientManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tp_client);
        ScreenUtils.setFullScreen(this);//设置全屏.
        String serviceIp = getIntent().getStringExtra(INTENT_KEY_SERVICE_IP);//服务端IP.
        SurfaceView surfaceView = findViewById(R.id.sv);
        surfaceView.getHolder().addCallback(
                new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(@NonNull SurfaceHolder holder) {
                        if (!TextUtils.isEmpty(serviceIp)) {
                            ToastUtils.showShort("SurfaceView初始化成功");
                            initSocketManager(serviceIp, holder.getSurface());// 连接到服务端
                        } else {
                            ToastUtils.showShort("未得到服务器IP");
                        }
                    }

                    @Override
                    public void surfaceChanged(
                            @NonNull SurfaceHolder holder, int format, int width, int height) {
                    }

                    @Override
                    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mTpClientManager != null) {
            mTpClientManager.stop();
        }
        super.onDestroy();
    }

    //初始化投屏客户端-管理器.
    private void initSocketManager(String ip, Surface surface) {
        mTpClientManager = new TpClientManager();
        mTpClientManager.start(ip, surface);
    }

}