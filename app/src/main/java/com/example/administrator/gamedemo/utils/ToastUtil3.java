package com.example.administrator.gamedemo.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.gamedemo.R;

/**
 * Created by Administrator on 2016/7/22.
 */

public class ToastUtil3 {
    private static Handler handler = new Handler(Looper.getMainLooper());


    private static Object synObj = new Object();
    private static View view;
    private static String oldMsg;
   // protected static Toast toast   = null;
    private static long oneTime=0;
    private static long twoTime=0;
    private static TextView tv_toast;
    private static Toast toast;

    public static void showToast(Context context, String s){
        if(s.equals("No cache data") || s.contains("No cache data")){
            return;
        }
        showMessage(context,s,Toast.LENGTH_SHORT);
    }


    public static void showToast(Context context, int resId){
        showToast(context, context.getString(resId));
    }

    public static void showMessage(final Context act, final String msg,
                                   final int len) {
        new Thread(new Runnable() {
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (synObj) {
                            if (toast != null) {
//                                toast.cancel();
                                toast.setView(view);
                                toast.setText(msg);
                                toast.setDuration(len);
                            } else {
                                toast = Toast.makeText(act, msg, len);
                                view  = toast.getView();
                            }
                            toast.show();
                        }
                    }
                });
            }
        }).start();
    }

}