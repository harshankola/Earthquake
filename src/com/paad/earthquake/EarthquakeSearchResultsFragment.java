package com.paad.earthquake;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

public class EarthquakeSearchResultsFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private SimpleCursorAdapter adapter;
	private String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// Create a new adapter and bind it to the List View
		adapter = new SimpleCursorAdapter(
				getActivity().getApplicationContext(),
				android.R.layout.simple_list_item_1, null,
				new String[] { EarthquakeProvider.KEY_SUMMARY },
				new int[] { android.R.id.text1 }, 0);
		setListAdapter(adapter);

		// Initiate the Cursor Loader
		getLoaderManager().initLoader(0, null, this);

		// Get the launch Intent
		parseIntent(getActivity().getIntent());
	}

	void parseIntent(Intent intent) {
		// TODO Auto-generated method stub
		// If the Activity was started to service a Search request,
		// extract the search query.
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String searchQuery = intent.getStringExtra(SearchManager.QUERY);

			// Perform the search, passing in the search query as an argument
			// to the Cursor Loader.
			Bundle args = new Bundle();
			args.putString(QUERY_EXTRA_KEY, searchQuery);

			// Restart the Cursor Loader to execute the new query.
			getLoaderManager().restartLoader(0, args, this);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		String query = "0";

		if (args != null) {
			// Extract the search query from the arguments.
			query = args.getString(QUERY_EXTRA_KEY);
		}

		// COnstruct the new query in the form of a Cursor Loader.
		String[] projection = { EarthquakeProvider.KEY_ID,
				EarthquakeProvider.KEY_SUMMARY };
		String where = EarthquakeProvider.KEY_SUMMARY + " LIKE\"%" + query
				+ "%\"";
		String[] whereArgs = null;
		String sortOrder = EarthquakeProvider.KEY_SUMMARY
				+ " COLLATE LOCALIZED ASC";

		// Create the new cursor loader.
		return new CursorLoader(getActivity(), EarthquakeProvider.CONTENT_URI,
				projection, where, whereArgs, sortOrder);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// TODO Auto-generated method stub
		// Replace the result Cursor displayed by the Cursor Adapter with
		// the new result set.
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		// Remove the existing result Cursor from the List Adapter
		adapter.swapCursor(null);
	}

}
