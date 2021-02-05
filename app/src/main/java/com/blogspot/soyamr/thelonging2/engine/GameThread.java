package com.blogspot.soyamr.thelonging2.engine;


import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

    private boolean running;
    private GameSurface gameSurface;
    private SurfaceHolder surfaceHolder;

    public GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder) {
        this.gameSurface = gameSurface;
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();

        while (running) {
            if (!surfaceHolder.getSurface().isValid())
                continue;
            try {
                // Get Canvas from Holder and lock it.
                Canvas canvas = this.surfaceHolder.lockCanvas();
                this.gameSurface.update();
                this.gameSurface.draw(canvas);
                this.surfaceHolder.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                // Do nothing.

            }
            long now = System.nanoTime();
            // Interval to redraw game
            // (Change nanoseconds to milliseconds)
            long waitTime = (now - startTime) / 1000000;
            if (waitTime < 10) {
                waitTime = 10; // Millisecond.
            }

            try {
                // Sleep.
                sleep(waitTime);
            } catch (InterruptedException ignored) {

            }
            startTime = System.nanoTime();
        }
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}