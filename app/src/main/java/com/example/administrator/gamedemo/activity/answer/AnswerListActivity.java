package com.example.administrator.gamedemo.activity.answer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.OnlineAnswerActivity;
import com.example.administrator.gamedemo.activity.SendAnswerActivity;
import com.example.administrator.gamedemo.activity.mine.UploadActivity;
import com.example.administrator.gamedemo.activity.mine.togther.SendTogtherActivity;
import com.example.administrator.gamedemo.adapter.UploadAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.widget.request.MomentsRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;

/**
 * @auther lixu
 * Created by lixu on 2017/1/4 0004.
 */
public class AnswerListActivity extends BaseActivity{

    @BindView(R.id.recycler)
    RecyclerView circleRecyclerView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private static final int REQUEST_REFRESH = 0x10;
    private static final int REQUEST_LOADMORE = 0x11;
    private LinearLayoutManager mLayoutManager;
    //request
    private MomentsRequest momentsRequest;
    private List<MomentsInfo> momentsInfoList;
    // private List<Share> responseTemp;
    private UploadAdapter adapter;
    @BindView(R.id.rl_hint)
    RelativeLayout rl_hint;
    @BindView(R.id.tv_hint_1)
    TextView tv_hint_1;
    @BindView(R.id.tv_hint_2)
    TextView tv_hint_2;

    @BindView(R.id.ll_toobar)
    LinearLayout ll_toobar;
    private boolean isReadCache = true;
    private boolean isLoad = false;
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_upload);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("一起答");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_jiahao:
                        Intent gIntent = new Intent(AnswerListActivity.this,SendAnswerActivity.class);
                        gIntent.putExtra(SendAnswerActivity.INTENT,SendAnswerActivity.CHANGE);
                        startActivityForResult(gIntent,1);
                        break;
                }
                return true;
            }
        });
        momentsInfoList = new ArrayList<>();
        momentsRequest = new MomentsRequest();
        mLayoutManager = new LinearLayoutManager(AnswerListActivity.this);
        circleRecyclerView.setLayoutManager(mLayoutManager);
        circleRecyclerView.setHasFixedSize(true);
        circleRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new UploadAdapter(AnswerListActivity.this,momentsInfoList);
        circleRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new UploadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("topic",momentsInfoList.get(position));
                Intent gIntent = new Intent(AnswerListActivity.this, OnlineAnswerActivity.class);

                gIntent.putExtras(bundle);
                startActivityForResult(gIntent,1);
            }

            @Override
            public void onItemLongClick(View view, int position) {
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
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    //如果正在下拉刷新中，删除上拉的布局 并屏蔽上拉
                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }else{
                        //滑动到底部，开始加载更多
                        if(momentsInfoList.size() >= Constants.FIRSTLOADNUM) { // 最少要有10条才能触发加载更多
                            if (!isLoad) {                                      //是否在加载中
                                adapter.setLoadStatus(true);
                                loadMore();
                            }
                        }
                    }
                }
            }
        });


        isReadCache = true;
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        initData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });


        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_jiahao:
                        if(isLogin()){
                            Intent wIntent = new Intent(AnswerListActivity.this, SendAnswerActivity.class);
                            startActivityForResult(wIntent,1);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    @Override
    public void initData() {
        swipeRefreshLayout.setRefreshing(true);
        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_REFRESH);
        momentsRequest.setCurPage(0);
        momentsRequest.setCache(isReadCache);
        momentsRequest.execute();
        isReadCache = false;
    }

    /**
     * 加载更多
     */
    private void loadMore(){
        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_LOADMORE);
        momentsRequest.execute();
    }
    //call back block
    //==============================================
    private SimpleResponseListener<List<MomentsInfo>> momentsRequestCallBack = new SimpleResponseListener<List<MomentsInfo>>() {
        @Override
        public void onSuccess(List<MomentsInfo> response, int requestType) {
            swipeRefreshLayout.setRefreshing(false);
            adapter.setLoadStatus(false);
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        circleRecyclerView.setVisibility(View.VISIBLE);
                        rl_hint.setVisibility(View.GONE);
                        adapter.updateData(response);
                    }else{
                        circleRecyclerView.setVisibility(View.GONE);
                        rl_hint.setVisibility(View.VISIBLE);
                            tv_hint_1.setText(R.string.upload);
                            tv_hint_2.setText(R.string.upload_2);
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
            swipeRefreshLayout.setRefreshing(false);
            Logger.d("错误" + e.toString());
        }

        @Override
        public void onProgress(int pro) {
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 || resultCode == Constants.REFRESH_CODE ){
            isReadCache = false;
            initData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_togther, menu);
        return true;
    }
}
