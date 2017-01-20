package com.example.administrator.gamedemo.utils.presenter;

import android.support.annotation.Nullable;
import android.text.TextUtils;


import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.CollectImpl;
import com.example.administrator.gamedemo.model.CommentImpl;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.LikeImpl;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.view.IMomentView;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;
import com.example.administrator.gamedemo.widget.request.callback.OnCollectChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnCommentChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnLikeChangeCallback;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;


/**
 * Created by 大灯泡 on 2016/12/7.
 * <p>
 * 朋友圈presenter
 */

public class MomentPresenter implements IMomentPresenter {
    private IMomentView momentView;
    private CommentImpl commentModel;
    private LikeImpl likeModel;
    private CollectImpl collectModel;
    private Students cUser;

    public MomentPresenter() {
        this(null);
    }

    public MomentPresenter(IMomentView momentView) {
        this.momentView = momentView;
        commentModel = new CommentImpl();
        likeModel = new LikeImpl();
        collectModel = new CollectImpl();
        cUser = BmobUser.getCurrentUser(Students.class);
    }

    @Override
    public IBasePresenter<IMomentView> bindView(IMomentView view) {
        this.momentView = view;
        return this;
    }

    @Override
    public IBasePresenter<IMomentView> unbindView() {
        return this;
    }

    //=============================================================动作控制
   /* @Override
    public void addLike(final int viewHolderPos, String momentid, final List<Students> currentLikeUserList) {
        likeModel.addLike(momentid, new OnLikeChangeCallback() {
            @Override
            public void onLike() {
                List<Students> resultLikeList = new ArrayList<Students>();
                if (!ToolUtil.isListEmpty(currentLikeUserList)) {
                    resultLikeList.addAll(currentLikeUserList);
                }
                boolean hasLocalLiked = findPosByObjid(resultLikeList, cUser.getObjectId()) > -1;
                if (!hasLocalLiked) {
                    resultLikeList.add(0, cUser);
                }
                if (momentView != null) {
                    momentView.onLikeChange(viewHolderPos, resultLikeList);
                }
            }

            @Override
            public void onUnLike() {

            }

        });
    }*/

    @Override
    public void addLike(final int viewHolderPos, String momentid, final List<Students> currentLikeUserList) {
        likeModel.addLike(momentid, new OnLikeChangeCallback() {
            @Override
            public void onLike() {
                List<Students> resultLikeList = new ArrayList<Students>();
                if (!ToolUtil.isListEmpty(currentLikeUserList)) {
                    resultLikeList.addAll(currentLikeUserList);
                }
                boolean hasLocalLiked = findPosByObjid(resultLikeList, Constants.getInstance().getUser().getObjectId()) > -1;
                if (!hasLocalLiked) {
                    resultLikeList.add(0, Constants.getInstance().getUser());
                }
                if (momentView != null) {
                    momentView.onLikeChange(viewHolderPos, resultLikeList);
                }
            }

            @Override
            public void onUnLike() {

            }
        });
    }
    @Override
    public void collect(final int viewHolderPos, String momentid, final List<Students> collectUserList) {
        collectModel.addCollect(momentid,collectUserList, new OnCollectChangeCallback() {
            @Override
            public void onCollect() {
                List<Students> resultLikeList = new ArrayList<Students>();
                if (!ToolUtil.isListEmpty(collectUserList)) {
                    resultLikeList.addAll(collectUserList);
                }
                boolean hasLocalLiked = findPosByObjid(resultLikeList, Constants.getInstance().getUser().getObjectId()) > -1;
                if (!hasLocalLiked) {
                    resultLikeList.add(0, Constants.getInstance().getUser());
                }
                if (momentView != null) {
                    momentView.onCollectChange(viewHolderPos, resultLikeList);
                }


            }

            @Override
            public void onUnCollect() {

            }
        });
    }

    @Override
    public void unCollect(final int viewHolderPos, String momentid, final List<Students> unCollectUserList) {
        collectModel.unCollect(momentid,unCollectUserList, new OnCollectChangeCallback() {
            @Override
            public void onCollect() {

            }

            @Override
            public void onUnCollect() {
                List<Students> resultLikeList = new ArrayList<Students>();
                if (!ToolUtil.isListEmpty(unCollectUserList)) {
                    resultLikeList.addAll(unCollectUserList);
                }
                final int localLikePos = findPosByObjid(resultLikeList, Constants.getInstance().getUser().getObjectId());
                if (localLikePos > -1) {
                    resultLikeList.remove(localLikePos);
                }
                if (momentView != null) {
                    momentView.onCollectChange(viewHolderPos, resultLikeList);
                }
            }
        });
    }
    /*@Override
    public void unLike(final int viewHolderPos, String momentid, final List<Students> currentLikeUserList) {
        likeModel.unLike(momentid, new OnLikeChangeCallback() {
            @Override
            public void onLike() {

            }

            @Override
            public void onUnLike() {
                List<Students> resultLikeList = new ArrayList<Students>();
                if (!ToolUtil.isListEmpty(currentLikeUserList)) {
                    resultLikeList.addAll(currentLikeUserList);
                }
                final int localLikePos = findPosByObjid(resultLikeList,cUser.getObjectId());
                if (localLikePos > -1) {
                    resultLikeList.remove(localLikePos);
                }
                if (momentView != null) {
                    momentView.onLikeChange(viewHolderPos, resultLikeList);
                }
            }

        });
    }*/

    @Override
    public void unLike(final int viewHolderPos, String momentid, final List<Students> currentLikeUserList) {
        likeModel.unLike(momentid, new OnLikeChangeCallback() {
            @Override
            public void onLike() {

            }

            @Override
            public void onUnLike() {
                List<Students> resultLikeList = new ArrayList<Students>();
                if (!ToolUtil.isListEmpty(currentLikeUserList)) {
                    resultLikeList.addAll(currentLikeUserList);
                }
                final int localLikePos = findPosByObjid(resultLikeList, Constants.getInstance().getUser().getObjectId());
                if (localLikePos > -1) {
                    resultLikeList.remove(localLikePos);
                }
                if (momentView != null) {
                    momentView.onLikeChange(viewHolderPos, resultLikeList);
                }
            }

        });
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




    public void showCommentBox(int itemPos, Share momentid, @Nullable CommentWidget commentWidget) {
        if (momentView != null) {
            momentView.showCommentBox(itemPos, momentid, commentWidget);
        }
    }


    /**
     * 从bmobobj列表寻找符合id的位置
     *
     * @return -1:找不到
     */
    private int findPosByObjid(List<? extends BmobObject> objectList, String id) {
        int result = -1;
        if (ToolUtil.isListEmpty(objectList)) return result;
        for (int i = 0; i < objectList.size(); i++) {
            BmobObject object = objectList.get(i);
            if (TextUtils.equals(object.getObjectId(), id)) {
                result = i;
                break;
            }
        }
        return result;
    }

    //------------------------------------------interface impl-----------------------------------------------
}
