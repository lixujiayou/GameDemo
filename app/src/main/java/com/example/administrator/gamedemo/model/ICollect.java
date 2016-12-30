package com.example.administrator.gamedemo.model;

import com.example.administrator.gamedemo.widget.request.callback.OnCollectChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnLikeChangeCallback;

/**
 * @auther lixu
 * Created by lixu on 2016/12/30 0030.
 */
public interface ICollect {
    /**
     * 添加收藏
     */
    void addCollect(String momentid, OnCollectChangeCallback onLikeChangeCallback);

    /**
     * 取消收藏
     */
    void unCollect(String momentid, OnCollectChangeCallback onLikeChangeCallback);

}
