package com.robotoatmeal.android;

import android.app.Application;

public class RobotOatmeal extends Application
{
	public Search savedSearch = new Search();
	
	public class Search
	{
		public String query;
		public CouponContainer results;
		
		public Search()
		{
			query = null;
			results = null;
		}
		
		public void clearSearchResults()
		{
			query = null;
			results = null;
		}
	}
}
