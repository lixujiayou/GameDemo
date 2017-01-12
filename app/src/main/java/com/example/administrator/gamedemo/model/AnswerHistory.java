package com.example.administrator.gamedemo.model;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * @auther lixu
 * Created by lixu on 2017/1/10 0010.
 * 答题记录
 */

public class AnswerHistory extends BmobObject implements Serializable{

    public interface HistoryFields {
        String AUTHER = "auther";
        String TIME = "time";
        String RESULT = "result";
        String SCORE = "score";
        String TYPE = "type";
    }


    private String time;  //时间
    private String result;//称号
    private Students auther;//作者
    private int score;//分数
    private String is_upload;//分数
    private String type;//类型
    private String erro;//错题位置
    private String erroSelect;//选择的错误答案

    public AnswerHistory(){}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Students getAuther() {
        return auther;
    }

    public void setAuther(Students auther) {
        this.auther = auther;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public String getIs_upload() {
        return is_upload;
    }

    public void setIs_upload(String is_upload) {
        this.is_upload = is_upload;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getErroSelect() {
        return erroSelect;
    }

    public void setErroSelect(String erroSelect) {
        this.erroSelect = erroSelect;
    }
}
