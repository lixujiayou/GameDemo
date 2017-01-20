package com.example.administrator.gamedemo.model;

import com.example.administrator.gamedemo.widget.request.AddCollectRequest;
import com.example.administrator.gamedemo.widget.request.AddLikeRequest;
import com.example.administrator.gamedemo.widget.request.OnResponseListener;
import com.example.administrator.gamedemo.widget.request.UnCollectRequest;
import com.example.administrator.gamedemo.widget.request.callback.OnCollectChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnLikeChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.UnLikeRequest;

import java.util.List;

import cn.bmob.v3.exception.BmobException;


/**
 * Created by lixu on 2016/12/30.
 * <p>
 * 收藏model
 */

public class CollectImpl implements ICollect {

    @Override
    public void addCollect(String momentid, List<Students> collectUserList,final OnCollectChangeCallback onCollectChangeCallback) {
        if (onCollectChangeCallback == null) return;
        AddCollectRequest request = new AddCollectRequest(momentid,collectUserList);
        request.setOnResponseListener(new OnResponseListener<Boolean>() {
            @Override
            public void onStart(int requestType) {

            }

            @Override
            public void onSuccess(Boolean response, int requestType) {
                if (response) {
                    onCollectChangeCallback.onCollect();
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
    public void unCollect(String momentid,List<Students> collectUserList, final OnCollectChangeCallback onCollectChangeCallback) {
        if (onCollectChangeCallback == null) return;
        UnCollectRequest request = new UnCollectRequest(momentid,collectUserList);
        request.setOnResponseListener(new OnResponseListener<Boolean>() {
            @Override
            public void onStart(int requestType) {

            }

            @Override
            public void onSuccess(Boolean response, int requestType) {
                if (response) {
                    onCollectChangeCallback.onUnCollect();
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
