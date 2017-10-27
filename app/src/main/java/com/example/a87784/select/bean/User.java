package com.example.a87784.select.bean;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;

/**
 * Created by 87784 on 2017/10/13.
 */

public class User extends BmobUser{

    private String name;            //姓名
    private String major;           //学院
    private String identity;        //身份
    private String cookie;          //cookie

    private ArrayList<String> seatRecords;  //历史预定座位记录

    public User(){

    }

    public User(String studentId,String password){
        super.setUsername(studentId);
        super.setPassword(password);
        seatRecords = new ArrayList<>();
    }



    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public ArrayList<String> getSeatRecords() {
        return seatRecords;
    }

    public void addSeatRecords(String seatRecordItem) {
        seatRecords.add(seatRecordItem);
    }
}
