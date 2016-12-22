package com.example.administrator.gamedemo.activity.mine;

import android.os.Bundle;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.base.BaseActivity;

/**
 * Created by lixu on 2016/12/22 0022.
 * 个人中心
 */
public class MineCenterActivity extends BaseActivity{

    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mine_center);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("个人中心");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle_black);
    }

    @Override
    public void initData() {

    }
}
