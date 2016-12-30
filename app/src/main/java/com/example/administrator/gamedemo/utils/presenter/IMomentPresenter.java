package com.example.administrator.gamedemo.utils.presenter;

import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.view.IMomentView;

import java.util.List;


/**
 * Created by lixu on 2016/12/7.
 */

public interface IMomentPresenter extends IBasePresenter<IMomentView> {


    void addLike(int viewHolderPos, String momentid, List<Students> currentLikeUserList);

    void unLike(int viewHolderPos, String momentid, List<Students> currentLikeUserList);

    void addComment(int viewHolderPos, Share momentid, Students replyUserid, String commentContent, List<CommentInfo> currentCommentList);

    void deleteComment(int viewHolderPos, String commentid, List<CommentInfo> currentCommentList);

    void collect(int viewHolderPos, String momentid);

    void unCollect(int viewHolderPos, String momentid);
}
