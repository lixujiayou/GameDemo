package com.example.administrator.gamedemo.utils.view;

import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.bean.LikesInfo;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;

import java.util.List;


/**
 * Created by 大灯泡 on 2016/12/7.
 */

public interface IMomentView extends IBaseView {


    void onLikeChange(int itemPos, List<LikesInfo> likeUserList);

    void onCommentChange(int itemPos, List<CommentInfo> commentInfoList);

    void showCommentBox(int itemPos, Share momentid, CommentWidget commentWidget);

    void onCollectChange(int itemPos, List<Students> collectUserList);

    void onMessageChange(String itemPos,String content);

}
