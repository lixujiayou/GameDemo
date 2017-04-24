package com.example.administrator.gamedemo.fragment.message;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.mine.message.AboutActivity;
import com.example.administrator.gamedemo.adapter.AboutMAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.AboutMessage;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseFragment;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenterTogther;
import com.example.administrator.gamedemo.widget.request.MessageRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.exception.BmobException;

/**
 * @auther lixu
 * Created by lixu on 2017/2/6 0006.
 * 关于我的消息
 */
public class AboutMFragment extends BaseFragment {
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

    private List<AboutMessage> messageList;
    private AboutMAdapter aboutMAdapter;
    private LinearLayoutManager mLayoutManager;
    private MessageRequest messageRequest;
    private boolean isLoad = false;
    private boolean isPrepared;
    private boolean isReadCache = true;
    private  boolean isFirst = true;//是否第一次加载数据
    private int cLoadSize = 0;//本次加载
    private MomentPresenterTogther momentPresenter;

    public AboutMFragment() {
    }

    public static AboutMFragment getInstance() {
        return answerFragmentHolder.instance;
    }

    public static class answerFragmentHolder {
        public static final AboutMFragment instance = new AboutMFragment();
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
        momentPresenter = new MomentPresenterTogther();
        messageRequest = new MessageRequest();
        messageList = new ArrayList<>();
        aboutMAdapter = new AboutMAdapter(mContext, messageList);
        mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
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
                        if (cLoadSize >= Constants.FIRSTLOADNUM) { // 最少要有10条才能触发加载更多
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
                if(!isLoad) {
                    ivLoadState.setVisibility(View.GONE);
                    isFirst = true;
                    initData();
                }else{
                    swipeRefresh.setRefreshing(false);
                }
            }
        });

    aboutMAdapter.setOnItemClickListener(new AboutMAdapter.OnItemClickListener() {
    @Override
    public void onItemClick(View view, int position) {
            Intent mIntent = new Intent(mContext, AboutActivity.class);
            mIntent.putExtra(AboutActivity.KEYCODE,messageList.get(position).getId());
            mIntent.putExtra(AboutActivity.TYPECODE,messageList.get(position).getType());
            startActivityForResult(mIntent,1);
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        new AlertDialog.Builder(mContext)
                .setTitle("删除提示")
                .setMessage("确定删除本条消息？")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        momentPresenter.deleteMessage(messageList.get(position).getObjectId());

                        messageList.remove(position);
                        aboutMAdapter.notifyItemRemoved(position);

                        dialogInterface.dismiss();

                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        dialogInterface.dismiss();
                    }
                })
                .create()
                .show();
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
    private SimpleResponseListener<List<AboutMessage>> momentsRequestCallBack = new SimpleResponseListener<List<AboutMessage>>() {
        @Override
        public void onSuccess(List<AboutMessage> response, int requestType) {
            isLoad = false;
            swipeRefresh.setRefreshing(false);
            aboutMAdapter.setLoadStatus(false);
            ivLoadState.setVisibility(View.GONE);
            switch (requestType) {
                case REQUEST_REFRESH:
                    cLoadSize = response.size();
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
                    cLoadSize = response.size();
                    aboutMAdapter.addMore(response);
                    break;
            }
        }

        @Override
        public void onError(BmobException e, int requestType) {
            super.onError(e, requestType);
            swipeRefresh.setRefreshing(false);

            Logger.d("错误" + e.toString());
            if(messageList == null || messageList.size() == 0){
                ivLoadState.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.icon_load_erro));
                ivLoadState.setVisibility(View.VISIBLE);
            }else{
                ToastUtil3.showToast(mContext, "获取消息失败,请检查网络并重试");
            }
        }

        @Override
        public void onProgress(int pro) {

        }
    };


}
