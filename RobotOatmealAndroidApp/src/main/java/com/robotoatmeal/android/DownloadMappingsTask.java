package com.robotoatmeal.android;

import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;

public class DownloadMappingsTask extends AsyncTask<Long, Void, String>
{
	private final static String MAPPINGS_URI = "http://www.robotoatmeal.com/static/js/ro-merchant-names.js";
	private MappingsUpdater m_updater;
	
	public DownloadMappingsTask()
	{
		
	}
	
	public DownloadMappingsTask(MappingsUpdater updater)
	{
		m_updater = updater;
	}

	@Override
	protected String doInBackground(Long ... params)
	{
		long lastUpdateDate = params[0];
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setIfModifiedSince(lastUpdateDate);
		requestHeaders.setAcceptEncoding(ContentCodingType.GZIP);
		
		RestTemplate client = new RestTemplate();
		
		/* Uncompress GZIP output */
		client.getMessageConverters().add(new StringHttpMessageConverter());
		
		ResponseEntity<String> response = client.exchange(MAPPINGS_URI,
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
	
	@Override
    protected void onPostExecute(String result) 
    {
        super.onPostExecute(result);
        m_updater.updateMappingsFile(result);
    }
}