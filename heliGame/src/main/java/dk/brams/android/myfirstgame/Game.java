package dk.brams.android.myfirstgame;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity {
    private static final String TAG = "Game";
    private static final String HIGHSCORE = "Record";
    private GamePanel panel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //turn title off
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // get last highscore from shared preferences
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        int currentHighScore = sharedPref.getInt(HIGHSCORE, 0);
        Log.i(TAG, "onCreate: read highscore from preferences = " + currentHighScore);

        panel = new GamePanel(this, currentHighScore);

        // set listener for handling new high score
        panel.setHighScoreListener(new GamePanel.HighScoreListener() {
            @Override
            public void onHighScoreUpdated(int best) {
                // code to handle updates
                Log.i(TAG, "onHighScoreUpdated: new best value = " + best);

                // Update shared preferences
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(HIGHSCORE, best);
                editor.commit();
            }

        });
        setContentView(panel);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        panel.releaseSoundPool();
    }
}
