package progark.a15.model;

import java.util.ArrayList;
import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;


public class GameLayer {
	private ArrayList<Sprite> sprites = new ArrayList<Sprite>();


	//Does this layer check for collision?
	private boolean isPhysical;
	public GameLayer(boolean isPhys) {
		isPhysical=isPhys;
	}

	public void addSprite(Sprite s) {
		sprites.add(s);
	}
	public void removeSprite(Sprite s) {
		sprites.remove(s);
	}

	//Update all sprites and check for collisions
	public void update(float dt) {
		if(isPhysical)
			checkCollision();
		
		for(Sprite s1 : sprites) {
			s1.update(dt);
			//OPTIMIZATION LINE: This is a hack claiming that all sprites outside the screen do not need to be redrawn!
			if(s1.getPosition().bottom<0) return;
			
		}
	}	

	/*
	 * Passes draw cals to all sprites Checking of bounds might become a bottleneck! TODO: profile the draw stack.
	 */
	public void draw(Canvas c) {
		for(Sprite s : sprites) {
			//Sprite has passed below game screen. Delete it.
			if(s.getPosition().top>c.getClipBounds().bottom) {
				removeSprite(s);
				//Since one sprite is removed, we have to iterate over again (Else we have no control over the list anymore.)
				draw(c);
				return;
			}
			//Don't draw sprites above screen clip
			if(s.getPosition().bottom>c.getClipBounds().top) {
				s.draw(c);
			}
			//			If one sprite is above screen clip, the rest is too
			else { 	return;	}
		}
	}

	public void move(float dx,float dy) {
		for(Sprite s : sprites) {
			s.move(dx, dy);
		}
	}

	private void checkCollision() {

		for(Sprite s1 : sprites) {
			//Uncomment code beneath if only player sprite can collide with other objects. Makes this algorithm O(n) instead of O(n²)
			if(s1 instanceof PlayerSprite) {
				for(Sprite s2 : sprites) {
					//OPTIMIZATION LINE: if sprite is above player, none of the other sprites collide. Return
					if(s1.getPosition().top>s2.getPosition().bottom) return;
					//Collision detected! Activate casting hell!
					if(s2 instanceof CollisionListener && s2 != s1 && RectF.intersects(s1.getPosition(),s2.getPosition())) {
						//Notify both sprites of collision
						((CollisionListener) s1).collided((CollisionListener) s2);
						((CollisionListener) s2).collided((CollisionListener) s1);
					}
				}
				//PlayerSprite found. No need to iterate anymore. Return
				return;
			}
		}
	}
}
