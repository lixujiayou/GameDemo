package com.example.administrator.gamedemo.utils.presenter;

import android.text.TextUtils;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.CollectImpl;
import com.example.administrator.gamedemo.model.CommentImplShare;
import com.example.administrator.gamedemo.model.CommentImplTogther;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.ICollect;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.view.IMomentViewTogther;
import com.example.administrator.gamedemo.utils.view.IShareView;
import com.example.administrator.gamedemo.widget.request.callback.OnCollectChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnCommentChangeCallback;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @auther lixu
 * Created by lixu on 2017/4/16 0016.
 */
public class SharePresenter implements ISharePresenter {
    private IShareView momentView;
    private CommentImplShare commentModel;
    private CollectImpl collect;//收藏

    public SharePresenter(IShareView mMomentView){
        this.momentView = mMomentView;
        commentModel = new CommentImplShare();
        collect = new CollectImpl();
    }

    @Override
    public void addComment(final int viewHolderPos, Share momentid, Students replyUserid, String commentContent, final List<CommentInfo> currentCommentList) {
        if (TextUtils.isEmpty(commentContent)) return;
        commentModel.addComment(momentid, Constants.getInstance().getUser(), replyUserid, commentContent, new OnCommentChangeCallback() {
            @Override
            public void onAddComment(CommentInfo response) {
                List<CommentInfo> resultLikeList = new ArrayList<>();
                if (!ToolUtil.isListEmpty(currentCommentList)) {
                    resultLikeList.addAll(currentCommentList);
                }
                resultLikeList.add(response);
                Logger.d("评论成功 >>>  " + response.getAuthor().getNick_name());
                if (momentView != null) {
                    momentView.onCommentChange(viewHolderPos, resultLikeList);
                }
            }

            @Override
            public void onDeleteComment(String response) {

            }
        });
    }

    @Override
    public void deleteComment(final int viewHolderPos, String commentid, final List<CommentInfo> currentCommentList) {
        if (TextUtils.isEmpty(commentid)) return;
             commentModel.deleteComment(commentid, new OnCommentChangeCallback() {
            @Override
            public void onAddComment(CommentInfo response) {
            }

            @Override
            public void onDeleteComment(String commentid) {
                if (TextUtils.isEmpty(commentid)) return;
                List<CommentInfo> resultLikeList = new ArrayList<CommentInfo>();
                if (!ToolUtil.isListEmpty(currentCommentList)) {
                    resultLikeList.addAll(currentCommentList);
                }
                Iterator<CommentInfo> iterator = resultLikeList.iterator();
                while (iterator.hasNext()) {
                    CommentInfo info = iterator.next();
                    if (TextUtils.equals(info.getCommentid(), commentid)) {
                        iterator.remove();
                        break;
                    }
                }
                Logger.i("删除评论成功 >>>  " + commentid);
                if (momentView != null) {
                    momentView.onCommentChange(viewHolderPos, resultLikeList);
                }

            }
        });

    }

    @Override
    public void addCollect(int viewHolderPos, Share momentid, Students students,List<Students> collects) {
        if(momentid == null){
            return;
        }
        collect.addCollect(momentid.getObjectId(), collects, new OnCollectChangeCallback() {
            @Override
            public void onCollect() {

            }

            @Override
            public void onUnCollect() {

            }
        });
    }

    @Override
    public void unCollect(int viewHolderPos, Share momentid, Students students,List<Students> collects) {
        if(momentid == null){
            return;
        }
        collect.unCollect(momentid.getObjectId(), collects, new OnCollectChangeCallback() {
            @Override
            public void onCollect() {

            }

            @Override
            public void onUnCollect() {

            }
        });
    }

    @Override
    public IBasePresenter<IShareView> bindView(IShareView view) {
        this.momentView = view;
        return this;
    }

    @Override
    public IBasePresenter<IShareView> unbindView() {
        return this;
    }
}
