package com.echo5bravo.govre.UTILS;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import android.content.Context;
import com.echo5bravo.govre.INFO.CalDates;

public class Common {
	
	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = Common.class.getSimpleName();
	
	//Converts Strings to Dates based on String format (ex: "yyyy/mm/dd" or "yyyymmdd")
	public static Date ConvertStringDate(String dateString, String yyyyMMdd){
   	
	    	DateFormat formatter;
			Date convertedDate = null;
			
			formatter = new SimpleDateFormat(yyyyMMdd);
			try {
				convertedDate = (Date) formatter.parse(dateString);
			} catch (ParseException e) {				
				//e.printStackTrace();
			}

			return convertedDate;
				
	}
	
	/*Checks to see if it's SATURDAY or SUNDAY.. VRE does not operate on Weekends */
	public static boolean IsItTheWeekend()
	{
		Calendar calendarToday = Calendar.getInstance();
			
		if (calendarToday.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendarToday.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
		{
			//TODO: //Change back to true, set to false to test information on the weekend
			return true;
		}
		else
		{
			return false;
		}

	}
	
	public static long CompareTimeDelta(String arrivalTime){		
		
		Calendar calendarToday = Calendar.getInstance();
		Calendar calendarTrainArrival = Calendar.getInstance();
		
		long millisecondsCurrent = calendarToday.getTimeInMillis();
		long millisecondsTrainArrival;
		
		int nowYear = calendarToday.get(Calendar.YEAR);
    	int nowMonth = calendarToday.get(Calendar.MONTH);
    	int nowDay = calendarToday.get(Calendar.DATE);
		
		int evalhr=0;
    	int evalmin=0;
    	int evalsec=0;
		
		String delims = "[:]";
    	String[] timesplit = arrivalTime.split(delims);
    	
    	evalhr = Integer.parseInt(timesplit[0]);	//HR
    	evalmin = Integer.parseInt(timesplit[1]); 	//MIN
    	evalsec = Integer.parseInt(timesplit[2]);	//SEC - VRE does not provide a schedule down to seconds, so ignore.
    	
    	calendarTrainArrival.set(nowYear, nowMonth, nowDay, evalhr, evalmin);
    	millisecondsTrainArrival = calendarTrainArrival.getTimeInMillis();    
    	
    	return millisecondsCurrent - millisecondsTrainArrival;
	}
	
	//Same as CompareTimeDelta but accepts milliseconds as string not time (05:05)
	public static long CompareMilliTimeDelta(String arrivalTime){		
		
		Calendar calendarToday = Calendar.getInstance();
		
		long millisecondsCurrent = calendarToday.getTimeInMillis();
    	
    	return millisecondsCurrent - Long.parseLong(arrivalTime);
	}
	
	public static String ArrivalTime(long arrivalMilliseconds, String timeformat){
		
		DateFormat formatter;
		Date convertedDate = null;	
		
		Calendar calendarToday = Calendar.getInstance();
		calendarToday.setTimeInMillis(arrivalMilliseconds);
		
		String Time = String.valueOf(calendarToday.get(Calendar.HOUR_OF_DAY) + ":" + calendarToday.get(Calendar.MINUTE)) ;
		formatter = new SimpleDateFormat("HH:mm");
		try {
			convertedDate = (Date) formatter.parse(Time);
			
		} catch (ParseException e) {
			//e.printStackTrace();
		}
    	
		if (timeformat.equals("24"))
			return formatter.format(convertedDate);	
		else
			return ampmChanger(formatter.format(convertedDate));
		
		
	}
	
	public static long ArrivalMilliseconds(String arrivalTime){		
		
		Calendar calendarToday = Calendar.getInstance();
		Calendar calendarTrainArrival = Calendar.getInstance();
		
		long millisecondsTrainArrival;
		
		int nowYear = calendarToday.get(Calendar.YEAR);
    	int nowMonth = calendarToday.get(Calendar.MONTH);
    	int nowDay = calendarToday.get(Calendar.DATE);
		
		int evalhr=0;
    	int evalmin=0;
    	int evalsec=0;
		
		String delims = "[:]";
    	String[] timesplit = arrivalTime.split(delims);
    	
    	evalhr = Integer.parseInt(timesplit[0]);	//HR
    	evalmin = Integer.parseInt(timesplit[1]); 	//MIN
    	evalsec = Integer.parseInt(timesplit[2]);	//SEC - VRE does not provide a schedule down to seconds, so ignore.
    	
    	calendarTrainArrival.set(nowYear, nowMonth, nowDay, evalhr, evalmin);
    	millisecondsTrainArrival = calendarTrainArrival.getTimeInMillis();  
    	
    	return millisecondsTrainArrival;
	}
	
	public static String FormatTime(String Time, String timeformat){
		
    	DateFormat formatter;
		Date convertedDate = null;		
		
		formatter = new SimpleDateFormat("HH:mm");
		try {
			convertedDate = (Date) formatter.parse(Time);
			
		} catch (ParseException e) {
			//e.printStackTrace();
		}
    	
		if (timeformat.equals("24"))
			return formatter.format(convertedDate);	
		else
			return ampmChanger(formatter.format(convertedDate));
		
	}
	
	/*Converts 24HR Time to Standard Time */
	public static String ampmChanger(String time) {
		  int hour = Integer.parseInt(time.substring(0,2));
		  String timeAppendage;
		  if (hour > 12) { // For 1 PM and on
		    hour -= 12; 
		    timeAppendage = "p"; 
		  }
		  else if (hour == 12) timeAppendage = "p"; // For 12 pm
		  else timeAppendage = "a"; // For 0(12AM) to Noon

		  return String.valueOf(hour)+time.substring(2)+timeAppendage;
		}

	public static boolean compareDates(Date thisDate, Date todayDate){
		
		int results = todayDate.compareTo(thisDate);
		   
	    if(results > 0)
	      System.out.println("First Date is after second");
	    else if (results < 0)
	      System.out.println("First Date is before second");
	    else
	      return true;
	    
	    return false;
		
	}
	
	/************************************************************************************************************************************                                                              
	  This routine calculates the distance between two points (given the    
	  latitude/longitude of those points). It is being used to calculate     
	  the distance between two locations
	                                                                         
	  Definitions:                                                          
	    South latitudes are negative, east longitudes are positive           
	                                                                         
	  Passed to function:                                                    
	    lat1, lon1 = Latitude and Longitude of point 1 (in decimal degrees)  
	    lat2, lon2 = Latitude and Longitude of point 2 (in decimal degrees)  
	    unit = the unit you desire for results                               
	           where: 'M' is statute miles                                   
	                  'K' is kilometers (default)                            
	                  'N' is nautical miles                                 
                 
   *************************************************************************************************************************************/	
	public static double Distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		  double radius = 6371.0090667;
		  lat1 = lat1 * Math.PI / 180.0;
		  lon1 = lon1 * Math.PI / 180.0;
		  lat2 = lat2 * Math.PI / 180.0;
		  lon2 = lon2 * Math.PI / 180.0;
		  double dlon = lon1 - lon2;
		  
		  double distance = Math.acos( Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(dlon)) * radius;
		  if (unit == "K") {
		    return distance;
		  } else if (unit == "M") {
		  	return (distance * 0.621371192);
		  } else if (unit == "F") { //FEET
			return ((distance * 0.621371192) * 5280);
		  } else if (unit == "N") {
		  	return (distance * 0.539956803);
		  } else {
		  	return 0;
		  }
		}
	
	/************************************************************************************************************************************
	* Return all the schedule type for today 
	* 
	* GTFS CalDates can be updated by VRE to reflect the addition or removal of an entire schedule.  
	* VRE calls these Special = "S" schedules, and "S" run before or after specific holidays (ie.. Friday after Thanksgiving) or a snow day
	* 
	* To ensure the correct schedule is being loaded for the Stop/Station, the system must verify if any new CalDates have been added
	* or removed for today.  This function assumes that the "REGULAR" schedule is running unless a CalDate specifies otherwise.
	* The use of GTFS exception_type (1 or 2) is evaluated to determine if a schedule has been removed and if so, has it been replaced 
	* by a "SPECIAL" schedule.
	* 
	* Since VRE does not run on Weekends, the determination is also evaluated.
	* 
	* Possible Outcomes:
	* 
	* "REGULAR" - Default Schedule
	* "NOSCHEDULEWKND" - No Schedule due to today being a weekend day
	* "NOSCHEDULE" - The schedule was dropped and there is not special schedule to replace it (ie, Christmas Day or most Fed Holidays)
	* "SPECIAL" - The schedule was dropped but replaced by a special schedule, (ie, Day after Thanksgiving)
	*************************************************************************************************************************************/	
	public static String TodaysScheduleType(Context context, ArrayList<CalDates> allCalDates, boolean ignoreWeekend){	
				
		//Today's Date
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date today = new Date();
		Date todayWithZeroTime = null;
		try {
			todayWithZeroTime = formatter.parse(formatter.format(today));
		} catch (ParseException e) {		
			//e.printStackTrace();
		}
		
		String SchedReturn = "REGULAR";
		
		//GTFS- A value of 1 indicates that service has been added for the specified date.
		int exception_type_added = 1;		//NO Magic Number: Documented by GTFS
		//GTFS- A value of 2 indicates that service has been removed for the specified date.
		int exception_type_removed = 2;		//NO Magic Number: Documented by GTFS
		
		try{			
			//If Weekend, Exit Function
			if(IsItTheWeekend()){
				
				if (ignoreWeekend)
					SchedReturn = "REGULAR";  
				else
					SchedReturn = "NOSCHEDULEWKND";
			}
			else
			{	
				//Loop through all CalDates and compare the evaluation criteria (ServiceId and SchedType) for the Station
				for (CalDates each : allCalDates){	
											
					//If there is a replacement "SPECIAL" schedule for the station, find it here, else return NOSCHEDULE
					if(compareDates(ConvertStringDate(each.CALDATES_DATE(), "yyyyMMdd"), todayWithZeroTime) && 
						each.CALDATES_EXCEPTION_TYPE().equals(exception_type_added)){
						SchedReturn = "SPECIAL";
					}										
				}
				
				if (SchedReturn=="REGULAR"){
					
					//Loop through all CalDates and compare the evaluation criteria (ServiceId and SchedType) for the Station
					for (CalDates each : allCalDates){					
						
						//Check for dropped schedules for today 1st
						if(compareDates(ConvertStringDate(each.CALDATES_DATE(), "yyyyMMdd"), todayWithZeroTime) && 
							each.CALDATES_EXCEPTION_TYPE().equals(exception_type_removed)){
							SchedReturn = "NOSCHEDULE";
						}										
					}
				}
			}
		}
		catch(Exception ex)
		{
			//Log.d(TAG, "CalDate Error " + ex.toString()); 
		}
		
		//Return Outcome
		return SchedReturn;		
	}
}
