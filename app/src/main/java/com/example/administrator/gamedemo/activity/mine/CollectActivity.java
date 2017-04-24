package com.example.administrator.gamedemo.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.share.ShareInfoActivity;
import com.example.administrator.gamedemo.adapter.MyShareAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.widget.request.ShareRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;

/**
 * @auther lixu
 * Created by lixu on 2017/1/2 0002.
 */
public class CollectActivity extends BaseActivity{

    @BindView(R.id.recycler)
    RecyclerView circleRecyclerView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;



    @BindView(R.id.rl_hint)
    RelativeLayout rl_hint;

    @BindView(R.id.iv_load_state)
    ImageView ivLoadState;

    private MyShareAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;

    private List<Share> momentsInfoList;
    private ShareRequest momentsRequest;
    private boolean isReadCache = false;

    private static final int REQUEST_REFRESH = 0x10;
    private static final int REQUEST_LOADMORE = 0x11;
    private boolean isOne = true;
    private boolean isLoad;//是否正在加载
    private int mCLoadCount = 0;//本次加载条数


    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mine_collect);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("我的收藏");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        momentsInfoList = new ArrayList<>();
        momentsRequest = new ShareRequest();
        mToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circleRecyclerView.smoothScrollToPosition(0);
            }
        });
        ivLoadState.setImageDrawable(ContextCompat.getDrawable(CollectActivity.this,R.mipmap.icon_loading));
        ivLoadState.setVisibility(View.VISIBLE);

        mAdapter = new MyShareAdapter(CollectActivity.this,momentsInfoList);
        linearLayoutManager = new LinearLayoutManager(CollectActivity.this);
        circleRecyclerView.setLayoutManager(linearLayoutManager);
        circleRecyclerView.setItemAnimator(new DefaultItemAnimator());
        circleRecyclerView.setHasFixedSize(true);
        circleRecyclerView.setAdapter(mAdapter);
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


        circleRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
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
                               getLoadMore();
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
                Intent gIntent = new Intent(CollectActivity.this, ShareInfoActivity.class);
                startActivityForResult(gIntent,1);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
    }

    @Override
    public void initData() {
        getFirstData();
    }



    private void getFirstData(){
        swipeRefreshLayout.setRefreshing(true);
        isLoad = true;
        if(!isOne) {
            ivLoadState.setVisibility(View.GONE);
        }

        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_REFRESH);
        momentsRequest.setCurPage(0);
        momentsRequest.setCache(false);
        momentsRequest.setIsCollect(true);
        momentsRequest.execute();
        isReadCache = false;
    }

    private void getLoadMore(){
        isLoad = false;
        if(!isOne) {
            ivLoadState.setVisibility(View.GONE);
        }
        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_LOADMORE);
        momentsRequest.execute();
    }



    //call back block
    //==============================================
    private SimpleResponseListener<List<Share>> momentsRequestCallBack = new SimpleResponseListener<List<Share>>() {
        @Override
        public void onSuccess(List<Share> response, int requestType) {
            swipeRefreshLayout.setRefreshing(false);
            ivLoadState.setVisibility(View.GONE);
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        mCLoadCount = response.size();
                        rl_hint.setVisibility(View.GONE);
                        circleRecyclerView.setVisibility(View.VISIBLE);

//                        momentsInfoList.clear();
//                        momentsInfoList.addAll(response);

                        mAdapter.updateData(response);
                    }else{
                        mCLoadCount = 0;
                        rl_hint.setVisibility(View.VISIBLE);
                    }
                    break;
                case REQUEST_LOADMORE:
                    mCLoadCount = response.size();
   /*                 momentsInfoList.clear();
                    momentsInfoList.addAll(response);*/
                    mAdapter.addMore(response);
                    break;
            }
        }

        @Override
        public void onError(BmobException e, int requestType) {
            super.onError(e, requestType);
           swipeRefreshLayout.setRefreshing(false);
            isOne = false;
            if(momentsInfoList == null || momentsInfoList.size() == 0) {
                ivLoadState.setVisibility(View.VISIBLE);
                ivLoadState.setImageDrawable(ContextCompat.getDrawable(CollectActivity.this, R.mipmap.icon_load_erro));
            }else{
                ToastUtil3.showToast(CollectActivity.this,"查询失败，请检查网络并重试。");
            }
            }

        @Override
        public void onProgress(int pro) {

        }
    };







    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 3){
            isReadCache = false;

            getFirstData();
        }
    }
}
