package com.sIlence.androidracer;

import android.graphics.Canvas;
import java.util.ArrayList;

/**
 *
 * @author Mytchel
 */
public class LineFall extends Part {

	protected ArrayList<Particle> particles;

	public LineFall(GameView v, int color, int[] xa, int[] ya, int state) {
		super(v);
		dieing = state;

		particles = new ArrayList<Particle>();

		int x0, y0, x1, y1, distance;
		for (int i = 0; i < xa.length - 1; i++) {
			if (xa[i + 1] == 0 && ya[i + 1] == 0) break;

			x0 = xa[i];
			y0 = ya[i];

			x1 = xa[i + 1];
			y1 = ya[i + 1];

			if (x0 != x1) { // horizontal line
				y = y0;
				x = x0;

				if (x1 > x0) { // left
					distance = x1 - x0;
					if (distance <= view.boxWidth()) {
						for (int j = 0; j < distance; j++) {
							particles.add(new Particle(color, x + j, y, 3, 0.5f, 30, rand.nextInt(5)));
						}
					}
				} else { // right
					distance = x0 - x1;
					if (distance <= view.boxWidth()) {
						for (int j = 0; j < distance; j++) {
							particles.add(new Particle(color, x - j, y, 1, 0.5f, 30, rand.nextInt(5)));
						}
					}
				}
			} else { // vertical line
				y = y0;
				x = x0;

				if (y1 > y0) { // up
					distance = y1 - y0;
					if (distance <= view.boxHeight()) {
						for (int j = 0; j < distance; j++) {
							particles.add(new Particle(color, x, y + j, 0, 0.5f, 30, rand.nextInt(5)));
						}
					}
				} else { // down
					distance = y0 - y1;
					if (distance <= view.boxWidth()) {
						for (int j = 0; j < distance; j++) {
							particles.add(new Particle(color, x, y - j, 2, 0.5f, 30, rand.nextInt(5)));
						}
					}
				}
			}
		}
	}

	@Override
	public void update() {
		if (dieing != 0) return;
		dieing = 1;
		for (int i = 0; i < particles.size(); i++) {
			if (particles.get(i).isAlive()) {
				particles.get(i).update();
				dieing = 0;
			}
		}
	}

	@Override
	public void render(Canvas c) {
		if (dieing != 0) return;
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).render(c);
		}
	}

	@Override
	public void rotate(GameView v, int increase) {
		for (int i = 0; i < particles.size(); i++) {
			particles.get(i).rotate(view.rotation(), v.rotation(), increase, view.getWidth(), view.getHeight());
		}
	}

}
