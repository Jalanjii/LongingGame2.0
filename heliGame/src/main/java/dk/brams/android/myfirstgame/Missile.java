package dk.brams.android.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.Random;

public class Missile extends GameObject{
    private int score;
    private int speed;
    private int streamId;
    private Random rand = new Random();
    private Animation animation = new Animation();
    private Bitmap spritesheet;

    public Missile(Bitmap verticalSprites, int x, int y, int width, int height, int score, int numFrames) {

        super(x,y,width,height);

        // set capped speed depending on current game score
        speed = 7 + (int) (rand.nextDouble()*score/30);
        if(speed>40)
            speed = 40;

        // unpack images from sprites
        Bitmap[] image = new Bitmap[numFrames];
        for(int i = 0; i<numFrames;i++) {
            image[i] = Bitmap.createBitmap(verticalSprites, 0, i*height, width, height);
        }

        animation.setFrames(image);
        animation.setDelay(100-speed);
    }


    public void update() {
        x-=speed;
        animation.update();
    }


    public void draw(Canvas canvas) {
        try {
            canvas.drawBitmap(animation.getImage(),x,y,null);
        } catch(Exception e){}
    }


    @Override
    public int getWidth() {
        //offset slightly for more realistic collision detection
        return width-10;
    }

    public int getStreamId() {
        return streamId;
    }

    public void setStreamId(int streamId) {
        this.streamId = streamId;
    }
}
