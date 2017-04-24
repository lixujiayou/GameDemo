package com.example.administrator.gamedemo.widget.request;

import android.util.Log;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.model.bean.LikesInfo;
import com.example.administrator.gamedemo.utils.StringUtil;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
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
    private static boolean isFirstRequest = true;
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
        if(StringUtil.isEmpty(single)) {
            if (!isCollect) {
                BmobQuery<Share> query = new BmobQuery<>();

                query.include(Share.MomentsFields.AUTHOR_USER);
                query.setLimit(count);
                query.setSkip(curPage * count);

                query.order("-createdAt");
                query.setCachePolicy(isFirstRequest ? BmobQuery.CachePolicy.CACHE_ELSE_NETWORK : BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);

                query.findObjects(new FindListener<Share>() {
                    @Override
                    public void done(List<Share> object, BmobException e) {
                        if (e == null) {
                            if (!ToolUtil.isListEmpty(object)) {
                                Logger.d("查询SHARE==" + object.size());
                                queryCommentAndLikes(object);
                            } else {
                                Logger.d("查询SHARE没有数据");
                                onResponseSuccess(object, getRequestType());
                            }
                        } else {
                            onResponseError(e, getRequestType());
                            Logger.d("查询SHARE异常" + e.toString());
                        }
                    }

                });
            } else {

                // 查询喜欢这个帖子的所有用户，因此查询的是用户表
                BmobQuery<Share> query = new BmobQuery<Share>();
                //likes是Post表中的字段，用来存储所有喜欢该帖子的用户
                query.addWhereRelatedTo(Students.UserFields.FAV, new BmobPointer(cUser));
                query.include(Share.MomentsFields.AUTHOR_USER
                );
                query.setLimit(count);
                query.setSkip(curPage * count);

                query.order("-createdAt");
                query.setCachePolicy(isFirstRequest ? BmobQuery.CachePolicy.CACHE_ELSE_NETWORK : BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);

                query.findObjects(new FindListener<Share>() {

                    @Override
                    public void done(List<Share> object, BmobException e) {
                        if (e == null) {
                            if (!ToolUtil.isListEmpty(object)) {
                                Logger.d("查询SHARE==" + object.size());
                                queryCommentAndLikes(object);
                            } else {
                                Logger.d("查询SHARE没有数据");
                                onResponseSuccess(object, getRequestType());
                            }
                        } else {
                            onResponseError(e, getRequestType());
                            Logger.d("查询SHARE异常" + e.toString());
                        }
                    }

                });
            }
        }else{
            /**
             * 定位到一个分享
             */
            BmobQuery<Share> query = new BmobQuery<>();
            query.include(Share.MomentsFields.AUTHOR_USER);
            query.setLimit(count);
            query.setSkip(curPage * count);

            query.order("-createdAt");
            query.setCachePolicy(isFirstRequest ? BmobQuery.CachePolicy.CACHE_ELSE_NETWORK : BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query.getObject(single, new QueryListener<Share>() {
                @Override
                public void done(Share share, BmobException e) {
                    if(e == null){

                        List<Share> shares = new ArrayList<Share>();
                        if(share == null){
                            onResponseSuccess(shares, getRequestType());
                            return;
                        }

                        shares.add(share);
                        queryCommentAndLikes(shares);

                    }else{
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

        final List<CommentInfo> commentInfoList = new ArrayList<>();
        final List<Students> collectList = new ArrayList<>();

        final boolean[] isCommentRequestFin = {false};
        final boolean[] isLikesRequestFin = {false};

        if(ToolUtil.isListEmpty(momentsList)){
            onResponseSuccess(momentsList, getRequestType());
        }

        BmobQuery<CommentInfo> commentQuery = new BmobQuery<>();
        commentQuery.include(MOMENT + "," + REPLY_USER + "," + AUTHOR_USER);
        List<String> id = new ArrayList<>();
        for (Share momentsInfo : momentsList) {
            id.add(momentsInfo.getObjectId());
        }

        commentQuery.addWhereContainedIn(CommentInfo.CommentFields.MOMENT, id);
        commentQuery.order("createdAt");
        commentQuery.setLimit(1000);//默认只有100条数据，最多1000条
        commentQuery.setCachePolicy(isFirstRequest? BmobQuery.CachePolicy.CACHE_ELSE_NETWORK: BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        commentQuery.findObjects(new FindListener<CommentInfo>() {
            @Override
            public void done(List<CommentInfo> list, BmobException e) {
                if(e == null) {
                    if (!ToolUtil.isListEmpty(list)) {
                        Logger.d("查询评论=="+list.size());
                        commentInfoList.addAll(list);
                    }
                    mergeData(commentInfoList, momentsList, e);
                }else{
                    Logger.d("查询评论异常"+e.toString());
                    onResponseError(e, getRequestType());
                }
            }
        });

/*
        BmobQuery<Students> likesInfoBmobQuery = new BmobQuery<>();
        likesInfoBmobQuery.include(Students. + "," + LikesInfo.LikesField.USERID);
        likesInfoBmobQuery.addWhereContainedIn(, id);
        likesInfoBmobQuery.order("createdAt");
        likesInfoBmobQuery.setLimit(1000);
        likesInfoBmobQuery.setCachePolicy(isFirstRequest? BmobQuery.CachePolicy.CACHE_ELSE_NETWORK: BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        likesInfoBmobQuery.findObjects(new FindListener<Students>() {
            @Override
            public void done(List<Students> list, BmobException e) {
                if(e == null) {
                    isLikesRequestFin[0] = true;
                    if (!ToolUtil.isListEmpty(list)) {

                        collectList.addAll(list);

                    }
                    mergeData(isCommentRequestFin[0], isLikesRequestFin[0], commentInfoList, likesInfoList, momentsList, e);
                }else{

                    onResponseError(e, getRequestType());
                }
            }
        });*/

    }


    private void mergeData(List<CommentInfo> commentInfoList, List<Share> momentsList, BmobException e) {

        if (e != null) {
            onResponseError(e, getRequestType());
            return;
        }
        if (ToolUtil.isListEmpty(momentsList)) {
            onResponseError(new BmobException("动态数据为空"), getRequestType());
            return;
        }
        curPage++;
        HashMap<String, Share> map = new HashMap<>();
        for (Share momentsInfo : momentsList) {
            map.put(momentsInfo.getMomentid(), momentsInfo);
        }

        for (CommentInfo commentInfo : commentInfoList) {
            Share info = map.get(commentInfo.getMoment().getMomentid());
            if (info != null) {
                info.addComment(commentInfo);
            }
        }
        Logger.d("查询SHARE返回"+momentsList.size());
        onResponseSuccess(momentsList, getRequestType());
        isFirstRequest = false;
    }

    @Override
    protected void onResponseSuccess(List<Share> response, int requestType) {
        super.onResponseSuccess(response, requestType);
        isFirstRequest = false;
    }
}
