package com.example.administrator.gamedemo.activity;

import android.os.Bundle;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.base.BaseActivity;

/**
 * Created by Administrator on 2016/12/15 0015.
 */
public class LoginActivity extends BaseActivity{
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("登录");
    }

    @Override
    public void initData() {

    }
}
