package com.example.administrator.gamedemo.widget.request;

import android.util.Log;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.model.bean.LikesInfo;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lixu on 2016/12/29.
 * 一起的点赞request
 */

// FIXME: 2016/12/13 如果不怕嵌套地狱，可以考虑返回当前点赞列表，然而在下不干了。。。
public class AddLikeRequestTogther extends BaseRequestClient<String> {

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
        LikesInfo info =new LikesInfo();
        info.setTogtherid(momentsId);
        info.setUserid(userid);
        info.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                Logger.d("点赞成功后返回"+s);
                onResponseSuccess(s, requestType);
            }
        });
    }
}
