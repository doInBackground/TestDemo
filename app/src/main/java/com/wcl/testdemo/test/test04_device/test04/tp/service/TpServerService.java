package com.wcl.testdemo.test.test04_device.test04.tp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;

import com.blankj.utilcode.util.LogUtils;
import com.wcl.testdemo.R;
import com.wcl.testdemo.init.TestActivity;

/**
 * @Author WCL
 * @Date 2023/4/27 15:37
 * @Version
 * @Description 投屏服务端:后台服务.
 */
public class TpServerService extends Service {

    public static final String INTENT_KEY_RESULT_CODE = "resultCode";
    public static final String INTENT_KEY_INTENT = "intent";

    /**
     * Comment:屏幕录制器的管理器.
     */
    private MediaProjectionManager mMediaProjectionManager;
    /**
     * Comment:投屏服务端-管理器.
     */
    private TpServerManager mTpServerManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int resultCode = intent.getIntExtra(INTENT_KEY_RESULT_CODE, -1);//录屏请求的返回信息resultCode.
        Intent resultData = intent.getParcelableExtra(INTENT_KEY_INTENT);//录屏请求的返回信息intent.
        startProject(resultCode, resultData);//准备开始录屏.
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mTpServerManager.close();
        super.onDestroy();
    }

    // 录屏开始后进行编码推流.
    private void startProject(int resultCode, Intent data) {
        MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
        if (mediaProjection == null) {
            return;
        }
        LogUtils.d("MediaProjection录屏器获取成功!准备开启服务器,将录屏数据编码并推流.");
        mTpServerManager = new TpServerManager();//创建投屏服务端-管理器.
        mTpServerManager.start(mediaProjection);//开始录屏,并编码发送.
    }

    //后台服务通知栏.
    private void createNotificationChannel() {
        Notification.Builder builder = new Notification.Builder(this.getApplicationContext());
        Intent intent = new Intent(this, TestActivity.class);
        PendingIntent pendingIntent;//PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        builder.setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher)) // 设置下拉列表中的图标(大图标)
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("is running......") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
            // 前台服务notification适配
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_SOUND; // 设置为默认通知音
        startForeground(110, notification);
    }

}