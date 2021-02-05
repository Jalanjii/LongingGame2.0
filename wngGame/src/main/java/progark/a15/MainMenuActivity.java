package progark.a15;

import java.util.List;

import progark.a15.model.SpriteFactory;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainMenuActivity extends Activity {
	
	static final int NUMBER_OF_HIGH_SCORES = 5;
	
	HighScores hs;
	List<HighScore> hsList;
	ArrayAdapter<HighScore> hsAdapter;
	RelativeLayout root;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);
		//Give spriteFactory resources
		SpriteFactory.getInstance().setResources(this.getResources());
		calculateScreenSize();
		makeHSList();
		Button startButton = (Button) findViewById(R.id.new_game_button);

		startButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Open the difficulty selection screen
				Intent intent = new Intent(MainMenuActivity.this, DiffSelectActivity.class);
				MainMenuActivity.this.startActivity(intent);
			}
		});

	}	
	
	@Override
	public void onRestart() {
		super.onRestart();
		hsList.clear(); // FIXME? Not sure if clear() and addAll() is the best (cleanest) way to make the refreshing of the highscores work, but it does the trick.
		hsList.addAll(hs.getTopScores(NUMBER_OF_HIGH_SCORES));
		hsAdapter.notifyDataSetChanged();
	}
	
	private void calculateScreenSize() {
		// Get the layout id
		root = (RelativeLayout) findViewById(R.id.mainmenu);
		root.post(new Runnable() { 
			public void run() { 
				Rect rect = new Rect(); 
				Window win = getWindow();  // Get the Window
				win.getDecorView().getWindowVisibleDisplayFrame(rect); 
				// Get the height of Status Bar 
				int statusBarHeight = rect.top; 
				// Get the height occupied by the decoration contents 
				int contentViewTop = win.findViewById(Window.ID_ANDROID_CONTENT).getTop(); 
				// Calculate titleBarHeight by deducting statusBarHeight from contentViewTop  
				int titleBarHeight = contentViewTop - statusBarHeight; 
				Log.i("MY", "titleHeight = " + titleBarHeight + " statusHeight = " + statusBarHeight + " contentViewTop = " + contentViewTop); 

				// By now we got the height of titleBar & statusBar
				// Now lets get the screen size
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);   
				int screenHeight = metrics.heightPixels;
				int screenWidth = metrics.widthPixels;
				Log.i("MY", "Actual Screen Height = " + screenHeight + " Width = " + screenWidth);   
				// Now calculate the height that our layout can be set
				// If you know that your application doesn't have statusBar added, then don't add here also. Same applies to application bar also 
				int layoutHeight = screenHeight - (titleBarHeight + statusBarHeight);
				Log.i("MY", "Layout Height = " + layoutHeight);  

				//Initialize SpriteFactory!

				SpriteFactory.getInstance().setScalation(R.drawable.backgroundplain,
						screenWidth,
						layoutHeight);
			} 
		}); 
	}
	private void makeHSList() {
		ListView scores = (ListView) this.findViewById(R.id.highScoreList);
		scores.setFocusable(false);
		hs = new HighScores(this);
		hsList = hs.getTopScores(NUMBER_OF_HIGH_SCORES);
		hsAdapter = new ArrayAdapter<HighScore>(this, R.layout.highscore_list_item, hsList) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;
				LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				if (null == convertView) {
					row = mInflater.inflate(R.layout.highscore_list_item, null);
				} else {
					row = convertView;
				}

				TextView textName = (TextView) row.findViewById(R.id.score_name);
				textName.setText(this.getItem(position).getName());
				TextView textScore = (TextView) row.findViewById(R.id.score_value);
				textScore.setText(this.getItem(position).getScore()+"");
				textName.setTextColor(Color.BLACK);
				textScore.setTextColor(Color.BLACK);
				return row;
			}
		};
		scores.setAdapter(hsAdapter);
	}


}
