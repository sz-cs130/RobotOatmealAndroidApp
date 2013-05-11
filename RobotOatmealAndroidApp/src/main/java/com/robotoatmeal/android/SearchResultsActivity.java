package com.robotoatmeal.android;

import java.io.StringReader;
import java.util.ArrayList;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
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
			ArrayList<Button> views = new ArrayList<Button>();
			GridLayout grid = (GridLayout) findViewById(R.id.grid);
			for (final Coupon coupon: container.coupon) {
				Button couponButton = new Button(this);
				couponButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(SearchResultsActivity.this, CouponDetailActivity_.class);
						Gson gson = new Gson();
						String couponDetail = gson.toJson(coupon, Coupon.class);
						intent.putExtra(MainActivity.COUPON_DETAIL, couponDetail);
						startActivity(intent);
					}
					
				});
				
				views.add(couponButton);
				views.get(views.size()-1).setText(coupon.couponCode);
				grid.addView(views.get(views.size()-1));
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		search = intent.getStringExtra(MainActivity.SEARCH);
		results = intent.getStringExtra(MainActivity.RESULTS);
		if (search == null || results == null) {
			SharedPreferences queries = getSharedPreferences("queries", 0);
			search = queries.getString(MainActivity.SEARCH, null);
			results = queries.getString(MainActivity.RESULTS, null);
		} else {
			SharedPreferences queries = getSharedPreferences("queries", 0);
			SharedPreferences.Editor editor = queries.edit();
			editor.putString(MainActivity.SEARCH, search);
			editor.putString(MainActivity.RESULTS, results);
			editor.commit();
		}
		
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
