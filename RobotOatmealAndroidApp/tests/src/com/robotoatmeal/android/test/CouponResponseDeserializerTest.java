package com.robotoatmeal.android.test;

import java.io.IOException;
import java.io.InputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.robotoatmeal.android.CouponContainer;
import com.robotoatmeal.android.CouponResponseDeserializer;

import android.content.Context;
import android.test.InstrumentationTestCase;

public class CouponResponseDeserializerTest extends InstrumentationTestCase
{
	public void testCouponDeserializer()
	{
		Context mockContext = getInstrumentation().getContext();
		InputStream is = mockContext.getResources().openRawResource(R.raw.gamestop);
		
		int bytesRead = -1;
		
		StringBuilder output = new StringBuilder();
		
		byte [] buffer = new byte[8192]; 
		
		try {
			while ((bytesRead = is.read(buffer)) != -1)
			{
				output.append(new String(buffer, 0, bytesRead));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String jsonResults = output.toString();
		
		Gson gson = new GsonBuilder()
			.registerTypeAdapter(CouponContainer.class, 
					new CouponResponseDeserializer())
			.create();
		
		CouponContainer container = gson.fromJson(jsonResults, CouponContainer.class);
		
		assertNotNull(container);
	}
}
