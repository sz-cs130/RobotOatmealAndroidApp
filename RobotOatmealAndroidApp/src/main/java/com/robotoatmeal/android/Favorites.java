package com.robotoatmeal.android;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Favorites extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "UserSettings";
    private static final int DATABASE_VERSION = 1;
    
    private static final String FAVORITE_TABLE_NAME = "Favorites";
    private static final String MERCHANT_ID = "merchantId";
    private static final String NAME = "name";
    private static final String LAST_UPDATED = "lastUpdated";
    private static final String FAVORITE_TABLE_CREATE =
    		"CREATE TABLE " + FAVORITE_TABLE_NAME + " (" +
    MERCHANT_ID + " INT PRIMARY KEY, " +
    				NAME + " TEXT, " +
    LAST_UPDATED + " TEXT);";
    
    private static final String CACHED_COUPONS_TABLE_NAME = "CachedCoupons";
    private static final String COUPON_CODE = "CouponCode";
    private static final String CACHED_COUPONS_TABLE_CREATE = 
    		"CREATE TABLE " + CACHED_COUPONS_TABLE_NAME + " (" +
    MERCHANT_ID + " INT, " +
    				COUPON_CODE + " TEXT, " +
    "FOREIGN KEY(" + MERCHANT_ID + ") REFERENCES " + FAVORITE_TABLE_NAME + "(" + MERCHANT_ID + "));";
    
    private HashMap<Integer, Merchant> favorites;
    private RobotOatmealState m_appState;

    Favorites(RobotOatmealState context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	m_appState = context;
    	load();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FAVORITE_TABLE_CREATE);
        db.execSQL(CACHED_COUPONS_TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public void load() {
	    SQLiteDatabase database = getReadableDatabase();
	    Cursor cursor = database.query(
	    		FAVORITE_TABLE_NAME, null, null, null, null, null, null);
	
	    favorites = new HashMap<Integer, Merchant>();
	    while (cursor.moveToNext()) {
	    	Merchant merchant = new Merchant();
	    	merchant.id = cursor.getInt(
	    			cursor.getColumnIndex(Favorites.MERCHANT_ID));
	    	merchant.name = cursor.getString(
	    			cursor.getColumnIndex(Favorites.NAME));
	    	favorites.put(merchant.id, merchant);
	    }
	    cursor.close();
	}
	
	public void add(int id) {
		if (!favorites.containsKey(id)) {
			Merchant merchant = new Merchant();
			merchant.id = id;
			merchant.name = m_appState.mappings.getMerchantName(id);
			favorites.put(merchant.id, merchant);

			SQLiteDatabase database = getWritableDatabase();
			ContentValues favorite_values = new ContentValues();
			favorite_values.put(MERCHANT_ID, merchant.id);
			favorite_values.put(NAME, merchant.name);
			favorite_values.put(LAST_UPDATED, Calendar.getInstance().toString());
			database.insert(FAVORITE_TABLE_NAME, null, favorite_values);
			
			ContentValues cached_coupon_values = new ContentValues();
			cached_coupon_values.put(MERCHANT_ID, merchant.id);
			for (Coupon coupon: m_appState.savedSearch.results.coupons) {
				cached_coupon_values.put(COUPON_CODE, coupon.couponCode);
				database.insert(CACHED_COUPONS_TABLE_NAME, null, cached_coupon_values);
			}
		}
	}
	
	public void delete(int id) {
		if (favorites.containsKey(id)) {
			favorites.remove(id);

			SQLiteDatabase database = getWritableDatabase();
			database.delete(CACHED_COUPONS_TABLE_NAME, MERCHANT_ID + " = ?", new String[] {id + ""});
			database.delete(FAVORITE_TABLE_NAME, MERCHANT_ID + " = ?", new String[] {id + ""});
		}
	}
	
	public Merchant get(int id)
	{
		if (favorites.containsKey(id))
			return favorites.get(id);
		return null;
	}
	
	public Merchant[] getall() {
		return (Merchant[]) favorites.values().toArray(new Merchant[favorites.size()]);
	}
	
	public HashSet<String> getCachedCoupons(int id) {
	    SQLiteDatabase database = getReadableDatabase();
	    Cursor cursor = database.query(
	    		CACHED_COUPONS_TABLE_NAME, null, MERCHANT_ID + " = " + id, null, null, null, null);
	
	    HashSet<String> cachedCoupons = new HashSet<String>();
	    while (cursor.moveToNext()) {
	    	cachedCoupons.add(cursor.getString(cursor.getColumnIndex(COUPON_CODE)));
	    }
	    cursor.close();
	    return cachedCoupons;
	}
	
}
