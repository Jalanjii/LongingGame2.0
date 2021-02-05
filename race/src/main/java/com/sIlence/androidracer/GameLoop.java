package com.sIlence.androidracer;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameLoop extends Thread {

    final int       wantedFps = 40;
    final int       framePeriod = 1000 / wantedFps;

    boolean         running = false;
    boolean         isPaused = false;

    GameView		view;

    public GameLoop(GameView v) {
        view = v;
        running = true;
        isPaused = true;
    }

    @Override
    public void run() {
        Canvas canvas;
        long beginTime;
        long timeDiff;
        int sleepTime;

        SurfaceHolder holder = view.getHolder();

        while (running) {
            canvas = null;
            try {
                canvas = holder.lockCanvas(null);
                synchronized (holder) {
                    beginTime = System.currentTimeMillis();

                    view.update();
                    if (view.game.otherDifficualty == AIRacer.DIFF_INSANE) view.update();

                    view.render(canvas);

                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int) (framePeriod - timeDiff);

                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (Exception e) {};
					}
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            if (isPaused) {
                synchronized (this) {
                    while (isPaused && running) {
                        try {
                            wait();
                        } catch (Exception e) {}
                    }
                }
            }

        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void pauseGame() {
        isPaused = true;
    }

    public synchronized void resumeGame() {
        isPaused = false;
        notify();
    }

    public synchronized void stopGame() {
        running = false;
        isPaused = true;
        notify();
    }
}
