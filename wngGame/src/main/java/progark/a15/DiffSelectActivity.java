package progark.a15;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class DiffSelectActivity extends Activity {
	private RadioGroup diffRadioGroup;
	private RadioGroup charRadioGroup;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diffselect);
        
        diffRadioGroup = (RadioGroup) findViewById(R.id.diff_radiogroup);
        charRadioGroup = (RadioGroup) findViewById(R.id.char_radiogroup);
        
        Button startButton = (Button) findViewById(R.id.start_button);
        
        startButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// Check if both difficulty and character are selected
				int difficulty = -1;
				int playerType = -1;

				int checkedRadioButtonId = diffRadioGroup.getCheckedRadioButtonId();
				if (checkedRadioButtonId == R.id.easy_radio) {
					difficulty = 0;
				} else if (checkedRadioButtonId == R.id.medium_radio) {
					difficulty = 1;
				} else if (checkedRadioButtonId == R.id.hard_radio) {
					difficulty = 2;
				}

				int radioButtonId = charRadioGroup.getCheckedRadioButtonId();
				if (radioButtonId == R.id.first_char_radio) {
					playerType = 0;
				} else if (radioButtonId == R.id.second_char_radio) {
					playerType = 1;
				} else {
					playerType = -1;
				}
				if (difficulty != -1 && playerType != -1) {
					
					Intent intent = new Intent(DiffSelectActivity.this, GameActivity_getwang.class);
					
					intent.putExtra("difficulty", difficulty);
					intent.putExtra("playerType", playerType);
					
					DiffSelectActivity.this.startActivity(intent);
					
				} else {
					// Tell the user to choose difficulty and character
					Toast.makeText(getApplicationContext(), "You must choose a difficulty and a character.", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
    }
}