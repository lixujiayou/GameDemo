package com.example.administrator.gamedemo.activity.mine.togther;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.LoginActivity;
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
import com.example.administrator.gamedemo.widget.request.MomentsRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.example.administrator.gamedemo.widget.request.TogtherRequest;
import com.example.administrator.gamedemo.widget.simage.Crop;
import com.orhanobut.logger.Logger;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by lixu on 2016/12/22 0022.
 * 一起
 */
public class TogetherActivity extends BaseActivity implements onRefreshListener2, IMomentViewTogther, CircleRecyclerView.OnPreDispatchTouchListener {
    private static final int REQUEST_REFRESH = 0x10;
    private static final int REQUEST_LOADMORE = 0x11;
    private static final int CAMERA_REQUEST_CODE = 1458;
    private static final int GALLERY_REQUEST_CODE = 1450;
//    @BindView(R.id.swipe_refresh)
//    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.main_content)
    CoordinatorLayout mainContent;

    @BindView(R.id.recycler)
     CircleRecyclerView circleRecyclerView;
    @BindView(R.id.widget_comment)
     CommentBoxTogther commentBox;

    private HostViewHolder hostViewHolder;
    private TogtherAdapter adapter;
    private List<Togther> momentsInfoList;
    //request
    private TogtherRequest togtherRequest;
    private MomentPresenterTogther presenter;
    private File mTempDir;//修改封面 选取的图片路径
    private SweetAlertDialog pDialog;


    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_togther);
    }

    @Override
    public void initViews() {
        momentsInfoList = new ArrayList<>();
        togtherRequest = new TogtherRequest();

        mTempDir = new File( Environment.getExternalStorageDirectory(),"bibleAsk");
        if(!mTempDir.exists()){
            mTempDir.mkdirs();
        }


        mToolbar.setTitle("一起");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_jiahao:
                        Intent gIntent = new Intent(TogetherActivity.this,SendTogtherActivity.class);
                        startActivityForResult(gIntent,1);
                        break;
                }
                return true;
            }
        });


        presenter = new MomentPresenterTogther(this);

        hostViewHolder = new HostViewHolder(this);
        circleRecyclerView = (CircleRecyclerView) findViewById(R.id.recycler);
        circleRecyclerView.setOnRefreshListener(this);
        circleRecyclerView.setOnPreDispatchTouchListener(this);
        circleRecyclerView.addHeaderView(hostViewHolder.getView());

        commentBox.setOnCommentSendClickListener(onCommentSendClickListener);

        TogtherAdapter.Builder<Togther> builder = new TogtherAdapter.Builder<>(this);
        builder.addType(EmptyMomentsVHTogther.class, MomentsType.EMPTY_CONTENT, R.layout.moments_empty_content)
                .addType(MultiImageMomentsVHTogther.class, MomentsType.MULTI_IMAGES, R.layout.moments_multi_image)
                .addType(TextOnlyMomentsVHTogther.class, MomentsType.TEXT_ONLY, R.layout.moments_only_text)
                .addType(WebMomentsVHTogther.class, MomentsType.WEB, R.layout.moments_web)
                .setData(momentsInfoList)
                .setPresenter(presenter);
        adapter = builder.build();
        circleRecyclerView.setAdapter(adapter);
        circleRecyclerView.autoRefresh();

        hostViewHolder.loadHostData(Constants.getInstance().getUser());
        initKeyboardHeightObserver();
    }

    @Override
    public void initData() {
//        swipeRefresh.setRefreshing(true);
//        togtherRequest = new TogtherRequest();
//        togtherRequest.setOnResponseListener(momentsRequestCallBack);
//        togtherRequest.setRequestType(REQUEST_REFRESH);
//        togtherRequest.setCurPage(0);
//        togtherRequest.execute();
    }



        @OnClick(R.id.toolbar)
        public void onClick() {
            circleRecyclerView.getRecyclerView().smoothScrollToPosition(0);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
            getMenuInflater().inflate(R.menu.menu_togther, menu);
            return true;
        }

    // TODO: 2016/12/13 进一步优化对齐功能
    private void initKeyboardHeightObserver() {
        //观察键盘弹出与消退
        KeyboardControlMnanager.observerKeyboardVisibleChange(this, new KeyboardControlMnanager.OnKeyboardStateChangeListener() {
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
        togtherRequest.setOnResponseListener(momentsRequestCallBack);
        togtherRequest.setRequestType(REQUEST_REFRESH);
        togtherRequest.setCurPage(0);
        togtherRequest.execute();
    }

    @Override
    public void onLoadMore() {
        togtherRequest.setOnResponseListener(momentsRequestCallBack);
        togtherRequest.setRequestType(REQUEST_LOADMORE);
        togtherRequest.execute();
    }

    //call back block
    //==============================================
    private SimpleResponseListener<List<Togther>> momentsRequestCallBack = new SimpleResponseListener<List<Togther>>() {
        @Override
        public void onSuccess(List<Togther> response, int requestType) {
            circleRecyclerView.compelete();
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        adapter.updateData(response);
                    }
                    break;
                case REQUEST_LOADMORE:
                    adapter.addMore(response);
                    break;
            }
        }

        @Override
        public void onError(BmobException e, int requestType) {
            super.onError(e, requestType);
            circleRecyclerView.compelete();
        }

        @Override
        public void onProgress(int pro) {

        }
    };


    //=============================================================View's method
    @Override
    public void onLikeChange(int itemPos, List<Students> likeUserList) {
        Togther momentsInfo = adapter.findData(itemPos);
        if (momentsInfo != null) {
            momentsInfo.setLikesList(likeUserList);
            adapter.notifyItemChanged(itemPos);
        }
    }

    @Override
    public void onCommentChange(int itemPos, List<CommentInfo> commentInfoList) {
        Togther momentsInfo = adapter.findData(itemPos);
        if (momentsInfo != null) {
            momentsInfo.setCommentList(commentInfoList);
            adapter.notifyItemChanged(itemPos);
        }
    }

    @Override
    public void showCommentBox(int itemPos, Togther momentid, CommentWidget commentWidget) {
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
            Logger.d("获取不到itemView，pos = " + itemPos);
            return null;
        }
        if (commentType == CommentBoxTogther.CommentType.TYPE_CREATE) {
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
        int decorViewHeight = getWindow().getDecorView().getHeight();
        int alignScrollY;
        if (commentType == CommentBoxTogther.CommentType.TYPE_CREATE) {
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
        if (commentBox == null) return 0;
        if (commentBoxViewLocation[1] != 0) return commentBoxViewLocation[1];
        commentBox.getLocationInWindow(commentBoxViewLocation);
        return commentBoxViewLocation[1];
    }




    //=============================================================call back
    private CommentBoxTogther.OnCommentSendClickListener onCommentSendClickListener = new CommentBoxTogther.OnCommentSendClickListener() {
        @Override
        public void onCommentSendClick(View v, Togther momentid, Students commentAuthorId, String commentContent) {
            if (TextUtils.isEmpty(commentContent)) return;
            int itemPos = commentBox.getDataPos();
            if (itemPos < 0 || itemPos > adapter.getItemCount()) return;
            List<CommentInfo> commentInfos = adapter.findData(itemPos).getCommentList();
            presenter.addComment(itemPos, momentid, commentAuthorId, commentContent, commentInfos);
            commentBox.clearDraft();
            commentBox.dismissCommentBox(true);
        }
    };


    private  class HostViewHolder {
        private View rootView;
        private ImageView friend_wall_pic;
        private ImageView friend_avatar;
        private ImageView message_avatar;
        private TextView message_detail;
        private TextView hostid;

        public HostViewHolder(Context context) {
            this.rootView = LayoutInflater.from(context).inflate(R.layout.circle_host_header_togther, null);
            this.hostid = (TextView) rootView.findViewById(R.id.host_id);
            this.friend_wall_pic = (ImageView) rootView.findViewById(R.id.friend_wall_pic);
            this.friend_avatar = (ImageView) rootView.findViewById(R.id.friend_avatar);
            this.message_avatar = (ImageView) rootView.findViewById(R.id.message_avatar);
            this.message_detail = (TextView) rootView.findViewById(R.id.message_detail);

            this.friend_wall_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //切换封面
                    if(isLogin()) {
                        showPhotoDialog();
                    }
                }
            });

            this.friend_avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isLogin()){

                    }else{
                        Intent lIntent = new Intent(TogetherActivity.this, LoginActivity.class);
                        startActivityForResult(lIntent,1);
                    }
                }
            });
        }

        public void loadHostData(Students hostInfo) {
            if (hostInfo == null){
                hostid.setText("未登陆");
            }else {
                ImageLoadMnanger.INSTANCE.loadImageToCover(friend_wall_pic, hostInfo.getCover().getFileUrl());
                ImageLoadMnanger.INSTANCE.loadImage(friend_avatar, hostInfo.getUser_icon().getFileUrl());
                hostid.setText(hostInfo.getNick_name());
            }
        }
        public View getView() {
            return rootView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 3){
            circleRecyclerView.autoRefresh();
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:   // 调用相机拍照
                    File temp = new File(mCurrentPhotoPath);
                    beginCrop(Uri.fromFile(temp));
                    break;
                case GALLERY_REQUEST_CODE:  // 直接从相册获取
                    beginCrop(data.getData());
                    break;
                case Crop.REQUEST_CROP:  // 裁剪图片结果
                    handleCrop( resultCode, data);
                    break;
            }
        }
    }

    private Dialog dialog_help_2;
    View view_2;
    private String mCurrentPhotoPath; //当前选取的相片路径
    helpdialog_item_2 hi_2 = null;
    private void showPhotoDialog() {
        if(hi_2==null){

            hi_2= new helpdialog_item_2();
            view_2 = getLayoutInflater().inflate(R.layout.dialog_out_login, null);
            hi_2.tv_help1 = (TextView) view_2.findViewById(R.id.bt_help1);
            hi_2.tv_help2 = (TextView) view_2.findViewById(R.id.bt_help2);
            hi_2.tv_helpcancle = (TextView) view_2.findViewById(R.id.bt_helpcancle);
            dialog_help_2 = new Dialog(this, R.style.transparentFrameWindowStyle);
            dialog_help_2.setContentView(view_2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = dialog_help_2.getWindow();
            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 0;
            wl.y = this.getWindowManager().getDefaultDisplay().getHeight();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog_help_2.onWindowAttributesChanged(wl);
            dialog_help_2.setCanceledOnTouchOutside(true);
            view_2.setTag(hi_2);
        }else{
            hi_2 = (helpdialog_item_2) view_2.getTag();
        }
        hi_2.tv_help1.setText("相册选取");
        hi_2.tv_help2.setText("相机拍照");
        hi_2.tv_help1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
                dialog_help_2.dismiss();
            }
        });
        hi_2.tv_help2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = "love" + String.valueOf( System.currentTimeMillis());
                Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //下面这句指定调用相机拍照后的照片存储的路径
                File cropFile = new File( mTempDir, fileName);
                Uri fileUri = Uri.fromFile( cropFile);
                takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                mCurrentPhotoPath = fileUri.getPath();
                startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
                dialog_help_2.dismiss();
            }
        });
        hi_2.tv_helpcancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_help_2.dismiss();
            }
        });
        dialog_help_2.show();
    }

    class helpdialog_item_2{
        TextView tv_help1;
        TextView tv_help2;
        TextView tv_helpcancle;
    }


    private Uri imgUrlToImageview;
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == Activity.RESULT_OK) {
            imgUrlToImageview = Crop.getOutput(result);
            upLoadImg(Crop.getOutput(result).getPath());
        } else if (resultCode == Crop.RESULT_ERROR) {
            ToastUtil3.showToast(TogetherActivity.this,"裁剪图片失败，请重试");
        }
    }

    /**
     * 裁剪图片
     * @param source
     */
    private void beginCrop(Uri source) {
        String fileName = "cover_" + String.valueOf( System.currentTimeMillis()+".image");
        File cropFile = new File( mTempDir, fileName);
        Uri outputUri = Uri.fromFile( cropFile);
        new Crop(source).output(outputUri).withAspect(4,3).start(this);
    }

    /**
     * 上传封面
     */
    private BmobFile bmobFileCover;
    private void upLoadImg(String imgUrl){
        showProgressBarDialog(TogetherActivity.this);
        bmobFileCover = new BmobFile(new File(imgUrl));
        bmobFileCover.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    cUser.setCover(bmobFileCover);
                    cUser.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            dimssProgressDialog();
                        if(e == null){
                            hostViewHolder.friend_wall_pic.setImageURI(imgUrlToImageview);
                            ToastUtil3.showToast(TogetherActivity.this,"上传封面成功");
                        }else{
                            ToastUtil3.showToast(TogetherActivity.this,"上传封面失败，请检查网络并重试"+e);
                        }
                        }
                    });
                }else{
                    ToastUtil3.showToast(TogetherActivity.this,"上传封面失败，请检查网络并重试"+e);
                    dimssProgressDialog();
                }
            }
        });

    }



    public void showProgressBarDialog(final Activity mContext){
        try {
                pDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.setTitleText("正在上传封面，请稍等");
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

    /**
     * 设置进度
     * @param pro
     */
    public void setProgressDialogText(String pro){
        if(pDialog == null){
            return;
        }
        pDialog.setTitleText(pro);
    }


}
