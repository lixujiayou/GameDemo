package com.example.administrator.gamedemo.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.gamedemo.core.MyApplication;
import com.orhanobut.logger.Logger;


/**
 * Created by 大灯泡 on 2016/10/26.
 *
 * ui工具类
 */
public class UIHelper {

    // =============================================================tools
    // methods

    /**
     * dip转px
     */
    public static int dipToPx(float dip) {
        return (int) (dip * MyApplication.getAppContext().getResources()
                                       .getDisplayMetrics().density + 0.5f);
    }

    /**
     * px转dip
     */
    public static int pxToDip(float pxValue) {
        final float scale = MyApplication.getAppContext().getResources()
                                       .getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将sp值转换为px值
     */
    public static int sp2px(float spValue) {
        final float fontScale = MyApplication.getAppContext().getResources()
                                           .getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕分辨率：宽
     */
    public static int getScreenWidthPix(@Nullable Context context) {
        if (context == null) context = MyApplication.getAppContext();
        int width = context.getResources().getDisplayMetrics().widthPixels;
        return width;
    }

    /**
     * 获取屏幕分辨率：高
     */
    public static int getScreenHeightPix(@Nullable Context context) {
        if (context == null) context = MyApplication.getAppContext();
        int height = context.getResources().getDisplayMetrics().heightPixels;
        return height;
    }

    /**
     * 获取状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
                                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources()
                            .getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 隐藏软键盘
     */
    public static void hideInputMethod(View view) {
        try {
            InputMethodManager imm = (InputMethodManager) view.getContext()
                                                              .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.t(e.toString());
        }
    }

    public static void hideInputMethod(final View view, long delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideInputMethod(view);
            }
        }, delayMillis);
    }

    /**
     * 显示软键盘
     */
//    public static void showInputMethod(View view) {
//        if (view != null && view instanceof EditText) view.requestFocus();
//
//        InputMethodManager imm = (InputMethodManager) view.getContext()
//                                                          .getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
//        }
//    }


    /**
     * 显示软键盘
     */
    public static void showInputMethod(View view) {
        if (view == null) return;
        if (view instanceof EditText) view.requestFocus();
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            boolean success=imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            Logger.d(" isSuccess   >>>   "+success);
        }
    }

    /**
     * 显示软键盘
     */
    public static void showInputMethod(Context context) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 多少时间后显示软键盘
     */
    public static void showInputMethod(final View view, long delayMillis) {
        if (view != null)
        // 显示输入法
        {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UIHelper.showInputMethod(view);
                }
            }, delayMillis);
        }
    }



    /**
     * Toast封装
     */
    public static void ToastMessage(String msg) {
        ToastUtil3.showToast(MyApplication.getAppContext(), msg);
    }

    /**
     * =============================================================
     * 资源工具
     */

    public static int getResourceColor(int colorResId) {
        if (colorResId > 0) {
            return MyApplication.getAppContext()
                              .getResources()
                              .getColor(colorResId);
        } else {
            return Color.TRANSPARENT;
        }
    }

    public static int getScreenWidthPixels(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeightPixels(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    public static float dipToPx(Context context, int dip) {
        return dip * getScreenDensity(context) + 0.5f;
    }

    public static float getScreenDensity(Context context) {
        try {
            DisplayMetrics dm = new DisplayMetrics();
            ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
            return dm.density;
        } catch (Exception e) {
            return DisplayMetrics.DENSITY_DEFAULT;
        }
    }
}
