package com.robotoatmeal.android;

import java.util.ArrayList;
import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

public class CouponContainer implements Parcelable
{
	int merchantId;
	List<Coupon> coupons = new ArrayList<Coupon>();
	int includedResults;
	int totalResults;
	
    public CouponContainer(Parcel in) 
    {
    	merchantId = in.readInt();
    	System.out.println("Coupon Container's merchantId: " + merchantId);
        in.readTypedList(coupons, Coupon.CREATOR);
        includedResults = in.readInt();
        totalResults = in.readInt();   
    }

	public CouponContainer(int id, List<Coupon> couponlist,
			int included, int total) 
	{
		merchantId = id;
		coupons = couponlist;
		includedResults = included;
		totalResults = total;
	}
	
	@Override
	public int describeContents() {
	    return 0;
	}
	
	@Override
     public void writeToParcel(Parcel out, int flags) {
		 out.writeInt(merchantId);
         out.writeTypedList(coupons);
         out.writeInt(includedResults);
         out.writeInt(totalResults);
     }

     public static final Parcelable.Creator<CouponContainer> CREATOR
             = new Parcelable.Creator<CouponContainer>() {
         public CouponContainer createFromParcel(Parcel in) {
             return new CouponContainer(in);
         }

         public CouponContainer[] newArray(int size) {
             return new CouponContainer[size];
         }
     };
}