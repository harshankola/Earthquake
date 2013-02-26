/**
 * 
 */
package com.paad.earthquake;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Harrsh
 * 
 */
public class EarthquakeUpdateService extends IntentService {

	public EarthquakeUpdateService() {
		super("EarthquakeUpdateService");
		// TODO Auto-generated constructor stub
	}

	public EarthquakeUpdateService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void refreshEarthquakes() {
		// Get the XML
		URL url;

		try {
			String quakeFeed = getString(R.string.quake_feed);
			url = new URL(quakeFeed);

			URLConnection connection;
			connection = url.openConnection();

			HttpURLConnection httpConnection = (HttpURLConnection) connection;
			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConnection.getInputStream();
				DocumentBuilderFactory dbf = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();

				// Parse the earthquake feed.
				Document dom = db.parse(in);
				Element docEle = dom.getDocumentElement();

				// Clear the old earthquakes
				// earthquakes.clear();

				// Get a list of each earthquake entry.
				NodeList nl = docEle.getElementsByTagName("entry");
				if (nl != null && nl.getLength() > 0) {
					for (int i = 0; i < nl.getLength(); i++) {
						Element entry = (Element) nl.item(i);
						Element title = (Element) entry.getElementsByTagName(
								"title").item(0);
						Element g = (Element) entry.getElementsByTagName(
								"georss:point").item(0);
						Element when = (Element) entry.getElementsByTagName(
								"updated").item(0);
						Element link = (Element) entry.getElementsByTagName(
								"link").item(0);

						String details = title.getFirstChild().getNodeValue();
						String hostname = "http://earthquake.usgs.gov";
						String linkString = hostname
								+ link.getAttribute("href");

						String point = g.getFirstChild().getNodeValue();
						String dt = when.getFirstChild().getNodeValue();
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd'T'hh:mm:ss'Z'");
						Date qdate = new GregorianCalendar(0, 0, 0).getTime();
						try {
							qdate = sdf.parse(dt);
						} catch (ParseException e) {
							Log.d(TAG, "Data parsing exception.", e);
						}

						String[] location = point.split(" ");
						Location l = new Location("dummyGPS");
						l.setLatitude(Double.parseDouble(location[0]));
						l.setLongitude(Double.parseDouble(location[1]));

						String magnitudeString = details.split(" ")[1];
						int end = magnitudeString.length() - 1;
						double magnitude = Double.parseDouble(magnitudeString
								.substring(0, end));

						details = details.split(",")[1].trim();

						final Quake quake = new Quake(qdate, details, l,
								magnitude, linkString);

						// Process a newly found earthquake
						addNewQuake(quake);
					}
				}
			}
		} catch (MalformedURLException e) {
			Log.d(TAG, "MalformedURLException");
		} catch (IOException e) {
			// TODO: handle exception
			Log.d(TAG, "IOException");
		} catch (ParserConfigurationException e) {
			// TODO: handle exception
			Log.d(TAG, "Parser Configuration Exception");
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "SAX Exception");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Log.d(TAG, "getting to finally");
		}
	}

	private void addNewQuake(Quake _quake) {
		// TODO Add the earthquakes to the array list.
		ContentResolver cr = getContentResolver();

		// Construct a where clause to make sure we don't already have this
		// earthquake in the provider.
		String w = EarthquakeProvider.KEY_DATE + " = "
				+ _quake.getDate().getTime();

		// If the earthquake is new, insert it into the provider.
		Cursor query = cr.query(EarthquakeProvider.CONTENT_URI, null, w, null,
				null);
		if (query.getCount() == 0) {
			ContentValues values = new ContentValues();

			values.put(EarthquakeProvider.KEY_DATE, _quake.getDate().getTime());
			values.put(EarthquakeProvider.KEY_DETAILS, _quake.getDetails());
			values.put(EarthquakeProvider.KEY_SUMMARY, _quake.toString());

			double lat = _quake.getLocation().getLatitude();
			double lng = _quake.getLocation().getLongitude();
			values.put(EarthquakeProvider.KEY_LOCATION_LAT, lat);
			values.put(EarthquakeProvider.KEY_LOCATION_LNG, lng);
			values.put(EarthquakeProvider.KEY_LINK, _quake.getLink());
			values.put(EarthquakeProvider.KEY_MAGNITUDE, _quake.getMagnitude());

			// Trigger a notification
			broadcastNotification(_quake);

			// Add the new quake to the Earthquake provider.
			cr.insert(EarthquakeProvider.CONTENT_URI, values);
		}
		query.close();
	}

	private void broadcastNotification(Quake _quake) {
		// TODO Auto-generated method stub
		Intent startActivityIntenet = new Intent(this, Earthquake.class);
		PendingIntent launchIntent = PendingIntent.getActivity(this, 0,
				startActivityIntenet, 0);
		earthquakeNotificationBuilder.setContentIntent(launchIntent)
				.setWhen(_quake.getDate().getTime())
				.setContentTitle("M: " + _quake.getMagnitude())
				.setContentText(_quake.getDetails());

		if (_quake.getMagnitude() > 6) {
			Uri ringURI = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

			earthquakeNotificationBuilder.setSound(ringURI);
		}

		double vibrateLength = 100 * Math.exp(0.53 * _quake.getMagnitude());
		long[] vibrate = new long[] { 100, 100, (long) vibrateLength };
		earthquakeNotificationBuilder.setVibrate(vibrate);

		int color;
		if (_quake.getMagnitude() < 5.4)
			color = Color.GREEN;
		else if (_quake.getMagnitude() < 6)
			color = Color.YELLOW;
		else
			color = Color.RED;

		earthquakeNotificationBuilder.setLights(color, (int) vibrateLength,
				(int) vibrateLength);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.notify(NOTIFICATION_ID,
				earthquakeNotificationBuilder.getNotification());

	}

	public static String TAG = "EARTHQUAKE_UPDATE_SERVICE";

	public static final int NOTIFICATION_ID = 1;

	private AlarmManager alarmManager;

	private PendingIntent alarmIntent;

	private Builder earthquakeNotificationBuilder;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		String ALARM_ACTION = EarthquakeAlarmReceiver.ACTION_REFRESH_EARTHQUAKE_ALARM;
		Intent intentToFire = new Intent(ALARM_ACTION);
		alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);

		earthquakeNotificationBuilder = new Notification.Builder(this);
		earthquakeNotificationBuilder.setAutoCancel(true)
				.setTicker("Earthquake detected")
				.setSmallIcon(R.drawable.notification_icon);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		// Retrieve the shared preferences
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		int updateFreq = Integer.parseInt(prefs.getString(
				PreferencesActivity.PREF_UPDATE_FREQ, "60"));
		boolean autoUpdateChecked = prefs.getBoolean(
				PreferencesActivity.PREF_AUTO_UPDATE, false);

		if (autoUpdateChecked) {
			int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
			long timeToRefresh = SystemClock.elapsedRealtime() + updateFreq
					* 60 * 1000;
			alarmManager.setInexactRepeating(alarmType, timeToRefresh,
					updateFreq * 60 * 1000, alarmIntent);
		} else
			alarmManager.cancel(alarmIntent);

		refreshEarthquakes();
	}

}
