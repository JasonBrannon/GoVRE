package com.echo5bravo.govre.BLL;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import com.echo5bravo.govre.DAL.BusinessBaseSchedule;
import com.echo5bravo.govre.INFO.Schedule;
import com.echo5bravo.govre.INFO.Station;
import com.echo5bravo.govre.INFO.Update;
import com.echo5bravo.govre.INFO.Vehicle;
import com.echo5bravo.govre.INFO.VehicleBubble;
import com.echo5bravo.govre.UTILS.Common;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

public class BusinessSchedule extends BusinessBaseSchedule {
	
	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = BusinessSchedule.class.getSimpleName();
	
	/* Holds all the defined Stations: gets populated on first access. */
    private static ArrayList<Schedule> allSchedules;
    private static ArrayList<Schedule> allSchedulesReport;  //Used for Grid b/c app needs to display schedule even on weekends
    private static Schedule schedule;
	
	//CONSTRUCTORS
	public BusinessSchedule(Context context) {
		super(context);	
	}
	
	//METHODS	
	/**
	* Return all VRE Schedules, Always check for NULL allSchedules because 
	* this data is being queried from SQLite.
	*/
	public static ArrayList<Schedule> getAllSchedules(Context context, String todaysScheduleType) 
	{
		try{		
			return getAllSchedulesInMemory(context, todaysScheduleType); 
		}
		catch (Exception e)
		{
			e.toString();
		}
		return null;
	}
	
	public static ArrayList<Schedule> getAllSchedules(Context context, String todaysScheduleType, boolean ignoreWeekend) 
	{
		try{		
			return getAllSchedulesInMemory(context, todaysScheduleType, ignoreWeekend); 
		}
		catch (Exception e)
		{
			e.toString();
		}
		return null;
	}

	public static Schedule fetchStationsNextDeparture(Context context
													, ArrayList<Schedule> schedules
													, String StationId
													, String todaysScheduleType
													, ArrayList<Vehicle> vehicles
													, ArrayList<Update> updates
													, String lineFilter
													, String timeformat){
		
		//[Added when Last Train was not updating Alert MSG 27Sept2012]
		schedule = null;  //Important to clear Static Class
		
		Calendar calendarToday = Calendar.getInstance();
		
    	long millisecondsCurrent = calendarToday.getTimeInMillis();		
		long millisecondsTrainArrival;  	//Defines when the train will actually arrive
		long currentKnownTimeDelta = 0;		//Takes the current scheduled time and calculates against today's date to determine an arrival
		long currentUpdateTimeDelta = 0;	//Actual Arrival time provided by GTFS-R Update feed, this is not always available so must evaluate to use currentKnownTimeDelta instead
		long currentUpdateTime = 0;
		long millisecondsDelay = 0;			//Provided by GTFS-R Update feed, number of seconds train is delayed or ahead of current schedule
		int secondsDelay = 0;				//Provided by GTFS-R Update feed, number of seconds train is delayed or ahead of current schedule
		String EstArrivalTime;				//Verbose arrival time estimate
		Boolean LiveUpdates = false;
		Boolean blnTrainCanceled = false;
		
		int letmeknow = 0; //delete this  
		int routeid = 0;
		
		Boolean bothRoutes = false;
		
		if (lineFilter.equals("FBG,ALL"))
			routeid = 2;
		else if (lineFilter.equals("MSS,ALL"))
			routeid = 4;
		else if (lineFilter.equals("FBG,ALL,MSS"))  
			bothRoutes = true;		
		else
			bothRoutes = true;
	
		try{		
	    	//Loop Through Each Schedule
	    	for (Schedule each : schedules) {	
	    		
	    		//FOR TESTING A SPECIFIC STATION STOP
	    		//if (each.SCHEDULE_STOP_ID().equals("0000000000000000000000000000000C")  && StationId.equals("0000000000000000000000000000000C")){
	    		
	    		//If the Schedule's stop_id = the StationId, store it in the inView Container
	    		if (each.SCHEDULE_STOP_ID().equals(StationId) && (bothRoutes | each.ROUTE_ID().equals(routeid))){
	    			
	    			//Compute NOW against the Trains Arrival Time, return the Delta in milliseconds for maximum accuracy
	    			currentKnownTimeDelta = Common.CompareTimeDelta(each.SCHEDULE_ARRIVAL_TIME());
	    			millisecondsTrainArrival = Common.ArrivalMilliseconds(each.SCHEDULE_ARRIVAL_TIME()); 
	    			
	    			if (updates != null){
		    			//Determine if the Stop is on schedule or there are delays
		    			for (Update eachUpdate: updates){  
		    				
		    				//TODO: ADD CHECK TO SEE IF THE UPDATES ARE FROM TODAY
	    					//if(eachUpdate.TIME()  == Today){
		    				
			    				if (eachUpdate.STOP_ID().equals(StationId)  && eachUpdate.TRIP_ID().equals(each.TRIP_ID())){
			    					
			    					if (eachUpdate.TRIP_SCHED_RELATION().equals("CANCELED"))
			    					   	blnTrainCanceled = true;
			    					else
			    						blnTrainCanceled = false;
			    					
			    					//Compute NOW against the Trains Arrival Time, return the Delta in milliseconds for maximum accuracy
			    					currentUpdateTimeDelta = Common.CompareMilliTimeDelta(eachUpdate.TIME());
			    					currentUpdateTime = Long.parseLong(eachUpdate.TIME()); 
			    					
			    					//Add any Delays or move train if it's ahead of schedule
			    					//eachUpdate.DELAY() = 0   -- No Delay
			    					//eachUpdate.DELAY() = (-) -- Delay
			    					//eachUpdate.DELAY() = (+) -- Ahead of Schedule		    				
			    					millisecondsDelay =  (eachUpdate.DELAY() * 1000); 
			    					secondsDelay = eachUpdate.DELAY();		    					
			    					
			    					LiveUpdates = true;
			    					
			    					//Break out of loop if 1st currentKnownTimeDelta is < 0
			    					if (currentUpdateTimeDelta < 0)  
			    						break;
			    					else
			    						currentUpdateTimeDelta = 0;
			    				}
			    			//}
		    			}
	    			}
	    			
	    			if(LiveUpdates == true && currentUpdateTimeDelta != 0){
	    				currentKnownTimeDelta = currentUpdateTimeDelta;
	    			}	
		    			/*Since the	currentSchedules Array is pre-sorted by SCHEDULE_ARRIVAL_TIME ASC
			    		*Look for the 1st instance where the known delta between Now and Arrival is less than 0
			    		*this will be the next train at the station */
			    		if (currentKnownTimeDelta <= 0){			    				
			    			schedule = new Schedule();			    				
				    		schedule.ROUTE_ID(each.ROUTE_ID());
				    		schedule.SERVICE_ID(each.SERVICE_ID());
				    		schedule.TRIP_ID(each.TRIP_ID());
				    		schedule.TRIP_HEADSIGN(each.TRIP_HEADSIGN());
				    		schedule.TRIP_SHORT_NAME(each.TRIP_SHORT_NAME());  
				    		schedule.TRIP_DIRECTION_ID(each.TRIP_DIRECTION_ID());
				    		schedule.TRIP_BLOCK_ID(each.TRIP_BLOCK_ID());
				    		schedule.TRIP_SHAPE_ID(each.TRIP_SHAPE_ID());
				    		schedule.SCHEDULE_TYPE(each.SCHEDULE_TYPE());
				    		schedule.SCHEDULE_ARRIVAL_TIME(Common.FormatTime(each.SCHEDULE_ARRIVAL_TIME(), timeformat));		
				    		schedule.SCHEDULE_ARRIVAL_TIME_ACTUAL(Common.ArrivalTime(millisecondsTrainArrival + millisecondsDelay, timeformat));
				    		schedule.SCHEDULE_DEPARTURE_TIME(Common.FormatTime(each.SCHEDULE_DEPARTURE_TIME(), timeformat));	
				    		schedule.SCHEDULE_DEPARTURE_TIME_ACTUAL(Common.ArrivalTime(millisecondsTrainArrival + millisecondsDelay, timeformat));	
				    		schedule.SCHEDULE_STOP_ID(each.SCHEDULE_STOP_ID());
				    		schedule.SCHEDULE_STOP_SEQUENCE(each.SCHEDULE_STOP_SEQUENCE());
				    		schedule.SCHEDULE_STOP_HEADSIGN(each.SCHEDULE_STOP_HEADSIGN());
				    		schedule.SCHEDULE_STOP_PICK_TYPE(each.SCHEDULE_STOP_PICK_TYPE());
				    		schedule.SCHEDULE_STOP_DROP_TYPE(each.SCHEDULE_STOP_DROP_TYPE());
				    		schedule.SCHEDULE_STOP_DIST_TRAV(each.SCHEDULE_STOP_DIST_TRAV());    
				    		
				    		if (LiveUpdates == true && currentUpdateTimeDelta != 0){	    			
				    			
				    			EstArrivalTime = (String) DateUtils.getRelativeTimeSpanString(millisecondsTrainArrival + millisecondsDelay, millisecondsCurrent, 0);
				    			schedule.MSG_ESTIMATED_TIME_ARRIVAL(" Depart " + EstArrivalTime);
				    			
				    			if (secondsDelay > 0){
						    		String sDelay = Integer.toString(secondsDelay / 60);
						    		schedule.MSG_ALERT(each.TRIP_SHORT_NAME()  + " - To " + each.TRIP_HEADSIGN() + ": Delayed " + sDelay + " minutes");
						    	}
						    	else
						    	{
						    		schedule.MSG_ALERT(each.TRIP_SHORT_NAME()  + " - To " + each.TRIP_HEADSIGN() + ": On-Time");
						    	}
				    		}
				    		else
				    		{			 
				    			EstArrivalTime = (String) DateUtils.getRelativeTimeSpanString(millisecondsTrainArrival + millisecondsDelay, millisecondsCurrent, 0);
				    			
				    			if (EstArrivalTime.contains("in 0 seconds")){
				    				schedule.MSG_ALERT(each.TRIP_SHORT_NAME() + " - To " + each.TRIP_HEADSIGN() + ": On-Time");
				    				schedule.MSG_ESTIMATED_TIME_ARRIVAL(" Arriving");
				    			}				    			
				    			else
				    			{
				    				if(blnTrainCanceled)
					    			{
					    				schedule.MSG_ALERT(each.TRIP_SHORT_NAME()  + " - To " + each.TRIP_HEADSIGN() + "\nCANCELED");
					    				schedule.MSG_ESTIMATED_TIME_ARRIVAL(" See Alerts ");					    				
					    			}
				    				else
				    				{
				    					schedule.MSG_ALERT(each.TRIP_SHORT_NAME()  + " - To " + each.TRIP_HEADSIGN() + "\nNo Live Updates");
				    					schedule.MSG_ESTIMATED_TIME_ARRIVAL(" Depart " + EstArrivalTime);
				    				}
				    			}
				    			
				    		}
				    		LiveUpdates = false;	
				    		break;		    				    
		    			}			
		       		}
	    		}
	    	//}    	
		}
		catch(Exception ex)
		{
			//Log.d(TAG, "fetchStationsNextDeparture " + ex.toString()); 
		}
		
		//Catch All For No Train Activity
		if (schedule == null){
			
    		if (todaysScheduleType.equals("NOSCHEDULEWKND")){
    			schedule = new Schedule();
    			schedule.SCHEDULE_STOP_ID(StationId);
    			schedule.MSG_ALERT("No Schedule - Weekend");
    			schedule.MSG_ESTIMATED_TIME_ARRIVAL("Press for Schedule");		    				
    		}
    		else if (todaysScheduleType.equals("NOSCHEDULE")){
    			schedule = new Schedule();
    			schedule.SCHEDULE_STOP_ID(StationId);
    			schedule.MSG_ALERT("No Schedule - Holiday");
    			schedule.MSG_ESTIMATED_TIME_ARRIVAL("Press for Schedule");			    				
    		}
    		else{
    			schedule = new Schedule();
    			schedule.SCHEDULE_STOP_ID(StationId);
    			schedule.MSG_ALERT("Press for Schedule");
    			schedule.MSG_ESTIMATED_TIME_ARRIVAL("Train Operations Ended");
    		}			
		}
		
    	return schedule;
	}
	
	public static VehicleBubble fetchVehicleBubble(Context context
													, Vehicle myVehicle
													, ArrayList<Update> updates
													, ArrayList<Station> stations
													, String todaysScheduleType
													, String timeformat){
		VehicleBubble vehicleBubble = null;
		
		ArrayList<Schedule> schedules = getAllSchedulesInMemory(context, todaysScheduleType);
		
		Calendar calendarToday = Calendar.getInstance();
		
    	long millisecondsCurrent = calendarToday.getTimeInMillis();		
		long millisecondsTrainArrival;  	//Defines when the train will actually arrive
		long currentKnownTimeDelta = 0;		//Takes the current scheduled time and calculates against today's date to determine an arrival
		long currentUpdateTimeDelta = 0;	//Actual Arrival time provided by GTFS-R Update feed, this is not always available so must evaluate to use currentKnownTimeDelta instead
		long currentUpdateTime = 0;
		long millisecondsDelay = 0;			//Provided by GTFS-R Update feed, number of seconds train is delayed or ahead of current schedule
		int secondsDelay = 0;				//Provided by GTFS-R Update feed, number of seconds train is delayed or ahead of current schedule
		String EstArrivalTime;				//Verbose arrival time estimate
		Boolean LiveUpdates = false;
		
		//VehicleBubble Variables
		String  bblVehicleId = "";			//ex: 303	
		String  bblLabel = "";				//ex: 303 / V53
		String  bblStatus = "";				//ex: Delayed 10 minutes or On-Time or At Station
		String  bblNextStop = "";			//ex: Quantico
		String  bblNextStopAndTime = "";	//ex: Quantico, 2:20 PM
		String  bblHeading = "";;			//ex: Union Station, 4:30 PM
		String  bblStopsRemaining = "";		//ex: 4 of 12 Stops Remaining
		String  bblCurrentStop = "";		//ex: Calculated Where the Train is currently at
		boolean bblAtStation = false;		//ex: identifies if the train is at the station if true
		boolean bblDELAYED = false;			//ex: identifies if the train is running under delay if true
		boolean bblLostComm = false;		//ex: identifies if GTFS has lost communications with train if true
		
		try{		
		    //Loop Through Each Schedule
		    for (Schedule eachSchedule : schedules) {
		    	
		    	//For some reason, vehicles may not have a trip_id provided, to get
		    	//the current trip_id, cross check against the Updates based on the 
		    	//last timestamp of the vehicle and the update's departure time
		    	if (myVehicle.STOP_ID().equals("")){
		    		
		    		//Loop Through Each Schedule
				    for (Update eachUpdate : updates) {
				    	
				    	if (eachUpdate.TRIP_ID().equals(myVehicle.TRIP_ID())){ 
				    		
				    		if (Long.parseLong(myVehicle.TIMESTAMP()) <= Long.parseLong(eachUpdate.TIME())){
				    			myVehicle.STOP_ID(eachUpdate.STOP_ID());
				    			break;
				    		}				    		
				    	}				    	
				    }		    		
		    	}		    	
		    	
		    	int tripCount = 0;
		    	int totalCount = 0;
		    	boolean endSequenceReached = false;
		    	//Loop Through Each Schedule
			     for (Update eachUpdate : updates) {	
			    	if (eachUpdate.TRIP_ID().equals(myVehicle.TRIP_ID())){
			    		totalCount++;
			    		if (Long.parseLong(myVehicle.TIMESTAMP()) <= Long.parseLong(eachUpdate.TIME())
			    			&& endSequenceReached == false){
			    			myVehicle.STOP_ID(eachUpdate.STOP_ID());
			    			tripCount = eachUpdate.STOP_SEQUENCE();
			    			endSequenceReached = true;
			    		}				    		
			    	}				    	
			    }
			    
			    //Formula: TotalCount - (tripCount -1) because tripCount = next station sequence which the train has not arrived yet.
			    if ((totalCount - (tripCount-1)) == 1)
			    	bblStopsRemaining = "Approaching last stop";
			    else
			    	bblStopsRemaining = String.valueOf(totalCount - (tripCount-1)) +  " of " + String.valueOf(totalCount) + " stops remaining";
		    		
		    	//If the Schedule's Trip ID Name = the VehicleId
		    	if (eachSchedule.TRIP_ID().equals(myVehicle.TRIP_ID()) &&
		    		eachSchedule.SCHEDULE_STOP_ID().equals(myVehicle.STOP_ID())){
		    		
		    		/********** UPDATE DRIVEN LOGIC *****************/
		    		
		    		//Compute NOW against the Trains Arrival Time, return the Delta in milliseconds for maximum accuracy
	    			currentKnownTimeDelta = Common.CompareTimeDelta(eachSchedule.SCHEDULE_ARRIVAL_TIME());
	    			millisecondsTrainArrival = Common.ArrivalMilliseconds(eachSchedule.SCHEDULE_ARRIVAL_TIME());
		    		
		    		//Loop Through Each Schedule
				    for (Update eachUpdate : updates) {	
				    	
				    	if (eachUpdate.STOP_ID().equals(eachSchedule.SCHEDULE_STOP_ID())  
				    		&& eachUpdate.TRIP_ID().equals(eachSchedule.TRIP_ID())){
	    					
	    					//Compute NOW against the Trains Arrival Time, return the Delta in milliseconds for maximum accuracy
	    					currentUpdateTimeDelta = Common.CompareMilliTimeDelta(eachUpdate.TIME());
	    					currentUpdateTime = Long.parseLong(eachUpdate.TIME());
	    					
	    					//Add any Delays or move train if it's ahead of schedule
	    					//eachUpdate.DELAY() = 0   -- No Delay
	    					//eachUpdate.DELAY() = (-) -- Delay
	    					//eachUpdate.DELAY() = (+) -- Ahead of Schedule		    				
	    					millisecondsDelay =  (eachUpdate.DELAY() * 1000); 
	    					secondsDelay = eachUpdate.DELAY();		    					
	    					
	    					LiveUpdates = true;
	    					
	    					//Break out of loop if 1st currentKnownTimeDelta is < 0
	    					if (currentUpdateTimeDelta < 0)  
	    						break;
	    					else
	    						currentUpdateTimeDelta = 0;
	    				}			    
				    }
				    
				    if(LiveUpdates == true && currentUpdateTimeDelta != 0){
	    				currentKnownTimeDelta = currentUpdateTimeDelta;		    				
	    			}
				    
				    /*Since the	currentSchedules Array is pre-sorted by SCHEDULE_ARRIVAL_TIME ASC
		    		*Look for the 1st instance where the known delta between Now and Arrival is less than 0
		    		*this will be the next train at the station */
		    		//if (currentKnownTimeDelta <= 0){
		    		
			    		
			    		//@@ Set Bubble Variable
		    			bblLabel = myVehicle.VEHICLE_ID() + " / " + myVehicle.VEHICLE_LABEL(); 
			    		bblVehicleId = myVehicle.VEHICLE_ID();
			    			    		
			    			
			    		if (LiveUpdates == true && currentUpdateTimeDelta != 0){	      			
			    			
			    			EstArrivalTime = (String) DateUtils.getRelativeTimeSpanString(millisecondsTrainArrival + millisecondsDelay, millisecondsCurrent, 0);
			    			//schedule.MSG_ESTIMATED_TIME_ARRIVAL(" Depart " + EstArrivalTime);  ???????????????????????
			    			
			    			if (secondsDelay > 0){
					    		String sDelay = Integer.toString(secondsDelay / 60);
					    		
					    		//@@ Set Bubble Variable
					    		bblStatus = "Delayed " + sDelay + " minutes";
					    		bblDELAYED = true;
					    	}
					    	else
					    	{
					    		//@@ Set Bubble Variable
					    		bblStatus = "On-Time";
					    		bblDELAYED = false;
					    	}
			    		}
			    		else
			    		{			 
			    			EstArrivalTime = (String) DateUtils.getRelativeTimeSpanString(millisecondsTrainArrival + millisecondsDelay, millisecondsCurrent, 0);
			    			
			    			if (EstArrivalTime.contains("in 0 seconds")){
			    				//@@ Set Bubble Variable
					    		bblStatus = "Arriving At Station";
					    		bblDELAYED = false;
			    			}			    		
			    		}	
			    		
			    		double stationDistance = 0;
			    		
			    		/****************************** STATION DRIVEN LOGIC *************************************/
			    		for (Station eachStation : stations) {
							
								try{
									
									//Calculate Distance between Train and Station, this will be used to determine if
									//the train is located at the station, as the GTFS AT_STOP notification is lacking in the feed.									
									stationDistance = Common.Distance(Double.valueOf(eachStation.STATION_LATITUDE())
																,Double.valueOf(eachStation.STATION_LONGITUDE())  
																,Double.valueOf(myVehicle.VEHICLE_LATITUDE())
																,Double.valueOf(myVehicle.VEHCILE_LONGITUDE()), "F"); 
								}
								catch(Exception e)
								{
									//Log.d(TAG, "LatLong Substring Error: " + e.toString());	
								}
								
							//Is train within 500 feet of Station
							if (stationDistance <= 500){
								bblAtStation = true;
								
								bblCurrentStop = eachStation.STATION_STOP_NAME();
								
								break;
							}
							else
							{
								bblAtStation = false;
							}			
						}
			    		
			    		for (Station eachStation : stations) {
			    			
			    			if (eachStation.STATION_ID().equals(myVehicle.STOP_ID())){		    				
			    				//@@ Set Bubble Variable
			    				bblNextStop = eachStation.STATION_STOP_NAME();
			    				bblNextStopAndTime = eachStation.STATION_STOP_NAME();
		    				}
			    		}
							
			    		/****************************** END STATION DRIVEN LOGIC *************************************/
			    		
			    		//@@ Set Bubble Variable
			    		//bblNextStopAndTime = bblNextStopAndTime + ", " + (Common.FormatTime(eachSchedule.SCHEDULE_ARRIVAL_TIME(), timeformat));	
			    		bblNextStopAndTime = bblNextStopAndTime + ", " + (Common.ArrivalTime(millisecondsTrainArrival + millisecondsDelay, timeformat));	 
			    		
			    		bblHeading = eachSchedule.TRIP_HEADSIGN();
			    		bblLostComm = LostCommunications(myVehicle.TIMESTAMP());
			    		
			    		if (bblLostComm)
			    			bblStatus = "Lost Communications";
			    					    				
			    		vehicleBubble = new VehicleBubble();
			    		vehicleBubble.VEHICLE_ID(bblVehicleId);				//ex: 303
			    		vehicleBubble.VEHICLE_LABLE(bblLabel);				//ex: 303 / V53
			    		vehicleBubble.STATUS(bblStatus);					//ex: Delayed 10 minutes or On-Time or At Station
			    		vehicleBubble.NEXT_STOP(bblNextStop);				//ex: Quantico
			    		vehicleBubble.NEXT_STOP_AND_TIME(bblNextStopAndTime);	//ex: Quantico, 2:20 PM
			    		vehicleBubble.HEADING(bblHeading);					//ex: Union Station, 4:30 PM
			    		vehicleBubble.STOPS_REMAINING(bblStopsRemaining);	//ex: 4 of 12 Stops Remaining
			    		vehicleBubble.CURRENT_STOP(bblCurrentStop);
			    		vehicleBubble.AT_STATION(bblAtStation);				//ex: identifies if the train is at the station if true
			    		vehicleBubble.DELAYED(bblDELAYED);					//ex: identifies if the train is running under delay if true
			    		vehicleBubble.LOST_COMM(bblLostComm);				//ex: identifies if GTFS has lost communications with train if true
			     			    		
			    		LiveUpdates = false;
			    		break;	
		    		//}
		    	}		    		
		   	}	
		}
		catch(Exception ex)
		{
			//Log.d(TAG, "ERROR fetchVehicleBubble " + ex.toString());     
		}
		return vehicleBubble;		
	}
	
	/**
	* Return an array of all station schedules Schedule[] for a specific station 
	* @throws ParseException 
	*/ 	
	public static Schedule[]  fetchScheduleAtSelectStation(Context context, String StationId, Integer Direction, String todaysScheduleType) throws ParseException {				
		
		ArrayList<Schedule> contents = getAllSchedulesInMemory(context, todaysScheduleType);
			
		//Our container objects	
		ArrayList<Schedule> stationSchedule = new ArrayList<Schedule>();
		Schedule currentSchedule = new Schedule();								    	
	    	    	
		for (Schedule each : contents) { 
		
		//Logic: Return the next departure time of Station
	    		
			//Does the Station Code match (exp: FBG = FBG ?)  	
			if (each.SCHEDULE_STOP_ID().equals(StationId))
			{
				if (each.SCHEDULE_DEPARTURE_TIME() != null || each.SCHEDULE_DEPARTURE_TIME() != "")
				{
					//Pull either NORTH or SOUTH
					if (each.TRIP_DIRECTION_ID() == Direction)
					{
						try{
							//Create new instance of object
							currentSchedule = new Schedule();  				
								
							currentSchedule.ROUTE_ID(each.ROUTE_ID());
							currentSchedule.SERVICE_ID(each.SERVICE_ID());
							currentSchedule.TRIP_ID(each.TRIP_ID());
							currentSchedule.TRIP_HEADSIGN(each.TRIP_HEADSIGN());
							currentSchedule.TRIP_SHORT_NAME(each.TRIP_SHORT_NAME());
							currentSchedule.TRIP_DIRECTION_ID(each.TRIP_DIRECTION_ID());
							currentSchedule.TRIP_BLOCK_ID(each.TRIP_BLOCK_ID());
							currentSchedule.TRIP_SHAPE_ID(each.TRIP_SHAPE_ID());
							currentSchedule.SCHEDULE_TYPE(each.SCHEDULE_TYPE());
							currentSchedule.SCHEDULE_ARRIVAL_TIME(each.SCHEDULE_ARRIVAL_TIME());
							currentSchedule.SCHEDULE_DEPARTURE_TIME(each.SCHEDULE_DEPARTURE_TIME());
							currentSchedule.SCHEDULE_STOP_ID(each.SCHEDULE_STOP_ID());
							currentSchedule.SCHEDULE_STOP_SEQUENCE(each.SCHEDULE_STOP_SEQUENCE());
							currentSchedule.SCHEDULE_STOP_HEADSIGN(each.SCHEDULE_STOP_HEADSIGN());
							currentSchedule.SCHEDULE_STOP_PICK_TYPE(each.SCHEDULE_STOP_PICK_TYPE());
							currentSchedule.SCHEDULE_STOP_DROP_TYPE(each.SCHEDULE_STOP_DROP_TYPE());
							currentSchedule.SCHEDULE_STOP_DIST_TRAV(each.SCHEDULE_STOP_DIST_TRAV());
			    					
							//Add newly populated object to ArrayList
							stationSchedule.add(currentSchedule);		    				
				    		    	    	    		
						}
						catch(Exception e)
						{
							e.toString();
						}						
					}
	    		}
	    	}     		
		}		
	   	// Convert them into an array so we can pass directly to the list view.
	   	Schedule[] results = stationSchedule.toArray(new Schedule[stationSchedule.size()]);
	    return results;  
	}	
	
	/*
	 * VRE's definition of "LOST COMMUNICATIONS":
	 * ---------------------------------------------------------------------------------------
	 * "lost communications" is an application specific decision to deem communications lost 
	 * when the GPS timestamp is "old".  The determination of "old" is application specific,
	 * so TRIP can indicate a train is "comm failed" but there is no way to indicate that in 
	 * the GTFS feed; the mobile app will need to determine when it wants to show alert the 
	 * user that the data is old.
	 */
	private static boolean LostCommunications(String TimeStamp){
		Long delta;
		delta = (Common.CompareMilliTimeDelta(TimeStamp) / 1000 / 60);  //Convert milliseconds back to minutes
		
		if (delta >= 5)  //TODO: Make this 3 (minutes) a global variable
			return true;
		else	
			return false;
	}
		
	/**
	 * Return all VRE schedules, Always check for NULL allSchedules because
	 * this data is being queried from SQLite.
	 */
	 private static ArrayList<Schedule> getAllSchedulesInMemory(Context context, String todaysScheduleType) {	 	
	 	if (allSchedules == null)
	 	{
	 		allSchedules = BusinessBaseSchedule.getAllSchedules(context, todaysScheduleType); 
	 	}	     
	    return allSchedules;
	 }
	 
	 /**
	 * Return all VRE schedules, Always check for NULL allSchedules because
	 * this data is being queried from SQLite.
	 */
	 private static ArrayList<Schedule> getAllSchedulesInMemory(Context context, String todaysScheduleType, boolean ignoreWeekend)  {		 	
	 	if (allSchedulesReport == null)
	 	{
	 		allSchedulesReport = BusinessBaseSchedule.getAllSchedules(context, todaysScheduleType);	 		
	  	}	     
	    return allSchedulesReport;
	 }
}
