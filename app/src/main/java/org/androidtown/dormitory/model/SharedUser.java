package org.androidtown.dormitory.model;

/**
 * Created by AndroidApp on 2017-08-14.
 */

public class SharedUser {
    private String uid;
    private String birth_date;
    private String building;
    private String building_number;
    private String mClass;
    private String email;
    private String name;
    private String nickname;
    private String password;
    private String council;
    private String phone_number;


    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public SharedUser(String uid, String mbirth_date, String mbuiding, String mbuilding_number, String mclass, String memail, String mname, String mnickname, String mpassword, String council, String phone_number) {
        this.uid = uid;
        this.birth_date = mbirth_date;
        this.building = mbuiding;
        this.building_number = mbuilding_number;
        this.mClass = mclass;
        this.email = memail;
        this.name = mname;
        this.nickname = mnickname;
        this.password = mpassword;
        this.council = council;
        this.phone_number = phone_number;
    }

    public SharedUser() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCouncil() {
        return council;
    }

    public void setCouncil(String council) {
        this.council = council;
    }

    public String getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(String birth_date) {
        this.birth_date = birth_date;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getBuilding_number() {
        return building_number;
    }

    public void setBuilding_number(String building_number) {
        this.building_number = building_number;
    }

    public String getmClass() {
        return mClass;
    }

    public void setmClass(String mClass) {
        this.mClass = mClass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "birth_date='" + birth_date + '\'' +
                ", building='" + building + '\'' +
                ", building_number='" + building_number + '\'' +
                ", mClass='" + mClass + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", council='" + council + '\'' +
                ", phone_number='" + phone_number + '\'' +
                '}';
    }
}
