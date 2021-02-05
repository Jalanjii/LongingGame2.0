package com.sIlence.androidracer;

import android.graphics.Canvas;
public class WallRacer extends LightRacer {

    public WallRacer (GameView v, int x, int y) {
        super (v, 0xC05FFE3C, GameView.INCREASE_NULL);
		
        direction = 3;

        id = "WallRacer-" + nextId;

        lineFall = null;
        explosions = null;

        int length = view.boxsX() + view.boxsY() - view.top() / view.boxHeight() - (100 / view.game.otherDifficualty + 15);

        linex = new int[length];
        liney = new int[length];

        linex[0] = x;
        liney[0] = y;

		cycleVolume = 0.05f;

		if (view.game.otherDifficualty == AIRacer.DIFF_INSANE) cycleRateLimit = 3f;
		else cycleRateLimit = 2f;
	}

    @Override
    public boolean changeDirection(int di) {
        direction = di;

		if (cycleRate > 0.6) {
			cycleRate -= 0.6f;
		}

        return true;
    }

    @Override
    public void update() {
		updateSound();

        offScreen();
        move();
        updateLine();
    }

    public void preupdate() {
        offScreen();
        move();
        updateLine();
    }

    @Override
    public void offScreen() {
        if (linex[0] < view.boxWidth() * 2 && liney[0] > view.getHeight() / 4 * 3) {
            changeDirection(0);
        } else if (liney[0] < view.top() + (view.boxHeight() * 2) && linex[0] < view.getWidth() / 4) {
            changeDirection(1);
        } else if (linex[0] > (view.boxsX() - 2) * view.boxWidth() && liney[0] < view.getHeight() / 4) {
            changeDirection(2);
        } else if (liney[0] > (view.boxsY() - 2) * view.boxHeight() && linex[0] > view.getWidth() / 4 * 3) {
            changeDirection(3);
        }
    }

    @Override
    public void render(Canvas c) {
        renderLines(c);
    }
}