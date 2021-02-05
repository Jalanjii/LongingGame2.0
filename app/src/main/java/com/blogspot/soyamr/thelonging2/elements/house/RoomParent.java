package com.blogspot.soyamr.thelonging2.elements.house;

import android.graphics.Bitmap;

import com.blogspot.soyamr.thelonging2.engine.Controller;

public class RoomParent {
    private Karidor karidor;
    private BedRoom bedRoom;
    private Kitchen kitchen;
    private Balcony balcony;

    public Kitchen getKitchen() {
        return kitchen;
    }

    public Balcony getBalcony() {
        return balcony;
    }

    public RoomParent(Bitmap karidor, Bitmap bedRoomBitmap, Bitmap kitchenBitmap, Bitmap balaconBitmap, Controller controller) {
        this.karidor = new Karidor(karidor, controller, this);
        bedRoom = new BedRoom(bedRoomBitmap, controller, this);
        kitchen = new Kitchen(kitchenBitmap, controller, this);
        balcony = new Balcony(balaconBitmap, controller, this);
    }

    public BedRoom getBedRoom() {
        return bedRoom;
    }

    public Karidor getKaridor() {
        return karidor;
    }
}
