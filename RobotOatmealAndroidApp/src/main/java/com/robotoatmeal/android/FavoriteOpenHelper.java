package com.robotoatmeal.android;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "RobotOatmealFavorites";
    private static final int DATABASE_VERSION = 1;
    private static final String FAVORITE_TABLE_NAME = "favorite";
    private static final String ID = "ID";
    private static final String NAME = "NAME";
    private static final String FAVORITE = "FAVORITE";
    private static final String FAVORITE_TABLE_CREATE =
                "CREATE TABLE " + FAVORITE_TABLE_NAME + " (" +
                ID + " INT PRIMARY KEY NOT NULL, " +
                NAME + " TEXT NOT NULL, " +
                FAVORITE + " INT);";

    FavoriteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FAVORITE_TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isFavorite(int merchantId) {
		SQLiteDatabase database = getReadableDatabase();
		Cursor cursor = database.query(FAVORITE_TABLE_NAME, 
				new String[] {FAVORITE}, 
				ID + " = " + merchantId + " AND " +
				FAVORITE + " = " + 1, null, null, null, null);
		boolean ret = cursor.getCount() == 1;
		cursor.close();
		return ret;
	}
	
	public void toggleFavorite(int merchantId, String merchantName) {
		SQLiteDatabase database = getWritableDatabase();
		Cursor cursor = database.query(FAVORITE_TABLE_NAME, 
				new String[] {FAVORITE}, 
				ID + " = " + merchantId, null, null, null, null);
		
		ContentValues values = new ContentValues();
		if (cursor.getCount() == 0) {
			values.put(ID, merchantId);
			values.put(NAME, merchantName);
			values.put(FAVORITE, 1);
			database.insert(FAVORITE_TABLE_NAME, null, values);
		} else {
			cursor.moveToFirst();
			if (cursor.getInt(cursor.getColumnIndex(FAVORITE)) == 0)
				values.put(FAVORITE, 1);
			else {
				values.put(FAVORITE, 0);
			}
			database.update(FAVORITE_TABLE_NAME, values, ID + " = " + merchantId, 
					null);
		}
		cursor.close();
	}
	
	public ArrayList<Merchant> getFavoriteMerchants() {
	    SQLiteDatabase database = getReadableDatabase();
	    Cursor cursor = database.query(
	    		FAVORITE_TABLE_NAME, null, 
	    		FAVORITE + " = 1", null, null, null, null);
	
	    ArrayList<Merchant> favorites = new ArrayList<Merchant>();
	    while (cursor.moveToNext()) {
	    	Merchant merchant = new Merchant();
	    	merchant.id = cursor.getInt(
	    			cursor.getColumnIndex(FavoriteOpenHelper.ID));
	    	merchant.name = cursor.getString(
	    			cursor.getColumnIndex(FavoriteOpenHelper.NAME));
	    	favorites.add(merchant);
	    }
	    cursor.close();
	    return favorites;
	}
	
}
