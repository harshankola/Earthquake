/**
 * 
 */
package com.paad.earthquake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Harrsh
 * 
 */
public class EarthquakeAlarmReceiver extends BroadcastReceiver {

	public static final String ACTION_REFRESH_EARTHQUAKE_ALARM = "com.paad.earthquake.ACTION_REFRESH_EARTHQUAKE_ALARM";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Intent startIntent = new Intent(context, EarthquakeUpdateService.class);
		context.startService(startIntent);
	}

}
