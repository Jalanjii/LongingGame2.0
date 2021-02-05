package com.sIlence.androidracer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.Surface;
import java.util.Arrays;

public class LightRacer extends Part {
    public static final int	STANDARD_LENGTH = 70;

    int[]					linex;
    int[]					liney;

    Explosion[]				explosions;
    LineFall[]				lineFall;

    int						scoreToChange;

    int						ri, a, front;

	boolean					foundSpawn;

	int						cycleStreamId;
	float					cycleVolume;
	float					cycleRate;
	float					cycleRateLimit;

    public LightRacer(GameView v, int c, int stc) {
        super(v);

        id = "LightRacer-LocalPlayer";
        linex = new int[STANDARD_LENGTH];
        liney = new int[STANDARD_LENGTH];
        direction = rand.nextInt(4);
        color = c;
        startColor = color;
        brush.setStrokeWidth(0f);

        explosions = new Explosion[3];
        Arrays.fill(explosions, new Explosion(view, color, 0, 0, 0, 0, 0, 0, 0, 1));

        lineFall = new LineFall[3];
        Arrays.fill(lineFall, new LineFall(view, startColor, linex, liney, 1));

        startColor = color;

        scoreToChange = stc;

		foundSpawn = true;

		cycleStreamId = 0;
		cycleVolume = 0.4f;
		cycleRate = 0.5f;

		if (view.game.otherDifficualty == AIRacer.DIFF_INSANE) cycleRateLimit = 6f;
		else cycleRateLimit = 4f;
	}

    @Override
    public void update() {
		updateSound();

        updateExplosions();

        if (dieing > 0) {
            dieing++;

			if (!foundSpawn) {
				findSpawn();
			}

            if (dieing == 30) {
				view.stopSound(cycleStreamId);
				cycleStreamId = 0;

				if (!view.checkScore()) {

					if (!foundSpawn) {
						spawn(view.game.kills * view.game.kills + STANDARD_LENGTH);
					} else {
						spawnSpec(linex[0], liney[0], safestDirection(), view.game.kills * view.game.kills + STANDARD_LENGTH);
					}

					dieing = 0;
					foundSpawn = true;
				}
            }
            return;
        }

        move();
        offScreen();

        if (view.game.kills * view.game.kills + STANDARD_LENGTH != linex.length) {
            if (view.game.kills > 0) {
                int[] tempx = linex.clone();
                int[] tempy = liney.clone();

                linex = new int[view.game.kills * view.game.kills + STANDARD_LENGTH];
                liney = new int[view.game.kills * view.game.kills + STANDARD_LENGTH];

                int length = (linex.length > tempx.length) ? tempx.length : linex.length;

                System.arraycopy(tempx, 0, linex, 0, length);
                System.arraycopy(tempy, 0, liney, 0, length);
            }
        }

        updateLine();
        checkCollisions();
    }

    public void die() {
        if (explosions == null || lineFall == null) return;

		view.play(view.crashSoundId(), 0.7f, 0, 1.0f);

		for (int i = 0; i < explosions.length; i++) {
            if (!explosions[i].isAlive()) {
                explosions[i] = new Explosion(view, startColor, (int) linex[0], (int) liney[0], direction, 100, 30, 0, 3, 0);
                break;
            }
        }

        for (int i = 0; i < lineFall.length; i++) {
            if (!lineFall[i].isAlive()) {
                lineFall[i] = new LineFall(view, startColor, linex, liney, 0);
                break;
            }
        }

		view.vibrate(500);

		view.changeScore(scoreToChange);
        dieing = 1;

		foundSpawn = false;
    }

    public void updateExplosions() {
        for (int i = 0; i < explosions.length; i++) {
            explosions[i].update();
        }
        for (int i = 0; i < lineFall.length; i++) {
            lineFall[i].update();
        }
    }

    public void updateLine() {
        for (int i = linex.length - 1; i > 0; i--) {
            if (linex[0] == linex[i] && liney[0] == liney[i]) {
                die();
                return;
            }

            linex[i] = linex[i - 1];
            liney[i] = liney[i - 1];
        }
    }

    public void checkCollisions() {
        if (opps == null) return;

        x = linex[0];
        y = liney[0];
        for (int i = 0; i < opps.length; i++) {
            if (opps[i] != this) {
                if (opps[i].collides(this) && opps[i].isAlive()) {
                    die();
                    break;
                }
            }
        }
    }

    public void move() {
        switch (direction) {
            case 0:
                liney[0] -= view.boxHeight();
                break;
            case 1:
                linex[0] += view.boxWidth();
                break;
            case 2:
                liney[0] += view.boxHeight();
                break;
            case 3:
                linex[0] -= view.boxWidth();
                break;
        }
    }

    @Override
    public void render(Canvas c) {
        for (ri = 0; ri < explosions.length; ri++) {
            explosions[ri].render(c);
        }
        for (ri = 0; ri < lineFall.length; ri++) {
            lineFall[ri].render(c);
        }

        if (dieing == 0) {
            renderLines(c);
        }
    }

    public void renderLines(Canvas c) {
        front = Color.argb(0, 255, 255, 255);

        brush.setColor(color);
        for (ri = linex.length - 1; ri > 0; ri--) {
            if ((((linex[ri] > linex[ri - 1]) ? (linex[ri] - linex[ri - 1]) : (linex[ri - 1] - linex[ri])) <= view.boxWidth()) &&
				(((liney[ri] > liney[ri - 1]) ? (liney[ri] - liney[ri - 1]) : (liney[ri - 1] - liney[ri])) <= view.boxHeight())) {
	            c.drawLine(linex[ri], liney[ri], linex[ri - 1], liney[ri - 1], brush);
	    }
        }
        for (ri = 10; ri > 0; ri--) {
            if ((((linex[ri] > linex[ri - 1]) ? (linex[ri] - linex[ri - 1]) : (linex[ri - 1] - linex[ri])) <= view.boxWidth()) &&
				(((liney[ri] > liney[ri - 1]) ? (liney[ri] - liney[ri - 1]) : (liney[ri - 1] - liney[ri])) <= view.boxHeight())) {
	        a = Color.alpha(front);
                a += 25;
                front = Color.argb(a, 255, 255, 255);
                brush.setColor(front);
                c.drawLine(linex[ri], liney[ri], linex[ri - 1], liney[ri - 1], brush);
            }
        }
    }

    public boolean changeDirection(int wd) { // Change to spicific direction
		if (((wd == oppDirection(direction)) || (wd == direction) || (lastTurn + 5 > view.game.ticks)) || (dieing != 0)) {
			return false;
        } else { // Can turn this way8
            direction = wd;
            lastTurn = view.game.ticks;

			if (cycleRate > 0.6) {
				cycleRate -= 0.3f;
			}

            return true;
        }
    }

    protected void newLine() {
        for (int i = 0; i < lineFall.length; i++) {
            if (!lineFall[i].isAlive()) {
                lineFall[i] = new LineFall(view, startColor, linex, liney, 0);
                break;
            }
        }

        linex = new int[view.game.deaths * view.game.deaths + STANDARD_LENGTH];
        liney = new int[view.game.deaths * view.game.deaths + STANDARD_LENGTH];
    }

	public void spawn(int length) {

		linex[0] = (rand.nextInt(view.boxsX() - 20) + 10) * view.boxWidth();
		liney[0] = (rand.nextInt(view.boxsY() - 25) + 15) * view.boxHeight();

		spawnSpec(linex[0], liney[0], safestDirection(), length);
	}

	public void findSpawn() {
		linex[0] = (rand.nextInt(view.boxsX() - 20) + 10) * view.boxWidth();
	    liney[0] = (rand.nextInt(view.boxsY() - 25) + 15) * view.boxHeight();

        if (!safeToTurn(0, 100)) {
			return;
		} else if (!safeToTurn(1, 100)) {
			return;
		} else if (!safeToTurn(2, 100)) {
			return;
		} else if (!safeToTurn(3, 100)) {
			return;
		}

		foundSpawn = true;
	}

    public void spawnSpec(int x, int y, int di, int length) {
        linex = new int[length];
        liney = new int[length];

        lastTurn = 0;
        color = startColor;

        linex[0] = x;
        liney[0] = y;
        direction = di;

		cycleRate = 0.3f;
		cycleStreamId = 0;
    }

	public int safestDirection() {
        int newDi = -1;
        int bestClearance = 0;

        for (int checkingDi = 0; checkingDi < 4; checkingDi++) {

            x = linex[0];
            y = liney[0];

            clearancetesting:
            for (int clearance = 1; clearance < view.gratestLengthInSegments(); clearance++) {
                switch (checkingDi) {
                    case 0:
                        x = linex[0];
                        y = liney[0] - (clearance * view.boxHeight());
                        break;
                    case 1:
                        x = linex[0] + (clearance * view.boxWidth());
                        y = liney[0];
                        break;
                    case 2:
                        x = linex[0];
                        y = liney[0] + (clearance * view.boxHeight());
                        break;
                    case 3:
                        x = linex[0] - (clearance * view.boxWidth());
                        y = liney[0];
                        break;
                }

				if (clearance > bestClearance) {
					newDi = checkingDi;
					bestClearance = clearance;
				}


                for (int i = 0; i < opps.length; i++) {
                    if (opps[i].collides(this)) {

                        if (clearance > bestClearance) {
                            newDi = checkingDi;
                            bestClearance = clearance;
                        }

                        break clearancetesting;
                    }
                }
            }
        }

        return newDi;
    }

    public boolean safeToTurn(int wd, int distance) {
		int distanceChecked = 0;

		x = linex[0];
        y = liney[0];

        while (distanceChecked < distance) {
            switch (wd) {
                case 0:
					distanceChecked += view.boxHeight();

                    y -= view.boxHeight();
                    break;
                case 1:
					distanceChecked += view.boxWidth();

                    x += view.boxWidth();
                    break;
                case 2:
					distanceChecked += view.boxHeight();

                    y += view.boxHeight();
                    break;
                case 3:
					distanceChecked += view.boxWidth();

                    x -= view.boxWidth();
                    break;
            }

            for (int i = 0; i < opps.length; i++) {
                if (opps[i].collides(this)) {
                    return false;
                }
            }
        }

        return true;
    }

    public void offScreen() {
        if (liney[0] < view.top() + view.boxHeight()) {
    //        newLine();

            linex[0] = view.boxsX() / 2 * view.boxWidth() + (view.boxsX() / 2 * view.boxWidth() - x);
            liney[0] = (view.boxsY() - 1) * view.boxHeight();
        } else if (liney[0] / view.boxHeight() > view.boxsY() - 1) {
      //      newLine();

            linex[0] = view.boxsX() / 2 * view.boxWidth() + (view.boxsX() / 2 * view.boxWidth() - x);
            liney[0] = view.top() + view.boxHeight();
        } else if (linex[0] < view.boxWidth()) {
        //    newLine();

            linex[0] = (view.boxsX() - 1) * view.boxWidth();
            liney[0] = view.boxsY() / 2 * view.boxHeight() + (view.boxsY() / 2 * view.boxHeight() - y) + view.top();
        } else if (linex[0] / view.boxWidth() > view.boxsX() - 1) {
          //  newLine();

            linex[0] = view.boxWidth();
            liney[0] = view.boxsY() / 2 * view.boxHeight() + (view.boxsY() / 2 * view.boxHeight() - y) + view.top();
        }
    }


    @Override
    public void rotate(GameView v, int increase) {
        int diff, midle;

        boolean clockwise = true;

		// rotation changes the way the top goes, eg top right is clockwise
		
        if (view.rotation() == Surface.ROTATION_0 && v.rotation() == Surface.ROTATION_90) {
            clockwise = false;
        } else if (view.rotation() == Surface.ROTATION_270 && v.rotation() == Surface.ROTATION_0) {
            clockwise = false;
        } else if (view.rotation() == Surface.ROTATION_90 && v.rotation() == Surface.ROTATION_0) {
            clockwise = true;
        } else if (view.rotation() == Surface.ROTATION_0 && v.rotation() == Surface.ROTATION_270) {
            clockwise = true;
        } else if (view.rotation() == Surface.ROTATION_180 && v.rotation() == Surface.ROTATION_90) {
            clockwise = true;
        } else if (view.rotation() == Surface.ROTATION_270 && v.rotation() == Surface.ROTATION_180) {
            clockwise = true;
        } else if (view.rotation() == Surface.ROTATION_90 && v.rotation() == Surface.ROTATION_180) {
            clockwise = false;
        } else if (view.rotation() == Surface.ROTATION_180 && v.rotation() == Surface.ROTATION_270) {
            clockwise = false;
        }

        if (clockwise) {
            midle = view.getHeight() / 2;
            direction++;

            for (int i = 0; i < linex.length; i++) {
                if (liney[i] == 0 && linex[i] == 0) break;

                diff = midle - liney[i];
                liney[i] = midle + diff;
				
				linex[i] = (linex[i] / view.boxWidth()) * v.boxWidth();
				liney[i] = (liney[i] / view.boxHeight()) * v.boxHeight();
            }
        } else {
            midle = view.getWidth() / 2;
            direction--;

            for (int i = 0; i < linex.length; i++) {
                if (liney[i] == 0 && linex[i] == 0) break;

                diff = midle - linex[i];
                linex[i] = midle + diff;
				
				linex[i] = (linex[i] / view.boxWidth()) * v.boxWidth();
				liney[i] = (liney[i] / view.boxHeight()) * v.boxHeight();
            }
        }

        while (direction > 3) direction -= 4;
        while (direction < 0) direction += 4;

        view = v;
        int[] lx = linex.clone();
        linex = liney.clone();
        liney = lx.clone();
    }


    @Override
    public boolean collides(Part other) {
        int lx = other.getX();
        int ly = other.getY();
        for (int i = 0; i < linex.length; i++) {
            if (lx == linex[i] && ly == liney[i]) {
                return true;
            }
        }
        return false;
    }

	@Override
	public void pause() {
		view.pauseSound(cycleStreamId);
	}

	@Override
	public void resume() {
		view.resumeSound(cycleStreamId);
	}

	@Override
	public void stop() {
		view.stopSound(cycleStreamId);
		cycleStreamId = 0;
	}

	public void updateSound() {
		if (dieing == 0) {
		    if (cycleRate < cycleRateLimit / 6) {
			cycleRate += 0.05f;
		    } else if (cycleRate < cycleRateLimit / 4) {
				cycleRate += 0.03f;
			} else if (cycleRate < cycleRateLimit / 2) {
				cycleRate += 0.01f;
			} else if (cycleRate < cycleRateLimit) {
				cycleRate += 0.005f;
			}
		} else if (cycleRate > 0.3) {
			cycleRate -= 0.3f;
		}


		if (cycleStreamId == 0) {
			cycleStreamId = view.play(view.cycleSoundId(), cycleVolume, -1, cycleRate);
		} else {
			view.setRate(cycleStreamId, cycleRate);
		}
	}
}