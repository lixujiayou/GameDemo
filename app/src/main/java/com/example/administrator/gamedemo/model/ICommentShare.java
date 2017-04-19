package com.example.administrator.gamedemo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.administrator.gamedemo.widget.request.callback.OnCommentChangeCallback;

/**
 * @auther lixu
 * Created by lixu on 2017/4/16 0016.
 */
public interface ICommentShare {

    void addComment(@NonNull Share momentsId,
                    @NonNull Students authorId,
                    @Nullable Students replyUserId,
                    @NonNull String content,
                    @NonNull OnCommentChangeCallback onCommentChangeCallback);

    void deleteComment(@NonNull String commentid, @NonNull final OnCommentChangeCallback onCommentChangeCallback);

}
