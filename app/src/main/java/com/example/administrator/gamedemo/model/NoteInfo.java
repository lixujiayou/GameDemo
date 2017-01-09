package com.example.administrator.gamedemo.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.UpdateListener;
import rx.Subscription;

/**
 * @auther lixu
 * Created by lixu on 2017/1/9 0009.
 */
public class NoteInfo extends BmobObject{
    private String title;
    private String content;
    private Students author;
    private Integer noteColor;
    private String noteCreateTime;

    public NoteInfo(){}

    public NoteInfo(Students author, String title, String content, Integer noteColor, String noteCreateTime) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.noteColor = noteColor;
        this.noteCreateTime = noteCreateTime;
    }

    public Integer getNoteColor() {
        return noteColor;
    }

    public void setNoteColor(Integer noteColor) {
        this.noteColor = noteColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Students getAuthor() {
        return author;
    }

    public void setAuthor(Students author) {
        this.author = author;
    }

    public String getNoteCreateTime() {
        return noteCreateTime;
    }

    public void setNoteCreateTime(String noteCreateTime) {
        this.noteCreateTime = noteCreateTime;
    }
}
