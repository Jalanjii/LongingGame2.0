package dk.brams.android.myfirstgame;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Border extends GameObject {
    protected Bitmap bitmap;

    public Border(Bitmap bitmap, int x, int y, int width, int height) {
        super(x, y, width, height);
        dx = GamePanel.MOVESPEED;
        this.bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
    }

    public void update() {x +=dx;}
    public void draw(Canvas canvas) {try{canvas.drawBitmap(bitmap, x, y, null);} catch(Exception e){};}

}
