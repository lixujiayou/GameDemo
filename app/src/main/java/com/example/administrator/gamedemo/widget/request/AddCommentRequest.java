package com.example.administrator.gamedemo.widget.request;

import android.text.TextUtils;

import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.StringUtil;
import com.orhanobut.logger.Logger;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by 大灯泡 on 2016/10/28.
 *
 * 添加评论
 */

public class AddCommentRequest extends BaseRequestClient<CommentInfo> {
    private CommentInfo commentInfo1;
    private String content;
    private Students authorId;
    private Students replyUserId;
    private Share share;

    public void setContent(String content) {
        this.content = content;
    }

    public void setAuthorId(Students authorId) {
        this.authorId = authorId;
    }

    public void setReplyUserId(Students replyUserId) {
        this.replyUserId = replyUserId;
    }

    public void setMomentsInfoId(Share share) {
        this.share = share;
    }

    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        if (authorId == null) {
            onResponseError(new BmobException("创建用户为空"), getRequestType());
            return;
        }

        commentInfo1 = new CommentInfo();
        commentInfo1.setContent(content);
        commentInfo1.setAuthor(authorId);
        commentInfo1.setMoment(share);
        if (replyUserId != null) {
            commentInfo1.setReply(replyUserId);
            Logger.d("评论者"+authorId.getNick_name()+"被回复者"+replyUserId.getNick_name());
        }

        commentInfo1.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Logger.d("评论成功");
                    onResponseSuccess(commentInfo1, requestType);
//                    BmobQuery<CommentInfo> commentQuery = new BmobQuery<>();
//                    commentQuery.getObject(s, new QueryListener<CommentInfo>() {
//                        @Override
//                        public void done(CommentInfo commentInfo, BmobException e) {
//                            if (e == null) {
//                                onResponseSuccess(commentInfo1, requestType);
//                            } else {
//                                onResponseError(e, requestType);
//                            }
//                        }
//                    });
                } else {
                    Logger.d("评论失败"+s);
                    onResponseError(e, requestType);
                }
            }
        });
    }
}
