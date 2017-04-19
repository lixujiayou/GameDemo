package com.example.administrator.gamedemo.model.bean;

import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;

import cn.bmob.v3.BmobObject;

/**
 * Created by 大灯泡 on 2017/3/28.
 */

public class LikesInfo extends BmobObject {
    private Students userid;
    private MomentsInfo momentsid;
    private Togther togtherid;


    public LikesInfo() {

    }

    public interface LikesField {
        String USERID = "userid";
        String TOGTHERID = "togtherid";
    }


    public String getUserid() {
        return userid == null ? null : userid.getObjectId();
    }

    public void setUserid(String userid) {
        if (this.userid != null) {
            this.userid.setObjectId(userid);
        } else {
            this.userid = new Students();
            this.userid.setObjectId(userid);
        }
    }

    public Students getUserInfo() {
        return userid;
    }

    public void setUserInfo(Students userid) {
        this.userid = userid;
    }


    public Togther getTogtherid() {
        return togtherid;
    }



    public void setTogtherid(String togtherid) {
        if (this.togtherid != null) {
            this.togtherid.setObjectId(togtherid);
        } else {
            this.togtherid = new Togther();
            this.togtherid.setObjectId(togtherid);
        }
    }
}
