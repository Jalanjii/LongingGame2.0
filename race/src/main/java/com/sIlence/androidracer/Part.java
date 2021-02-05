package com.sIlence.androidracer;

import android.graphics.Canvas;
import android.graphics.Paint;
import java.util.Random;

public class Part {
    public static int nextId = 0;

    int x;
    int y;
    GameView view;
    int direction;
    int color;
    int startColor;
    Random rand;
    Paint brush;
    String id;
    Part[] opps;
    long lastTurn;
    int dieing;

    public Part(GameView v) {
        this.view = v;
        x = 0;
        y = 0;
        direction = 1;
        color = 0xff00ffff;
        startColor = color;
        rand = new Random();
        brush = new Paint(Paint.ANTI_ALIAS_FLAG);
        id = "Part-" + nextId++;
        opps = null;
        lastTurn = 0;
        dieing = 0;
    }
    public void setOpps(Part[] o) {
        opps = o.clone();
    }
    public boolean isAlive() {
        if (dieing == 0) return true;
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDirection() {
        return direction;
    }

    public boolean collides(Part other) {
        return false;
    }


    public static int oppDirection(int di) {
        di -= 2;
        while (di < 0) di += 4;
        return di;
    }

    public void update() {}
    public void render(Canvas c) {}

    public void rotate(GameView v, int increase) {}

	public void pause() {}
	public void resume() {}
	public void stop() {}
}
