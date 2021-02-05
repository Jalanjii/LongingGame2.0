package com.blogspot.soyamr.thelonging2.elements.house;

import android.graphics.Bitmap;

import com.blogspot.soyamr.thelonging2.engine.Controller;
import com.blogspot.soyamr.thelonging2.helpers.Point;
import com.blogspot.soyamr.thelonging2.helpers.Utils;

public  class Kitchen extends Room {


    //right door
    static Point[] rightDoor = {
            new Point(2260, 212, true),
            new Point(2188, 247, true),
            new Point(2188, 788, true),
            new Point(2260, 855, true),
    };

    //floor
    static Point[] floor = {
            new Point(2255, 861, true),
            new Point(2190, 805, true),
            new Point(2020, 840, true),
            new Point(2020, 885, true),
            new Point(1874, 931, true),
            new Point(1742, 861, true),
            new Point(1771, 840, true),
            new Point(1491, 840, true),
            new Point(1491, 825, true),
            new Point(1334, 840, true),
            new Point(1334, 802, true),
            new Point(137, 802, true),
            new Point(101, 753, true),
            new Point(0, 825, true),
            new Point(0, 1080, true),
            new Point(2260, 1080, true),
    };


    private RoomParent roomParent;
    private Controller controller;

    //another elements
    //elements that only belongs to LivingRoom
    public Kitchen(Bitmap roomBitmap, Controller controller, RoomParent roomParent) {
        super(rightDoor, null, floor, roomBitmap, Utils.appluScallingY(850));
        this.controller = controller;
        this.roomParent = roomParent;
    }

    public void hasReachedDoor(int x, int y) {
         if (hasReachedRightDoor(x, y)) {
            controller.changeBackground(roomParent.getKaridor());
            controller.moveToTheLeft();
        }
    }

    @Override
    public boolean isSteppingOnRoomObject(int x, int y) {
        return false;
    }

    @Override
    public int whereAmI(int x, int y) {
        if (x < Utils.appluScallingX(370))
            return Room.KITCHEN_WINDOW;
        else
            return -1;
    }

    @Override
    public Room getNextRoom() {
        return null;
    }

}