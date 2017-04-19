package com.example.administrator.gamedemo.widget.request.callback;

import android.text.TextUtils;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.model.bean.LikesInfo;
import com.example.administrator.gamedemo.widget.request.BaseRequestClient;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;


/**
 * Created by lixu on 2016/12/29.
 */
public class UnLikeRequestTogther extends BaseRequestClient<Boolean> {

    private String likeId;

    public UnLikeRequestTogther(String momentsId) {
        this.likeId = momentsId;
    }

    public String getMomentsId() {
        return likeId;
    }

    public UnLikeRequestTogther setMomentsId(String momentsId) {
        this.likeId = momentsId;
        return this;
    }





    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        if (TextUtils.isEmpty(likeId)) {
            onResponseError(new BmobException("取消点赞失败"), requestType);
            return;
        }
        LikesInfo info = new LikesInfo();
        info.setObjectId(likeId);
        info.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                onResponseSuccess(e == null, requestType);
                if(e!= null){
                    Logger.d("取消点赞失败"+e.toString());
                }
            }
        });
    }
}
