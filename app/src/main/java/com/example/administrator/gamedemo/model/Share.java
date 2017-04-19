package com.example.administrator.gamedemo.model;

import android.text.TextUtils;

import com.example.administrator.gamedemo.core.MomentsType;
import com.example.administrator.gamedemo.model.bean.LikesInfo;
import com.example.administrator.gamedemo.utils.StringUtil;
import com.example.administrator.gamedemo.utils.ToolUtil;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * @author lixu
 * Created by lixu on 2016/12/19.
 * 分享模块bean
 */

public class Share extends BmobObject {



    public interface MomentsFields {

        String COMMENTS = "comments";
        String AUTHOR_USER = "students";
    }

    private Students students;

    private List<CommentInfo> commentList;

    private String text;
    private String title;
    private List<BmobFile> pics;




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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public void addComment(CommentInfo commentInfo){
        if(this.commentList == null){
            this.commentList = new ArrayList<>();
        }
        if(commentInfo != null) {
            this.commentList.add(commentInfo);
        }
    }

    public List<CommentInfo> getCommentList(){
        return commentList;
    }

    public void setCollectList(List<Students> collectList) {

    }

    public void setCommentList(List<CommentInfo> commentList) {
        this.commentList = commentList;
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
        if (ToolUtil.isListEmpty(pics)) {
            if (TextUtils.isEmpty(text)) {
                return false;
            }
        }
        return true;

    }
}
