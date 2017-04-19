package com.example.administrator.gamedemo.fragment.message;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.mine.message.AboutActivity;
import com.example.administrator.gamedemo.adapter.AboutMAdapter;
import com.example.administrator.gamedemo.adapter.TextAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.AboutMessage;
import com.example.administrator.gamedemo.model.Message_;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseFragment;
import com.example.administrator.gamedemo.widget.request.MessageRequest;
import com.example.administrator.gamedemo.widget.request.MessageSysRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;

/**
 * @auther lixu
 * Created by lixu on 2017/2/16 0016.
 * 系统消息
 */
public class SysMFragment extends BaseFragment{
    private static final int REQUEST_REFRESH = 0x10;
    private static final int REQUEST_LOADMORE = 0x11;


    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.rl_hint)
    RelativeLayout rlHint;

    @BindView(R.id.iv_load_state)
    ImageView ivLoadState;


    private List<Message_> messageList;

    private TextAdapter aboutMAdapter;

    private LinearLayoutManager mLayoutManager;
    private MessageSysRequest messageRequest;
    private boolean isLoad = false;
    private boolean isPrepared;
    private boolean isReadCache = true;

    public static SysMFragment getInstance() {
        return answerFragmentHolder.instance;
    }
    private  boolean isFirst = true;//是否第一次加载数据
    public static class answerFragmentHolder {
        public static final SysMFragment instance = new SysMFragment();
    }

    @Override
    public void initTheme() {
    }

    @Override
    public int initCreatView() {
        return R.layout.fragment_message;
    }

    @Override
    public void initViews() {
        messageRequest = new MessageSysRequest();
        messageList = new ArrayList<>();
        aboutMAdapter = new TextAdapter(mContext, messageList);
        mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setBackgroundResource(R.color.colorAccent);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(aboutMAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == aboutMAdapter.getItemCount()) {
                    //如果正在下拉刷新中，删除上拉的布局 并屏蔽上拉
                    boolean isRefreshing = swipeRefresh.isRefreshing();
                    if (isRefreshing) {
                        aboutMAdapter.notifyItemRemoved(aboutMAdapter.getItemCount());
                        return;
                    } else {
                        //滑动到底部，开始加载更多
                        if (messageList.size() >= Constants.FIRSTLOADNUM) { // 最少要有10条才能触发加载更多
                            if (!isLoad) {                                      //是否在加载中
                                aboutMAdapter.setLoadStatus(true);
                                loadMore();
                            }
                        }
                    }
                }
            }
        });
        isReadCache = true;
        isFirst = true;
        isPrepared = true;
        swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        swipeRefresh.setRefreshing(true);
        ivLoadState.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.icon_loading));
        ivLoadState.setVisibility(View.VISIBLE);
        initData();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isFirst = true;
                initData();
                ivLoadState.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void initData() {

        if (!isPrepared || !isVisible || !isFirst) {
            if(swipeRefresh != null){
                if(swipeRefresh.isRefreshing()){
                    swipeRefresh.setRefreshing(false);
                }
            }

            return;
        } else {
            isFirst = false;
            isLoad = true;
            messageRequest.setOnResponseListener(momentsRequestCallBack);
            messageRequest.setRequestType(REQUEST_REFRESH);
            messageRequest.setCurPage(0);
            messageRequest.setCache(isReadCache);
            messageRequest.execute();
            isReadCache = false;
        }
    }
    /**
     * 加载更多
     */
    private void loadMore() {
        isLoad = true;
        messageRequest.setOnResponseListener(momentsRequestCallBack);
        messageRequest.setRequestType(REQUEST_LOADMORE);
        messageRequest.execute();
    }
    //call back block
    //==============================================
    private SimpleResponseListener<List<Message_>> momentsRequestCallBack = new SimpleResponseListener<List<Message_>>() {
        @Override
        public void onSuccess(List<Message_> response, int requestType) {
            isLoad = false;
            swipeRefresh.setRefreshing(false);
            aboutMAdapter.setLoadStatus(false);
            ivLoadState.setVisibility(View.GONE);
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        recyclerView.setVisibility(View.VISIBLE);
                        rlHint.setVisibility(View.GONE);
                        aboutMAdapter.updateData(response);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        rlHint.setVisibility(View.VISIBLE);
                    }
                    break;
                case REQUEST_LOADMORE:
                    aboutMAdapter.addMore(response);
                    break;
            }
        }

        @Override
        public void onError(BmobException e, int requestType) {
            super.onError(e, requestType);
            if(messageList == null || messageList.size() == 0) {
                ivLoadState.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_load_erro));
                ivLoadState.setVisibility(View.VISIBLE);
            }else{
                ToastUtil3.showToast(mContext, "获取消息失败,请检查网络并重试");
            }
            swipeRefresh.setRefreshing(false);

            Logger.d("错误" + e.toString());
        }

        @Override
        public void onProgress(int pro) {

        }
    };

}
