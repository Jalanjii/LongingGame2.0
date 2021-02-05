/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sIlence.androidracer;

import java.io.BufferedReader;

/**
 *
 * @author Mytchel
 */
public class WebRacer extends LightRacer {

    BufferedReader		in;

    public WebRacer(GameView v, BufferedReader i) {
    	super(v, 0xC0FFE64D, GameView.INCREASE_KILLS);
				
		id = "LightRacer-WebMulti";
		
		direction = -1;
		in = i;
    }

    public void update(String input) {
		updateSound();
		updateExplosions();
		
		
    	try {
			if (input.equals(MultiplayerGameView.CODE_DIE)) {
				input = in.readLine();
				direction = Integer.parseInt(input);
				
				die();
			} else if (input.equals(MultiplayerGameView.CODE_SPAWNING)) {
				view.checkScore();
				
				input = in.readLine();
				int x = Integer.parseInt(input);

				input = in.readLine();
				int y = Integer.parseInt(input);

				input = in.readLine();
				int length = Integer.parseInt(input);

				spawnSpec(x * view.boxWidth(), y * view.boxHeight(), length);
				
			} else if (input.equals(MultiplayerGameView.CODE_UPDATING)) {
				input = in.readLine();
				int x = Integer.parseInt(input);

				input = in.readLine();
				int y = Integer.parseInt(input);

				linex[0] = x * view.boxWidth();
				liney[0] = y * view.boxHeight();
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		updateLine();
   }

    public void spawnSpec(int x, int y, int length) {
		dieing = 0;
		
		linex = new int[length];
		liney = new int[length];

		lastTurn = 0;
		color = startColor;

		linex[0] = x;
		liney[0] = y;
   }

   @Override
   public void updateLine() {
		for (ri = linex.length - 1; ri > 0; ri--) {
		    linex[ri] = linex[ri - 1];
		    liney[ri] = liney[ri - 1];
		}
   }
}
