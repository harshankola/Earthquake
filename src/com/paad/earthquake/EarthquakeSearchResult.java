/**
 * 
 */
package com.paad.earthquake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * @author Harrsh
 *
 */
public class EarthquakeSearchResult extends FragmentActivity {

	private EarthquakeSearchResultsFragment earthquakeSearchFragment;

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		FragmentManager fm = getSupportFragmentManager();
		earthquakeSearchFragment=(EarthquakeSearchResultsFragment) fm.findFragmentById(R.id.EarthquakeSerchResultsFragment);
		earthquakeSearchFragment.parseIntent(getIntent());
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		earthquakeSearchFragment.parseIntent(getIntent());
	}

}
