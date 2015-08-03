package com.echo5bravo.govre.DAL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.echo5bravo.govre.INFO.Alert;

public abstract class BusinessBaseAlert {
	
	// LOCAL VARIABLES AND PROPERTIES	
	private static final String TAG = BusinessBaseAlert.class.getSimpleName();
	
	static Alert currentAlert = new Alert();	
	static SQLiteFactoryAdapter dbAdapter;

	// DATABASE
	/** Keep track of context so that we can load SQL from string resources */
	private final Context myContext;

	public final Alert getCurrentAlerts()
	{
		return currentAlert;
	}	
	
	public static final void setAlert(Alert value)
	{
		currentAlert = value;
	}

	public BusinessBaseAlert(Context context) {
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
	 * Public Function: getAllAlerts()
	 * 
	 * @return  List<Alert> : All Persons
	 */
	public static synchronized ArrayList<Alert> getAllAlerts(Context context)
	{			
		//Create SQLFactoryAdapter instance			
		SQLiteFactoryAdapter dbAdapter = SQLiteFactoryAdapter.getSQLiteFactoryAdapterInstance(context);
					
				
		//Logic to create or use the existing database
		try
		{	
			dbAdapter.createOrUseDataBase();
			dbAdapter.close();
		}
		catch (Exception e) {
			//Log.e(TAG, "Error " + e.toString());
		}	
			
		//Populate SQL
		String sql = "SELECT "  
			+ "A.TRAIN_NUM "
			+ ",A.ALERT_MESSAGE "
			+ ",A.DT_UPDATED "		
			+ ",A.MINUTES_DELAY "
			+ ",A.TRAIN_DELAYED "	
			+ ",A.TRAIN_CANCELLED "	
			+ "FROM TRAIN_ALERTS A; ";
		
		//Open Database
		dbAdapter.openDataBase();
		//Log.e(TAG, "BusinessBaseAlert: getAllAlerts() database OPEN");
		
		//Create SQLite Cursor
		Cursor cur = dbAdapter.selectRecordsFromDB(sql, null);	
		
		//Our container objects	
		ArrayList<Alert> curAlertList = new ArrayList<Alert>();
		Alert currentAlert = new Alert();

		//If anything returned, LOOP through cursor and populate our container objects
		try
		{
			while (cur.moveToNext())
			{					
				//Initiate local variables
				String _ALERT_TRAIN_NUM = cur.getString(cur.getColumnIndex("TRAIN_NUM"));
				String _ALERT_MESSAGE = cur.getString(cur.getColumnIndex("ALERT_MESSAGE"));
				String _ALERT_DT_UPDATE = cur.getString(cur.getColumnIndex("DT_UPDATED"));
				String _ALERT_MINUTES_DELAY = cur.getString(cur.getColumnIndex("MINUTES_DELAY"));
				String _ALERT_TRAIN_DELAYED = cur.getString(cur.getColumnIndex("TRAIN_DELAYED"));
				String _ALERT_TRAIN_CANCELLED = cur.getString(cur.getColumnIndex("TRAIN_CANCELLED"));
				
				//Create new instance of object
				currentAlert = new Alert();
				currentAlert.setALERT_TRAIN_NUM(_ALERT_TRAIN_NUM);
				currentAlert.setALERT_MESSAGE(_ALERT_MESSAGE);
				currentAlert.setALERT_DT_UPDATE(_ALERT_DT_UPDATE);	
				currentAlert.setALERT_MINUTES_DELAY(_ALERT_MINUTES_DELAY);
				currentAlert.setALERT_TRAIN_DELAYED(Boolean.valueOf(_ALERT_TRAIN_DELAYED));
				currentAlert.setALERT_TRAIN_CANCELLED(Boolean.valueOf(_ALERT_TRAIN_CANCELLED));
				
				//Add newly populated object to ArrayList
				curAlertList.add(currentAlert);
			}	
			
		} catch (SQLiteException e) {
			//Log.d(TAG, "Select Alerts Database Error: " + e.toString());
		} catch (Exception ex) {
			//Log.d(TAG, "Select Alerts non-SQL Error: " + ex.toString());
		} finally {
			//House Keeping
			cur.close();
			cur.deactivate();
			dbAdapter.close(); 
			//Log.e(TAG, "BusinessBaseAlert: getAllAlerts() database CLOSED");
		}
		return curAlertList;
	}
	
	
	/***
	 * Public Function: Insert(Context context)
	 */
	public static void Insert(Context context, ArrayList<Alert> alerts) {

		//Create SQLFactoryAdapter instance			
		SQLiteFactoryAdapter dbAdapter = SQLiteFactoryAdapter.getSQLiteFactoryAdapterInstance(context);

		try {	
			
			//Open Database
			dbAdapter.openDataBase();
			//Log.e(TAG, "BusinessBaseAlert: Insert() database OPEN");
			
			for (Alert each : alerts) {	
			
				SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
				String locDate = s.format(new Date());			
			
				String _ALERT_TRAIN_NUM;
				_ALERT_TRAIN_NUM = ((each.getALERT_TRAIN_NUM() == null) ? "" : each.getALERT_TRAIN_NUM());
				
				String _ALERT_MESSAGE;
				_ALERT_MESSAGE = ((each.getALERT_MESSAGE() == null) ? "" : each.getALERT_MESSAGE());
				
				String _ALERT_DT_UPDATE;
				_ALERT_DT_UPDATE = locDate;	
				
				String _MINUTES_DELAY;
				_MINUTES_DELAY = ((each.getALERT_MINUTES_DELAY() == null) ? "" : each.getALERT_MINUTES_DELAY());
				
				String _TRAIN_DELAYED;
				_TRAIN_DELAYED = each.getALERT_TRAIN_DELAYED().toString();   	//SQLite does not support boolean datatypes
				
				
				String _TRAIN_CANCELLED;
				_TRAIN_CANCELLED = each.getALERT_TRAIN_CANCELLED().toString();	//SQLite does not support boolean datatypes
				
			
				//LOAD VALUES
				ContentValues cv = new ContentValues();
	
				cv.clear();
				cv.put("TRAIN_NUM", _ALERT_TRAIN_NUM);
				cv.put("ALERT_MESSAGE", _ALERT_MESSAGE);
				cv.put("DT_UPDATED", _ALERT_DT_UPDATE);	
				cv.put("MINUTES_DELAY", _MINUTES_DELAY);
				cv.put("TRAIN_DELAYED", _TRAIN_DELAYED);
				cv.put("TRAIN_CANCELLED", _TRAIN_CANCELLED);				
				
				dbAdapter.insertRecordsInDB("TRAIN_ALERTS", cv);
			
			}

		} catch (SQLiteException e) {
			//Log.d(TAG, "Insert Alerts Database Error: " + e.toString());
		} catch (Exception ex) {
			//Log.d(TAG, "Insert Alerts non-SQL Error: " + ex.toString());
		} finally {
			//House Keeping	
			dbAdapter.close();
			//Log.e(TAG, "BusinessBaseAlert: Insert() database CLOSED");
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
			//Log.e(TAG, "BusinessBaseAlert: DeleteAll() database OPEN");
			
			dbAdapter.deleteRecordInDB("TRAIN_ALERTS", null, null);

		} catch (SQLiteException e) {
			//Log.d(TAG, "Delete Alerts Database Error: " + e.toString());
		} catch (Exception ex) {
			//Log.d(TAG, "Delete Alerts non-SQL Error: " + ex.toString());
		} finally {
			//House Keeping	
			dbAdapter.close();
			//Log.e(TAG, "BusinessBaseAlert: DeleteAll() database CLOSED");
		}	
	}
}


