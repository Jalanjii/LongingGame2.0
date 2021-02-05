package progark.a15.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class ObstacleSprite extends AbstractPhysSprite{
	//All sprites need a paint class to draw.
	private static Paint dummyPaint = new Paint();
	private Bitmap image;
	public ObstacleSprite(Bitmap img) {
		image=img;
		//Set bounding box
		setSize(img.getWidth(), img.getHeight());
	}

	public void collided(CollisionListener c) {}

	public void draw(Canvas c) {
		c.drawBitmap(image, getPosition().left, getPosition().top, dummyPaint);	
	}
	
}
