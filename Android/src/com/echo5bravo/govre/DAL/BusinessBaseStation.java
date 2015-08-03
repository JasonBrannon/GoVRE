package com.echo5bravo.govre.DAL;


import java.util.ArrayList;

import com.echo5bravo.govre.INFO.Station;
import android.content.Context;
import android.database.Cursor;

public abstract class BusinessBaseStation{

	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = BusinessBaseStation.class.getSimpleName();
	
	Station currentStation = new Station();	
	static SQLiteFactoryAdapter dbAdapter;

	// DATABASE
	/** Keep track of context so that we can load SQL from string resources */
	private final Context myContext;

	public final Station getCurrentStation()
	{
		return currentStation;
	}	
	
	public final void setCurrentPerson(Station value)
	{
		currentStation = value;
	}

	public BusinessBaseStation(Context context) {
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
	 * Public Function: getAllStations()
	 * 
	 * @return  ArrayList<Station> : All Stations
	 */
	public static synchronized ArrayList<Station> getAllStations(Context context)
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
		String sql = "SELECT DISTINCT  " +
				" stops.stop_id  " +
				" , stops.stop_code  " +
				" , stops.stop_name  " +
				" , stops.stop_desc  " +
				" , stops.stop_lat  " +
				" , stops.stop_lon  " +
				" , stops.zone_id  " +
				" , stops.stop_url  " +
				" , stops.location_type  " +
				" , stops.parent_station  " +
				" , stops.stop_timezone  " +
				" , GOVRE_STATION.ADDRESS  " +
				" , GOVRE_STATION.CITY  " +
				" , GOVRE_STATION.STATE  " +
				" , GOVRE_STATION.ZIP  " +
				" , GOVRE_STATION.URL  " +
				" , GOVRE_STATION.ZONE  " +
				" , GOVRE_STATION.SORT_ORDER  " +
				" , GOVRE_STATION.ABBREVIATION  " +
				" , group_concat(trips.shape_id) AS STATION_LINES " +
				" FROM  " +
				" stops LEFT OUTER JOIN  " +
				" GOVRE_STATION ON stops.stop_id = GOVRE_STATION.stop_id LEFT OUTER JOIN  " +
				" stop_times ON stops.stop_id = stop_times.stop_id LEFT OUTER JOIN  " +
				" trips ON stop_times.trip_id = trips.trip_id  " +
				" GROUP BY  " +     
				" stops.stop_id;  "; 
		
		//Open Database
		dbAdapter.openDataBase();
		//Log.e(TAG, "BusinessBaseStation: getAllStations() database OPEN");
		
		//Create SQLite Cursor
		Cursor cur = dbAdapter.selectRecordsFromDB(sql, null);	
		
		//Our container objects	
		ArrayList<Station> curStationList = new ArrayList<Station>();
		Station currentStation = new Station();

		//If anything returned, LOOP through cursor and populate our container objects
		try
		{
			while (cur.moveToNext())
			{	
				//Initiate local variables
				String _STATION_ID = cur.getString(cur.getColumnIndex("stop_id"));
				String _STATION_STOP_CODE = cur.getString(cur.getColumnIndex("stop_code"));
				String _STATION_STOP_NAME = cur.getString(cur.getColumnIndex("stop_name"));
				String _STATION_DESC = cur.getString(cur.getColumnIndex("stop_desc"));
				String _STATION_LATITUDE = cur.getString(cur.getColumnIndex("stop_lat"));
				String _STATION_LONGITUDE = cur.getString(cur.getColumnIndex("stop_lon"));
				String _STATION_ZONE_ID = cur.getString(cur.getColumnIndex("zone_id"));	
				String _STATION_URL = cur.getString(cur.getColumnIndex("stop_url"));	
				Integer _STATION_LOCATION_TYPE = cur.getInt(cur.getColumnIndex("location_type"));	
				String _STATION_PARENT_STATION = cur.getString(cur.getColumnIndex("parent_station"));	
				String _STATION_TIMEZONE = cur.getString(cur.getColumnIndex("stop_timezone"));	
				String _STATION_LINES = cur.getString(cur.getColumnIndex("STATION_LINES"));	
				
				//From GOVRE_STATION - Static Table Not Updated by GTFS
				String _ADDRESS = cur.getString(cur.getColumnIndex("ADDRESS"));	
				String _CITY = cur.getString(cur.getColumnIndex("CITY"));	
				String _STATE = cur.getString(cur.getColumnIndex("STATE"));	
				String _ZIP = cur.getString(cur.getColumnIndex("ZIP"));	
				String _URL = cur.getString(cur.getColumnIndex("URL"));	
				String _ZONE = cur.getString(cur.getColumnIndex("ZONE"));	
				Integer _SORT_ORDER = cur.getInt(cur.getColumnIndex("SORT_ORDER"));
				String _ABBREVIATION = cur.getString(cur.getColumnIndex("ABBREVIATION"));	
				
				
				//Create new instance of object
				currentStation = new Station();
				currentStation.STATION_ID(_STATION_ID);
				currentStation.STATION_STOP_CODE(_STATION_STOP_CODE);
				currentStation.STATION_STOP_NAME(_STATION_STOP_NAME);
				currentStation.STATION_DESC(_STATION_DESC);
				currentStation.STATION_LATITUDE(_STATION_LATITUDE);
				currentStation.STATION_LONGITUDE(_STATION_LONGITUDE);
				currentStation.STATION_ZONE_ID(_STATION_ZONE_ID);
				currentStation.STATION_URL(_STATION_URL);
				currentStation.STATION_LOCATION_TYPE(_STATION_LOCATION_TYPE);
				currentStation.STATION_PARENT_STATION(_STATION_PARENT_STATION);
				currentStation.STATION_TIMEZONE(_STATION_TIMEZONE);
				currentStation.STATION_LINES(_STATION_LINES);
				
				//From GOVRE_STATION - Static Table Not Updated by GTFS
				currentStation.ADDRESS(_ADDRESS);
				currentStation.CITY(_CITY);
				currentStation.STATE(_STATE);
				currentStation.ZIP(_ZIP);
				currentStation.URL(_URL);
				currentStation.ZONE(_ZONE);
				currentStation.SORT_ORDER(_SORT_ORDER);
				currentStation.ABBREVIATION(_ABBREVIATION);
				
				//Add newly populated object to ArrayList
				curStationList.add(currentStation);			
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
			//Log.e(TAG, "BusinessBaseStation: getAllStations() database CLOSED");
		}
		return curStationList;		
	}	
}



