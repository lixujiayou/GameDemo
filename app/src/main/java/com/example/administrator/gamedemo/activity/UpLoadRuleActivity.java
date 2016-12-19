package com.example.administrator.gamedemo.activity;

import android.os.Bundle;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.base.BaseActivity;

/**
 * Created by Administrator on 2016/12/16 0016.
 */
public class UpLoadRuleActivity extends BaseActivity{
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_upload_rule);
    }

    @Override
    public void initViews() {
        mToolbar.setNavigationIcon(R.drawable.icon_cancle);
        mToolbar.setTitle("上传规范");
    }

    @Override
    public void initData() {

    }


}
