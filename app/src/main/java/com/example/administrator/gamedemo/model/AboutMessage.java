package com.example.administrator.gamedemo.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * @auther lixu
 * Created by lixu on 2017/1/23 0023.
 */
public class AboutMessage extends BmobObject{

    public static final String SHARE = "share";
    public static final String TOGTHER = "togther";

    private String content;
    private String type;
    private String id;
    private BmobRelation cUsers;
    private BmobRelation mUsers;

    public AboutMessage(){}


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BmobRelation getcUsers() {
        return cUsers;
    }

    public void setcUsers(BmobRelation cUsers) {
        this.cUsers = cUsers;
    }

    public BmobRelation getmUsers() {
        return mUsers;
    }

    public void setmUsers(BmobRelation mUsers) {
        this.mUsers = mUsers;
    }
}