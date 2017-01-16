package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by lixu on 2016/12/8.
 */
public class UnCollectRequest extends BaseRequestClient<Boolean> {

    private String momentsId;
    private String userid;

    public UnCollectRequest(String momentsId) {
        this.momentsId = momentsId;
        this.userid = Constants.getInstance().getUser().getObjectId();
    }

    public String getMomentsId() {
        return momentsId;
    }

    public UnCollectRequest setMomentsId(String momentsId) {
        this.momentsId = momentsId;
        return this;
    }

    public String getUserid() {
        return userid;
    }

    public UnCollectRequest setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {

        Share info = new Share();
        info.setObjectId(momentsId);

        Students userInfo = new Students();
        userInfo.setObjectId(userid);

        info.removeCollect(userInfo);

        info.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                onResponseSuccess(e == null, requestType);
                if(e == null ){
                    Logger.d("取消收藏啦");
                }else{
                    Logger.d("取消收藏失败"+e);
                }

            }
        });

    }
}
