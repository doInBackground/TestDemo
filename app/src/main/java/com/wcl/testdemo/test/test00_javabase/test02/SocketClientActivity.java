package com.wcl.testdemo.test.test00_javabase.test02;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.wcl.testdemo.test.test00_javabase.test02.SocketServiceActivity.SERVICE_PORT;

/**
 * @Author WCL
 * @Date 2023/3/30 13:56
 * @Version
 * @Description 双人局域网聊天(客户端)
 */
class SocketClientActivity extends AppCompatActivity {

    public static final String INTENT_SERVICE_IP = "INTENT_SERVICE_IP";

    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_content)
    TextView mTvContent;
    @BindView(R.id.tv_sure)
    TextView mTvSure;
    @BindView(R.id.et)
    EditText mEt;
    private final StringBuffer mSb = new StringBuffer();
    private String mServiceIp;
    /**
     * Comment: 是否连接上服务器.
     */
    private volatile boolean sIsConnection = false;
    /**
     * Comment: 发送任务.
     */
    private ClientSend sClientSendRunnable;
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket_client);
        ButterKnife.bind(this);
        mServiceIp = getIntent().getStringExtra(INTENT_SERVICE_IP);
        mTvTitle.setText("[" + mServiceIp + "] 聊天室 (连接中...)");
        new Thread(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            try {
                mSocket.close();
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
                    if (sIsConnection) {
                        if (sClientSendRunnable != null) {
                            sClientSendRunnable.msg = msg;
                            new Thread(sClientSendRunnable).start();//新建线程启动.
                            mSb.append("\n[我]: ").append(msg);
                            mTvContent.setText(mSb);
                            mEt.setText(null);
                        } else {
                            LogUtils.w("客户端: 准备发送数据: 发送任务为空.");
                        }
                    } else {
                        LogUtils.w("客户端: 准备发送数据: 服务器未连接.");
                    }
                }
                break;
        }
    }

    //客户端连接Socket服务器. //在Android中这个方法只能运行在子线程.
    private void connect() {
        LogUtils.v("客户端: 准备连接.");
        while (!sIsConnection) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTvTitle.setText("[" + mServiceIp + "] 聊天室 (连接中...)");
                }
            });
            try {
                //客户端尝试连接服务器:
                //Android不能在主线程创建.
                mSocket = new Socket(mServiceIp, SERVICE_PORT);
                sIsConnection = true;
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvTitle.setText("[" + mServiceIp + "] 聊天室");
                    }
                });
                LogUtils.d("客户端: 连接成功!!!");
                //发送需求: 客户端socket先拿到输出流,再写数据.
                ObjectOutputStream oos = new ObjectOutputStream(mSocket.getOutputStream()); //ObjectOutputStream不是同一对象时,接收端会无法解析报错.所以这里两个发送者要用同一份ObjectOutputStream.//Socket用普通输出流发送数据后没有close()时,接收端用普通的输入流读取输出时拿不到,故这里用ObjectOutputStream.
                new Thread(new ClientHeart(mSocket, oos)).start();//发送需求(心跳): 拿到客户端socket,再拿到输出流,发送.
                sClientSendRunnable = new ClientSend(mSocket, oos);//发送需求: 拿到客户端socket,再拿到输出流,发送.
                //接收需求: 客户端socket先拿到输入流,再读数据.
                new Thread(new ClientReceive(mSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
                LogUtils.d("客户端: 尝试重新连接...");
                sIsConnection = false;
                try {
                    Thread.sleep(2000); //等待一下,再尝试重连.
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * @Author WCL
     * @Date 2023/3/17 10:05
     * @Version
     * @Description 客户端: 接收者.
     */
    class ClientReceive implements Runnable {

        private Socket mSocket;

        public ClientReceive(Socket socket) {
            mSocket = socket;
        }

        @Override
        public void run() {
            LogUtils.v("客户端-接收者: 准备接收数据.");
            try {
                ObjectInputStream ois = new ObjectInputStream(mSocket.getInputStream());
                while (true) { //死循环:保证线程不结束,始终接收数据.
                    Object msg = ois.readObject(); //阻塞:收到数据时才执行,保证循环不是快速无限执行浪费性能的.
                    mSb.append("\n[Ta]: ").append(msg);
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvContent.setText(mSb);
                        }
                    });
                }
            } catch (IOException | ClassNotFoundException e) {
                LogUtils.e("客户端-接收者,报错: ", e);
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
     * @Description 客户端: 发送者.
     */
    class ClientSend implements Runnable {
        private Socket mSocket;
        private ObjectOutputStream mOos;
        public volatile String msg = null;

        public ClientSend(Socket socket, ObjectOutputStream oos) {
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
                LogUtils.e("客户端-发送者,报错: ", e);
                try {
                    mSocket.close();
                    sIsConnection = false;
                    connect();
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
     * @Description 客户端: 心跳包机制,保证Socket连接.
     */
    class ClientHeart implements Runnable {
        private Socket mSocket;
        private ObjectOutputStream mOos;

        public ClientHeart(Socket socket, ObjectOutputStream oos) {
            mSocket = socket;
            mOos = oos;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep(10000);//间隔一段时间发送一个心跳包.
                    mOos.writeObject("心跳包");
                    mOos.flush();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                LogUtils.e("客户端-心跳包,报错: ", e);
                try {
                    mSocket.close();
                    sIsConnection = false;
                    connect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}