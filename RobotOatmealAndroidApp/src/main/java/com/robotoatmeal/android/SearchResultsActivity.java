package com.robotoatmeal.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_search_results)
public class SearchResultsActivity extends Activity {
	
	@ViewById
	TextView message;
	
	private RobotOatmeal m_appState;
	private String search;
	private CouponContainer results;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		m_appState = (RobotOatmeal) getApplicationContext();
		
		Intent intent = getIntent();
		search = intent.getStringExtra(MainActivity.SEARCH);
		results = (CouponContainer) intent.getParcelableExtra(MainActivity.RESULTS);
		
		/* activities are always destroyed in the 
		 * emulator after you start a new activity...
		 * so let's just get the saved state each time 
		 * */
		getSavedSearchResults();
		
		/* TODO: This is called multiple times for no reason.
		 * Primarily an Android Annotations issue.
		 * */
		setContentView(R.layout.activity_search_results);
		
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	private void getSavedSearchResults()
	{
		if(results == null)
		{
			results = m_appState.savedSearchResults;
			search = m_appState.savedSearchQuery;
		}
	}
	
	private void setSavedSearchResults()
	{
		m_appState.savedSearchResults = results;
		m_appState.savedSearchQuery = search;
	}
	
	private void clearSavedSearchResults()
	{
		m_appState.savedSearchResults = null;
		m_appState.savedSearchQuery = null;
	}
	
	/* So future empty searches don't show the previous ones */
	@Override
	protected void onStop()
	{
		super.onStop();
		clearSavedSearchResults();
	}
	
	/* Can persist data, but only if this Activity is not destroyed. */
	@Override
	protected void onPause()
	{
		super.onPause();
		setSavedSearchResults();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		getSavedSearchResults();
	}
	
	/* Cannot persist data. Can only temporarily persist UI elements.
	 * Completely useless if OS destroys the activity when starting
	 * another.  */
	@Override
	protected void onSaveInstanceState (Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	@AfterViews
	void validateResults()
	{
		if(results == null || results.totalResults == 0)
		{
			message.setText("Oops, we can't find any coupons for " + search);
		}
		else
		{
			displaySearchResults();
		}
	}
	
	void displaySearchResults()
	{
		message.setText("Search results for" + search);
		
		GridView grid = (GridView) findViewById(R.id.grid);
		
		ArrayAdapter<Coupon> adapter = new ArrayAdapter<Coupon>(this,
				android.R.layout.simple_list_item_1, results.coupons);
		
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) 
			{	
				Gson gson = new Gson();
				Coupon coupon = (Coupon) parent.getItemAtPosition(position);
				String couponDetail = gson.toJson(coupon, Coupon.class);
	
				Intent intent = new Intent(SearchResultsActivity.this, CouponDetailActivity_.class);
				intent.putExtra(MainActivity.COUPON_DETAIL, couponDetail);
				startActivity(intent);
			}
		});
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_results, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
