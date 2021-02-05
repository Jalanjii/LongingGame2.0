package com.blogspot.soyamr.thelonging2.elements.house;

import android.graphics.Bitmap;

import com.blogspot.soyamr.thelonging2.classes.mainRoom.microActions.bookShelf.PutBook;
import com.blogspot.soyamr.thelonging2.helpers.Point;
import com.blogspot.soyamr.thelonging2.helpers.RayCastingAlgorithm;
import com.blogspot.soyamr.thelonging2.helpers.Utils;

abstract public class Room {
    public static final int LIBRARY = 0;
    public static final int TV = 1;
    public static final int KITCHEN_WINDOW = 2;

    private Point[] rightDoor;
    private Point[] leftDoor;
    private Point[] floor;
    private Bitmap roomBitmap;

    public int getFloorYend() {
        return floorYend;
    }

    int floorYend;

    Room(Point[] rightDoor, Point[] leftDoor, Point[] floor,
         Bitmap roomBitmap, int floorYend) {

        this.rightDoor = rightDoor;
        this.leftDoor = leftDoor;
        this.floor = floor;
        this.roomBitmap = roomBitmap;
        this.floorYend = floorYend;
    }

    public boolean isInsideFloor(int x, int y) {
        return RayCastingAlgorithm.isInside(floor, new Point(x, y));
    }

    public Bitmap getRoomBitmap() {
        return roomBitmap;
    }

    boolean hasReachedRightDoor(int x, int y) {
        return RayCastingAlgorithm.isInside(rightDoor, new Point(x + Utils.characterWidth, y));
    }

    boolean hasReachedLeftDoor(int x, int y) {
        return RayCastingAlgorithm.isInside(leftDoor, new Point(x, y));
    }

    public abstract void hasReachedDoor(int x, int y);

    public abstract boolean isSteppingOnRoomObject(int x, int y);

    public abstract int whereAmI(int x, int y);

    public boolean reachedFloorEnd(int y) {
        return y < floorYend;
    }

    public abstract Room getNextRoom();
}
