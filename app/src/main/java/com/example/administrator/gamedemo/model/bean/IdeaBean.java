package com.example.administrator.gamedemo.model.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2016/5/20 0020.
 */
public class IdeaBean extends BmobObject {

    private String content;
    private String phone_;

    public IdeaBean() {
        super();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhone_() {
        return phone_;
    }

    public void setPhone_(String phone_) {
        this.phone_ = phone_;
    }
}
