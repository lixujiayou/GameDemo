package com.example.administrator.gamedemo.model.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * @auther lixu
 * Created by lixu on 2017/2/16 0016.
 * 每日经文
 */
public class Scriptures extends BmobObject{
    private String sContent; //经文
    private BmobFile sImage;//图片
    private String sNotice;//公告 万不得已不会用
    public Scriptures(){}


    public String getsContent() {
        return sContent;
    }

    public void setsContent(String sContent) {
        this.sContent = sContent;
    }

    public BmobFile getsImage() {
        return sImage;
    }

    public void setsImage(BmobFile sImage) {
        this.sImage = sImage;
    }

    public String getsNotice() {
        return sNotice;
    }

    public void setsNotice(String sNotice) {
        this.sNotice = sNotice;
    }
}
