package com.echo5bravo.govre.DAL;

import java.util.ArrayList;

import com.echo5bravo.govre.INFO.Agency;
import android.content.Context;
import android.database.Cursor;

public abstract class BusinessBaseAgency{

	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = BusinessBaseAgency.class.getSimpleName();
	
	Agency currentAgency = new Agency();	
	static SQLiteFactoryAdapter dbAdapter;

	// DATABASE
	/** Keep track of context so that we can load SQL from string resources */
	private final Context myContext;

	public final Agency getCurrentAgency()
	{
		return currentAgency;
	}	
	
	public final void setCurrentPerson(Agency value)
	{
		currentAgency = value;
	}

	public BusinessBaseAgency(Context context) {
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
	 * Public Function: getAllAgencies()
	 * 
	 * @return  ArrayList<Agency> : All Agencies
	 */
	public static synchronized ArrayList<Agency> getAllAgencies(Context context)
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
		String sql = "SELECT agency.agency_id  " +
				" ,agency.agency_name  " +
				" ,agency.agency_url  " +
				" ,agency.agency_timezone  " +
				" ,agency.agency_lang  " +
				" ,agency.agency_phone  " +
				" ,agency.agency_fare_url  " +
				" FROM   " +
				" agency;  ";
		
		//Open Database
		dbAdapter.openDataBase();
		//Log.e(TAG, "BusinessBaseAgency: getAllAgencys() database OPEN");
		
		//Create SQLite Cursor
		Cursor cur = dbAdapter.selectRecordsFromDB(sql, null);	
		
		//Our container objects	
		ArrayList<Agency> curAgencyList = new ArrayList<Agency>();
		Agency currentAgency = new Agency();

		//If anything returned, LOOP through cursor and populate our container objects
		try
		{
			while (cur.moveToNext())
			{	
				//Initiate local variables
				String _AGENCY_ID = cur.getString(cur.getColumnIndex("agency_id"));
				String _AGENCY_NAME = cur.getString(cur.getColumnIndex("agency_name"));
				String _AGENCY_URL = cur.getString(cur.getColumnIndex("agency_url"));
				String _AGENCY_TIMEZONE = cur.getString(cur.getColumnIndex("agency_timezone"));
				String _AGENCY_LANG = cur.getString(cur.getColumnIndex("agency_lang"));
				String _AGENCY_PHONE = cur.getString(cur.getColumnIndex("agency_phone"));
				String _AGENCY_FARE_URL = cur.getString(cur.getColumnIndex("agency_fare_url"));			
				
				//Create new instance of object
				currentAgency = new Agency();
				currentAgency.AGENCY_ID(_AGENCY_ID);
				currentAgency.AGENCY_NAME(_AGENCY_NAME);
				currentAgency.AGENCY_URL(_AGENCY_URL);
				currentAgency.AGENCY_TIMEZONE(_AGENCY_TIMEZONE);
				currentAgency.AGENCY_LANG(_AGENCY_LANG);
				currentAgency.AGENCY_PHONE(_AGENCY_PHONE);
				currentAgency.AGENCY_FARE_URL(_AGENCY_FARE_URL);
				
				//Add newly populated object to ArrayList
				curAgencyList.add(currentAgency);			
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
			//Log.e(TAG, "BusinessBaseAgency: getAllAgencies() database CLOSED");
		}
		return curAgencyList;		
	}	
}

