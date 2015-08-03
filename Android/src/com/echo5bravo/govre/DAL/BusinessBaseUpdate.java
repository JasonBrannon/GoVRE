package com.echo5bravo.govre.DAL;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.echo5bravo.govre.INFO.Update;


public abstract class BusinessBaseUpdate{

	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = BusinessBaseUpdate.class.getSimpleName();
	
	Update currentUpdate = new Update();	
	static SQLiteFactoryAdapter dbAdapter;

	// DATABASE
	/** Keep track of context so that we can load SQL from string resources */
	private final Context myContext;

	public final Update getCurrentUpdate()
	{
		return currentUpdate;
	}	
	
	public final void setCurrentPerson(Update value)
	{
		currentUpdate = value;
	}

	public BusinessBaseUpdate(Context context) {
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
	 * Public Function: getAllUpdates()
	 * 
	 * @return  ArrayList<Update> : All Updates
	 */
	public static synchronized ArrayList<Update> getAllUpdates(Context context)
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
		String sql = "SELECT [trip_id]  " +
				"   ,[trip_schedule_relationship]  " +
				"   ,[route_id]  " +
				"   ,[vehicle_id]  " +
				"    ,[vehicle_label]  " +
				"    ,[stop_sequence]  " +
				"    ,[stop_id]  " +
				"    ,[stop_schedule_relationship]  " +
				"    ,[delay]  " +
				"    ,[time]  " +
				"  FROM [updates]  ORDER BY [time] ASC;";
		
		//Open Database
		dbAdapter.openDataBase();
		//Log.e(TAG, "BusinessBaseUpdate: getAllUpdates() database OPEN");
		
		//Create SQLite Cursor
		Cursor cur = dbAdapter.selectRecordsFromDB(sql, null);	
		
		//Our container objects	
		ArrayList<Update> curUpdateList = new ArrayList<Update>();
		Update currentUpdate = new Update();

		//If anything returned, LOOP through cursor and populate our container objects
		try
		{
			while (cur.moveToNext())
			{	
				//Initiate local variables
				String _TRIP_ID = cur.getString(cur.getColumnIndex("trip_id"));
				String _TRIP_SCHED_RELATION = cur.getString(cur.getColumnIndex("trip_schedule_relationship"));
				Integer _ROUTE_ID = cur.getInt(cur.getColumnIndex("route_id"));
				String _VEHICLE_ID = cur.getString(cur.getColumnIndex("vehicle_id"));
				String _VEHICLE_LABEL = cur.getString(cur.getColumnIndex("vehicle_label"));
				Integer _STOP_SEQUENCE = cur.getInt(cur.getColumnIndex("stop_sequence"));
				String _STOP_ID = cur.getString(cur.getColumnIndex("stop_id"));
				String _STOP_SCHED_RELATION = cur.getString(cur.getColumnIndex("stop_schedule_relationship"));	
				Integer _DELAY = cur.getInt(cur.getColumnIndex("delay"));		
				String _TIME = cur.getString(cur.getColumnIndex("time"));	
				
				//Create new instance of object
				currentUpdate = new Update();
				currentUpdate.TRIP_ID(_TRIP_ID);
				currentUpdate.TRIP_SCHED_RELATION(_TRIP_SCHED_RELATION);
				currentUpdate.ROUTE_ID(_ROUTE_ID);
				currentUpdate.VEHICLE_ID(_VEHICLE_ID);
				currentUpdate.VEHICLE_LABEL(_VEHICLE_LABEL);
				currentUpdate.STOP_SEQUENCE(_STOP_SEQUENCE);
				currentUpdate.STOP_ID(_STOP_ID);
				currentUpdate.STOP_SCHED_RELATION(_STOP_SCHED_RELATION);
				currentUpdate.DELAY(_DELAY);
				currentUpdate.TIME(_TIME);
				
				//Add newly populated object to ArrayList
				curUpdateList.add(currentUpdate);			
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
			//Log.e(TAG, "BusinessBaseUpdate: getAllUpdates() database CLOSED");
		}
		return curUpdateList;		
	}	
	
	/***
	 * Public Function: Insert(Context context)
	 */
	public static void Insert(Context context, ArrayList<Update> Update) {

		//Create SQLFactoryAdapter instance			
		SQLiteFactoryAdapter dbAdapter = SQLiteFactoryAdapter.getSQLiteFactoryAdapterInstance(context);

		try {	
			
			//Open Database
			dbAdapter.openDataBase();
			//Log.e(TAG, "BusinessBaseUpdate: Insert() database OPEN");
			
			for (Update each : Update) {	
				
				String	_TRIP_ID = ((each.TRIP_ID() == null) ? "" : each.TRIP_ID());
				String	_TRIP_SCHED_RELATION = ((each.TRIP_SCHED_RELATION() == null) ? "" : each.TRIP_SCHED_RELATION());
				Integer	_ROUTE_ID = ((each.ROUTE_ID() == null) ? 0 : each.ROUTE_ID());
				String	_VEHICLE_ID = ((each.VEHICLE_ID() == null) ? "" : each.VEHICLE_ID());
				String	_VEHICLE_LABEL = ((each.VEHICLE_LABEL() == null) ? "" : each.VEHICLE_LABEL());
				Integer	_STOP_SEQUENCE = ((each.STOP_SEQUENCE() == null) ? 0 : each.STOP_SEQUENCE());
				String	_STOP_ID = ((each.STOP_ID() == null) ? "" : each.STOP_ID());
				String	_STOP_SCHED_RELATION = ((each.STOP_SCHED_RELATION() == null) ? "" : each.STOP_SCHED_RELATION());
				Integer	_DELAY = ((each.DELAY() == null) ? 0 : each.DELAY());
				String	_TIME = ((each.TIME() == null) ? "" : each.TIME());

				
				//LOAD VALUES
				ContentValues cv = new ContentValues();
	
				cv.clear();
				cv.put("trip_id", _TRIP_ID);
				cv.put("trip_schedule_relationship", _TRIP_SCHED_RELATION);
				cv.put("route_id", _ROUTE_ID);	
				cv.put("vehicle_id", _VEHICLE_ID);
				cv.put("vehicle_label", _VEHICLE_LABEL);
				cv.put("stop_sequence", _STOP_SEQUENCE);				
				cv.put("stop_id", _STOP_ID);	
				cv.put("stop_schedule_relationship", _STOP_SCHED_RELATION);	
				cv.put("delay", _DELAY);	
				cv.put("time", _TIME);	
				
				//dbAdapter.deleteRecordInDB("Updates", null, null);
				dbAdapter.insertRecordsInDB("Updates", cv);
			
			}

		} catch (SQLiteException e) {
			//Log.d(TAG, "Insert Update Database Error: " + e.toString());
		} catch (Exception ex) {
			//Log.d(TAG, "Insert Update non-SQL Error: " + ex.toString());
		} finally {
			//House Keeping	
			dbAdapter.close();
			//Log.e(TAG, "BusinessBaseUpdate: Insert() database CLOSED");
		}	
	}
	
	/***
	 * Public Function: DeleteAll(Context context)
	 */
	public static void DeleteAll(Context context) {

		//Create SQLFactoryAdapter instance			
		SQLiteFactoryAdapter dbAdapter = SQLiteFactoryAdapter.getSQLiteFactoryAdapterInstance(context);

		try {			
			
			//Open Database
			dbAdapter.openDataBase();
			//Log.e(TAG, "BusinessBaseUpdate: DeleteAll() database OPEN");
			
			dbAdapter.deleteRecordInDB("Updates", null, null);

		} catch (SQLiteException e) {
			//Log.d(TAG, "Delete Update Database Error: " + e.toString());
		} catch (Exception ex) {
			//Log.d(TAG, "Delete Update non-SQL Error: " + ex.toString());
		} finally {
			//House Keeping	
			dbAdapter.close();
			//Log.e(TAG, "BusinessBaseUpdate: DeleteAll() database CLOSED");
		}	
	}
}


