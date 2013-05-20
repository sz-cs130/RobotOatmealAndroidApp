package com.robotoatmeal.android;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.TimerTask;

import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;

public class UpdaterTask extends TimerTask
{
	private final static String MAPPINGS_URI = "http://www.robotoatmeal.com/static/js/ro-merchant-names.js";
	private final static String MAPPINGS_FILE_NAME = "ro-merchant-names.js";
	private final static String PREF_KEY = "UpdateInfo";
	private final static String LAST_UPDATED_KEY = "lastUpdated";
	private RestTemplate m_httpClient;
	private IMappings m_mappings;
	private Context m_context;
	private LocalBroadcastManager m_broadcastManager;
	private SharedPreferences m_updateInfo;
	
	public UpdaterTask(Context appContext, IMappings mappings, 
			LocalBroadcastManager broadcastManager)
	{
		m_context = appContext;
		m_httpClient = new RestTemplate();
		m_mappings = mappings;
		m_broadcastManager = broadcastManager;
		m_updateInfo = m_context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
	}
	
	@Override
	public void run()
	{
		checkForUpdates();
	}
	
	public void checkForUpdates()
	{
		/* load the existing mappings file if it exists */
		File mappingsFile = getMappingsFile();
		
		if(mappingsFile.exists())
		{
			m_mappings.load(mappingsFile);
			broadcastMappingsLoaded();
		}
		
		/* setting default to 0 forces us to get the latest mappings,
		 * since this is the first time we are doing it */
		long lastUpdateDate = m_updateInfo.getLong(LAST_UPDATED_KEY, 0);
		String mappingsData = getMappingsData(lastUpdateDate);
		
		if(mappingsData == "")
			return;
		
		updateMappingsFile(mappingsData);
	}
	
	private File getMappingsFile()
	{
		return new File(m_context.getFilesDir().getAbsolutePath() + 
				MAPPINGS_FILE_NAME);
	}
	
	private String getMappingsData(long lastUpdateDate)
	{
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setIfModifiedSince(lastUpdateDate);
		requestHeaders.setAcceptEncoding(ContentCodingType.GZIP);
		
		/* Uncompress GZIP output */
		m_httpClient.getMessageConverters().add(new StringHttpMessageConverter());
		
		ResponseEntity<String> response = m_httpClient.exchange(MAPPINGS_URI,
			HttpMethod.GET,
			new HttpEntity<String>(requestHeaders),
			String.class);

		/* was it updated? */
		if(response.getStatusCode() == HttpStatus.NOT_MODIFIED)
		{
			return "";
		}
		else
		{
			return response.getBody();
		}
	}
	
	private void updateMappingsFile(String mappingsData)
	{
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
