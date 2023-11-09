package com.wcl.testdemo.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.blankj.utilcode.util.LogUtils;
import com.wcl.testdemo.init.TestActivity;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageService;

/**
 * @Author WCL
 * @Date 2023/10/20 10:59
 * @Version
 * @Description 从 JPush 5.2.0 开始，需要配置继承 JPushMessageService 的广播,原来如果配了 MyReceiver和JPushMessageReceiver 现在可以弃用.
 */
public class PushMessageService extends JPushMessageService {

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        LogUtils.d("推送测试: [onMessage] " + customMessage);
        Intent intent = new Intent("com.jiguang.demo.message");
        intent.putExtra("msg", customMessage.message);
        context.sendBroadcast(intent);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        LogUtils.d("推送测试: [onNotifyMessageOpened] " + message);
        try {
            //打开自定义的Activity
            Intent i = new Intent(context, TestActivity.class);//设置APP启动界面.
            Bundle bundle = new Bundle();
            bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, message.notificationTitle);
            bundle.putString(JPushInterface.EXTRA_ALERT, message.notificationContent);
            i.putExtras(bundle);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        } catch (Throwable throwable) {

        }
    }

    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        LogUtils.d("推送测试: [onMultiActionClicked] 用户点击了通知栏按钮");
        String nActionExtra = intent.getExtras().getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA);

        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        if (nActionExtra == null) {
            LogUtils.d("推送测试: ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null");
            return;
        }
        if (nActionExtra.equals("my_extra1")) {
            LogUtils.d("推送测试: [onMultiActionClicked] 用户点击通知栏按钮一");
        } else if (nActionExtra.equals("my_extra2")) {
            LogUtils.d("推送测试: [onMultiActionClicked] 用户点击通知栏按钮二");
        } else if (nActionExtra.equals("my_extra3")) {
            LogUtils.d("推送测试: [onMultiActionClicked] 用户点击通知栏按钮三");
        } else {
            LogUtils.d("推送测试: [onMultiActionClicked] 用户点击通知栏按钮未定义");
        }
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {//收到推送消息时被回调.
        LogUtils.d("推送测试: [onNotifyMessageArrived] " + message);
//        //不需要手动设置角标,通过推送后台可以配置角标模式,一般用"自增+1"模式即可.
//        int oldBadgeNumber = SPStaticUtils.getInt("BadgeNumber", 0);//旧的角标数量.
//        int currentBadgeNumber = oldBadgeNumber + 1;//新角标数量.
//        SPStaticUtils.put("BadgeNumber", currentBadgeNumber);//记录新角标数量.
//        JPushInterface.setBadgeNumber(this, currentBadgeNumber);//设置新角标数量(仅支持华为平台).
//        LogUtils.d("推送测试: 当前应该设置角标数量:" + currentBadgeNumber);
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        LogUtils.d("推送测试: [onNotifyMessageDismiss] " + message);
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        LogUtils.d("推送测试: [onRegister] " + registrationId);
        Intent intent = new Intent("com.jiguang.demo.message");
        intent.putExtra("rid", registrationId);
        context.sendBroadcast(intent);
    }

    @Override
    public void onConnected(Context context, boolean isConnected) {
        LogUtils.d("推送测试: [onConnected] " + isConnected);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        LogUtils.d("推送测试: [onCommandResult] " + cmdMessage);
        //cmd 为 2008 时说明为应用冷启动后，SDK 首次初始化成功的回调(只回调一次).
        //cmd 为 10000 时说明为厂商 token 回调.
        if (cmdMessage != null && cmdMessage.cmd == 10000 && cmdMessage.extra != null) {
            String token = cmdMessage.extra.getString("token");
            int platform = cmdMessage.extra.getInt("platform");
            String deviceName = "unkown";
            switch (platform) {
                case 1:
                    deviceName = "小米";
                    break;
                case 2:
                    deviceName = "华为";
                    break;
                case 3:
                    deviceName = "魅族";
                    break;
                case 4:
                    deviceName = "OPPO";
                    break;
                case 5:
                    deviceName = "VIVO";
                    break;
                case 6:
                    deviceName = "ASUS";
                    break;
                case 7:
                    deviceName = "荣耀";
                    break;
                case 8:
                    deviceName = "FCM";
                    break;
            }
            LogUtils.d("推送测试: 获取到" + deviceName + "的token:" + token);
        }
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    @Override
    public void onNotificationSettingsCheck(Context context, boolean isOn, int source) {
        super.onNotificationSettingsCheck(context, isOn, source);
        LogUtils.d("推送测试: [onNotificationSettingsCheck] isOn:" + isOn + ",source:" + source);
    }

}
