package com.example.administrator.gamedemo.utils;

import android.content.Context;
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

    private static String oldMsg;
    protected static Toast toast   = null;
    private static long oneTime=0;
    private static long twoTime=0;
    private static TextView tv_toast;
    public static void showToast(Context context, String s){


        if(toast==null){

            LayoutInflater inflater = LayoutInflater.from(context);
            View toastView = inflater.inflate(R.layout.layout_toast, null);
            tv_toast = (TextView) toastView.findViewById(R.id.tv_toast);

            Toast toast = new Toast(context);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastView);                //设置自定义view
            toast.setGravity(Gravity.CENTER, 0, 0);  //控制显示到屏幕中间
            tv_toast.setText(s);
            toast.show();

//            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
//            toast.show();
            oneTime= System.currentTimeMillis();
        }else{
            twoTime= System.currentTimeMillis();
            if(s.equals(oldMsg)){
                if(twoTime-oneTime> Toast.LENGTH_SHORT){
                    toast.show();
                }
            }else{
                toast.cancel();
                oldMsg = s;
                tv_toast.setText(s);
//                toast.setText(s);
                toast.show();
            }
        }
        oneTime=twoTime;
    }


    public static void showToast(Context context, int resId){
        showToast(context, context.getString(resId));
    }







}