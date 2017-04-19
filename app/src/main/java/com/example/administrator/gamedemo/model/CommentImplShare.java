package com.example.administrator.gamedemo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.administrator.gamedemo.utils.presenter.IBasePresenter;
import com.example.administrator.gamedemo.utils.presenter.ISharePresenter;
import com.example.administrator.gamedemo.utils.view.IShareView;
import com.example.administrator.gamedemo.widget.request.AddShareCommentRequest;
import com.example.administrator.gamedemo.widget.request.DeleteCommentRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.example.administrator.gamedemo.widget.request.callback.OnCommentChangeCallback;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bmob.v3.exception.BmobException;

/**
 * @auther lixu
 * Created by lixu on 2017/4/16 0016.
 */
public class CommentImplShare implements ICommentShare{


    @Override
    public void addComment(
            @NonNull Share momentsId
            , @NonNull Students authorId
            , @Nullable Students replyUserId
            , @NonNull String content
            , @NonNull final OnCommentChangeCallback onCommentChangeCallback) {

        if(onCommentChangeCallback == null){
            return;
        }
        Logger.d("来评论啊2");
        AddShareCommentRequest addShareCommentRequest = new AddShareCommentRequest();
        addShareCommentRequest.setContent(content);
        addShareCommentRequest.setAuthorId(authorId);

        addShareCommentRequest.setReplyUserId(replyUserId);

        addShareCommentRequest.setMomentsInfoId(momentsId);
        addShareCommentRequest.setOnResponseListener(new SimpleResponseListener<CommentInfo>() {
            @Override
            public void onSuccess(CommentInfo response, int requestType) {
                onCommentChangeCallback.onAddComment(response);
            }

            @Override
            public void onError(BmobException e, int requestType) {
                super.onError(e, requestType);
            }

            @Override
            public void onProgress(int pro) {
            }
        });
        addShareCommentRequest.execute();
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
