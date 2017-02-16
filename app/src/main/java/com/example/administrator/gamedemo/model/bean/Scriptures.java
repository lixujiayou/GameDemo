package com.example.administrator.gamedemo.model.bean;

import cn.bmob.v3.BmobObject;

/**
 * @auther lixu
 * Created by lixu on 2017/2/16 0016.
 * 每日经文
 */
public class Scriptures extends BmobObject{
    private String sContent;
    public Scriptures(){}


    public String getsContent() {
        return sContent;
    }

    public void setsContent(String sContent) {
        this.sContent = sContent;
    }



}
