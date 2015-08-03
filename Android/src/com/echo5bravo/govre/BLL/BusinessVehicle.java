package com.echo5bravo.govre.BLL;

import java.util.ArrayList;
import java.util.Date;

import com.echo5bravo.govre.DAL.BusinessBaseVehicle;
import com.echo5bravo.govre.INFO.Vehicle;
import com.echo5bravo.govre.UTILS.ProxyNetworkGTFS;
import com.echo5bravo.gtfs.GtfsRealtime.TripDescriptor;
import com.echo5bravo.gtfs.GtfsRealtime.FeedEntity;
import com.echo5bravo.gtfs.GtfsRealtime.FeedMessage;
import com.echo5bravo.gtfs.GtfsRealtime.Position;
import com.echo5bravo.gtfs.GtfsRealtime.VehiclePosition;

import android.content.Context;
import android.util.Log;



public class BusinessVehicle extends BusinessBaseVehicle {
	
	private static final String TAG = BusinessVehicle.class.getSimpleName();
	
	//CONSTRUCTORS
	public BusinessVehicle(Context context) {
		super(context);	
	}
	
	//METHODS	
	/**
	* Return all VRE Vehicles, Always check for NULL allVehicles because 
	* this data is being queried from SQLite.
	*/
	public static ArrayList<Vehicle> getAllVehicles(Context context) 
	{
 
		// Validation goes here
		//return super.getAllVehicles();
		try{
			//return getAllVehiclesInMemory(context);  
			return BusinessBaseVehicle.getAllVehicles(context);	
		}
		catch (Exception e)
		{
			e.toString();
		}
		return null;
	}	
	
	/* GTFS Method Call for GTFS-Realtime*/
	public static void LoadVehiclesFromGTFS(Context context){
		
		ProxyNetworkGTFS prxyGTFS = new ProxyNetworkGTFS();		
		FeedMessage feed = prxyGTFS.fetchVehiclePositionFeed(context);
		ArrayList<Vehicle> inView = new ArrayList<Vehicle>();
		
		try{		
			if (!(feed == null)){
						
				for (FeedEntity entity : feed.getEntityList()) {
	    		
		    		//If no Vehicle - Exit
		    	    if (!entity.hasVehicle()) {
		    	      continue;
		    	    }  
		    	    
		    	    //If no Position - Exit
		    	    VehiclePosition vehicle = entity.getVehicle();
		    	    if (!vehicle.hasPosition()) { 
		    	      continue;
		    	    }
		    	    
		    	    //If no LAT/LONG - Exit
		    	    Position position = vehicle.getPosition();
		    	    if (!position.hasLatitude() ||  !position.hasLongitude()) {
		      	      continue;
		      	    }
		    	    
		    	    TripDescriptor trip = vehicle.getTrip();
		    	    if (!trip.hasTripId() ||  !trip.hasRouteId()) {
		        	      continue;
		        	}
		    	    
		    	    //Load Vehicle Object
		    	    Vehicle v = new Vehicle();
		    	    v.VEHICLE_ID(entity.getId());
		    	    v.VEHICLE_LABEL(vehicle.getVehicle().getLabel());
		    	    v.VEHICLE_LATITUDE(String.valueOf(position.getLatitude()));
		    	    v.VEHCILE_LONGITUDE(String.valueOf(position.getLongitude()));
		    	    v.TRIP_ID(trip.getTripId());
		    	    v.ROUTE_ID(Integer.parseInt(trip.getRouteId()));
		    	    
		    	    //Fix to address VRE change to in POSIX time: 24July2013
		    	    long x = (vehicle.getTimestamp());
				    Date time = new Date(x * 1000); //Convert to milliseconds
				    v.TIMESTAMP(String.valueOf(time.getTime()));
		    	    
		 
		    	    v.STOP_SEQUENCE(vehicle.getCurrentStopSequence());
		    	    v.STOP_ID(vehicle.getStopId());
		    	    v.CURRENT_STATUS(vehicle.getCurrentStatus().toString());
		    	      
		    	    //Add Vehicle to collection
		    	    inView.add(v);
	    		}	
			}
		}
		catch(Exception e)
		{
			//Log.d(TAG, "GTFS LOADING ERROR: " + e.toString());	
		}		
		
		//Delete all local stored Vehicles and Load SQL with the latest Collection
		
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
