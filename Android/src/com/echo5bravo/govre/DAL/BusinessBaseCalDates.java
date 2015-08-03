package com.echo5bravo.govre.DAL;

import java.util.ArrayList;

import com.echo5bravo.govre.INFO.CalDates;
import com.echo5bravo.govre.INFO.Vehicle;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

public abstract class BusinessBaseCalDates{

	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = BusinessBaseCalDates.class.getSimpleName();
	
	CalDates currentCalDates = new CalDates();	
	static SQLiteFactoryAdapter dbAdapter;

	// DATABASE
	/** Keep track of context so that we can load SQL from string resources */
	private final Context myContext;

	public final CalDates getCurrentCalDates()
	{
		return currentCalDates;
	}	
	
	public final void setCurrentPerson(CalDates value)
	{
		currentCalDates = value;
	}

	public BusinessBaseCalDates(Context context) {
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
	 * Public Function: getAllCalDates()
	 * 
	 * @return  ArrayList<CalDates> : All CalDatess
	 */
	public static synchronized ArrayList<CalDates> getAllCalDates(Context context)
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
		String sql = "SELECT service_id   " +  
				" ,[date]   " +  
				" ,exception_type   " +  
				" FROM calendar_dates; ";
		
		//Open Database
		dbAdapter.openDataBase();
		//Log.e(TAG, "BusinessBaseCalDates: getAllCalDates() database OPEN");
		
		//Create SQLite Cursor
		Cursor cur = dbAdapter.selectRecordsFromDB(sql, null);	
		
		//Our container objects	
		ArrayList<CalDates> curCalDatesList = new ArrayList<CalDates>();
		CalDates currentCalDates = new CalDates();

		//If anything returned, LOOP through cursor and populate our container objects
		try
		{
			while (cur.moveToNext())
			{	
				//Initiate local variables
				String _SERVICE_ID = cur.getString(cur.getColumnIndex("service_id"));
				String _CALDATES_DATE = cur.getString(cur.getColumnIndex("date"));
				Integer _CALDATES_EXCEPTION_TYPE = cur.getInt(cur.getColumnIndex("exception_type"));
				
				//Create new instance of object
				currentCalDates = new CalDates();
				currentCalDates.SERVICE_ID(_SERVICE_ID);	
				currentCalDates.CALDATES_DATE(_CALDATES_DATE);	
				currentCalDates.CALDATES_EXCEPTION_TYPE(_CALDATES_EXCEPTION_TYPE);
				
				//Add newly populated object to ArrayList
				curCalDatesList.add(currentCalDates);			
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
			//Log.e(TAG, "BusinessBaseCalDates: getAllCalDates() database CLOSED");
		}
		return curCalDatesList;		
	}
	
	/***
	 * Public Function: Insert(Context context)
	 */
	public static void Insert(Context context, ArrayList<CalDates> allCalDates) {

		//Create SQLFactoryAdapter instance			
		SQLiteFactoryAdapter dbAdapter = SQLiteFactoryAdapter.getSQLiteFactoryAdapterInstance(context);

		try {	
			
			//Open Database
			dbAdapter.openDataBase();
			//Log.e(TAG, "BusinessBaseVehicle: Insert() database OPEN");
			
			for (CalDates each : allCalDates) {	
				
								
				String _SERVICE_ID = ((each.SERVICE_ID() == null) ? "" : each.SERVICE_ID());
				String _CALDATES_DATE = ((each.CALDATES_DATE() == null) ? "" : each.CALDATES_DATE());
				Integer _CALDATES_EXCEPTION_TYPE = ((each.CALDATES_EXCEPTION_TYPE() == null) ? 0 : each.CALDATES_EXCEPTION_TYPE());
				
			
				//LOAD VALUES
				ContentValues cv = new ContentValues();
	
				cv.clear();
				cv.put("service_id", _SERVICE_ID);
				cv.put("date", _CALDATES_DATE);
				cv.put("exception_type", _CALDATES_EXCEPTION_TYPE);	
				
				//dbAdapter.deleteRecordInDB("vehicles", null, null);
				dbAdapter.insertRecordsInDB("calendar_dates", cv);
			
			}

		} catch (SQLiteException e) {
			//Log.d(TAG, "Insert Calander_Dates Database Error: " + e.toString());
		} catch (Exception ex) {
			//Log.d(TAG, "Insert Calander_Dates non-SQL Error: " + ex.toString());
		} finally {
			//House Keeping	
			dbAdapter.close();
			//Log.e(TAG, "BusinessBaseCalDates: Insert() database CLOSED");
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
			//Log.e(TAG, "BusinessBaseCalDates: DeleteAll() database OPEN");
			
			dbAdapter.deleteRecordInDB("calendar_dates", null, null);

		} catch (SQLiteException e) {
			//Log.d(TAG, "Delete Calendar Dates Database Error: " + e.toString());
		} catch (Exception ex) {
			//Log.d(TAG, "Delete Calendar Dates non-SQL Error: " + ex.toString());
		} finally {
			//House Keeping	
			dbAdapter.close();
			//Log.e(TAG, "BusinessBaseCalDates: DeleteAll() database CLOSED");
		}	
	}
}




