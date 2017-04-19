package com.example.administrator.gamedemo.utils.presenter;

import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.model.bean.LikesInfo;
import com.example.administrator.gamedemo.utils.view.IMomentView;
import com.example.administrator.gamedemo.utils.view.IMomentViewTogther;

import java.util.List;


/**
 * Created by lixu on 2016/12/27.
 */

public interface IMomentPresenterTogther extends IBasePresenter<IMomentViewTogther> {


    void addLike(int viewHolderPos, String momentid, List<LikesInfo> currentLikeUserList);

    void unLike(int viewHolderPos, String momentid, List<LikesInfo> currentLikeUserList);

    void addComment(int viewHolderPos, Togther momentid, Students replyUserid, String commentContent, List<CommentInfo> currentCommentList);

    void deleteComment(int viewHolderPos, String commentid, List<CommentInfo> currentCommentList);

    void addMessage(String cUserId,String rId,String type,String content);
}
