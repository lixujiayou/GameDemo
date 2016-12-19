package com.example.administrator.gamedemo.model;


import java.util.ArrayList;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by LIXU .
 * <p>
 * 用户
 */

public class Students extends BmobUser{

    public interface UserFields {
        String USERNAME = "username";
        String PASSWORD = "password";
        String NICK = "nick";
        String AUTHOR_USER = "author";
        String AVATAR = "avatar";
        String COVER="cover";
    }

//    private String username;
//    private String password;
    private String avatar;
    private String cover;
    private BmobFile user_icon;

    private int age;
    private String address;
    private String sex;
    private String myself_speak;
    private String nick_name;

    private BmobRelation favorite;

    private ArrayList<String> loveid;
    private ArrayList<String> favid;

    public Students() {
    }

   /* public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return getObjectId();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }*/


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public BmobFile getUser_icon() {
        return user_icon;
    }

    public void setUser_icon(BmobFile user_icon) {
        this.user_icon = user_icon;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getMyself_speak() {
        return myself_speak;
    }

    public void setMyself_speak(String myself_speak) {
        this.myself_speak = myself_speak;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public BmobRelation getFavorite() {
        return favorite;
    }

    public void setFavorite(BmobRelation favorite) {
        this.favorite = favorite;
    }

    public ArrayList<String> getLoveid() {
        return loveid;
    }

    public void setLoveid(ArrayList<String> loveid) {
        this.loveid = loveid;
    }

    public ArrayList<String> getFavid() {
        return favid;
    }

    public void setFavid(ArrayList<String> favid) {
        this.favid = favid;
    }





}
