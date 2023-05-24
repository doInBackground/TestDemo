package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter;

import android.content.Context;
import android.opengl.GLES20;

/**
 * @Author WCL
 * @Date 2023/5/24 11:33
 * @Version
 * @Description FBO滤镜基类.
 */
class AbstractFboFilter extends AbstractFilter {

    /**
     * Comment:native层FBO句柄.
     */
    protected int[] mFrameBufferPtr;
    /**
     * Comment:native层纹理句柄.
     */
    protected int[] mTexturesPtr;

    public AbstractFboFilter(Context context, int vertexShaderId, int fragmentShaderId) {
        super(context, vertexShaderId, fragmentShaderId);
    }

    @Override
    public void setSize(int width, int height) {//在此处,初始化FBO.
        super.setSize(width, height);

        releaseFrame();

        //实例化fbo,让摄像头的数据,先渲染到fbo.
        mFrameBufferPtr = new int[1];//GPU中,句柄.
        GLES20.glGenFramebuffers(1, mFrameBufferPtr, 0);

        //生成纹理|生成图层.
        mTexturesPtr = new int[1];
        GLES20.glGenTextures(mTexturesPtr.length, mTexturesPtr, 0);

        //配置纹理.
        for (int i = 0; i < mTexturesPtr.length; i++) {
            //开始操作纹理:绑定纹理,后续配置纹理.
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexturesPtr[i]);//绑定纹理,表示后续的操作是原子性.
            // GL_LINEAR: 是放大模糊效果.
            // GL_NEAREST: 锯齿效果,没有模糊.
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);//放大过滤
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);//缩小过滤
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);//解绑纹理,表示GPU操完了.
        }

        //开始做绑定操作.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexturesPtr[0]);//绑定纹理.
         /*
         指定一个二维的纹理图片
         参数2[level]:指定细节级别，0级表示基本图像，n级则表示Mipmap缩小n级之后的图像（缩小2^n）.
         参数3[internalformat]:指定纹理内部格式，必须是下列符号常量之一：GL_ALPHA，GL_LUMINANCE，GL_LUMINANCE_ALPHA，GL_RGB，GL_RGBA。
         参数4[width]:
         参数5[height]:指定纹理图像的宽高，所有实现都支持宽高至少为64 纹素的2D纹理图像和宽高至少为16 纹素的立方体贴图纹理图像。
         参数6[border]:指定边框的宽度。必须为0。
         参数7[format]:指定纹理数据的格式。必须匹配internalformat。下面的符号值被接受：GL_ALPHA，GL_RGB，GL_RGBA，GL_LUMINANCE，和GL_LUMINANCE_ALPHA。
         参数8[type]:指定纹理数据的数据类型。下面的符号值被接受：GL_UNSIGNED_BYTE，GL_UNSIGNED_SHORT_5_6_5，GL_UNSIGNED_SHORT_4_4_4_4，和GL_UNSIGNED_SHORT_5_5_5_1。
         参数9[pixels]:指定一个指向内存中图像数据的指针。
         */
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        //开始使用GPU的fbo数据区域.
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferPtr[0]);//綁定FBO.

        //真正发生绑定,fbo和纹理(图层).
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mTexturesPtr[0], 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);//解绑纹理.

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);//解绑FBO.
    }

    @Override
    public int onDraw(int texture) {
        //数据渲染到fbo中. 意思是数据的输出设备就是fbo.
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferPtr[0]);
        super.onDraw(texture);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return mTexturesPtr[0];//返回fbo所对应的纹理.
    }

    //释放纹理和FBO.
    private void releaseFrame() {
        if (mTexturesPtr != null) {
            GLES20.glDeleteTextures(1, mTexturesPtr, 0);
            mTexturesPtr = null;
        }
        if (mFrameBufferPtr != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBufferPtr, 0);
        }
    }

}
