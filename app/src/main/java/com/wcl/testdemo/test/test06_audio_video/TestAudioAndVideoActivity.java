package com.wcl.testdemo.test.test06_audio_video;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.test.test06_audio_video.test00.TpActivity;
import com.wcl.testdemo.test.test06_audio_video.test01.CameraActivity;
import com.wcl.testdemo.test.test06_audio_video.test02.VideoChatActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/5/15 9:45
 * @Version
 * @Description 测试: 音视频相关.
 */
public class TestAudioAndVideoActivity extends AppCompatActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_audio_and_video);
        ButterKnife.bind(this);
        String ipAddressByWifi = NetworkUtils.getIpAddressByWifi();
        mTvConsole.setText("本机IP: " + ipAddressByWifi);
        PermissionUtils.permission(PermissionConstants.CAMERA)
                .callback(new PermissionUtils.SingleCallback() {
                    @Override
                    public void callback(boolean isAllGranted, @NonNull List<String> granted, @NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        if (!isAllGranted) {//权限全部同意.
                            ToastUtils.showShort("相机权限被拒,测试功能将受到限制!!!");
                        }
                    }
                })
                .request();
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://投屏测试.
                startActivity(new Intent(this, TpActivity.class));
                break;
            case R.id.tv_1://Camera测试.
                startActivity(new Intent(TestAudioAndVideoActivity.this, CameraActivity.class));
                break;
            case R.id.tv_2://视频通话测试.
                startChat();
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

    //启动视频聊天.
    private void startChat() {
        final Intent intent = new Intent(TestAudioAndVideoActivity.this, VideoChatActivity.class);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                .setTitle("启动客户端还是服务端?")//设置对话框的标题
                .setNegativeButton("服务端", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intent);
                    }
                })
                .setPositiveButton("客户端(需服务端IP)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText et = new EditText(TestAudioAndVideoActivity.this);
                        et.setInputType(InputType.TYPE_CLASS_NUMBER);
                        et.setKeyListener(DigitsKeyListener.getInstance("0123456789."));//限制输入IP类型.
                        et.setHint("请输入服务端IP");
                        new AlertDialog.Builder(TestAudioAndVideoActivity.this)
                                .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                                .setTitle("请输入服务端IP")//设置对话框的标题
                                .setView(et)
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String ip = et.getText().toString();
                                        if (!TextUtils.isEmpty(ip)) {
                                            String[] arr = ip.split("\\.");
                                            if (arr.length == 4) {
                                                intent.putExtra(VideoChatActivity.INTENT_SERVER_IP, ip);
                                                startActivity(intent);
                                                dialog.dismiss();
                                            } else {
                                                ToastUtils.showShort("IP格式不正确!");
                                            }
                                        } else {
                                            ToastUtils.showShort("IP不能为空!");
                                        }
                                    }
                                }).show();
                    }
                })
                .setNeutralButton("取消", null)
                .create();
        dialog.show();
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