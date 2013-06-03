package com.robotoatmeal.android;

import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MerchantSearchTask extends AsyncTask<Intent, Void, CouponContainer>
{
	private RestTemplate m_httpClient;
	static public final String BASE_URI = "http://catalog.bizrate.com/services/catalog/v1/us/coupons?";
	static public final String API_KEY = "&apiKey=54e639e630f129f73d3e77b2a67a0030";
	static public final String PUBLISHER_ID = "&publisherId=1004";
	static public final String FORMAT = "&format=json";
	private Context m_context;
	private Intent m_intent;
	
	public MerchantSearchTask()
	{
	
	}
	
	public MerchantSearchTask(Context context)
	{
		m_context = context;
	}

	@Override
	protected CouponContainer doInBackground(Intent ... params)
	{
		m_intent = params[0];
		m_httpClient = new RestTemplate();
		
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAcceptEncoding(ContentCodingType.GZIP);
		
		/* Uncompress GZIP output */
		m_httpClient.getMessageConverters().add(new StringHttpMessageConverter());
		
		int merchantId = m_intent.getIntExtra(MainActivity_.MERCHANT_ID, -1);
		
		String endpointUrl = BASE_URI + API_KEY + PUBLISHER_ID + 
				FORMAT + "&merchantId=" + merchantId;
		
		System.out.println("Merchant ID for this search: " + merchantId);
		
		try {
			ResponseEntity<String> response = m_httpClient.exchange(endpointUrl,
				HttpMethod.GET,
				new HttpEntity<String>(requestHeaders),
				String.class);
			
			String jsonResults = response.getBody();
			
			Gson gson = new GsonBuilder()
			.registerTypeAdapter(CouponContainer.class, 
					new CouponResponseDeserializer())
			.create();
			
			System.out.println(jsonResults);
			
			CouponContainer container = gson.fromJson(jsonResults, CouponContainer.class);
			
			return container;
		}
		catch(ResourceAccessException e){
			e.printStackTrace();
			
			/* TODO: Inform user that the host is unreachable. */
			return null;
		}
	}
	
	@Override
    protected void onPostExecute(CouponContainer result) 
    {
        super.onPostExecute(result);
        m_intent.putExtra(MainActivity.RESULTS, result);
        m_context.startActivity(m_intent);
    }

}
