package com.robotoatmeal.android;

import java.util.List;
import android.os.Parcel;
import android.os.Parcelable;

public class CouponContainer implements Parcelable
{
	int merchantId;
	List<Coupon> couponType;
	int includedResults;
	int totalResults;
	
	@Override
	public int describeContents() {
	    return 0;
	}
	
	@Override
     public void writeToParcel(Parcel out, int flags) {
		 out.writeInt(merchantId);
         out.writeList(couponType);
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
     
     private CouponContainer(Parcel in) 
     {
    	 merchantId = in.readInt();
         in.readList(couponType, null);
         includedResults = in.readInt();
         totalResults = in.readInt();   
     }
}