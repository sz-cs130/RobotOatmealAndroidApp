package com.robotoatmeal.android;

import java.util.HashSet;
import java.util.TimerTask;

import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FavoritesNotifier extends TimerTask {
	
	private RobotOatmealState m_appState;
	private Favorites favorites;
	private HashSet<Merchant> updates;
	
	FavoritesNotifier(RobotOatmealState context) {
		m_appState = context;
		favorites = m_appState.favorites;
		updates = new HashSet<Merchant>();
	}

	public void checkForUpdates() {
		updates.clear();
		boolean update = false;
		
		for (Merchant merchant: favorites.getall()) {
			RestTemplate httpClient = new RestTemplate();
			
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setAcceptEncoding(ContentCodingType.GZIP);
			
			/* Uncompress GZIP output */
			httpClient.getMessageConverters().add(new StringHttpMessageConverter());
			
			int merchantId = merchant.id;
			
			String endpointUrl = MerchantSearchTask.BASE_URI + MerchantSearchTask.API_KEY + MerchantSearchTask.PUBLISHER_ID + 
					MerchantSearchTask.FORMAT + "&merchantId=" + merchantId;
			
			System.out.println("Merchant ID for this search: " + merchantId);
			
			try {
				ResponseEntity<String> response = httpClient.exchange(endpointUrl,
					HttpMethod.GET,
					new HttpEntity<String>(requestHeaders),
					String.class);
				
				String jsonResults = response.getBody();
				
				Gson gson = new GsonBuilder()
				.registerTypeAdapter(CouponContainer.class, 
						new CouponResponseDeserializer())
				.create();
				
				System.out.println(jsonResults);
				
				HashSet<String> cachedCoupons = favorites.getCachedCoupons(merchantId);
				CouponContainer container = gson.fromJson(jsonResults, CouponContainer.class);
				for (Coupon coupon: container.coupons) {
					if (!cachedCoupons.contains(coupon.couponCode)) {
						update = true;
						updates.add(favorites.get(merchantId));
					}
				}
			}
			catch(ResourceAccessException e){
				e.printStackTrace();
				
				/* TODO: Inform user that the host is unreachable. */
			}
		}
		if (update)
			notifyAboutUpdates();
	}
	
	public void notifyAboutUpdates() {
		
		boolean first = true;
		StringBuilder text = new StringBuilder();
		
		for (Merchant merchant: updates) {
			if (!first)
				text.append(", ");
			text.append(merchant.name);
		}
		
		if (updates.size() != 1)
			text.append(" have new coupons.");
		else
			text.append(" has new coupons.");

		Intent resultIntent = new Intent(m_appState, MainActivity_.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(m_appState);
		stackBuilder.addParentStack(MainActivity_.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );

		Notification noti = new Notification.Builder(m_appState)
		        .setContentTitle("RobotOatmealAndroidApp")
		        .setContentText(text.toString())
		        .setSmallIcon(R.drawable.ic_action_search)
		        .setContentIntent(resultPendingIntent).build();
		    
		  
		NotificationManager notificationManager = 
		  (NotificationManager) m_appState.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, noti); 
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		checkForUpdates();
	}
	
}
