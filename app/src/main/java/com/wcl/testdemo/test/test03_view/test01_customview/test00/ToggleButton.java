package com.wcl.testdemo.test.test03_view.test01_customview.test00;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.wcl.testdemo.R;

/**
 * @Author WCL
 * @Date 2017/6/8 10:51
 * @Version 1.0
 * @Description 自定义控件-开关
 * 1.继承现有的容器  LinearLayout RelativeLayout 再组合原生控件  加入本容器  --> 写法简单粗暴,不需要考虑测量 布局 和绘制操作 都是由系统原生控件自己操作的.
 * 2.直接继承View或ViewGroup --> 写法较为复杂 需要自行重写onMeasure  onLayout  onDraw等方法.
 */
public class ToggleButton extends View {

    private Bitmap downBitmap;//开关背景图
    private Bitmap upBitmap;//开关滑块图
    private float left;//滑块距离左侧Y轴距离
    private int max;//滑块左右最大滑动区域宽度
    private long downTime;//按下时的系统时间
    private int downX;//按下位置X轴坐标
    private int downY;//按下位置Y轴坐标
    private boolean isOpen;//开关是否打开
    boolean isChange;//isChange是在down下的时候记录的isOpen的状态
    private OnToggleButtonChangeListener listener;//开关状态改变的监听

    //控件的四大流程
    // 1.构造初始化 --->
    // 创建对象时,用于初始化数据.
    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    // 2.测量 --->
    // 测量并设置控件的宽高.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        //[###]规范的测量宽高的方法:
//        int width = measureWidthHeight(widthMeasureSpec);
//        int height = measureWidthHeight(heightMeasureSpec);
//        setMeasuredDimension(width, height);//设置测量后的宽高.
        //[###]不规范的测量宽高的方法: 此处没有对宽高参考值进行解析,直接用了背景图的宽高.
        setMeasuredDimension(downBitmap.getWidth(), downBitmap.getHeight());//将控件的宽高设置为和背景图片同宽高.
    }

    // 3.布局(排版) --->
    // ViewGroup的子类-组控件,具备摆放子元素的功能,需要重写该方法,对子元素进行排版布局,摆放子控件.
    // View的子类-单控件,一般不需要理会该方法.
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    // 4.绘制 --->
    // View的子类-单控件,具备具体的功能,一般都要重写该方法.
    // ViewGroup的子类-组控件,具备摆放子元素的功能,一般不用理会该方法.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //自定义开关-显示
        // 画在屏幕上
        // 参数2: 距离屏幕左边缘距离
        // 参数3: 距离屏幕上边缘距离
        canvas.drawBitmap(downBitmap, 0, 0, null);
        canvas.drawBitmap(upBitmap, left, 0, null);
    }

    //初始化:图片资源和自定义属性.
    private void init(AttributeSet attrs) {
        downBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.toggle_button_down);
        upBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.toggle_button_up);
        max = downBitmap.getWidth() - upBitmap.getWidth();
        //自定义开关-自定义属性:在"res/values/"文件夹下新建attrs.xml文件,用来配置自定义属性.文件内容如下:
//        <?xml version="1.0" encoding="utf-8"?>
//        <resources>
//            <declare-styleable name="Toggle"> <!-- 属性组名 -->
//                <attr name="isOpen" format="boolean"></attr> <!-- 单个属性名及类型 -->
//            </declare-styleable>
//        </resources>
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.Toggle);
        isOpen = typedArray.getBoolean(R.styleable.Toggle_isOpen, false);
        typedArray.recycle();
        left = isOpen ? max : 0;// 如果开启,left就是max,反之为0
    }

    //通过该方法,输入参考值(32位的二进制数),得到一个最终值.
    //该最终值用来在onMeasure()中最后要设置真实宽高的方法setMeasuredDimension()中作为参数传入.
    private int measureWidthHeight(int measureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(measureSpec);//mode(前2位):外部使用该自定义控件时设置宽高的模式.
        int size = MeasureSpec.getSize(measureSpec);//size(后30位):宽高参考值.
        switch (mode) {
            case MeasureSpec.EXACTLY://使用该自定义控件时,设置的宽或高为match_parent或者固定值.
                //EXACTLY模式下,应当使用宽高参考值:
                result = size;
                break;
            case MeasureSpec.AT_MOST://使用该自定义控件时,设置的宽或高为wrap_content.
            case MeasureSpec.UNSPECIFIED://使用该自定义控件时,设置的宽或高未指定.
                //AT_MOST或UNSPECIFIED模式下,应当计算并使用子元素的宽或高:
                //(1)当该自定义控件是View的子类-单控件: 获取其内容的宽或高即可.
//                result = bm.getHeight(); //bm=BitmapFactory.decodeResource(getResources(),R.drawable.xxx);
                //(2)当该自定义控件是ViewGroup的子类-组控件: 获取子控件的宽或高即可.
                /**
                 * 子控件宽高获取方法:
                 * 控件在完整的绘制到界面上之前,控件及控件内部的子控件,都无法通过getHeight()和getWidth()获取到宽高.
                 * 控件大小是由外而内决定的,外部控件大小没有通过onMeasure()确定,内部子控件大小也无意义.
                 * onMeasure()完成前要用子控件宽高的话,就要调用子控件View的方法measure(int widMeasureSpec, int heightMeasureSpec),
                 * 该方法会回调子控件自身onMeasure()方法测量自身,再调用getMeasuredHeight()和getMeasuredWidth()得到子控件的宽高(Google工程师建议使用该方法测量子控件宽高).
                 * measure(int widMeasureSpec, int heightMeasureSpec)中的参数:
                 * 如果在父控件onMeasure()中,为得出父控件的真实大小的话,则应考虑父控件对子控件宽高的限制,传父控件noMeasure()的参数widMeasureSpec和heightMeasureSpec.
                 * 其他地方,单纯是为了得到子控件不受限制的宽高的情况下,参数可传measure(0,0).
                 */
                break;
        }
        return result;
    }

    //处理事件.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://自定义开关-按下
                isChange = isOpen;
                downTime = SystemClock.uptimeMillis();
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE://自定义开关-滑动
                int moveX = (int) event.getX();
                int dx = moveX - downX;
                left += dx;
                // 越界的处理
                if (left < 0) {
                    left = 0;
                }
                if (left > max) {
                    left = max;
                }
                invalidate();//重绘 调用该方法会重新执行onDraw方法
                downX = moveX;
                break;
            case MotionEvent.ACTION_UP://自定义开关-滑动后抬起
                int upX = (int) event.getX();
                int upY = (int) event.getY();
                // 提高容错率 当手抖的时候 也可以勉强判定为点击操作
                if (SystemClock.uptimeMillis() - downTime < 300 && Math.abs(upX - downX) < 5 && Math.abs(upY - downY) < 5) {// 点击操作
                    //自定义开关-点击操作
                    if (!isOpen) {// 执行打开操作
                        // 判断点击的有效范围
                        if (downX > upBitmap.getWidth() && downX < downBitmap.getWidth()) {
                            // 有效的点击范围 执行打开操作
                            isOpen = true;
                            left = max;
                            invalidate();
                        }
                    } else {// 执行关闭操作
                        // 判断点击的有效范围
                        if (downX > 0 && downX < max) {
                            isOpen = false;
                            left = 0;
                            invalidate();
                        }
                    }
                } else { // 滑动
                    if (left < max / 2) {
                        left = 0;
                        isOpen = false;
                    } else {
                        left = max;
                        isOpen = true;
                    }
                    invalidate();
                }
                // isChange是在down下的时候记录的isOpen的状态.
                // 抬起时,如果isChange != isOpen他们不相等,则表示状态改变了.
                if (listener != null && isChange != isOpen) {
                    listener.onChanged(isOpen);
                }
                break;
            default:
                break;
        }
        return true;// 表示自己处理事件
    }

    /**
     * 获取开关的状态.
     *
     * @return true:打开 false:关闭
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 设置开关的状态.
     *
     * @param isOpen true:打开 false:关闭
     */
    public void setOpen(boolean isOpen) {
        if (isOpen != this.isOpen && listener != null) {
            listener.onChanged(isOpen);
        }
        this.isOpen = isOpen;
        left = isOpen ? max : 0;// 如果开启,left就是max,反之为0
        invalidate();
    }

    /**
     * 设置开关状态改变的监听器.
     *
     * @param listener 监听器
     */
    public void setOnToggleButtonChangeListener(OnToggleButtonChangeListener listener) {
        this.listener = listener;
    }

    /**
     * @Author WCL
     * @Date 2017/6/8 11:16
     * @Version 1.0
     * @Description 自定义开关-打开或关闭后的监听.
     */
    public interface OnToggleButtonChangeListener {
        void onChanged(boolean isOpen);
    }

}