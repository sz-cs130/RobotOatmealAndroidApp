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
		
		assertEquals(first.name, "00 - Cymax Stores Global Access");
		assertEquals(first.id, 185495);
		
		assertEquals(last.name,"zZounds");
		assertEquals(last.id,18022);
		
		assertEquals(mappings.getMerchantId("GameStop.com"), 23984);
	}
}
