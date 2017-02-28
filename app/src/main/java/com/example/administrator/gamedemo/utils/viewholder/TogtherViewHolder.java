package com.example.administrator.gamedemo.utils.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.LoginActivity;
import com.example.administrator.gamedemo.activity.mine.togther.TogetherActivity;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.utils.SimpleObjectPool;
import com.example.administrator.gamedemo.utils.TimeUtil;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.UIHelper;
import com.example.administrator.gamedemo.utils.base.BaseRecyclerViewHolder;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenter;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenterTogther;
import com.example.administrator.gamedemo.widget.ClickShowMoreLayout;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;
import com.example.administrator.gamedemo.widget.popup.CommentPopup;
import com.example.administrator.gamedemo.widget.popup.CommentPopup_togther;
import com.example.administrator.gamedemo.widget.popup.DeleteCommentPopup;
import com.example.administrator.gamedemo.widget.praisewidget.PraiseWidget;
import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by lixu on 2016/12/14.
 * <p>
 * 一起基本item
 */
public abstract class TogtherViewHolder extends BaseRecyclerViewHolder<Togther> implements BaseMomentVH<Togther>, ViewGroup.OnHierarchyChangeListener {
    private int commentLeftAndPaddintRight = UIHelper.dipToPx(8f);
    private int commentTopAndPaddintBottom = UIHelper.dipToPx(3f);

    //头部
    protected ImageView avatar;
    protected TextView nick;
    protected ClickShowMoreLayout userText;

    //底部
    protected TextView createTime;
    protected ImageView commentImage;
    protected FrameLayout menuButton;
    protected LinearLayout commentAndPraiseLayout;
    protected PraiseWidget praiseWidget;
    protected View line;
    protected LinearLayout commentLayout;

    //内容区
    protected RelativeLayout contentLayout;

    //评论区的view对象池
    private static final SimpleObjectPool<CommentWidget> COMMENT_TEXT_POOL = new SimpleObjectPool<CommentWidget>(35);

    private CommentPopup_togther commentPopup;
    private MomentPresenterTogther momentPresenter;
    private int itemPosition;
    private DeleteCommentPopup deleteCommentPopup;
    private Togther momentsInfo;

    private Context mContext;

    public TogtherViewHolder(Context context, ViewGroup viewGroup, int layoutResId) {
        super(context, viewGroup, layoutResId);
        this.mContext = context;
        onFindView(itemView);
        //header
        avatar = (ImageView) findView(avatar, R.id.avatar);
        nick = (TextView) findView(nick, R.id.nick);
        userText = (ClickShowMoreLayout) findView(userText, R.id.item_text_field);

        //bottom
        createTime = (TextView) findView(createTime, R.id.create_time);
        commentImage = (ImageView) findView(commentImage, R.id.menu_img);
        menuButton = (FrameLayout) findView(menuButton, R.id.menu_button);
        commentAndPraiseLayout = (LinearLayout) findView(commentAndPraiseLayout, R.id.comment_praise_layout);
        praiseWidget = (PraiseWidget) findView(praiseWidget, R.id.praise);
        line = findView(line, R.id.divider);
        commentLayout = (LinearLayout) findView(commentLayout, R.id.comment_layout);
        //content
        contentLayout = (RelativeLayout) findView(contentLayout, R.id.tv_title);

        if (commentPopup == null) {
            commentPopup = new CommentPopup_togther((Activity) getContext());
            commentPopup.setOnCommentPopupClickListener(onCommentPopupClickListener);
        }

        if (deleteCommentPopup == null) {
            deleteCommentPopup = new DeleteCommentPopup((Activity) getContext());
            deleteCommentPopup.setOnDeleteCommentClickListener(onDeleteCommentClickListener);
        }
    }

    @Override
    public void onBindData(Togther data, int position) {
        if (data == null) {
            Logger.t("wu无数据");
            findView(userText, R.id.item_text_field);
            userText.setText("");
            return;
        }
        this.momentsInfo=data;
        this.itemPosition = position;

        //通用数据绑定
        onBindMutualDataToViews(data);
        //点击事件
        menuButton.setOnClickListener(onMenuButtonClickListener);
        menuButton.setTag(R.id.momentinfo_data_tag_id, data);
        //传递到子类
        onBindDataToView(data, position, getViewType());
    }

    private void onBindMutualDataToViews(Togther data) {
        //header
        if(data.getAuthor().getUser_icon() != null) {
            ImageLoadMnanger.INSTANCE.loadRoundImage( avatar, data.getAuthor().getUser_icon().getFileUrl());
        }else{
            avatar.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_loading_small));
        }

        nick.setText(data.getAuthor().getNick_name());

        userText.setText(data.getText());

        //bottom
        createTime.setText(TimeUtil.getTimeStringFromBmob(data.getCreatedAt()));
        boolean needPraiseData = addLikes(data.getLikesList());
        boolean needCommentData = addComment(data.getCommentList());
        praiseWidget.setVisibility(needPraiseData ? View.VISIBLE : View.GONE);
        commentLayout.setVisibility(needCommentData ? View.VISIBLE : View.GONE);
        line.setVisibility(needPraiseData && needCommentData ? View.VISIBLE : View.GONE);
        commentAndPraiseLayout.setVisibility(needCommentData || needPraiseData ? View.VISIBLE : View.GONE);

    }


    /**
     * 添加点赞
     *
     * @param likesList
     * @return ture=显示点赞，false=不显示点赞
     */
    private boolean addLikes(List<Students> likesList) {
        if (ToolUtil.isListEmpty(likesList)) {
            return false;
        }
        praiseWidget.setDatas(likesList);
        return true;
    }


    private int commentPaddintRight = UIHelper.dipToPx(8f);

    /**
     * 添加评论
     *
     * @param commentList
     * @return ture=显示评论，false=不显示评论
     */
   /* private boolean addComment(List<CommentInfo> commentList) {
        if (ToolUtil.isListEmpty(commentList)) {
            return false;
        }
        final int childCount = commentLayout.getChildCount();
        commentLayout.setOnHierarchyChangeListener(this);
        if (childCount < commentList.size()) {
            //当前的view少于list的长度，则补充相差的view
            int subCount = commentList.size() - childCount;
            for (int i = 0; i < subCount; i++) {
                CommentWidget commentWidget = COMMENT_TEXT_POOL.get();
                if (commentWidget == null) {
                    commentWidget = new CommentWidget(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.topMargin = 1;
                    params.bottomMargin = 1;
                    commentWidget.setLayoutParams(params);
                    commentWidget.setPadding(0, 0, commentPaddintRight, 0);
                    commentWidget.setLineSpacing(4, 1);
                }
                commentWidget.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.selector_comment_widget));
                commentWidget.setOnClickListener(onCommentClickListener);
                commentWidget.setOnLongClickListener(onCommentLongClickListener);
                commentLayout.addView(commentWidget);
            }
        } else if (childCount > commentList.size()) {
            //当前的view的数目比list的长度大，则减去对应的view
            commentLayout.removeViews(commentList.size(), childCount - commentList.size());
        }
        //绑定数据
        for (int n = 0; n < commentList.size(); n++) {
            CommentWidget commentWidget = (CommentWidget) commentLayout.getChildAt(n);
            if (commentWidget != null) commentWidget.setCommentText(commentList.get(n));
        }
        return true;
    }*/



    /**
     * 添加评论
     *
     * @param commentList
     * @return ture=显示评论，false=不显示评论
     */
    private boolean addComment(List<CommentInfo> commentList) {
        if (ToolUtil.isListEmpty(commentList)) {
            return false;
        }
        final int childCount = commentLayout.getChildCount();
        if (childCount < commentList.size()) {
            //当前的view少于list的长度，则补充相差的view
            int subCount = commentList.size() - childCount;
            for (int i = 0; i < subCount; i++) {
                CommentWidget commentWidget = COMMENT_TEXT_POOL.get();
                if (commentWidget == null) {
                    commentWidget = new CommentWidget(getContext());
                    commentWidget.setPadding(commentLeftAndPaddintRight, commentTopAndPaddintBottom, commentLeftAndPaddintRight, commentTopAndPaddintBottom);
                    commentWidget.setLineSpacing(4, 1);
                }
                commentWidget.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.selector_comment_widget));
                commentWidget.setOnClickListener(onCommentWidgetClickListener);
                commentWidget.setOnLongClickListener(onCommentLongClickListener);
                commentLayout.addView(commentWidget);
            }
        } else if (childCount > commentList.size()) {
            //当前的view的数目比list的长度大，则减去对应的view
            commentLayout.removeViews(commentList.size(), childCount - commentList.size());
        }
        //绑定数据
        for (int n = 0; n < commentList.size(); n++) {
            CommentWidget commentWidget = (CommentWidget) commentLayout.getChildAt(n);
            if (commentWidget != null) commentWidget.setCommentText(commentList.get(n));
        }
        return true;
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
        if(Constants.getInstance().getUser() == null) {
            Intent lIntent = new Intent(mContext,LoginActivity.class);
            mContext.startActivity(lIntent);
            return;
        }
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
        if(Constants.getInstance().getUser() == null) {
            Intent lIntent = new Intent(mContext,LoginActivity.class);
            mContext.startActivity(lIntent);
            return;
        }
        if (child instanceof CommentWidget) COMMENT_TEXT_POOL.put((CommentWidget) child);
    }

    public void clearCommentPool() {
        COMMENT_TEXT_POOL.clearPool();
    }

    /**
     * ==================  click listener block
     */

    private View.OnClickListener onCommentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnLongClickListener onCommentLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    };

    private View.OnClickListener onMenuButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Constants.getInstance().getUser() == null) {
                Intent lIntent = new Intent(mContext,LoginActivity.class);
                mContext.startActivity(lIntent);
                return;
            }
            Togther info = (Togther) v.getTag(R.id.momentinfo_data_tag_id);
            if (info != null) {
                commentPopup.updateMomentInfo(info);
                commentPopup.showPopupWindow(commentImage);
            }
        }
    };


    private CommentPopup_togther.OnCommentPopupClickListener onCommentPopupClickListener=new CommentPopup_togther.OnCommentPopupClickListener() {

        @Override
        public void onLikeClick(View v, @NonNull Togther info, boolean hasLiked) {
            if(Constants.getInstance().getUser() == null) {
                Intent lIntent = new Intent(mContext,LoginActivity.class);
                mContext.startActivity(lIntent);
                return;
            }
            if (hasLiked) {
                Logger.d("取消点赞"+info.getObjectId());
                momentPresenter.unLike(itemPosition, info.getMomentid(), info.getLikesList());
            } else {
                Logger.d("点赞"+info.getObjectId());
                momentPresenter.addLike(itemPosition, info.getMomentid(), info.getLikesList());

                momentPresenter.addMessage(info.getAuthor().getObjectId()
                        ,info.getObjectId()
                        , Constants.MESSAGE_TOGTHER
                        ,""+Constants.getInstance().getUser().getNick_name()+"赞了您");
            }
        }

        @Override
        public void onCommentClick(View v, @NonNull Togther info) {
            if(Constants.getInstance().getUser() == null) {
                Intent lIntent = new Intent(mContext,LoginActivity.class);
                mContext.startActivity(lIntent);
                return;
            }
            Logger.d("评论");
            momentPresenter.showCommentBox(itemPosition, info, null);

        }
    };

    /**
     * ============  tools method block
     */


    protected final View findView(View view, int resid) {
        if (resid > 0 && itemView != null && view == null) {
            return itemView.findViewById(resid);
        }
        return view;
    }
    public void setPresenter(MomentPresenterTogther momentPresenter) {
        this.momentPresenter = momentPresenter;
    }

    /**
     * ==================  click listener block
     */
    private View.OnClickListener onCommentWidgetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Constants.getInstance().getUser() == null) {
                Intent lIntent = new Intent(mContext,LoginActivity.class);
                mContext.startActivity(lIntent);
                return;
            }
            if (!(v instanceof CommentWidget)) return;
            CommentInfo commentInfo = ((CommentWidget) v).getData();
            if (commentInfo == null) return;
            if (commentInfo.canDelete()) {
                deleteCommentPopup.showPopupWindow(commentInfo);
            } else {
                momentPresenter.showCommentBox(itemPosition, momentsInfo, (CommentWidget) v);
            }
        }
    };

    private DeleteCommentPopup.OnDeleteCommentClickListener onDeleteCommentClickListener=new DeleteCommentPopup.OnDeleteCommentClickListener() {
        @Override
        public void onDelClick(CommentInfo commentInfo) {
            if(Constants.getInstance().getUser() == null) {
                Intent lIntent = new Intent(mContext,LoginActivity.class);
                mContext.startActivity(lIntent);
                return;
            }
            try {
                momentPresenter.deleteComment(itemPosition, commentInfo.getCommentid(), momentsInfo.getCommentList());
                deleteCommentPopup.dismiss();
            }catch (Exception e){
                Logger.d("空了"+e);
            }
            }
    };
}
