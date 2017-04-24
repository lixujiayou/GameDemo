package com.example.administrator.gamedemo.utils.presenter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.CommentImplTogther;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.LikeImplTogther;
import com.example.administrator.gamedemo.model.MyBmobInstallation;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.model.bean.LikesInfo;
import com.example.administrator.gamedemo.model.impl.MessageImpl;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.view.IMomentViewTogther;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;
import com.example.administrator.gamedemo.widget.request.callback.OnCommentChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnLikeChangeCallback;
import com.example.administrator.gamedemo.widget.request.callback.OnMessageCallback;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;


/**
 * Created by lixu on 2016/12/27.
 * <p>
 * 消息presenter
 */

public class MomentPresenterAbout implements IMomentPresenterTogther {
    private IMomentViewTogther momentView;
    private CommentImplTogther commentModel;
    private LikeImplTogther likeModel;
    private Students cUser;
    private MessageImpl message;
    public MomentPresenterAbout() {
        this(null);
    }

    public MomentPresenterAbout(IMomentViewTogther momentView) {
        this.momentView = momentView;
        commentModel = new CommentImplTogther();
        likeModel = new LikeImplTogther();
        cUser = BmobUser.getCurrentUser(Students.class);
        message = new MessageImpl();
    }

    @Override
    public IBasePresenter<IMomentViewTogther> bindView(IMomentViewTogther view) {
        this.momentView = view;
        return this;
    }

    @Override
    public IBasePresenter<IMomentViewTogther> unbindView() {
        return this;
    }


    @Override
    public void addLike(final int viewHolderPos, final String momentid, final List<LikesInfo> currentLikeUserList) {
        likeModel.addLike(momentid, new OnLikeChangeCallback() {
            @Override
            public void onLike(String likeId) {
                List<LikesInfo> resultLikeList = new ArrayList<>();
                if (!ToolUtil.isListEmpty(currentLikeUserList)) {
                    resultLikeList.addAll(currentLikeUserList);
                }
                boolean hasLocalLiked = findPosByObjid(resultLikeList, Constants.getInstance().getUser().getObjectId()) > -1;
                if (!hasLocalLiked) {

                    LikesInfo likesInfo = new LikesInfo();
                    likesInfo.setTogtherid(momentid);
                    likesInfo.setObjectId(likeId);
                    likesInfo.setUserInfo(Constants.getInstance().getUser());

                    resultLikeList.add(0, likesInfo);

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
    public void unLike(final int viewHolderPos, String momentid, final List<LikesInfo> currentLikeUserList) {
        likeModel.unLike(momentid, new OnLikeChangeCallback() {
            @Override
            public void onLike(String likeId) {
            }

            @Override
            public void onUnLike() {
                List<LikesInfo> resultLikeList = new ArrayList<>();
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
    public void addComment(final int viewHolderPos, Togther momentid, Students replyUserid, String commentContent, final List<CommentInfo> currentCommentList) {
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
    public void addMessage(final String cUserId, String rId, String type, final String content) {
        message.addMessage(cUserId, rId, type, content, new OnMessageCallback() {
            @Override
            public void onMessage() {
                BmobQuery<MyBmobInstallation> bmobQuery = new BmobQuery<>();
                bmobQuery.addWhereEqualTo("uid",cUserId);
                bmobQuery.findObjects(new FindListener<MyBmobInstallation>() {
                    @Override
                    public void done(List<MyBmobInstallation> list, BmobException e) {
                        if(list.size() > 0){
                            momentView.onMessageChange(list.get(0).getInstallationId(),content);
                        }
                    }
                });
            }

            @Override
            public void onUnMessage() {

            }
        });
    }

    @Override
    public void deleteMessage(String messageId) {

    }


    public void showCommentBox(int itemPos, Togther momentid, @Nullable CommentWidget commentWidget) {
        if (momentView != null) {
            momentView.showCommentBox(itemPos, momentid, commentWidget);
        }
    }


    /**
     * 从bmobobj列表寻找符合id的位置
     *
     * @return -1:找不到
     */
    private int findPosByObjid(List<LikesInfo> objectList, String id) {
        int result = -1;
        if (ToolUtil.isListEmpty(objectList)) return result;
        for (int i = 0; i < objectList.size(); i++) {
            BmobObject object = objectList.get(i).getUserInfo();
            if (TextUtils.equals(object.getObjectId(), id)) {
                result = i;
                break;
            }
        }
        return result;
    }

    //------------------------------------------interface impl-----------------------------------------------
}
