package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test00;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @Author WCL
 * @Date 2023/5/17 11:03
 * @Version
 * @Description 三角形的渲染器.
 * | 内置变量       | 含义                                | 值数据类型 |
 * | ------------- | ---------------------------------- | ---------- |
 * | gl_PointSize  | 点渲染模式，方形点区域渲染像素大小     | float      |
 * | gl_Position   | 顶点位置坐标                        | vec4       |
 * | gl_FragColor  | 片元颜色值                         | vec4       |
 * | gl_FragCoord  | 片元坐标，单位像素                 | vec2       |
 * | gl_PointCoord | 点渲染模式对应点像素坐标           | vec2       |
 */
public class TriangleRender implements GLSurfaceView.Renderer {

    /**
     * Comment:顶点程序.(有几个顶点,该程序就会执行几次).
     */
    private final String mVertexShaderCode =
            "attribute vec4 vPosition; " + //"attribute"声明的变量,表示要从Java传来的变量."vec4"表示float[4]用来填充一个顶点坐标的x/y/z/w值.
                    "void main() {" +
                    "gl_Position = vPosition;" + //"gl_Position"是内置变量,为它赋值后GL就知道了绘制形状的顶点.
                    "}";
    /**
     * Comment:片元程序.(执行多次).
     */
    private final String mFragmentShaderCode =
            "precision mediump float;" + //"precision"表示设置精度(高中低),此处表示所有"float"使用"mediump"精度.
                    "uniform vec4 vColor;" + //CPU传过来的颜色.
                    "void main() {" +
                    "  gl_FragColor = vColor;" + //"gl_FragColor"是内置变量,为它赋值后GL就知道了绘制的颜色.
                    "}";
    /**
     * Comment: 顶点坐标数组.
     * OpenGL是一个3D图形库，所以在OpenGL中我们指定的所有坐标都是3D坐标（x、y、z）。
     * (界面中心为原点,x范围[-1,1],y范围[-1,1],z范围[-1,1]).
     */
    private final float[] mTriangleCoords = {
            0f, 1f, 0.0f,  // top: (x,y,z)
            -1f, -1f, 0.0f,  // left-bottom: (x,y,z)
            1f, -1f, 0.0f,   // right-bottom : (x,y,z)
    };
    /**
     * Comment:FloatBuffer类型的顶点坐标,以便OpenGLES程序接收.
     */
    private FloatBuffer mVertexFloatBuffer;
    /**
     * Comment:OpenGLES程序.
     */
    private int mProgram;
    /**
     * Comment:颜色数组，依次为红绿蓝和透明通道.(红色)
     */
    private final float[] mColorArr = {
            1.0f, 0f, 0f, 1.0f
    };

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);//设置背景色.
        //顶点坐标的转换.
        ByteBuffer bb = ByteBuffer.allocateDirect(mTriangleCoords.length * 4);//申请底层空间.(每个float占4字节).
        bb.order(ByteOrder.nativeOrder());//GPU重新整理下内存.
        mVertexFloatBuffer = bb.asFloatBuffer();//将坐标数据转换为FloatBuffer，用以传入OpenGLES程序.
        mVertexFloatBuffer.put(mTriangleCoords);
        mVertexFloatBuffer.position(0);
        //处理着色器程序.
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, mVertexShaderCode);//顶点着色器.
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, mFragmentShaderCode);//片元着色器.
        //处理OpenGLES程序.
        mProgram = GLES20.glCreateProgram();//创建一个空的OpenGLES程序,并返回句柄.
        GLES20.glAttachShader(mProgram, vertexShader);//将顶点着色器加入到程序.
        GLES20.glAttachShader(mProgram, fragmentShader);//将片元着色器加入到程序中.
        GLES20.glLinkProgram(mProgram);//连接到着色器程序.
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);//在onDrawFrame()中调用glClear(GL_COLOR_BUFFER_BIT)清空屏幕，会调用glClearColor中定义的颜色来填充整个屏幕。

        //将程序加入到OpenGLES2.0环境.
        GLES20.glUseProgram(mProgram);

        //获取顶点着色器的vPosition成员句柄.
        int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //启用三角形顶点的句柄:允许CPU往GPU里面写该值数据.
        GLES20.glEnableVertexAttribArray(positionHandle);
        //准备三角形的坐标数据,告诉OpenGL该如何解析顶点数据.
        //参数意义:([1]顶点着色器句柄,[2]顶点坐标的维度(此处3维),[3]顶点坐标值类型,[4]false,[5]每个顶点坐标所用字节<步长:维度(此处3维)*坐标值类型所占长度(此处float占4字节)>,[6]顶点位置向量首地址);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexFloatBuffer);

        //获取片元着色器的vColor成员的句柄.
        int colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        //设置颜色.
        //参数意义:([1]要更改的uniform变量的位置<片元着色器句柄>,[2]要更改的元素个数<如果目标uniform变量不是一个数组那么这个值应该设为1>,[3]颜色数组,[4]offset);
        GLES20.glUniform4fv(colorHandle, 1, mColorArr, 0);

        //绘制三角形.
        //参数意义:([1]绘制形状类型,[2]从数组缓存中的哪一位开始绘制<一般为0>,[3]数组中顶点的数量);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

        //禁止顶点数组的句柄.
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    /*
     * 根据想要的类型,创建着色器,并返回其句柄.
     * type:着色器类型.
     * shaderCode:本质上是一个可执行程序,执行在GPU上.
     * */
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);//根据type,创建空的"顶点着色器"或者"片元着色器",并返回着色器句柄.
        GLES20.glShaderSource(shader, shaderCode);//根据着色器句柄,将源码加入到着色器中.
        GLES20.glCompileShader(shader);//编译源码.
        return shader;//着色器句柄(是GPU里面的).
    }

}
