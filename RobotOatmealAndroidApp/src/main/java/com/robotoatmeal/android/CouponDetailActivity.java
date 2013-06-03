package com.robotoatmeal.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_coupon_detail)
public class CouponDetailActivity extends Activity {

	private RobotOatmealState m_appState;
	Coupon coupon;
	int merchantId;
	String merchantName;
	
	@ViewById
	TextView description;
		
	@ViewById
	TextView couponCode;
	
	@AfterViews
	void updateText() {
		description.setText(coupon.description);
		couponCode.setText(coupon.couponCode);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		m_appState = (RobotOatmealState) getApplicationContext();
		
		merchantId = getIntent().getIntExtra(MainActivity.MERCHANT_ID, -1);
		merchantName = getIntent().getStringExtra(MainActivity.MERCHANT_NAME);
		
		String couponDetail = getIntent().getStringExtra(MainActivity.COUPON_DETAIL);
		Gson gson = new Gson();
		coupon = gson.fromJson(couponDetail, Coupon.class);
		
		setTitle(merchantName + " / " + coupon.couponCode);
		
		setContentView(R.layout.activity_coupon_detail);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.favorite, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (merchantId != -1) {
			MenuItem favorite_toggle = menu.findItem(R.id.favorite_toggle);
			if (m_appState.favorites.get(merchantId) == null)
				favorite_toggle.setIcon(R.drawable.ic_action_search);
			else
				favorite_toggle.setIcon(R.drawable.ic_launcher);
		}
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
		case R.id.favorite_toggle:
			if (merchantId != -1) {
				if (m_appState.favorites.get(merchantId) == null)
					m_appState.favorites.add(merchantId);
				else
					m_appState.favorites.delete(merchantId);
				invalidateOptionsMenu();
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
