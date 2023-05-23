package com.wcl.testdemo.test.test02_4components.test00;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;


/**
 * @Author WCL
 * @Date 2023/3/31 14:18
 * @Version
 * @Description Service.
 */
class MyService extends Service {

    /**
     * Comment: Handler.
     */
    private final Handler mHandler = new Handler();
    /**
     * Comment: 定时任务(模拟播放音乐).
     */
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            ToastUtils.showShort("后台播放歌曲: <<青花瓷>> ..");
            mHandler.postDelayed(this, 3000);
        }
    };

    /**
     * 构造方法.
     */
    public MyService() {
        LogUtils.i("Service生命周期: 构造方法");
    }

    /**
     * [生命周期(1)]
     * 其他组件startService()启动Service时回调该方法.(只会调用一次)
     * Service是系统创建的,且是单例模式,因此onCreate()只有在第一次创建Service时被调用一次.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("Service生命周期: onCreate()");
    }

    /**
     * [生命周期(2)]
     * 其他组件startService()启动Service时回调该方法.(可能会被多次调用)
     *
     * @param intent  intent
     * @param flags   flags
     * @param startId startId
     * @return int
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i("Service生命周期: onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * [生命周期(3)]
     * 其他组件bindService()绑定该Service时被调用.(绑定成功后,只会调用一次)
     * 多次调用bindService()时onBind()只有在第一次时被调用.
     *
     * @param intent intent
     * @return IBinder
     */
    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.i("Service生命周期: onBind()");
        return new MyBinder(); // MyBinder继承Binder,Binder实现了IBinder.多态.
    }

    /**
     * [生命周期(4)]
     * 其他组件unBindService()解绑该Service时被调用.
     * 已经解绑时,再调用unBindService()会崩溃.
     *
     * @param intent intent
     * @return boolean
     */
    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.i("Service生命周期: onUnbind()");
        return super.onUnbind(intent);
    }

    /**
     * [生命周期(5)]
     * 其他组件stopService()销毁Service时回调该方法.(只会调用一次)
     */
    @Override
    public void onDestroy() {
        LogUtils.i("Service生命周期: onDestroy()");
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    //(模拟)开始播放歌曲.
    private void startPlay() {
        mHandler.postDelayed(mRunnable, 0);
    }

    //(模拟)暂停播放歌曲.
    private void pausePlay() {
        mHandler.removeCallbacks(mRunnable);
    }

    /**
     * @Author WCL
     * @Date 2023/3/31 14:26
     * @Version
     * @Description 通讯类.
     * <p>
     * 该私有内部类继承Binder即也间接实现了IBinder，用于在onBind方法执行后返回的对象，该对象对外提供了该Service里的方法.
     */
    private class MyBinder extends Binder implements IDoInBackground {

        @Override
        public void startPlay() {
            MyService.this.startPlay();
        }

        @Override
        public void pausePlay() {
            MyService.this.pausePlay();
        }
    }

}