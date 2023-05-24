package com.wcl.testdemo.test.test06_audio_video.test03_opengl.test08.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.utils.YuvUtils;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @Author WCL
 * @Date 2023/5/24 11:12
 * @Version
 * @Description 自定义控件: "按下拍"控件,按下即开始录制视频.
 */
public class RecordButton extends AppCompatTextView {

    private OnRecordListener mListener;

    public RecordButton(Context context) {
        super(context);
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);//selector想要生效,控件的super.onTouchEvent(event)必须被调用到,且控件clickable必须为true或设置点击事件.
        if (mListener == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mListener.onRecordStart();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mListener.onRecordStop();
                ToastUtils.showLong("视频录制路径:\n" + YuvUtils.TEMP_DATA_H264);
                break;
        }
        return true;
    }

    /**
     * 设置录制监听.
     *
     * @param listener
     */
    public void setOnRecordListener(OnRecordListener listener) {
        mListener = listener;
    }

    /**
     * @Author WCL
     * @Date 2023/5/24 11:05
     * @Version
     * @Description 录制监听.
     */
    public interface OnRecordListener {
        void onRecordStart();

        void onRecordStop();
    }

}
