package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lixu on 2016/12/29.
 * 一起的点赞request
 */

// FIXME: 2016/12/13 如果不怕嵌套地狱，可以考虑返回当前点赞列表，然而在下不干了。。。
public class AddLikeRequestTogther extends BaseRequestClient<Boolean> {

    private String momentsId;
    private String userid;
    private Students cUser;
    public AddLikeRequestTogther(String momentsId) {
        this.momentsId = momentsId;
        cUser = Constants.getInstance().getUser();
        if(cUser != null){
            this.userid = cUser.getObjectId();
        }

    }

    public String getMomentsId() {
        return momentsId;
    }

    public AddLikeRequestTogther setMomentsId(String momentsId) {
        this.momentsId = momentsId;
        return this;
    }

    public String getUserid() {
        return userid;
    }

    public AddLikeRequestTogther setUserid(String userid) {
        this.userid = userid;
        return this;
    }

    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        Togther info = new Togther();
        info.setObjectId(momentsId);
        Students userInfo = new Students();
        userInfo.setObjectId(userid);
        info.setLikesBmobRelation(new BmobRelation(userInfo));
        info.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    Logger.d("点赞成功");
                }else {
                    Logger.d("点赞失败" + e);
                }
                onResponseSuccess(e == null, requestType);
            }
        });

    }
}
