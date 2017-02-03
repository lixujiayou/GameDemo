package com.example.administrator.gamedemo.model;

import android.content.Context;

import cn.bmob.v3.BmobInstallation;

/**
 * @auther lixu
 * Created by lixu on 2017/1/23 0023.
 */
public class MyBmobInstallation extends BmobInstallation {

    /**
     * 用户id-这样可以将设备与用户之间进行绑定
     */
    private String uid;

    public MyBmobInstallation(Context context) {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}