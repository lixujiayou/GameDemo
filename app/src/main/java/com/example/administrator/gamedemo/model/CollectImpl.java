package com.example.administrator.gamedemo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.administrator.gamedemo.widget.request.AddCollectRequest;
import com.example.administrator.gamedemo.widget.request.AddShareCommentRequest;
import com.example.administrator.gamedemo.widget.request.DeleteCommentRequest;
import com.example.administrator.gamedemo.widget.request.OnResponseListener;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.example.administrator.gamedemo.widget.request.UnCollectRequest;
import com.example.administrator.gamedemo.widget.request.callback.OnCollectChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnCommentChangeCallback;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bmob.v3.exception.BmobException;

/**
 * @auther lixu
 * Created by lixu on 2017/4/16 0016.
 */
public class CollectImpl implements ICollect{
    @Override
    public void addCollect(String momentid, List<Students> collectUserList, final OnCollectChangeCallback onLikeChangeCallback) {
        if(onLikeChangeCallback == null){
            return;
        }
        AddCollectRequest addShareCommentRequest = new AddCollectRequest(momentid,collectUserList);
        addShareCommentRequest.setOnResponseListener(new OnResponseListener<Boolean>() {
            @Override
            public void onStart(int requestType) {

            }

            @Override
            public void onSuccess(Boolean response, int requestType) {
                onLikeChangeCallback.onCollect();
            }

            @Override
            public void onError(BmobException e, int requestType) {

            }

            @Override
            public void onProgress(int pro) {

            }
        });

        addShareCommentRequest.execute();
    }

    @Override
    public void unCollect(String momentid, List<Students> collectUserList, final OnCollectChangeCallback onLikeChangeCallback) {
        if (onLikeChangeCallback == null) return;
        UnCollectRequest deleteCommentRequest = new UnCollectRequest(momentid,collectUserList);
        deleteCommentRequest.setOnResponseListener(new OnResponseListener<Boolean>() {
            @Override
            public void onStart(int requestType) {
            }
            @Override
            public void onSuccess(Boolean response, int requestType) {
                onLikeChangeCallback.onUnCollect();
            }
            @Override
            public void onError(BmobException e, int requestType) {
            }
            @Override
            public void onProgress(int pro) {
            }
        });
        deleteCommentRequest.execute();
    }
}
