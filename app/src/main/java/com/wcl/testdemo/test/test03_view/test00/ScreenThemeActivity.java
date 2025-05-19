package com.wcl.testdemo.test.test03_view.test00;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LanguageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.utils.MyAppUtils;
import com.wcl.testdemo.utils.ScreenUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.wcl.testdemo.init.BaseActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author WCL
 * @Date 2023/3/27 11:23
 * @Version
 * @Description (1)[沉浸式][全屏][横竖屏] 标题栏-状态栏-底部导航栏:(透明导航状态栏遇到白色背景会看不到)
 * (2)获取系统及屏幕信息.
 * (4)获取屏幕方向.
 * (3)获取状态栏高度.
 */
public class ScreenThemeActivity extends BaseActivity {

    /**
     * Comment: 启动Activity时Intent的键,值为Activity状态.
     * 0: [竖屏-沉浸式]
     * 1: [竖屏-全屏]
     * 2: [横屏-沉浸式]
     * 3: [横屏-全屏]
     */
    public static final String THEME_TYPE = "themeType";
    /**
     * Comment: 用来输出测试结果的控制台.
     */
    @BindView(R.id.tv)
    TextView mTvConsole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);//目的:让应用的界面内容能够延伸到屏幕的边缘.
        setContentView(R.layout.activity_screen_theme);
        ButterKnife.bind(this);
//        ViewCompat.setOnApplyWindowInsetsListener(((ViewGroup)findViewById(android.R.id.content)).getChildAt(0), (v, insets) -> { //findViewById(R.id.main)
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });//目的:避免"setContentView()"的根布局中的子布局(内容布局)被[状态栏]和[挖孔]遮挡.与主题设置中的<item name="android:fitsSystemWindows">true</item>相同.
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        int themeType = intent.getIntExtra(THEME_TYPE, -1);
        switch (themeType) {
            case 0:
                ScreenUtils.setOrientationPortrait(this);//设置传竖屏.
                ScreenUtils.setImmersive(this, true);//沉浸式
                break;
            case 1:
                ScreenUtils.setOrientationPortrait(this);//设置传竖屏.
                ScreenUtils.setFullScreen(this);//全屏
                break;
            case 2:
                ScreenUtils.setOrientationLandscape(this, 0);//设置传感器左右屏横屏.
                ScreenUtils.setImmersive(this, true);//沉浸式
                break;
            case 3:
                ScreenUtils.setOrientationLandscape(this, 0);//设置传感器左右屏横屏.
                ScreenUtils.setFullScreen(this);//全屏
                break;
        }
    }

    @OnClick({R.id.tv_0, R.id.tv_1, R.id.tv_2, R.id.tv_3, R.id.tv_4, R.id.tv_5, R.id.tv_6, R.id.tv_7, R.id.tv_8, R.id.tv_9, R.id.tv_10})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_0://竖屏-沉浸式.
                LogUtils.d("栈顶Activity: " + ActivityUtils.getTopActivity());
                Intent intent0 = new Intent(this, ScreenThemeActivity.class);
                intent0.putExtra(THEME_TYPE, 0);
                startActivity(intent0);
                finish();
                break;
            case R.id.tv_1://竖屏-全屏.
                Intent intent1 = new Intent(this, ScreenThemeActivity.class);
                intent1.putExtra(THEME_TYPE, 1);
                startActivity(intent1);
                finish();
                break;
            case R.id.tv_2://横屏-沉浸式.
                Intent intent2 = new Intent(this, ScreenThemeActivity.class);
                intent2.putExtra(THEME_TYPE, 2);
                startActivity(intent2);
                finish();
                break;
            case R.id.tv_3://横屏-全屏.
                Intent intent3 = new Intent(this, ScreenThemeActivity.class);
                intent3.putExtra(THEME_TYPE, 3);
                startActivity(intent3);
                finish();
                break;
            case R.id.tv_4://获取系统及屏幕信息.
                Resources resources = this.getResources();
                Map<String, Object> map = new HashMap<>();
                map.put("brand", android.os.Build.BRAND);//设备品牌
                map.put("model", android.os.Build.MODEL);//设备型号
                map.put("systemBit", MyAppUtils.is64Bit() ? 64 : 32);
                map.put("system", "Android " + Build.VERSION.RELEASE);//Android版本
                map.put("pixelRatio", resources.getDisplayMetrics().density);//像素比
                map.put("screenWidth", com.blankj.utilcode.util.ScreenUtils.getScreenWidth());
                map.put("screenHeight", com.blankj.utilcode.util.ScreenUtils.getScreenHeight());
                map.put("statusBarHeight", BarUtils.getStatusBarHeight());
                Locale appContextLanguage = LanguageUtils.getAppContextLanguage();
                map.put("language", appContextLanguage.getLanguage() + "_" + appContextLanguage.getCountry());
                //安全区域
//                int[] safeArea = com.pi.util.ScreenUtils.getSafeArea();
//                info.safearea_left = safeArea[0];
//                info.safearea_top = safeArea[1];
//                info.safearea_right = info.screenWidth - safeArea[2];
//                info.safearea_bottom = info.screenHeight - safeArea[3];
//                info.safearea_width = info.safearea_right - info.safearea_left;
//                info.safearea_height = info.safearea_bottom - info.safearea_top;
                mTvConsole.setText(GsonUtils.toJson(map));
                break;
            case R.id.tv_5://获取屏幕方向.
                mTvConsole.setText("屏幕角度: " + ScreenUtils.getDisplayRotation(this));
                break;
            case R.id.tv_6://获取状态栏高度.
                mTvConsole.setText("状态栏高度:" + BarUtils.getStatusBarHeight());
                break;
            case R.id.tv_7:
                break;
            case R.id.tv_8:
                break;
            case R.id.tv_9:
                break;
            case R.id.tv_10:
                break;
        }
    }


}