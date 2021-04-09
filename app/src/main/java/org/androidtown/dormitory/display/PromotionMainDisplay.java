package org.androidtown.dormitory.display;
/**
 * Created by AndroidApp on 2017-08-20.
 */

public class PromotionMainDisplay {

    public String title;
    public String contents;
    private String time;
    private Double orderTime;
    private String imageUrl;
    private long imageHeight;
    private long imageWidth;
    private String uid;

    public PromotionMainDisplay(){

    }

    public PromotionMainDisplay(String title, String contents, String time, Double orderTime, long imageHeight, long imageWidth, String uid) {
        this.title = title;
        this.contents = contents;
        this.time = time;
        this.orderTime = orderTime;
        this.imageUrl = imageUrl;
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }





}