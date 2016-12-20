package com.example.administrator.gamedemo.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.adapter.CircleMomentsAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.core.MomentsType;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.KeyboardControlMnanager;
import com.example.administrator.gamedemo.utils.PreferenceHelper;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseFragment;
import com.example.administrator.gamedemo.utils.bmob.bmob.BmobInitHelper;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenter;
import com.example.administrator.gamedemo.utils.view.IMomentView;
import com.example.administrator.gamedemo.utils.view.IMomentViewShare;
import com.example.administrator.gamedemo.utils.viewholder.EmptyMomentsVH;
import com.example.administrator.gamedemo.utils.viewholder.MultiImageMomentsVH;
import com.example.administrator.gamedemo.utils.viewholder.TextOnlyMomentsVH;
import com.example.administrator.gamedemo.utils.viewholder.WebMomentsVH;
import com.example.administrator.gamedemo.widget.SoftKeyboardStateHelper;
import com.example.administrator.gamedemo.widget.commentwidget.CommentBox;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;
import com.example.administrator.gamedemo.widget.pullrecyclerview.CircleRecyclerView;
import com.example.administrator.gamedemo.widget.pullrecyclerview.interfaces.onRefreshListener2;
import com.example.administrator.gamedemo.widget.request.MomentsRequest;
import com.example.administrator.gamedemo.widget.request.ShareRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.orhanobut.logger.Logger;
import com.victor.loading.rotate.RotateLoading;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Administrator on 2016/12/8 0008.
 * @author lixu
 */

public class ShareFragment extends BaseFragment  implements onRefreshListener2, IMomentView, CircleRecyclerView.OnPreDispatchTouchListener {

    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    private boolean isPrepared;

    private static final int REQUEST_REFRESH = 0x10;
    private static final int REQUEST_LOADMORE = 0x11;

    @BindView(R.id.recycler)
     CircleRecyclerView circleRecyclerView;
    @BindView(R.id.widget_comment)
     CommentBox commentBox;


//试一下这次呢
    private HostViewHolder hostViewHolder;
    private CircleMomentsAdapter adapter;
    private List<Share> momentsInfoList;
    //request
    private ShareRequest momentsRequest;
    private MomentPresenter presenter;
   // private List<Share> responseTemp;

    public ShareFragment(){
    }

    public static ShareFragment getInstance() {
        isFirst  = true;
        return answerFragmentHolder.instance;
    }




    public static class answerFragmentHolder {
        public static final ShareFragment instance = new ShareFragment();
    }

    @Override
    public void initTheme() {
        getActivity().setTheme(R.style.AppTheme_NoActionBar_Immerse);
    }




    @Override
    public int initCreatView() {
        return R.layout.fragment_share;
    }

    @Override
    public void initViews() {

        momentsInfoList = new ArrayList<>();
        momentsRequest = new ShareRequest();


        presenter = new MomentPresenter(this);

        hostViewHolder = new HostViewHolder(mContext);

        circleRecyclerView.setOnRefreshListener(this);
        circleRecyclerView.setOnPreDispatchTouchListener(this);

        circleRecyclerView.addHeaderView(hostViewHolder.getView());

        commentBox.setOnCommentSendClickListener(onCommentSendClickListener);

        CircleMomentsAdapter.Builder<Share> builder = new CircleMomentsAdapter.Builder<>(mContext);
        builder.addType(EmptyMomentsVH.class, MomentsType.EMPTY_CONTENT, R.layout.moments_empty_content)
                .addType(MultiImageMomentsVH.class, MomentsType.MULTI_IMAGES, R.layout.moments_multi_image)
                .addType(TextOnlyMomentsVH.class, MomentsType.TEXT_ONLY, R.layout.moments_only_text)
                .addType(WebMomentsVH.class, MomentsType.WEB, R.layout.moments_web)
                .setData(momentsInfoList)
                .setPresenter(presenter);
        adapter = builder.build();
        circleRecyclerView.setAdapter(adapter);



        initKeyboardHeightObserver();


        toolbar.setTitleTextColor(ContextCompat.getColor(mContext,R.color.white));
        toolbar.setTitle(R.string.main_share);

        isPrepared = true;
        initData();

    }

    @Override
    public void initData() {
        if(!isPrepared || !isVisible || !isFirst){
            return;
        }else {
            circleRecyclerView.autoRefresh();
            isFirst = false;
        }

    }

    private static class HostViewHolder {
        private View rootView;
        public HostViewHolder(Context context) {
            this.rootView = LayoutInflater.from(context).inflate(R.layout.heard_null, null);
        }
        public View getView() {
            return rootView;
        }
    }

    // TODO: 2016/12/13 进一步优化对齐功能
    private void initKeyboardHeightObserver() {
        //观察键盘弹出与消退
        KeyboardControlMnanager.observerKeyboardVisibleChange(this.getActivity(), new KeyboardControlMnanager.OnKeyboardStateChangeListener() {
            View anchorView;

            @Override
            public void onKeyboardChange(int keyboardHeight, boolean isVisible) {
                int commentType = commentBox.getCommentType();
                if (isVisible) {
                    //定位评论框到view
                    anchorView = alignCommentBoxToView(commentType);
                } else {
                    //定位到底部
                    commentBox.dismissCommentBox(false);
                    alignCommentBoxToViewWhenDismiss(commentType, anchorView);
                }
            }
        });
    }


    @Override
    public void onRefresh() {
        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_REFRESH);
        momentsRequest.setCurPage(0);
        momentsRequest.execute();
    }

    @Override
    public void onLoadMore() {
        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_LOADMORE);
        momentsRequest.execute();
    }
    //call back block
    //==============================================
    private boolean isOne = true;
    private SimpleResponseListener<List<Share>> momentsRequestCallBack = new SimpleResponseListener<List<Share>>() {
        @Override
        public void onSuccess(List<Share> response, int requestType) {
            circleRecyclerView.compelete();
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        momentsInfoList.clear();
                        momentsInfoList.addAll(response);
//                        if(isOne){
//                            BmobInitHelper bb = new BmobInitHelper();
//                            bb.addComments(response);
//                            isOne = false;
//                        }


                        Logger.i("第一条动态ID   >>>   " + response.get(0).getMomentid());
                   //     hostViewHolder.loadHostData(Constants.getInstance().getUser());
                        adapter.updateData(response);
                    }
                    break;
                case REQUEST_LOADMORE:
                    momentsInfoList.clear();
                    momentsInfoList.addAll(response);
                    adapter.addMore(response);
                    break;
            }
        }

        @Override
        public void onError(BmobException e, int requestType) {
            super.onError(e, requestType);
            circleRecyclerView.compelete();
        }
    };


    //=============================================================View's method


    @Override
    public void onLikeChange(int itemPos, List<Students> likeUserList) {
        Logger.d("onLikeChange");
        Share momentsInfo = adapter.findData(itemPos);
        if (momentsInfo != null) {
            momentsInfo.setLikesList(likeUserList);
            adapter.notifyItemChanged(itemPos);
        }
    }

    /**
     * 点击发送
     * @param itemPos
     * @param commentInfoList
     */
    @Override
    public void onCommentChange(int itemPos, List<CommentInfo> commentInfoList) {
        Share momentsInfo = adapter.findData(itemPos);
        if (momentsInfo != null) {
            momentsInfo.setCommentList(commentInfoList);
            adapter.notifyItemChanged(itemPos);
        }
    }

    /**
     * 点击评论
     * @param itemPos
     * @param momentid
     * @param commentWidget
     */
    @Override
    public void showCommentBox(int itemPos, Share momentid, CommentWidget commentWidget) {
        Logger.d("showCommentBox");
        commentBox.setDataPos(itemPos);
        commentBox.setCommentWidget(commentWidget);
        commentBox.toggleCommentBox(momentid, commentWidget == null ? null : commentWidget.getData(), false);
    }

    @Override
    public boolean onPreTouch(MotionEvent ev) {
        if (commentBox != null && commentBox.isShowing()) {
            commentBox.dismissCommentBox(false);
            return true;
        }
        return false;
    }

    //=============================================================tool method

    int[] momentsViewLocation;
    int[] commentWidgetLocation;
    int[] commentBoxViewLocation;

    /**
     * 定位评论框到点击的view
     *
     * @param commentType
     * @return
     */
    private View alignCommentBoxToView(int commentType) {
        // FIXME: 2016/12/13 有可能会获取不到itemView，特别是当view没有完全visible的时候。。。。暂无办法解决
        int firstPos = circleRecyclerView.findFirstVisibleItemPosition();
        int itemPos = commentBox.getDataPos() - firstPos + circleRecyclerView.getHeaderViewCount();
        final View itemView = circleRecyclerView.getRecyclerView().getChildAt(itemPos);
        if (itemView == null) {
            Logger.e("获取不到itemView，pos = " + itemPos);
            return null;
        }
        if (commentType == CommentBox.CommentType.TYPE_CREATE) {
            //对齐到动态底部
            int scrollY = calcuateMomentsViewOffset(itemView);
            circleRecyclerView.getRecyclerView().smoothScrollBy(0, scrollY);
            return itemView;
        } else {
            //对齐到对应的评论
            CommentWidget commentWidget = commentBox.getCommentWidget();
            if (commentWidget == null) return null;
            int scrollY = calcuateCommentWidgetOffset(commentWidget);
            circleRecyclerView.getRecyclerView().smoothScrollBy(0, scrollY);
            return commentWidget;
        }

    }

    /**
     * 输入法消退时，定位到与底部相隔一个评论框的位置
     *
     * @param commentType
     * @param anchorView
     */
    private void alignCommentBoxToViewWhenDismiss(int commentType, View anchorView) {
        if (anchorView == null) return;
        int decorViewHeight = getActivity().getWindow().getDecorView().getHeight();
        int alignScrollY;
        if (commentType == CommentBox.CommentType.TYPE_CREATE) {
            alignScrollY = decorViewHeight - anchorView.getBottom() - commentBox.getHeight();
        } else {
            Rect rect = new Rect();
            anchorView.getGlobalVisibleRect(rect);
            alignScrollY = decorViewHeight - rect.bottom - commentBox.getHeight();
        }
        circleRecyclerView.getRecyclerView().smoothScrollBy(0, -alignScrollY);
    }

    /**
     * 计算回复评论的偏移
     *
     * @param commentWidget
     * @return
     */
    private int calcuateCommentWidgetOffset(CommentWidget commentWidget) {
        if (commentWidgetLocation == null) commentWidgetLocation = new int[2];
        if (commentWidget == null) return 0;
        commentWidget.getLocationInWindow(commentWidgetLocation);
        return commentWidgetLocation[1] + commentWidget.getHeight() - getCommentBoxViewTopInWindow();
    }

    /**
     * 计算动态评论的偏移
     *
     * @param momentsView
     * @return
     */
    private int calcuateMomentsViewOffset(View momentsView) {
        if (momentsViewLocation == null) momentsViewLocation = new int[2];
        if (momentsView == null) return 0;
        momentsView.getLocationInWindow(momentsViewLocation);
        return momentsViewLocation[1] + momentsView.getHeight() - getCommentBoxViewTopInWindow();
    }

    /**
     * 获取评论框的顶部（因为getTop不准确，因此采取 getLocationInWindow ）
     *
     * @return
     */
    private int getCommentBoxViewTopInWindow() {
        if (commentBoxViewLocation == null) commentBoxViewLocation = new int[2];
        if (commentBox == null) return 0;
        if (commentBoxViewLocation[1] != 0) return commentBoxViewLocation[1];
        commentBox.getLocationInWindow(commentBoxViewLocation);
        return commentBoxViewLocation[1];
    }

    //=============================================================call back
    private CommentBox.OnCommentSendClickListener onCommentSendClickListener = new CommentBox.OnCommentSendClickListener() {
        @Override
        public void onCommentSendClick(View v, Share momentid, Students commentAuthorId, String commentContent) {
            if (TextUtils.isEmpty(commentContent)) return;
            int itemPos = commentBox.getDataPos();
            if (itemPos < 0 || itemPos > adapter.getItemCount()) return;
            List<CommentInfo> commentInfos = adapter.findData(itemPos).getCommentList();
            presenter.addComment(itemPos, momentsInfoList.get(itemPos), commentAuthorId, commentContent, commentInfos);
            commentBox.clearDraft();
            commentBox.dismissCommentBox(true);
        }
    };
}
