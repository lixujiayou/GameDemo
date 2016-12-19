package com.example.administrator.gamedemo.model;

/**
 * Created by Administrator on 2016/12/17 0017.
 */
public class ReshEvent {
    public static final int ReshOk = 1;
    public static final int ReshNo = -1;
    private int isResh ;

    public int getIsResh() {
        return isResh;
    }

    public void setIsResh(int isResh) {
        this.isResh = isResh;
    }

    public ReshEvent(int isResh) {
        this.isResh = isResh;
    }
}
