package com.example.administrator.gamedemo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.administrator.gamedemo.widget.request.AddCommentRequest;
import com.example.administrator.gamedemo.widget.request.DeleteCommentRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.example.administrator.gamedemo.widget.request.callback.OnCommentChangeCallback;


/**
 * Created by lixu on 2016/12/27.
 * <p>
 * 评论Model
 */

public class CommentImpl implements IComment {
    @Override
    public void addComment(@NonNull Share momentid,
                           @NonNull Students authorid,
                           @Nullable Students replyUserId,
                           @NonNull String content,
                           @NonNull final OnCommentChangeCallback onCommentChangeCallback) {
        if (onCommentChangeCallback == null) return;
        AddCommentRequest addCommentRequest = new AddCommentRequest();
        addCommentRequest.setContent(content);
        addCommentRequest.setMomentsInfoId(momentid);
        addCommentRequest.setAuthorId(authorid);
        addCommentRequest.setReplyUserId(replyUserId);
        addCommentRequest.setOnResponseListener(new SimpleResponseListener<CommentInfo>() {
            @Override
            public void onSuccess(CommentInfo response, int requestType) {
                onCommentChangeCallback.onAddComment(response);
            }

            @Override
            public void onProgress(int pro) {

            }
        });
        addCommentRequest.execute();
    }

    @Override
    public void deleteComment(@NonNull String commentid, @NonNull final OnCommentChangeCallback onCommentChangeCallback) {
        if (onCommentChangeCallback == null) return;
        DeleteCommentRequest deleteCommentRequest = new DeleteCommentRequest();
        deleteCommentRequest.setCommentid(commentid);
        deleteCommentRequest.setOnResponseListener(new SimpleResponseListener<String>() {
            @Override
            public void onSuccess(String response, int requestType) {
                onCommentChangeCallback.onDeleteComment(response);
            }

            @Override
            public void onProgress(int pro) {

            }
        });
        deleteCommentRequest.execute();

    }
}
