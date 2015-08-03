package com.echo5bravo.govre.BLL;

import android.content.Context;
import com.echo5bravo.govre.DAL.BusinessBaseTrip;
import com.echo5bravo.govre.INFO.Trip;
import java.util.ArrayList;

public class BusinessTrip extends BusinessBaseTrip {
	
	//CONSTRUCTORS
	public BusinessTrip(Context context) {
		super(context);	
	}
	
	//METHODS	
	/**
	* Return all VRE Trips, Always check for NULL allTrips because 
	* this data is being queried from SQLite.
	*/
	public static ArrayList<Trip> getAllTrips(Context context) 
	{
		try{
			@SuppressWarnings("unused")
			ArrayList<Trip> allTrips; 
			return allTrips = BusinessBaseTrip.getAllTrips(context); 
		}
		catch (Exception e)
		{
			e.toString();
		}
		return null;
	}		
	
}
