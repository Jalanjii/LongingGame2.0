package com.blogspot.soyamr.thelonging2.engine;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.blogspot.soyamr.thelonging2.R;
import com.blogspot.soyamr.thelonging2.ViewParent;
import com.blogspot.soyamr.thelonging2.elements.character.VovaCharacter;
import com.blogspot.soyamr.thelonging2.elements.house.Balcony;
import com.blogspot.soyamr.thelonging2.elements.house.Room;
import com.blogspot.soyamr.thelonging2.elements.house.RoomParent;
import com.blogspot.soyamr.thelonging2.helpers.Utils;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback, Controller {

    private static final int MAX_STREAMS = 100;
    Room currentRoom;
    SurfaceHolder holder;
    private GameThread gameThread;
    private VovaCharacter vova;
    private RoomParent roomParent;
    private int soundIdBackground;
    private boolean soundPoolLoaded;
    private SoundPool soundPool;
    private ViewParent refToParent;


    public GameSurface(ViewParent object) {
        super(object.getContext());
        refToParent = object;

        // Make Game Surface focusable so it can handle events.
        this.setFocusable(true);
        holder = getHolder();
        // Set callback.
        this.getHolder().addCallback(this);

        this.initSoundPool();
        this.initVovaCharacter();
        //initialize rooms
        roomParent = new RoomParent(
                refToParent.getRoomBitmap(R.drawable.karidor),
                refToParent.getRoomBitmap(R.drawable.bed_room_day),
                refToParent.getRoomBitmap(R.drawable.kitchen),
                refToParent.getRoomBitmap(R.drawable.balkon),
                this);
        currentRoom = roomParent.getBedRoom();
    }

    private void initVovaCharacter() {
        Bitmap vovaBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.vova);
        vova = new VovaCharacter(vovaBitmap,
                Utils.appluScallingX(1130), Utils.appluScallingY(500), this);
    }

    private void initSoundPool() {

        AudioAttributes audioAttrib = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

        this.soundPool = builder.build();


        // When SoundPool load complete.
        this.soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            soundPoolLoaded = true;

            // Playing background sound.
            playSoundBackground();
        });

        // Load the sound background.mp3 into SoundPool
        this.soundIdBackground = this.soundPool.load(this.getContext(), R.raw.background, 1);
    }

    public void playSoundBackground() {
        if (this.soundPoolLoaded) {
            float leftVolumn = 0.8f;
            float rightVolumn = 0.8f;
            // Play sound background.mp3
            int streamId = this.soundPool.play(this.soundIdBackground, leftVolumn, rightVolumn, 1, -1, 1f);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            int x = (int) event.getX();
            int y = (int) event.getY();
            if (!currentRoom.isInsideFloor(x, y))
                return true;
            /*
            transformed touch x = real touch x * (target pixels on x axis / real pixels on x axis)
            transformed touch y = real touch y * (target pixels on y axis / real pixels on y axis)
             */
            vova.setMovingVector(x, y);

            return true;
        }
        return false;
    }

    public void changeBackground(Room room) {
        currentRoom = room;
        refToParent.changeBackground(room);
    }

    @Override
    public void hasReachedDoor(int x, int y) {
        currentRoom.hasReachedDoor(x, y);
    }

    @Override
    public void moveToTheRight() {
        vova.moveToTheRightOfScreen();
    }

    @Override
    public void moveToTheLeft() {
        vova.moveToTheLeftOfScreen();
    }

    @Override
    public boolean steppingOnRoomObject(int x, int y) {
        return currentRoom.isSteppingOnRoomObject(x, y);
    }

    @Override
    public void whereAmI(int x, int y) {
        int objectCode = currentRoom.whereAmI(x,y);
        switch (objectCode){
            case Room.LIBRARY:
                refToParent.addButtonBookShelf();
                break;
            case Room.TV:
                refToParent.addPuzzleButton();
                break;
            case Room.KITCHEN_WINDOW:
                refToParent.addTIKTOKTOEButton();
                break;
            default:
                refToParent.removeButtons();
        }
    }

    @Override
    public boolean reachedFloorEnd(int y) {
        return currentRoom.reachedFloorEnd(y);
    }

    @Override
    public int getCurrentFloorY() {
        return currentRoom.getFloorYend();
    }

    @Override
    public void startPokimonGame() {
        refToParent.startPokimonGame();
    }

    @Override
    public void startRaceGame() {
        refToParent.startRaceGame();
    }

    @Override
    public void startWangGame() {
        refToParent.startWangGame();
    }

    public void update() {

        vova.update();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        if (currentRoom instanceof Balcony) {

            refToParent.initailizeBalaconButtons();

            return;
        }
        vova.draw(canvas);
        //if to draw something else it would go here

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        resume();
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }

    public void pause() {
        soundPool.autoPause();
        this.gameThread.setRunning(false);
        while (true) {
            // Papubrent thread must wait until the end of GameThread.
            try {
                this.gameThread.join();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {
        soundPool.autoResume();
        this.gameThread = new GameThread(this, holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
        changeBackground(currentRoom);
    }
}