package com.robotoatmeal.android.test;

import java.io.File;

import com.robotoatmeal.android.MappingsArray;
import com.robotoatmeal.android.Merchant;
import com.robotoatmeal.android.UpdaterTask;

import android.content.Context;
import android.test.InstrumentationTestCase;

public class UpdaterTest extends InstrumentationTestCase
{
	private Context mockContext;
	private Context appContext;
	private UpdaterTask updater;
	private MappingsArray mappings;
	
	public void testFirstMappingsUpdateSuccess()
	{
		mockContext = getInstrumentation().getContext();
		appContext = getInstrumentation().getTargetContext();
		mappings = new MappingsArray();

		File mappingsFile = new File(appContext.getFilesDir().getAbsolutePath() + "/ro-merchant-names.js");
		mappingsFile.delete();
		
		updater = new UpdaterTask(appContext, mappings);
		
		boolean updated = updater.checkForUpdates();
		assert(updated == true);
		
		Merchant[] merchants = mappings.getMerchants();
		Merchant first = merchants[0];
		Merchant last = merchants[merchants.length - 1];
		assert(first.name == "00 - Cymax Stores Global Access" && first.id == 185495);
		assert(last.name == "zZounds" && last.id == 18022);
		
		updated = updater.checkForUpdates();
		assert(updated == false);
	}
	
	public void testUpdateWithExistingMappingsLoaded()
	{
		return;
	}
	
}