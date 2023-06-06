package com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test01.PlayVideoActivity;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/5/16 16:58
 * @Version
 * @Description OpenGL ES 测试界面.
 */
public class FFmpegTestActivity extends AppCompatActivity {

    static {
        System.loadLibrary("my-ffmpeg");
    }

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;
    @BindView(R.id.tv_0)
    TextView mTv0;
    private boolean mIsShowFFmpegInfo;//是否展示了FFmpeg的配置信息.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ffmpeg_test);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://FFmpeg配置信息.
                if (mIsShowFFmpegInfo) {
                    mTvConsole.setText("");
                    mTv0.setText("测试0:\n显示FFmpeg配置信息");
                } else {
                    mTvConsole.setText(getFFmpegInfo());
                    mTv0.setText("测试0:\n隐藏FFmpeg配置信息");
                }
                mIsShowFFmpegInfo = !mIsShowFFmpegInfo;
                break;
            case R.id.tv_1://FFmpeg软解-仅视频.
                startActivity(new Intent(this, PlayVideoActivity.class));
                break;
            case R.id.tv_2://
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

    public native String getFFmpegInfo();

    //一键三连,在三个地方输出打印结果.
    private void print(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            LogUtils.d(msg);
            ToastUtils.showShort(msg);
            mTvConsole.setText(msg);
        }
    }

}