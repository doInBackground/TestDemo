package com.wcl.testdemo.test.test01_androidbase.test08;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2024/1/9 16:05
 * @Version
 * @Description Dialog测试.
 * 需要测试的内容:
 * (1)测试各种弹窗.
 * (2)控制源生Dialog的宽高和位置(顶部/底部/中间).
 */
public class DialogTestActivity extends AppCompatActivity {

    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_test);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://展示源生AlertDialog.
                showCommonAlertDialog();
                break;
            case R.id.tv_1://测试控制AlertDialog的宽高和位置.
                testDialogWidthHeight();
                break;
            case R.id.tv_2://展示源生AlertDialog-单选对话框.
                showSingleChoiceDialog();
                break;
            case R.id.tv_3://展示源生AlertDialog-多选对话框.
                showMultiChoiceDialog();
                break;
            case R.id.tv_4://展示源生ProgressDialog-(横向)进度条对话框.
                showProgressDialogHorizontal();
                break;
            case R.id.tv_5://展示源生ProgressDialog-(环形)进度条对话框.
                showProgressDialogSpinner();
                break;
            case R.id.tv_6://
                break;
            case R.id.tv_7://
                break;
            case R.id.tv_8://
                break;
            case R.id.tv_9://
                break;
            case R.id.tv_10://
                break;
            case R.id.tv_11://
                break;
            case R.id.tv_12://
                break;
            case R.id.tv_13://
                break;
            case R.id.tv_14://
                break;
            case R.id.tv_15://
                break;
            case R.id.tv_16://
                break;
            case R.id.tv_17://
                break;
            case R.id.tv_18://
                break;
            case R.id.tv_19://
                break;
            case R.id.tv_20://
                break;
        }
    }

    //展示源生ProgressDialog-(环形)进度条对话框.
    public void showProgressDialogSpinner() {
        ProgressDialog dialog = new ProgressDialog(this);//继承AlertDialog.
        dialog.setCancelable(false);//设置点击返回和外部不取消.
        dialog.setMessage("加载中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//设置成环形的进度条.
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {//调用[dialog.setCancelable(false)]或[dialog.dismiss()]后,都不会回调到此.
                LogUtils.d("onCancel");
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                LogUtils.d("onDismiss");
            }
        });
        dialog.show();
        //5s后关闭:
        new Thread(new Runnable() {
            public void run() {
                SystemClock.sleep(5000);
                dialog.dismiss();
            }
        }).start();
    }

    //展示源生ProgressDialog-(横向)进度条对话框.
    public void showProgressDialogHorizontal() {
        ProgressDialog dialog = new ProgressDialog(this);//继承AlertDialog.
        dialog.setTitle("进度条Dialog:");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置成横向的进度条.
        dialog.setMax(100);
        dialog.show();
        //模拟进度数据:
        new Thread(new Runnable() {
            public void run() {
                for (int i = 1; i < 101; i++) {
                    SystemClock.sleep(30);
                    dialog.setProgress(i);//子线程更新界面(所有和进度条相关的控件全部都可以在子线程更新界面,因为它内部自己处理了线程跳转).
                }
            }
        }).start();
    }

    //展示源生AlertDialog-多选对话框.
    public void showMultiChoiceDialog() {
        final String[] items = {"雪玉老师", "苍老师", "加老师", "熊叫兽", "黄日天", "锅良辰"};
        final boolean[] checkedItems = {true, true, false, false, false, false};
        new AlertDialog.Builder(this)
                .setTitle("请选择你最喜欢老师:")
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//取出所有选项
                        StringBuilder sb = new StringBuilder("你最喜欢的老师是: ");
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                sb.append(items[i]).append(" ");
                            }
                        }
                        ToastUtils.showShort(sb);
                    }
                })
                .show();
    }

    //展示源生AlertDialog-单选对话框.
    private void showSingleChoiceDialog() {
        final String[] items = {"张镐老师", "赵日天", "刘诛魔", "李杀神"};
        new AlertDialog.Builder(this)
                .setTitle("请选择你最喜欢老师:")
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast.makeText(DialogTestActivity.this, "你最喜欢的老师是:" + items[which], Toast.LENGTH_SHORT).show();
//                        dialog.dismiss();//关闭对话框
                        ToastUtils.showShort("你最喜欢的老师是:" + items[which]);
                    }
                })
                .show();
    }

    //测试控制AlertDialog的宽高和位置.
    private void testDialogWidthHeight() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(LayoutInflater.from(this).inflate(R.layout.dialog_tips, null))
//                .setCancelable(false)//设置点击返回和外部不取消.
                .create();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //透明背景.
//        window.setBackgroundDrawableResource(R.drawable.shape_rectangle_blue_45a6fd); //设置背景.
        dialog.show();
        //方式一(show()之后):
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(window.getAttributes());
        WindowManager.LayoutParams lp = window.getAttributes();
        //(1)窗体大小:
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT; //300
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT; //500
        //(2)窗体位置: 通过x和y值来设置窗体位置时,需要注意gravity属性,如果gravity没有设置或者是center之类的,那么设置的x和y值就不会起作用.
        lp.gravity = Gravity.BOTTOM;
        lp.x = 100; //X轴上的调整.
        lp.y = 100; //Y轴上的调整.
        window.setAttributes(lp);
        //方式二(show()之后):
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    //展示源生AlertDialog.
    private void showCommonAlertDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)//设置标题的图片.
                .setTitle("对话框标题")//设置对话框的标题.
                .setMessage("对话框内容")//设置对话框的内容.
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//积极键.
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//消极键.
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNeutralButton("中立", new DialogInterface.OnClickListener() {//中立键.
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setCancelable(false)//设置点击返回和外部不取消.
                .show();
        //AlertDialog dialog = builder.create(); //判断Dialog是否正在展示:dialog.isShowing()
    }

    //一键三连,在三个地方输出打印结果.
    private void print(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            LogUtils.d(msg);
            ToastUtils.showShort(msg);
            mTvConsole.setText(msg);
        }
    }

}