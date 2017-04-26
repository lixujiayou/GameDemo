package com.example.administrator.gamedemo.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.mine.togther.SendTogtherActivity;
import com.example.administrator.gamedemo.activity.share.SendShareActivity;
import com.example.administrator.gamedemo.activity.share.ShareInfoActivity;
import com.example.administrator.gamedemo.adapter.AboutMAdapter;
import com.example.administrator.gamedemo.adapter.MyShareAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.core.MomentsType;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.utils.KeyboardControlMnanager;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseFragment;
import com.example.administrator.gamedemo.utils.view.IMomentView;

import com.example.administrator.gamedemo.utils.view.IMomentViewShare;
import com.example.administrator.gamedemo.utils.view.IMomentViewTogther;
import com.example.administrator.gamedemo.utils.view.IShareView;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.example.administrator.gamedemo.widget.commentwidget.CommentBox;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;
import com.example.administrator.gamedemo.widget.pullrecyclerview.CircleRecyclerView;
import com.example.administrator.gamedemo.widget.pullrecyclerview.interfaces.onRefreshListener2;
import com.example.administrator.gamedemo.widget.request.ShareRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import butterknife.OnClick;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by Administrator on 2016/12/8 0008.
 *
 * @author lixu
 */

public class ShareFragment extends BaseFragment  {

    @BindView(R.id.tv_repair)
    TextView tv_repair;
    @BindView(R.id.rl_bar)
    RelativeLayout rlBar;
    @BindView(R.id.recycler)
    RecyclerView circleRecyclerView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.widget_comment)
    CommentBox commentBox;

    @BindView(R.id.iv_add)
     ImageView iv_add;

    @BindView(R.id.iv_load_state)
    ImageView ivLoadState;

    private boolean isPrepared;

    private static final int REQUEST_REFRESH = 0x10;
    private static final int REQUEST_LOADMORE = 0x11;
    private int keyHeight;
    private List<Share> momentsInfoList;
    //request
    private ShareRequest momentsRequest;
    private MyShareAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private boolean isReadCache = true;
    private boolean isOne = true;
    private  boolean isFirst = true;//是否第一次加载数据
    private boolean isLoad ;//是否正在加载数据
    private int mCLoadCount = 0;//本次加载了多少条

    public ShareFragment() {
    }

    public static ShareFragment getInstance() {

        return answerFragmentHolder.instance;
    }

    @OnClick({R.id.rl_bar,R.id.iv_add})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_bar:
                circleRecyclerView.smoothScrollToPosition(0);
                break;
            case R.id.iv_add:
                Intent iIntent = new Intent(mContext, SendShareActivity.class);
                startActivityForResult(iIntent,1);
                break;
        }
    }



    public static class answerFragmentHolder {
        public static final ShareFragment instance = new ShareFragment();
    }

    @Override
    public void initTheme() {
        getActivity().setTheme(R.style.AppBaseTheme);
    }


    @Override
    public int initCreatView() {
        return R.layout.fragment_share;
    }

    @Override
    public void initViews() {
        ViewGroup.LayoutParams lp = tv_repair.getLayoutParams();
        lp.height = Constants.getInstance().getStatusBarHeight(mContext);

        momentsInfoList = new ArrayList<>();
        momentsRequest = new ShareRequest();

        mAdapter = new MyShareAdapter(mContext,momentsInfoList);
        mLayoutManager = new LinearLayoutManager(mContext);
        circleRecyclerView.setLayoutManager(mLayoutManager);
        circleRecyclerView.setHasFixedSize(true);
        circleRecyclerView.setItemAnimator(new DefaultItemAnimator());
        circleRecyclerView.setAdapter(mAdapter);

        isFirst = true;
        isPrepared = true;
        ivLoadState.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.icon_loading));
        ivLoadState.setVisibility(View.VISIBLE);
        initData();
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isLoad) {
                    getFirstData();
                }else{
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        /*circleRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (commentBox != null && commentBox.isShowing()) {
                    commentBox.dismissCommentBox(false);
                    return true;
                }
                return false;
            }
        });*/

        circleRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == mAdapter.getItemCount()) {
                    //如果正在下拉刷新中，删除上拉的布局 并屏蔽上拉
                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();


                        if (isRefreshing) {
                            mAdapter.notifyItemRemoved(mAdapter.getItemCount());
                            return;
                        } else {
                            //滑动到底部，开始加载更多
                            if (mCLoadCount >= Constants.FIRSTLOADNUM) { // 最少要有10条才能触发加载更多
                                if (!isLoad) {                                      //是否在加载中
                                    mAdapter.setLoadStatus(true);
                                    getMoreData();
                                }
                            } else {
                                mAdapter.setLoadStatus(false);
                            }
                        }




                }
            }
        });

        mAdapter.setOnItemClickListener(new MyShareAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Constants.getInstance().setMShare(momentsInfoList.get(position));
                Intent gIntent = new Intent(mContext, ShareInfoActivity.class);
                startActivityForResult(gIntent,1);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

    }

    @Override
    public void initData() {
        Logger.d("isPrepared="+isPrepared+"isVisible="+isVisible+"isFirst="+isFirst);

        if (!isPrepared || !isFirst) {
            return;
        } else {
            swipeRefreshLayout.setRefreshing(true);
            isFirst = false;
            getFirstData();
        }

        Students cUserTemp = Constants.getInstance().getUser();
        if(cUserTemp!= null){
            if(cUserTemp.getIsManage() == Constants.CONTROLNUM){
                iv_add.setVisibility(View.VISIBLE);
            }else{
                iv_add.setVisibility(View.GONE);
            }
        }else{
            iv_add.setVisibility(View.GONE);
        }
    }


    private void getFirstData(){
        if(!isOne) {
            ivLoadState.setVisibility(View.GONE);
        }
        isLoad = true;
        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_REFRESH);
        momentsRequest.setCurPage(0);
        momentsRequest.setCache(isReadCache);
        momentsRequest.execute();
        isReadCache = false;
    }
    private void getMoreData(){
        if(!isOne) {
            ivLoadState.setVisibility(View.GONE);
        }
        isLoad = true;
        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_LOADMORE);
        momentsRequest.execute();
    }

    //call back block
    //==============================================
    private SimpleResponseListener<List<Share>> momentsRequestCallBack = new SimpleResponseListener<List<Share>>() {
        @Override
        public void onSuccess(List<Share> response, int requestType) {
            isLoad = false;
            swipeRefreshLayout.setRefreshing(false);
            ivLoadState.setVisibility(View.GONE);
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        mCLoadCount = response.size();
                        momentsInfoList.clear();
                        momentsInfoList.addAll(response);
                      /*mAdapter.notifyDataSetChanged();*/
                        mAdapter.updateData(response);
                    }else{
                        mCLoadCount = 0;
                    }
                    break;
                case REQUEST_LOADMORE:
                    Logger.d("加载更多====="+response.size());
                  //  momentsInfoList.addAll(response);
                    mCLoadCount = response.size();
                    mAdapter.addMore(response);
                    break;
            }
        }

        @Override
        public void onError(BmobException e, int requestType) {
            super.onError(e, requestType);
            Logger.d("查询SHAREf返回异常"+e.toString());
            swipeRefreshLayout.setRefreshing(false);
            isOne = false;
                if (momentsInfoList == null || momentsInfoList.size() == 0) {
                    ivLoadState.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_load_erro));
                    ivLoadState.setVisibility(View.VISIBLE);
                }
        }

        @Override
        public void onProgress(int pro) {
        }
    };
    //=============================================================View's method



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 3){
            isReadCache = false;
            getFirstData();
        }
    }




}
