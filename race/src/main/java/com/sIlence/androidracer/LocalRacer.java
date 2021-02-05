/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sIlence.androidracer;

import java.io.PrintStream;

/**
 *
 * @author Mytchel
 */
public class LocalRacer extends LightRacer {

    PrintStream out;

    public LocalRacer(GameView v, PrintStream o) {
        super(v, 0xC003CCF1, GameView.INCREASE_DEATHS);

		id = "LightRacer-LocalMulti";
		
        out = o;
    }

    @Override
    public void update() {
		updateSound();
        updateExplosions();
        
		if (dieing > 0) {
			dieing++;

			if (!foundSpawn) {
				findSpawn();
			}

            if (dieing == 30) {
				view.stopSound(cycleStreamId);
				cycleStreamId = 0;

				view.checkScore();

				if (!foundSpawn) {
					spawn(view.game.kills * view.game.kills + STANDARD_LENGTH);
				} else {
					spawnSpec(linex[0], liney[0], safestDirection(), view.game.kills * view.game.kills + STANDARD_LENGTH);
				}
					
		        out.println(MultiplayerGameView.CODE_SPAWNING);
			    out.println(linex[0] / view.boxWidth());
				out.println(liney[0] / view.boxHeight());
				out.println(linex.length);
				out.flush();
					
				dieing = 0;
				foundSpawn = true;
				return;
            }
			
			
			out.println(MultiplayerGameView.CODE_DO_NOTHING);
			out.flush();
			
            return;
        }

        move();
        offScreen();
		
        out.println(MultiplayerGameView.CODE_UPDATING);
        out.println(linex[0] / view.boxWidth());
        out.println(liney[0] / view.boxHeight());
        out.flush();

        if (view.game.kills * view.game.kills + STANDARD_LENGTH != linex.length) {
            if (view.game.kills > 0) {
                int[] tempx = linex.clone();
                int[] tempy = liney.clone();

                linex = new int[view.game.kills * view.game.kills + STANDARD_LENGTH];
                liney = new int[view.game.kills * view.game.kills + STANDARD_LENGTH];

                int length = (linex.length > tempx.length) ? tempx.length : linex.length;

                System.arraycopy(tempx, 0, linex, 0, length);
                System.arraycopy(tempy, 0, liney, 0, length);
            }
        }

        updateLine();
        checkCollisions();
    }

	@Override
	public void die() {
		super.die();
		
		out.println(MultiplayerGameView.CODE_DIE);
		out.println(direction);
		out.flush();
	}

}
