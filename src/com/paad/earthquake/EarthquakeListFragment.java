package com.paad.earthquake;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.widget.SimpleCursorAdapter;

public class EarthquakeListFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private static final String TAG = "EARTHQUAKE";
	SimpleCursorAdapter adapter;
	Handler handler=new Handler();

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		// Create a new Adapter and bind it to the List View
		adapter = new SimpleCursorAdapter(getActivity(),
				android.R.layout.simple_list_item_1, null,
				new String[] { EarthquakeProvider.KEY_SUMMARY },
				new int[] { android.R.id.text1 }, 0);
		setListAdapter(adapter);

		getLoaderManager().initLoader(0, null, this);

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				refreshEarthquakes();
			}
		});
		t.start();
	}

	public void refreshEarthquakes() {
		
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getLoaderManager().restartLoader(0, null, EarthquakeListFragment.this);
			}
		});
		

		getActivity().startService(
				new Intent(getActivity(), EarthquakeUpdateService.class));
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		String[] projection = new String[] { EarthquakeProvider.KEY_ID,
				EarthquakeProvider.KEY_SUMMARY };

		Earthquake earthquakeActivity = (Earthquake) getActivity();
		String where = EarthquakeProvider.KEY_MAGNITUDE + " > "
				+ earthquakeActivity.minimumMagnitude;

		CursorLoader loader = new CursorLoader(getActivity()
				.getApplicationContext(), EarthquakeProvider.CONTENT_URI,
				projection, where, null, null);

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		// TODO Auto-generated method stub
		adapter.swapCursor(arg1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		adapter.swapCursor(null);
	}

}
