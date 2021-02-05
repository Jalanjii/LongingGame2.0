package com.blogspot.soyamr.thelonging2.elements.house;

import android.graphics.Bitmap;

import com.blogspot.soyamr.thelonging2.engine.Controller;
import com.blogspot.soyamr.thelonging2.helpers.Point;

public class Balcony extends Room {

    private RoomParent roomParent;
    private Controller controller;

    public Balcony(Bitmap balaconBitmap, Controller controller, RoomParent roomParent) {
        super(null, null, null, balaconBitmap,-1);
        this.controller = controller;
        this.roomParent = roomParent;
    }

    @Override
    public boolean isInsideFloor(int x, int y) {
        return false;
    }

    public void hasReachedDoor(int x, int y) {
    }

    @Override
    public boolean isSteppingOnRoomObject(int x, int y) {
        return false;
    }

    @Override
    public int whereAmI(int x, int y) {
        return -1;
    }

    @Override
    public Room getNextRoom() {
        return roomParent.getBedRoom();
    }
}
