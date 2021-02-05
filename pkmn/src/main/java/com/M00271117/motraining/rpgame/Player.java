package com.M00271117.motraining.rpgame;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

import com.M00271117.motraining.framework.Graphics;
import com.M00271117.motraining.framework.Pixmap;
import com.M00271117.motraining.rpgame.GameScreen.GameState;

/** This class is used to creating the main character of the game. It is created an instance of this class inside the world class */
public class Player {
	
    public static final int UP = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int RIGHT = 3;
    
    public static final float speed = 5;
    
    public int direction;
    public int x, y;
    public int fightX, fightY;
    public int originX, originY;
    public boolean manageToRun = false;
    
    /** The player is looking on the right, while the game starts */
    public Pixmap playerPixmap = Assets.headRight[0];
    
    public String monsterName;
    public int monsterX, monsterY;
    public int monsterInitialX, monsterInitialY;
    public int powerX, powerY;
    
    /** This describes weather or not the power attack had been used on a single battle */
    boolean usedPower = false;
    
    /** This field describes how many tiles of glasses the player has walk on */
    int grassesWalked = 0;
    boolean magicFluteReceived = false;
    
    /** This describes weather or not the player has walked on enough tiles of grasses to start a fight */
    boolean attacked = false;
    boolean inGrass = false;
    
    /** This describes the frames of the walking animation of the player while running */
    float animationFrame = 0;
    
    /** This describes weather or not the player monster has finished the attack or power attack animation */
    boolean monsterHitAnimation = false;
    
    /** The amount of hit points the a basic attack can hit on others */
    int hit = 20;
    /** The amount of hit points the a power attack can hit on others */
    int powerHit = 30;
    /** The maximum amount of hit points the main character's monster can have */
    int maxHealth = 100;
    /** The current amount of hit points that the main character's monster can have */
    int currentHealth = maxHealth;
    
    int money = 0;
    
    /** This describes weather or not is player turn on a fight */
    boolean myTurn = true;
    
    /** Player Class constructor used to initialise basic values of the player class */
    public Player() {
    	
        direction = RIGHT;
        
        x = 50;
        y = 280;
        
        monsterInitialX = 60;
        monsterInitialY = 271;
        
        monsterX = monsterInitialX;
        monsterY = monsterInitialY;
        
        powerX = monsterInitialX;
        powerY = monsterInitialY;
        
        fightX = 70;
        fightY = 237;
        
        monsterName = "My Monster";
        
    }
    
    /** This draw method is use to control which set of sprites are drawn according to the direction that the player is facing. */
    public void draw(Graphics g, Pixmap currentPlayerPixmap)
    {
        if(direction == Player.UP) 
        	currentPlayerPixmap = Assets.headUp[(int)animationFrame];
        else if(direction == Player.LEFT) 
        	currentPlayerPixmap = Assets.headLeft[(int)animationFrame];
        else if(direction == Player.DOWN) 
        	currentPlayerPixmap = Assets.headDown[(int)animationFrame];
        else if(direction == Player.RIGHT) 
        	currentPlayerPixmap = Assets.headRight[(int)animationFrame];
        
        g.drawPixmap(currentPlayerPixmap, x, y);
    	
    }
  
    /** This simple method just converts integer value of the current health point of the player to strings in order to be able to be prnted */
  public void drawHealth(Graphics g)
  {
      String currentHealthString = Integer.toString(currentHealth);
      g.drawText(currentHealthString, 273, 336, Color.BLACK, 12);
  }
    
}
