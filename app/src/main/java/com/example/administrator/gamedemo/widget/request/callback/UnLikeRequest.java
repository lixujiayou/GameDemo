package com.example.administrator.gamedemo.widget.request.callback;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.widget.request.BaseRequestClient;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by 大灯泡 on 2016/12/8.
 */
public class UnLikeRequest extends BaseRequestClient<Boolean> {

    private String momentsId;
    private String userid;

    public UnLikeRequest(String momentsId) {
        this.momentsId = momentsId;
        this.userid = Constants.getInstance().getUser().getObjectId();
    }

    public String getMomentsId() {
        return momentsId;
    }

    public UnLikeRequest setMomentsId(String momentsId) {
        this.momentsId = momentsId;
        return this;
    }

    public String getUserid() {
        return userid;
    }

    public UnLikeRequest setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        Share info = new Share();
        info.setObjectId(momentsId);
        Students userInfo = new Students();
        userInfo.setObjectId(userid);
        BmobRelation bmobRelation = new BmobRelation();
        bmobRelation.remove(userInfo);
        info.setLikesBmobRelation(bmobRelation);
        info.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {

                onResponseSuccess(e == null, requestType);
                if(e == null ){
                    Logger.d("取消点赞啦");
                }else{
                    Logger.d("取消点赞失败"+e);
                }

            }
        });

    }
}
