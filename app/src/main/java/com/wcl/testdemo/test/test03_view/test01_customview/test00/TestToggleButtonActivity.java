package com.wcl.testdemo.test.test03_view.test01_customview.test00;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.wcl.testdemo.R;

import com.wcl.testdemo.init.BaseActivity;

/**
 * @Author WCL
 * @Date 2023/6/27 18:14
 * @Version
 * @Description 测试自定义控件ToggleButton的界面.
 */
public class TestToggleButtonActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_toggle_button);
        //找控件.
        ToggleButton tbt = findViewById(R.id.tbt);
        TextView tv0 = findViewById(R.id.tv_0);
        TextView tv1 = findViewById(R.id.tv_1);
        TextView tv2 = findViewById(R.id.tv_2);

        //初始化控件.
        tbt.setOnToggleButtonChangeListener(new ToggleButton.OnToggleButtonChangeListener() {
            @Override
            public void onChanged(boolean isOpen) {
                ToastUtils.showShort("当前开关状态: " + (isOpen ? "开" : "关"));
            }
        });
        tv0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tbt.setOpen(true);
            }
        });
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tbt.setOpen(false);
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.showShort("当前开关状态: " + (tbt.isOpen() ? "开" : "关"));
            }
        });
    }

}