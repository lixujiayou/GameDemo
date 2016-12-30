package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lixu on 2016/12/30.
 */

//
public class AddCollectRequest extends BaseRequestClient<Boolean> {

    private String momentsId;
    private String userid;
    private Students cUser;
    public AddCollectRequest(String momentsId) {
        this.momentsId = momentsId;
        cUser = BmobUser.getCurrentUser(Students.class);
        this.userid = cUser.getObjectId();
    }

    public String getMomentsId() {
        return momentsId;
    }

    public AddCollectRequest setMomentsId(String momentsId) {
        this.momentsId = momentsId;
        return this;
    }

    public String getUserid() {
        return userid;
    }

    public AddCollectRequest setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        Share info = new Share();
        info.setObjectId(momentsId);

        BmobRelation relation = new BmobRelation();
        //将当前Share添加到多对多关联中
        relation.add(info);


        Students userInfo = new Students();
        userInfo.setObjectId(userid);

        userInfo.setCollects(relation);

        userInfo.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Logger.d("收藏成功");
                }else {
                    Logger.d("收藏失败" + e);
                }
                onResponseSuccess(e == null, requestType);
            }
        });

    }
}
