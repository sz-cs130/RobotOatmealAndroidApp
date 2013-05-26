
package com.robotoatmeal.android;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;
import com.robotoatmeal.android.CacheService.CacheBinder;

@EActivity(R.layout.activity_main)
public class MainActivity
    extends Activity
{
	static final String SEARCH = "search";
	static final String RESULTS = "results";
	static final String COUPON_DETAIL = "couponDetail";
	
	CacheService m_service;
	boolean m_bound = false;
	
	BroadcastReceiver m_broadcastReceiver;
	LocalBroadcastManager m_localBroadcastManager;
	
	IMappings m_mappings;
	
	private RobotOatmeal m_appState;
	
	private ServiceConnection m_connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className,
		        IBinder service)
		{
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			CacheBinder binder = (CacheBinder) service;
			m_service = binder.getService();
			m_bound = true;
		}

	    @Override
	    public void onServiceDisconnected(ComponentName arg0) {
	        m_bound = false;
	    }
	};
    
	@ViewById
	AutoCompleteTextView searchBar;
    
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		/* clear old search results */
		m_appState = (RobotOatmeal) getApplicationContext();
		m_appState.savedSearch.clearSearchResults();
				
        m_localBroadcastManager = LocalBroadcastManager.getInstance(this);

        m_broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if(intent.getAction().equals("MappingsLoaded"))
                {
                    m_mappings = m_service.getMappings();
                    Merchant[] merchants = m_mappings.getMerchants();
                    
                    ArrayAdapter<Merchant> adapter = new ArrayAdapter<Merchant>(MainActivity.this,
                            android.R.layout.simple_dropdown_item_1line, merchants);
                 
                    searchBar.setAdapter(adapter);
                    searchBar.setEnabled(true);
                } 
            }
        };
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		IntentFilter filter = new IntentFilter();
	    filter.addAction("MappingsLoaded");
	    m_localBroadcastManager.registerReceiver(m_broadcastReceiver, filter);
	}
	
	/* Only starts after checkForUpdates has been run once. 
	 * onStart() for activities and services both use the main
	 * UI thread. */
    @Override
    protected void onStart() {
        super.onStart();
        
        Intent intent = new Intent(this, CacheService.class);
        bindService(intent, m_connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (m_bound) {
            unbindService(m_connection);
            m_bound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater();
        return true;
    }
    
    @UiThread
    void doSomethingElseOnUiThread()
    {
    }

    @Background
    void doSomethingInBackground() {
    	
		Intent serviceIntent = new Intent(this, CacheService.class);
		this.startService(serviceIntent);
    	
        doSomethingElseOnUiThread();
    }
    
    @Click(R.id.searchButton) 
    void searchButtonClicked(View view)
    {
    	Intent intent = new Intent(this, SearchResultsActivity_.class);
    	String search = searchBar.getText().toString();
    	
    	if(search == "" || search.replaceAll("\\s", "") == "")
    	{
    		return;
    	}
    	
    	int merchantId = m_mappings.getMerchantId(search);
    	
    	if(merchantId == -1)
    	{
    		/* not found */
    		System.out.println("[MainActivity] Id not found.");
    		
    		CouponContainer empty = null;
    		intent.putExtra(RESULTS, empty);
    		this.startActivity(intent);
    	}
    	else
    	{
        	intent.putExtra("merchantId", merchantId);
        	intent.putExtra(SEARCH, search);

    		new MerchantSearchTask(this).execute(intent);	
    	}
    	
    }

}
