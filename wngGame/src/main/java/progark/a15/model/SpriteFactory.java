package progark.a15.model;

import java.util.HashMap;

import progark.a15.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.RectF;
/*
 * Singleton factory for creating sprites.
 * MUST BE INITIALIZED BY PROVIDING SCALING PERCENTAGE AND IMAGE RESOURCE CLASS PRIOR TO USE!
 */
import android.graphics.PointF;
import android.util.Log;

public class SpriteFactory {
	//Singleton instance
	private static final SpriteFactory instance = new SpriteFactory();
	//Variables needing to be set prior to use
	private PointF scalation= new PointF(-1,-1);
	private Resources res;
	//Dimensions of screen
	private Point screenDim = new Point(0,0);
	//Hashmap storing image id->bitmap pairs
	private HashMap<Integer,Bitmap> cachedImages = new HashMap<Integer,Bitmap>();
	
	//List of image ids usable
	private final int[] cachedImgKeys = {R.drawable.backgroundmountain,
										 R.drawable.backgroundplain,
										 R.drawable.backgroundstart,
										 R.drawable.cloud128,
										 R.drawable.flamesheet64,
										 R.drawable.sprite128,
										 R.drawable.sprite128_2,
										 R.drawable.ground,
										 R.drawable.star64,
										 R.drawable.fuelcan64,
										 R.drawable.toxicwaste,
										 R.drawable.fuelfillbackground
										 };
	
	/*
	 * Run these prior to use! If both are set, the factory automatically starts preloading images.
	 * SetScalation takes an image reference to an image supposed to fill the screen, and the real screen dimensions.
	 */
	public void setScalation(int reference,int width,int height) {
		screenDim.set(width, height);
		Bitmap screenFill = BitmapFactory.decodeResource(res, reference);
		Log.d("SCREEN SIZE","Dims: "+width+"x"+height);
		Log.d("SCREENFILL","Dims: "+screenFill.getWidth()+"x"+screenFill.getHeight());
		scalation.set((float)width/screenFill.getWidth(), (float)height/screenFill.getHeight());
		screenFill.recycle();
		Log.d("SCALATION","Dims: "+scalation.x+"x"+scalation.y);
		//Both values set. Start preloading
		if(res!=null) preloadImages();
	}
	public void setResources(Resources r) {
		res = r;
		//Both values set. Start preloading
		if(scalation.x!=-1) preloadImages();
	}
	
	//Getter method for singleton instance
	public static SpriteFactory getInstance() {
		return instance;
	}
	
	public BackgroundSprite getMountains() {
			Bitmap img = cachedImages.get(R.drawable.backgroundmountain);
			BackgroundSprite b = new BackgroundSprite(img);
			b.setPosition(new RectF(0,screenDim.y-img.getHeight(),img.getWidth(),screenDim.y));
			return b;
	}
	
	public ObstacleSprite getGround() {
		Bitmap img = cachedImages.get(R.drawable.ground);
		ObstacleSprite b = new ObstacleSprite(img);
		b.setPosition(new RectF(0,screenDim.y-img.getHeight(),img.getWidth(),screenDim.y));
		return b;
	}
	public PlayerSprite getPlayer(int playerType) {
		SpriteSheet flame = new SpriteSheet(3,2,70,cachedImages.get(R.drawable.flamesheet64));
		// Player selection
		Bitmap img;
		
		// Player selection
		if (playerType == 0) {
			img = cachedImages.get(R.drawable.sprite128); // Green player
		} else if (playerType == 1) {
			img = cachedImages.get(R.drawable.sprite128_2); // Red player
		} else {
			// This may not be necessary? Just in case something goes wrong.
			img = cachedImages.get(R.drawable.sprite128);
		}
		
		PlayerSprite p = new PlayerSprite(
				img,
				img,
				img,
				flame);
		p.setPosition(new RectF(screenDim.x/2,screenDim.y/2+20,screenDim.x/2+img.getWidth(),screenDim.y/2+20+img.getHeight()));
		return p;
	}
	
	public BackgroundSprite makeCloud() {
		Bitmap img = cachedImages.get(R.drawable.cloud128);
		BackgroundSprite cloud = new BackgroundSprite(img);
		cloud.setPosition(new RectF(0,0,img.getWidth(),img.getHeight()));
		return cloud;
	}
	
	public BackgroundSprite makeStar(){
		Bitmap img = cachedImages.get(R.drawable.star64);
		BackgroundSprite star = new BackgroundSprite(img);
		star.setPosition(new RectF(0,0,img.getWidth(), img.getHeight()));
		return star;
	}
	
	public BackgroundSprite makeFuelFillBackground(){
		Bitmap img = cachedImages.get(R.drawable.fuelfillbackground);
		BackgroundSprite fuelFill = new BackgroundSprite(img);
		fuelFill.setPosition(new RectF(0,0,img.getWidth(), img.getHeight()));
		return fuelFill;
	}
	
	public CollectableSprite makeFuel(){
		Bitmap img = cachedImages.get(R.drawable.fuelcan64);
		CollectableSprite fuelcan = new CollectableSprite(img, BonusType.FUEL_ADD);
		fuelcan.setPosition(new RectF(0,0,img.getWidth(),img.getHeight()));
		return fuelcan;
	}
	
	public CollectableSprite makeWaste(){
		Bitmap img = cachedImages.get(R.drawable.toxicwaste);
		CollectableSprite wastecan = new CollectableSprite(img, BonusType.FUEL_DEC);
		wastecan.setPosition(new RectF(0,0,img.getWidth(),img.getHeight()));
		return wastecan;
	}
	
	/*
	 * TODO:
	 * Methods for generating completely rigged sprites here
	 * 
	 * 
	 */
	
	/*
	 * Private helper method for preloading and prescaling all sprites to be used.
	 */
	private void preloadImages() {
		//Preload in new thread so this doesn't fudge up the draw thread
		//new Thread(new Runnable() {
		//	public void run() {
				Log.d("PRELOAD","Scalation at "+scalation.x+"x"+scalation.y);
				//Iterate over keys in defined images to be cached
				for(int i=0;i<cachedImgKeys.length;i++) {
					cachedImages.put(cachedImgKeys[i], getBitmap(cachedImgKeys[i]));
				}
		//	}
		//}).start();
	}
	/*
	 * Private helper method for creating Bitmap images for the cache (let's hope this fits in RAM). Outputs scaled images.
	 */
	private Bitmap getBitmap(int id) {
		return scalePercentage(BitmapFactory.decodeResource(res, id));
	}
	
	/*
	 * Scale an image by a given percentage (to fit screen)
	 */
	private Bitmap scalePercentage(Bitmap src) {
		return Bitmap.createScaledBitmap(src, (int)(src.getWidth()*scalation.x), (int)(src.getHeight()*scalation.y), true);
	}
	
	//TODO: Use this method to recalculate speed and such to real screen coordinates.
	public PointF getScalation() { return scalation; }
	public Point getScreenDims() { return screenDim; }
}
