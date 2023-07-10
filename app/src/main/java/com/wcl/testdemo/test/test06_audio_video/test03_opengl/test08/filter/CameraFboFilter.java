package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.wcl.testdemo.R;

/**
 * @Author WCL
 * @Date 2023/5/24 13:31
 * @Version
 * @Description 相机滤镜.
 */
public class CameraFboFilter extends AbstractFboFilter {

    private float[] mtx;
    private int vMatrix;//GPU中vMatrix变量的句柄.

    public CameraFboFilter(Context context) {
        super(context, R.raw.camera_vert, R.raw.camera_frag);

        //获取GPU中着色器代码中的各个变量的句柄,方便有数据时赋值.
        vMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");//变换矩阵,需要将原本的vCoord(01,11,00,10)与矩阵相乘,才能得到Camera的正确显示方向.
    }

    @Override
    public void beforeDraw() {
        super.beforeDraw();
        //"vMatrix"(矩阵)赋值:
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mtx, 0);
    }

    /**
     * 设置矩阵.
     *
     * @param mtx 矩阵
     */
    public void setTransformMatrix(float[] mtx) {
        this.mtx = mtx;
    }

}
