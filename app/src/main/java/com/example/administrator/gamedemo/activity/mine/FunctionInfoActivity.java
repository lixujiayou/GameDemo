package com.example.administrator.gamedemo.activity.mine;

import android.os.Bundle;
import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.utils.base.BaseWebView;
import butterknife.BindView;
/**
 * @auther lixu
 * Created by lixu on 2017/2/20 0020.
 * 功能介绍
 */
public class FunctionInfoActivity extends BaseActivity {

    @BindView(R.id.wb_info)
    BaseWebView wbInfo;

    private String mUrl = "http://m.bibleask.icoc.me/index.jsp";

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
        wbInfo.loadUrl(mUrl);
        wbInfo.setFocusable(true);//设置有焦点
        wbInfo.setFocusableInTouchMode(true);//设置可触摸
    }
}
