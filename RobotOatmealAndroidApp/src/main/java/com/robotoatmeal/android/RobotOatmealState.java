package com.robotoatmeal.android;

import java.util.Timer;

import android.app.Application;

public class RobotOatmealState extends Application
{
	public boolean tasksScheduled = false;
	public IMappings mappings = new MerchantMappingsArray();
	public Search savedSearch = new Search();
	public Favorites favorites;
	public Timer favoritesNotifier;
	public int merchantId;
	
	public class Search
	{
		public int merchantId;
		public String merchantName;
		public CouponContainer results;
		
		public Search()
		{
			clearSearchResults();
		}
		
		public void clearSearchResults()
		{
			merchantId = -1;
			merchantName = null;
			results = null;
		}
	}
	
	public void startFavoritesNotifier()
	{
		favoritesNotifier = new Timer();
		favoritesNotifier.scheduleAtFixedRate(
				new FavoritesNotifier(this), 0, MainActivity.TIMER_INTERVAL);
	}
}
