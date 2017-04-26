package com.example.administrator.gamedemo.widget.request;

import android.util.Log;

import com.example.administrator.gamedemo.core.Constants;
import com.example.administrator.gamedemo.model.MomentsInfo;
import com.example.administrator.gamedemo.model.CommentInfo;
import com.example.administrator.gamedemo.model.Share;
import com.example.administrator.gamedemo.model.Students;
import com.example.administrator.gamedemo.model.Togther;
import com.example.administrator.gamedemo.model.bean.LikesInfo;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.example.administrator.gamedemo.utils.bmob.bmob.BmobInitHelper;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import static  com.example.administrator.gamedemo.model.CommentInfo.CommentFields.AUTHOR_USER;
import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.MOMENT;
import static com.example.administrator.gamedemo.model.CommentInfo.CommentFields.REPLY_USER;

/**
 * Created by lixu on 2016/11/27.
 * <p>
 * 上传题库时间线请求
 */

public class MomentsRequest extends BaseRequestClient<List<MomentsInfo>> {

    private int count = 10;
    private int curPage = 0;
    private Students cUser;

    private boolean isReadCache = true;//是否读取缓存
    private String mType;
    public MomentsRequest() {
        cUser = Constants.getInstance().getUser();
    }

    public MomentsRequest setCount(int count) {
        this.count = (count <= 0 ? 10 : count);
        return this;
    }

    public void setmType(String type){
        this.mType = type;
    }

    public MomentsRequest setCurPage(int page) {
        this.curPage = page;
        return this;
    }
    public void setCache(boolean isSet){
        this.isReadCache = isSet;
    }

    @Override
    protected void executeInternal(final int requestType, boolean showDialog) {
        BmobQuery<MomentsInfo> bmobQuery = new BmobQuery<>();
        bmobQuery.include(MomentsInfo.MomentsFields.AUTHOR_USER
                + "," + MomentsInfo.MomentsFields.HOST
                + "," + MomentsInfo.MomentsFields.CPERSON);
        bmobQuery.setLimit(count);
        bmobQuery.setSkip(curPage * count);
        bmobQuery.order("-createdAt");

        if(mType != null && mType.toString().length() > 0){
            bmobQuery.addWhereEqualTo(MomentsInfo.MomentsFields.RP,mType);
            bmobQuery.addWhereEqualTo(MomentsInfo.MomentsFields.AUTHOR_USER,new BmobPointer(cUser));
        }else{
            bmobQuery.addWhereEqualTo(MomentsInfo.MomentsFields.RP,Constants.UPLOAD_OK);
        }
        bmobQuery.setCachePolicy(!isReadCache? BmobQuery.CachePolicy.CACHE_ELSE_NETWORK: BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        bmobQuery.findObjects(new FindListener<MomentsInfo>() {
            @Override
            public void done(List<MomentsInfo> list, BmobException e) {
                if (!ToolUtil.isListEmpty(list)) {
                    queryCommentAndLikes(list);
                }else {
                    onResponseSuccess(list, getRequestType());
                }
            }
        });
    }

    private void queryCommentAndLikes(final List<MomentsInfo> momentsList) {

        final List<Students> studentsList = new ArrayList<>();
        final List<CommentInfo> commentInfoList = new ArrayList<>();

        final boolean[] isStudentsRequestFin = {false};
        final boolean[] isCommentRequestFin = {false};

        List<String> id = new ArrayList<>();
        for (MomentsInfo momentsInfo : momentsList) {
            id.add(momentsInfo.getObjectId());
        }

            BmobQuery<Students> likesQuery = new BmobQuery<>();
            likesQuery.order("-createdAt");
            likesQuery.addWhereRelatedTo("likes", new BmobPointer(id));
            likesQuery.setCachePolicy(!isReadCache? BmobQuery.CachePolicy.CACHE_ELSE_NETWORK: BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
            likesQuery.findObjects(new FindListener<Students>() {
                @Override
                public void done(List<Students> list, BmobException e) {
                    if (e == null) {
                        isStudentsRequestFin[0] = true;
                        if (!ToolUtil.isListEmpty(list)) {
                            studentsList.addAll(list);
                        }
                        mergeData(isStudentsRequestFin[0], isCommentRequestFin[0], studentsList, commentInfoList, momentsList, e);
                    } else {
                        onResponseError(e, getRequestType());
                    }
                }
            });

        BmobQuery<CommentInfo> commentQuery = new BmobQuery<>();
        commentQuery.include(MOMENT + "," + REPLY_USER + "," + AUTHOR_USER);
        commentQuery.addWhereEqualTo("moment", id);
        commentQuery.order("-createdAt");
        commentQuery.setCachePolicy(!isReadCache? BmobQuery.CachePolicy.CACHE_ELSE_NETWORK: BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        commentQuery.findObjects(new FindListener<CommentInfo>() {
            @Override
            public void done(List<CommentInfo> list, BmobException e) {

                if (e == null) {
                    isCommentRequestFin[0] = true;
                    if (!ToolUtil.isListEmpty(list)) {
                        commentInfoList.addAll(list);
                    }
                    mergeData(isStudentsRequestFin[0], isCommentRequestFin[0], studentsList, commentInfoList, momentsList, e);

                } else {
                    onResponseError(e, getRequestType());
                }
            }
        });

    }

    private void mergeData(boolean isCommentRequestFin,
                           boolean isLikeRequestFin,
                           List<Students> commentInfoList,
                           List<CommentInfo> likesInfoList,
                           List<MomentsInfo> momentsList,
                           BmobException e) {

        if (!isCommentRequestFin || !isLikeRequestFin) return;

        if (e != null) {
            onResponseError(e, getRequestType());
            return;
        }

        if (ToolUtil.isListEmpty(momentsList)) {
            onResponseError(new BmobException("没有查询到数据"), getRequestType());
            return;
        }
        curPage++;

        for(int i = 0;i < commentInfoList.size();i++){
            Students cCommentInfo = commentInfoList.get(i);
            for(int ii = 0;ii < momentsList.size();ii++){
                if(momentsList.get(ii).getObjectId().equals(cCommentInfo.getObjectId())){
                    momentsList.get(ii).addLikes(cCommentInfo);
                }
            }
        }

        for(int i = 0;i < likesInfoList.size();i++){
            CommentInfo cLikeInfo = likesInfoList.get(i);
            for(int ii = 0;ii < momentsList.size();ii++){
                if(momentsList.get(ii).getObjectId().equals(cLikeInfo.getObjectId())){
                    momentsList.get(ii).addComment(cLikeInfo);
                }
            }
        }
        onResponseSuccess(momentsList, getRequestType());
 //       isFirstRequest = false;
    }

}
