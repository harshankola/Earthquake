/**
 * 
 */
package com.paad.earthquake;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author Harrsh
 * 
 */
public class EarthquakeProvider extends ContentProvider {

	public static final Uri CONTENT_URI = Uri
			.parse("content://com.paad.earthquakeprovider/earthquakes");

	// Column Names
	public static final String KEY_ID = "_id";
	public static final String KEY_DATE = "date";
	public static final String KEY_DETAILS = "details";
	public static final String KEY_SUMMARY = "summary";
	public static final String KEY_LOCATION_LAT = "latitude";
	public static final String KEY_LOCATION_LNG = "longitude";
	public static final String KEY_MAGNITUDE = "magnitude";
	public static final String KEY_LINK = "link";

	private EarthquakeDatabaseHelper dbHelper;

	// Create the constants used to differentiate between the different URI
	// requests.
	private static final int QUAKES = 1;
	private static final int QUAKE_ID = 2;

	private static final UriMatcher uriMatcher;

	// Allocate the UriMatcher object, where a URI ending in 'earthquakes' will
	// correspond to a request for all earthquakes, and 'earthquakes' with a
	// trailing '/[rowID]' will represent a single earthquake row.
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.paad.earthquakeprovider", "earthquakes", QUAKES);
		uriMatcher.addURI("com.paad.earthquakeprovider", "earthquakes/#",
				QUAKE_ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase database = dbHelper.getWritableDatabase();

		int count;
		switch (uriMatcher.match(uri)) {
		case QUAKES:
			count = database.delete(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE,
					where, whereArgs);
			break;
		case QUAKE_ID:
			String segment = uri.getPathSegments().get(1);
			count = database.delete(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE,
					KEY_ID
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ") " : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (uriMatcher.match(uri)) {
		case QUAKES:
			return "vnd.android.cursor.dir/vnd.paad.earthquake";
		case QUAKE_ID:
			return "vnd.android.cursor.item/vnd.paad.earthquake";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#insert(android.net.Uri,
	 * android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		SQLiteDatabase database = dbHelper.getWritableDatabase();

		// Insert the new row. The call to database.insert will return the row
		// number
		// if it is successful.

		long rowID = database.insert(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE,
				"quake", values);

		// Return a URI to the newly inserted row on success.
		if (rowID > 0) {
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Context context = getContext();

		dbHelper = new EarthquakeDatabaseHelper(context,
				EarthquakeDatabaseHelper.DATABASE_NAME, null,
				EarthquakeDatabaseHelper.DATABASE_VERSION);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri,
	 * java.lang.String[], java.lang.String, java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteDatabase database = dbHelper.getWritableDatabase();

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		qb.setTables(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE);

		// If this is a row query, limit the result set to the passed in row.
		switch (uriMatcher.match(uri)) {
		case QUAKE_ID:
			qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
			break;
		default:
			break;
		}

		// If no sort order is specified, sort by date / time
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = KEY_DATE;
		} else {
			orderBy = sortOrder;
		}

		// Apply the query to the underlying database.
		Cursor c = qb.query(database, projection, selection, selectionArgs,
				null, null, orderBy);

		// Register the contexts ContentResolver to be notified if
		// the cursor result set changes.
		c.setNotificationUri(getContext().getContentResolver(), uri);

		// Return a cursor to the query result.
		return c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#update(android.net.Uri,
	 * android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		SQLiteDatabase database = dbHelper.getWritableDatabase();

		int count;
		switch (uriMatcher.match(uri)) {
		case QUAKES:
			count = database.update(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE,
					values, selection, selectionArgs);
			break;
		case QUAKE_ID:
			String segment = uri.getPathSegments().get(1);
			count = database.update(EarthquakeDatabaseHelper.EARTHQUAKE_TABLE,
					values,
					KEY_ID
							+ "="
							+ segment
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ") " : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/**
	 * @author Harrsh
	 * 
	 */
	// Helper class for opening, creating, and managing database version control
	private static class EarthquakeDatabaseHelper extends SQLiteOpenHelper {

		private static final String TAG = "EarthquakeProvider";

		private static final String DATABASE_NAME = "earthquakes.db";
		private static final int DATABASE_VERSION = 1;
		private static final String EARTHQUAKE_TABLE = "earthquakes";

		private static final String DATABASE_CREATE = "create table "
				+ EARTHQUAKE_TABLE + " (" + KEY_ID
				+ " integer primary key autoincrement, " + KEY_DATE
				+ " INTEGER, " + KEY_DETAILS + " TEXT, " + KEY_SUMMARY
				+ " TEXT, " + KEY_LOCATION_LAT + " FLOAT, " + KEY_LOCATION_LNG
				+ " FLOAT, " + KEY_MAGNITUDE + " FLOAT, " + KEY_LINK
				+ " TEXT);";

		// The underlying database
		private SQLiteDatabase earthquakeDB;

		public EarthquakeDatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database
		 * .sqlite.SQLiteDatabase)
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DATABASE_CREATE);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database
		 * .sqlite.SQLiteDatabase, int, int)
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data.");

			db.execSQL("DROP TABLE IF EXISTS " + EARTHQUAKE_TABLE);
			onCreate(db);
		}

	}
}
