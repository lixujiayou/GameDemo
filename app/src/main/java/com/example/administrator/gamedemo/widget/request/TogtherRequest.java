package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.AUTHOR_USER;
import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.MOMENT;
import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.REPLY_USER;

/**
 * Created by 大灯泡 on 2016/10/27.
 * <p>
 * 朋友圈时间线请求
 */

public class TogtherRequest extends BaseRequestClient<List<Togther>> {

    private int count = 10;
    private int curPage = 0;

    private boolean isReadCache = true;//是否读取缓存

    public TogtherRequest() {
    }

    public TogtherRequest setCount(int count) {
        this.count = (count <= 0 ? 10 : count);
        return this;
    }

    public TogtherRequest setCurPage(int page) {
        this.curPage = page;
        return this;
    }

    public void setCache(boolean isSet){
        this.isReadCache = isSet;
    }
    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        BmobQuery<Togther> bmobQuery = new BmobQuery<>();
        bmobQuery.include(Togther.MomentsFields.AUTHOR_USER
                + "," + Togther.MomentsFields.HOST
        );
        bmobQuery.setLimit(count);
        bmobQuery.setSkip(curPage * count);
        bmobQuery.order("-createdAt");


        if(isReadCache) {
            boolean isCache = bmobQuery.hasCachedResult(Togther.class);
            if (isCache) {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
            } else {
                bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
            }
        }else{
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        }
        bmobQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(5));//此表示缓存5天
        bmobQuery.findObjects(new FindListener<Togther>() {
            @Override
            public void done(List<Togther> list, BmobException e) {
                if (!ToolUtil.isListEmpty(list)) {
                    queryCommentAndLikes(list);
                }else {
                    onResponseError(e, getRequestType());
                }
            }
        });
    }

   /* private void queryCommentAndLikes(final List<Share> momentsList) {
        *//**
         * 因为bmob不支持在查询时把关系表也一起填充查询，因此需要手动再查一次，同时分页也要手动实现。。
         * oRz，果然没有自己写服务器来的简单，好吧，都是在下没钱的原因，我的锅
         *//*
        for (int i = 0; i < momentsList.size(); i++) {
            final int currentPos = i;
            final Share momentsInfo = momentsList.get(i);
            BmobQuery<Students> likesQuery = new BmobQuery<>();
            likesQuery.order("-createdAt");
            likesQuery.addWhereRelatedTo("likes", new BmobPointer(momentsInfo));
            likesQuery.findObjects(new FindListener<Students>() {
                @Override
                public void done(List<Students> list, BmobException e) {
                    BmobQuery<CommentInfo> commentQuery = new BmobQuery<>();
                    commentQuery.include(MOMENT + "," + REPLY_USER + "," + AUTHOR_USER);
                    commentQuery.addWhereEqualTo("moment", momentsInfo);
                    commentQuery.order("-createdAt");
                    commentQuery.findObjects(new FindListener<CommentInfo>() {
                        @Override
                        public void done(List<CommentInfo> list, BmobException e) {
                            if (!ToolUtil.isListEmpty(list)) {
                                momentsInfo.setCommentList(list);
                            }

                            if (e == null) {
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
    }*/
    private void queryCommentAndLikes(final List<Togther> momentsList) {
        /**
         * 因为bmob不支持在查询时把关系表也一起填充查询，因此需要手动再查一次，同时分页也要手动实现。。
         * oRz，果然没有自己写服务器来的简单，好吧，都是在下没钱的原因，我的锅
         *
         */
        for (int i = 0; i < momentsList.size(); i++) {
            final int currentPos = i;
            final Togther momentsInfo = momentsList.get(i);
            BmobQuery<Students> likesQuery = new BmobQuery<>();
            likesQuery.addWhereRelatedTo("likes", new BmobPointer(momentsInfo));
            //根据更新时间降序
            //文档:http://docs.bmob.cn/data/Android/b_developdoc/doc/index.html#查询数据
            //排序子目录
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
            likesQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(5));//此表示缓存5天

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
                    commentQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(5));//此表示缓存5天

                    commentQuery.findObjects(new FindListener<CommentInfo>() {
                        @Override
                        public void done(List<CommentInfo> list, BmobException e) {
                            if (!ToolUtil.isListEmpty(list)) {
                                momentsInfo.setCommentList(list);
                            }
                            if (e == null) {
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
}
