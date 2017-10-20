package com.example.a87784.select.bean;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;

/**
 * Created by 87784 on 2017/10/13.
 */

public class User extends BmobUser{

//    private String studentId;       //学号
    private String name;            //姓名
    private String major;           //学院
    private String identity;        //身份
    private String cookie;          //cookie
//    private String password;        //密码

    private Seat selectedSeat;      //正占用的座位
    private ArrayList<Seat> seatRecords;  //历史预定座位记录

    public User(String studentId,String password){
        super.setUsername(studentId);
        super.setPassword(password);
        seatRecords = new ArrayList<>();
    }

    public void selectSeat(Seat seat) {
        selectedSeat = seat;
    }

    private void addSeatLists(Seat seat){
        seatRecords.add(seat);
    }

    public void finishSelectSeat(){
        selectedSeat = null;
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

    public Seat getSelectedSeat() {
        return selectedSeat;
    }

    public void setSelectedSeat(Seat selectedSeat) {
        this.selectedSeat = selectedSeat;
    }

    public ArrayList<Seat> getSeatRecords() {
        return seatRecords;
    }

    public void setSeatRecords(ArrayList<Seat> seatRecords) {
        this.seatRecords = seatRecords;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
