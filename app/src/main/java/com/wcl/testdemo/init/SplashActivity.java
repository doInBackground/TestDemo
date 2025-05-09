package com.wcl.testdemo.init;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.blankj.utilcode.util.AppUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.utils.ScreenUtils;

import com.wcl.testdemo.init.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author WCL
 * @Date 2023/10/26 16:38
 * @Version
 * @Description 开屏界面.
 * <p>
 * (1)"res/anim/"文件夹下存放:
 * tween animation(补间动画:AlphaAnimation/RotateAnimation/ScaleAnimation/TranslateAnimation).
 * frame animation(帧动画).
 * xml文件里只有scale、rotate、translate、alpha、set五个标签；
 * 在代码中使用AnimationUtils.loadAnimation()方法加载；
 * 使用View.setAnimation(Animation)为View加载动画；
 * 使用View.startAnimation()开启动画；
 * <p>
 * (2)"res/animator/"文件夹下存放:
 * property animation(属性动画:ObjectAnimator)，
 * xml文件里有animator、objectAnimator和set三个标签；
 * 在代码中使用AnimatorInflater.loadAnimator()方法加载动画；
 * 使用Animation.setTarget(View)为View加载动画；
 * 使用Animation.start()开启动画；
 */
public class SplashActivity extends BaseActivity {

    /**
     * Comment:开屏界面背景图.
     */
    @BindView(R.id.iv_splash_bg)
    ImageView mIvBg;
    /**
     * Comment:开屏界面相机图.
     */
    @BindView(R.id.iv_splash_camera)
    ImageView mIvCamera;
    /**
     * Comment:开屏界面钛马图标(帧动画).
     */
    @BindView(R.id.iv_splash_tm)
    ImageView mIvTM;
    /**
     * Comment:返回当前界面时,是否进入下一个界面.
     */
    private boolean mIsEnter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ScreenUtils.setFullScreen(this);//设置全屏.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        startAnimation();//开始补间动画.
        ((AnimationDrawable) mIvTM.getDrawable()).start();//开始帧动画.
    }

    //开始补间动画.
    private void startAnimation() {
        //初始化动画.
        Animation animationBg = AnimationUtils.loadAnimation(this, R.anim.anim_splash_bg);
        animationBg.setFillAfter(true);
        Animation animationCamera = AnimationUtils.loadAnimation(this, R.anim.anim_splash_camera);
        animationCamera.setFillAfter(true);
        //设置动画监听.
        animationBg.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //mIvBg.clearAnimation();
                animationBg.cancel();
                animationCamera.cancel();
                doEnter();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        //开始播放动画.
        mIvBg.startAnimation(animationBg);
        mIvCamera.startAnimation(animationCamera);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsEnter) {
            mIsEnter = false;
            foregroundEnter();
        }
    }

    /**
     * 进入主界面.
     */
    protected void doEnter() {
        if (AppUtils.isAppForeground()) {//在前台:立即进入.
            foregroundEnter();
        } else {
            mIsEnter = true;
        }
    }

    //前台进入.
    private void foregroundEnter() {
        Intent intent = new Intent(this, TestActivity.class);//设置APP启动界面.
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//启动模式.
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

}