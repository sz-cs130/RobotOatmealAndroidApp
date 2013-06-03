package com.robotoatmeal.android;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class MerchantMappingsArray implements IMappings
{
	private Merchant[] m_merchants;
	private boolean m_loaded;
	
	public MerchantMappingsArray()
	{
		m_loaded = false;
	}
	
	public Merchant[] getMerchants()
	{
		return m_merchants;
	}
	
	public void load(File mappingsFile)
	{
		StringBuilder mappingsBuffer = new StringBuilder("");
		try
		{
			InputStream is = new FileInputStream(mappingsFile);
			
	    	byte[] buffer = new byte[8096];
	    	
	    	int bytesRead = 0;
			
			while ((bytesRead = is.read(buffer)) != -1) {
				mappingsBuffer.append(new String(buffer,0,bytesRead));
			}
			
			is.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
    
    	String mappingsContent = mappingsBuffer.toString();
    	
    	/* convert javascript array into JSON */
    	int start = mappingsContent.indexOf("[");
    	int end = mappingsContent.indexOf("]");
    	String merchantArray = mappingsContent.substring(start,end + 1);
    	
    	Gson gson = new Gson();
		JsonReader reader = new JsonReader(new StringReader(merchantArray));
		m_merchants = gson.fromJson(reader, Merchant[].class);
		
		m_loaded = true;
		
		return;
	}
	
	public int getMerchantId(String name)
	{
		String key = name.toLowerCase();
		int lo = 0;
		int hi = m_merchants.length - 1;
		int result = 0;
		
		while(lo <= hi)
		{
			int mid = lo + (hi - lo) / 2;
			
			result = key.compareTo(m_merchants[mid].name.toLowerCase());
			
			if(result == 0)
			{
				return m_merchants[mid].id;
			}
			else if(result < 0)
			{
				hi = mid - 1;
			}
			else
			{
				lo = mid + 1;
			}
		}
		
		return -1;
	}
	
	public String getMerchantName(int id)
	{
		for (Merchant merchant: m_merchants) {
			if (merchant.id == id)
				return merchant.name;
		}
		
		return null;
	}
	
	public boolean isLoaded()
	{
		return m_loaded;
	}
}
