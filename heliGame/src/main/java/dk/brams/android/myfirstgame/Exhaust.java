package dk.brams.android.myfirstgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Exhaust extends GameObject {
    private int r;

    public Exhaust(int x, int y) {
        super(x, y, 0, 0);
        r = 5;
    }

    public void update() {
        x-=10;
    }


    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(x-r, y-r, r, paint);
        canvas.drawCircle(x-r+2, y-r-2,r,paint);
        canvas.drawCircle(x-r+4, y-r+1, r, paint);
    }

}
