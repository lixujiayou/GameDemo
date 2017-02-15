package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.AboutMessage;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * @auther lixu
 * Created by lixu on 2017/2/14 0014.情人节  我在写这个
 */
public class MessageRequest extends BaseRequestClient<List<AboutMessage>> {
    private int count = 10;
    private int curPage = 0;
    private boolean isReadCache = true;//是否读取缓存
    private boolean isCollect = false;
    private Students cUser;

    public MessageRequest() {
        cUser = Constants.getInstance().getUser();
    }

    public MessageRequest setCount(int count) {
        this.count = (count <= 0 ? 10 : count);
        return this;
    }

    public MessageRequest setCurPage(int page) {
        this.curPage = page;
        return this;
    }

    public void setIsCollect(boolean isC) {
        this.isCollect = isC;
    }

    public void setCache(boolean isSet) {
        this.isReadCache = isSet;
    }


    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        cUser = Constants.getInstance().getUser();

        BmobQuery<AboutMessage> query = new BmobQuery<AboutMessage>();

        //likes是Post表中的字段，用来存储所有喜欢该帖子的用户
        //   query.addWhereRelatedTo(Students.UserFields.FAV, new BmobPointer(cUser));
        query.include(AboutMessage.CUSER
                + "," + AboutMessage.MUSER
        );
        query.addWhereEqualTo(AboutMessage.CUSER, new BmobPointer(cUser));

        query.setLimit(count);
        query.setSkip(curPage * count);

        query.order("-createdAt");
        if (isReadCache) {
            boolean isCache = query.hasCachedResult(AboutMessage.class);
            if (isCache) {
                query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
            } else {
                query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
            }
        } else {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        }
        query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存2天
        query.findObjects(new FindListener<AboutMessage>() {
            @Override
            public void done(List<AboutMessage> object, BmobException e) {
                if (e == null) {
                    onResponseSuccess(object, getRequestType());
                } else {
                    onResponseError(e, getRequestType());
                }
            }
        });
    }
}
