package com.robotoatmeal.android;

import android.os.Parcel;
import android.os.Parcelable;

class Coupon implements Parcelable
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

    public Coupon(Parcel in) 
    {
    	couponCode = in.readString();
        description = in.readString();
        couponUsageCount = in.readInt();
        expirationDate = in.readString();
        lastUsedDate = in.readString();
    }
	
	public Coupon(String code, String desc, int usage, 
			String expire, String lastused)
	{
		couponCode = code;
		description = desc;
		couponUsageCount = usage;
		expirationDate = expire;
		lastUsedDate = lastused;
	}
	
	@Override
	public int describeContents() {
	    return 0;
	}
	
	@Override
    public void writeToParcel(Parcel out, int flags)
	{
		out.writeString(couponCode);
        out.writeString(description);
        out.writeInt(couponUsageCount);
        out.writeString(expirationDate);
        out.writeString(lastUsedDate);
	}

     public static final Parcelable.Creator<Coupon> CREATOR
             = new Parcelable.Creator<Coupon>() {
         public Coupon createFromParcel(Parcel in) {
             return new Coupon(in);
         }

         public Coupon[] newArray(int size) {
             return new Coupon[size];
         }
     };
}