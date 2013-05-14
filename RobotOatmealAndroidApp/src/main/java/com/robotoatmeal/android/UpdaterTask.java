package com.robotoatmeal.android;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
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
import android.content.SharedPreferences;

public class UpdaterTask extends TimerTask
{
	private final static String MAPPINGS_URI = "http://www.robotoatmeal.com/static/js/ro-merchant-names.js";
	private final static String MAPPINGS_FILE_NAME = "ro-merchant-names.js";
	private final static String UPDATEKEY = "UpdateInfo";
	private RestTemplate m_httpClient;
	private Context m_context;
	private IMappings m_mappings;
	
	public UpdaterTask(Context appContext, IMappings mappings)
	{
		m_context = appContext;
		m_httpClient = new RestTemplate();
		m_mappings = mappings;
	}
	
	@Override
	public void run()
	{
		checkForUpdates();
	}
	
	public boolean checkForUpdates()
	{
		/* load the existing mappings file if it exists */
		File mappingsFile = new File(m_context.getFilesDir().getAbsolutePath() + 
				MAPPINGS_FILE_NAME);
		
		if(mappingsFile.exists())
			m_mappings.load(mappingsFile);
		
		SharedPreferences preferences = m_context.getSharedPreferences(UPDATEKEY, Context.MODE_PRIVATE);
		
		/* setting default to 0 forces us to get the latest mappings,
		 * since this is the first time we are doing it */
		long lastUpdateDate = preferences.getLong("lastUpdated", 0);
	
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setIfModifiedSince(lastUpdateDate);
		requestHeaders.setAcceptEncoding(ContentCodingType.GZIP);
		
		/* de-compress GZIP output */
		m_httpClient.getMessageConverters().add(new StringHttpMessageConverter());
		
		ResponseEntity<String> response = m_httpClient.exchange(MAPPINGS_URI,
			HttpMethod.GET,
			new HttpEntity<String>(requestHeaders),
			String.class);

		/* was it updated? */
		if(response.getStatusCode() == HttpStatus.NOT_MODIFIED)
		{
			return false;
		}
		
		String mappingsData = response.getBody();
		updateMappingsFile(mappingsData);
		
		return true;
	}
	
	private void updateMappingsFile(String mappingsData)
	{
		File mappingsFile = new File(m_context.getFilesDir().getAbsolutePath() + 
				MAPPINGS_FILE_NAME);
		
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
		SharedPreferences preferences = m_context.getSharedPreferences(UPDATEKEY, Context.MODE_PRIVATE);
		Date date = new Date();
		preferences.edit().putLong("lastUpdated", date.getTime());
	}
}
