package com.example.administrator.gamedemo.activity.mine;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.administrator.gamedemo.R;
import com.example.administrator.gamedemo.utils.base.BaseActivity;
import com.example.administrator.gamedemo.utils.base.BaseWebView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @auther lixu
 * Created by lixu on 2017/2/20 0020.
 * 功能介绍
 */
public class FunctionInfoActivity extends BaseActivity {

    @BindView(R.id.wb_info)
    BaseWebView wbInfo;

    private String mUrl = "https://www.baidu.com/";

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
