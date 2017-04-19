package com.example.administrator.gamedemo.model;

import android.text.TextUtils;

import com.example.administrator.gamedemo.core.Constants;

import cn.bmob.v3.BmobObject;

/**
 * Created by lixu on 2016/12/12.
 * <p>
 * 评论
 */

public class CommentInfo extends BmobObject {

    public interface CommentFields {
        String REPLY_USER = "reply";
        String MOMENT = "moment";
        String CONTENT = "content";
        String AUTHOR_USER = "author";
        String TOGTHER = "togther";
    }


    private Share moment;
    private Togther togther;
    private String content;
    private Students author;
    private Students reply;

    public Share getMoment() {
        return moment;
    }

    public void setMoment(Share moment) {
        this.moment = moment;
    }

    public Togther getTogther() {
        return togther;
    }

    public void setTogther(Togther togther) {
        this.togther = togther;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentid() {
        return getObjectId();
    }

    public Students getAuthor() {
        return author;
    }

    public void setAuthor(Students author) {
        this.author = author;
    }

    public Students getReply() {
        return reply;
    }

    public void setReply(Students reply) {
        this.reply = reply;
    }

    public boolean canDelete() {
        return author != null && TextUtils.equals(author.getObjectId(), Constants.getInstance().getUser().getObjectId());
    }


}
