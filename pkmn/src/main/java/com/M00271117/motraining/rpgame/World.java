package com.M00271117.motraining.rpgame;

import java.util.Random;

import com.M00271117.motraining.framework.Pixmap;

import java.util.Random;

/** The world class that is responsible for the two elements of the world in this game. The grass and the player */
public class World {
    static final int WORLD_WIDTH = 10;
    static final int WORLD_HEIGHT = 13;
    static final int SCORE_INCREMENT = 10;
    static final float TICK_INITIAL = 0.5f;
    static final float TICK_DECREMENT = 0.05f;

    public Player player;
    public boolean gameOver = false;;
    public int score = 0;

    boolean fields[][] = new boolean[WORLD_WIDTH][WORLD_HEIGHT];
    float tickTime = 0;
    static float tick = TICK_INITIAL;
    
    static final int grassColumnNO = 14;
    static final int grassRowNO = 10;
    
    int[][] grassPosX = new int[grassColumnNO][grassRowNO];
    int[][] grassPosY = new int[grassColumnNO][grassRowNO];
    
    Pixmap[][] grassPixmap = new Pixmap[grassColumnNO][grassRowNO];
    
    /** The constructor is initialising the grass and creating a natural effect of grass, by randomly picking the left side grass picture or the right */
    public World() {
    	
        player = new Player();
        
        for(int r = 0; r < grassRowNO; r++)
        {
		    for(int c = 0; c < grassColumnNO; c++)
		    {
		    	grassPosX[c][r] = 30 +(c*30);
		    	grassPosY[c][r] = 50 - 550;
		    }
        }
        
        for(int r = 0; r < grassRowNO; r++)
        {
		    for(int c = 0; c < grassColumnNO; c++)
		    {
	    		Random drawLeft = new Random();
	    		
	    		if (drawLeft.nextBoolean())
	    			grassPixmap[c][r] =  Assets.grass_left;
	    		else
	    			grassPixmap[c][r] =  Assets.grass_right;
		    }
    	}
    }

}
