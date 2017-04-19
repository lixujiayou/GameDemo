package com.example.administrator.gamedemo.utils.view;

import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;

import java.util.List;


/**
 * Created by 大灯泡 on 2016/12/7.
 */

public interface IMomentViewShare extends IBaseView {


    void onCommentChange(int itemPos, List<CommentInfo> commentInfoList);

}
