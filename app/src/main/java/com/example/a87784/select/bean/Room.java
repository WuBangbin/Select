package com.example.a87784.select.bean;

import android.support.v4.app.Fragment;

import com.example.a87784.select.fragment.RoomFragment;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

/**
 * Created by 87784 on 2017/10/13.
 */

public class Room extends BmobObject{

    private int floorNumber;
    private int roomNumber;
    private int[] roomTypeLists;
    private String[] seatOwnerLists;
    private RoomFragment roomFragment;

    public Room(int floorNumber,int roomNumber){
        this.floorNumber = floorNumber;
        this.roomNumber = roomNumber;
        roomTypeLists = new int[80];
        seatOwnerLists = new String[80];
    }

    public Room(int floorNumber,int roomNumber,RoomFragment roomFragment) {
        this.floorNumber = floorNumber;
        this.roomNumber = roomNumber;
        this.roomFragment = roomFragment;
        roomTypeLists = new int[80];
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

    public int[] getRoomTypeLists() {
        return roomTypeLists;
    }

    public void setRoomTypeLists(int[] roomTypeLists) {
        this.roomTypeLists = roomTypeLists;
    }

    public String[] getSeatOwnerLists() {
        return seatOwnerLists;
    }

    public void setSeatOwnerLists(String[] seatOwnerLists) {
        this.seatOwnerLists = seatOwnerLists;
    }

    public RoomFragment getRoomFragment() {
        return roomFragment;
    }

    public void setRoomFragment(RoomFragment roomFragment) {
        this.roomFragment = roomFragment;
    }

    @Override
    public String toString() {
        return floorNumber + "楼第" + roomNumber + "书库";
    }
}
