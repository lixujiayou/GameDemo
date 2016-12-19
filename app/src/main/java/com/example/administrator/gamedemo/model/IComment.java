package com.example.administrator.gamedemo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.administrator.gamedemo.widget.request.callback.OnCommentChangeCallback;


/**
 * Created by 大灯泡 on 2016/12/6.
 */

public interface IComment {


    /**
     * 添加评论
     */
    void addComment(@NonNull Share momentsId,
                    @NonNull Students authorId,
                    @Nullable Students replyUserId,
                    @NonNull String content,
                    @NonNull OnCommentChangeCallback onCommentChangeCallback);

    void deleteComment(@NonNull String commentid, @NonNull final OnCommentChangeCallback onCommentChangeCallback);
}
