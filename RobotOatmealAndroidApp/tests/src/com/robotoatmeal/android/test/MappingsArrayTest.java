package com.robotoatmeal.android.test;
import com.robotoatmeal.android.Merchant;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.robotoatmeal.android.MappingsArray;

public class MappingsArrayTest extends InstrumentationTestCase 
{
	private Merchant[] merchants;
	private Context mockContext;
	private Context appContext;
	private MappingsArray mappings;
	
	public MappingsArrayTest()
	{

	}
	
	public void testArrayLoadedCorrectly()
	{
		mockContext = getInstrumentation().getContext();
		appContext = getInstrumentation().getTargetContext();
		mappings = new MappingsArray();

		/* create the mappings file on the system */
		File mappingsFile = new File(appContext.getFilesDir().getAbsolutePath() + "/ro-merchant-names.js");
		
		try
		{
			InputStream is = mockContext.getResources().openRawResource(R.raw.romerchantnames);
			FileOutputStream output = new FileOutputStream(mappingsFile);
			
	    	byte[] buffer = new byte[8096];
	    	
	    	int bytesRead = 0;

			while ((bytesRead = is.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
			
			is.close();
			output.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		mappings.load(mappingsFile);
		merchants = mappings.getMerchants();

		Merchant first = merchants[0];
		Merchant last = merchants[merchants.length - 1];
		assert(first.name == "00 - Cymax Stores Global Access" && first.id == 185495);
		assert(last.name == "zZounds" && last.id == 18022);
	}
	
	/* TODO: Code smell. Do not rely on ordering of unit tests.  */
	public void testMerchantSearchMin()
	{
		assert(mappings.getMerchantId("00 - Cymax Stores Global Access") == 185495);
	}
	
	public void testMerchantSearchMid()
	{
		assert(mappings.getMerchantId("GameStop.com") == 23984);
	}
	
	public void testMerchantSearchMax()
	{
		assert(mappings.getMerchantId("zZounds") == 18022);
	}
}
