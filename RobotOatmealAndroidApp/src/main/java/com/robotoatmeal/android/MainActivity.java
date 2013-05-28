
package com.robotoatmeal.android;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.Click;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity
    extends Activity
{
	static final String SEARCH = "search";
	static final String RESULTS = "results";
	static final String COUPON_DETAIL = "couponDetail";
	final static long TIMER_INTERVAL = 24*3600*1000;
	
	private RobotOatmealState m_appState;
	BroadcastReceiver m_broadcastReceiver;
	LocalBroadcastManager m_localBroadcastManager;
	private IMappings m_mappings;
	
	@ViewById
	AutoCompleteTextView searchBar;
    
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		m_appState = (RobotOatmealState) getApplicationContext();
		
		/* clear old search results */
		m_appState.savedSearch.clearSearchResults();
				
        m_localBroadcastManager = LocalBroadcastManager.getInstance(this);

        m_broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                if(intent.getAction().equals("MappingsLoaded"))
                {
                	attachAutoComplete();
                } 
            }
        };
		
        startRecurringTasks();
	}
	
	/*
	 * Note: Make sure that Android does not destroy everything on 
	 * screen rotate! Check the AndroidManifest.xml for this setting!
	 * */
    @Override
    protected void onStart() {
        super.onStart();
        
		if(m_appState.mappings.isLoaded())
		{
			attachAutoComplete();
		}
    }
    
	@Override
	protected void onResume()
	{
		super.onResume();
		
		IntentFilter filter = new IntentFilter();
	    filter.addAction("MappingsLoaded");
	    m_localBroadcastManager.registerReceiver(m_broadcastReceiver, filter);
	}

    @Override
    protected void onStop() {
        super.onStop();
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
    	
        doSomethingElseOnUiThread();
    }
    
    void startRecurringTasks()
    {
		if(m_appState.tasksScheduled == false)
		{
			AlarmManager alarmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
			Intent intent = new Intent(this, MappingsUpdater.class);   
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,  intent, 0);
			alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 
					SystemClock.elapsedRealtime(), TIMER_INTERVAL, pendingIntent);
			
			m_appState.tasksScheduled = true;
		}
    }
    
    void attachAutoComplete()
    {
        m_mappings = m_appState.mappings;
        Merchant[] merchants = m_mappings.getMerchants();
        
        ArrayAdapter<Merchant> adapter = new ArrayAdapter<Merchant>(MainActivity.this,
                android.R.layout.simple_dropdown_item_1line, merchants);
     
        searchBar.setAdapter(adapter);
        searchBar.setEnabled(true);
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
