package com.wcl.testdemo.utils.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.wcl.testdemo.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

/**
 * @Author WCL
 * @Date 2022/9/28 15:07
 * @Version
 * @Description com.blankj.utilcode.util.PermissionUtils辅助类, 用于在申请权限前弹窗提醒用户, 用法如下:
 * new MyDialogFragment().initDialog().show();
 */
public class MyDialogFragment extends DialogFragment {

    private CharSequence title;
    private CharSequence message;
    private CharSequence positiveText;
    private CharSequence negativeText;
    private DialogInterface.OnClickListener positiveListener;
    private DialogInterface.OnClickListener negativeListener;
    private PermissionUtils.OnExplainListener.ShouldRequest shouldRequest;

    /**
     * 初始化弹窗信息.
     *
     * @param message       内容
     * @param shouldRequest 是否继续请求权限
     * @return
     */
    public MyDialogFragment initDialog(CharSequence message, PermissionUtils.OnExplainListener.ShouldRequest shouldRequest) {
        this.message = message;
        this.shouldRequest = shouldRequest;
        return this;
    }

    /**
     * 初始化弹窗信息.
     *
     * @param title            标题
     * @param message          内容
     * @param positiveText     积极键-文字
     * @param positiveListener 积极键-监听
     * @param negativeText     消极键-文字
     * @param negativeListener 消极键-监听
     * @param shouldRequest    是否继续请求权限
     * @return
     */
    public MyDialogFragment initDialog(
            CharSequence title,
            CharSequence message,
            CharSequence positiveText,
            DialogInterface.OnClickListener positiveListener,
            CharSequence negativeText,
            DialogInterface.OnClickListener negativeListener,
            PermissionUtils.OnExplainListener.ShouldRequest shouldRequest
    ) {
        this.title = title;
        this.message = message;
        this.positiveText = positiveText;
        this.negativeText = negativeText;
        this.positiveListener = positiveListener;
        this.negativeListener = negativeListener;
        this.shouldRequest = shouldRequest;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(TextUtils.isEmpty(title) ? "提示" : title)
                .setMessage(message)
                .setPositiveButton(
                        TextUtils.isEmpty(positiveText) ? "好的" : positiveText,
                        positiveListener == null ? new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (shouldRequest != null) {
                                    shouldRequest.start(true);
                                }
                            }
                        } : positiveListener)
                .setNegativeButton(negativeText,
                        negativeListener == null ? new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (shouldRequest != null) {
                                    shouldRequest.start(false);
                                }
                            }
                        } : negativeListener);
        AlertDialog dialog = builder.create();
//        dialog.setCancelable(false);//(无效!点击返回键Dialog依然会取消)
        dialog.setCanceledOnTouchOutside(false);//设置点击外部Dialog不取消.
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return true;//默认false,改为true.点击返回键Dialog不取消.
            }
        });
        return dialog;
    }


    /**
     * 展示DialogFragment.
     *
     * @param context 上下文
     */
    public void show(Context context) {
        String tag = getClass().getSimpleName();
        ThreadUtils.runOnUiThread(new Runnable() {
            @SuppressLint("CommitTransaction")
            @Override
            public void run() {
                FragmentActivity activity = getFragmentActivity(context);
                if (ActivityUtils.isActivityAlive(activity)) {
                    FragmentManager fm = activity.getSupportFragmentManager();
                    Fragment prev = fm.findFragmentByTag(tag);
                    if (prev != null) {
                        fm.beginTransaction().remove(prev);
                    }
                    MyDialogFragment.super.show(fm, tag);
                }
            }
        });
    }

    //获取FragmentActivity.
    private FragmentActivity getFragmentActivity(Context context) {
        Activity activity = ActivityUtils.getActivityByContext(context);
        if (activity == null) return null;
        if (activity instanceof FragmentActivity) {
            return (FragmentActivity) activity;
        }
        LogUtils.w(context + "not instanceof FragmentActivity");
        return null;
    }
}
