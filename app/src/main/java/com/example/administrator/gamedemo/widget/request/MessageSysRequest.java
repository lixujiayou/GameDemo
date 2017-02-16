package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.AboutMessage;
import com.example.administrator.gamedemo.model.Message_;
import com.example.administrator.gamedemo.model.Students;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @auther lixu
 * Created by lixu on 2017/2/16.
 */
public class MessageSysRequest extends BaseRequestClient<List<Message_>> {
    private int count = 10;
    private int curPage = 0;
    private boolean isReadCache = true;//是否读取缓存

    public MessageSysRequest() {
    }

    public MessageSysRequest setCount(int count) {
        this.count = (count <= 0 ? 10 : count);
        return this;
    }

    public MessageSysRequest setCurPage(int page) {
        this.curPage = page;
        return this;
    }

    public void setCache(boolean isSet) {
        this.isReadCache = isSet;
    }


    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {

        BmobQuery<Message_> query = new BmobQuery<Message_>();

        query.setLimit(count);
        query.setSkip(curPage * count);

        query.order("-createdAt");
        if (isReadCache) {
            boolean isCache = query.hasCachedResult(Message_.class);
            if (isCache) {
                query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
            } else {
                query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
            }
        } else {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        }
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存2天
        query.findObjects(new FindListener<Message_>() {
            @Override
            public void done(List<Message_> object, BmobException e) {
                if (e == null) {
                    onResponseSuccess(object, getRequestType());
                } else {
                    onResponseError(e, getRequestType());
                }
            }
        });
    }
}
