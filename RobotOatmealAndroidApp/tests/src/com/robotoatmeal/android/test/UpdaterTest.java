package com.robotoatmeal.android.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.robotoatmeal.android.MappingsArray;
import com.robotoatmeal.android.Merchant;
import com.robotoatmeal.android.UpdaterTask;

import android.content.Context;
import android.test.InstrumentationTestCase;

public class UpdaterTest extends InstrumentationTestCase
{
	private Context appContext;
	private UpdaterTask updater;
	private MappingsArray mappings;
	
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
		
		updater = new UpdaterTask(appContext, mappings);
		updater.checkForUpdates();
		
		/* are mappings correctly loaded? */
		Merchant[] merchants = mappings.getMerchants();
		Merchant first = merchants[0];
		Merchant last = merchants[merchants.length - 1];
		assert(first.name == "00 - Cymax Stores Global Access" && first.id == 185495);
		assert(last.name == "zZounds" && last.id == 18022);
	}
	
	public void testUpdateWithoutNewUpdates()
	{
		appContext = getInstrumentation().getTargetContext();
		mappings = new MappingsArray();
		
		updater = new UpdaterTask(appContext, mappings);
		updater.checkForUpdates();
		
		String oldMappings = readFile(getMappingsFile());
		
		updater.checkForUpdates();
		
		String newMappings = readFile(getMappingsFile());
		
		assert(oldMappings == newMappings);
	}
	
}