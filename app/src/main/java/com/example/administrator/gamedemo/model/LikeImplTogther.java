package com.example.administrator.gamedemo.model;

import com.example.administrator.gamedemo.widget.request.AddLikeRequestTogther;
import com.example.administrator.gamedemo.widget.request.OnResponseListener;
import com.example.administrator.gamedemo.widget.request.callback.OnLikeChangeCallback;

import com.example.administrator.gamedemo.widget.request.callback.UnLikeRequestTogther;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.exception.BmobException;


/**
 * Created by lixu on 2016/12/29.
 * <p>
 * 点赞model
 */

public class LikeImplTogther implements ILike {

    @Override
    public void addLike(String momentid, final OnLikeChangeCallback onLikeChangeCallback) {
        if (onLikeChangeCallback == null) return;
        AddLikeRequestTogther request = new AddLikeRequestTogther(momentid);

        request.setOnResponseListener(new OnResponseListener<String>() {
            @Override
            public void onStart(int requestType) {

            }

            @Override
            public void onSuccess(String response, int requestType) {
                if (response != null) {
                    onLikeChangeCallback.onLike(response);
                }
            }

            @Override
            public void onError(BmobException e, int requestType) {

            }

            @Override
            public void onProgress(int pro) {

            }
        });
        request.execute();
    }

    @Override
    public void unLike(String momentid, final OnLikeChangeCallback onLikeChangeCallback) {
        if (onLikeChangeCallback == null) return;
        UnLikeRequestTogther request = new UnLikeRequestTogther(momentid);
        request.setOnResponseListener(new OnResponseListener<Boolean>() {
            @Override
            public void onStart(int requestType) {
            }

            @Override
            public void onSuccess(Boolean response, int requestType) {
                if (response) {
                    onLikeChangeCallback.onUnLike();
                }
            }

            @Override
            public void onError(BmobException e, int requestType) {

            }

            @Override
            public void onProgress(int pro) {

            }
        });
        request.execute();
    }
}
