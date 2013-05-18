package com.robotoatmeal.android;

class Coupon {
	
	String description;
	int merchantId;
	String couponCode;
	int couponUsagesCount;
	String expirationDate;
	String lastUsedDate;
	
	@Override
	public String toString() {
		return couponCode;
	}
}