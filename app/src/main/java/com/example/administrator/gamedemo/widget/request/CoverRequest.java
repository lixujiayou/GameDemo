package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.AboutMessage;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.bean.Scriptures;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @auther lixu
 * Created by lixu on 2017/2/14 0017.
 */
public class CoverRequest extends BaseRequestClient<List<Scriptures>> {
    private int count = 2;
    private int curPage = 1;

    public CoverRequest() {
    }

    /*public CoverRequest setCount(int count) {
        this.count = (count <= 0 ? 10 : count);
        return this;
    }

    public CoverRequest setCurPage(int page) {
        this.curPage = page;
        return this;
    }*/

    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        BmobQuery<Scriptures> query = new BmobQuery<Scriptures>();
        query.order("-createdAt");
        Logger.d("开始查询");
        query.findObjects(new FindListener<Scriptures>() {
            @Override
            public void done(List<Scriptures> object, BmobException e) {
                Logger.d("查询完");
                if (e == null) {
                    onResponseSuccess(object, getRequestType());
                } else {
                    onResponseError(e, getRequestType());
                }
            }
        });
    }
}
