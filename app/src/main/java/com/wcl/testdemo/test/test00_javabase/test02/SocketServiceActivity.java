package com.wcl.testdemo.test.test00_javabase.test02;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/3/30 13:51
 * @Version
 * @Description 双人局域网聊天(服务端)
 * <p>
 * 此处仅进行Socket的使用测试,其他问题暂不处理,比如:
 * (*)发送消息以及服务端接收到客户端Socket时,线程的频繁创建,未使用线程池限制.
 * (*)Activity退出时客户端Socket重连线程还在不断尝试重连.
 * (*)客户端退出时,服务端的接收线程未关闭.
 * (*)ServerSocket和Socket的关闭释放.
 */
class SocketServiceActivity extends AppCompatActivity {

    public static final int SERVICE_PORT = 9999;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.tv_sure)
    TextView mTvSure;
    @BindView(R.id.et)
    EditText mEt;
    private final StringBuffer mSb = new StringBuffer();
    /**
     * Comment: 发送任务.
     */
    private ServiceSend sServiceSendRunnable;
    private ServerSocket mServerSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_service);
        ButterKnife.bind(this);
        mTvTitle.setText("[" + NetworkUtils.getIpAddressByWifi() + "] 聊天室(群主)");
        new Thread(new Runnable() {
            @Override
            public void run() {
                startService();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServerSocket != null) {
            try {
                mServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick({R.id.tv_sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sure:
                String msg = mEt.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    ToastUtils.showShort("发送的消息不能为空");
                } else {
                    if (sServiceSendRunnable != null) {
                        sServiceSendRunnable.msg = msg;
                        new Thread(sServiceSendRunnable).start();//新建线程启动.
                        mSb.append("\n[我]: ").append(msg);
                        mTvContent.setText(mSb);
                        mEt.setText(null);
                    } else {
                        LogUtils.w("服务端: 准备发送数据: 发送任务为空.");
                    }
                }
                break;
        }
    }

    //启动Socket服务器. //在Android中这个方法只能运行在子线程.
    private void startService() {
        try {
            //服务端: 启动服务器.
            //启动服务端.
            mServerSocket = new ServerSocket(SERVICE_PORT);
            LogUtils.d("服务器: 开始运行!!!");
            while (true) {
                //接收新的客户端:
                Socket socket = mServerSocket.accept();//阻塞直到有下一个客户端socket接入:故Android不能在主线程这么做.
                LogUtils.d("服务器: accept() 一次阻塞结束...");
                //接收需求: 拿到客户端socket,先拿到输入流,再读数据.
                new Thread(new ServiceReceive(socket)).start();
                //发送需求: 拿到客户端socket,先拿到输出流,再写数据.
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream()); //Socket用普通输出流发送数据后没有close()时,接收端用普通的输入流读取输出时拿不到,故这里用ObjectOutputStream.
                sServiceSendRunnable = new ServiceSend(socket, oos);//发送需求: 拿到客户端socket,再拿到输出流,发送.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Author WCL
     * @Date 2023/3/17 10:05
     * @Version
     * @Description 服务器: 接收者.
     */
    class ServiceReceive implements Runnable {

        private Socket mSocket;

        public ServiceReceive(Socket socket) {
            mSocket = socket;
        }

        @Override
        public void run() {
            LogUtils.v("服务器-接收者: 准备接收数据.");
            try {
                ObjectInputStream ois = new ObjectInputStream(mSocket.getInputStream());
                while (true) { //死循环:保证线程不结束,始终接收数据.
                    Object msg = ois.readObject(); //阻塞:收到数据时才执行,保证循环不是快速无限执行浪费性能的.
//                    String allMsg = "服务器-接收者: 接收数据: \n" + msg;
//                    LogUtils.d(allMsg);
//                    ToastUtils.showShort(allMsg);
                    if (!TextUtils.equals("心跳包", msg.toString())) {
                        mSb.append("\n[Ta]: ").append(msg);
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTvContent.setText(mSb);
                            }
                        });
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                LogUtils.e("服务器-接收者,报错: ", e);
                e.printStackTrace();
            } finally {
                try {
                    mSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    /**
     * @Author WCL
     * @Date 2023/3/17 10:12
     * @Version
     * @Description 服务端: 发送者.
     */
    class ServiceSend implements Runnable {
        private Socket mSocket;
        private ObjectOutputStream mOos;
        public volatile String msg = null;

        public ServiceSend(Socket socket, ObjectOutputStream oos) {
            mSocket = socket;
            mOos = oos;
        }

        @Override
        public void run() {
            try {
                mOos.writeObject(msg);
                mOos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.e("服务端-发送者,报错: ", e);
                try {
                    mSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

}