package com.example.administrator.gamedemo.widget.request;

import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Togther;
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
 * Created by 大灯泡 on 2016/10/27.
 * <p>
 * 朋友圈时间线请求
 */

public class TogtherRequest extends BaseRequestClient<List<Togther>> {

    private int count = 10;
    private int curPage = 0;
    private static boolean isFirstRequest = true;
    private boolean isReadCache = true;//是否读取缓存
    private String mKey;

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
    public TogtherRequest setKey(String key) {
        this.mKey = key;
        return this;
    }

    public void setCache(boolean isSet){
        this.isReadCache = isSet;
    }
    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {

        if(StringUtil.isEmpty(mKey)){
            BmobQuery<Togther> query = new BmobQuery<>();
            query.order("-createdAt");
            query.include(Togther.MomentsFields.AUTHOR_USER+ "," + Togther.MomentsFields.HOST);
            query.setLimit(count);
            query.setSkip(curPage * count);

            query.setCachePolicy(isFirstRequest? BmobQuery.CachePolicy.CACHE_ELSE_NETWORK: BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);

        query.findObjects(new FindListener<Togther>() {
            @Override
            public void done(List<Togther> list, BmobException e) {
                if(e == null) {
                    if (!ToolUtil.isListEmpty(list)) {
                        queryCommentAndLikes(list);
                    }else{
                        onResponseSuccess(list, getRequestType());
                    }
                }else{

                    onResponseError(e, getRequestType());
                }
            }
        });
        }else{
            BmobQuery<Togther> query2 = new BmobQuery<Togther>();
            //query2.include(Togther.MomentsFields.AUTHOR_USER+ "," + Togther.MomentsFields.HOST);
            //query2.setCachePolicy(isFirstRequest? BmobQuery.CachePolicy.CACHE_ELSE_NETWORK: BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            query2.getObject(mKey, new QueryListener<Togther>() {
                @Override
                public void done(Togther togther, BmobException e) {
                    List<Togther> togthers = new ArrayList<>();
                    Logger.d("查询关于完成----1");
                    if(e == null){
                        Logger.d("查询关于完成----2");
                        if(togther != null){
                            togthers.add(togther);
                            queryCommentAndLikes(togthers);
                        }else{
                            onResponseSuccess(togthers, getRequestType());
                        }
                    }else{
                        onResponseError(e, getRequestType());
                    }
                }
            });
        }
    }

    private void queryCommentAndLikes(final List<Togther> momentsList) {
        /**
         * 因为bmob不支持在查询时把关系表也一起填充查询，因此需要手动再查一次，同时分页也要手动实现。。
         * oRz，果然没有自己写服务器来的简单，好吧，都是在下没钱的原因，我的锅
         */
        final List<CommentInfo> commentInfoList = new ArrayList<>();
        final List<LikesInfo> likesInfoList = new ArrayList<>();

        final boolean[] isCommentRequestFin = {false};
        final boolean[] isLikesRequestFin = {false};

        BmobQuery<CommentInfo> commentQuery = new BmobQuery<>();
        commentQuery.include(MOMENT + "," + REPLY_USER + "," + AUTHOR_USER);
        List<String> id = new ArrayList<>();
        for (Togther momentsInfo : momentsList) {
            id.add(momentsInfo.getObjectId());
        }
        commentQuery.addWhereContainedIn(CommentInfo.CommentFields.TOGTHER, id);
        commentQuery.order("createdAt");
        commentQuery.setLimit(1000);//默认只有100条数据，最多1000条

        commentQuery.setCachePolicy(isFirstRequest? BmobQuery.CachePolicy.CACHE_ELSE_NETWORK: BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        commentQuery.findObjects(new FindListener<CommentInfo>() {
            @Override
            public void done(List<CommentInfo> list, BmobException e) {
                if(e == null) {
                    isCommentRequestFin[0] = true;
                    if (!ToolUtil.isListEmpty(list)) {
                        Logger.d("共查询评论多少条："+list.size());
                        commentInfoList.addAll(list);
                    }
                    mergeData(isCommentRequestFin[0], isLikesRequestFin[0], commentInfoList, likesInfoList, momentsList, e);
                }else{

                    onResponseError(e, getRequestType());
                }
            }
        });

        BmobQuery<LikesInfo> likesInfoBmobQuery = new BmobQuery<>();
        likesInfoBmobQuery.include(LikesInfo.LikesField.TOGTHERID + "," + LikesInfo.LikesField.USERID);
        likesInfoBmobQuery.addWhereContainedIn(LikesInfo.LikesField.TOGTHERID, id);
        likesInfoBmobQuery.order("createdAt");
        likesInfoBmobQuery.setLimit(1000);
        likesInfoBmobQuery.setCachePolicy(isFirstRequest? BmobQuery.CachePolicy.CACHE_ELSE_NETWORK: BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        likesInfoBmobQuery.findObjects(new FindListener<LikesInfo>() {
            @Override
            public void done(List<LikesInfo> list, BmobException e) {
                if(e == null) {
                    isLikesRequestFin[0] = true;
                    if (!ToolUtil.isListEmpty(list)) {

                        likesInfoList.addAll(list);

                    }
                    mergeData(isCommentRequestFin[0], isLikesRequestFin[0], commentInfoList, likesInfoList, momentsList, e);
                }else{

                    onResponseError(e, getRequestType());
                }
            }
        });
    }

    private void mergeData(boolean isCommentRequestFin,
                           boolean isLikeRequestFin,
                           List<CommentInfo> commentInfoList,
                           List<LikesInfo> likesInfoList,
                           List<Togther> momentsList,
                           BmobException e) {

        if (!isCommentRequestFin || !isLikeRequestFin) return;

        if (e != null) {
            onResponseError(e, getRequestType());
            return;
        }

        if (ToolUtil.isListEmpty(momentsList)) {
            onResponseError(new BmobException("动态数据为空"), getRequestType());
            return;
        }
        curPage++;

        for(int i = 0;i < commentInfoList.size();i++){
            CommentInfo cCommentInfo = commentInfoList.get(i);
            for(int ii = 0;ii < momentsList.size();ii++){
                if(momentsList.get(ii).getObjectId().equals(cCommentInfo.getTogther().getObjectId())){
                    momentsList.get(ii).addComment(cCommentInfo);
                }
            }
        }

        for(int i = 0;i < likesInfoList.size();i++){
            LikesInfo cLikeInfo = likesInfoList.get(i);
            for(int ii = 0;ii < momentsList.size();ii++){
                if(momentsList.get(ii).getObjectId().equals(cLikeInfo.getTogtherid().getObjectId())){
                    momentsList.get(ii).addLikes(cLikeInfo);
                }
            }
        }
        onResponseSuccess(momentsList, getRequestType());
        isFirstRequest = false;
    }


}
