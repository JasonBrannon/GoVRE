package com.echo5bravo.govre.DAL;

import java.util.ArrayList;

import com.echo5bravo.govre.INFO.CalDates;
import com.echo5bravo.govre.INFO.GtfsUpdateTracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;


public class BusinessBaseGtfsUpdateTracker {

	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = BusinessBaseGtfsUpdateTracker.class.getSimpleName();
	
	GtfsUpdateTracker currentGtfsUpdateTracker = new GtfsUpdateTracker();	
	static SQLiteFactoryAdapter dbAdapter;

	// DATABASE
	/** Keep track of context so that we can load SQL from string resources */
	private final Context myContext;

	public final GtfsUpdateTracker getCurrentGtfsUpdateTracker()
	{
		return currentGtfsUpdateTracker;
	}	
	
	public final void setCurrentGtfsUpdateTracker(GtfsUpdateTracker value)
	{
		currentGtfsUpdateTracker = value;
	}

	public BusinessBaseGtfsUpdateTracker(Context context) {
		this.myContext = context;
		
		SQLiteFactoryAdapter dbAdapter = SQLiteFactoryAdapter.getSQLiteFactoryAdapterInstance(myContext);
		try
		{
			dbAdapter.createOrUseDataBase();
		}
		catch (Exception e) {
			//Log.e(TAG, "Error " + e.toString());
		}
	}	

	/***
	 * Public Function: getAllGtfsFeedUpdates()
	 * 
	 * @return  ArrayList<GtfsUpdateTracker> : All GtfsFeedUpdates
	 */
	public static synchronized ArrayList<GtfsUpdateTracker> getAllGtfsFeedUpdates(Context context)
	{	
		//Create SQLFactoryAdapter instance	
		SQLiteFactoryAdapter dbAdapter = SQLiteFactoryAdapter.getSQLiteFactoryAdapterInstance(context);
				
		//Logic to create or use the existing database
		try
		{	
			dbAdapter.createOrUseDataBase();
		}
		catch (Exception e) {
			//Log.e(TAG, "Error " + e.toString());
		}			
		
		//Populate SQL
		String sql = "SELECT FeedName  " +
				" ,LastUpdated  " +				
				" FROM   " +
				" GTFS_UPDATE_TRACKER;  ";
		
		//Open Database
		dbAdapter.openDataBase();
		//Log.e(TAG, "BusinessBaseGtfsUpdateTracker: getAllGtfsUpdateTrackers() database OPEN");
		
		//Create SQLite Cursor
		Cursor cur = dbAdapter.selectRecordsFromDB(sql, null);	
		
		//Our container objects	
		ArrayList<GtfsUpdateTracker> curGtfsUpdateTrackerList = new ArrayList<GtfsUpdateTracker>();
		GtfsUpdateTracker currentGtfsUpdateTracker = new GtfsUpdateTracker();

		//If anything returned, LOOP through cursor and populate our container objects
		try
		{
			while (cur.moveToNext())
			{	
				//Initiate local variables
				String _FeedName = cur.getString(cur.getColumnIndex("FeedName"));
				String _LastUpdated = cur.getString(cur.getColumnIndex("LastUpdated"));
					
				
				//Create new instance of object
				currentGtfsUpdateTracker = new GtfsUpdateTracker();
				currentGtfsUpdateTracker.FeedName(_FeedName);
				currentGtfsUpdateTracker.LastUpdated(_LastUpdated);				
				
				//Add newly populated object to ArrayList
				curGtfsUpdateTrackerList.add(currentGtfsUpdateTracker);			
			}
		}
		catch (Exception e) {
			//Log.e(TAG, "Error " + e.toString());
		}
		finally{
			//House Keeping
			cur.close();
			cur.deactivate();
			dbAdapter.close();	
			//Log.e(TAG, "BusinessBaseGtfsUpdateTracker: getAllGtfsFeedUpdates() database CLOSED");
		}
		return curGtfsUpdateTrackerList;		
	}
	
	/***
	 * Public Function: Update(Context context)
	 */
	public static void Update(Context context, GtfsUpdateTracker curGtfsUpdateTracker) {

		//Create SQLFactoryAdapter instance			
		SQLiteFactoryAdapter dbAdapter = SQLiteFactoryAdapter.getSQLiteFactoryAdapterInstance(context);
		
						
		//Logic to create or use the existing database
		try
		{	
			dbAdapter.createOrUseDataBase();
		}
		catch (Exception e) {
			//Log.e(TAG, "Error " + e.toString());
		}			

		try {	
			
			//Open Database
			dbAdapter.openDataBase();
			//Log.e(TAG, "BusinessBaseTrip: getAllTrips() database OPEN");
			
			String _FeedName = ((curGtfsUpdateTracker.FeedName() == null) ? "calendar_dates" : curGtfsUpdateTracker.FeedName());
			String _LastUpdated = ((curGtfsUpdateTracker.LastUpdated() == null) ? "" : curGtfsUpdateTracker.LastUpdated());
					
			ContentValues args = new ContentValues();
		    //args.put("FeedName", _FeedName);
		    args.put("LastUpdated", _LastUpdated);
		    String whereArgs[] = new String[1];
		    whereArgs[0] = "" + _FeedName;
		    dbAdapter.updateRecordsInDB("GTFS_UPDATE_TRACKER", args, "FeedName= ?", whereArgs);
			
		

		} catch (SQLiteException e) {
			//Log.d(TAG, "Update GTFS_FEED_TRACKER Update for: Calander_Dates Database Error: " + e.toString());
		} catch (Exception ex) {
			//Log.d(TAG, "Update GTFS_FEED_TRACKER Update for: Calander_Dates non-SQL Error: " + ex.toString());
		} finally {
			//House Keeping	
			dbAdapter.close();
			//Log.e(TAG, "GTFS_FEED_TRACKER Update for: Calander_Dates database CLOSED");
		}	
	}
}

