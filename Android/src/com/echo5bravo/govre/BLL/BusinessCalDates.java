package com.echo5bravo.govre.BLL;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.echo5bravo.govre.DAL.BusinessBaseCalDates;
import com.echo5bravo.govre.INFO.CalDates;
import com.echo5bravo.govre.INFO.GtfsUpdateTracker;
import com.echo5bravo.govre.INFO.Schedule;
import com.echo5bravo.govre.UTILS.Common;
import com.echo5bravo.govre.UTILS.ProxyNetworkGTFS;

import java.util.ArrayList;
import java.util.Calendar;

public class BusinessCalDates extends BusinessBaseCalDates {
	
	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = BusinessCalDates.class.getSimpleName();
	
	/* Holds all the defined CalDates: gets populated on first access. */
    private static ArrayList<CalDates> allCalDates; 
	
	//CONSTRUCTORS
	public BusinessCalDates(Context context) {
		super(context);	
	}	
	
	//METHODS	
	/**
	* Return all VRE CalDatess, Always check for NULL allCalDatess because 
	* this data is being queried from SQLite.
	*/
	public static ArrayList<CalDates> getAllCalDates(Context context) 
	{ 
		try{
					
			return  getAllCalDatesInMemory(context); 
		}
		catch (Exception e)
		{
			e.toString();
		}
		return null;
	}
	
	/**
	 * Return all VRE CalendarDates, Always check for NULL allCalDates because
	 * this data is being queried from SQLite.
	 */
	 private static ArrayList<CalDates> getAllCalDatesInMemory(Context context) {
		 
		ArrayList<GtfsUpdateTracker> gtfsUpdates = BusinessGtfsUpdateTracker.getAllGtfsFeedUpdates(context);
		
		for (GtfsUpdateTracker each : gtfsUpdates) {	
			
			if (each.FeedName().equals("calendar_dates"))
			{	
				//The inital value is empty, force update
				if (each.LastUpdated().equals("")){
					
					UpdateFeed(context);
					UpdateGtfsUpdateTracker(context);
					break;
				}
				else
				{
					//Get # milliseconds since last data pull
					long x = Common.CompareMilliTimeDelta(each.LastUpdated());
						
					//Do not refresh feed if last check is less than X minutes old, use the Shared Preferences to determine Update Frequency
						
					/* Pull user's preferred default train line */
					SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
					String sMinutes = getPrefs.getString("listGtfsFrequency", "1440");
					long refMinutes = Long.parseLong(sMinutes);
					long lastChkMinutes = (long) Math.round(x / 60000); 
									
						
					if (lastChkMinutes > refMinutes){
								
						UpdateFeed(context);
						UpdateGtfsUpdateTracker(context);	
						break;
					}							
				}											
			}					
		}		 		
		
		return BusinessBaseCalDates.getAllCalDates(context); 		
		 
	 }
	 
	 private static void UpdateFeed(Context context){		 
		 
		//Update local database with Live Feed:
		ProxyNetworkGTFS prxyGTFS = new ProxyNetworkGTFS();	
			
		allCalDates = prxyGTFS.fetchCalendarDates(context);
			
		//Delete all local stored Updates and Load SQL with the latest Collection
		if (allCalDates != null)
		{				
			DeleteAll(context);
						
			//Insert new Updates 
			Insert(context, allCalDates);				
				
		}	
	 }
	 
	 /*Updated GTFS_UPDATE_TRACKER to document last date/time in milliseconds the Feed was accessed.
	  * This prevents the overhead of refreshing all the time.
	  */
	 private static void UpdateGtfsUpdateTracker(Context context){
		 
		 GtfsUpdateTracker gtfsTracker = new GtfsUpdateTracker();
		 
		 //Calculate current date/time milliseconds
		 Calendar calendarToday = Calendar.getInstance();
		 long millisecondsCurrent = calendarToday.getTimeInMillis();
		 
		 gtfsTracker.FeedName("calendar_dates");  //This value must match an existing "GTFS_UPDATE_TRACKER.FeedName" value!
		 gtfsTracker.LastUpdated("" + millisecondsCurrent + "");
		 
		 BusinessGtfsUpdateTracker.Update(context, gtfsTracker);
		 
	 }
}