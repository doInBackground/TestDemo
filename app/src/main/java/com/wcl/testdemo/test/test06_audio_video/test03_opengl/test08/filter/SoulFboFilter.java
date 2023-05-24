package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.wcl.testdemo.R;

/**
 * @Author WCL
 * @Date 2023/5/24 14:31
 * @Version
 * @Description 灵魂出窍滤镜.
 */
public class SoulFboFilter extends AbstractFboFilter {

    private int mScalePercent;//取值范围:1-2.
    private int mMixturePercent;//取值范围:0-1.
    float scale = 0.0f; //缩放，越大就放的越大.
    float mix = 0.0f; //透明度，越大越透明.

    public SoulFboFilter(Context context) {
        super(context, R.raw.base_vert, R.raw.soul_frag);
        mScalePercent = GLES20.glGetUniformLocation(mProgram, "scalePercent");//从GPU拿到句柄.
        mMixturePercent = GLES20.glGetUniformLocation(mProgram, "mixturePercent");//从GPU拿到句柄.
    }

    /**
     * 摄像头数据绘制前该方法会被不断的调用.
     */
    @Override
    public void beforeDraw() {
        super.beforeDraw();
        GLES20.glUniform1f(mScalePercent, 1.0f + scale);//越来越大.
        GLES20.glUniform1f(mMixturePercent, 1.0f - mix);//越来越小.

        //步长数据.
        scale += 0.05f;
        mix += 0.05f;

        //重置步长数据.
        if (scale >= 1.0) {
            scale = 0.0f;
        }
        if (mix >= 1.0) {
            mix = 0.0f;
        }
    }

    @Override
    public int onDraw(int texture) {
        super.onDraw(texture);
        return mTexturesPtr[0];//纹理句柄.
    }
}
