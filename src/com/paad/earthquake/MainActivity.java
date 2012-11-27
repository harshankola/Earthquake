package com.paad.earthquake;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

	static final private int MENU_PREFERENCES = Menu.FIRST + 1;
	static final private int MENU_UPDATE = Menu.FIRST + 2;
	private static final int SHOW_PREFERENCES = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		updateFromPreferences();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_PREFERENCES, Menu.NONE, R.string.menu_preferences);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case (MENU_PREFERENCES): {
			Class c = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? PreferencesActivity.class
					: FragmentPreferences.class;
			Intent i = new Intent(this, c);
			startActivityForResult(i, SHOW_PREFERENCES);
			return true;
		}
		}
		return false;
	}

	public static int minimumMagnitude = 0;
	public static boolean autoUpdateChecked = false;
	public static int updateFreq = 0;

	private void updateFromPreferences() {
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		minimumMagnitude = Integer.parseInt(prefs.getString(
				PreferencesActivity.PREF_MIN_MAG, "3"));
		updateFreq = Integer.parseInt(prefs.getString(
				PreferencesActivity.PREF_UPDATE_FREQ, "60"));

		autoUpdateChecked = prefs.getBoolean(
				PreferencesActivity.PREF_AUTO_UPDATE, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SHOW_PREFERENCES) {
			updateFromPreferences();

			FragmentManager fm = getSupportFragmentManager();
			final EarthquakeListFragment earthquakeList = (EarthquakeListFragment) fm
					.findFragmentById(R.id.EarthquakeListFragment);
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					earthquakeList.refreshEarthquakes();
				}
			});
			t.start();
		}
	}
}
