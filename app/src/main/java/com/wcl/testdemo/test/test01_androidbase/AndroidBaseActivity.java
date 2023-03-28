package com.wcl.testdemo.test.test01_androidbase;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.UtilsTransActivity;
import com.wcl.testdemo.R;
import com.wcl.testdemo.utils.FileUtils;
import com.wcl.testdemo.utils.dialog.MyDialogFragment;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/3/24 15:04
 * @Version
 * @Description 测试: Android基础.
 */
public class AndroidBaseActivity extends AppCompatActivity {

    /**
     * Comment: 通过系统文件选择器,选择文件的结果.
     */
    private final int PICK_FILE = 100;
    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_base);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10, R.id.tv_11, R.id.tv_12, R.id.tv_13, R.id.tv_14, R.id.tv_15, R.id.tv_16, R.id.tv_17, R.id.tv_18, R.id.tv_19, R.id.tv_20})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_0://打开系统文件选择器.
                pickFile();
                break;
            case R.id.tv_1://请求[存储]和[定位]权限.
                requestPermission();
                break;
            case R.id.tv_2://
                break;
            case R.id.tv_3://
                break;
            case R.id.tv_4://
                break;
            case R.id.tv_5://
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_FILE://文件选择器结果:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri uri = data.getData();
                        StringBuilder sb = new StringBuilder();
                        if (uri != null) {//(1)选了单个文件:
//                            File tempFile = UriUtils.uri2File(uri);//该工具方法拿不到真实File时,会拷贝一份文件到内部沙箱缓存,再给出拷贝后的路径.
                            File file = FileUtils.copyUri2File(uri,
                                    new File(Utils.getApp().getExternalCacheDir(), "" + System.currentTimeMillis()));
                            sb.append("[")
                                    .append("uri = ")
                                    .append(uri)
                                    .append(", uri-path = ")
                                    .append(uri.getPath())
                                    .append(", 拷贝到: ")
                                    .append(file.getAbsolutePath())
                                    .append("]");
                            print(sb.toString());
                        } else {//(2)选了多个文件:
                            ClipData clipData = data.getClipData();
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                Uri multipleUri = clipData.getItemAt(i).getUri();
                                File file = FileUtils.copyUri2File(multipleUri,
                                        new File(Utils.getApp().getExternalCacheDir(), "" + System.currentTimeMillis()));
                                sb.append("[")
                                        .append(i)
                                        .append("号, uri = ")
                                        .append(multipleUri)
                                        .append(", uri-path = ")
                                        .append(multipleUri.getPath())
                                        .append(", 拷贝到: ")
                                        .append(file.getAbsolutePath())
                                        .append("]\n");
                            }
                            print(sb.toString());
                        }
                    } else {
                        print("文件选择器回传Intent(data)为空");
                    }
                }
                break;
        }
    }

    //一键三连,在三个地方输出打印结果.
    private void print(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            LogUtils.d(msg);
            ToastUtils.showShort(msg);
            mTvConsole.setText(msg);
        }
    }

    //打开系统文件选择器.
    private void pickFile() {
        //action和category都是固定不变的.type属性可以用于对文件类型进行过滤,type属性必须要指定，否则会产生崩溃。
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//是否支持多选文件.
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE);
    }

    //请求[存储]和[定位]权限.
    private void requestPermission() {
        PermissionUtils.permission(PermissionConstants.STORAGE, PermissionConstants.LOCATION)
                .explain(new PermissionUtils.OnExplainListener() {
                    @Override
                    public void explain(@NonNull UtilsTransActivity activity, @NonNull List<String> denied, @NonNull ShouldRequest shouldRequest) {
                        StringBuilder sb = new StringBuilder("\"XXX功能\"")//需依具体情况改变.
                                .append("需要使用到以下权限:\r\n")
                                .append("[存储权限][定位权限]")//需依具体情况改变.
                                .append("\r\n接下来我们即将向您申请这些权限,请您同意以提升您的使用体验.");
                        new MyDialogFragment().initDialog(sb, shouldRequest).show(activity);
                    }
                })
//                .rationale(new PermissionUtils.OnRationaleListener() {//回调时机:第二次及以后被拒.
//                    @Override
//                    public void rationale(@NonNull UtilsTransActivity activity, @NonNull ShouldRequest shouldRequest) {
//                        ToastUtils.showShort("权限又被拒!");
//                        LogUtils.d("权限又被拒!");
//                    }
//                })
                .callback(new PermissionUtils.SingleCallback() {
                    @Override
                    public void callback(boolean isAllGranted, @NonNull List<String> granted, @NonNull List<String> deniedForever, @NonNull List<String> denied) {
                        // isAllGranted:权限是否全部通过.
                        // granted:同意的权限.
                        // deniedForever(权限被永远拒绝了才有内容):被永远拒绝的权限.
                        // denied(只要有被拒绝的权限就有内容):被拒绝的权限.
                        StringBuilder sb = new StringBuilder("权限测试:")
                                .append("\n(1)权限是否全部授予: ")
                                .append(isAllGranted)
                                .append("\n(2)同意的权限: ").append(GsonUtils.toJson(granted))
                                .append("\n(3)默认拒绝的权限: ").append(GsonUtils.toJson(deniedForever))
                                .append("\n(4)本次拒绝的所有权限: ").append(GsonUtils.toJson(denied));
                        print(sb.toString());
                        if (isAllGranted) {//权限全部同意.
//                            ToastUtils.showShort("权限通过");
                        } else {
                            if (deniedForever.size() > 0) {//表示用户点击了"拒绝且不再询问",这时需要跳转设置页允许权限.
//                                ToastUtils.showShort("权限被默认拒绝,功能受限");
                                setPermissionDialog();
                            } else {//表示用户仅点击了"拒绝",不要再弹窗了以免引起用户反感.
//                                ToastUtils.showShort("权限被拒,功能受限");
                            }
                        }
                    }
                })
                .request();
    }


    //前往"设置"界面开启权限的提示.
    private void setPermissionDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)//标题图标.
                .setTitle("权限异常")//标题.
                .setMessage("我们检测到相关权限被您默认拒绝,如确需使用该功能,请前往\"设置\"给予应用相关权限")//内容.
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//积极键.
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PermissionUtils.launchAppDetailsSettings();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//消极键.
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        ToastUtils.showShort("权限被拒,功能受限");
                    }
                })
//                .setNeutralButton("按钮", new DialogInterface.OnClickListener() {//中立键.
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                    }
//                })
                .setCancelable(false)//设置点击返回和外部不取消.
                .show();
    }

}