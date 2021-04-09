package org.androidtown.dormitory.display;
/**
 * Created by AndroidApp on 2017-08-20.
 */

public class SleepMainDisplay {

    public String building;
    public String buildingNumber;
    public String destination;
    public String from;
    public String to;
    public String name;
    public Double orderTime;
    public String reason;
    public String status;
    public String time;
    public String uid;

    public SleepMainDisplay(){

    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Double orderTime) {
        this.orderTime = orderTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public SleepMainDisplay(String building, String buildingNumber, String destination, String from, String to, String name, Double orderTime, String reason, String status, String time, String uid) {
        this.building = building;
        this.buildingNumber = buildingNumber;
        this.destination = destination;
        this.from = from;
        this.to = to;
        this.name = name;

        this.orderTime = orderTime;
        this.reason = reason;
        this.status = status;
        this.time = time;
        this.uid = uid;
    }
}