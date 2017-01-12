package com.example.administrator.gamedemo.utils.viewholder;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.fragment.ShareFragment;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.SimpleObjectPool;
import com.example.administrator.gamedemo.utils.TimeUtil;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.UIHelper;
import com.example.administrator.gamedemo.utils.base.BaseRecyclerViewHolder;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenter;
import com.example.administrator.gamedemo.widget.ClickShowMoreLayout;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;
import com.example.administrator.gamedemo.widget.commentwidget.StretchyTextView;
import com.example.administrator.gamedemo.widget.popup.CommentPopup;
import com.example.administrator.gamedemo.widget.popup.DeleteCommentPopup;
import com.example.administrator.gamedemo.widget.praisewidget.PraiseWidget;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by lixu on 2016/12/14.
 * <p>
 * 基本item
 */
public abstract class ShareViewHolder extends BaseRecyclerViewHolder<Share> implements BaseMomentVH<Share>, ViewGroup.OnHierarchyChangeListener {
    private int commentLeftAndPaddintRight = UIHelper.dipToPx(8f);
    private int commentTopAndPaddintBottom = UIHelper.dipToPx(3f);

    //头部
    protected ImageView avatar;
    protected TextView nick;
    //protected ClickShowMoreLayout userText;
    protected StretchyTextView userText;
    protected ImageView iv_collect;

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

    private CommentPopup commentPopup;
    private MomentPresenter momentPresenter;
    private int itemPosition;
    private DeleteCommentPopup deleteCommentPopup;
    private Share momentsInfo;

    private Context mContext;
    private boolean isOrCollect = true;
    private SweetAlertDialog pDialog;
    public ShareViewHolder(Context context, ViewGroup viewGroup, int layoutResId) {
        super(context, viewGroup, layoutResId);
        this.mContext = context;
        onFindView(itemView);
        //header
        avatar = (ImageView) findView(avatar, R.id.avatar);
        iv_collect = (ImageView) findView(iv_collect, R.id.iv_collect);
        nick = (TextView) findView(nick, R.id.nick);
        userText = (StretchyTextView) findView(userText, R.id.item_text_field);
        iv_collect.setVisibility(View.VISIBLE);

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
            commentPopup = new CommentPopup((Activity) getContext());
            commentPopup.setOnCommentPopupClickListener(onCommentPopupClickListener);
        }

        if (deleteCommentPopup == null) {
            deleteCommentPopup = new DeleteCommentPopup((Activity) getContext());
            deleteCommentPopup.setOnDeleteCommentClickListener(onDeleteCommentClickListener);
        }
    }

    @Override
    public void onBindData(Share data, int position) {
        if (data == null) {
            Logger.t("wu无数据");
            findView(userText, R.id.item_text_field);
            userText.setContent("");
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

    private void onBindMutualDataToViews(final Share data) {
        //header
        if(data.getAuthor().getUser_icon() != null) {
            ImageLoadMnanger.INSTANCE.loadRoundImage(avatar, data.getAuthor().getUser_icon().getFileUrl());
        }else{
            avatar.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_loading_small));
        }

        nick.setText(data.getAuthor().getNick_name());

        userText.setContentTextColor(ContextCompat.getColor(mContext,R.color.textcolor_2));
        userText.setMaxLineCount(4);
        userText.setContent(data.getText());

        //bottom
        createTime.setText(TimeUtil.getTimeStringFromBmob(data.getCreatedAt()));
        boolean needPraiseData = addLikes(data.getLikesList());
        boolean needCommentData = addComment(data.getCommentList());
        praiseWidget.setVisibility(needPraiseData ? View.VISIBLE : View.GONE);
        commentLayout.setVisibility(needCommentData ? View.VISIBLE : View.GONE);
        line.setVisibility(needPraiseData && needCommentData ? View.VISIBLE : View.GONE);
        commentAndPraiseLayout.setVisibility(needCommentData || needPraiseData ? View.VISIBLE : View.GONE);
        //收藏
        iv_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPhotoDialog(itemPosition,data);

            }
        });
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
                commentWidget.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.selector_comment_widget));
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

    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
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
            Share info = (Share) v.getTag(R.id.momentinfo_data_tag_id);
            if (info != null) {
                commentPopup.updateMomentInfo(info);
                commentPopup.showPopupWindow(commentImage);
            }
        }
    };


    private CommentPopup.OnCommentPopupClickListener onCommentPopupClickListener=new CommentPopup.OnCommentPopupClickListener() {

        @Override
        public void onLikeClick(View v, @NonNull Share info, boolean hasLiked) {
            if (hasLiked) {
                Logger.d("取消点赞");
                momentPresenter.unLike(itemPosition, info.getMomentid(), info.getLikesList());
            } else {
                Logger.d("点赞");
                momentPresenter.addLike(itemPosition, info.getMomentid(), info.getLikesList());
            }
        }

        @Override
        public void onCommentClick(View v, @NonNull Share info) {

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
    public void setPresenter(MomentPresenter momentPresenter) {
        this.momentPresenter = momentPresenter;
    }

    /**
     * ==================  click listener block
     */
    private View.OnClickListener onCommentWidgetClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
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
            try {
                momentPresenter.deleteComment(itemPosition, commentInfo.getCommentid(), momentsInfo.getCommentList());
                deleteCommentPopup.dismiss();
            }catch (Exception e){
                Logger.d("空了"+e);
            }
            }
    };



    private Dialog dialog_help_2;
    View view_2;
    helpdialog_item_2 hi_2 = null;
    private void showPhotoDialog(final int itemPositionTemp, final Share dataTemp) {
        if(hi_2==null){
            hi_2= new helpdialog_item_2();
            view_2 = LayoutInflater.from(mContext).inflate(R.layout.popup_delete_comment, null);
            hi_2.tv_help1 = (TextView) view_2.findViewById(R.id.delete);
            hi_2.tv_helpcancle = (TextView) view_2.findViewById(R.id.cancel);

            dialog_help_2 = new Dialog(mContext, R.style.transparentFrameWindowStyle);
            dialog_help_2.setContentView(view_2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = dialog_help_2.getWindow();
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = ShareFragment.getInstance().getActivity().getWindowManager().getDefaultDisplay().getHeight();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog_help_2.onWindowAttributesChanged(wl);
            dialog_help_2.setCanceledOnTouchOutside(true);
            view_2.setTag(hi_2);
        }else{
            hi_2 = (helpdialog_item_2) view_2.getTag();
        }
        queryCollect(dataTemp);
        hi_2.tv_help1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOrCollect){
                    hi_2.tv_help1.setTextColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
                    momentPresenter.collect(itemPositionTemp, dataTemp.getObjectId());
                }else{
                    momentPresenter.unCollect(itemPositionTemp, dataTemp.getObjectId());
                }
                dialog_help_2.dismiss();
            }
        });

        hi_2.tv_helpcancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_help_2.dismiss();
            }
        });
    }

    class helpdialog_item_2{
        TextView tv_help1;
        TextView tv_helpcancle;
    }

    private void queryCollect(final Share share){
        showProgressBarDialog(mContext);
        Students cUser = Constants.getInstance().getUser();
        if(cUser != null){
            BmobQuery<Share> shareBmobQuery = new BmobQuery<>();
            shareBmobQuery.addWhereRelatedTo("collects", new BmobPointer(cUser));
            shareBmobQuery.findObjects(new FindListener<Share>() {
                @Override
                public void done(List<Share> list, BmobException e) {
                    dimssProgressDialog();
                    isOrCollect = true;
                    if(e == null){
                        if(list == null || list.size() == 0){
                            isOrCollect = true;
                            hi_2.tv_help1.setText("收藏");
                            dialog_help_2.show();
                        }else{
                            for(int i = 0;i<list.size();i++){
                                if(list.get(i).getObjectId().equals(share.getObjectId())){
                                    isOrCollect = false;
                                }
                            }
                            if(isOrCollect){
                                hi_2.tv_help1.setText("收藏");
                                dialog_help_2.show();
                            }else{
                                hi_2.tv_help1.setText("取消收藏");
                                dialog_help_2.show();
                            }
                        }
                    }else{
                        isOrCollect = true;
                        hi_2.tv_help1.setText("收藏");
                        dialog_help_2.show();
                    }
                }
            });

        }
    }

    public void showProgressBarDialog(final Context mContext){
        try {
            pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.setTitleText("正在初始化数据");
            pDialog.setCancelable(false);
            pDialog.show();

        }catch (Exception e){
            Logger.d("ProgressBarDialog的上下文找不到啦！"+e);
        }
    }

    /**
     * 取消进度框
     */
    public void dimssProgressDialog(){
        if(pDialog == null){
            return;
        }
        pDialog.dismiss();
    }
}
