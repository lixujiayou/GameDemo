package com.example.administrator.gamedemo.activity.mine;

import android.os.Bundle;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.base.BaseActivity;

/**
 * @auther lixu
 * Created by lixu on 2017/2/20 0020.
 */
public class AboutWeActivity extends BaseActivity{
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_about_we);
    }

    @Override
    public void initViews() {
        mToolbar.setTitle("关于我们");
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);



    }

    @Override
    public void initData() {

    }
}
