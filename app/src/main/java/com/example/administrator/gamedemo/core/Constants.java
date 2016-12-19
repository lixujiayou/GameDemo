package com.example.administrator.gamedemo.core;

import android.content.Context;
import android.content.Intent;

import com.example.administrator.gamedemo.activity.LoginActivity;
import com.example.administrator.gamedemo.model.Students;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2016/12/8 0008.
 */

public class Constants {



    //public static final String BmobId = "7fc6618e5572b09c6055ef4d53d0017a";
    public static final String BmobId = "95246b6418bbbe7f25241c33f8d414be"; //我的
    public static final String LoggerTAG = "qzzzzzzz";
    public static final int  REFRESH_CODE = 24;

    public Constants(){

    }


    public static Constants getInstance(){
        return mConstants.instance;
    }

    public static class mConstants  {
        public static final Constants instance = new Constants();
    }


    public Students getUser(Context mContext){
        Students bmobUser = BmobUser.getCurrentUser(Students.class);
        if(bmobUser == null){
            Intent lIntent = new Intent(mContext, LoginActivity.class);
            return null;
        }

        return bmobUser;
    }
 public Students getUser(){
        Students bmobUser = BmobUser.getCurrentUser(Students.class);


        return bmobUser;
    }

    /**
     * 是否已登录
     * @return
     */
    public boolean isLogin(Context mContext){
        if(getUser(mContext) == null){
            Intent lIntent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(lIntent);
            return false;
        }
        return true;
    }

}
