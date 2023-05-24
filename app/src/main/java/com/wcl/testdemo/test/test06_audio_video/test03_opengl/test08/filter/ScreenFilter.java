package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter;

import android.content.Context;

import com.wcl.testdemo.R;

/**
 * @Author WCL
 * @Date 2023/5/24 14:31
 * @Version
 * @Description 屏幕滤镜.
 */
public class ScreenFilter extends AbstractFilter {

    public ScreenFilter(Context context) {
        super(context, R.raw.base_vert, R.raw.base_frag);
    }

}
