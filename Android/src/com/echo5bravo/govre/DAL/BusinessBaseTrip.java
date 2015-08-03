package com.echo5bravo.govre.DAL;

import java.util.ArrayList;

import com.echo5bravo.govre.INFO.Trip;
import android.content.Context;
import android.database.Cursor;

public abstract class BusinessBaseTrip{

	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = BusinessBaseTrip.class.getSimpleName();
	
	Trip currentTrip = new Trip();	
	static SQLiteFactoryAdapter dbAdapter;

	// DATABASE
	/** Keep track of context so that we can load SQL from string resources */
	private final Context myContext;

	public final Trip getCurrentTrip()
	{
		return currentTrip;
	}	
	
	public final void setCurrentPerson(Trip value)
	{
		currentTrip = value;
	}

	public BusinessBaseTrip(Context context) {
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
	 * Public Function: getAllTrips()
	 * 
	 * @return  ArrayList<Trip> : All Trips
	 */
	public static synchronized ArrayList<Trip> getAllTrips(Context context)
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
			String sql = "SELECT DISTINCT [route_id]  " +
				" ,[service_id]  " +
				" ,[trip_id]  " +
				" ,[trip_headsign]  " +
				" ,[trip_short_name]  " +
				" ,[direction_id]  " +
				" ,[block_id]  " +
				" ,[shape_id]  " +
				" ,CASE WHEN (SUBSTR([service_id], LENGTH([service_id])-1, 2) = '-S') THEN 'SPECIAL' ELSE 'REGULAR' END AS SCHEDULE_TYPE  " +
				" FROM trips; ";
		
		//Open Database
		dbAdapter.openDataBase();
		//Log.e(TAG, "BusinessBaseTrip: getAllTrips() database OPEN");
		
		//Create SQLite Cursor
		Cursor cur = dbAdapter.selectRecordsFromDB(sql, null);	
		
		//Our container objects	
		ArrayList<Trip> curTripList = new ArrayList<Trip>();
		Trip currentTrip = new Trip();

		//If anything returned, LOOP through cursor and populate our container objects
		try
		{
			while (cur.moveToNext())
			{	
				//Initiate local variables
				Integer _ROUTE_ID = cur.getInt(cur.getColumnIndex("route_id"));
				String _SERVICE_ID = cur.getString(cur.getColumnIndex("service_id"));
				String _TRIP_ID = cur.getString(cur.getColumnIndex("trip_id"));
				String _TRIP_HEADSIGN = cur.getString(cur.getColumnIndex("trip_headsign"));
				String _TRIP_SHORT_NAME = cur.getString(cur.getColumnIndex("trip_short_name"));
				Integer _TRIP_DIRECTION_ID = cur.getInt(cur.getColumnIndex("direction_id"));
				String _TRIP_BLOCK_ID = cur.getString(cur.getColumnIndex("block_id"));
				Integer _TRIP_SHAPE_ID = cur.getInt(cur.getColumnIndex("shape_id"));	
				String _TRIP_SCHEDULE_TYPE = cur.getString(cur.getColumnIndex("SCHEDULE_TYPE"));							
				
				//Create new instance of object
				currentTrip = new Trip();
				currentTrip.ROUTE_ID(_ROUTE_ID);
				currentTrip.SERVICE_ID(_SERVICE_ID);
				currentTrip.TRIP_ID(_TRIP_ID);
				currentTrip.TRIP_HEADSIGN(_TRIP_HEADSIGN);
				currentTrip.TRIP_SHORT_NAME(_TRIP_SHORT_NAME);
				currentTrip.TRIP_DIRECTION_ID(_TRIP_DIRECTION_ID);
				currentTrip.TRIP_BLOCK_ID(_TRIP_BLOCK_ID);
				currentTrip.TRIP_SHAPE_ID(_TRIP_SHAPE_ID);
				currentTrip.TRIP_SCHEDULE_TYPE(_TRIP_SCHEDULE_TYPE);
				
				//Add newly populated object to ArrayList
				curTripList.add(currentTrip);			
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
			//Log.e(TAG, "BusinessBaseTrip: getAllTrips() database CLOSED");
		}
		return curTripList;		
	}	
}
