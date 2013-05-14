package com.robotoatmeal.android;

import java.util.Timer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class CacheService extends Service
{
	private Context m_context;
	private IMappings m_mappings;
	private Timer m_timer;
	private final static long TIMER_INTERVAL = 24*3600*1000;
	
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		m_context = this.getApplicationContext();
		m_mappings = new MappingsArray();
		m_timer = new Timer();
		
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
		UpdaterTask updater = new UpdaterTask(m_context, m_mappings);
		m_timer.scheduleAtFixedRate(updater, 0, TIMER_INTERVAL);
	}
}
