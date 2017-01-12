package com.example.administrator.gamedemo.utils;

import android.util.Log;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/4/8 0008.
 */
public class BaseUiListener implements IUiListener {

    protected void doComplete(JSONObject values) {
    }

    @Override
    public void onComplete(Object o) {
        Log.i("qqqqq","onComplete");
    }

    @Override
    public void onError(UiError e) {
        Log.i("qqqqq","onError:"+"code:" + e.errorCode + ", msg:" + e.errorMessage + ", detail:" + e.errorDetail);
    }
    @Override
    public void onCancel() {
        Log.i("qqqqq","onCancel");
    }
}