package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter;

import android.content.Context;

import com.wcl.testdemo.R;

/**
 * @Author WCL
 * @Date 2023/5/24 14:44
 * @Version
 * @Description 多屏滤镜.
 */
public class SplitFboFilter extends AbstractFboFilter {

    public SplitFboFilter(Context context) {
        super(context, R.raw.base_vert, R.raw.split2_screen);
    }

    public int onDraw(int texture) {
        super.onDraw(texture);
        return mTexturesPtr[0];
    }

}