package com.blogspot.soyamr.thelonging2.elements.character;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.blogspot.soyamr.thelonging2.engine.Controller;
import com.blogspot.soyamr.thelonging2.helpers.Utils;

import static com.blogspot.soyamr.thelonging2.helpers.Utils.BOTTOM_TO_TOP;
import static com.blogspot.soyamr.thelonging2.helpers.Utils.INITIAL_LEFT_POSITION;
import static com.blogspot.soyamr.thelonging2.helpers.Utils.LEFT_TO_RIGHT;
import static com.blogspot.soyamr.thelonging2.helpers.Utils.RIGHT_TO_LEFT;
import static com.blogspot.soyamr.thelonging2.helpers.Utils.TOP_TO_BOTTOM;

public class VovaCharacter extends GameObject {

    public static int DIRECTION = 0;
    // Row index of Image are being used.
    int rowUsing = LEFT_TO_RIGHT;

    private int colUsing;

    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;

    // Velocity of game character (pixel/millisecond)
    private static final float VELOCITY = 0.7f;


    private long lastDrawNanoTime = -1;

    Controller controller;
    private CharacterDirection characterDirection;

    public VovaCharacter(Bitmap image, int x, int y, Controller controller) {
        super(image, 4, 3, x, y);

        this.controller = controller;
        this.topToBottoms = new Bitmap[colCount]; // 3
        this.rightToLefts = new Bitmap[colCount]; // 3
        this.leftToRights = new Bitmap[colCount]; // 3
        this.bottomToTops = new Bitmap[colCount]; // 3

        for (int col = 0; col < this.colCount; col++) {
            this.topToBottoms[col] = this.createSubImageAt(TOP_TO_BOTTOM, col);
            this.rightToLefts[col] = this.createSubImageAt(RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(LEFT_TO_RIGHT, col);
            this.bottomToTops[col] = this.createSubImageAt(BOTTOM_TO_TOP, col);
        }

        characterDirection = new CharacterDirection(this);
    }

    private Bitmap[] getMoveBitmaps() {
        switch (rowUsing) {
            case BOTTOM_TO_TOP:
                return this.bottomToTops;
            case LEFT_TO_RIGHT:
                return this.leftToRights;
            case RIGHT_TO_LEFT:
                return this.rightToLefts;
            case TOP_TO_BOTTOM:
                return this.topToBottoms;
            default:
                return null;
        }
    }

    private Bitmap getCurrentMoveBitmap() {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[colUsing];
    }


    public void update() {

        if (characterDirection.checkObstacles()) {
            return;
        }
        if (++ctr % refreshRate == 0) {
            this.colUsing++;//i wonder what will happen when ctr reaches 2147483647
        }

        if (colUsing >= this.colCount) {
            this.colUsing = 0;
        }

        // Current time in nanoseconds
        long now = System.nanoTime();
        // Never once did draw.
        if (lastDrawNanoTime == -1) {
            lastDrawNanoTime = now;
        }
        // Change nanoseconds to milliseconds (1 nanosecond = 1000000 milliseconds).
        int deltaTime = (int) ((now - lastDrawNanoTime) / 1000000);

        // Distance moves
        float distance = VELOCITY * deltaTime;

        double movingVectorLength = Math.sqrt(Utils.power2(characterDirection.movingVectorX) +
                Utils.power2(characterDirection.movingVectorY));


        // Calculate the new position of the game character.
        this.x = x + (int) (distance * characterDirection.movingVectorX / movingVectorLength);
        this.y = y + (int) (distance * characterDirection.movingVectorY / movingVectorLength);

        characterDirection.reachedWall();

        rowUsing = characterDirection.getMovingDirection();

    }


    public void draw(Canvas canvas) {
        Bitmap bitmap = this.getCurrentMoveBitmap();
        canvas.drawBitmap(bitmap, x, y, null);
        // Last draw time.
        this.lastDrawNanoTime = System.nanoTime();
    }

    public void setMovingVector(int targetX, int targetY) {
        //now the character foot will follow my touch instead of its head
        characterDirection.movingVectorX = targetX - Utils.characterWidth - getX();
        characterDirection.movingVectorY = targetY - Utils.characterHeight - getY();

        characterDirection.targetX = targetX;
        characterDirection.targetY = targetY;


    }

    public void moveToTheRightOfScreen() {
        x = Utils.INITIAL_RIGHT_POSITION;
        characterDirection.movingVectorY = 0;
        characterDirection.movingVectorX = 0;
        rowUsing = RIGHT_TO_LEFT;
    }

    public void moveToTheLeftOfScreen() {
        x = INITIAL_LEFT_POSITION;
        characterDirection.movingVectorY = 0;
        characterDirection.movingVectorX = 0;
        rowUsing = LEFT_TO_RIGHT;
    }
}
