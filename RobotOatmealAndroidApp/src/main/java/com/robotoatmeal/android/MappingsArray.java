package com.robotoatmeal.android;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class MappingsArray implements IMappings
{
	private Merchant[] m_merchants;
	
	public Merchant[] getMerchants()
	{
		return m_merchants;
	}
	
	public void load(File mappingsFile)
	{
		StringBuffer mappingsBuffer = new StringBuffer("");
		try
		{
			InputStream is = new FileInputStream(mappingsFile);
			
	    	byte[] buffer = new byte[8096];
			
			while (is.read(buffer) != -1) {
				mappingsBuffer.append(new String(buffer));
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
		
		return;
	}
	
	public int getMerchantId(String name)
	{
		String key = name;
		int lo = 0;
		int hi = m_merchants.length - 1;
		int result = 0;
		
		while(lo <= hi)
		{
			int mid = lo + (lo + hi) / 2;
			
			result = key.compareTo(m_merchants[mid].name);
			
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
}
