package com.example.administrator.gamedemo.fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.LoginActivity;
import com.example.administrator.gamedemo.activity.mine.AboutWeActivity;
import com.example.administrator.gamedemo.activity.mine.CollectActivity;
import com.example.administrator.gamedemo.activity.mine.FunctionInfoActivity;
import com.example.administrator.gamedemo.activity.mine.IdeaActivity;
import com.example.administrator.gamedemo.activity.mine.InviteFriend;
import com.example.administrator.gamedemo.activity.mine.MessageActivity;
import com.example.administrator.gamedemo.activity.mine.MineCenterActivity;
import com.example.administrator.gamedemo.activity.mine.UploadActivity;
import com.example.administrator.gamedemo.activity.mine.togther.SendTogtherActivity;
import com.example.administrator.gamedemo.activity.mine.togther.TogetherActivity;
import com.example.administrator.gamedemo.activity.notes.NotesListActivity;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.UIHelper;
import com.example.administrator.gamedemo.utils.base.BaseFragment;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2016/12/8 0008.
 */

public class MineFragment extends BaseFragment {

    @BindView(R.id.rl_mine)
    RelativeLayout relativeLayout;

    @BindView(R.id.rv_icon)
    ImageView iv_icon;

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.iv_dim)
    ImageView iv_dim;

    @BindView(R.id.iv_message)
    ImageView iv_message;
    @BindView(R.id.ll_mine_center)
    LinearLayout llMineCenter;
    @BindView(R.id.ll_together)
    LinearLayout llTogether;
    @BindView(R.id.ll_collect)
    LinearLayout llCollect;
    @BindView(R.id.ll_upload)
    LinearLayout llUpload;
    @BindView(R.id.ll_mine)
    LinearLayout llMine;

    @BindView(R.id.ll_mine_about)
    LinearLayout llMineAbout;

    @BindView(R.id.ll_mine_suggest)
    LinearLayout llMineSuggest;

    @BindView(R.id.ll_functioninfo)
    LinearLayout llFunctionInfo;

    @BindView(R.id.ll_invite)
    LinearLayout llInvite;

    @BindView(R.id.ll_note)
    LinearLayout ll_note;

    private boolean isPrepared;

    public MineFragment() {
    }

    public static MineFragment getInstance() {
        return answerFragmentHolder.instance;
    }

    @OnClick({R.id.ll_mine_center
            ,R.id.ll_mine
            , R.id.ll_together
            , R.id.ll_collect
            , R.id.ll_upload
            ,R.id.ll_note
            ,R.id.ll_invite
            ,R.id.iv_message
            ,R.id.ll_mine_about
            ,R.id.ll_mine_suggest
            ,R.id.ll_functioninfo})
    public void onClick(View view) {
        Intent gIntent = new Intent();
        switch (view.getId()) {
            case R.id.ll_mine:
            case R.id.ll_mine_center:
                if(BmobUser.getCurrentUser() == null){
                    gIntent.setClass(mContext, LoginActivity.class);
                }else {
                    gIntent.setClass(mContext, MineCenterActivity.class);
                }
                break;
            case R.id.ll_together:
                gIntent.setClass(mContext, TogetherActivity.class);
                break;
            case R.id.ll_collect:
                gIntent.setClass(mContext, CollectActivity.class);
                break;
            case R.id.ll_upload:
                gIntent.setClass(mContext, UploadActivity.class);
                break;
            case R.id.ll_note:
                gIntent.setClass(mContext, NotesListActivity.class);
                break;
            case R.id.ll_invite:
                gIntent.setClass(mContext, InviteFriend.class);
                break;
            case R.id.iv_message:
                gIntent.setClass(mContext, MessageActivity.class);
                break;
            case R.id.ll_mine_about:
                gIntent.setClass(mContext, AboutWeActivity.class);
                break;
            //反馈
            case R.id.ll_mine_suggest:
                gIntent.setClass(mContext, IdeaActivity.class);
                break;
            //功能介绍
            case R.id.ll_functioninfo:
                gIntent.setClass(mContext, FunctionInfoActivity.class);
                break;
            default:
                return;
        }
        startActivityForResult(gIntent,1);
    }

    public static class answerFragmentHolder {
        public static final MineFragment instance = new MineFragment();
    }

    @Override
    public void initTheme() {
        //getActivity().setTheme(R.style.AppBaseTheme);
    }

    @Override
    public int initCreatView() {
        return R.layout.fragment_mine;
    }

    @Override
    public void initViews() {
        iv_dim.setPadding(0, Constants.getInstance().getStatusBarHeight(mContext), 0, 0);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) iv_message.getLayoutParams();
        lp.setMargins(0, Constants.getInstance().getStatusBarHeight(mContext), 0, 0);
        isPrepared = true;
    }

    @Override
    public void onStart() {
        super.onStart();
        initUserInfo();
    }

    @Override
    public void initData() {
        if (!isPrepared || !isVisible || !isFirst) {
            return;
        } else {
            Logger.d("切换" + isPrepared + "--" + isVisible + "--" + isFirst);
            //initUserInfo();
            isFirst = false;
        }
    }

    /**
     * 初始化用户头像，昵称
     */
    private void initUserInfo() {
        Students students = Constants.getInstance().getUser();
        if (students != null) {
            if (students.getUser_icon() != null) {
                ImageLoadMnanger.INSTANCE.loadCicleImage(this, iv_icon, students.getUser_icon().getFileUrl());
                ImageLoadMnanger.INSTANCE.steDimImage(this, students.getUser_icon().getFileUrl(), iv_dim);
            } else {
                iv_icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_user_default));
                iv_dim.setImageBitmap(Constants.doBlur(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_user_default), 10, false));
            }
            if (students.getNick_name() != null) {
                tv_name.setText(students.getNick_name());
            } else {
                tv_name.setText("前去编辑我的昵称");
            }
        }else{
            iv_icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_user_default));
            iv_dim.setImageBitmap(Constants.doBlur(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_user_default), 10, false));
            tv_name.setText("点击登录");
        }
    }
}
