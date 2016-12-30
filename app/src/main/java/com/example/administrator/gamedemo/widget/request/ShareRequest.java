package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
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

import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.AUTHOR_USER;
import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.MOMENT;
import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.REPLY_USER;

/**
 * Created by 大灯泡 on 2016/10/27.
 * <p>
 * 朋友圈时间线请求
 */

public class ShareRequest extends BaseRequestClient<List<Share>> {

    private int count = 10;
    private int curPage = 0;

    public ShareRequest() {
    }

    public ShareRequest setCount(int count) {
        this.count = (count <= 0 ? 10 : count);
        return this;
    }

    public ShareRequest setCurPage(int page) {
        this.curPage = page;
        return this;
    }


    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        BmobQuery<Share> bmobQuery = new BmobQuery<>();
        bmobQuery.include(Share.MomentsFields.AUTHOR_USER
                + "," + Share.MomentsFields.HOST
                + "," + Share.MomentsFields.COLLECTS
        );

        bmobQuery.setLimit(count);
        bmobQuery.setSkip(curPage * count);
        bmobQuery.order("-createdAt");

        //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
        boolean isCache = bmobQuery.hasCachedResult(Share.class);
 //       if(isCache){
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
//            Logger.d("有缓存Share");
//        }else{
//            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
//            Logger.d("没缓存Share");
//        }

        bmobQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(5));//此表示缓存一天

        bmobQuery.findObjects(new FindListener<Share>() {
            @Override
            public void done(List<Share> list, BmobException e) {
                if (!ToolUtil.isListEmpty(list)) {
                    queryCommentAndLikes(list);
                }else {
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
    private void queryCommentAndLikes(final List<Share> momentsList) {
        /**
         * 因为bmob不支持在查询时把关系表也一起填充查询，因此需要手动再查一次，同时分页也要手动实现。。
         * oRz，果然没有自己写服务器来的简单，好吧，都是在下没钱的原因，我的锅
         *
         */
        for (int i = 0; i < momentsList.size(); i++) {
            final int currentPos = i;
            final Share momentsInfo = momentsList.get(i);
            BmobQuery<Students> likesQuery = new BmobQuery<>();
            likesQuery.addWhereRelatedTo("likes", new BmobPointer(momentsInfo));
            //根据更新时间降序
            //文档:http://docs.bmob.cn/data/Android/b_developdoc/doc/index.html#查询数据
            //排序子目录
            likesQuery.order("-createdAt");


            //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
            boolean isCache = likesQuery.hasCachedResult(Students.class);
//            if(isCache){
                likesQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
//                Logger.d("有缓存Students");
//            }else{
//                likesQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
//                Logger.d("没缓存Students");
//            }
//
//            likesQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(5));//此表示缓存一天
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


                    boolean isCache = commentQuery.hasCachedResult(CommentInfo.class);
//                    if(isCache){
                        commentQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
//                        Logger.d("有缓存CommentInfo");
//                    }else{
//                        commentQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
//                        Logger.d("没缓存CommentInfo");
//                    }
//                    commentQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(5));//此表示缓存一天
                    commentQuery.findObjects(new FindListener<CommentInfo>() {
                        @Override
                        public void done(List<CommentInfo> list, BmobException e) {
                            if (!ToolUtil.isListEmpty(list)) {
                                momentsInfo.setCommentList(list);
                                Logger.d("----评论"+list.size());
                            }else{
                                Logger.d("----评论为空");
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
