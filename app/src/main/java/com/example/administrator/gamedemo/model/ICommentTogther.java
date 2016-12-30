package com.example.administrator.gamedemo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.administrator.gamedemo.widget.request.callback.OnCommentChangeCallback;


/**
 * Created by lixu on 2016/12/27.
 */

public interface ICommentTogther {


    /**
     * 添加评论
     */
    void addComment(@NonNull Togther momentsId,
                    @NonNull Students authorId,
                    @Nullable Students replyUserId,
                    @NonNull String content,
                    @NonNull OnCommentChangeCallback onCommentChangeCallback);

    void deleteComment(@NonNull String commentid, @NonNull final OnCommentChangeCallback onCommentChangeCallback);
}
