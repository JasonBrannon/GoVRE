package com.echo5bravo.govre.BLL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.echo5bravo.govre.DAL.BusinessBaseUpdate;
import com.echo5bravo.govre.INFO.Update;
import com.echo5bravo.govre.UTILS.ProxyNetworkGTFS;
import com.echo5bravo.gtfs.GtfsRealtime.FeedEntity;
import com.echo5bravo.gtfs.GtfsRealtime.FeedMessage;
import com.echo5bravo.gtfs.GtfsRealtime.TripUpdate;
import com.echo5bravo.gtfs.GtfsRealtime.TripUpdate.StopTimeUpdate;

public class BusinessUpdate extends BusinessBaseUpdate {
	
	private static final String TAG = BusinessUpdate.class.getSimpleName(); 	
	
	//CONSTRUCTORS
	public BusinessUpdate(Context context) {
		super(context);	
	}
	
	//METHODS	
	/**
	* Return all VRE Updates, Always check for NULL allUpdates because 
	* this data is being queried from SQLite.
	*/
	public static ArrayList<Update> getAllUpdates(Context context) 
	{
 
		// Validation goes here
		//return super.getAllUpdates();
		try{
			//return getAllUpdatesInMemory(context); 
			return BusinessBaseUpdate.getAllUpdates(context);	 
		}
		catch (Exception e)
		{
			e.toString();
		}
		return null;
	}	
	
	/* GTFS Method Call for GTFS-Realtime*/
	public static void LoadUpdatesFromGTFS(Context context){
		
		ProxyNetworkGTFS prxyGTFS = new ProxyNetworkGTFS();		
		FeedMessage feed = prxyGTFS.fetchTripUpdateFeed(context);
		ArrayList<Update> inView = new ArrayList<Update>();
		List<StopTimeUpdate> stopList = new ArrayList<StopTimeUpdate>();		
		
		try{		
			if (!(feed == null)){
			
				for (FeedEntity entity : feed.getEntityList()) {
					
					if (!entity.hasTripUpdate()) {
		    	        continue;
		    	    }
		    	    TripUpdate update = entity.getTripUpdate();
		    	    
		    	    if (!update.hasTrip()) {
		    	       continue;
		    	    } 		    	    		    	   		    	    
		    	    
		    	    update.getTrip().getScheduleRelationship(); 		    	    
		    	    if (!update.hasVehicle()) {
			    	       continue;
			    	} 		    	   
		    	    
		    	    stopList = update.getStopTimeUpdateList();
		    	  
		    	    for (StopTimeUpdate each : stopList) {
		    	    	
		    	    	Update u = new Update();		    	    	
		    	    		    	    	
		    	    	u.TRIP_ID(update.getTrip().getTripId());
			    	    u.TRIP_SCHED_RELATION(String.valueOf(update.getTrip().getScheduleRelationship()));
			    	    u.ROUTE_ID(Integer.parseInt(update.getTrip().getRouteId()));
			    	    u.VEHICLE_ID(update.getVehicle().getId());
			    	    u.VEHICLE_LABEL(update.getVehicle().getLabel());
			    	    
			    	    u.STOP_SEQUENCE(each.getStopSequence());
					    u.STOP_ID(each.getStopId());
					    u.STOP_SCHED_RELATION(each.getScheduleRelationship().name());
					    u.DELAY(each.getDeparture().getDelay());
					    
					    //Fix to address VRE change to in POSIX time: 24July2013
					    long x = (each.getDeparture().getTime());
					    Date time = new Date(x * 1000); //Convert to milliseconds
					    u.TIME(String.valueOf(time.getTime()));
			    	    
				
					    
					    //Add Update to collection
			    	    inView.add(u);
		    	    }   
	    		}	
			}
		}
		catch(Exception e)
		{
			//Log.d(TAG, "GTFS UPDATE LOADING ERROR: " + e.toString());	
		}
		
		//MOVED from below inView NULL check b/c on 13Sept 2 late last trains stuck in the schedule.
		//Delete all Updates and refresh with new 
		//DeleteAll(context);
		
		//Delete all local stored Updates and Load SQL with the latest Collection
		if (inView != null)
		{	
			DeleteAll(context);
			
			//Insert new Updates 
			Insert(context, inView);
		}
	}
}
