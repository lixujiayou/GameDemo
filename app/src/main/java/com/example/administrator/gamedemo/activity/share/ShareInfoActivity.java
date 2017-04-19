package com.example.administrator.gamedemo.activity.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.LoginActivity;
import com.example.administrator.gamedemo.adapter.OnlineAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.ReshEvent;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.MyEditText;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.utils.base.BounceScrollView;
import com.example.administrator.gamedemo.utils.presenter.SharePresenter;
import com.example.administrator.gamedemo.utils.view.IShareView;
import com.example.administrator.gamedemo.widget.SoftKeyboardStateHelper;
import com.example.administrator.gamedemo.widget.popup.DeleteCommentPopup;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @auther lixu
 * Created by lixu on 2017/4/16 0016.
 */
public class ShareInfoActivity extends BaseActivity implements IShareView {
    @BindView(R.id.tv_topic)
    TextView tvTopic;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.ll_include)
    LinearLayout llInclude;
    @BindView(R.id.recyler_comment)
    RecyclerView recylerComment;
    @BindView(R.id.tv_reminder)
    TextView tvReminder;
    @BindView(R.id.ll_line)
    LinearLayout llLine;
    @BindView(R.id.top_divider)
    View topDivider;
    @BindView(R.id.btn_send)
    TextView btnSend;
    @BindView(R.id.ed_comment_content)
    MyEditText edCommentContent;
    @BindView(R.id.ll_write)
    LinearLayout llWrite;

    @BindView(R.id.swipe_refresh)
    BounceScrollView bounceScrollView;
    private Share mShare;
    private OnlineAdapter onlineAdapter;
    private List<CommentInfo> mCommentInfoList = new ArrayList<>();

    private LinearLayoutManager mLayoutManager;
    private SharePresenter shareRequest;


    private boolean isReplySend;
    private int downNum;//锁定点击的评论
    //键盘监控
    private SoftKeyboardStateHelper softKeyboardStateHelper;
    private boolean isShow = false;//键盘是否弹出

    private DeleteCommentPopup deleteCommentPopup;

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_share_info);
    }

    @Override
    public void initViews() {
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        mToolbar.setTitle("详情");
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_send_whrite:
                        if (isLogin()) {
                            isReplySend = true;
                            edCommentContent.setHint("");
                            edCommentContent.setText("");
                            popupSoftkeyboard();
                        } else {
                            Intent lIntent = new Intent(ShareInfoActivity.this, LoginActivity.class);
                            startActivityForResult(lIntent, 1);
                        }
                        break;
                }
                return true;
            }
        });

        btnSend.setOnClickListener(this);
        mShare = Constants.getInstance().getmShare();
        if (mShare == null) {
            ToastUtil3.showToast(ShareInfoActivity.this, "数据异常，请重试");
            setResult(22);
            finish();
            return;
        }

        shareRequest = new SharePresenter(ShareInfoActivity.this);
        softKeyboardStateHelper = new SoftKeyboardStateHelper(findViewById(R.id.ll_activity_online));
        softKeyboardStateHelper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                //键盘打开
                isShow = true;
            }

            @Override
            public void onSoftKeyboardClosed() {
                llWrite.setVisibility(View.GONE);
                isShow = false;
            }
        });




        recylerComment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isShow){
                    //使键盘消失
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    llWrite.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });
        initDeleteWidget();
    }

    @Override
    public void initData() {
        tvTopic.setText("\t\t" + mShare.getTitle());
        tvContent.setText("\t\t" + mShare.getText());

        mCommentInfoList = mShare.getCommentList();
        if (ToolUtil.isListEmpty(mCommentInfoList)) {
            tvReminder.setVisibility(View.VISIBLE);
            recylerComment.setVisibility(View.GONE);
        }
            mLayoutManager = new LinearLayoutManager(this);
            recylerComment.setLayoutManager(mLayoutManager);
            recylerComment.setItemAnimator(new DefaultItemAnimator());
            onlineAdapter = new OnlineAdapter(ShareInfoActivity.this, mCommentInfoList);
            recylerComment.setAdapter(onlineAdapter);
            bounceScrollView.setScrollY(0);

        onlineAdapter.setOnItemClickListener(new OnlineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                downNum = position;
                if(mCommentInfoList.get(position).getAuthor().getObjectId().equals(cUser.getObjectId())){
                    deleteCommentPopup.showPopupWindow();
                }else {
                    isReplySend = false;
                    popupSoftkeyboard();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onSendClick(View view, int position) {

            }

            @Override
            public void onReplyClick(View view, int position) {

            }
        });
    }

    /**
     * 弹出软键盘
     */
    private void popupSoftkeyboard() {
        if(!isReplySend) {
            edCommentContent.setHint("回复：" +mCommentInfoList.get(downNum).getAuthor().getNick_name());
        }
        //获取焦点
        llWrite.setVisibility(View.VISIBLE);
        edCommentContent.setFocusable(true);
        edCommentContent.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(edCommentContent, InputMethodManager.SHOW_IMPLICIT);
    }
    /**
     * 删除
     */
    private void initDeleteWidget(){

        if (deleteCommentPopup == null) {
            deleteCommentPopup = new DeleteCommentPopup(this);
        }

        deleteCommentPopup.setOnDeleteCommentClickListener(new DeleteCommentPopup.OnDeleteCommentClickListener() {
            @Override
            public void onDelClick(final CommentInfo commentInfo) {
                shareRequest.deleteComment(0,mCommentInfoList.get(downNum).getObjectId(),mCommentInfoList);
                deleteCommentPopup.dismiss();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_send_whrite, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.btn_send:


                Students cMser = null;
                if(!isReplySend){
                    cMser = mCommentInfoList.get(downNum).getAuthor();
                }
                Logger.d("启动评论");
                shareRequest.addComment(0,mShare,cMser,edCommentContent.getText().toString(),mCommentInfoList);

                //使键盘消失
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                llWrite.setVisibility(View.GONE);
                isShow = false;
                edCommentContent.setText("");
                break;
        }
    }

    @Override
    public void onCommentChange(int itemPos, List<CommentInfo> commentInfoList) {

        tvReminder.setVisibility(View.GONE);
        recylerComment.setVisibility(View.VISIBLE);
        if(mCommentInfoList != null) {
            mCommentInfoList.clear();
        }else{
            mCommentInfoList = new ArrayList<>();
        }
        mCommentInfoList.addAll(commentInfoList);
        mShare.setCommentList(commentInfoList);
        Constants.getInstance().getmShare().setCommentList(commentInfoList);
        onlineAdapter.notifyDataSetChanged();

    }


    @Override
    public void onBackPressed() {
        if(!isShow) {
            if(llWrite.getVisibility() == View.GONE){
                setResult(Constants.REFRESH_CODE);
                finish();
            }else{
                //使键盘消失
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                llWrite.setVisibility(View.GONE);
                isShow = false;
            }

        }else{
            //使键盘消失
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            llWrite.setVisibility(View.GONE);
            isShow = false;
        }
    }

}
