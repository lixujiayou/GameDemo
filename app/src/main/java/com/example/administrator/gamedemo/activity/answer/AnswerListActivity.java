package com.example.administrator.gamedemo.activity.answer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.LoginActivity;
import com.example.administrator.gamedemo.activity.OnlineAnswerActivity;
import com.example.administrator.gamedemo.activity.SendAnswerActivity;
import com.example.administrator.gamedemo.activity.mine.UploadActivity;
import com.example.administrator.gamedemo.activity.mine.togther.SendTogtherActivity;
import com.example.administrator.gamedemo.adapter.AnswerAdapterU;
import com.example.administrator.gamedemo.adapter.AnswersAdapter;
import com.example.administrator.gamedemo.adapter.TogtherAdapter;
import com.example.administrator.gamedemo.adapter.UploadAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.core.MomentsType;
import com.example.administrator.gamedemo.model.AnswerHistory;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.model.bean.LikesInfo;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenterAnswer;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenterTogther;
import com.example.administrator.gamedemo.utils.view.IMomentViewTogther;
import com.example.administrator.gamedemo.utils.viewholder.AnswerViewHolder;
import com.example.administrator.gamedemo.utils.viewholder.EmptyMomentsVHTogther;
import com.example.administrator.gamedemo.utils.viewholder.MultiImageMomentsVHAnswer;
import com.example.administrator.gamedemo.utils.viewholder.MultiImageMomentsVHTogther;
import com.example.administrator.gamedemo.utils.viewholder.TextOnlyMomentsVHTogther;
import com.example.administrator.gamedemo.utils.viewholder.TopicBaseViewHolder;
import com.example.administrator.gamedemo.utils.viewholder.WebMomentsVHTogther;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;
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
public class AnswerListActivity extends BaseActivity implements IMomentViewTogther {

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
    //private UploadAdapter adapter;
    private UploadAdapter adapter;
    @BindView(R.id.rl_hint)
    RelativeLayout rl_hint;
    @BindView(R.id.tv_hint_1)
    TextView tv_hint_1;
    @BindView(R.id.tv_hint_2)
    TextView tv_hint_2;

    @BindView(R.id.ll_toobar)
    LinearLayout ll_toobar;

    @BindView(R.id.iv_load_state)
    ImageView ivLoadState;

    private boolean isReadCache = true;
    private boolean isLoad = false;

    private int cLoadNum;//当前加载数据条数

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_upload);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("一起答");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        setSupportActionBar(mToolbar);
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
                Constants.getInstance().setMomentsInfo(momentsInfoList.get(position));
                Intent gIntent = new Intent(AnswerListActivity.this, OnlineAnswerActivity.class);
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
                        if(cLoadNum >= Constants.FIRSTLOADNUM) { // 最少要有10条才能触发加载更多
                            if (!isLoad) {                                      //是否在加载中
                                adapter.setLoadStatus(true);
                                loadMore();
                            }
                        }else {
                            adapter.setLoadStatus(false);
                        }
                    }
                }
            }
        });


        isReadCache = true;
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);


        ivLoadState.setImageDrawable(ContextCompat.getDrawable(AnswerListActivity.this,R.mipmap.icon_loading));
        ivLoadState.setVisibility(View.VISIBLE);

        initData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isLoad) {
                    ivLoadState.setVisibility(View.GONE);
                    isReadCache = false;
                    initData();
                }else{
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });


        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_jiahao:
                        if(isLogin()){
                            Intent gIntent = new Intent(AnswerListActivity.this, SendAnswerActivity.class);
                            gIntent.putExtra(SendAnswerActivity.INTENT,SendAnswerActivity.SEND);
                            startActivityForResult(gIntent,1);
                        }else{
                            Intent gIntent = new Intent(AnswerListActivity.this, LoginActivity.class);
                            startActivityForResult(gIntent,1);
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
        isLoad = true;
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
        isLoad = true;
        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_LOADMORE);
        momentsRequest.execute();
    }
    //call back block
    //==============================================
    private SimpleResponseListener<List<MomentsInfo>> momentsRequestCallBack = new SimpleResponseListener<List<MomentsInfo>>() {
        @Override
        public void onSuccess(List<MomentsInfo> response, int requestType) {
            isLoad = false;
            swipeRefreshLayout.setRefreshing(false);
            ivLoadState.setVisibility(View.GONE);
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        cLoadNum = response.size();
                        circleRecyclerView.setVisibility(View.VISIBLE);
                        rl_hint.setVisibility(View.GONE);
                        adapter.updateData(response);
                    }else{
                        cLoadNum = 0;
                        circleRecyclerView.setVisibility(View.GONE);
                        rl_hint.setVisibility(View.VISIBLE);
                        tv_hint_1.setText(R.string.upload);
                        tv_hint_2.setText(R.string.upload_2);
                    }
                    break;
                case REQUEST_LOADMORE:
                    cLoadNum = response.size();
                    adapter.addMore(response);
                    break;
            }
        }

        @Override
        public void onError(BmobException e, int requestType) {
            super.onError(e, requestType);
            isLoad = false;
            swipeRefreshLayout.setRefreshing(false);
            Logger.d("错误" + e.toString());
            if(momentsInfoList == null || momentsInfoList.size() == 0) {
                ivLoadState.setImageDrawable(ContextCompat.getDrawable(AnswerListActivity.this, R.mipmap.icon_load_erro));
                ivLoadState.setVisibility(View.VISIBLE);
            }else{
                ToastUtil3.showToast(AnswerListActivity.this,"加载失败，请检查网络并重试");
            }
        }

        @Override
        public void onProgress(int pro) {
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 || resultCode == Constants.REFRESH_CODE ){
            initData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 為了讓 Toolbar 的 Menu 有作用，這邊的程式不可以拿掉
        getMenuInflater().inflate(R.menu.menu_togther, menu);
        return true;
    }

    @Override
    public void onLikeChange(int itemPos, List<LikesInfo> likeUserList) {

    }

    @Override
    public void onCommentChange(int itemPos, List<CommentInfo> commentInfoList) {

    }

    @Override
    public void showCommentBox(int itemPos, Togther momentid, CommentWidget commentWidget) {

    }

    @Override
    public void onMessageChange(String itemPos, String content) {

    }
}
