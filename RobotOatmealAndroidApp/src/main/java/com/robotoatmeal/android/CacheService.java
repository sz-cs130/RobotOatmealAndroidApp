package com.robotoatmeal.android;

import java.util.Timer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class CacheService extends Service
{
	private Context m_context;
	private LocalBroadcastManager m_broadcastManager;
	private IMappings m_mappings;
	private Timer m_timer;
	private final IBinder m_binder = new CacheBinder();
	private final static long TIMER_INTERVAL = 24*3600*1000;
	
    public class CacheBinder extends Binder 
    {
        CacheService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CacheService.this;
        }
    }

	public IBinder onBind(Intent intent)
	{
		return m_binder;
	}
	
	@Override
	public void onCreate()
	{
		m_context = this.getApplicationContext();
		m_mappings = new MappingsArray();
		m_timer = new Timer();
		
		m_broadcastManager = LocalBroadcastManager.getInstance(this);
		
		startUpdater();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return 0;
	}
	 
	@Override
	public void onDestroy()
	{
		return;
	}
	
	private void startUpdater()
	{
		UpdaterTask updater = new UpdaterTask(m_context, m_mappings, m_broadcastManager);
		m_timer.scheduleAtFixedRate(updater, 0, TIMER_INTERVAL);
	}
	
	public IMappings getMappings()
	{
		return m_mappings;
	}

}
