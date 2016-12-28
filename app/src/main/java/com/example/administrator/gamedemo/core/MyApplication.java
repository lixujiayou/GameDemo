package com.example.administrator.gamedemo.core;

import android.app.Application;
import android.content.Context;

import com.example.administrator.gamedemo.model.Students;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;


/**
 * Created by Administrator on 2016/12/8 0008.
 */

public class MyApplication extends Application{
    private static Context CONTEXT;
    private static MyApplication myApplication = new MyApplication();
    public static MyApplication getInstance(){
        return myApplication;
    }

    public Students getCurrentUser() {

        Students user = cn.bmob.v3.BmobUser.getCurrentUser(Students.class);
        if(user!=null){
            return user;
        }
        return null;
    }

public static Context getAppContext() {
    return CONTEXT;
}
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        MyApplication.CONTEXT = getApplicationContext();
        initBmob();
        //初始化Logger
        Logger.init(Constants.LoggerTAG);
    }

    private void initBmob() {
        BmobConfig config = new BmobConfig.Builder(this)
                //设置appkey
                .setApplicationId(Constants.BmobId)
                //请求超时时间（单位为秒）：默认10s
                .setConnectTimeout(10)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024 * 1024)
                //文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(1800)
                .build();
        Bmob.initialize(config);
    }



}
