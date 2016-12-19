package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lixu on 2016/12/13.
 */

// FIXME: 2016/12/13 如果不怕嵌套地狱，可以考虑返回当前点赞列表，然而在下不干了。。。
public class AddLikeRequest extends BaseRequestClient<Boolean> {

    private String momentsId;
    private String userid;
    private Students cUser;
    public AddLikeRequest(String momentsId) {
        this.momentsId = momentsId;
        cUser = BmobUser.getCurrentUser(Students.class);
        this.userid = cUser.getObjectId();
    }

    public String getMomentsId() {
        return momentsId;
    }

    public AddLikeRequest setMomentsId(String momentsId) {
        this.momentsId = momentsId;
        return this;
    }

    public String getUserid() {
        return userid;
    }

    public AddLikeRequest setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        Share info = new Share();
        info.setObjectId(momentsId);
        Students userInfo = new Students();
        userInfo.setObjectId(userid);
        info.setLikesBmobRelation(new BmobRelation(userInfo));
        info.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Logger.d("点赞成功");
                }
                Logger.d("点赞失败"+e);
                onResponseSuccess(e == null, requestType);
            }
        });

    }
}
