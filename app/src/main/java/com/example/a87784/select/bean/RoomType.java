package com.example.a87784.select.bean;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by 87784 on 2017/10/23.
 */

public class RoomType extends BmobObject {

    private int[][] roomTypes;


    public RoomType(){
        roomTypes = new int[20][20];
    }

    public int[][] getRoomTypes() {
        return roomTypes;
    }

    public void setRoomTypes(int[][] roomTypes) {
        this.roomTypes = roomTypes;
    }
}
