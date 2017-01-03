package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.utils.UIHelper;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by lixu on 2017/12/28.
 */

public abstract class SimpleResponseListener<T> implements OnResponseListener<T> {

    @Override
    public void onStart(int requestType) {

    }

    @Override
    public void onError(BmobException e, int requestType) {
        UIHelper.ToastMessage(e.getMessage());
        e.printStackTrace();
    }
}
