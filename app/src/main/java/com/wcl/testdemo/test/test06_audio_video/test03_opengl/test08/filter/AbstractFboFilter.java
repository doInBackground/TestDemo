package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.blankj.utilcode.util.LogUtils;

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

    /**
     * 执行该方法后,新建了FBO,新建了纹理,最后将二者绑定.
     * 之后,外界在调用GLES20.glDrawArrays()渲染前,先调用GLES20.glBindFramebuffer()绑定FBO,这时就不会再渲染到GLSurfaceView,而是渲染到了新建的FBO中.
     * 而新建的FBO已经与新建的纹理进行了绑定,故也会渲染到新建的纹理上,后续使用新建的纹理即可.
     * <p>
     * 该方法手动调用时机:在GLSurfaceView.Renderer的回调方法onSurfaceChanged()中,屏幕尺寸发生变化,这时应该手动调用该方法.
     *
     * @param width  宽
     * @param height 高
     */
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);

        releaseFrame();//释放新建的FBO和新建的纹理,为重新创建做准备.

        //新建FBO.
        mFrameBufferPtr = new int[1];//用来存储GPU中,新建FBO的句柄.
        GLES20.glGenFramebuffers(1, mFrameBufferPtr, 0);//新建FBO.(参数1:要实例化FBO的个数.参数2:入参出参对象.参数3:偏移量.)

        //新建纹理(图层).
        mTexturesPtr = new int[1];//用来存储GPU中,新建纹理的句柄.
        GLES20.glGenTextures(mTexturesPtr.length, mTexturesPtr, 0);//新建纹理.(参数1:要实例化纹理的个数.参数2:入参出参对象(函数结束后,新建纹理的句柄会保存到此数组中).参数3:偏移量.)
        LogUtils.d("纹理句柄-FBO新建纹理:", mTexturesPtr);//每次新建,句柄值会+1.

        //配置纹理.
        for (int i = 0; i < mTexturesPtr.length; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexturesPtr[i]);//<绑定纹理>表示后续的操作是原子性.

            //开始操作纹理:设置环绕和过滤方式.

            //(1)环绕(超出纹理坐标范围):(s==x, t==y) (GL_REPEAT:重复)
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            //(2)过滤(纹理像素映射到坐标点):(缩小、放大) (GL_LINEAR:线性,是放大模糊效果. GL_NEAREST:锯齿效果,没有模糊.)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);//放大过滤.
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);//缩小过滤.

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);//<解绑纹理>表示GPU操完了.
        }

        //开始做绑定操作.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexturesPtr[0]);//<绑定纹理>.
         /*
         指定一个二维的纹理图片
         参数2[level]:指定细节级别，0级表示基本图像，n级则表示Mipmap缩小n级之后的图像（缩小2^n）.
         参数3[internalformat]:指定纹理内部格式，必须是下列符号常量之一：GL_ALPHA，GL_LUMINANCE，GL_LUMINANCE_ALPHA，GL_RGB，GL_RGBA。
         参数4[width]:
         参数5[height]:指定纹理图像的宽高，所有实现都支持宽高至少为64 纹素的2D纹理图像和宽高至少为16 纹素的立方体贴图纹理图像。
         参数6[border]:指定边框的宽度。必须为0。
         参数7[format]:指定纹理数据的格式。必须匹配internalformat。下面的符号值被接受：GL_ALPHA，GL_RGB，GL_RGBA，GL_LUMINANCE，和GL_LUMINANCE_ALPHA。
         参数8[type]:指定纹理数据的数据类型。下面的符号值被接受：GL_UNSIGNED_BYTE，GL_UNSIGNED_SHORT_5_6_5，GL_UNSIGNED_SHORT_4_4_4_4，和GL_UNSIGNED_SHORT_5_5_5_1。
         参数9[pixels]:指定一个指向内存中图像数据的指针。(数据是从摄像头渲染的,暂未指定暂时先填空.)
         */
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferPtr[0]);//<綁定FBO>.

        //重要:真正发生绑定,fbo和纹理(图层).
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mTexturesPtr[0], 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);//<解绑纹理>.

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);//<解绑FBO>.

        //至此,新建FBO和新建纹理绑定完成,FBO的变化都会传入到新建纹理中去.
    }

    /**
     * FBO滤镜绘制时,调用此方法绘制.
     *
     * @param texture 纹理ID
     * @return 与FBO绑定的新建纹理的ID
     */
    @Override
    public int onDraw(int texture) {
        // GLES20.glBindFramebuffer()绑定FBO后,GLES20.glDrawArrays()渲染时就不会再渲染到GLSurfaceView,而是渲染到了FBO中.
        // 数据渲染到FBO中,意思是数据的输出设备就是FBO,不再是GLSurfaceView.
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBufferPtr[0]);//<绑定FBO>让摄像头的数据,先渲染到FBO.
        super.onDraw(texture);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);//<解绑FBO>.
        return mTexturesPtr[0];//返回FBO所对应的新建纹理.
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
