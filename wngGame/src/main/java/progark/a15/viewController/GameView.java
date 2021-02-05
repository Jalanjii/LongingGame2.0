package progark.a15.viewController;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback{
	//Game engine
	private GameEngine gEngine;

	//objects which house info about the screen
	private Context context;

	//our Thread class which houses the game loop
	private PaintThread thread;

	//Time counter for touch events. Only accept one touch event every "threshold" ms.
	private long lastTime = -1;
	//Only update five times a second.

	private long threshold = 100;

	//initialization code
	void initView(){
		//initialize our screen holder
		SurfaceHolder holder = getHolder();
		holder.addCallback( this);
		//Make our game engine. Initialize it when we know the screen size.
		gEngine = new GameEngine();
		//initialize our Thread class. A call will be made to start it later
		thread = new PaintThread(holder,context, gEngine);
		//Pass paintThread callback to game engine
		setFocusable(true);		
	}
	public void setGameOverHandler(Handler h) {
		gEngine.setGameOverHandler(h);
	}
	
	
	//class constructors
	public GameView(Context contextS, AttributeSet attrs, int defStyle){
		super(contextS, attrs, defStyle);
		context=contextS;
		initView();
	}
	public GameView(Context contextS, AttributeSet attrs){
		super(contextS, attrs);
		context=contextS;
		initView();
	}

	public void setGameSettings(Bundle gameSettings) {
		gEngine.setGameSettings(gameSettings);
	}
	public void pause() {
		thread.pause();
	}

	//these methods are overridden from the SurfaceView super class. They are automatically called when a SurfaceView is created, resumed or suspended.
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		boolean retry = true;
		//code to end gameloop
		thread.state=PaintThread.PAUSED;
		while (retry) {
			try {
				//code to kill Thread
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}

	}

	public void surfaceCreated(SurfaceHolder arg0) {
		if(thread.state==PaintThread.PAUSED){
			//When game is opened again in the Android OS
			thread = new PaintThread(getHolder(),context, gEngine);
			thread.start();
		}else{
			//creating the game Thread for the first time
			thread.start();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {		
		if(event.getAction()==MotionEvent.ACTION_UP) {
			gEngine.onTouchUp(event);
		}
		else {
			long now = System.currentTimeMillis();
			if (lastTime > -1 && (now - lastTime) < threshold) {
				// Return if a touch event was received less than "threshold" time ago
				return false;
			}
			lastTime = now;
			gEngine.onTouchDown(event);
		}
		return true;
	}
}
