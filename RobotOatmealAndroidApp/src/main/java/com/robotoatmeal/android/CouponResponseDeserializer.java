package com.robotoatmeal.android;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class CouponResponseDeserializer 
	implements JsonDeserializer<CouponContainer>
{
	@Override 
	public CouponContainer deserialize(JsonElement json, 
			Type typeOfT, JsonDeserializationContext ctx) throws JsonParseException
	{
		JsonObject obj = json.getAsJsonObject().get("coupons").getAsJsonObject();
		Iterator<Entry<String, JsonElement>> entry = obj.entrySet().iterator();
		
		int merchantId = entry.next().getValue().getAsInt();
		
		JsonArray couponArray = entry.next().getValue().getAsJsonArray();
		Iterator<JsonElement> couponIter = couponArray.iterator();
		
		List<Coupon> coupons = new ArrayList<Coupon>();
		
		/* if an entry is not found, it is set to null */
		while(couponIter.hasNext())
		{
			JsonObject coupon = couponIter.next().getAsJsonObject();
			String couponCode = coupon.get("couponCode").getAsString();
			String description = coupon.get("description").getAsString();
			int couponUsageCount = coupon.get("couponUsageCount").getAsInt();
			
			String expirationDate;
			
			JsonElement optional = coupon.get("expirationDate");
			
			if(optional == null)
				expirationDate = "";
			else
				expirationDate = optional.getAsString();
				
			String lastUsedDate;
			optional = coupon.get("lastUsedDate");
			
			if(optional == null)
				lastUsedDate = "";
			else
				lastUsedDate = optional.getAsString();
			
			
			coupons.add(new Coupon(couponCode, description, couponUsageCount, expirationDate, lastUsedDate));
		}
		
		int includedResults = entry.next().getValue().getAsInt();
		int totalResults = entry.next().getValue().getAsInt();
		
		return new CouponContainer(merchantId, coupons, includedResults, totalResults);
	}
}
