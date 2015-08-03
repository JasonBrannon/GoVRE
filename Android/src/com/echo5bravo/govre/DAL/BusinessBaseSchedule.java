package com.echo5bravo.govre.DAL;

import java.util.ArrayList;

import com.echo5bravo.govre.INFO.Schedule;
import android.content.Context;
import android.database.Cursor;

public abstract class BusinessBaseSchedule{

	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = BusinessBaseSchedule.class.getSimpleName();
	
	Schedule currentSchedule = new Schedule();	
	static SQLiteFactoryAdapter dbAdapter;

	// DATABASE
	/** Keep track of context so that we can load SQL from string resources */
	private final Context myContext;

	public final Schedule getCurrentSchedule()
	{
		return currentSchedule;
	}	
	
	public final void setCurrentPerson(Schedule value)
	{
		currentSchedule = value;
	}

	public BusinessBaseSchedule(Context context) {
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
	 * Public Function: getAllSchedules()
	 * 
	 * @return  ArrayList<Schedule> : All Schedules
	 */
	public static synchronized ArrayList<Schedule> getAllSchedules(Context context, String todaysScheduleType)
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
		String sql = "SELECT DISTINCT     " +  
				" trips.route_id  " +
				" , trips.service_id  " +  
				" , trips.trip_id  " +
				" , trips.trip_headsign  " +
				" , trips.trip_short_name  " +
				" , trips.direction_id  " +
				" , trips.block_id  " +
				" , trips.shape_id  " +
				" , CASE WHEN (SUBSTR([service_id], LENGTH([service_id])-1, 2) = '-S') THEN 'SPECIAL' ELSE 'REGULAR' END AS SCHEDULE_TYPE  " +
				" , stop_times.arrival_time  " +
				" , stop_times.departure_time  " +
				" , stop_times.stop_id  " +
				" , stop_times.stop_sequence  " +
				" , stop_times.stop_headsign  " +
				" , stop_times.pickup_type  " +
				" , stop_times.drop_off_type  " +
				" , stop_times.shape_dist_traveled  " +
				" FROM  " +
				"  trips INNER JOIN stop_times ON trips.trip_id = stop_times.trip_id " +
				"  WHERE SCHEDULE_TYPE = '" + todaysScheduleType + "' " +			
				" ORDER BY stop_times.arrival_time ASC; ";
		
		//Open Database
		dbAdapter.openDataBase();
		//Log.e(TAG, "BusinessBaseSchedule: getAllSchedules() database OPEN");
		
		//Create SQLite Cursor
		Cursor cur = dbAdapter.selectRecordsFromDB(sql, null);	
		
		//Our container objects	
		ArrayList<Schedule> curScheduleList = new ArrayList<Schedule>();
		Schedule currentSchedule = new Schedule();

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
				String _SCHEDULE_TYPE = cur.getString(cur.getColumnIndex("SCHEDULE_TYPE"));
				String _SCHEDULE_ARRIVAL_TIME = cur.getString(cur.getColumnIndex("arrival_time"));	
				String _SCHEDULE_DEPARTURE_TIME = cur.getString(cur.getColumnIndex("departure_time"));	
				String _SCHEDULE_STOP_ID = cur.getString(cur.getColumnIndex("stop_id"));	
				Integer _SCHEDULE_STOP_SEQUENCE = cur.getInt(cur.getColumnIndex("stop_sequence"));				
				String _SCHEDULE_STOP_HEADSIGN = cur.getString(cur.getColumnIndex("stop_headsign"));
				Integer _SCHEDULE_STOP_PICK_TYPE = cur.getInt(cur.getColumnIndex("pickup_type"));
				Integer _SCHEDULE_STOP_DROP_TYPE = cur.getInt(cur.getColumnIndex("drop_off_type"));
				String _SCHEDULE_STOP_DIST_TRAV = cur.getString(cur.getColumnIndex("shape_dist_traveled"));
				
				//Create new instance of object
				currentSchedule = new Schedule();
				currentSchedule.ROUTE_ID(_ROUTE_ID);
				currentSchedule.SERVICE_ID(_SERVICE_ID);
				currentSchedule.TRIP_ID(_TRIP_ID);
				currentSchedule.TRIP_HEADSIGN(_TRIP_HEADSIGN);
				currentSchedule.TRIP_SHORT_NAME(_TRIP_SHORT_NAME);
				currentSchedule.TRIP_DIRECTION_ID(_TRIP_DIRECTION_ID);
				currentSchedule.TRIP_BLOCK_ID(_TRIP_BLOCK_ID);
				currentSchedule.TRIP_SHAPE_ID(_TRIP_SHAPE_ID);
				currentSchedule.SCHEDULE_TYPE(_SCHEDULE_TYPE);
				currentSchedule.SCHEDULE_ARRIVAL_TIME(_SCHEDULE_ARRIVAL_TIME);
				currentSchedule.SCHEDULE_DEPARTURE_TIME(_SCHEDULE_DEPARTURE_TIME);
				currentSchedule.SCHEDULE_STOP_ID(_SCHEDULE_STOP_ID);
				currentSchedule.SCHEDULE_STOP_SEQUENCE(_SCHEDULE_STOP_SEQUENCE);
				currentSchedule.SCHEDULE_STOP_HEADSIGN(_SCHEDULE_STOP_HEADSIGN);
				currentSchedule.SCHEDULE_STOP_PICK_TYPE(_SCHEDULE_STOP_PICK_TYPE);
				currentSchedule.SCHEDULE_STOP_DROP_TYPE(_SCHEDULE_STOP_DROP_TYPE);
				currentSchedule.SCHEDULE_STOP_DIST_TRAV(_SCHEDULE_STOP_DIST_TRAV);			
				
				//Add newly populated object to ArrayList
				curScheduleList.add(currentSchedule);			
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
			//Log.e(TAG, "BusinessBaseSchedule: getAllSchedules() database CLOSED");
		}
		return curScheduleList;		
	}	
}


