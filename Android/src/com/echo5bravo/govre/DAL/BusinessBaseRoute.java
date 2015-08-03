package com.echo5bravo.govre.DAL;

import java.util.ArrayList;

import com.echo5bravo.govre.INFO.Route;
import android.content.Context;
import android.database.Cursor;

public abstract class BusinessBaseRoute{

	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = BusinessBaseRoute.class.getSimpleName();
	
	Route currentRoute = new Route();	
	static SQLiteFactoryAdapter dbAdapter;

	// DATABASE
	/** Keep track of context so that we can load SQL from string resources */
	private final Context myContext;

	public final Route getCurrentRoute()
	{
		return currentRoute;
	}	
	
	public final void setCurrentPerson(Route value)
	{
		currentRoute = value;
	}

	public BusinessBaseRoute(Context context) {
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
	 * Public Function: getAllRoutes()
	 * 
	 * @return  ArrayList<Route> : All Routes
	 */
	public static synchronized ArrayList<Route> getAllRoutes(Context context, Integer RouteId)
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
		String sql = "SELECT   " +    
					"  routes.route_id  " +
					" , routes.agency_id  " +
					" , routes.route_short_name  " +
					" , routes.route_long_name  " +
					" , routes.route_desc   " +
					" , routes.route_type   " +
					" , routes.route_url   " +
					" , routes.route_color  " +
					" , routes.route_text_color  " +
					" , shapes.shape_pt_lat " +
					" , shapes.shape_pt_lon " +
					" , shapes.shape_pt_sequence " +
					" , shapes.shape_dist_traveled " +					
			" FROM     " +			      
			" routes JOIN  " +			
			" shapes ON routes.route_id = shapes.shape_id  " +			 
			" WHERE   " +			
			" routes.route_id = " + RouteId + " " +			
			" ORDER BY   " +			
			" shape_pt_sequence ASC  ";			
		
		//Open Database
		dbAdapter.openDataBase();
		//Log.e(TAG, "BusinessBaseRoute: getAllRoutes() database OPEN");
		
		//Create SQLite Cursor
		Cursor cur = dbAdapter.selectRecordsFromDB(sql, null);	
		
		//Our container objects	
		ArrayList<Route> curRouteList = new ArrayList<Route>();
		Route currentRoute = new Route();

		//If anything returned, LOOP through cursor and populate our container objects
		try
		{
			while (cur.moveToNext())
			{	
				//Initiate local variables
				Integer _ROUTE_ID = cur.getInt(cur.getColumnIndex("route_id"));
				String _ROUTE_AGENCY_ID = cur.getString(cur.getColumnIndex("agency_id"));
				String _ROUTE_SHORT_NAME = cur.getString(cur.getColumnIndex("route_short_name"));
				String _ROUTE_LONG_NAME = cur.getString(cur.getColumnIndex("route_long_name"));
				String _ROUTE_DESC = cur.getString(cur.getColumnIndex("route_desc"));
				Integer _ROUTE_TYPE = cur.getInt(cur.getColumnIndex("route_type"));
				String _ROUTE_URL = cur.getString(cur.getColumnIndex("route_url"));
				String _ROUTE_COLOR = cur.getString(cur.getColumnIndex("route_color"));	
				String _ROUTE_TEXT_COLOR = cur.getString(cur.getColumnIndex("route_text_color"));				
				String _ROUTE_LATITUDE = cur.getString(cur.getColumnIndex("shape_pt_lat"));
				String _ROUTE_LONGITUDE = cur.getString(cur.getColumnIndex("shape_pt_lon"));
				Integer _ROUTE_SEQUENCE = cur.getInt(cur.getColumnIndex("shape_pt_sequence"));
				String _ROUTE_DIST_TRAVELED = cur.getString(cur.getColumnIndex("shape_dist_traveled"));	
				
				//Create new instance of object
				currentRoute = new Route();
				currentRoute.ROUTE_ID(_ROUTE_ID);
				currentRoute.ROUTE_AGENCY_ID(_ROUTE_AGENCY_ID);
				currentRoute.ROUTE_SHORT_NAME(_ROUTE_SHORT_NAME);
				currentRoute.ROUTE_LONG_NAME(_ROUTE_LONG_NAME);
				currentRoute.ROUTE_DESC(_ROUTE_DESC);
				currentRoute.ROUTE_TYPE(_ROUTE_TYPE);
				currentRoute.ROUTE_URL(_ROUTE_URL);
				currentRoute.ROUTE_COLOR(_ROUTE_COLOR);
				currentRoute.ROUTE_TEXT_COLOR(_ROUTE_TEXT_COLOR);				
				currentRoute.ROUTE_LATITUDE(_ROUTE_LATITUDE);
				currentRoute.ROUTE_LONGITUDE(_ROUTE_LONGITUDE);
				currentRoute.ROUTE_SEQUENCE(_ROUTE_SEQUENCE);
				currentRoute.ROUTE_DIST_TRAVELED(_ROUTE_DIST_TRAVELED);
				
				//Add newly populated object to ArrayList
				curRouteList.add(currentRoute);			
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
			//Log.e(TAG, "BusinessBaseRoute: getAllRoutes() database CLOSED");
		}
		return curRouteList;		
	}	
}

