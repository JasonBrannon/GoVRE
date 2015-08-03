package com.echo5bravo.govre.BLL;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import com.echo5bravo.govre.DAL.BusinessBaseStation;
import com.echo5bravo.govre.INFO.Schedule;
import com.echo5bravo.govre.INFO.Station;
import com.echo5bravo.govre.INFO.Update;
import com.echo5bravo.govre.INFO.Vehicle;

public class BusinessStation extends BusinessBaseStation {
	
	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = BusinessBaseStation.class.getSimpleName();
	
	/* Holds all the defined Stations: gets populated on first access. */
    private static ArrayList<Station> allStations;    
    
	public static final String KEY_NAME = "NAME";
	public static final String KEY_LONGITUDE = "LONGITUDE";
	public static final String KEY_LATITUDE = "LATITUDE"; 
	
	private static String mStationSortOrder;
	
	//CONSTRUCTORS
	public BusinessStation(Context context) {
		super(context);	
	}
	
	//METHODS	
	/**
	* Return all VRE Stations, Always check for NULL allStations because 
	* this data is being queried from SQLite.
	*/
	public static ArrayList<Station> getAllStations(Location locus, Context context) 
	{ 
		try{
			@SuppressWarnings("unused")
			ArrayList<Station> allStations; 
			return allStations = getAllStationsInMemory(locus, context); 
		}
		catch (Exception e)
		{
			e.toString();
		}
		return null;
	}
	
	/**
	* Return all VRE Stations, Always check for NULL allStations because 
	* this data is being queried from SQLite.
	*/
	public static ArrayList<Station> getAllStations(Context context) { 
		try{
			@SuppressWarnings("unused")
			ArrayList<Station> allStations; 
			return allStations = getAllStationsInMemory(context); 
		}
		catch (Exception e)
		{
			e.toString();
		}
		return null;
	}
	
	/**
	 * Return all VRE stations, Always check for NULL allStations because
	 * this data is being queried from SQLite.
	 */
	public static ArrayList<Station> getAllStationsInMemory(Location locus, Context context) {
		
	 	if (allStations == null)
	 	{
	 		allStations = BusinessBaseStation.getAllStations(context);	
	 	}
			
		// Refresh the distance and bearing settings for each station.
	    if (locus != null){
	        for (Station station : allStations)
	        	station.calculateDistance(locus); 			                
		} 	     
	    return allStations;
	 }
	
	/**
	 * Return all VRE stations, Always check for NULL allStations because
	 * this data is being queried from SQLite.
	 */
	public static ArrayList<Station> getAllStationsInMemory(Context context) {
	 	
	 	if (allStations == null)
	 	{
	 		allStations = BusinessBaseStation.getAllStations(context); 
	 	}
     
	    return allStations;
	 }
	
	   /**
	   * Return an array of all stations <Station>  that are within the given bearing from the specified location
	   * and at least the given minimum distance.
	   */ 
		
		//TODO: Add a new overloaded fetchAllStationsAtBearing method that has logic that calculates the estimated schedule, then add a new
		//      method call from ActivityStationList that calls the overload from a timer on a threaded or runnable tread (research that) that
		//      will be called every x minutes.  This overload will be used to maintain state of the ListView while updating the calculated
		// 		Next Departure times.
		//
		//		Once the Next Departure Times are working, Add jSoup PageScrap to bring it alerts and updates to the Departure Times.
	public static Station[] fetchAllStationsAtBearing(Location locus
														, int bearing
														, int minDistance
														, String lineFilter
														, Context context
														, boolean blnNextDepartureTime
														, String lineSortOrder
														, ArrayList<Station> stationStops
														, ArrayList<Schedule> schedules
														, String todaysScheduleType
														, ArrayList<Vehicle> vehicles
														, ArrayList<Update> updates
														, Boolean blnCalledByTimer
														, String timeformat) {	
			Schedule schedule = null; 
        		    	
	    	//Station Collection to inject custom information
	    	ArrayList<Station> inView = new ArrayList<Station>(); 
	    	
	    	String arrivalMSG = "";
	    	
	    	try{	    		
	    		if (blnCalledByTimer){   
			    	for (Station station : stationStops) { 
			    		if (lineFilter.contains(station.STATION_LINES())){
				    		schedule = BusinessSchedule.fetchStationsNextDeparture(context, schedules, station.STATION_ID(), 
				    														todaysScheduleType, vehicles, updates, lineFilter, timeformat);					    		
				    		if (!(schedule==null)){
				    			
				    			if (station.STATION_ID().equals(schedule.SCHEDULE_STOP_ID())){
				    				station.MSG_ALERT(schedule.MSG_ALERT());
				    				
				    				if (!(schedule.SCHEDULE_ARRIVAL_TIME_ACTUAL() == null))
				    					arrivalMSG = schedule.SCHEDULE_ARRIVAL_TIME_ACTUAL() + " - " + schedule.MSG_ESTIMATED_TIME_ARRIVAL();
				    				else
				    					arrivalMSG = schedule.MSG_ESTIMATED_TIME_ARRIVAL();
				    				
				    				station.MSG_NEXT_DEPARTURE(arrivalMSG);
				    			}			    		    				
				    		}	
				    		inView.add(station);
			    		} 
			    	}
	    		}
	    		else
	    		{
	    			// Refresh the distance and bearing settings for each station.
	    		    for (Station station : stationStops){   
				    	if (lineFilter.contains(station.STATION_LINES())){ 
		    		       	station.calculateDistance(locus); 
		    		       	inView.add(station);
	    		       	}
	    		    }
	    		}
	    	}
			catch(Exception ex)
			{
				//Log.d(TAG, "fetchAllStationsAtBearing " + ex.toString());   
			}
	    	
	    	//Set Local Variables
	    	mStationSortOrder = lineSortOrder;
	    	  
	    	//Display using the nearest station within range of the device
	    	if (mStationSortOrder.equals("proximity")){
	    		
	    		 //Sort by closest proximity.    	
	    		Collections.sort(inView);
	    	}
	    	else
	    	{
		    	final Comparator<Station> STATION_ORDER = new Comparator<Station>() {
					public int compare(Station s1, Station s2) {				
						
						//Display using the physical stopping order of each train station
						if (mStationSortOrder.equals("order")){
							
							Calendar calendar = new GregorianCalendar(); 
							
							//Determine if the time of day is AM or PM
							// * If AM sort by SOUTH to NORTH
							// * If PM sort by NORTH to SOUTH
							 
							if (calendar.get(Calendar.AM_PM) == 0){
								
								 //Sort Order: STATION SORT ORDER (NORTH) 
								return s2.SORT_ORDER() - s1.SORT_ORDER();  
							}
							else
							{
								 //Sort Order: STATION SORT ORDER (SOUTH) 
								return s2.SORT_ORDER() + s1.SORT_ORDER();
							}					 
						}
						 //Display using simple ASC Station Name order 
						else if (mStationSortOrder.equals("name")){
								
							 //Sort Order: STATION_NAME ASC 
							return s1.STATION_STOP_NAME().compareTo(s2.STATION_STOP_NAME());
						}	
						
						return 0;		
					}			
		    	};		    	
		    	// Sort by specified order or name using Comparator class collection.    	
		    	Collections.sort(inView, STATION_ORDER);
	    	}	    	
	    	// Convert them into an array so we can pass directly to the list view.
	    	Station[] results = inView.toArray(new Station[inView.size()]);
	    	return results; 
		}
}

