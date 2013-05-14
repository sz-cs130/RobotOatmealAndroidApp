package com.robotoatmeal.android;
import java.io.File;

public interface IMappings 
{
	public void load(File mappingsFile);
	public int getMerchantId(String name);
}
