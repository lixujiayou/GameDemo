package com.example.administrator.gamedemo.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.LoginActivity;
import com.example.administrator.gamedemo.activity.SendAnswerActivity;
import com.example.administrator.gamedemo.adapter.AnswersAdapter;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.core.MomentsType;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.NetWorkEvent;
import com.example.administrator.gamedemo.model.ReshEvent;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.base.BaseFragment;
import com.example.administrator.gamedemo.utils.bmob.bmob.BmobInitHelper;
import com.example.administrator.gamedemo.utils.viewholder.AnswerViewHolder;
import com.example.administrator.gamedemo.widget.ImageLoadMnanger;
import com.example.administrator.gamedemo.widget.pullrecyclerview.CircleRecyclerView;
import com.example.administrator.gamedemo.widget.pullrecyclerview.interfaces.onRefreshListener2;
import com.example.administrator.gamedemo.widget.request.MomentsRequest;
import com.example.administrator.gamedemo.widget.request.SimpleResponseListener;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/12/8 0008.
 */

public class AnswerFragment extends BaseFragment implements onRefreshListener2 {
    private static final int REQUEST_REFRESH = 0x10;
    private static final int REQUEST_LOADMORE = 0x11;

    @BindView(R.id.banner_imageView)
    ImageView bannerImageView;
    @BindView(R.id.title_tv)
    AppCompatTextView titleTv;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;


    @BindView(R.id.cl_fragment)
    CoordinatorLayout coordinatorLayout;
//    @BindView(R.id.recyclerView)
//    RecyclerView recyclerView;

    @BindView(R.id.circleRecyclerView)
    CircleRecyclerView circleRecyclerView;


    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipe_refresh;

    private MomentsRequest momentsRequest;
    private List<MomentsInfo> momentsInfoList;
    private HostViewHolder hostViewHolder;

    private AnswersAdapter adapter;

    public AnswerFragment() {
    }

    public static AnswerFragment getInstance() {
        return answerFragmentHolder.instance;
    }

    @Override
    public void onRefresh() {
        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_REFRESH);
        momentsRequest.setCurPage(0);
        momentsRequest.execute();
    }

    @Override
    public void onLoadMore() {
        momentsRequest.setOnResponseListener(momentsRequestCallBack);
        momentsRequest.setRequestType(REQUEST_LOADMORE);
        momentsRequest.execute();
    }



//    @OnClick({R.id.cv_temp, R.id.cv_set})
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.cv_temp:
//                login();
//                break;
//            case R.id.cv_set:
//
//                circleRecyclerView.getRecyclerView().smoothScrollToPosition(0);
////                BmobInitHelper bb = new BmobInitHelper();
////                bb.addShares();
//                break;
//        }
//    }


    public static class answerFragmentHolder {
        public static final AnswerFragment instance = new AnswerFragment();
    }

    @Override
    public void initTheme() {
        getActivity().setTheme(R.style.AppTheme_NoActionBar_Immerse);
    }

    @Override
    public int initCreatView() {
        return R.layout.fragment_answer;
    }

    @Override
    public void initViews() {

        EventBus.getDefault().register(this);
        login();
        swipe_refresh.setEnabled(false);
        momentsInfoList = new ArrayList<>();
        momentsRequest = new MomentsRequest();

        circleRecyclerView.setOnRefreshListener(this);
        hostViewHolder = new HostViewHolder(mContext);
        circleRecyclerView.addHeaderView(hostViewHolder.getView());

        toolbar.setTitleTextColor(ContextCompat.getColor(mContext, R.color.white));
       // toolbar.setTitle(R.string.main_answer);

        AnswersAdapter.Builder<MomentsInfo> builder = new AnswersAdapter.Builder<>(mContext);
        builder.addType(AnswerViewHolder.class, MomentsType.EMPTY_CONTENT, R.layout.item_problem)
                .addType(AnswerViewHolder.class, MomentsType.MULTI_IMAGES, R.layout.item_problem)
                .addType(AnswerViewHolder.class, MomentsType.TEXT_ONLY, R.layout.item_problem)
                .addType(AnswerViewHolder.class, MomentsType.WEB, R.layout.item_problem)
                .setData(momentsInfoList);

        adapter = builder.build();

        circleRecyclerView.setAdapter(adapter);
        circleRecyclerView.autoRefresh();

        hostViewHolder.friend_wall_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constants.getInstance().getUser(mContext) != null) {
                    Intent gIntent = new Intent(mContext, SendAnswerActivity.class);
                    startActivityForResult(gIntent,1);
                } else {
                    Intent lIntent = new Intent(mContext, LoginActivity.class);
                    startActivityForResult(lIntent,1);
                }
            }
        });
    }

    @Override
    public void initData() {

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //  toolbar.inflateMenu(R.menu.menu_main);  // 添加菜单

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            ToastUtil3.showToast(mContext, "搜索");
            return true;
        } else if (id == R.id.action_message) {
            ToastUtil3.showToast(mContext, "搜索");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //call back block
    //==============================================
    private SimpleResponseListener<List<MomentsInfo>> momentsRequestCallBack = new SimpleResponseListener<List<MomentsInfo>>() {
        @Override
        public void onSuccess(List<MomentsInfo> response, int requestType) {
            circleRecyclerView.compelete();
            Logger.d("requestType = " + requestType);
            switch (requestType) {
                case REQUEST_REFRESH:
                    if (!ToolUtil.isListEmpty(response)) {
                        adapter.updateData(response);
                        Logger.d("--" + response.size());
                    }
                    break;
                case REQUEST_LOADMORE:
                    Logger.d("loadmore compelete");
                    adapter.addMore(response);
                    break;
            }
        }

        @Override
        public void onError(BmobException e, int requestType) {
            super.onError(e, requestType);
            circleRecyclerView.compelete();
            Logger.d("错误" + e.toString());
        }

        @Override
        public void onProgress(int pro) {

        }
    };

    private static class HostViewHolder {
        private View rootView;
        private ImageView friend_wall_pic;
//        private ImageView message_avatar;
//        private TextView message_detail;

        public HostViewHolder(Context context) {
            this.rootView = LayoutInflater.from(context).inflate(R.layout.circle_host_header, null);
            this.friend_wall_pic = (ImageView) rootView.findViewById(R.id.friend_wall_pic);
//            this.message_avatar = (ImageView) rootView.findViewById(R.id.message_avatar);
//            this.message_detail = (TextView) rootView.findViewById(R.id.message_detail);
        }

        public void loadHostData(Students hostInfo) {
            if (hostInfo == null) return;
            ImageLoadMnanger.INSTANCE.loadNomalImage(AnswerFragment.getInstance(),friend_wall_pic, "http://qn.ciyo.cn/upload/FgbnwPphrRD46RsX_gCJ8PxMZLNF");

        }

        public View getView() {
            return rootView;
        }

    }

    private void login() {
        Logger.d("登录中");
        Students bmobUser = new Students();
        bmobUser.setUsername("qwerrr");
        bmobUser.setPassword("123");
        bmobUser.login(new SaveListener<Object>() {

            @Override
            public void done(Object o, BmobException e) {
                if (e == null) {
                    Logger.d("登录成功");
                    ToastUtil3.showToast(mContext, "登录成功");
                } else {
                    Logger.d("登录失败" + e.toString());
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Constants.REFRESH_CODE){
            circleRecyclerView.autoRefresh();
        }
    }

    @Subscribe
    public void onEvent(ReshEvent reshEvent) {
        circleRecyclerView.autoRefresh();
    }
}
