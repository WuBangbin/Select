package com.example.a87784.select.bean;

import android.support.v4.app.Fragment;

import com.example.a87784.select.fragment.RoomFragment;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

/**
 * Created by 87784 on 2017/10/13.
 */

public class Room extends BmobObject{

    private Integer floorNumber;
    private Integer roomNumber;
    private Integer[] roomTypeLists;
    private String[] seatOwnerLists;

    public Room(Integer floorNumber,Integer roomNumber){
        this.floorNumber = floorNumber;
        this.roomNumber = roomNumber;
        roomTypeLists = new Integer[80];
        seatOwnerLists = new String[80];
    }


    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Integer[] getRoomTypeLists() {
        return roomTypeLists;
    }

    public void setRoomTypeLists(Integer[] roomTypeLists) {
        this.roomTypeLists = roomTypeLists;
    }

    public String[] getSeatOwnerLists() {
        return seatOwnerLists;
    }

    public void setSeatOwnerLists(String[] seatOwnerLists) {
        this.seatOwnerLists = seatOwnerLists;
    }

    @Override
    public String toString() {
        return floorNumber + "楼第" + roomNumber + "书库";
    }
}
