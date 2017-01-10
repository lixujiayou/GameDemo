package com.example.administrator.gamedemo.utils.base;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.activity.LoginActivity;
import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.NetWorkEvent;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.NetWorkUtil;
import com.example.administrator.gamedemo.utils.ToastUtil3;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

/**
 * Created by lixu on 2016/11/29.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    private BroadcastReceiver netStateReceiver;
    public Students mUser ;
    @BindView(R.id.ll_network)
    public LinearLayout ll_netWork;
    @BindView(R.id.toolbar)
    public Toolbar mToolbar;
    public Students cUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView(savedInstanceState);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initNetWork();
        mUser = BmobUser.getCurrentUser(Students.class);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white));
        cUser = BmobUser.getCurrentUser(Students.class);
        initViews();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
    }
    // 初始化UI，setContentView
    protected abstract void initContentView(Bundle savedInstanceState);
    public abstract void initViews();
    public abstract void initData();

    @Override
    public void onClick(View view) {
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        overridePendingTransition(R.anim.anim_none, R.anim.trans_center_2_right);
        unregisterReceiver(netStateReceiver);
    }

    public boolean isLogin(){
        cUser = BmobUser.getCurrentUser(Students.class);
        if(cUser == null){
            return false;
        }else{
            return true;
        }
    }

    public void initNetWork(){
        netStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(
                        ConnectivityManager.CONNECTIVITY_ACTION)) {
                    if (NetWorkUtil.isNetWorkConnected(BaseActivity.this)) {
                        EventBus.getDefault().post(new NetWorkEvent(NetWorkEvent.AVAILABLE));
                    } else {
                        EventBus.getDefault().post(new NetWorkEvent(NetWorkEvent.UNAVAILABLE));
                    }
                }
            }
        };

        registerReceiver(netStateReceiver, new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Subscribe
    public void onEvent(NetWorkEvent event) {
        if (event.getType() == NetWorkEvent.UNAVAILABLE) {
            ll_netWork.setVisibility(View.VISIBLE);
        }else{
            ll_netWork.setVisibility(View.GONE);
        }
    }

}
