package com.example.administrator.gamedemo.model.impl;

import com.example.administrator.gamedemo.model.ICollect;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.widget.request.AddCollectRequest;
import com.example.administrator.gamedemo.widget.request.AddMessageRequest;
import com.example.administrator.gamedemo.widget.request.DeleteMessageRequest;
import com.example.administrator.gamedemo.widget.request.OnResponseListener;
import com.example.administrator.gamedemo.widget.request.UnCollectRequest;
import com.example.administrator.gamedemo.widget.request.callback.OnCollectChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnMessageCallback;

import java.util.List;

import cn.bmob.v3.exception.BmobException;


/**
 * Created by lixu on 2016/12/30.
 * <p>
 * Message model
 */

public class MessageImpl implements IMessage {

    @Override
    public void addMessage(String userId, String rId, String type, String content, final OnMessageCallback onMessageCallback) {
        if (onMessageCallback == null) return;
        AddMessageRequest request = new AddMessageRequest( userId, rId, type, content);
        request.setOnResponseListener(new OnResponseListener<Boolean>() {
            @Override
            public void onStart(int requestType) {

            }

            @Override
            public void onSuccess(Boolean response, int requestType) {
                if (response) {
                    onMessageCallback.onMessage();
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
    public void unMessage(String messageId, final  OnMessageCallback onMessageCallback) {
        if (onMessageCallback == null) return;
        DeleteMessageRequest request = new DeleteMessageRequest(messageId);
        request.setOnResponseListener(new OnResponseListener<Boolean>() {
            @Override
            public void onStart(int requestType) {
            }

            @Override
            public void onSuccess(Boolean response, int requestType) {
                if (response) {
                    onMessageCallback.onUnMessage();
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
