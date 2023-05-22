package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test02;

import android.content.Context;
import android.opengl.GLES20;

import com.wcl.testdemo.R;
import com.wcl.testdemo.utils.OpenGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_TEXTURE0;

/**
 * @Author WCL
 * @Date 2023/5/18 10:17
 * @Version
 * @Description 滤镜类.
 * <p>
 * ByteBuffer:
 * 首先要理解两个概念：
 * 1.Android应用层的JAVA代码并不是运行在硬件上，而是运行在Dalvik虚拟机上面，运行在虚拟机上的代码不能直接访问native环境。
 * 2.opengl作为native系统库，运行在native环境。
 * <p>
 * android应用层的代码运行环境和opengl运行的环境不同。那么他们怎么通信呢？有两种技术。
 * 第一种是使用NDK，当我们调用Android上面OpenGL的函数的时候，实际上就是在使用NDK调用native系统库中的opengl，只是这些都被封装好了。
 * 第二种技术就是改变内存分配的方式，JAVA有一个特殊的类集合，它们可以分配native环境的内存块，并且把JAVA的数据复制到native环境的内存中。
 * <p>
 * 第二种技术方案。需要了解ByteBuffer,还有它的兄弟类FloatBuffer,DoubleBuffer等几个类，
 * 这几个类的对象都是通过ByteBuffer的asFloatBuffer(), asDoubleBuffer()等几个函数获得，它们使用ByteBuffer的allocateDirect分配的内存。
 * 使用案例参考本类中的代码.
 */
public class ScreenFilter {

    private static int sRawVertexId;//顶点着色器代码
    private static int sRawFragmentId;//片元着色器代码
    /**
     * Comment:顶点坐标.
     */
    private final float[] VERTEX = {
            -1.0f, -1.0f,//左下
            1.0f, -1.0f,//右下
            -1.0f, 1.0f,//左上
            1.0f, 1.0f//右上
    };
    /**
     * Comment:纹理坐标.
     */
    private final float[] TEXTURE = {
            0.0f, 0.0f,//左下
            1.0f, 0.0f,//右下
            0.0f, 1.0f,//左上
            1.0f, 1.0f//右上
    };
    private final FloatBuffer mVertexFloatBuffer; //顶点坐标(native)
    private final FloatBuffer mTextureFloatBuffer; //纹理坐标(native)

    private int mProgram;//顶点着色器&片元着色器.
    private int vPosition;//GPU中vPosition变量的句柄.
    private int vCoord;//GPU中vCoord变量的句柄.
    private int vTexture;//GPU中vTexture变量的句柄.
    private int vMatrix;//GPU中vMatrix变量的句柄.

    private int mWidth;
    private int mHeight;
    private float[] mtx;

    //构造方法.
    public ScreenFilter(Context context) {
        //创建native内存,接收顶点坐标.
        mVertexFloatBuffer = ByteBuffer.allocateDirect(2 * 4 * 4)//分配native缓冲区大小,这块内存不会被垃圾回收机制给回收: VERTEX.length*sizeof(float).
                .order(ByteOrder.nativeOrder())//按照native字节序组织内容.GPU重新整理下内存.
                .asFloatBuffer();//不是想操作单独的字节,而是想操作浮点数.故转换为FloatBuffer,用以传入OpenGLES程序.
        mVertexFloatBuffer.clear();
        mVertexFloatBuffer.put(VERTEX);//把dalvik虚拟机中的内存复制到native环境内存中.将坐标数据
        //创建native内存,接收纹理坐标.
        mTextureFloatBuffer = ByteBuffer.allocateDirect(2 * 4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTextureFloatBuffer.clear();
        mTextureFloatBuffer.put(TEXTURE);
        //获取着色器程序源码.
        String vertexShader = OpenGLUtils.readRawTextFile(context, sRawVertexId == 0 ? R.raw.camera_vert : sRawVertexId);//顶点着色器程序.
        String fragShader = OpenGLUtils.readRawTextFile(context, sRawFragmentId == 0 ? R.raw.camera_frag : sRawFragmentId);//片元着色器程序.
        mProgram = OpenGLUtils.loadProgram(vertexShader, fragShader);//返回着色器程序句柄.//先编译,再链接,再运行程序.

        //获取GPU中着色器代码中的各个变量的句柄,方便有数据时赋值.(句柄值是其变量在着色器代码中先后顺序的索引)
        vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");//0
        vCoord = GLES20.glGetAttribLocation(mProgram, "vCoord");//1//接收纹理坐标,接收采样器采样图片的坐标.
        vTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");//采样点的坐标.
        vMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");//变换矩阵,需要将原本的vCoord(01,11,00,10)与矩阵相乘.
    }

    /**
     * 拿到摄像头数据,通知渲染.
     *
     * @param texture 图层ID
     */
    public void onDraw(int texture) {
        GLES20.glViewport(0, 0, mWidth, mHeight);//View的大小.
        GLES20.glUseProgram(mProgram);//使用程序:将程序加入到OpenGLES2.0环境.

        //给着色器代码中的变量赋值.
        //"vPosition"(顶点坐标)赋值:
        mVertexFloatBuffer.position(0);//从索引位0的地方读.
        /*
        CPU和GPU的通信:CPU的数据通过ByteBuffer设置到GPU,在通过该方法将GPU的值(参数6)赋值给GPU中的另一个变量(参数1).
        参数意义可参考本项目TriangleRender类中的相同方法的描述.
        参数1[index]:指定要修改的通用顶点属性的索引。
        参数2[size]:指定每个通用顶点属性的组件数。
        参数3[type]:指定数组中每个组件的数据类型。接受符号常量GL_FLOAT/GL_BYTE/GL_UNSIGNED_BYTE/GL_SHORT/GL_UNSIGNED_SHORT/GL_FIXED.初始值为GL_FLOAT.
        参数4[normalized]:指定在访问定点数据值时是应将其标准化（GL_TRUE:比如坐标越界会改为可用值）还是直接转换为定点值（GL_FALSE）。
        参数5[stride]:数据源(参数6)传给目标数据(参数1)时的步长.
        参数6[ptr]:数据源(首地址).
        */
        GLES20.glVertexAttribPointer(vPosition, 2, GL_FLOAT, false, 0, mVertexFloatBuffer);
        GLES20.glEnableVertexAttribArray(vPosition);//生效:CPU传数据到GPU,默认情况下着色器无法读取到这个数据,需要我们启用一下才可以读取.

        //"vCoord"(纹理坐标)赋值:
        mTextureFloatBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mTextureFloatBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);
        //"vPosition"和"vCoord"传过去后,形状就确定了.

        //"vTexture"(采样器)赋值:
        GLES20.glActiveTexture(GL_TEXTURE0);//激活图层,GPU获取读取. 参数取值范围:GL_TEXTURE(0-31).
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);//绑定生成一个采样器.
        GLES20.glUniform1i(vTexture, 0);//参数2表示采样图层索引. 是否应该传入texture变量呢?

        //"vMatrix"(矩阵)赋值:
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mtx, 0);

        //通知绘制: 参数意义:([1]绘制形状类型,[2]从数组缓存中的哪一位开始绘制<一般为0>,[3]数组中顶点的数量);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    /**
     * GLSurfaceView宽高改变时需调用此方法,传入新的宽高.
     *
     * @param width  宽
     * @param height 高
     */
    public void setSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    /**
     * 设置矩阵.
     *
     * @param mtx 矩阵
     */
    public void setTransformMatrix(float[] mtx) {
        this.mtx = mtx;
    }

    /**
     * 设置顶点着色器代码和片元着色器代码.
     *
     * @param rawVertexId   顶点着色器代码
     * @param rawFragmentId 片元着色器代码
     */
    public static void setShaderCode(int rawVertexId, int rawFragmentId) {
        ScreenFilter.sRawVertexId = rawVertexId;
        ScreenFilter.sRawFragmentId = rawFragmentId;
    }

}
