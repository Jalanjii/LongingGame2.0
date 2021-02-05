package com.M00271117.motraining.rpgame;

import java.util.List;
import android.graphics.Color;
import java.util.Random;

import com.M00271117.motraining.framework.Game;
import com.M00271117.motraining.framework.Graphics;
import com.M00271117.motraining.framework.Pixmap;
import com.M00271117.motraining.framework.Screen;
import com.M00271117.motraining.framework.Input.TouchEvent;

/** This is the screen where the whole game happens */
public class GameScreen extends Screen {
	
	/** This is describing the state that the game is on */
    enum GameState {
        Running,
        Fighting,
        Paused,
        GameOver
    }
    GameState gameState = GameState.Running;
    
    /** This describes the state of the fight */
    enum FightState {
        Ready,
        Attack,
        Power,
        Items,
        Run,
        Kill
    }
    FightState fightState = FightState.Ready;
    
    
    World world;
    Enemies newEnemy;
    //Boss boss;
    
    int BGposX = 0;
    int BGposY = -550;
    
    
	int currentColumn = -1;
	int currentRow = -1;
	
	float attackFrame = 0;
	float fightFrame = 0;
	
	Pixmap currentPlayerPixmap;
	
	Random randomGEN = new Random();
	int grassesUntilAttack;
    
	/** Constructor which initialise the main game screen and the object instance world */
    public GameScreen(Game game) {
        super(game);
        world = new World();
        //boss = new Boss(Enemies.BOSS);
        
        grassesUntilAttack = randomGEN.nextInt(10);
    }

    @Override
    /** The main update that loops constantly through the game */
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        //game.getInput().getKeyEvents();
        
        if(gameState == GameState.Running)
        {
        	//boss.update(world, gameState);
        	
        	if (!world.player.attacked)
        	{
        		updateRunning(deltaTime, touchEvents);
            	updateGrass();
            	rest();
            	//awakeBoss();
        	}
            else if (world.player.attacked)
            {
            	attackFrame += deltaTime;

            	grassAnimation();
            	
            	if (attackFrame > 2)
        		{
        			gameState = GameState.Fighting;
        			attackFrame = 0;
    	    		world.player.usedPower = false;
    	    		world.player.myTurn = true;
    	    		
    	    		//if (world.player.inGrass)
    	    		newEnemy = new Enemies(randomGEN.nextInt(4));

        		}
            }
        }
        else if(gameState == GameState.Fighting)
        {
			world.player.grassesWalked = 0;
			grassesUntilAttack = randomGEN.nextInt(10);
			world.player.attacked = false;
			
			if (world.player.currentHealth <= 0 && newEnemy.hitAnimation)
			{
				updateGameOver(touchEvents);
			}
			else
			{
				if (world.player.myTurn || (!world.player.myTurn && newEnemy.currentHealth <= 0))
					updatePlayerFight(touchEvents);
				else if (!world.player.myTurn && newEnemy.currentHealth > 0)
					newEnemy.Update(world);
			}
			
        }
        else if(gameState == GameState.Paused)
            updatePaused(touchEvents);
        
        if (BGposY > 0) BGposY = 0;
    }
    
//    private void awakeBoss()
//    {
//    	if (game.getInput().isTouchDown(0) && world.player.direction == world.player.UP)
//    	{
//    		if(world.player.x >= 230 && world.player.x <= 250 && world.player.y == 40 && world.player.magicFluteReceived && BGposY == 0)
//			{
//				world.player.attacked = true;
//			}
//    	}
//    }
    
    /** This method is used inside the main update method of the game, in order to produce the animation of the grass only when player walk on the grass */
	private void grassAnimation()
    {
		for(int r = 0; r < world.grassRowNO; r++)
        {
	        for(int c = 0; c < world.grassColumnNO; c++)
	        {
        		
	        	if (world.player.originX > world.grassPosX[c][r] && world.player.originX < world.grassPosX[c][r] + world.grassPixmap[c][r].getWidth() &&
        			world.player.originY > world.grassPosY[c][r] && world.player.originY < world.grassPosY[c][r] + world.grassPixmap[c][r].getHeight() )
	        	{
		        		if (world.grassPixmap[c][r].equals(Assets.grass_left))
		        			world.grassPixmap[c][r] = Assets.grass_right;
		        		else if (world.grassPixmap[c][r].equals(Assets.grass_right))
		        			world.grassPixmap[c][r] = Assets.grass_left;
	        		}
	        	}
        }
    }
    
	/** This is one of the most important method of this program, it does moderate the moving and the walking animation of the character. Moreover, it check for collision of the house and lastly it increases and rests the frames when appropriate */
    private void updateRunning(float deltaTime, List<TouchEvent> touchEvents)
    {            
        
    	world.player.originX = world.player.x + (world.player.playerPixmap.getWidth()/2);
    	world.player.originY = world.player.y + (world.player.playerPixmap.getHeight()/2);
    	
    	
    	if(game.getInput().isTouchDown(0)) {
          
        	if(game.getInput().getTouchX(0) < 64 && game.getInput().getTouchY(0) < 64) {
              
                if(Settings.soundEnabled)
                    Assets.click.play(1);
                gameState = GameState.Paused;
                return;
                
            }
            
        }
        
    	//Collision Detection with house
        if (game.getInput().isTouchDown(0) && world.player.direction == world.player.UP)
        {
              if (world.player.y > 40 && world.player.y <= 250 && world.player.x >= 0 && world.player.x < 195)
                world.player.y += world.player.speed;
        }
		else if (game.getInput().isTouchDown(0) && world.player.direction == world.player.LEFT)
		{
			if (world.player.y <= 250 && world.player.y >= 0 && world.player.x >= 0 && world.player.x < 195 &&
			BGposY >= -550 && BGposY < -480)
				world.player.x += world.player.speed;
		}
		else if (game.getInput().isTouchDown(0) && world.player.direction == world.player.DOWN)
		{
			if (world.player.x >= 0 && world.player.x < 195)
			{
				
				if (world.player.y <= 190 && world.player.y >= 35 && BGposY <= -490)
					BGposY += 4;	
				
				else if (world.player.y <= 450 && BGposY <= 0 && BGposY >= -530)
					BGposY -= 4;
				
				
			}
		}
        //=====================================================
           
           
        //User Input for Moving
		if (game.getInput().isTouchDown(0) && game.getInput().getTouchX(0) < 175 && game.getInput().getTouchX(0) > 145 && game.getInput().getTouchY(0) < 410 && game.getInput().getTouchY(0) > 365)
		{

			world.player.direction = world.player.UP;
		
			if (world.player.y <= 40 && BGposY < 0)
				BGposY += 4;
		
			else if(world.player.y > 40 && BGposY <= 0)
				world.player.y -= world.player.speed;
		}
		else if(game.getInput().isTouchDown(0) && game.getInput().getTouchX(0) < 175 && game.getInput().getTouchX(0) > 145 && game.getInput().getTouchY(0) < 465 && game.getInput().getTouchY(0) > 420)
		{
			world.player.direction = world.player.DOWN;
			
			if (world.player.x >= 195 && world.player.x <= 300)
			{
				if (world.player.y <= 450 && BGposY <= 0 && BGposY >= -530)
					BGposY -= 4;
				
				else if(world.player.y < 290 && BGposY <= 0)
					world.player.y += world.player.speed;
			}
			else if(world.player.y < 290 && world.player.y > 250 && BGposY <= 0)
				world.player.y += world.player.speed;
		}
		else if(game.getInput().isTouchDown(0) && game.getInput().getTouchX(0) < 145 && game.getInput().getTouchX(0) > 115 && game.getInput().getTouchY(0) < 430 && game.getInput().getTouchY(0) > 400)
		{
			world.player.direction = world.player.LEFT;
		
			if (world.player.x > 20 && BGposX < 0 && BGposX >= -180)
				BGposX += 4;
			
			else if(world.player.x > 20 && BGposX <= 0)
				world.player.x -= world.player.speed;
		}
		else if(game.getInput().isTouchDown(0) && game.getInput().getTouchX(0) < 210 && game.getInput().getTouchX(0) > 165 && game.getInput().getTouchY(0) < 430 && game.getInput().getTouchY(0) > 400) 
		{
			world.player.direction = world.player.RIGHT;
		
			if (world.player.x >= 275 && BGposX <= 0 && BGposX >= -160)
				BGposX -= 4;
			
			else if(world.player.x < 280 && BGposX <= 0)
				world.player.x += world.player.speed;
		}
		//==========================================================================
		
		if (game.getInput().isTouchDown(0))
		{
			world.player.animationFrame += (deltaTime*4);
	        
	        if (world.player.animationFrame > 3) world.player.animationFrame = 1;
		}
		else
		{
			world.player.animationFrame = 0;
		}
    }
    
    /** A very simple method that reset the player's health point back to its maximum value */
    private void rest()
    {
    	if (world.player.y == 250 && world.player.x >= 80 && world.player.x <= 100)
    		world.player.currentHealth = world.player.maxHealth;
    }
    
    /** In this method, the position of the grass are updated in order to be visible after scrolling the background. Additionally, this is responsible for counting how many tiles of grasses the character has walked on and once this number reach the appropriate random number generated, this brings up the fight screen*/
    private void updateGrass()
    {
    	for(int r = 0; r < world.grassRowNO; r++)
        {
	        for(int c = 0; c < world.grassColumnNO; c++)
	        {
	        	if (c < (world.grassColumnNO/2))
	        		world.grassPosX[c][r] = 30 + (c*30) + BGposX;
	        	else
	        		world.grassPosX[c][r] = 60 + (c*30) + BGposX;
	        	
	        	world.grassPosY[c][r] = 50 + (r*20) + BGposY;
        		
	        	if (world.player.originX > world.grassPosX[c][r] && world.player.originX < world.grassPosX[c][r] + world.grassPixmap[c][r].getWidth() &&
        			world.player.originY > world.grassPosY[c][r] && world.player.originY < world.grassPosY[c][r] + world.grassPixmap[c][r].getHeight() )
	        	{
	        		world.player.inGrass = true;
	        		
	        		if (currentColumn != c || currentRow != r)
	        		{
		        		if (world.grassPixmap[c][r].equals(Assets.grass_left))
		        			world.grassPixmap[c][r] = Assets.grass_right;
		        		else if (world.grassPixmap[c][r].equals(Assets.grass_right))
		        			world.grassPixmap[c][r] = Assets.grass_left;
		        		
		        		world.player.grassesWalked++;
	        			
		        		if(world.player.grassesWalked >= grassesUntilAttack)
	        				world.player.attacked = true;
	        			
		        		currentColumn = c;
		        		currentRow = r;
	        		}
	        	}
	        	else
	        		world.player.inGrass = false;
        		
	        }
        }
    }
    
    /** A method that pause and resume the game using user input */
    private void updatePaused(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {
                if(event.x > 80 && event.x <= 240) {
                    if(event.y > 100 && event.y <= 148) {
                        if(Settings.soundEnabled)
                            Assets.click.play(1);
                        gameState = GameState.Running;
                        return;
                    }                    
                    if(event.y > 148 && event.y < 196) {
                        if(Settings.soundEnabled)
                            Assets.click.play(1);
                        game.setScreen(new MainMenuScreen(game));                        
                        return;
                    }
                }
            }
        }
    }
    
    /** A game over screen which is using user input in order to restart the game */
    private void updateGameOver(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {
                if(event.x >= 128 && event.x <= 192 &&
                   event.y >= 200 && event.y <= 264) {
                	
                	gameState = GameState.GameOver;
                	
                    if(Settings.soundEnabled)
                        Assets.click.play(1);
                    
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
        }
    }

    @Override
    /** The main method responsible for drawing the graphics and the game and it handle all other methods that has to do with drawing various elements of the game */
    public void present(float deltaTime)
    {
    	
        Graphics g = game.getGraphics();
        
        g.clear(Color.BLACK);
        
        
        if(gameState == GameState.Running)
        {
        	g.drawPixmap(Assets.background, BGposX, BGposY);
        	//boss.drawSleeping(g, BGposX, BGposY);
            drawGrassAndPlayer();
            drawRunningUI();

            g.drawText(world.player.monsterName + "'s health", 10, 380, Color.WHITE, 15);
            g.drawText("points are "+ world.player.currentHealth+".", 10, 400, Color.WHITE, 15);
            g.drawText("Money: "+ world.player.money, 10, 425, Color.WHITE, 15);
            
            g.drawText("Visit home to rest", 190, 380, Color.WHITE, 15);
            g.drawText("your monster!", 220, 400, Color.WHITE, 15);
            
            //Debugging
            g.drawText("Player X: "+world.player.x, 10, 450, Color.GRAY, 15);
            g.drawText("Player Y: "+world.player.y, 10, 470, Color.GRAY, 15);
            
            g.drawText("Background X: "+BGposX, 180, 450, Color.GRAY, 15);
            g.drawText("Background Y: "+BGposY, 180, 470, Color.GRAY, 15);
            
        }
        else if (gameState == GameState.Fighting)
        {	
        	g.drawPixmap(Assets.fightingBG, 0, 0);
        	
        	newEnemy.Draw(g);
        	
        	g.drawPixmap(Assets.playerFight, world.player.fightX, world.player.fightY);
        	
        	g.drawText(world.player.monsterName, 230, 326, Color.BLACK, 10);
        	
        	if (fightFrame > 3)
        		g.drawPixmap(Assets.mainMonster, world.player.monsterX, world.player.monsterY);
        	
        	world.player.drawHealth(g);
        	
        	fightFrame += deltaTime;
        	
        	if (world.player.myTurn)
        	{
	        	if (fightFrame == 0)
	        	{
	        		world.player.fightX = 70;
	        		world.player.fightY = 237;
	        	}
	        	
	        	
	        	if (world.player.currentHealth <= 0 && newEnemy.hitAnimation)
				{
					g.drawText("You died!", 20, 430, Color.RED, 25);
				}
				else
				{
					if (fightFrame > 3)
						drawTextOnFight(g);
				}
	        	
	        	
	        	
	        	if (fightFrame > 2)
	        		world.player.fightX -= 1.5f;
	        	
	        	
	        	
	        	if (fightFrame > 1 && fightFrame < 3)
	        		g.drawText("You: Go "+world.player.monsterName+"!!!", 20, 430, Color.BLACK, 20);
	        	
	        	
        	}
        	else if (!world.player.myTurn && fightState != FightState.Kill)
        	{
        		g.drawText(newEnemy.monsterName + " is hitting you for ", 20, 420, Color.BLACK, 15);
        		g.drawText(newEnemy.hit + " health points.", 20, 440, Color.BLACK, 15);
        	}
        	
        	if (fightState == FightState.Kill)
        	{
            	if (newEnemy.currentHealth <= 0)
            	{
            		newEnemy.currentHealth = 0;
            		
            		g.drawText("CONGRATULATIONS", 20, 395, Color.BLACK, 18);
            		
            		g.drawText("You have killed " + newEnemy.monsterName + ",", 20, 420, Color.BLACK, 15);
            		
            		if (newEnemy.monsterName.equals("Flute Holder"))
            		{
            			g.drawText("and have received a Magic Flute!", 20, 440, Color.BLACK, 15);
            			world.player.magicFluteReceived = true;
            		}
            		else
                		g.drawText("and have received "+newEnemy.moneyYearn+" bucks!", 20, 440, Color.BLACK, 15);
            		
            		g.drawText("CONTINUE", 20, 470, Color.BLACK, 20);
            	}
        	}
        	
        	
        	
        }
        
        if(gameState == GameState.Paused)
            drawPausedUI();
        
        if(world.player.currentHealth <= 0)
        {
            drawGameOverUI(g);
        }
    }
    
    /** This method handles all the parts of the fight that has to do with the player. Moving from one option (attack, run , power, items) to another and handling the main character animation*/
    private void updatePlayerFight(List<TouchEvent> touchEvents)
    {
    	
    	int len = touchEvents.size();
    	
        for(int i = 0; i < len; i++) {
        	
            TouchEvent event = touchEvents.get(i);
            
            if(event.type == TouchEvent.TOUCH_DOWN) {
            	
            	
    	    	if (fightState == FightState.Ready && fightFrame > 3)
    	    	{
    	    		 if (event.x > 50 && event.x < 100 && event.y > 415 && event.y < 425)
    	    		 {
    	    			 fightState = FightState.Attack;
    	    			 world.player.monsterHitAnimation = true;
    	    			 newEnemy.currentHealth -= world.player.hit;
    	    		 }
    	    		 else if (event.x > 50 && event.x < 100 && event.y > 445 && event.y < 455)
    	    		 {
    	    			 fightState = FightState.Power;
    	    			 
    	    			 if (!world.player.usedPower)
    	    			 {
	    	    			 world.player.monsterHitAnimation = true;
	    	    			 newEnemy.currentHealth -= world.player.powerHit;

    	    			 }
    	    		 }
    	    		 else if (event.x > 180 && event.x < 230 && event.y > 415 && event.y < 425)
    	    		 {
    	    			 fightState = FightState.Items;
    	    		 }
    	    		 else if (event.x > 180 && event.x < 225 && event.y > 445 && event.y < 455)
    	    		 {
    	    			 fightState = FightState.Run;
    	    			 world.player.manageToRun = randomGEN.nextBoolean();
    	    		 }
    			}
    	    	else if (fightState == FightState.Items || fightState == FightState.Power)
    	    	{
    	    		if (event.x > 20 && event.x < 70 && event.y > 450 && event.y < 480)
   	    		 	{
    	    			fightState = FightState.Ready;
   	    		 	}
    	    	}
    	    	else if (fightState == FightState.Run)
    	    	{
    	    		
    	    		if (event.x > 20 && event.x < 120 && event.y > 450 && event.y < 480 && world.player.manageToRun)
   	    		 	{
    	    			fightState = FightState.Ready;
    	    			gameState = GameState.Running;
    	    			fightFrame = 0;
   	    		 	}
    	    		else if (event.x > 20 && event.x < 70 && event.y > 450 && event.y < 480 && !world.player.manageToRun)
   	    		 	{
    	    			fightState = FightState.Ready;
    	    			world.player.myTurn = false;
   	    		 	}
    	    	}
    	    	else if (fightState == FightState.Kill && newEnemy.currentHealth <= 0)
    	    	{
    	    		if (newEnemy.currentHealth <= 0 && (event.x > 20 && event.x < 120 && event.y > 450 && event.y < 470 && !world.player.manageToRun))
    	    		{
	    	    		fightState = FightState.Ready;
	    	    		gameState = GameState.Running;
	    	    		world.player.money += newEnemy.moneyYearn;
    	    		}

    	    	}
            	
            }
        }

        
    	if (fightState == FightState.Attack)//Do animation
    	{
    		
    		if (world.player.monsterHitAnimation && world.player.monsterX < 200)
    		{
    			world.player.monsterX += 4;
    			world.player.monsterY -= 4;
    			if (world.player.monsterX >= 200) world.player.monsterHitAnimation = false;
    		}
    		else if (!world.player.monsterHitAnimation && world.player.monsterX > world.player.monsterInitialX)
    		{
    			world.player.monsterX -= 6;
    			world.player.monsterY += 6;
    			if (world.player.monsterX <= world.player.monsterInitialX)
				{
					world.player.monsterX = world.player.monsterInitialX;
					world.player.monsterY = world.player.monsterInitialY;
					
					if (newEnemy.currentHealth <= 0)
			    		fightState = FightState.Kill;
		    		else
		    			fightState = FightState.Ready;
					
					world.player.myTurn = false;
				}
    		}
		}
    	else if (fightState == FightState.Power)
		{
    		if (!world.player.usedPower)
    		{
	    		if (world.player.monsterHitAnimation && world.player.powerX < 200)
	    		{
	    			world.player.powerX += 4;
	    			world.player.powerY -= 4;
	    			if (world.player.powerX >= 200) world.player.monsterHitAnimation = false;
	    		}
	    		else if (!world.player.monsterHitAnimation)
	    		{
					world.player.powerX = world.player.monsterInitialX;
					world.player.powerY = world.player.monsterInitialY;
					
					if (newEnemy.currentHealth <= 0)
			    		fightState = FightState.Kill;
		    		else
		    			fightState = FightState.Ready;
					
					world.player.usedPower = true;
					world.player.myTurn = false;
	    		}
    		}
		}
    	
    	
    	
    }
    
    /** This method is responsible drawing all the text that is happening on the fight screen */
    private void drawTextOnFight(Graphics g)
    {
    	
	    	if (fightState == FightState.Ready)
	    	{
				g.drawText("What do you want "+world.player.monsterName+" to do?", 10, 395, Color.BLACK, 18);
				
				g.drawText("Attack", 50, 425, Color.BLACK, 20);
				g.drawText("Power", 50, 455, Color.BLACK, 20);
				g.drawText("Items", 180, 425, Color.BLACK, 20);
				g.drawText("Run", 180, 455, Color.BLACK, 20);
	    	}
	    	else if (fightState == FightState.Power)
	    	{
	    		if (world.player.usedPower)
	    		{
	    			g.drawText("POWER", 20, 395, Color.BLACK, 18);
		    		
		    		g.drawText("You can use only one power attack", 20, 420, Color.BLACK, 18);
		    		g.drawText("on each fight.", 20, 435, Color.BLACK, 18);
		    		
		    		g.drawText("BACK", 20, 460, Color.BLACK, 20);
	    		}
	    		else
	    		{
	    			g.drawPixmap(Assets.power, world.player.powerX, world.player.powerY);
	    		}
	    	}
	    	else if (fightState == FightState.Items)
	    	{
	    		g.drawText("ITEMS", 20, 395, Color.BLACK, 18);
	    		
	    		g.drawText("No items in your backpack.", 20, 420, Color.BLACK, 18);
	    		
	    		g.drawText("BACK", 20, 460, Color.BLACK, 20);
	    		
	    	}
	    	else if (fightState == FightState.Run)
	    	{	
	    		g.drawText("RUN", 20, 395, Color.BLACK, 18);
	    		
	    		if (world.player.manageToRun)
	    			g.drawText("Run Away!", 20, 460, Color.BLACK, 20);
	    		else
	    		{
	    			g.drawText("You were unable to escape.", 20, 423, Color.BLACK, 18);
	    		
	    			g.drawText("BACK", 20, 460, Color.BLACK, 20);
	    		}
	    		
	    	}    	
    	
    }
    
    /** In this method the grass and the player is drawn. The reason that those are both in this method is due to the need when sometimes that grass texture needed to overlap the player's */
    private void drawGrassAndPlayer()
    {
    	Graphics g = game.getGraphics();
    	
    	for(int r = 0; r < world.grassRowNO; r++)
        {
	    	for(int c = 0; c < world.grassColumnNO; c++)
	    	{
	    		g.drawPixmap(world.grassPixmap[c][r], world.grassPosX[c][r], world.grassPosY[c][r]);
	    		
	    		world.player.draw(g, currentPlayerPixmap);
	    	}
        }
    	
    	for(int r = 0; r < world.grassRowNO; r++)
        {
	    	for(int c = 0; c < world.grassColumnNO; c++)
	    	{
	    		
	        	if (world.player.originX > world.grassPosX[c][r] && world.player.originX < world.grassPosX[c][r] + world.grassPixmap[c][r].getWidth() &&
        			world.player.originY > world.grassPosY[c][r] && world.player.originY < world.grassPosY[c][r] + world.grassPixmap[c][r].getHeight())
	        	{
	        		
	        		g.drawPixmap(world.grassPixmap[c][r], world.grassPosX[c][r], world.grassPosY[c][r]);
	        	}
	    	}
        }
    	
    }
    /** This is responsible for drawing the User Interface of the game */
    private void drawRunningUI() {
        Graphics g = game.getGraphics();

        g.drawPixmap(Assets.buttons, 0, 0, 64, 128, 64, 64);
        g.drawLine(0, 360, 480, 360, Color.BLACK);
        g.drawRect(0, 360, 480, 80, Color.BLACK);
        g.drawPixmap(Assets.arrowController, 110, 365);
    }
    
    private void drawPausedUI() {
        Graphics g = game.getGraphics();
        
        g.drawPixmap(Assets.pause, 80, 100);
        g.drawLine(0, 416, 480, 416, Color.BLACK);
    }

    private void drawGameOverUI(Graphics g) {
        
        g.drawPixmap(Assets.gameOver, 62, 100);
        g.drawPixmap(Assets.buttons, 128, 200, 0, 128, 64, 64);
    }
    
    @Override
    public void pause() {
        if(gameState == GameState.Running)
            gameState = GameState.Paused;
        
        if(world.gameOver) {
            Settings.addScore(world.score);
            Settings.save(game.getFileIO());
        }
    }

    @Override
    public void resume() {
        
    }

    @Override
    public void dispose() {
        
    }
}