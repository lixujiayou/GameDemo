package com.example.administrator.gamedemo.server;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.mine.MessageActivity;
import com.example.administrator.gamedemo.utils.VibratorUtil;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * Created by Administrator on 2016/4/16 0016.
 */
public class MyPushMessageReceiver extends BroadcastReceiver {
 private Context myContent;
    @Override
    public void onReceive(Context context, Intent intent) {

        VibratorUtil.Vibrate(context, 100);   //震动100ms

        myContent =context;
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            Logger.d("客户端收到推送内容：" + intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            try {
                JSONObject jn = new JSONObject(intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
                Log.i("bmob", jn.getString("alert"));
                addNotificaction(jn.getString("alert"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加一个notification
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void addNotificaction(String info) {
        NotificationManager manager = (NotificationManager) myContent
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // 创建一个Notification



        /***
         * notification.contentIntent:一个PendingIntent对象，当用户点击了状态栏上的图标时，该Intent会被触发
         * notification.contentView:我们可以不在状态栏放图标而是放一个view
         * notification.deleteIntent 当当前notification被移除时执行的intent
         * notification.vibrate 当手机震动时，震动周期设置
         */

        // 添加声音提示
       // notification.defaults=Notification.DEFAULT_SOUND;
        // audioStreamType的值必须AudioManager中的值，代表着响铃的模式
      //  notification.audioStreamType= android.media.AudioManager.ADJUST_LOWER;

        //下边的两个方式可以添加音乐
        //notification.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");
        //notification.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");
        Intent intent = new Intent(myContent, MessageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(myContent, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        // 点击状态栏的图标出现的提示信息设置
        Notification notification;

        notification  = new Notification.Builder(myContent)
                .setContentTitle("消息")
                .setContentText(info)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(myContent.getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        manager.notify(1, notification);
    }


}