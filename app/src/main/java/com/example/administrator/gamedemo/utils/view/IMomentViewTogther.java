package com.example.administrator.gamedemo.utils.view;

import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;

import java.util.List;


/**
 * Created by lixu on 2016/12/7.
 */

public interface IMomentViewTogther extends IBaseView {

    void onLikeChange(int itemPos, List<Students> likeUserList);

    void onCommentChange(int itemPos, List<CommentInfo> commentInfoList);

    void showCommentBox(int itemPos, Togther momentid, CommentWidget commentWidget);

    void onMessageChange(String itemPos,String content);

}
