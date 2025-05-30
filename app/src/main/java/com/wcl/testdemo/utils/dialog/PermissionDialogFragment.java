package com.wcl.testdemo.utils.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

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
public class PermissionDialogFragment extends DialogFragment {

    private CharSequence message;
    private PermissionUtils.OnExplainListener.ShouldRequest shouldRequest;

    /**
     * 初始化弹窗信息.
     *
     * @param message       内容
     * @param shouldRequest 是否继续请求权限
     * @return
     */
    public PermissionDialogFragment initDialog(CharSequence message, PermissionUtils.OnExplainListener.ShouldRequest shouldRequest) {
        this.message = message;
        this.shouldRequest = shouldRequest;
        return this;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_bt1, null);
        TextView tvDialogTitle = rootView.findViewById(R.id.tv_dialog_title);//标题.
        tvDialogTitle.setText("权限用途告知");
        TextView tvDialogContent = rootView.findViewById(R.id.tv_dialog_content);//内容.
        tvDialogContent.setText(message);
        rootView.findViewById(R.id.tv_dialog_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldRequest != null) {
                    shouldRequest.start(true);
                }
            }
        });
        //构建Dialog.
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(rootView) // 设置自定义布局
                .create();
        dialog.setCanceledOnTouchOutside(false);//设置点击外部Dialog不取消.
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return true;//默认false,改为true.点击返回键Dialog不取消.
            }
        });
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //透明背景.
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
                    PermissionDialogFragment.super.show(fm, tag);
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
