package progark.a15.model;

/*
 * Enum containing all bonus types available.
 * Used by CollectableSprite for bonus values and PlayerSprite (for utilizing the bonus)
 * Format is: BONUS_NAME (magnitude_easy, magnitude_medium, magnitude_hard), for the three difficulty values.
 * Magnitude is a concrete number which relates to different things depending on the type of bonus.
 */
public enum BonusType {
	FUEL_ADD(5000, 3000, 1500),//10, 7, 5),
	FUEL_FILL(100, 75, 50),
	FUEL_DEC(-1500,-3000,-5000),
	BONUSPOINT_SMALL(100, 200, 300),
	BONUSPOINT_BIG(500, 700, 1000),
	BONUS_OCCURRENCE(3, 2, 1);
	
	private final int mag_e,mag_m,mag_h;
	private BonusType(int mag_e, int mag_m, int mag_h) {
		this.mag_e=mag_e;
		this.mag_m=mag_m;
		this.mag_h=mag_h;
	}
	public int getEasy() { return mag_e; }
	public int getMedium() { return mag_m; }
	public int getHard() { return mag_h; }

	public int getMagnitude(int difficulty) {
		if(difficulty==0) return getEasy();
		if(difficulty==1) return getMedium();
		if(difficulty==2) return getHard();
		else return 0;
	}
	
	
}
