package org.androidtown.dormitory.model;

/**
 * Created by AndroidApp on 2017-08-22.
 */

public class RepairComment {
    public String time;
    public String uid;
    public String text;
    public Double orderTime;


    public RepairComment(){

    }

    public RepairComment(String time, String uid, String text, Double orderTime) {
        this.time = time;
        this.uid = uid;
        this.text = text;
        this.orderTime = orderTime;
    }

    public String getTime() {

        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Double orderTime) {
        this.orderTime = orderTime;
    }
}

