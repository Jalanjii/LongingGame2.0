package com.M00271117.motraining.rpgame;

import com.M00271117.motraining.framework.Graphics;
import com.M00271117.motraining.framework.Pixmap;
import com.M00271117.motraining.rpgame.GameScreen.GameState;

/** Class Under development (Not Used for this game yet) */
public class Boss extends Enemies {
	
	Pixmap sleepingBoss;
	Pixmap fightingBoss;
	
	public Boss(int Type)
	{
		super(Type);
		
		X = 220;
		Y = 0;
		
		sleepingBoss = Assets.sleepingBoss;
		//fightingBoss
	}
	
	public void update(World world, GameState gameState)
	{
		if (gameState == GameState.Fighting)
			Update(world);
		
		
		
	}
//	public void draw(Graphics g)
//	{
//		Draw(g);
//	}
	
	public void drawSleeping(Graphics g, int BGposX, int BGposY)
	{
		g.drawPixmap(sleepingBoss, X + BGposX, Y + BGposY);
	}
}
