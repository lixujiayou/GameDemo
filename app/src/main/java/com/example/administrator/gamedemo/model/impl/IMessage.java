package com.example.administrator.gamedemo.model.impl;

import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.widget.request.callback.OnCollectChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnMessageCallback;

import java.util.List;

/**
 * @auther lixu
 * Created by lixu on 2016/1/23 0030.
 */
public interface IMessage {

    /**
     * 通知消息
     */
    void addMessage(String userId,String rId,String type , String content,OnMessageCallback onMessageCallback);

    /**
     * 删除消息
     */
    void unMessage(String userId,String rId,String type , String content,OnMessageCallback onMessageCallback);

}
