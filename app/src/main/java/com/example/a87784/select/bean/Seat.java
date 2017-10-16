package com.example.a87784.select.bean;

/**
 * Created by 87784 on 2017/10/13.
 */

public class Seat {

    private int floor;  //楼
    private int room;   //书库
    private int row;    //行
    private int col;    //列

    public Seat(int floor, int room, int row, int col) {
        this.floor = floor;
        this.room = room;
        this.row = row;
        this.col = col;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public String toString() {
        return floor + " 楼第 " + room + " 书库 " + row + " 行 " + col + " 列 ";
    }
}
