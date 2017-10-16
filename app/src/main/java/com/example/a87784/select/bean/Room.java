package com.example.a87784.select.bean;

import android.support.v4.app.Fragment;

import com.example.a87784.select.fragment.RoomFragment;

/**
 * Created by 87784 on 2017/10/13.
 */

public class Room {

    private int floorNumber;
    private int roomNumber;
    private RoomFragment roomView;

    public Room(int floorNumber,int roomNumber,RoomFragment roomView) {
        this.floorNumber = floorNumber;
        this.roomNumber = roomNumber;
        this.roomView = roomView;
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

    public RoomFragment getRoomView() {
        return roomView;
    }

    public void setRoomView(RoomFragment roomView) {
        this.roomView = roomView;
    }


}
