package com.example.administrator.gamedemo.model;

import com.example.administrator.gamedemo.core.MomentsType;
import com.orhanobut.logger.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by Administrator on 2016/12/12 0012.
 *
 */
public class MomentsInfo extends BmobObject implements Serializable{

    public interface MomentsFields {
        String LIKES = "likes";
        String HOST = "hostinfo";
        String COMMENTS = "comments";
        String AUTHOR_USER = "author";
    }

    private Students author;
    private Students hostinfo;
    private BmobRelation likes;
    private List<Students> likesList;     //已经答过本问题的
    private List<CommentInfo> commentList;//评论
    private MomentContent content;
    private BmobFile cover;     //封面

    private String topic;  //问题的题目
    private List<String> answers;//答案s
    private String hint;    //提示

    private int no;//问题编号
    private int aw_num;//答题次数

    public MomentsInfo() {
    }

    public Students getAuthor() {
        return author;
    }

    public void setAuthor(Students author) {
        this.author = author;
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

    public void addComment(CommentInfo comment){
        if(this.commentList == null){
            this.commentList = new ArrayList<>();
        }
        this.commentList.add(comment);
    }
    public void removeComment(CommentInfo comment){
        if(this.commentList == null || comment == null){
            return;
        }
        this.commentList.remove(comment);
    }

    public MomentContent getContent() {
        return content;
    }

    public void setContent(MomentContent content) {
        this.content = content;
    }

    public int getMomentType() {
        return 0;
    }

    public BmobRelation getLikes() {
        return likes;
    }

    public void setLikes(BmobRelation likes) {
        this.likes = likes;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }


    public void addAnswer(String answer) {
        if (answers == null) {
            answers = new ArrayList<>();
        }
        if (answers.size() < 5) {
            answers.add(answer);
        }
    }


    public BmobFile getCover() {
        return cover;
    }

    public void setCover(BmobFile cover) {
        this.cover = cover;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getNo() {
        return no;
    }

    public int getAw_num() {
        return aw_num;
    }

    public void setAw_num(int aw_num) {
        this.aw_num = aw_num;
    }
}
