package progark.a15.model;

import android.graphics.Canvas;
import android.graphics.RectF;

public interface Sprite {
	public void update(float dt);
	public void draw(Canvas c);
	
	//All sprites have a position and size determined by a rectangle in space.
	public void setPosition(RectF p);
	public RectF getPosition();
	public void move(float dx,float dy);
	
}
