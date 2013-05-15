package com.robotoatmeal.android;

import java.io.StringReader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.gson.stream.JsonReader;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_search_results)
public class SearchResultsActivity extends Activity {
	
	@ViewById
	TextView message;
	private String search;
	private String results;

	@AfterViews
	void updateViews() {
		if (search == null || results == null)
			message.setText("Oops, we can't find any coupons for " + search);
		else {
			message.setText("Search results for" + search);
			
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new StringReader(results));
			reader.setLenient(true);
			
			CouponContainer container = gson.fromJson(reader, CouponContainer.class);
			GridView grid = (GridView) findViewById(R.id.grid);
			ArrayAdapter<Coupon> adapter = new ArrayAdapter<Coupon>(this, 
					android.R.layout.simple_list_item_1, container.coupon);
			grid.setAdapter(adapter);
			grid.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {	
					Gson gson = new Gson();
					Coupon coupon = (Coupon) parent.getItemAtPosition(position);
					String couponDetail = gson.toJson(coupon, Coupon.class);
					
					Intent intent = new Intent(SearchResultsActivity.this, CouponDetailActivity_.class);
					intent.putExtra(MainActivity.COUPON_DETAIL, couponDetail);
					startActivity(intent);
				}
				
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.GLOBAL, MODE_PRIVATE);
		search = sharedPreferences.getString(MainActivity.SEARCH, null);
		results = sharedPreferences.getString(MainActivity.RESULTS, null);
		
		setContentView(R.layout.activity_search_results);
		// Show the Up button in the action bar.
		setupActionBar();
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
