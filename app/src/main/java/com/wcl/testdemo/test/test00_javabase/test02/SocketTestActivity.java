package com.wcl.testdemo.test.test00_javabase.test02;

import com.wcl.testdemo.init.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;

import static com.wcl.testdemo.test.test00_javabase.test02.SocketClientActivity.INTENT_SERVICE_IP;

/**
 * @Author WCL
 * @Date 2023/3/30 11:29
 * @Version
 * @Description 测试Socket.
 */
public class SocketTestActivity extends BaseActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_test);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://双人局域网聊天(服务端)
                startActivity(new Intent(this, SocketServiceActivity.class));
                break;
            case R.id.tv_1://双人局域网聊天(客户端)
                startClient();
                break;
            case R.id.tv_2://
                break;
            case R.id.tv_3://
                break;
            case R.id.tv_4://
                break;
            case R.id.tv_5://
                break;
        }
    }

    //启动对话框输入服务器IP,进行客户端连接.
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
                                Intent intent = new Intent(SocketTestActivity.this, SocketClientActivity.class);
                                intent.putExtra(INTENT_SERVICE_IP, ip);
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

    //一键三连,在三个地方输出打印结果.
    private void print(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            LogUtils.d(msg);
            ToastUtils.showShort(msg);
            mTvConsole.setText(msg);
        }
    }
}