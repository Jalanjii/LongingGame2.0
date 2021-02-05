package progark.a15.model;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

/*
 * AbstractSprite handles bounds and physics of a sprite.
 */

public abstract class AbstractPhysSprite implements CollisionListener,Sprite {
	private RectF position = new RectF(0,0,0,0);
	private PointF speed = new PointF(0,0);
	private PointF acceleration = new PointF(0,0);	

	public void update(float dt) {
		//Object has acceleration. Update speed.
		if(acceleration.x!=0 || acceleration.y!=0) {
			//Accelerate velocity (integration) v=v0+a*dt
			speed.offset(acceleration.x*dt, acceleration.y*dt);
		}
		//Object has speed. update position
		if(speed.x!=0 || speed.y!=0) {
			//Move object according to equation of motion: r=r0+vt+at²/2
			position.offset(speed.x*dt+acceleration.x*dt*dt/2,
					speed.y*dt+acceleration.y*dt*dt/2);
		}
	}


	public void setPosition(RectF r) { position = r; }
	public void move(float dx,float dy) { position.offset(dx, dy); }
	public RectF getPosition() { return position; }
	public void setSize(float width,float height) { position.inset(-width, -height); }
	//Some more physical parameters most sprites share.
	public void setSpeed(PointF spd) { speed = spd; }
	public void setSpeed(float x, float y) { speed.set(x, y); }
	public PointF getSpeed() { return speed; }
	public void setAcceleration(PointF acc) { acceleration=acc; }
	public void setAcceleration(float x, float y) { acceleration.set(x, y); }
	public PointF getAcceleration() { return acceleration; }

}
