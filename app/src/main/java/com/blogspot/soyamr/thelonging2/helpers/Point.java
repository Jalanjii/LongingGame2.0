package com.blogspot.soyamr.thelonging2.helpers;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point(int x, int y, boolean applyScalingFactor) {
        this(x, y);
        if (!applyScalingFactor)
            return;
        this.x /= Utils.SCALING_FACTOR_X;
        this.y /= Utils.SCALING_FACTOR_Y;
    }
}