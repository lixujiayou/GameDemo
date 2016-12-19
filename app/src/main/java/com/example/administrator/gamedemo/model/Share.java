package com.example.administrator.gamedemo.model;

import com.example.administrator.gamedemo.core.MomentsType;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * @author lixu
 * Created by lixu on 2016/12/19.
 * 分享模块bean
 */

public class Share extends BmobObject {


    public interface MomentsFields {
        String LIKES = "likes";
        String HOST = "hostinfo";
        String COMMENTS = "comments";
        String AUTHOR_USER = "author";
    }

    private Students students;
    private Students hostinfo;
    private BmobRelation likes;
    private List<Students> likesList;
    private List<CommentInfo> commentList;
    private MomentContent content;


    public Share() {
    }

    public Students getAuthor() {
        return students;
    }

    public void setAuthor(Students author) {
        this.students = author;
    }

    public String getMomentid() {
        return getObjectId();
    }

    public Students getHostinfo() {
        return hostinfo;
    }

    public void setHostinfo(Students hostinfo) {
        this.hostinfo = hostinfo;
    }

    public void setLikesBmobRelation(BmobRelation likes) {
        this.likes = likes;
    }

    public List<Students> getLikesList() {
        return likesList;
    }

    public void setLikesList(List<Students> likesList) {
        this.likesList = likesList;
    }

    public List<CommentInfo> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<CommentInfo> commentList) {
        this.commentList = commentList;
    }

    public MomentContent getContent() {
        return content;
    }

    public void setContent(MomentContent content) {
        this.content = content;
    }

    public int getMomentType() {
        if (content == null) {
            Logger.d("朋友圈内容居然是空的？？？？？MDZZ！！！！");
            return MomentsType.EMPTY_CONTENT;
        }
        return content.getMomentType();
    }

    public void addComment(CommentInfo commentInfo){
        if(this.commentList == null){
            this.commentList = new ArrayList<>();
        }
        if(commentInfo != null) {
            this.commentList.add(commentInfo);
        }
    }

    public void removeComment(CommentInfo commentInfo){
        if(commentInfo != null) {
            if(this.commentList != null) {
                this.commentList.remove(commentInfo);
            }
        }
    }
}
