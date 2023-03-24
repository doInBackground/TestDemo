package com.wcl.testdemo.init;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.test.test00_javabase.JavaBaseActivity;
import com.wcl.testdemo.test.test01_androidbase.AndroidBaseActivity;
import com.wcl.testdemo.test.test02_4components.TestComponentsActivity;
import com.wcl.testdemo.test.test03_view.TestViewActivity;
import com.wcl.testdemo.test.test04_device.TestDeviceActivity;
import com.wcl.testdemo.test.test05_library.TestLibraryActivity;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Author WCL
 * @Date 2020/4/27 15:22
 * @Version 1.0
 * @Description 测试Demo的主入口.
 */
public class TestActivity extends AppCompatActivity implements TestAdapter.OnItemClickListener {

    /**
     * Comment:列表即将展示的数据.
     */
    private final ArrayList<TestBean> mList = new ArrayList<>();

    {
        mList.add(new TestBean("(0) Java基础测试."));
        mList.add(new TestBean("(1) Android基础测试."));
        mList.add(new TestBean("(2) 四大组件测试."));
        mList.add(new TestBean("(3) 视图测试. (View; Dialog)"));
        mList.add(new TestBean("(4) 设备功能测试. (传感器; 蓝牙; WIFI)"));
        mList.add(new TestBean("(5) 三方库测试. (插件化; 热更)"));
    }

    @Override
    public void onItemClick(int position, View rootView) {
//        LogUtils.d("点击条目: " + position);
        Intent intent = null;
        switch (position) {
            case 0://Java基础测试.
                intent = new Intent(TestActivity.this, JavaBaseActivity.class);
                break;
            case 1://Android基础测试.
                intent = new Intent(TestActivity.this, AndroidBaseActivity.class);
                break;
            case 2://四大组件测试.
                intent = new Intent(TestActivity.this, TestComponentsActivity.class);
                break;
            case 3://视图测试.
                intent = new Intent(TestActivity.this, TestViewActivity.class);
                break;
            case 4://设备功能测试.
                intent = new Intent(TestActivity.this, TestDeviceActivity.class);
                break;
            case 5://三方库测试.
                intent = new Intent(TestActivity.this, TestLibraryActivity.class);
                break;
            default://其他情况
                intent = null;
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initRcv();//初始化RecyclerView.
    }

    //初始化RecyclerView.
    private void initRcv() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv);//[一].通过id找到布局中的控件.
        TestAdapter adapter = new TestAdapter(TestActivity.this, mList);
        adapter.setOnItemClickListener(this);//条目点击事件监听.
        recyclerView.setAdapter(adapter);//[二].设置适配器(类似于ListView)
        //注意: rcv和lv不一样,需要根据需求设置设置不同种类的LayoutManager.
        //(Ⅰ)LinearLayoutManager(线性管理器):支持[横向][纵向][反转].
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false); //参数2-orientation:方向; 参数3-reverseLayout:翻转布局.
        //(Ⅱ)GridLayoutManager(网格布局管理器):支持反转.
        //GridLayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false); //参数2-spanCount:显示为几列或者几行; 参数3-orientation:方向; 参数4-reverseLayout:翻转布局.
        //(Ⅲ)StaggeredGridLayoutManager(瀑布式布局管理器).
        //StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL); //参数1-spanCount:显示为几列或者几行; 参数2-orientation:方向;
        LinearLayoutManager layoutManager = new LinearLayoutManager(TestActivity.this) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//                return super.generateDefaultLayoutParams();
                //RecyclerView的字条目想要沾满全屏需要在此处重新设置.
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        recyclerView.setLayoutManager(layoutManager);//[三].设置LayoutManager.
    }

}