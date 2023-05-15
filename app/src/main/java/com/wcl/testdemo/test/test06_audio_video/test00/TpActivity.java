package com.wcl.testdemo.test.test06_audio_video.test00;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.test.test06_audio_video.test00.tp.client.TpClientActivity;
import com.wcl.testdemo.test.test06_audio_video.test00.tp.service.TpServerService;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.wcl.testdemo.test.test06_audio_video.test00.tp.service.TpServerService.INTENT_KEY_INTENT;
import static com.wcl.testdemo.test.test06_audio_video.test00.tp.service.TpServerService.INTENT_KEY_RESULT_CODE;

/**
 * @Author WCL
 * @Date 2023/4/28 12:56
 * @Version
 * @Description 投屏测试界面.
 */
public class TpActivity extends AppCompatActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    private final int PROJECTION_REQUEST_CODE = 111;
    private MediaProjectionManager mediaProjectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tp);
        ButterKnife.bind(this);
        String ipAddressByWifi = NetworkUtils.getIpAddressByWifi();
        mTvConsole.setText("本机IP: " + ipAddressByWifi);
    }

    @OnClick({R.id.tv_0, R.id.tv_1})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://[服务端]启动录屏.
                if (mediaProjectionManager == null) {//初始化录屏服务.
                    mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);//拿到MediaProjectionManager
                }
                Intent intent = mediaProjectionManager.createScreenCaptureIntent();
                startActivityForResult(intent, PROJECTION_REQUEST_CODE);
                break;
            case R.id.tv_1://[客户端]接收投屏信息.
                startClient();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PROJECTION_REQUEST_CODE:
                LogUtils.d("录屏授权成功");
                ToastUtils.showShort("录屏授权成功");
                Intent service = new Intent(this, TpServerService.class);
                service.putExtra(INTENT_KEY_RESULT_CODE, resultCode);
                service.putExtra(INTENT_KEY_INTENT, data);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(service);
                } else {
                    startService(service);
                }
                break;
        }
    }

    //启动接收投屏的客户端.
    private void startClient() {
        EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setKeyListener(DigitsKeyListener.getInstance("0123456789."));//限制输入IP类型.
        et.setHint("请输入服务端IP");
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                .setTitle("请输入服务端IP")//设置对话框的标题
                .setView(et)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ip = et.getText().toString();
                        if (!TextUtils.isEmpty(ip)) {
                            String[] arr = ip.split("\\.");
                            if (arr.length == 4) {
                                Intent intent = new Intent(TpActivity.this, TpClientActivity.class);
                                intent.putExtra(TpClientActivity.INTENT_KEY_SERVICE_IP, ip);
                                startActivity(intent);
                                dialog.dismiss();
                            } else {
                                ToastUtils.showShort("IP格式不正确!");
                            }
                        } else {
                            ToastUtils.showShort("IP不能为空!");
                        }
                    }
                }).create();
        dialog.show();
    }

}