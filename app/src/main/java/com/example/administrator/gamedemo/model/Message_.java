package com.example.administrator.gamedemo.model;

import cn.bmob.v3.BmobObject;

/**
 * @auther lixu
 * Created by lixu on 2017/2/16 0016.
 * 系统消息
 */
public class Message_ extends BmobObject{
    private String mContent;
    public Message_(){}

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }
}
