package com.robotoatmeal.android;

class Coupon 
{
	String couponCode;
	String description;
	int couponUsageCount;
	String expirationDate;
	String lastUsedDate;
	
	@Override
	public String toString() {
		return couponCode;
	}
}