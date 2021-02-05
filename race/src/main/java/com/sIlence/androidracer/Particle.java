package com.sIlence.androidracer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Surface;
import java.util.Random;

/**
 *
 * @author Mytchel
 */
public class Particle {

	private float	mx, my;
    private float	xv, yv;
    private int		age;
    private int		stop;
    private int		start;
    private int		fade;
	private int		color;
	private Random	rand;
	private boolean	alive;
	private Paint	brush;
	private int		direction;

    public Particle (int c, int x, int y, int d, float maxSpeed, int sto, int sta) {
		rand = new Random();
		brush = new Paint(Paint.ANTI_ALIAS_FLAG);

        color = c;
        mx = x;
        my = y;
        stop = rand.nextInt(sto) + 2;
        start = sta;
		age = 0;
        xv = ((maxSpeed * 2) * rand.nextFloat()) - maxSpeed;
        yv = ((maxSpeed * 2) * rand.nextFloat()) - maxSpeed;

        fade = Color.alpha(color) / 8;
		alive = true;

		direction = d;
        if (direction == 0 && yv > 0) yv = -yv;
		if (direction == 1 && xv < 0) xv = -xv;
		if (direction == 2 && yv < 0) yv = -yv;
		if (direction == 3 && xv > 0) xv = -xv;

		if (xv * xv + yv * yv > maxSpeed * maxSpeed) {
			xv *= 0.7f;
			yv *= 0.7f;
		}
    }

    public void update() {
        if (alive) {
            if (age > start) {
                if (age > stop) {
                    xv *= 0.8f;
                    yv *= 0.8f;
                }
                mx += xv;
                my += yv;
            }
            age++;

            if ((xv < 0.001f && xv > -0.001f) && (yv < 0.001f && yv > -0.001f)) {
                int a = Color.alpha(color);
                a -= fade;
                color = Color.argb(a, Color.red(color), Color.green(color), Color.blue(color));
                if (a <= 10) alive = false;
            }
        }
    }

    public void render (Canvas c) {
        brush.setColor(color);
        c.drawPoint(mx, my, brush);
    }

	public void rotate(int oldRotation, int newRotation, int increase, int width, int height) {

		float diff;
        int midle;

        boolean clockwise = true;

        if (oldRotation == Surface.ROTATION_0 && newRotation == Surface.ROTATION_90) {
            clockwise = false;
        } else if (oldRotation == Surface.ROTATION_270 && newRotation == Surface.ROTATION_0) {
            clockwise = false;
        } else if (oldRotation == Surface.ROTATION_90 && newRotation == Surface.ROTATION_0) {
            clockwise = true;
        } else if (oldRotation == Surface.ROTATION_0 && newRotation == Surface.ROTATION_270) {
            clockwise = true;
        }

        if (clockwise) {
            midle = height / 2;
            direction++;

            diff = midle - my;
            my = midle + diff;
		} else {
            midle = width / 2;
            direction--;

            diff = midle - mx;
            mx = midle + diff;
        }

        if (direction == 0 && yv > 0) yv = -yv;
		if (direction == 1 && xv < 0) xv = -xv;
		if (direction == 2 && yv < 0) yv = -yv;
		if (direction == 3 && xv > 0) xv = -xv;
	}

	public boolean isAlive() {
		return alive;
	}
}