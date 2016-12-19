package com.example.administrator.gamedemo.model;


import com.example.administrator.gamedemo.widget.request.callback.OnLikeChangeCallback;

/**
 * Created by 大灯泡 on 2016/12/6.
 */

public interface ILike {


    /**
     * 添加点赞
     */
    void addLike(String momentid, OnLikeChangeCallback onLikeChangeCallback);

    /**
     * 移除点赞
     */
    void unLike(String momentid, OnLikeChangeCallback onLikeChangeCallback);
}
