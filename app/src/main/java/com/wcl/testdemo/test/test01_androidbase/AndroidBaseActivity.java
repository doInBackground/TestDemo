package com.wcl.testdemo.test.test01_androidbase;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.utils.FileUtils;

import java.io.File;

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
            case R.id.tv_1://
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

}