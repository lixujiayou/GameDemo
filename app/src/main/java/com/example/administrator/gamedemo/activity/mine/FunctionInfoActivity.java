package com.example.administrator.gamedemo.activity.mine;

import android.os.Bundle;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.base.BaseActivity;

/**
 * @auther lixu
 * Created by lixu on 2017/2/20 0020.
 * 功能介绍
 */
public class FunctionInfoActivity extends BaseActivity{
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_function_info);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("功能介绍");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);


    }

    @Override
    public void initData() {

    }
}
