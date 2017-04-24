package com.example.administrator.gamedemo.utils.presenter;

import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.utils.view.IMomentViewTogther;
import com.example.administrator.gamedemo.utils.view.IShareView;

import java.util.List;

/**
 * @auther lixu
 * Created by lixu on 2017/4/16 0016.
 * Share
 */
public interface ISharePresenter extends IBasePresenter<IShareView> {

    void addComment(int viewHolderPos, Share momentid, Students replyUserid, String commentContent, List<CommentInfo> currentCommentList);

    void deleteComment(int viewHolderPos, String commentid, List<CommentInfo> currentCommentList);

    //收藏
    void addCollect(int viewHolderPos,Share momentid, Students students,List<Students> collects);

    //取消收藏
    void unCollect(int viewHolderPos,Share momentid, Students students,List<Students> collects);

}
