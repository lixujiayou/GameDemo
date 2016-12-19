package com.example.administrator.gamedemo.widget.request;

import cn.bmob.v3.exception.BmobException;

/**
 * @author lixu
 * Created by lixu on 2016/12/19.
 */

public interface OnResponseListener<T> {
    void onStart(int requestType);
    void onSuccess(T response, int requestType);
    void onError(BmobException e, int requestType);
}
