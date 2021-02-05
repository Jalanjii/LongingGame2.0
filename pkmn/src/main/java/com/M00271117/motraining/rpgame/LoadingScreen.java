package com.M00271117.motraining.rpgame;

import com.M00271117.motraining.framework.Game;
import com.M00271117.motraining.framework.Graphics;
import com.M00271117.motraining.framework.Screen;
import com.M00271117.motraining.framework.Graphics.PixmapFormat;

/** This class is used right before the main menu screen to load all the pictures and sounds in order the game to work  */
public class LoadingScreen extends Screen {
    public LoadingScreen(Game game) {
        super(game);
    }

    @Override
    public void update(float deltaTime) {
        
    	Graphics g = game.getGraphics();
        
        Assets.grass_left = g.newPixmap("world/grassLeft.png", PixmapFormat.ARGB4444);
        Assets.grass_right = g.newPixmap("world/grassRight.png", PixmapFormat.ARGB4444);
        Assets.background = g.newPixmap("backgrounds/world.png", PixmapFormat.ARGB4444);
        Assets.fightingBG = g.newPixmap("backgrounds/fightingBG.png", PixmapFormat.ARGB4444);
        Assets.title = g.newPixmap("title.png", PixmapFormat.ARGB4444);
        Assets.mainMenu = g.newPixmap("mainmenu.png", PixmapFormat.ARGB4444);
        Assets.arrowController = g.newPixmap("UI/ArrowSet.png", PixmapFormat.ARGB4444);
        Assets.buttons = g.newPixmap("buttons.png", PixmapFormat.ARGB4444);
        Assets.help1 = g.newPixmap("help1.png", PixmapFormat.ARGB4444);
        Assets.help2 = g.newPixmap("help2.png", PixmapFormat.ARGB4444);
        Assets.help3 = g.newPixmap("help3.png", PixmapFormat.ARGB4444);
        Assets.numbers = g.newPixmap("numbers.png", PixmapFormat.ARGB4444);
        Assets.ready = g.newPixmap("ready.png", PixmapFormat.ARGB4444);
        Assets.pause = g.newPixmap("pausemenu.png", PixmapFormat.ARGB4444);
        Assets.gameOver = g.newPixmap("gameover.png", PixmapFormat.ARGB4444);
        Assets.playerFight = g.newPixmap("characters/main/playerFighting.png", PixmapFormat.ARGB4444);
        Assets.headUp[0] = g.newPixmap("characters/main/headUp0.png", PixmapFormat.ARGB4444);
        Assets.headUp[1] = g.newPixmap("characters/AnimationUp/1.png", PixmapFormat.ARGB4444);
        Assets.headUp[2] = g.newPixmap("characters/AnimationUp/2.png", PixmapFormat.ARGB4444);
        Assets.headLeft[0] = g.newPixmap("characters/main/headLeft0.png", PixmapFormat.ARGB4444);
        Assets.headLeft[1] = g.newPixmap("characters/AnimationLeft/1.png", PixmapFormat.ARGB4444);
        Assets.headLeft[2] = g.newPixmap("characters/AnimationLeft/2.png", PixmapFormat.ARGB4444);
        Assets.headDown[0] = g.newPixmap("characters/main/headDown0.png", PixmapFormat.ARGB4444);
        Assets.headDown[1] = g.newPixmap("characters/AnimationDown/1.png", PixmapFormat.ARGB4444);
        Assets.headDown[2] = g.newPixmap("characters/AnimationDown/2.png", PixmapFormat.ARGB4444);
        Assets.headRight[0] = g.newPixmap("characters/main/headRight0.png", PixmapFormat.ARGB4444);
        Assets.headRight[1] = g.newPixmap("characters/AnimationRight/1.png", PixmapFormat.ARGB4444);
        Assets.headRight[2] = g.newPixmap("characters/AnimationRight/2.png", PixmapFormat.ARGB4444);
        Assets.monsters[0] = g.newPixmap("monsters/fire.png", PixmapFormat.ARGB4444);
        Assets.monsters[1] = g.newPixmap("monsters/grass.png", PixmapFormat.ARGB4444);
        Assets.monsters[2] = g.newPixmap("monsters/key.png", PixmapFormat.ARGB4444);
        Assets.monsters[3] = g.newPixmap("monsters/water.png", PixmapFormat.ARGB4444);
        Assets.monsters[4] = g.newPixmap("monsters/boss.png", PixmapFormat.ARGB4444);
        Assets.mainMonster = g.newPixmap("monsters/main.png", PixmapFormat.ARGB4444);
        Assets.sleepingBoss = g.newPixmap("monsters/sleep.png", PixmapFormat.ARGB4444);
        Assets.power = g.newPixmap("monsters/power.png", PixmapFormat.ARGB4444);
        Assets.tail = g.newPixmap("tail.png", PixmapFormat.ARGB4444);
        Assets.stain1 = g.newPixmap("stain1.png", PixmapFormat.ARGB4444);
        Assets.stain2 = g.newPixmap("stain2.png", PixmapFormat.ARGB4444);
        Assets.stain3 = g.newPixmap("stain3.png", PixmapFormat.ARGB4444);
        Assets.click = game.getAudio().newSound("sounds/click.mp3");
        Assets.eat = game.getAudio().newSound("sounds/eat.mp3");
        Assets.bitten = game.getAudio().newSound("sounds/bitten.mp3");
        Settings.load(game.getFileIO());
        game.setScreen(new MainMenuScreen(game));
    }

    @Override
    public void present(float deltaTime) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}