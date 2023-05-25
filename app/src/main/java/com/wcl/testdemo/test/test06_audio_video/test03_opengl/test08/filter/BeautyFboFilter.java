package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.wcl.testdemo.R;

/**
 * @Author WCL
 * @Date 2023/5/25 15:07
 * @Version
 * @Description 美颜滤镜.
 */
public class BeautyFboFilter extends AbstractFboFilter {

    private int mWidthPtr;//GPU中width变量的句柄.
    private int mHeightPtr;//GPU中height变量的句柄.

    public BeautyFboFilter(Context context) {
        //美颜片元程序1: R.raw.beauty_frag
        //美颜片元程序2: R.raw.beauty_fragment2
        super(context, R.raw.base_vert, R.raw.beauty_frag);
        mWidthPtr = GLES20.glGetUniformLocation(mProgram, "width");
        mHeightPtr = GLES20.glGetUniformLocation(mProgram, "height");
    }

    @Override
    public void beforeDraw() {
        super.beforeDraw();
        GLES20.glUniform1i(mWidthPtr, mWidth);
        GLES20.glUniform1i(mHeightPtr, mHeight);
    }

}
