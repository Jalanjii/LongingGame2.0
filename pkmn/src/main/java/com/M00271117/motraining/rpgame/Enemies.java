package com.M00271117.motraining.rpgame;

import android.graphics.Color;
import com.M00271117.motraining.framework.Graphics;
import com.M00271117.motraining.framework.Pixmap;

/** The Enemies class is responsible for generate random monster */
public class Enemies {
	
    public static final int FIRE = 0;
    public static final int GRASS = 1;
    public static final int FLUTE = 2;
    public static final int WATER = 3;
    //public static final int BOSS = 4;
    
    public int X, Y;
    public int initialX, initialY;
    
    float animationFrame = 0;
    boolean hitAnimation;
    
    String monsterName;
    int moneyYearn;
    
    Pixmap currentEnemyPixmap;
    
    int hit;
    int maxHealth = 50;
    int currentHealth;
    
    /** The class constructor is initialising the basic fields of the class */
    public Enemies(int Type) {
    	
    	hitAnimation = true;
        
    	initialX = 213;
    	initialY = 113;
    	
    	X = 213;
		Y = 113;
        
        if (Type == FIRE)
        {
        	hit = 10;
        	currentEnemyPixmap = Assets.monsters[0];
        	monsterName = "Fire Monster";
        	moneyYearn = 5;
        }
        else if (Type == GRASS)
        {
        	hit = 30;
        	currentEnemyPixmap = Assets.monsters[1];
        	monsterName = "Grass Monster";
        	moneyYearn = 20;
        }
        else if (Type == FLUTE)
        {
        	maxHealth = 80;
        	hit = 25;
        	currentEnemyPixmap = Assets.monsters[2];
        	monsterName = "Flute Holder";
        	moneyYearn = 0;
        }
        else if (Type == WATER)
        {
        	hit = 20;
        	currentEnemyPixmap = Assets.monsters[3];
        	monsterName = "Water Monster";
        	moneyYearn = 10;
        }
//        else if (Type== BOSS)
//        {
//        	hit = 40;
//        	currentEnemyPixmap = Assets.monsters[4];
//        	monsterName = "Awaken Boss";
//        	moneyYearn = 50;
//        }
        
        currentHealth = maxHealth;
        
    }
    
    /** This draw method which is called on the main game's present method, is drawing the Pixmap of the current monster, its name and its health points */
    public void Draw(Graphics g)
    {
    	g.drawPixmap(currentEnemyPixmap, X, Y);
    	
    	g.drawText(monsterName, 20, 43, Color.BLACK, 10);
    	
    	String currentHealthString = Integer.toString(currentHealth);
    	g.drawText(currentHealthString, 73, 52, Color.BLACK, 12);
    }
    
    /** This update method is called on the main update method of the program and it is responsible to drawing the hit animation of each monster */
    public void Update(World world)
    {
    	if (hitAnimation && X > 70)
		{
    		X -= 4;
    		Y += 4;
    		
			if (X <= 70)
			{
				hitAnimation = false;
				world.player.currentHealth -= hit;
			}
		}
		else if (!hitAnimation && X < initialX)
		{
			X += 6;
			Y -= 6;
			
			if (X >= initialX)
			{
				X = initialX;
				Y = initialY;
				
				hitAnimation = true;
				world.player.myTurn = true;
			}
		}
    	
    	if (world.player.currentHealth < 0) world.player.currentHealth = 0;
    }
    
     
}
