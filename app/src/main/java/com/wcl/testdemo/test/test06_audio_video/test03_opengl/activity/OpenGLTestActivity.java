package com.wcl.testdemo.test.test06_audio_video.test03_opengl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test00.BackgroundRender;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test00.GLSurfaceViewActivity;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test00.TriangleRender;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test02.CameraFilter;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test02.CameraXActivity;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.CameraRender;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.CameraXGLSurfaceView;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.CameraXHelper;
import com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.activity.OpenGLRecordActivity;

import com.wcl.testdemo.init.BaseActivity;
import androidx.camera.core.CameraX;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.CameraRender.FilterEnum.BEAUTY;
import static com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.CameraRender.FilterEnum.CAMERA;
import static com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.CameraRender.FilterEnum.SHOW;
import static com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.CameraRender.FilterEnum.SOUL;
import static com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.CameraRender.FilterEnum.SPLIT;

/**
 * @Author WCL
 * @Date 2023/5/16 16:58
 * @Version
 * @Description OpenGL ES 测试界面.
 */
public class OpenGLTestActivity extends BaseActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gl_test);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://设置GLSurfaceView背景色.
                GLSurfaceViewActivity.setRender(new BackgroundRender());
                startActivity(new Intent(OpenGLTestActivity.this, GLSurfaceViewActivity.class));
                break;
            case R.id.tv_1://绘制一个三角形.
                GLSurfaceViewActivity.setRender(new TriangleRender());
                startActivity(new Intent(OpenGLTestActivity.this, GLSurfaceViewActivity.class));
                break;
            case R.id.tv_2://渲染摄像头画面:普通.
                CameraFilter.setShaderCode(R.raw.camera_vert, R.raw.camera_frag);
                startActivity(new Intent(OpenGLTestActivity.this, CameraXActivity.class));
                break;
            case R.id.tv_3://渲染摄像头画面:灰色滤镜.
                CameraFilter.setShaderCode(R.raw.camera_vert, R.raw.camera_frag1);
                startActivity(new Intent(OpenGLTestActivity.this, CameraXActivity.class));
                break;
            case R.id.tv_4://渲染摄像头画面:暖色滤镜.
                CameraFilter.setShaderCode(R.raw.camera_vert, R.raw.camera_frag2);
                startActivity(new Intent(OpenGLTestActivity.this, CameraXActivity.class));
                break;
            case R.id.tv_5://渲染摄像头画面:冷色滤镜.
                CameraFilter.setShaderCode(R.raw.camera_vert, R.raw.camera_frag3);
                startActivity(new Intent(OpenGLTestActivity.this, CameraXActivity.class));
                break;
            case R.id.tv_6://渲染摄像头画面:旋转滤镜.
                CameraFilter.setShaderCode(R.raw.camera_vert, R.raw.camera_frag4);
                startActivity(new Intent(OpenGLTestActivity.this, CameraXActivity.class));
                break;
            case R.id.tv_7://渲染摄像头画面:分屏滤镜.
                CameraFilter.setShaderCode(R.raw.camera_vert, R.raw.camera_frag5);
                startActivity(new Intent(OpenGLTestActivity.this, CameraXActivity.class));
                break;
            case R.id.tv_8://FBO帧缓存测试: 渲染并录制摄像头画面-> 分屏+灵魂出窍滤镜
                CameraXHelper.sCurrentFacing = CameraX.LensFacing.BACK;
                CameraXGLSurfaceView.setFilterType(new CameraRender.FilterEnum[]{CAMERA, SHOW, SOUL, SPLIT});//
                startActivity(new Intent(OpenGLTestActivity.this, OpenGLRecordActivity.class));
                break;
            case R.id.tv_9://FBO帧缓存测试: 渲染并录制摄像头画面-> 美颜滤镜(高斯模糊->高反差图->轮廓原图非轮廓模糊)
                CameraXHelper.sCurrentFacing = CameraX.LensFacing.FRONT;
                CameraXGLSurfaceView.setFilterType(new CameraRender.FilterEnum[]{CAMERA, SHOW, BEAUTY});//
                startActivity(new Intent(OpenGLTestActivity.this, OpenGLRecordActivity.class));
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