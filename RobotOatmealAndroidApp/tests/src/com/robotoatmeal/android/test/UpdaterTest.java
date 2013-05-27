package com.robotoatmeal.android.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.robotoatmeal.android.IMappings;
import com.robotoatmeal.android.MappingsArray;
import com.robotoatmeal.android.Merchant;
import com.robotoatmeal.android.MappingsUpdater;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.InstrumentationTestCase;

public class UpdaterTest extends InstrumentationTestCase
{
	private Context appContext;
	private MappingsUpdater updater;
	private IMappings mappings;
	
	public File getMappingsFile()
	{
		return new File(appContext.getFilesDir().getAbsolutePath() + "/ro-merchant-names.js");
	}
	
	public String readFile(File file)
	{
		FileInputStream is;
		StringBuffer mappingsData = new StringBuffer("");
		
		try
		{
			is = new FileInputStream(file);
			
	    	byte[] buffer = new byte[8096];
	    	int bytesRead = 0;
	    	
			try {
				while ((bytesRead = is.read(buffer)) != -1)
				{
					mappingsData.append(new String(buffer, 0, bytesRead));
				}
				
				is.close();
				
				return mappingsData.toString();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return "";
	}
	
	public void testFirstMappingsUpdateSuccess()
	{
		appContext = getInstrumentation().getTargetContext();
		mappings = new MappingsArray();

		File mappingsFile = getMappingsFile();
		mappingsFile.delete();
		
		updater = new MappingsUpdater(appContext, mappings);
		updater.checkForUpdates();
		
		/* are mappings correctly loaded? */
		Merchant[] merchants = mappings.getMerchants();
		Merchant first = merchants[0];
		Merchant last = merchants[merchants.length - 1];
		
		assertEquals(first.name, "00 - Cymax Stores Global Access");
		assertEquals(first.id, 185495);
		
		assertEquals(last.name,"zZounds");
		assertEquals(last.id,18022);
	}
	
	public void testUpdateWithoutNewUpdates()
	{
		appContext = getInstrumentation().getTargetContext();
		mappings = new MappingsArray();
		
		updater = new MappingsUpdater(appContext, mappings);
		updater.checkForUpdates();
		
		String oldMappings = readFile(getMappingsFile());
		
		updater.checkForUpdates();
		
		String newMappings = readFile(getMappingsFile());
		
		assert(oldMappings == newMappings);
	}
	
	public void testPreferencesUpdated()
	{
		appContext = getInstrumentation().getTargetContext();
		mappings = new MappingsArray();
		
		SharedPreferences prefs = appContext.getSharedPreferences("UpdateInfo", Context.MODE_PRIVATE);
		long oldTime = prefs.getLong("lastUpdated", 0);
		
		updater = new MappingsUpdater(appContext, mappings);
		updater.checkForUpdates();
		
		long newTime = prefs.getLong("lastUpdated", 0);
		
		assert(newTime > oldTime);
	}
	
}