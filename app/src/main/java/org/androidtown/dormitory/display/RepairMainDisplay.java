package org.androidtown.dormitory.display;
/**
 * Created by AndroidApp on 2017-08-20.
 */

public class RepairMainDisplay {

    public String title;
    public String status;
    public String contents;
    private String time;
    private Double orderTime;
    private String imageUrl;
    private long imageHeight;
    private long imageWidth;
    private String open;
    private String uid;

    public RepairMainDisplay(){

    }

    public RepairMainDisplay(String title, String status, String contents, String time, Double orderTime, long imageHeight, long imageWidth, String open, String uid) {
        this.title = title;
        this.status = status;
        this.contents = contents;
        this.time = time;
        this.orderTime = orderTime;
        this.imageUrl = imageUrl;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.open = open;
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Double getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Double orderTime) {
        this.orderTime = orderTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(long imageHeight) {
        this.imageHeight = imageHeight;
    }

    public long getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(long imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }





}