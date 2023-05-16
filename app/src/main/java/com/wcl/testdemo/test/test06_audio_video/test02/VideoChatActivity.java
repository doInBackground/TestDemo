package com.wcl.testdemo.test.test06_audio_video.test02;

import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.wcl.testdemo.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @Author WCL
 * @Date 2023/5/16 15:16
 * @Version
 * @Description 视频通话界面.
 */
public class VideoChatActivity extends AppCompatActivity implements ISocketLive.SocketCallback {

    public static final String INTENT_SERVER_IP = "INTENT_SERVER_IP";

    private String mServerIp;//启动客户端,该变量不为空. 启动服务端,该变量为空.
    private LocalSurfaceView mLocalSurfaceView;//本地画面的SurfaceView.
    private DecodePlayerLiveH265 mDecodePlayerLiveH265;//解码器.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);
        mServerIp = getIntent().getStringExtra(INTENT_SERVER_IP);
        initView();
    }

    /**
     * 用户点击"呼叫".
     *
     * @param view 点击控件
     */
    public void connect(View view) {
        mLocalSurfaceView.startPush(mServerIp, this);
    }

    //本机Socket接收到了另外一端的数据.
    @Override
    public void callBack(byte[] data) {
        if (mDecodePlayerLiveH265 != null) {
            mDecodePlayerLiveH265.decode(data);
        }
    }

    //初始化控件.
    private void initView() {
        mLocalSurfaceView = findViewById(R.id.localSurfaceView);
        SurfaceView removeSurfaceView = findViewById(R.id.removeSurfaceView);
        removeSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mDecodePlayerLiveH265 = new DecodePlayerLiveH265();//初始化解码器.
                mDecodePlayerLiveH265.initDecoder(holder.getSurface());//解码器与展示器绑定.
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            }
        });
    }

}