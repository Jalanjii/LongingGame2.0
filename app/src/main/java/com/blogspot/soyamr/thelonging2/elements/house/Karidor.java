package com.blogspot.soyamr.thelonging2.elements.house;

import android.graphics.Bitmap;

import com.blogspot.soyamr.thelonging2.engine.Controller;
import com.blogspot.soyamr.thelonging2.helpers.Point;
import com.blogspot.soyamr.thelonging2.helpers.RayCastingAlgorithm;
import com.blogspot.soyamr.thelonging2.helpers.Utils;

public class Karidor extends Room {

    //left door
    static Point[] leftDoor = {
            new Point(0, 199, true),
            new Point(82, 237, true),
            new Point(82, 771, true),
            new Point(0, 844, true),
    };

    //right door
    static Point[] rightDoor = {
            new Point(2260, 212, true),
            new Point(2188, 247, true),
            new Point(2188, 788, true),
            new Point(2260, 855, true),
    };

    static Point[] kitchenDoor = {
            new Point(348, 745, true),
            new Point(620, 745, true),
            new Point(620, 850, true),
            new Point(348, 850, true),
    };
    static Point[] afterKitchen = {
            new Point(740, 745, true),
            new Point(1012, 745, true),
            new Point(740, 850, true),
            new Point(1012, 850, true),
    };static Point[] afterAfterKitchen = {
            new Point(1418, 745, true),
            new Point(1146, 745, true),
            new Point(1422, 850, true),
            new Point(1146, 850, true),
    };

    //floor
    static Point[] floor = {
            new Point(2255, 861, true),
            new Point(2190, 805, true),
            new Point(2020, 745, true),
            new Point(2020, 885, true),
            new Point(1874, 931, true),
            new Point(1742, 861, true),
            new Point(1771, 745, true),
            new Point(1491, 745, true),
            new Point(1491, 825, true),
            new Point(1334, 745, true),
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
    public Karidor(Bitmap roomBitmap, Controller controller, RoomParent roomParent) {
        super(rightDoor, leftDoor, floor, roomBitmap, Utils.appluScallingY(745));
        this.controller = controller;
        this.roomParent = roomParent;
    }

    public void hasReachedDoor(int x, int y) {
        if (hasReachedLeftDoor(x, y)) {
            controller.changeBackground(roomParent.getBedRoom());
            controller.moveToTheRight();
        } else if (RayCastingAlgorithm.isInside(kitchenDoor,
                new Point((x + Utils.characterWidth / 2), y + Utils.characterHeight)
        )) {
            controller.changeBackground(roomParent.getKitchen());
            controller.moveToTheRight();
        } else if (hasReachedRightDoor(x, y)) {
            controller.startPokimonGame();
        } else if (RayCastingAlgorithm.isInside(afterKitchen,
                new Point(x + Utils.characterWidth / 2, y + Utils.characterHeight))) {
            controller.startRaceGame();
        }else if (RayCastingAlgorithm.isInside(afterAfterKitchen,
                new Point(x + Utils.characterWidth / 2, y + Utils.characterHeight))) {
            controller.startWangGame();
        }
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
        return null;
    }
}
