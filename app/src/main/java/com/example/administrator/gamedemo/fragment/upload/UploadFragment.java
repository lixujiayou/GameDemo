package com.example.administrator.gamedemo.fragment.upload;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.LoginActivity;
import com.example.administrator.gamedemo.activity.OnlineAnswerActivity;
import com.example.administrator.gamedemo.activity.SendAnswerActivity;
import com.example.administrator.gamedemo.activity.share.SendShareActivity;
import com.example.administrator.gamedemo.adapter.AnswersAdapter;
import com.example.administrator.gamedemo.adapter.CircleMomentsAdapter;
import com.example.administrator.gamedemo.adapter.UploadAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.core.MomentsType;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.KeyboardControlMnanager;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseFragment;
import com.example.administrator.gamedemo.utils.presenter.MomentPresenter;
import com.example.administrator.gamedemo.utils.view.IMomentView;
import com.example.administrator.gamedemo.utils.viewholder.AnswerViewHolder;
import com.example.administrator.gamedemo.utils.viewholder.EmptyMomentsVH;
import com.example.administrator.gamedemo.utils.viewholder.MultiImageMomentsVH;
import com.example.administrator.gamedemo.utils.viewholder.TextOnlyMomentsVH;
import com.example.administrator.gamedemo.utils.viewholder.WebMomentsVH;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.example.administrator.gamedemo.widget.commentwidget.CommentBox;
import com.example.administrator.gamedemo.widget.commentwidget.CommentWidget;
import com.example.administrator.gamedemo.widget.pullrecyclerview.CircleRecyclerView;
import com.example.administrator.gamedemo.widget.pullrecyclerview.interfaces.onRefreshListener2;
import com.example.administrator.gamedemo.widget.request.MomentsRequest;
import com.example.administrator.gamedemo.widget.request.ShareRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Administrator on 2016/12/8 0008.
 * @author lixu
 * 上传答题
 */

public abstract class UploadFragment extends BaseFragment{

    @BindView(R.id.recycler)
    RecyclerView circleRecyclerView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private boolean isPrepared;

    private static final int REQUEST_REFRESH = 0x10;
    private static final int REQUEST_LOADMORE = 0x11;
    private LinearLayoutManager mLayoutManager;
    //request
    private MomentsRequest momentsRequest;
    private List<MomentsInfo> momentsInfoList;
    // private List<Share> responseTemp;
    private boolean isReadCache = true;
    private UploadAdapter adapter;
    @BindView(R.id.rl_hint)
    RelativeLayout rl_hint;
    @BindView(R.id.tv_hint_1)
    TextView tv_hint_1;
    @BindView(R.id.tv_hint_2)
    TextView tv_hint_2;

    @BindView(R.id.iv_load_state)
    ImageView ivLoadState;

    @BindView(R.id.ll_toobar)
    LinearLayout ll_toobar;

    public String mType = Constants.UPLOAD_OK;
    private boolean isLoad = false;
    protected abstract void setmType();

    @Override
    public void initTheme() {
        getActivity().setTheme(R.style.AppBaseTheme);
    }


    @Override
    public int initCreatView() {
        return R.layout.fragment_upload;
    }

    @Override
    public void initViews() {
        ll_toobar.setVisibility(View.GONE);
        momentsInfoList = new ArrayList<>();
        momentsRequest = new MomentsRequest();

        mLayoutManager = new LinearLayoutManager(mContext);
        circleRecyclerView.setLayoutManager(mLayoutManager);
        circleRecyclerView.setHasFixedSize(true);
        circleRecyclerView.setItemAnimator(new DefaultItemAnimator());

        adapter = new UploadAdapter(mContext,momentsInfoList);
        circleRecyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new UploadAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if(mType.equals(Constants.UPLOAD_OK)){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("topic",momentsInfoList.get(position));
                    Intent gIntent = new Intent(mContext, OnlineAnswerActivity.class);
                    gIntent.putExtras(bundle);
                    startActivityForResult(gIntent,1);
                }else if(mType.equals(Constants.UPLOAD_ING)){
                    ToastUtil3.showToast(mContext,"审核中");
                }else if(mType.equals(Constants.UPLOAD_NO)){
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SendAnswerActivity.TOPIC,momentsInfoList.get(position));
                    Intent nIntent = new Intent(mContext, SendAnswerActivity.class);
                    nIntent.putExtra(SendAnswerActivity.INTENT,SendAnswerActivity.CHANGE);
                    nIntent.putExtras(bundle);
                    startActivityForResult(nIntent,1);
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {
                showDiaLog(position);
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


        ivLoadState.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.icon_loading));
        ivLoadState.setVisibility(View.VISIBLE);

        isReadCache = true;
        isFirst = true;
        isPrepared = true;
        setmType();
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setRefreshing(true);
        initData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ivLoadState.setVisibility(View.GONE);
                isFirst = true;
                initData();
            }
        });
    }

    @Override
    public void initData() {
        if (!isPrepared || !isVisible || !isFirst) {
            if(swipeRefreshLayout != null){
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            return;
        } else {
            isFirst = false;
            isLoad = true;
            momentsRequest.setOnResponseListener(momentsRequestCallBack);
            momentsRequest.setRequestType(REQUEST_REFRESH);
            momentsRequest.setCurPage(0);
            momentsRequest.setmType(mType);
            momentsRequest.setCache(isReadCache);
            momentsRequest.execute();
            isReadCache = false;
        }
    }
    private void showDiaLog(final int position) {
        try {
            new AlertDialog.Builder(mContext)
                    .setTitle("提示")
                    .setMessage("是否删除本条数据？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            delete(position);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        } catch (Exception e) {
            ToastUtil3.showToast(mContext, "程序异常,请稍后重试");
        }
    }

    public void delete(final int position){
        MomentsInfo momentsInfo  = momentsInfoList.get(position);
        swipeRefreshLayout.setRefreshing(true);
        momentsInfo.delete(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                swipeRefreshLayout.setRefreshing(false);
                if(e == null){
                    circleRecyclerView.setVisibility(View.VISIBLE);
                    rl_hint.setVisibility(View.GONE);
                    momentsInfoList.remove(position);
                    adapter.updateData(momentsInfoList);
                    if(momentsInfoList.size() == 0){

                            circleRecyclerView.setVisibility(View.GONE);
                            rl_hint.setVisibility(View.VISIBLE);
                            if(mType.equals(Constants.UPLOAD_OK)){
                                tv_hint_1.setText(R.string.upload_ok_1);
                                tv_hint_2.setText(R.string.upload_ok_2);
                            }else if(mType.equals(Constants.UPLOAD_ING)){
                                tv_hint_1.setText(R.string.upload_ing_1);
                                tv_hint_2.setText(R.string.upload_ing_2);
                            }else if(mType.equals(Constants.UPLOAD_NO)){
                                tv_hint_1.setText(R.string.upload_no_1);
                                tv_hint_2.setText(R.string.upload_no_2);
                            }

                    }
                }else{
                    ToastUtil3.showToast(mContext,"删除失败，请检查网络并重试");
                    Logger.d("删除失败"+e);
                }

            }
        });
    }

    /**
     * 加载更多
     */
    private void loadMore(){
        isLoad = true;
        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_LOADMORE);
        momentsRequest.setmType(mType);
        momentsRequest.execute();
    }
    //call back block
    //==============================================
    private SimpleResponseListener<List<MomentsInfo>> momentsRequestCallBack = new SimpleResponseListener<List<MomentsInfo>>() {
        @Override
        public void onSuccess(List<MomentsInfo> response, int requestType) {
            isLoad = false;
            swipeRefreshLayout.setRefreshing(false);
            adapter.setLoadStatus(false);
            ivLoadState.setVisibility(View.GONE);
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        circleRecyclerView.setVisibility(View.VISIBLE);
                        rl_hint.setVisibility(View.GONE);
                        adapter.updateData(response);
                    }else{
                        circleRecyclerView.setVisibility(View.GONE);
                        rl_hint.setVisibility(View.VISIBLE);
                        if(mType.equals(Constants.UPLOAD_OK)){
                            tv_hint_1.setText(R.string.upload_ok_1);
                            tv_hint_2.setText(R.string.upload_ok_2);
                        }else if(mType.equals(Constants.UPLOAD_ING)){
                            tv_hint_1.setText(R.string.upload_ing_1);
                            tv_hint_2.setText(R.string.upload_ing_2);
                        }else if(mType.equals(Constants.UPLOAD_NO)){
                            tv_hint_1.setText(R.string.upload_no_1);
                            tv_hint_2.setText(R.string.upload_no_2);
                        }
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
            if(momentsInfoList ==null || momentsInfoList.size() == 0){
                ivLoadState.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.icon_loading));
                ivLoadState.setVisibility(View.VISIBLE);
            }else{
                ToastUtil3.showToast(mContext,"加载失败，请检查网络并重试。");
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
            isFirst = true;
            initData();
        }
    }
}
