package com.example.administrator.gamedemo.widget.request;

import android.util.Log;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.AUTHOR_USER;
import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.MOMENT;
import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.REPLY_USER;

/**
 * Created by lixu on 2016/11/27.
 * <p>
 * 朋友圈时间线请求
 */

public class ShareRequest extends BaseRequestClient<List<Share>> {
    private int count = 10;
    private int curPage = 0;
    private boolean isReadCache = true;//是否读取缓存
    private boolean isCollect = false;
    private Students cUser;
    private String single;

    public ShareRequest() {
        cUser = Constants.getInstance().getUser();
    }

    public ShareRequest setCount(int count) {
        this.count = (count <= 0 ? 10 : count);
        return this;
    }

    public ShareRequest setCurPage(int page) {
        this.curPage = page;
        return this;
    }
  public ShareRequest setKey(String sKey) {
        this.single = sKey;
        return this;
    }

    public void setIsCollect(boolean isC){
        this.isCollect = isC;
    }

    public void setCache(boolean isSet){
        this.isReadCache = isSet;
    }


    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        cUser = Constants.getInstance().getUser();

        if(!isCollect){



        BmobQuery<Share> bmobQuery = new BmobQuery<>();
            bmobQuery.include(Share.MomentsFields.AUTHOR_USER
                    + "," + Share.MomentsFields.HOST
            );

            if(single == null || single.isEmpty()) {
                bmobQuery.setLimit(count);

                bmobQuery.setSkip(curPage * count);

                bmobQuery.order("-createdAt");
                if(isReadCache) {
                    boolean isCache = bmobQuery.hasCachedResult(Share.class);
                    if (isCache) {
                        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
                    } else {
                        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
                    }
                }else{
                    bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
                }

                bmobQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存5天

                bmobQuery.findObjects(new FindListener<Share>() {
                    @Override
                    public void done(List<Share> list, BmobException e) {
                        if (!ToolUtil.isListEmpty(list)) {
                            queryCommentAndLikes(list);
                        }else {
                            onResponseSuccess(list, getRequestType());
                        }
                    }
                });
            }else{
//                if(isReadCache) {
//                    boolean isCache = bmobQuery.hasCachedResult(Share.class);
//                    if (isCache) {
//                        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
//                    } else {
//                        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
//                    }
//                }else{
//                    bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
//                }
//
//                bmobQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存5天
                bmobQuery.getObject(single, new QueryListener<Share>() {
                    @Override
                    public void done(Share share, BmobException e) {
                        List<Share> shares = new ArrayList<Share>();

                        if(e == null){
                            if(share == null){
                                onResponseSuccess(shares, getRequestType());
                            }else{
                                shares.add(share);
                                queryCommentAndLikes(shares);
                            }
                        }else{
                            onResponseError(e, getRequestType());
                        }
                    }
                });

            }




        }else {

            // 查询喜欢这个帖子的所有用户，因此查询的是用户表
            BmobQuery<Share> query = new BmobQuery<Share>();

            //likes是Post表中的字段，用来存储所有喜欢该帖子的用户
            query.addWhereRelatedTo(Students.UserFields.FAV, new BmobPointer(cUser));
            query.include(Share.MomentsFields.AUTHOR_USER
                    + "," + Share.MomentsFields.HOST
            );
            query.setLimit(count);
            query.setSkip(curPage * count);

            query.order("-createdAt");
            if (isReadCache) {
                boolean isCache = query.hasCachedResult(Share.class);
                if (isCache) {
                    query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
                } else {
                    query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
                }
            } else {
                query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            }
            query.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存5天
            query.findObjects(new FindListener<Share>() {

                @Override
                public void done(List<Share> object, BmobException e) {
                    if (e == null) {
                        if (!ToolUtil.isListEmpty(object)) {
                            queryCommentAndLikes(object);
                        } else {
                            onResponseSuccess(object, getRequestType());
                        }
                    } else {
                        onResponseError(e, getRequestType());
                    }
                }

            });
        }



    }


    private void queryCommentAndLikes(final List<Share> momentsList) {
        /**
         * 因为bmob不支持在查询时把关系表也一起填充查询，因此需要手动再查一次，同时分页也要手动实现。。
         */
        if(ToolUtil.isListEmpty(momentsList)){
            onResponseSuccess(momentsList, getRequestType());
        }
        for (int i = 0; i < momentsList.size(); i++) {
            final int currentPos = i;
            final Share momentsInfo = momentsList.get(i);
            BmobQuery<Students> likesQuery = new BmobQuery<>();

            likesQuery.addWhereRelatedTo("likes", new BmobPointer(momentsInfo));
            likesQuery.addWhereRelatedTo(Share.MomentsFields.AUTHOR_COLLECT, new BmobPointer(momentsInfo));

            likesQuery.order("-createdAt");

            if(isReadCache) {
                boolean isCache = likesQuery.hasCachedResult(Students.class);
                if (isCache) {
                    likesQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
                } else {
                    likesQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
                }
            }else{
                likesQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            }
            likesQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存2天
            likesQuery.findObjects(new FindListener<Students>() {
                @Override
                public void done(List<Students> list, BmobException e) {
                    if (!ToolUtil.isListEmpty(list)) {
                        momentsInfo.setLikesList(list);
                    }
                    BmobQuery<CommentInfo> commentQuery = new BmobQuery<>();
                    commentQuery.include(MOMENT + "," + REPLY_USER + "," + AUTHOR_USER);
                    commentQuery.addWhereEqualTo("moment", momentsInfo);
                    commentQuery.order("-createdAt");

                    if(isReadCache) {
                        boolean isCache = commentQuery.hasCachedResult(CommentInfo.class);
                        if (isCache) {
                            commentQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
                        } else {
                            commentQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
                        }
                    }else{
                        commentQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
                    }
                    commentQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存5天
                    commentQuery.findObjects(new FindListener<CommentInfo>() {
                        @Override
                        public void done(List<CommentInfo> list, BmobException e) {
                            if (!ToolUtil.isListEmpty(list)) {
                                momentsInfo.setCommentList(list);
                            }else{
                            }

                            if (e == null) {
                                Logger.d("查询当前位置"+currentPos);
                                if (currentPos == momentsList.size() - 1) {
                                    onResponseSuccess(momentsList, getRequestType());
                                    curPage++;
                                }
                            } else {
                                onResponseError(e, getRequestType());
                            }
                        }
                    });
                }
            });
        }

    }

    /*Share mMomentsInfo;
    int controlI = 0;
    private void queryCollect(final List<Share> momentsList) {
        *//**
         * 因为bmob不支持在查询时把关系表也一起填充查询，因此需要手动再查一次，同时分页也要手动实现。。
         *//*
        controlI = 0;
        for (int i = 0; i < momentsList.size(); i++) {
            mMomentsInfo = momentsList.get(i);
            BmobQuery<Students> likesQuery = new BmobQuery<>();
            likesQuery.addWhereRelatedTo(Share.MomentsFields.AUTHOR_COLLECT, new BmobPointer(mMomentsInfo));
            likesQuery.order("-createdAt");

            if(isReadCache) {
                boolean isCache = likesQuery.hasCachedResult(Students.class);
                if (isCache) {
                    likesQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
                } else {
                    likesQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
                }
            }else{
                likesQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            }
            likesQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(2));//此表示缓存2天
            likesQuery.findObjects(new FindListener<Students>() {
                @Override
                public void done(List<Students> list, BmobException e) {
                    Logger.d("得到收藏时的顺序"+controlI);

                    mMomentsInfo.setCollectList(list);
                    queryCommentAndLikes(mMomentsInfo);

                    if(controlI == momentsList.size() - 1){
                        onResponseSuccess(momentsList, getRequestType());
                                    curPage++;
                    }
                    controlI += 1;
                }
            });
        }


    }*/
}
