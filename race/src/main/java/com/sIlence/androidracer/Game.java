package com.sIlence.androidracer;

public class Game {

	public LightRacer		local = null;
	public LightRacer		other = null;
	public WallRacer		wall1 = null;
	public WallRacer		wall2 = null;
	
	public int				otherDifficualty = -1;

	public int				kills = 0;
	public int				deaths = 0;
	public int				time = 0;

	public long				ticks = 0;

	public int				currentState = AndroidRacer.STATE_IN_GAME;
	
	public String			screenName = AndroidRacer.DEFALT_SCREEN_NAME;

	public void newOrientation(GameView v) {
		local.rotate(v, (v.top() / v.boxHeight()) * v.boxHeight());
		other.rotate(v, (v.top() / v.boxHeight()) * v.boxHeight());


		wall1 = new WallRacer(v, v.boxWidth(), v.top() + v.boxHeight());
		wall2 = new WallRacer(v, v.boxWidth() * ((v.getWidth() / v.boxWidth()) - 1), v.boxHeight() * ((v.getHeight() / v.boxHeight()) - 1));

		for (int i = 0; i < (84 + 134) * 2; i++) {
			wall1.preupdate();
			wall2.preupdate();
		}

		Part[] parts = new Part[] {other, local, wall1, wall2};

		local.setOpps(parts);
		other.setOpps(parts);
	}
}
