package dk.brams.android.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Player extends GameObject{
    private int score;
    private boolean up;
    private boolean playing;
    private Animation animation = new Animation();
    private long startTime;

    public Player(Bitmap verticalSprites, int width, int height, int numFrames) {

        super(100, GamePanel.HEIGHT/2, width, height);

        dy = 0;
        score = 0;

        Bitmap[] bitmaps = new Bitmap[numFrames];
        for (int i = 0; i < numFrames; i++) {
            bitmaps[i] = Bitmap.createBitmap(verticalSprites, i*width, 0, width, height);
        }

        animation.setFrames(bitmaps);
        animation.setDelay(10);
        startTime = System.nanoTime();
    }



    public void update() {
        long elapsed = (System.nanoTime()-startTime)/1000000;
        if(elapsed>100) {
            score++;
            startTime = System.nanoTime();
        }
        animation.update();

        if(up){
            dy -=1;
        } else{
            dy +=1;
        }

        if(dy>14)dy = 14;
        if(dy<-14)dy = -14;

        y += dy;
    }


    public void draw(Canvas canvas) {
        canvas.drawBitmap(animation.getImage(),x,y,null);
    }


    public void setUp(boolean b){up = b;}
    public void resetDY(){dy = 0;}
    public int getScore(){return score;}
    public void resetScore(){score = 0;}
    public boolean getPlaying(){return playing;}
    public void setPlaying(boolean b){playing = b;}
}