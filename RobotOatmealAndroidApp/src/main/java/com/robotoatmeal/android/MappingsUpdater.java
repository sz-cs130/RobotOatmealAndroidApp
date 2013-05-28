package com.robotoatmeal.android;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

public class MappingsUpdater extends BroadcastReceiver
{
	private final static String MAPPINGS_FILE_NAME = "ro-merchant-names.js";
	private final static String PREF_KEY = "UpdateInfo";
	private final static String LAST_UPDATED_KEY = "lastUpdated";
	private Context m_context;
	private LocalBroadcastManager m_broadcastManager;
	private SharedPreferences m_updateInfo;
	private IMappings m_mappings;
	private RobotOatmealState m_appState;
	
	public MappingsUpdater()
	{
		
	}
	
	/* unit testability */
	public MappingsUpdater(Context context, IMappings mappings)
	{
		m_context = context;
		m_broadcastManager = LocalBroadcastManager.getInstance(m_context);
		m_updateInfo = m_context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		m_mappings = mappings;
	}
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		m_context = context;
		m_broadcastManager = LocalBroadcastManager.getInstance(m_context);
		m_updateInfo = m_context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
		m_appState = (RobotOatmealState) m_context.getApplicationContext();
		
		/* modify the global mappings state */
		m_mappings = m_appState.mappings;
		
		checkForUpdates();
	}
	
	public void checkForUpdates()
	{
		/* load the existing mappings file if it exists */
		File mappingsFile = getMappingsFile();
		
		if(mappingsFile.exists())
		{
			m_mappings.load(mappingsFile);
		}
		else
		{
			/* create it from the pre-loaded resource file */
			preloadMappings();
		}
		
		broadcastMappingsLoaded();
		
		/* setting default to 0 forces us to get the latest mappings,
		 * since this is the first time we are doing it */
		long lastUpdateDate = m_updateInfo.getLong(LAST_UPDATED_KEY, 0);
		new DownloadMappingsTask(this).execute(lastUpdateDate);
	}
	
	public void preloadMappings()
	{
		InputStream stream = m_context.getResources().openRawResource(R.raw.romerchantnames);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String line;
		StringBuilder mappings = new StringBuilder();
		
		try {
			
			while ((line = reader.readLine()) != null)
			{
				mappings.append(line);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		updateMappingsFile(mappings.toString());
	}
	
	private File getMappingsFile()
	{
		return new File(m_context.getFilesDir().getAbsolutePath() + 
				MAPPINGS_FILE_NAME);
	}
	
	public void updateMappingsFile(String mappingsData)
	{
		if(mappingsData == "")
			return;
		
		File mappingsFile = getMappingsFile();
		
		try
		{
			FileWriter writer = new FileWriter(mappingsFile);
			writer.write(mappingsData);
			writer.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		m_mappings.load(mappingsFile);
		
		/*  update the update time */
		Date date = new Date();
		m_updateInfo.edit().putLong(LAST_UPDATED_KEY, date.getTime());
		
		broadcastMappingsLoaded();
	}
	
	public void broadcastMappingsLoaded()
	{
	    Intent broadcastIntent = new Intent("MappingsLoaded");
	    m_broadcastManager.sendBroadcast(broadcastIntent);
	}
}
