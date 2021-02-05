/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sIlence.androidracer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.WindowManager;

/**
 *
 * @author Mytchel
 */
public class SettingsPreferences extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onPause() {
	CheckBoxPreference music = (CheckBoxPreference) findPreference("enable_music");
	CheckBoxPreference effects = (CheckBoxPreference) findPreference("sound_effects");
	CheckBoxPreference vibrate = (CheckBoxPreference) findPreference("vibrate_allowed");
	// save preferences

	SharedPreferences settings = getSharedPreferences("AndroidRacerSettings", 0);
	SharedPreferences.Editor editor = settings.edit();
	editor.putBoolean("background", music.isChecked());
	editor.putBoolean("effects", effects.isChecked());
	editor.putBoolean("vibrate", vibrate.isChecked());

	editor.commit();
	super.onPause();
    }

    @Override
    public void onStart() {
    	getPreferenceManager().setSharedPreferencesName("settings");
	addPreferencesFromResource(R.menu.settings);

	CheckBoxPreference music = (CheckBoxPreference) findPreference("enable_music");
	CheckBoxPreference effects = (CheckBoxPreference) findPreference("sound_effects");
	CheckBoxPreference vibrate = (CheckBoxPreference) findPreference("vibrate_allowed");
	Preference clear = (Preference) findPreference("clear_high_scores");

	SharedPreferences settings = getSharedPreferences("AndroidRacerSettings", 0);

	music.setChecked(settings.getBoolean("background", true));
	effects.setChecked(settings.getBoolean("effects", true));
	vibrate.setChecked(settings.getBoolean("vibrate", true));

	final Context c = this;
	clear.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

	    public boolean onPreferenceClick(Preference arg0) {
		new AlertDialog.Builder(c)
		.setTitle("Clear Scores")
		.setMessage("Are You Sure You Want To Clear Your High Scores?")
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

		    public void onClick(DialogInterface dialog, int whichButton) {
			Database db = new Database(c);
			db.clear();
		    }
		})
		.setNegativeButton(android.R.string.no, null).show();
		return true;
	    }
	});

	super.onStart();
    }
}
