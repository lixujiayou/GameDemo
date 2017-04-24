package com.example.administrator.gamedemo.activity.mine.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.gamedemo.R;

import com.example.administrator.gamedemo.activity.share.ShareInfoActivity;
import com.example.administrator.gamedemo.adapter.TogtherAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.core.MomentsType;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.model.bean.LikesInfo;
import com.example.administrator.gamedemo.utils.KeyboardControlMnanager;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseActivity;

import com.example.administrator.gamedemo.utils.presenter.MomentPresenterTogther;

import com.example.administrator.gamedemo.utils.view.IMomentViewTogther;
import com.example.administrator.gamedemo.utils.viewholder.EmptyMomentsVHTogther;
import com.example.administrator.gamedemo.utils.viewholder.MultiImageMomentsVHTogther;
import com.example.administrator.gamedemo.utils.viewholder.TextOnlyMomentsVHTogther;
import com.example.administrator.gamedemo.utils.viewholder.WebMomentsVHTogther;
import com.example.administrator.gamedemo.widget.commentwidget.CommentBox;
import com.example.administrator.gamedemo.widget.commentwidget.CommentBoxTogther;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;
import com.example.administrator.gamedemo.widget.pullrecyclerview.CircleRecyclerView;
import com.example.administrator.gamedemo.widget.pullrecyclerview.interfaces.onRefreshListener2;
import com.example.administrator.gamedemo.widget.request.ShareRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.example.administrator.gamedemo.widget.request.TogtherRequest;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import cn.bmob.v3.exception.BmobException;

/**
 * @auther lixu
 * Created by lixu on 2017/2/14 0014.
 */
public class AboutActivity extends BaseActivity implements IMomentViewTogther {
    private static final int REQUEST_REFRESH = 0x10;
    private static final int REQUEST_LOADMORE = 0x11;
    public static final String KEYCODE = "KEYCODE";
    public static final String TYPECODE = "TYPECODE";

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.widget_comment_togther)
    CommentBoxTogther widgetCommentTogther;
    @BindView(R.id.iv_load_state)
    ImageView ivLoadState;

    private TogtherAdapter togtherAdapter;

    private List<Togther> togtherList;

    private ShareRequest momentsRequest;
    private TogtherRequest togtherRequest;

    private MomentPresenterTogther presenterTogther;
    private boolean isReadCache = true;
    private LinearLayoutManager linearLayoutManager;

    private String mKey;
    private String mType;
    private boolean isOne;//可判断下拉刷新时是否显示动画

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about_me);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("关于");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recycler.smoothScrollToPosition(0);
            }
        });

        Intent gIntent = getIntent();
        mKey = gIntent.getExtras().getString(KEYCODE);
        mType = gIntent.getExtras().getString(TYPECODE);

        togtherList = new ArrayList<>();

        momentsRequest = new ShareRequest();
        togtherRequest = new TogtherRequest();

        swipeRefreshLayout.setRefreshing(true);
        linearLayoutManager = new LinearLayoutManager(AboutActivity.this);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        if(mType.equals(Constants.MESSAGE_TOGTHER)){
            presenterTogther = new MomentPresenterTogther(this);
            TogtherAdapter.Builder<Togther> builder = new TogtherAdapter.Builder<>(this);
            builder.addType(EmptyMomentsVHTogther.class, MomentsType.EMPTY_CONTENT, R.layout.moments_empty_content)
                    .addType(MultiImageMomentsVHTogther.class, MomentsType.MULTI_IMAGES, R.layout.moments_multi_image)
                    .addType(TextOnlyMomentsVHTogther.class, MomentsType.TEXT_ONLY, R.layout.moments_only_text)
                    .addType(WebMomentsVHTogther.class, MomentsType.WEB, R.layout.moments_web)
                    .setData(togtherList)
                    .setPresenter(presenterTogther);
            togtherAdapter = builder.build();
            recycler.setAdapter(togtherAdapter);
        }


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        widgetCommentTogther.setOnCommentSendClickListener(onCommentSendClickListener);
        initKeyboardHeightObserver();
    }

    @Override
    public void initData() {
        if(mType.equals(Constants.MESSAGE_SHARE)) {
            momentsRequest.setOnResponseListener(momentsRequestCallBack);
            momentsRequest.setRequestType(REQUEST_REFRESH);
            momentsRequest.setCurPage(0);
            momentsRequest.setKey(mKey);
            momentsRequest.setCache(isReadCache);
            momentsRequest.execute();
        }else{
            togtherRequest.setOnResponseListener(TogtherRequestCallBack);
            togtherRequest.setRequestType(REQUEST_REFRESH);
            togtherRequest.setCurPage(0);
            togtherRequest.setKey(mKey);
            togtherRequest.setCache(isReadCache);
            togtherRequest.execute();
        }
        isReadCache = false;
    }

    //==============================================
    private SimpleResponseListener<List<Share>> momentsRequestCallBack = new SimpleResponseListener<List<Share>>() {
        @Override
        public void onSuccess(List<Share> response, int requestType) {
            swipeRefreshLayout.setRefreshing(false);
            ivLoadState.setVisibility(View.GONE);
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        Constants.getInstance().setMShare(response.get(0));
                        Intent gIntent = new Intent(AboutActivity.this, ShareInfoActivity.class);
                        startActivityForResult(gIntent,1);
                    }
                    break;
                case REQUEST_LOADMORE:
                    break;
            }
        }


        @Override
        public void onError(BmobException e, int requestType) {
            super.onError(e, requestType);
            swipeRefreshLayout.setRefreshing(false);
            ToastUtil3.showToast(AboutActivity.this,"数据异常,请稍后重试");
            finish();
        }

        @Override
        public void onProgress(int pro) {
        }
    };
    //=============================================================View's method

    private SimpleResponseListener<List<Togther>> TogtherRequestCallBack = new SimpleResponseListener<List<Togther>>() {
        @Override
        public void onSuccess(List<Togther> response, int requestType) {
            swipeRefreshLayout.setRefreshing(false);
            ivLoadState.setVisibility(View.GONE);
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        togtherAdapter.updateData(response);
                    }
                    break;
                case REQUEST_LOADMORE:
                    break;
            }
        }

        @Override
        public void onError(BmobException e, int requestType) {
            super.onError(e, requestType);
            swipeRefreshLayout.setRefreshing(false);
            if(togtherList == null || togtherList.size() == 0){
                ivLoadState.setImageDrawable(ContextCompat.getDrawable(AboutActivity.this,R.mipmap.icon_load_erro));
                ivLoadState.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onProgress(int pro) {

        }
    };
    // TODO: 2016/12/13 进一步优化对齐功能
    private void initKeyboardHeightObserver() {
        //观察键盘弹出与消退
        KeyboardControlMnanager.observerKeyboardVisibleChange(this, new KeyboardControlMnanager.OnKeyboardStateChangeListener() {
            View anchorView;

            @Override
            public void onKeyboardChange(int keyboardHeight, boolean isVisible) {
                int commentType = widgetCommentTogther.getCommentType();
                if (isVisible) {
                    //定位评论框到view
                    anchorView = alignCommentBoxToView(commentType);
                } else {
                    //定位到底部
                    widgetCommentTogther.dismissCommentBox(false);
                    alignCommentBoxToViewWhenDismiss(commentType, anchorView);
                }
            }
        });
    }


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
        int firstPos = linearLayoutManager.findFirstVisibleItemPosition();
        int itemPos = widgetCommentTogther.getDataPos() - firstPos;
        final View itemView = recycler.getChildAt(itemPos);
        if (itemView == null) {
            Logger.d("获取不到itemView，pos = " + itemPos);
            return null;
        }
        if (commentType == CommentBoxTogther.CommentType.TYPE_CREATE) {
            //对齐到动态底部
            int scrollY = calcuateMomentsViewOffset(itemView);
            recycler.smoothScrollBy(0, scrollY);
            return itemView;
        } else {
            //对齐到对应的评论
            CommentWidget commentWidget = widgetCommentTogther.getCommentWidget();
            if (commentWidget == null) return null;
            int scrollY = calcuateCommentWidgetOffset(commentWidget);
            recycler.smoothScrollBy(0, scrollY);
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
        int decorViewHeight = getWindow().getDecorView().getHeight();
        int alignScrollY;
        if (commentType == CommentBoxTogther.CommentType.TYPE_CREATE) {
            alignScrollY = decorViewHeight - anchorView.getBottom() - widgetCommentTogther.getHeight();
        } else {
            Rect rect = new Rect();
            anchorView.getGlobalVisibleRect(rect);
            alignScrollY = decorViewHeight - rect.bottom - widgetCommentTogther.getHeight();
        }
        recycler.smoothScrollBy(0, -alignScrollY);
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
     * @return
     */
    private int getCommentBoxViewTopInWindow() {
        if (commentBoxViewLocation == null) commentBoxViewLocation = new int[2];
        if (widgetCommentTogther == null) return 0;
        if (commentBoxViewLocation[1] != 0) return commentBoxViewLocation[1];
        widgetCommentTogther.getLocationInWindow(commentBoxViewLocation);
        return commentBoxViewLocation[1];
    }




    //=============================================================call back
    private CommentBoxTogther.OnCommentSendClickListener onCommentSendClickListener = new CommentBoxTogther.OnCommentSendClickListener() {
        @Override
        public void onCommentSendClick(View v, Togther momentid, Students commentAuthorId, String commentContent) {
            if (TextUtils.isEmpty(commentContent)) return;
            int itemPos = widgetCommentTogther.getDataPos();
            if (itemPos < 0 || itemPos > togtherAdapter.getItemCount()) return;
            List<CommentInfo> commentInfos = togtherAdapter.findData(itemPos).getCommentList();
            presenterTogther.addComment(itemPos, momentid, commentAuthorId, commentContent, commentInfos);
            widgetCommentTogther.clearDraft();
            widgetCommentTogther.dismissCommentBox(true);

            presenterTogther.addMessage(momentid.getAuthor().getObjectId()
                    ,momentid.getObjectId()
                    ,Constants.MESSAGE_TOGTHER
                    ,""+Constants.getInstance().getUser().getNick_name()+"说："+commentContent);
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            finish();
        }
    }

    @Override
    public void onLikeChange(int itemPos, List<LikesInfo> likeUserList) {
        Togther momentsInfo = togtherAdapter.findData(itemPos);
        if (momentsInfo != null) {
            if(!ToolUtil.isListEmpty(likeUserList)) {
                Logger.d(likeUserList.get(0).getUserInfo().getUsername() + "新增评论：" + likeUserList.get(0).getObjectId());
            }else{
                Logger.d("新增评论清空");
            }
            momentsInfo.setLikesList(likeUserList);
            togtherAdapter.notifyItemChanged(itemPos);
        }
    }

    @Override
    public void onCommentChange(int itemPos, List<CommentInfo> commentInfoList) {
        Togther momentsInfo = togtherAdapter.findData(itemPos);
        if (momentsInfo != null) {
            momentsInfo.setCommentList(commentInfoList);
            togtherAdapter.notifyItemChanged(itemPos);
        }
    }

    @Override
    public void showCommentBox(int itemPos, Togther momentid, CommentWidget commentWidget) {
        widgetCommentTogther.setDataPos(itemPos);
        widgetCommentTogther.setCommentWidget(commentWidget);
        widgetCommentTogther.toggleCommentBox(momentid, commentWidget == null ? null : commentWidget.getData(), false);
    }

    @Override
    public void onMessageChange(String itemPos, String content) {
    }
}
