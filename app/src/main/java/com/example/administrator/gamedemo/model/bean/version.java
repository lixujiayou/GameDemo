package com.example.administrator.gamedemo.model.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2016/5/25 0025.
 * 版本
 */
public class version extends BmobObject{

    private BmobFile apk;   //apk
    private int version_num;//版本号
    private String updateInfo; //更新详情

    public version() {
    }

    public BmobFile getApk() {
        return apk;
    }

    public void setApk(BmobFile apk) {
        this.apk = apk;
    }

    public int getVersion_num() {
        return version_num;
    }

    public void setVersion_num(int version_num) {
        this.version_num = version_num;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }
}
