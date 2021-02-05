package progark.a15.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/*
 * Class for all items that disappear when collected
 */
public class CollectableSprite extends AbstractPhysSprite {
	//All sprites need a paint class to draw.
	private static Paint dummyPaint = new Paint();
	private Bitmap image;
	private BonusType type;
	private boolean usedUp = false;
	public CollectableSprite(Bitmap img,BonusType b) {
		image=img;
		type=b;
		//Set bounding box
		setSize(img.getWidth(), img.getHeight());
	}

	public void collided(CollisionListener c) {}

	public void draw(Canvas c) {
		if(!usedUp){
		c.drawBitmap(image, getPosition().left, getPosition().top, dummyPaint);
		}
	}
	public BonusType getType() { return type; }
	
	public void remove(){
		if(!usedUp){
			usedUp = true;
			this.setSize(0,0);
			this.move(-1000, 0);
						
		}
	}
	
	
}
