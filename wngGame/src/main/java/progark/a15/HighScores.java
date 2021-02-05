package progark.a15;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class HighScores {
	private String fileName = "JetWangScores.dat";
	private Context context;
	
	public HighScores(Context context) {
		this.context=context;
	}
	
	public void writeScore(String name,int score) {
		try {
			FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
			fos.write((name+"§§"+String.valueOf(score)+"\n").getBytes());
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public List<HighScore> getTopScores(int num) {
		ArrayList<HighScore> scores = new ArrayList<HighScore>();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(context.openFileInput(fileName)));
			String line = r.readLine();
			while(line!=null) {
				scores.add(new HighScore(line));
				line = r.readLine();
			}
			r.close();
			Collections.sort(scores);
			num = Math.min(num, scores.size());
			return scores.subList(0, num);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scores;
	}
}
