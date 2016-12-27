package com.example.administrator.gamedemo.model;

import android.text.TextUtils;

import com.example.administrator.gamedemo.core.MomentsType;
import com.example.administrator.gamedemo.utils.StringUtil;
import com.example.administrator.gamedemo.utils.ToolUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * @author lixu
 * Created by lixu on 2016/12/27.
 * 分享模块bean
 */

public class Togther extends BmobObject {

    public interface MomentsFields {
        String LIKES = "likes";
        String HOST = "hostinfo";
        String COMMENTS = "commentList";
        String AUTHOR_USER = "students";
    }

    private Students students;
    private Students hostinfo;
    private BmobRelation likes;
    private List<Students> likesList;
    private List<CommentInfo> commentList;

    private String text;
    private List<BmobFile> pics;
   // private MomentContent content;

    public Togther() {
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





    public int getMomentType() {
        if (pics == null && StringUtil.noEmpty(text)) {
            return MomentsType.EMPTY_CONTENT;
        }
        return getMomentType_();
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


    /**
     * 获取动态的类型
     *
     * @return
     */
    public int getMomentType_() {
        int type = MomentsType.TEXT_ONLY;
        //图片列表为空，则只能是文字或者web
        if (ToolUtil.isListEmpty(pics)) {
                type = MomentsType.TEXT_ONLY;
        } else {
            type = MomentsType.MULTI_IMAGES;
        }
        return type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<BmobFile> getPics() {
        return pics;
    }

    public void setPics(List<BmobFile> pics) {
        this.pics = pics;
    }

    public boolean isValided() {

            if (TextUtils.isEmpty(text)) {
                return false;
            }

        return true;

    }

    public void addPicture(BmobFile pic) {
        if (pics == null) {
            pics = new ArrayList<>();
        }
        if (pics.size() < 9) {
            pics.add(pic);
        }
    }


}
