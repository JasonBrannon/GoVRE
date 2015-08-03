package com.echo5bravo.govre.DAL;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import com.echo5bravo.govre.INFO.Vehicle;

public abstract class BusinessBaseVehicle{

	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = BusinessBaseVehicle.class.getSimpleName();
	
	Vehicle currentVehicle = new Vehicle();	
	static SQLiteFactoryAdapter dbAdapter;

	// DATABASE
	/** Keep track of context so that we can load SQL from string resources */
	private final Context myContext;

	public final Vehicle getCurrentVehicle()
	{
		return currentVehicle;
	}	
	
	public final void setCurrentPerson(Vehicle value)
	{
		currentVehicle = value;
	}

	public BusinessBaseVehicle(Context context) {
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
	 * Public Function: getAllVehicles()
	 * 
	 * @return  ArrayList<Vehicle> : All Vehicles
	 */
	public static synchronized ArrayList<Vehicle> getAllVehicles(Context context)
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
		String sql = "SELECT [vehicle_id]  " +
					"  ,[vehicle_label]  " +
					"  ,[vehicle_lat]  " +
					"  ,[vehicle_lon]  " +
					"  ,[trip_id]  " +
					"  ,[route_id]  " +
					"   ,[timestamp]  " +
					"   ,[stop_sequence]  " +
					"   ,[stop_id]  " +
					"  ,[current_status] " +
					"  FROM [vehicles] ORDER BY [timestamp] ASC;";
		
		//Open Database
		dbAdapter.openDataBase();
		//Log.e(TAG, "BusinessBaseVehicle: getAllVehicles() database OPEN");
		
		//Create SQLite Cursor
		Cursor cur = dbAdapter.selectRecordsFromDB(sql, null);	
		
		//Our container objects	
		ArrayList<Vehicle> curVehicleList = new ArrayList<Vehicle>();
		Vehicle currentVehicle = new Vehicle();

		//If anything returned, LOOP through cursor and populate our container objects
		try
		{
			while (cur.moveToNext())
			{	
				//Initiate local variables
				String _VEHICLE_ID = cur.getString(cur.getColumnIndex("vehicle_id"));
				String _VEHICLE_LABEL = cur.getString(cur.getColumnIndex("vehicle_label"));
				String _VEHICLE_LATITUDE = cur.getString(cur.getColumnIndex("vehicle_lat"));
				String _VEHCILE_LONGITUDE = cur.getString(cur.getColumnIndex("vehicle_lon"));
				String _TRIP_ID = cur.getString(cur.getColumnIndex("trip_id"));
				Integer _ROUTE_ID = cur.getInt(cur.getColumnIndex("route_id"));
				String _TIMESTAMP = cur.getString(cur.getColumnIndex("timestamp"));
				Integer _STOP_SEQUENCE = cur.getInt(cur.getColumnIndex("stop_sequence"));	
				String _STOP_ID = cur.getString(cur.getColumnIndex("stop_id"));		
				String _CURRENT_STATUS = cur.getString(cur.getColumnIndex("current_status"));	
				
				//Create new instance of object
				currentVehicle = new Vehicle();
				currentVehicle.VEHICLE_ID(_VEHICLE_ID);
				currentVehicle.VEHICLE_LABEL(_VEHICLE_LABEL);
				currentVehicle.VEHICLE_LATITUDE(_VEHICLE_LATITUDE);
				currentVehicle.VEHCILE_LONGITUDE(_VEHCILE_LONGITUDE);
				currentVehicle.TRIP_ID(_TRIP_ID);
				currentVehicle.ROUTE_ID(_ROUTE_ID);
				currentVehicle.TIMESTAMP(_TIMESTAMP);
				currentVehicle.STOP_SEQUENCE(_STOP_SEQUENCE);
				currentVehicle.STOP_ID(_STOP_ID);
				currentVehicle.CURRENT_STATUS(_CURRENT_STATUS);
				
				//Add newly populated object to ArrayList
				curVehicleList.add(currentVehicle);			
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
			//Log.e(TAG, "BusinessBaseVehicle: getAllVehicles() database CLOSED");
		}
		return curVehicleList;		
	}	
	
	/***
	 * Public Function: Insert(Context context)
	 */
	public static void Insert(Context context, ArrayList<Vehicle> vehicle) {

		//Create SQLFactoryAdapter instance			
		SQLiteFactoryAdapter dbAdapter = SQLiteFactoryAdapter.getSQLiteFactoryAdapterInstance(context);

		try {	
			
			//Open Database
			dbAdapter.openDataBase();
			//Log.e(TAG, "BusinessBaseVehicle: Insert() database OPEN");
			
			for (Vehicle each : vehicle) {	
				
				String _VEHICLE_ID = ((each.VEHICLE_ID() == null) ? "" : each.VEHICLE_ID());
				String _VEHICLE_LABEL = ((each.VEHICLE_LABEL() == null) ? "" : each.VEHICLE_LABEL());
				String _VEHICLE_LATITUDE = ((each.VEHICLE_LATITUDE() == null) ? "" : each.VEHICLE_LATITUDE());
				String _VEHCILE_LONGITUDE = ((each.VEHCILE_LONGITUDE() == null) ? "" : each.VEHCILE_LONGITUDE());
				String _TRIP_ID = ((each.TRIP_ID() == null) ? "" : each.TRIP_ID());
				Integer _ROUTE_ID = ((each.ROUTE_ID() == null) ? 0 : each.ROUTE_ID());
				String _TIMESTAMP = ((each.TIMESTAMP() == null) ? "" : each.TIMESTAMP());
				Integer _STOP_SEQUENCE = ((each.STOP_SEQUENCE() == null) ? 0 : each.STOP_SEQUENCE());
				String _STOP_ID = ((each.STOP_ID() == null) ? "" : each.STOP_ID());
				String _CURRENT_STATUS = ((each.CURRENT_STATUS() == null) ? "" : each.CURRENT_STATUS());
			
				//LOAD VALUES
				ContentValues cv = new ContentValues();
	
				cv.clear();
				cv.put("vehicle_id", _VEHICLE_ID);
				cv.put("vehicle_label", _VEHICLE_LABEL);
				cv.put("vehicle_lat", _VEHICLE_LATITUDE);	
				cv.put("vehicle_lon", _VEHCILE_LONGITUDE);
				cv.put("trip_id", _TRIP_ID);
				cv.put("route_id", _ROUTE_ID);				
				cv.put("timestamp", _TIMESTAMP);	
				cv.put("stop_sequence", _STOP_SEQUENCE);	
				cv.put("stop_id", _STOP_ID);
				cv.put("current_status", _CURRENT_STATUS);	
				
				//dbAdapter.deleteRecordInDB("vehicles", null, null);
				dbAdapter.insertRecordsInDB("vehicles", cv);
			
			}

		} catch (SQLiteException e) {
			//Log.d(TAG, "Insert Vehicle Database Error: " + e.toString());
		} catch (Exception ex) {
			//Log.d(TAG, "Insert Vehicle non-SQL Error: " + ex.toString());
		} finally {
			//House Keeping	
			dbAdapter.close();
			//Log.e(TAG, "BusinessBaseVehicle: Insert() database CLOSED");
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
			//Log.e(TAG, "BusinessBaseVehicle: DeleteAll() database OPEN");
			
			dbAdapter.deleteRecordInDB("vehicles", null, null);

		} catch (SQLiteException e) {
			//Log.d(TAG, "Delete Vehicle Database Error: " + e.toString());
		} catch (Exception ex) {
			//Log.d(TAG, "Delete Vehicle non-SQL Error: " + ex.toString());
		} finally {
			//House Keeping	
			dbAdapter.close();
			//Log.e(TAG, "BusinessBaseVehicle: DeleteAll() database CLOSED");
		}	
	}
}

