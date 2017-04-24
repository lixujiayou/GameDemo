package com.example.administrator.gamedemo.utils.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * @auther lixu
 * Created by lixu on 2017/4/24 0024.
 */
/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-04-24
 * Time: 14:42
 * 类描述：自定义带进度加载条的webView
 *
 * @version :
 */
public class BaseWebView extends WebView {
    private BaseWebViewProgressBar progressBar;//进度条的矩形（进度线）
    private Handler handler;
    private WebView mWebView;
    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //实例化进度条
        progressBar = new BaseWebViewProgressBar(context);
        //设置进度条的size
        progressBar.setLayoutParams(new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //刚开始时候进度条不可见
        progressBar.setVisibility(GONE);
        //把进度条添加到webView里面
        addView(progressBar);
        //初始化handle
        handler = new Handler();
        mWebView = this;
        initSettings();
    }

    private void initSettings() {
        // 初始化设置
        WebSettings mSettings = this.getSettings();
        mSettings.setJavaScriptEnabled(true);//开启javascript
        mSettings.setDomStorageEnabled(true);//开启DOM
        mSettings.setDefaultTextEncodingName("utf-8");//设置字符编码
        //设置web页面
        mSettings.setAllowFileAccess(true);//设置支持文件流
        mSettings.setSupportZoom(true);// 支持缩放
        mSettings.setBuiltInZoomControls(true);// 支持缩放
        mSettings.setUseWideViewPort(true);// 调整到适合webview大小
        mSettings.setLoadWithOverviewMode(true);// 调整到适合webview大小
        mSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);// 屏幕自适应网页,如果没有这个，在低分辨率的手机上显示可能会异常
        mSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //提高网页加载速度，暂时阻塞图片加载，然后网页加载好了，在进行加载图片
        mSettings.setBlockNetworkImage(true);
        mSettings.setAppCacheEnabled(true);//开启缓存机制
        setWebViewClient(new MyWebClient());
        setWebChromeClient(new MyWebChromeClient());
    }

    /**
     * 自定义WebChromeClient
     */
    private class MyWebChromeClient extends WebChromeClient {
        /**
         * 进度改变的回掉
         *
         * @param view        WebView
         * @param newProgress 新进度
         */
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setProgress(100);
                handler.postDelayed(runnable, 200);//0.2秒后隐藏进度条
            } else if (progressBar.getVisibility() == GONE) {
                progressBar.setVisibility(VISIBLE);
            }
            //设置初始进度10，这样会显得效果javascript真一点，总不能从1开始吧
            if (newProgress < 10) {
                newProgress = 10;
            }
            //不断更新进度
            progressBar.setProgress(newProgress);
            super.onProgressChanged(view, newProgress);
        }
    }
    private class MyWebClient extends WebViewClient {
        /**
         * 加载过程中 拦截加载的地址url
         *
         * @param view
         * @param url  被拦截的url
         * @return
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebView.loadUrl(url);
            return true;
        }
        /**
         * 页面加载过程中，加载资源回调的方法
         *
         * @param view
         * @param url
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }
        /**
         * 页面加载完成回调的方法
         *
         * @param view
         * @param url
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 关闭图片加载阻塞
            view.getSettings().setBlockNetworkImage(false);
        }
        /**
         * 页面开始加载调用的方法
         *
         * @param view
         * @param url
         * @param favicon
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onScaleChanged(WebView view, float oldScale, float newScale) {
            super.onScaleChanged(view, oldScale, newScale);
            BaseWebView.this.requestFocus();
            BaseWebView.this.requestFocusFromTouch();
        }
    }
    /**
     *刷新界面（此处为加载完成后进度消失）
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            progressBar.setVisibility(View.GONE);
        }
    };
}
