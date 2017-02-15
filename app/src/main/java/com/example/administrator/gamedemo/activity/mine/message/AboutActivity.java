package com.example.administrator.gamedemo.activity.mine.message;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.adapter.CircleMomentsAdapter;
import com.example.administrator.gamedemo.adapter.TogtherAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.core.MomentsType;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.utils.KeyboardControlMnanager;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenter;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenterTogther;
import com.example.administrator.gamedemo.utils.view.IMomentView;
import com.example.administrator.gamedemo.utils.view.IMomentViewTogther;
import com.example.administrator.gamedemo.utils.viewholder.EmptyMomentsVH;
import com.example.administrator.gamedemo.utils.viewholder.EmptyMomentsVHTogther;
import com.example.administrator.gamedemo.utils.viewholder.MultiImageMomentsVH;
import com.example.administrator.gamedemo.utils.viewholder.MultiImageMomentsVHTogther;
import com.example.administrator.gamedemo.utils.viewholder.TextOnlyMomentsVH;
import com.example.administrator.gamedemo.utils.viewholder.TextOnlyMomentsVHTogther;
import com.example.administrator.gamedemo.utils.viewholder.WebMomentsVH;
import com.example.administrator.gamedemo.utils.viewholder.WebMomentsVHTogther;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
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
import butterknife.ButterKnife;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;

/**
 * @auther lixu
 * Created by lixu on 2017/2/14 0014.
 */
public class AboutActivity extends BaseActivity implements onRefreshListener2, IMomentView,IMomentViewTogther, CircleRecyclerView.OnPreDispatchTouchListener {
    private static final int REQUEST_REFRESH = 0x10;
    private static final int REQUEST_LOADMORE = 0x11;
    public static final String KEYCODE = "KEYCODE";
    public static final String TYPECODE = "TYPECODE";
    @BindView(R.id.recycler)
    CircleRecyclerView recycler;
    @BindView(R.id.widget_comment)
    CommentBox widgetComment;

    @BindView(R.id.widget_comment_togther)
    CommentBoxTogther widgetCommentTogther;

    private CircleMomentsAdapter adapter;
    private TogtherAdapter togtherAdapter;

    private List<Share> momentsInfoList;
    private List<Togther> togtherList;


    private ShareRequest momentsRequest;
    private TogtherRequest togtherRequest;
    private MomentPresenter presenter;
    private MomentPresenterTogther presenterTogther;
    // private List<Share> responseTemp;
    private boolean isReadCache = true;
    private  HostViewHolder hostViewHolder;
    private String mKey;
    private String mType;
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
                recycler.getRecyclerView().smoothScrollToPosition(0);
            }
        });

        Intent gIntent = getIntent();
        mKey = gIntent.getExtras().getString(KEYCODE);
        mType = gIntent.getExtras().getString(TYPECODE);

        if(mType.equals(Constants.MESSAGE_SHARE)){
            widgetComment.setVisibility(View.VISIBLE);
            widgetCommentTogther.setVisibility(View.GONE);
        }else{
            widgetComment.setVisibility(View.GONE);
            widgetCommentTogther.setVisibility(View.VISIBLE);
        }

        momentsInfoList = new ArrayList<>();
        togtherList = new ArrayList<>();

        momentsRequest = new ShareRequest();
        togtherRequest = new TogtherRequest();

        presenter = new MomentPresenter(this);
        presenterTogther = new MomentPresenterTogther(this);

        hostViewHolder = new HostViewHolder(this);

        recycler.setOnRefreshListener(this);
        recycler.setOnPreDispatchTouchListener(this);

        recycler.addHeaderView(hostViewHolder.getView());

        if(mType.equals(Constants.MESSAGE_SHARE)) {
            widgetComment.setOnCommentSendClickListener(onCommentSendClickListener);
        }else {
            widgetCommentTogther.setOnCommentSendClickListener(onCommentSendClickListenerTogther);
        }

        if(mType.equals(Constants.MESSAGE_SHARE)){
            CircleMomentsAdapter.Builder<Share> builder = new CircleMomentsAdapter.Builder<>(this);
            builder.addType(EmptyMomentsVH.class, MomentsType.EMPTY_CONTENT, R.layout.moments_empty_content_share)
                    .addType(MultiImageMomentsVH.class, MomentsType.MULTI_IMAGES, R.layout.moments_multi_image_share)
                    .addType(TextOnlyMomentsVH.class, MomentsType.TEXT_ONLY, R.layout.moments_only_text_share)
                    .addType(WebMomentsVH.class, MomentsType.WEB, R.layout.moments_web_share)
                    .setData(momentsInfoList)
                    .setPresenter(presenter);
            adapter = builder.build();
            recycler.setAdapter(adapter);
        }else{
            TogtherAdapter.Builder<Togther> builder = new TogtherAdapter.Builder<>(this);
            builder.addType(EmptyMomentsVHTogther.class, MomentsType.EMPTY_CONTENT, R.layout.moments_empty_content_share)
                    .addType(MultiImageMomentsVHTogther.class, MomentsType.MULTI_IMAGES, R.layout.moments_multi_image_share)
                    .addType(TextOnlyMomentsVHTogther.class, MomentsType.TEXT_ONLY, R.layout.moments_only_text_share)
                    .addType(WebMomentsVHTogther.class, MomentsType.WEB, R.layout.moments_web_share)
                    .setData(togtherList)
                    .setPresenter(presenterTogther);
            togtherAdapter = builder.build();
            recycler.setAdapter(adapter);
        }


    }

    @Override
    public void initData() {
        isReadCache = true;
        recycler.autoRefresh();
        initKeyboardHeightObserver();
    }

    // TODO: 2016/12/13 进一步优化对齐功能
    private void initKeyboardHeightObserver() {
        //观察键盘弹出与消退
        KeyboardControlMnanager.observerKeyboardVisibleChange(this, new KeyboardControlMnanager.OnKeyboardStateChangeListener() {
            View anchorView;

            @Override
            public void onKeyboardChange(int keyboardHeight, boolean isVisible) {
             //   keyHeight = keyboardHeight;
                int commentType;
                if(mType.equals(Constants.MESSAGE_SHARE)) {
                    commentType = widgetComment.getCommentType();
                }else{
                    commentType= widgetCommentTogther.getCommentType();
                }


                if (isVisible) {
          //          commentBox.setMinimumHeight(keyboardHeight - 56);
//                        commentBox.setMinimumHeight(keyboardHeight);
                    //定位评论框到view
                    anchorView = alignCommentBoxToView(commentType);
                } else {
                    //定位到底部

                    if(mType.equals(Constants.MESSAGE_SHARE)){
                        widgetComment.dismissCommentBox(false);
                    }else{
                        widgetCommentTogther.dismissCommentBox(false);
                    }
                    alignCommentBoxToViewWhenDismiss(commentType, anchorView);
                }
            }
        });
    }


    @Override
    public void onRefresh() {
        if(mType.equals(Constants.MESSAGE_SHARE)) {
            momentsRequest.setOnResponseListener(momentsRequestCallBack);
            momentsRequest.setRequestType(REQUEST_REFRESH);
            momentsRequest.setCurPage(0);
            momentsRequest.setKey(mKey);
            momentsRequest.setCache(isReadCache);
            momentsRequest.execute();
            isReadCache = false;
        }else{
            togtherRequest.setOnResponseListener(momentsRequestCallBackTogther);
            togtherRequest.setRequestType(REQUEST_REFRESH);
            togtherRequest.setCurPage(0);
            togtherRequest.setKey(mKey);
            togtherRequest.setCache(isReadCache);
            togtherRequest.execute();
            isReadCache = false;
        }


    }

    @Override
    public void onLoadMore() {
        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_LOADMORE);
        momentsRequest.setKey(mKey);
        momentsRequest.execute();
    }

    //=============================================================call back
    private CommentBox.OnCommentSendClickListener onCommentSendClickListener = new CommentBox.OnCommentSendClickListener() {
        @Override
        public void onCommentSendClick(View v, Share momentid, Students commentAuthorId, String commentContent) {
            if (TextUtils.isEmpty(commentContent)) return;
            int itemPos;
            if(mType.equals(Constants.MESSAGE_SHARE)) {
                 itemPos = widgetComment.getDataPos();
            }else{
                itemPos = widgetCommentTogther.getDataPos();
            }

            if (itemPos < 0 || itemPos > adapter.getItemCount()) return;
            List<CommentInfo> commentInfos = adapter.findData(itemPos).getCommentList();
            presenter.addComment(itemPos, momentsInfoList.get(itemPos), commentAuthorId, commentContent, commentInfos);

            if(mType.equals(Constants.MESSAGE_SHARE)){


            widgetComment.clearDraft();
            widgetComment.dismissCommentBox(true);
            presenter.addMessage(momentid.getAuthor().getObjectId()
                    ,momentid.getObjectId()
                    , Constants.MESSAGE_SHARE
                    ,""+Constants.getInstance().getUser().getNick_name()+"说："+commentContent);
            }else{
                widgetCommentTogther.clearDraft();
                widgetCommentTogther.dismissCommentBox(true);
                presenter.addMessage(momentid.getAuthor().getObjectId()
                        ,momentid.getObjectId()
                        , Constants.MESSAGE_TOGTHER
                        ,""+Constants.getInstance().getUser().getNick_name()+"说："+commentContent);
            }
        }
    };

    private CommentBoxTogther.OnCommentSendClickListener onCommentSendClickListenerTogther = new CommentBoxTogther.OnCommentSendClickListener() {
        @Override
        public void onCommentSendClick(View v, Togther momentid, Students commentAuthorId, String commentContent) {
            if (TextUtils.isEmpty(commentContent)) return;
            int itemPos;
            if(mType.equals(Constants.MESSAGE_SHARE)) {
                 itemPos = widgetComment.getDataPos();
            }else{
                itemPos = widgetCommentTogther.getDataPos();
            }

            if (itemPos < 0 || itemPos > adapter.getItemCount()) return;
            List<CommentInfo> commentInfos = adapter.findData(itemPos).getCommentList();
            presenter.addComment(itemPos, momentsInfoList.get(itemPos), commentAuthorId, commentContent, commentInfos);

            if(mType.equals(Constants.MESSAGE_SHARE)){
            widgetComment.clearDraft();
            widgetComment.dismissCommentBox(true);
            presenter.addMessage(momentid.getAuthor().getObjectId()
                    ,momentid.getObjectId()
                    , Constants.MESSAGE_SHARE
                    ,""+Constants.getInstance().getUser().getNick_name()+"说："+commentContent);
            }else{
                widgetCommentTogther.clearDraft();
                widgetCommentTogther.dismissCommentBox(true);
                presenter.addMessage(momentid.getAuthor().getObjectId()
                        ,momentid.getObjectId()
                        , Constants.MESSAGE_TOGTHER
                        ,""+Constants.getInstance().getUser().getNick_name()+"说："+commentContent);
            }
        }
    };

    //call back block
    //==============================================
    private boolean isOne = true;
    private SimpleResponseListener<List<Share>> momentsRequestCallBack = new SimpleResponseListener<List<Share>>() {
        @Override
        public void onSuccess(List<Share> response, int requestType) {
            recycler.compelete();
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        momentsInfoList.clear();
                        momentsInfoList.addAll(response);
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
            recycler.compelete();
        }

        @Override
        public void onProgress(int pro) {

        }
    };


    private SimpleResponseListener<List<Togther>> momentsRequestCallBackTogther = new SimpleResponseListener<List<Togther>>() {
        @Override
        public void onSuccess(List<Togther> response, int requestType) {
            recycler.compelete();
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        togtherList.clear();
                        togtherList.addAll(response);
                        togtherAdapter.updateData(response);
                    }
                    break;
                case REQUEST_LOADMORE:
                    togtherList.clear();
                    togtherList.addAll(response);
                    togtherAdapter.addMore(response);
                    break;
            }
        }

        @Override
        public void onError(BmobException e, int requestType) {
            super.onError(e, requestType);
            recycler.compelete();
        }

        @Override
        public void onProgress(int pro) {

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
    @Override
    public void onCollectChange(int itemPos, List<Students> collectUserList) {
        Share momentsInfo = adapter.findData(itemPos);
        if (momentsInfo != null) {
            momentsInfo.setCollectList(collectUserList);
            adapter.notifyItemChanged(itemPos);
        }
        ToastUtil3.showToast(this,"BingGo(*^__^*)");
    }

    @Override
    public void onMessageChange(String itemPos, String content) {
        Logger.d("推送目标人："+itemPos+"推送内容:"+content);
        BmobPushManager bmobPush = new BmobPushManager();
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        query.addWhereEqualTo("installationId", itemPos);
        bmobPush.setQuery(query);
        bmobPush.pushMessage(content);
    }

    /**
     * 点击发送
     *
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

    @Override
    public void showCommentBox(int itemPos, final Togther momentid, CommentWidget commentWidget) {
        Logger.d("showCommentBox");

        if (commentWidget == null) {
            Logger.d("commentWidget == null--itemPos=" + itemPos);
        } else {
            Logger.d("commentWidget != null--itemPos==" + itemPos);
        }


        widgetCommentTogther.setDataPos(itemPos);
        widgetCommentTogther.setCommentWidget(commentWidget);
        widgetCommentTogther.toggleCommentBox(momentid, commentWidget == null ? null : commentWidget.getData(), false);

    }

    /**
     * 点击评论
     *
     * @param itemPos
     * @param momentid
     * @param commentWidget
     */
    @Override
    public void showCommentBox(int itemPos, Share momentid, CommentWidget commentWidget) {
        Logger.d("showCommentBox");

        if (commentWidget == null) {
            Logger.d("commentWidget == null--itemPos=" + itemPos);
        } else {
            Logger.d("commentWidget != null--itemPos==" + itemPos);
        }


        widgetComment.setDataPos(itemPos);
        widgetComment.setCommentWidget(commentWidget);
        widgetComment.toggleCommentBox(momentid, commentWidget == null ? null : commentWidget.getData(), false);
    }



    @Override
    public boolean onPreTouch(MotionEvent ev) {

        if(mType.equals(Constants.MESSAGE_SHARE)) {

            if (widgetComment != null && widgetComment.isShowing()) {
                widgetComment.dismissCommentBox(false);
                return true;
            }

        }else{

            if (widgetCommentTogther != null && widgetCommentTogther.isShowing()) {
                widgetCommentTogther.dismissCommentBox(false);
                return true;
            }

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
        int firstPos = recycler.findFirstVisibleItemPosition();
        int itemPos;
        if(mType.equals(Constants.MESSAGE_SHARE)) {
            itemPos = widgetComment.getDataPos() - firstPos + recycler.getHeaderViewCount();
        }else{
            itemPos = widgetCommentTogther.getDataPos() - firstPos + recycler.getHeaderViewCount();
        }


        final View itemView = recycler.getRecyclerView().getChildAt(itemPos);
        if (itemView == null) {
            Logger.e("获取不到itemView，pos = " + itemPos);
            return null;
        }
        if (commentType == CommentBox.CommentType.TYPE_CREATE) {
            //对齐到动态底部
            int scrollY = calcuateMomentsViewOffset(itemView);
            recycler.getRecyclerView().smoothScrollBy(0, scrollY);
            return itemView;
        } else {
            //对齐到对应的评论
            CommentWidget commentWidget;
            if(mType.equals(Constants.MESSAGE_SHARE)) {
                commentWidget = widgetComment.getCommentWidget();
            }else{
                commentWidget = widgetCommentTogther.getCommentWidget();
            }


            if (commentWidget == null) return null;


            int scrollY = calcuateCommentWidgetOffset(commentWidget);
            recycler.getRecyclerView().smoothScrollBy(0, scrollY );
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
        int decorViewHeight = this.getWindow().getDecorView().getHeight();
        int alignScrollY;
        if (commentType == CommentBox.CommentType.TYPE_CREATE) {

            if(mType.equals(Constants.MESSAGE_SHARE)) {


                alignScrollY = decorViewHeight - anchorView.getBottom() - widgetComment.getHeight();
            }else{
                alignScrollY = decorViewHeight - anchorView.getBottom() - widgetCommentTogther.getHeight();
            }

        } else {
            Rect rect = new Rect();
            anchorView.getGlobalVisibleRect(rect);

            if(mType.equals(Constants.MESSAGE_SHARE)) {
                alignScrollY = decorViewHeight - rect.bottom - widgetComment.getHeight();
            }else{
                alignScrollY = decorViewHeight - rect.bottom - widgetCommentTogther.getHeight();
            }

        }
        recycler.getRecyclerView().smoothScrollBy(0, -alignScrollY);
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
        if(mType.equals(Constants.MESSAGE_SHARE)) {
            if (widgetComment == null) return 0;
            if (commentBoxViewLocation[1] != 0) return commentBoxViewLocation[1];
            widgetComment.getLocationInWindow(commentBoxViewLocation);
        }else{
            if (widgetCommentTogther == null) return 0;
            if (commentBoxViewLocation[1] != 0) return commentBoxViewLocation[1];
            widgetCommentTogther.getLocationInWindow(commentBoxViewLocation);
        }
        return commentBoxViewLocation[1];
    }

    private static class HostViewHolder {
        private View rootView;
        private ImageView friend_wall_pic;

        public HostViewHolder(Context context) {
            this.rootView = LayoutInflater.from(context).inflate(R.layout.circle_host_header_share, null);
            this.friend_wall_pic = (ImageView) rootView.findViewById(R.id.friend_wall_pic);
            this.rootView.setVisibility(View.GONE);
        }


        public View getView() {
            return rootView;
        }

    }

}
