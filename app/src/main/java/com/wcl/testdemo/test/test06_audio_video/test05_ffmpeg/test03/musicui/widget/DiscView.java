package com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.musicui.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wcl.testdemo.R;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.musicui.model.MusicData;
import com.wcl.testdemo.test.test06_audio_video.test05_ffmpeg.test03.musicui.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


/**
 * @Author AchillesL
 * @Date 2016/11/15 17:12
 * @Version
 * @Description 音乐播放器组控件.
 */
public class DiscView extends RelativeLayout {

    /**
     * Comment: 唱针属性动画的持续时长.
     */
    public static final int DURATION_NEEDLE_ANIMATOR = 500;
    /**
     * Comment: 唱针控件.
     */
    private ImageView mIvNeedle;
    /**
     * Comment: 唱片滑动控件.
     */
    private ViewPager mVp;
    /**
     * Comment: 唱片滑动控件的适配器.
     */
    private ViewPagerAdapter mViewPagerAdapter;
    /**
     * Comment: 唱针的属性动画.
     */
    private ObjectAnimator mNeedleAnimator;
    /**
     * Comment: 每张唱片对应的音乐数据的集合.
     */
    private List<MusicData> mMusicDataList = new ArrayList<>();
    /**
     * Comment: 每张唱片布局(ViewPager的每张布局)的集合.
     */
    private List<View> mDiscLayoutList = new ArrayList<>();
    /**
     * Comment: 每张唱片的属性动画的集合.
     */
    private List<ObjectAnimator> mDiscAnimatorList = new ArrayList<>();
    /**
     * Comment: 标记ViewPager是否处于偏移的状态.
     * 只有在ViewPager不处于偏移状态时,才开始唱片旋转动画.
     */
    private boolean mIsViewPagerOffset = false;
    /**
     * Comment: 标记唱针复位后,是否需要重新偏移到唱片处.
     */
    private boolean mIsNeed2StartPlayAnimator = false;
    /**
     * Comment: 当前音乐状态.
     */
    private MusicStatus mMusicStatus = MusicStatus.STOP;
    /**
     * Comment: 当前唱针动画的状态.
     */
    private NeedleAnimatorStatus mNeedleAnimatorStatus = NeedleAnimatorStatus.IN_FAR_END;
    /**
     * Comment: 音乐改变监听.
     */
    private IDiscChangeListener mDiscChangeListener;
    /**
     * Comment: 屏幕宽高像素.
     */
    private int mScreenWidth, mScreenHeight;


    public DiscView(Context context) {
        this(context, null);
    }

    public DiscView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiscView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenWidth = DisplayUtil.getScreenWidth(context);
        mScreenHeight = DisplayUtil.getScreenHeight(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initDiscBackground();
        initViewPager();
        initNeedle();
        initObjectAnimator();
    }

    //初始化唱盘背后半透明的圆形背景控件.
    private void initDiscBackground() {
        ImageView ivDiscBackground = (ImageView) findViewById(R.id.ivDiscBackground);
        ivDiscBackground.setImageDrawable(getDiscBackgroundDrawable());
        int marginTop = (int) (DisplayUtil.SCALE_DISC_MARGIN_TOP * mScreenHeight);
        LayoutParams layoutParams = (LayoutParams) ivDiscBackground.getLayoutParams();
        layoutParams.setMargins(0, marginTop, 0, 0);
        ivDiscBackground.setLayoutParams(layoutParams);
    }

    //初始化唱片滑动控件.
    private void initViewPager() {
        mViewPagerAdapter = new ViewPagerAdapter();
        mVp = (ViewPager) findViewById(R.id.vpDiscContain);
        mVp.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int lastPositionOffsetPixels = 0;
            int currentItem = 0;

            /**
             * 当页面滑动时回调.
             *
             * @param position 页面索引.
             *                 当用手指滑动时,如果手指按在页面上不动,position和当前页面index是一致的;
             *                 翻上页:手指向右拖动（相应页面向左翻动）,这时候position大部分时间和[目标页面]是一致的,只有翻页不成功的情况下最后一次调用才会变为[原页面];
             *                 翻下页:手指向左拖动（相应页面向右翻动）,这时候position大部分时间和[当前页面]是一致的,只有翻页成功的情况下最后一次调用才会变为[目标页面];
             * @param positionOffset 页面偏移比(取值范围:0-1).
             *                       翻上页:从1(不含)递减至0(含);
             *                       翻下页:0到1递增,最后在趋近于1时突变为0;
             * @param positionOffsetPixels 页面偏移像素(取值范围:0-最大值).
             *                             翻上页:从最大值(不含)递减至0(含);
             *                             翻下页:0开始递增,最后在趋近于最大值时突变为0;
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                LogUtils.d("position=" + position, "positionOffset=" + positionOffset, "positionOffsetPixels=" + positionOffsetPixels);
                if (lastPositionOffsetPixels > positionOffsetPixels) {//递减:翻上页.
                    if (positionOffset < 0.5) {//翻页过半:
                        notifyMusicTitleChanged(position);
                    } else {//翻页未过半:
                        notifyMusicTitleChanged(mVp.getCurrentItem());
                    }
                } else if (lastPositionOffsetPixels < positionOffsetPixels) {//递增:翻下页.
                    if (positionOffset > 0.5) {//翻页过半:
                        notifyMusicTitleChanged(position + 1);
                    } else {//翻页未过半:
                        notifyMusicTitleChanged(position);
                    }
                }
                lastPositionOffsetPixels = positionOffsetPixels;
            }

            /**
             * 翻页成功时回调.
             * 初始化时不会调用,翻页过程和翻页失败也不会调用,只有翻页成功才会调用.
             *
             * @param position 当前选中页索引.
             */
            @Override
            public void onPageSelected(int position) {
//                LogUtils.d("position=" + position);
                resetOtherDiscAnimation(position);//取消其他唱片的动画,并复原角度.
                notifyMusicPicChanged(position);//通知更新背景图片.
                if (position > currentItem) {
                    notifyMusicStatusChanged(MusicChangedStatus.NEXT);
                } else {
                    notifyMusicStatusChanged(MusicChangedStatus.LAST);
                }
                currentItem = position;
            }

            /**
             * 滑动状态发生改变时被调用.
             * 当用手指滑动翻页时,手指按下去的时候会触发这个方法state值为1,手指抬起时如果发生了滑动(即使很小)这个值会变为2,然后最后变为0.总共执行这个方法三次.
             * 当setCurrentItem()翻页时,会执行这个方法两次,state值分别为2,0.
             *
             * @param state 当前状态(0:END; 1:PRESS; 2:UP;).
             *
             */
            @Override
            public void onPageScrollStateChanged(int state) {
//                LogUtils.d("state=" + state);
                doWithAnimatorOnPageScroll(state);
            }
        });
        mVp.setAdapter(mViewPagerAdapter);

        LayoutParams layoutParams = (LayoutParams) mVp.getLayoutParams();
        int marginTop = (int) (DisplayUtil.SCALE_DISC_MARGIN_TOP * mScreenHeight);
        layoutParams.setMargins(0, marginTop, 0, 0);
        mVp.setLayoutParams(layoutParams);
    }

    //取消其他页面上唱片的动画，并将图片旋转角度复原.
    private void resetOtherDiscAnimation(int position) {
        for (int i = 0; i < mDiscLayoutList.size(); i++) {
            if (position == i) continue;
            mDiscAnimatorList.get(position).cancel();
            ImageView imageView = (ImageView) mDiscLayoutList.get(i).findViewById(R.id.ivDisc);
            imageView.setRotation(0);
        }
    }

    private void doWithAnimatorOnPageScroll(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
            case ViewPager.SCROLL_STATE_SETTLING: {
                //滑动后手指抬起.
                mIsViewPagerOffset = false;
                if (mMusicStatus == MusicStatus.PLAY) {
                    playAnimator();
                }
                break;
            }
            case ViewPager.SCROLL_STATE_DRAGGING: {
                //开始滑动.
                mIsViewPagerOffset = true;
                pauseAnimator();
                break;
            }
        }
    }

    //初始化唱针控件.
    private void initNeedle() {
        mIvNeedle = (ImageView) findViewById(R.id.ivNeedle);

        int needleWidth = (int) (DisplayUtil.SCALE_NEEDLE_WIDTH * mScreenWidth);
        int needleHeight = (int) (DisplayUtil.SCALE_NEEDLE_HEIGHT * mScreenHeight);

        /*设置手柄的外边距为负数，让其隐藏一部分*/
        int marginTop = (int) (DisplayUtil.SCALE_NEEDLE_MARGIN_TOP * mScreenHeight) * -1;
        int marginLeft = (int) (DisplayUtil.SCALE_NEEDLE_MARGIN_LEFT * mScreenWidth);

        Bitmap originBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_needle);
        Bitmap bitmap = Bitmap.createScaledBitmap(originBitmap, needleWidth, needleHeight, false);

        LayoutParams layoutParams = (LayoutParams) mIvNeedle.getLayoutParams();
        layoutParams.setMargins(marginLeft, marginTop, 0, 0);

        int pivotX = (int) (DisplayUtil.SCALE_NEEDLE_PIVOT_X * mScreenWidth);
        int pivotY = (int) (DisplayUtil.SCALE_NEEDLE_PIVOT_Y * mScreenWidth);

        mIvNeedle.setPivotX(pivotX);
        mIvNeedle.setPivotY(pivotY);
        mIvNeedle.setRotation(DisplayUtil.ROTATION_INIT_NEEDLE);
        mIvNeedle.setImageBitmap(bitmap);
        mIvNeedle.setLayoutParams(layoutParams);
    }

    //初始化唱针的属性动画.
    private void initObjectAnimator() {
        mNeedleAnimator = ObjectAnimator.ofFloat(mIvNeedle, View.ROTATION, DisplayUtil.ROTATION_INIT_NEEDLE, 0);
        mNeedleAnimator.setDuration(DURATION_NEEDLE_ANIMATOR);
        mNeedleAnimator.setInterpolator(new AccelerateInterpolator());
        mNeedleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                /**
                 * 根据动画开始前NeedleAnimatorStatus的状态，
                 * 即可得出动画进行时NeedleAnimatorStatus的状态
                 * */
                if (mNeedleAnimatorStatus == NeedleAnimatorStatus.IN_FAR_END) {
                    mNeedleAnimatorStatus = NeedleAnimatorStatus.TO_NEAR_END;
                } else if (mNeedleAnimatorStatus == NeedleAnimatorStatus.IN_NEAR_END) {
                    mNeedleAnimatorStatus = NeedleAnimatorStatus.TO_FAR_END;
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mNeedleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {
                    mNeedleAnimatorStatus = NeedleAnimatorStatus.IN_NEAR_END;
                    int index = mVp.getCurrentItem();
                    playDiscAnimator(index);
                    mMusicStatus = MusicStatus.PLAY;
                } else if (mNeedleAnimatorStatus == NeedleAnimatorStatus.TO_FAR_END) {
                    mNeedleAnimatorStatus = NeedleAnimatorStatus.IN_FAR_END;
                    if (mMusicStatus == MusicStatus.STOP) {
                        mIsNeed2StartPlayAnimator = true;
                    }
                }
                if (mIsNeed2StartPlayAnimator) {
                    mIsNeed2StartPlayAnimator = false;
                    /**
                     * 只有在ViewPager不处于偏移状态时，才开始唱盘旋转动画
                     * */
                    if (!mIsViewPagerOffset) {
                        /*延时500ms*/
                        DiscView.this.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playAnimator();
                            }
                        }, 50);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void setPlayInfoListener(IDiscChangeListener listener) {
        this.mDiscChangeListener = listener;
    }

    /*得到唱盘背后半透明的圆形背景*/
    private Drawable getDiscBackgroundDrawable() {
        int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_disc_bg), discSize, discSize, false);
        RoundedBitmapDrawable roundDiscDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmapDisc);
        return roundDiscDrawable;
    }

    /**
     * 得到唱盘图片.
     * 唱盘图片由[空心黑圆盘]及[音乐专辑图片]合成得到.
     */
    private Drawable getDiscDrawable(int musicPicRes) {
        int discSize = (int) (mScreenWidth * DisplayUtil.SCALE_DISC_SIZE);
        int musicPicSize = (int) (mScreenWidth * DisplayUtil.SCALE_MUSIC_PIC_SIZE);

        Bitmap bitmapDisc = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_music_disc), discSize, discSize, false);
        Bitmap bitmapMusicPic = getMusicPicBitmap(musicPicSize, musicPicRes);
        BitmapDrawable discDrawable = new BitmapDrawable(bitmapDisc);
        RoundedBitmapDrawable roundMusicDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmapMusicPic);

        //抗锯齿
        discDrawable.setAntiAlias(true);
        roundMusicDrawable.setAntiAlias(true);

        Drawable[] drawables = new Drawable[2];
        drawables[0] = roundMusicDrawable;
        drawables[1] = discDrawable;

        LayerDrawable layerDrawable = new LayerDrawable(drawables);
        int musicPicMargin = (int) ((DisplayUtil.SCALE_DISC_SIZE - DisplayUtil.SCALE_MUSIC_PIC_SIZE) * mScreenWidth / 2);
        //调整专辑图片的四周边距，让其显示在正中.
        layerDrawable.setLayerInset(0, musicPicMargin, musicPicMargin, musicPicMargin, musicPicMargin);

        return layerDrawable;
    }

    private Bitmap getMusicPicBitmap(int musicPicSize, int musicPicRes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeResource(getResources(), musicPicRes, options);
        int imageWidth = options.outWidth;

        int sample = imageWidth / musicPicSize;
        int dstSample = 1;
        if (sample > dstSample) {
            dstSample = sample;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = dstSample;//设置图片采样率
        options.inPreferredConfig = Bitmap.Config.RGB_565;//设置图片解码格式

        return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), musicPicRes, options), musicPicSize, musicPicSize, true);
    }

    public void setMusicDataList(List<MusicData> musicDataList) {
        if (musicDataList.isEmpty()) return;

        mDiscLayoutList.clear();
        mMusicDataList.clear();
        mDiscAnimatorList.clear();
        mMusicDataList.addAll(musicDataList);

        int i = 0;
        for (MusicData musicData : mMusicDataList) {
            View llDisc = LayoutInflater.from(getContext()).inflate(R.layout.layout_disc, mVp, false);

            ImageView ivDisc = (ImageView) llDisc.findViewById(R.id.ivDisc);
            ivDisc.setImageDrawable(getDiscDrawable(musicData.getMusicPicRes()));

            mDiscAnimatorList.add(getDiscObjectAnimator(ivDisc, i++));
            mDiscLayoutList.add(llDisc);
        }
        mViewPagerAdapter.notifyDataSetChanged();

        MusicData musicData = mMusicDataList.get(0);
        if (mDiscChangeListener != null) {
            mDiscChangeListener.onMusicTitleChanged(musicData.getMusicName(), musicData.getMusicAuthor());
            mDiscChangeListener.onMusicPicChanged(musicData.getMusicPicRes());
        }
    }

    private ObjectAnimator getDiscObjectAnimator(ImageView disc, final int i) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(disc, View.ROTATION, 0, 360);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setDuration(20 * 1000);
        objectAnimator.setInterpolator(new LinearInterpolator());
        return objectAnimator;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    /*播放动画*/
    private void playAnimator() {
        /*唱针处于远端时，直接播放动画*/
        if (mNeedleAnimatorStatus == NeedleAnimatorStatus.IN_FAR_END) {
            mNeedleAnimator.start();
        }
        /*唱针处于往远端移动时，设置标记，等动画结束后再播放动画*/
        else if (mNeedleAnimatorStatus == NeedleAnimatorStatus.TO_FAR_END) {
            mIsNeed2StartPlayAnimator = true;
        }
    }

    /*暂停动画*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void pauseAnimator() {
        if (mNeedleAnimatorStatus == NeedleAnimatorStatus.IN_NEAR_END) {//播放时:暂停动画.
            int index = mVp.getCurrentItem();
            pauseDiscAnimator(index);//暂停当前唱片和唱针动画.
        } else if (mNeedleAnimatorStatus == NeedleAnimatorStatus.TO_NEAR_END) {//唱针往唱盘移动时:暂停动画.
            mNeedleAnimator.reverse();//暂停唱针动画.
            mNeedleAnimatorStatus = NeedleAnimatorStatus.TO_FAR_END;//若动画在没结束时执行reverse方法,则不会执行监听器的onStart方法,此时需要手动设置.
        }
        /**
         * 动画可能执行多次，只有音乐处于停止 / 暂停状态时，才执行暂停命令.
         * */
        if (mMusicStatus == MusicStatus.STOP) {
            notifyMusicStatusChanged(MusicChangedStatus.STOP);
        } else if (mMusicStatus == MusicStatus.PAUSE) {
            notifyMusicStatusChanged(MusicChangedStatus.PAUSE);
        }
    }

    /*播放唱盘动画*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void playDiscAnimator(int index) {
        ObjectAnimator objectAnimator = mDiscAnimatorList.get(index);
        if (objectAnimator.isPaused()) {
            objectAnimator.resume();
        } else {
            objectAnimator.start();
        }
        /**
         * 唱盘动画可能执行多次，只有不是音乐不在播放状态，在回调执行播放.
         * */
        if (mMusicStatus != MusicStatus.PLAY) {
            notifyMusicStatusChanged(MusicChangedStatus.PLAY);
        }
    }

    /*暂停唱盘和唱针动画*/
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void pauseDiscAnimator(int index) {
        ObjectAnimator objectAnimator = mDiscAnimatorList.get(index);
        objectAnimator.pause();
        mNeedleAnimator.reverse();
    }

    private void notifyMusicTitleChanged(int position) {
        if (mDiscChangeListener != null) {
            MusicData musicData = mMusicDataList.get(position);
            mDiscChangeListener.onMusicTitleChanged(musicData.getMusicName(), musicData.getMusicAuthor());
        }
    }

    private void notifyMusicPicChanged(int position) {
        if (mDiscChangeListener != null) {
            MusicData musicData = mMusicDataList.get(position);
            mDiscChangeListener.onMusicPicChanged(musicData.getMusicPicRes());
        }
    }

    private void notifyMusicStatusChanged(MusicChangedStatus musicChangedStatus) {
        if (mDiscChangeListener != null) {
            mDiscChangeListener.onMusicStatusChanged(musicChangedStatus);
        }
    }

    public void play() {
        mMusicStatus = MusicStatus.PLAY;
        playAnimator();
    }

    public void pause() {
        mMusicStatus = MusicStatus.PAUSE;
        pauseAnimator();
    }

    public void stop() {
        mMusicStatus = MusicStatus.STOP;
        pauseAnimator();
    }

    public void playOrPause() {
        if (mMusicStatus == MusicStatus.PLAY) {
            pause();
        } else {
            play();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void next() {
        int currentItem = mVp.getCurrentItem();
        if (currentItem == mMusicDataList.size() - 1) {
            Toast.makeText(getContext(), "已经到达最后一首", Toast.LENGTH_SHORT).show();
        } else {
            selectMusicWithButton();
            mVp.setCurrentItem(currentItem + 1, true);
        }
    }

    public void last() {
        int currentItem = mVp.getCurrentItem();
        if (currentItem == 0) {
            Toast.makeText(getContext(), "已经到达第一首", Toast.LENGTH_SHORT).show();
        } else {
            selectMusicWithButton();
            mVp.setCurrentItem(currentItem - 1, true);
        }
    }

//    public boolean isPlaying() {
//        return mMusicStatus == MusicStatus.PLAY;
//    }

    public MusicStatus getMusicStatus() {
        return mMusicStatus;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void selectMusicWithButton() {
        if (mMusicStatus == MusicStatus.PLAY) {
            mIsNeed2StartPlayAnimator = true;
            pauseAnimator();
        } else if (mMusicStatus == MusicStatus.PAUSE) {
            play();
        }
    }

    /**
     * @Author WCL
     * @Date 2023/6/12 17:32
     * @Version
     * @Description 适配器.
     */
    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View discLayout = mDiscLayoutList.get(position);
            container.addView(discLayout);
            return discLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mDiscLayoutList.get(position));
        }

        @Override
        public int getCount() {
            return mDiscLayoutList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    /**
     * @Author WCL
     * @Date 2023/6/12 17:31
     * @Version
     * @Description 接口回调.
     */
    public interface IDiscChangeListener {
        /*用于更新标题栏变化*/
        public void onMusicTitleChanged(String musicName, String musicAuthor);

        /*用于更新背景图片*/
        public void onMusicPicChanged(int musicPicRes);

        /*用于更新音乐播放状态*/
        public void onMusicStatusChanged(MusicChangedStatus musicChangedStatus);
    }

    /**
     * @Author WCL
     * @Date 2023/6/12 17:25
     * @Version
     * @Description 唱针当前所处的状态.
     */
    private enum NeedleAnimatorStatus {
        /**
         * Comment: 移动时-从唱盘往远处移动.
         */
        TO_FAR_END,
        /**
         * Comment: 移动时-从远处往唱盘移动.
         */
        TO_NEAR_END,
        /**
         * Comment: 静止时-离开唱盘.
         */
        IN_FAR_END,
        /**
         * Comment: 静止时-贴近唱盘.
         */
        IN_NEAR_END
    }

    /**
     * @Author WCL
     * @Date 2023/6/12 17:26
     * @Version
     * @Description 音乐当前的状态.
     */
    public enum MusicStatus {
        /**
         * Comment: 播放.
         */
        PLAY,
        /**
         * Comment: 暂停.
         */
        PAUSE,
        /**
         * Comment: 停止.
         */
        STOP
    }

    /**
     * @Author WCL
     * @Date 2023/6/12 17:28
     * @Version
     * @Description DiscView需要触发的音乐切换状态.
     */
    public enum MusicChangedStatus {
        /**
         * Comment: 播放.
         */
        PLAY,
        /**
         * Comment: 暂停.
         */
        PAUSE,
        /**
         * Comment: 下一首.
         */
        NEXT,
        /**
         * Comment: 上一首.
         */
        LAST,
        /**
         * Comment: 停止.
         */
        STOP
    }

}
