package progark.a15.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class SpriteSheet {
	private Bitmap bmp;
	private Rect srcRect=new Rect(0,0,0,0);
	private RectF dstRect=new RectF(0,0,0,0);
	private int width;
	private int height;
	private float step;
	private float dtBuffer=0;
	private int curX=0;
	private int curY=0;
	private Point bmpSize;
	/*
	 * Constructor. width is number of sprites in the image width.
	 * Height is number of sprites in te image height
	 * Step is number of seconds to show each sprite before switching to the next.
	 * the bitmap is the actual sprite.
	 */
	
	public SpriteSheet(int width,int height,float step, Bitmap bmp) {
		this.height = height;
		this.width = width;
		this.step = step;
		this.bmp=bmp;
		bmpSize=new Point(bmp.getWidth()/width,bmp.getHeight()/height);
		dstRect.set(0, 0, bmpSize.x, bmpSize.y);
	}
	public void update(float dt) {
		//Update frame counter
		dtBuffer+=dt;
		if(dtBuffer>step) {
			dtBuffer-=step;
			curX++;
			if(curX==width) {
				curX=0;
				curY++;
			}
			curY = (curY==height) ? 0 : curY;  

			srcRect.set(bmpSize.x*curX,bmpSize.y*curY, (1+curX)*bmpSize.x, (1+curY)*bmpSize.y);
		}		
	}
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bmp, srcRect, dstRect, null);
	}
	
	
	public Point getSize() {
		return bmpSize;
	}
	
	public int getHeight() { return height; }
	public int getWidth() { return width; }
	
	
	public void setPosition(RectF r) { dstRect.set(r); }
	public void setPosition(float newLeft,float newTop) { dstRect.offsetTo(newLeft, newTop); }
	public RectF getPosition() { return dstRect; }
	public void move(float dx,float dy) { dstRect.offset(dx, dy); }
		
}
