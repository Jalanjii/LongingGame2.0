package progark.a15;

public class HighScore implements Comparable<HighScore> {
	private String name;
	private int score;
	public HighScore(String name,int score) {
		this.name=name;
		this.score=score;
	}
	public HighScore(String rawData) {
		String[] split = rawData.split("§§");
		name=split[0];
		score=Integer.parseInt(split[1]);
	}
	
	public String getName() { return name; }
	public int getScore() { return score; }
	
	public int compareTo(HighScore arg0) {
		return (int)Math.signum(arg0.getScore()-this.score);
	}
	public String toString() { return name+": "+score; }
}
