package com.echo5bravo.govre.INFO;

import android.location.Location;

public class Station implements Comparable<Station> {
	
	//PRIVATE MEMBERS	
	private String _STATION_ID = null;
	private String _STATION_STOP_CODE = null;
	private String _STATION_STOP_NAME = null;
	private String _STATION_DESC = null;
	private String _STATION_LATITUDE = null;
	private String _STATION_LONGITUDE = null;
	private String _STATION_ZONE_ID = null;
	private String _STATION_URL = null;
	private Integer _STATION_LOCATION_TYPE;
	private String _STATION_PARENT_STATION = null;
	private String _STATION_TIMEZONE = null;
	private String _STATION_LINES = null;
	private String _MSG_NEXT_DEPARTURE = null;
	private String _MSG_ALERT = null;
	private String _ADDRESS = null;
	private String _CITY = null;
	private String _STATE = null;
	private String _ZIP = null;
	private String _URL = null;
	private String _ZONE = null;
	private Integer _SORT_ORDER;
	private String _ABBREVIATION = null;
	 
	
	public long rowID;
	private Float distance;
	private int bearing;	
	
	public final static String KEY_PROVIDER = "Station";	
	
	//PUBLIC ACCESSORS
	public String STATION_ID()
	{  
	    return this._STATION_ID;
	}
	public void STATION_ID(String value)
	{	  
	    this._STATION_ID = value;
	}
	
	public String STATION_STOP_CODE()
	{  
	    return this._STATION_STOP_CODE;
	}
	public void STATION_STOP_CODE(String value)
	{	  
	    this._STATION_STOP_CODE = value;
	}
	
	public String STATION_STOP_NAME()
	{  
		//Clean-up Station Stop Name
		if (this._STATION_STOP_NAME.length() > 20){			
			return this._STATION_STOP_NAME; //.substring(0, 20) + "..";					
		}
		else
		{
			return this._STATION_STOP_NAME;
		}
		
	}
	public void STATION_STOP_NAME(String value)
	{	  
	    this._STATION_STOP_NAME = value;
	}
	
	public String STATION_DESC()
	{  
	    return this._STATION_DESC;
	}
	public void STATION_DESC(String value)
	{	  
	    this._STATION_DESC = value;
	}
	
	public String STATION_LATITUDE()
	{  
	    return this._STATION_LATITUDE;
	}
	public void STATION_LATITUDE(String value)
	{	  
	    this._STATION_LATITUDE = value;
	}
	
	public String STATION_LONGITUDE()
	{  
	    return this._STATION_LONGITUDE;
	}
	public void STATION_LONGITUDE(String value)
	{	  
	    this._STATION_LONGITUDE = value;
	}
	
	public String STATION_ZONE_ID()
	{  
	    return this._STATION_ZONE_ID;
	}
	public void STATION_ZONE_ID(String value)
	{	  
	    this._STATION_ZONE_ID = value;
	}
	
	public String STATION_URL()
	{  
	    return this._STATION_URL;
	}
	public void STATION_URL(String value)
	{	  
	    this._STATION_URL = value;
	}
	
	public Integer STATION_LOCATION_TYPE()
	{  
	    return this._STATION_LOCATION_TYPE;
	}
	public void STATION_LOCATION_TYPE(Integer value)
	{	  
	    this._STATION_LOCATION_TYPE = value;
	}
	
	public String STATION_PARENT_STATION()
	{  
	    return this._STATION_PARENT_STATION;
	}
	public void STATION_PARENT_STATION(String value)
	{	  
	    this._STATION_PARENT_STATION = value;
	}
	
	public String STATION_TIMEZONE()
	{  
	    return this._STATION_TIMEZONE;
	}
	public void STATION_TIMEZONE(String value)
	{	  
	    this._STATION_TIMEZONE = value;
	}
	
	public String STATION_LINES()
	{  
		//Logic Returns Proper LINE Filter (FBG, MSS, ALL)
		//------------------------------------------------------------
		//VRE GTFS Designates STATION_LINES:
		// 2 = FBG (Fredericksburg/RED)
		// 4 = MSS (Manassas/BLUE)
		//------------------------------------------------------------
		boolean containsFBG = this._STATION_LINES.indexOf("2") != -1;
		boolean containsMSS = this._STATION_LINES.indexOf("4") != -1;
		
		if (containsFBG && containsMSS)
			return "ALL";
		else if (containsMSS)
			return "MSS";
		else if (containsFBG)
			return "FBG";
		else
			return "";	   
	}
	public void STATION_LINES(String value)
	{	  
	    this._STATION_LINES = value;
	}
	
	public String MSG_NEXT_DEPARTURE()
	{  
		if (_MSG_NEXT_DEPARTURE==null || _MSG_NEXT_DEPARTURE=="")
			return "Train Operations Ended";
		else
			return this._MSG_NEXT_DEPARTURE;
	}
	public void MSG_NEXT_DEPARTURE(String value)
	{	  
		 this._MSG_NEXT_DEPARTURE = value;
	}
	
	public String MSG_ALERT()
	{  
		if (_MSG_ALERT==null || _MSG_ALERT=="")
			return "Press for Schedule";
		else
			return this._MSG_ALERT;
	}
	public void MSG_ALERT(String value)
	{	  
	    this._MSG_ALERT = value;
	}
	
	public String ADDRESS()
	{  
	    return this._ADDRESS;
	}
	public void ADDRESS(String value)
	{	  
	    this._ADDRESS = value;
	}
	
	public String CITY()
	{  
	    return this._CITY;
	}
	public void CITY(String value)
	{	  
	    this._CITY = value;
	}
	
	public String STATE()
	{  
	    return this._STATE;
	}
	public void STATE(String value)
	{	  
	    this._STATE = value;
	}
	
	public String ZIP()
	{  
	    return this._ZIP;
	}
	public void ZIP(String value)
	{	  
	    this._ZIP = value;
	}
	
	public String URL()
	{  
	    return this._URL;
	}
	public void URL(String value)
	{	  
	    this._URL = value;
	}
	
	public String ZONE()
	{  
	    return "Zone: " + this._ZONE;
	}
	public void ZONE(String value)
	{	  
	    this._ZONE = value;
	}
	
	public Integer SORT_ORDER()
	{  
		return _SORT_ORDER;
	}
	public void SORT_ORDER(Integer value)
	{	  
	    this._SORT_ORDER = value;
	}
	
	public String ABBREVIATION()
	{  
	    return this._ABBREVIATION;
	}
	public void ABBREVIATION(String value)
	{	  
	    this._ABBREVIATION = value;
	}
	
	
	//CONSTRUCTORS
	public Station() {}	
			
	public Station(String STATION_ID,
					String STATION_STOP_CODE,
					String STATION_STOP_NAME,
					String STATION_DESC,
					String STATION_LATITUDE,
					String STATION_LONGITUDE,
					String STATION_ZONE_ID,
					String STATION_URL,
					Integer STATION_LOCATION_TYPE,
					String STATION_PARENT_STATION,
					String STATION_TIMEZONE,
					String STATION_LINES
				) {
		
		this._STATION_ID = STATION_ID;
		this._STATION_STOP_CODE = STATION_STOP_CODE;
		this._STATION_STOP_NAME = STATION_STOP_NAME;
		this._STATION_DESC = STATION_DESC;
		this._STATION_LATITUDE = STATION_LATITUDE;
		this._STATION_LONGITUDE = STATION_LONGITUDE;
		this._STATION_ZONE_ID = STATION_ZONE_ID;
		this._STATION_URL = STATION_URL;
		this._STATION_LOCATION_TYPE = STATION_LOCATION_TYPE;
		this._STATION_PARENT_STATION = STATION_PARENT_STATION ;
		this._STATION_TIMEZONE = STATION_TIMEZONE; 
		this._STATION_LINES = STATION_LINES;
	}
	
	//Returns Distance in MILE string format.
  	public String getDistance(){
  		 if (distance == null) return "NULL";  //Return a message to indicate no connectivity
  		 
  			 return String.format("%.1f mi", distance);
  	}

  	//Re-calculate Distance to update device when the GeoLoc has changed
  	//Closer or Further away from Station.
  	public void calculateDistance(Location locus) {	
  		
  		if (!(locus==null)){
  			
  			float[] results = new float[2];
  			
  			// The method has an odd result for bearing:  W is -90, E is 90, N is 0, S is +/-180.
  			Location.distanceBetween(locus.getLatitude(), locus.getLongitude(), Double.parseDouble(this.STATION_LATITUDE()), Double.parseDouble(this.STATION_LONGITUDE()), results);
  			
  			// The distance comes back in meters which is a bit too precise for our needs: switch to km.
  			//distance = results[0] / 1000f;  	  //For Kilometers
  			  distance = results[0] / 1609.344f;  //For Miles
  			//distance = results[0];  			  //For Meters
   			bearing = (int)results[1];
  			if (bearing < 0) bearing += 360;  // Convert things so we are [0,360].	
  		}
  	}

  	/*
  	 * This returns true if the Station's distance is within the given range.
  	 */
  	public boolean isWithinRange(int min, int max) {
  		if (distance == null) return false;  // We haven't a clue of our relative position yet.
  		return (distance <= max) && (distance >= min);
  	}

  	/*
  	 * This returns true if the Station's bearing is within a certain tolerance of the given heading.
  	 */
  	public boolean isWithinHeading(int heading) {
  		if (distance == null) return false;  // We haven't a clue of our relative position yet.

  		//final int tolerance = 10;  // We allow up to 10 degrees either side as being in view.  --Removed 
  		final int tolerance = 360;  // Use 360 so all locations display no matter the heading
  		return (Math.abs(bearing - heading) < tolerance);
  	}

  	/*
  	 * This returns a combination of the name and the distance if we have it.
  	 */
  	public String getTitle(){
  		StringBuilder builder = new StringBuilder();
  		builder.append(this.STATION_STOP_NAME());
  		if (distance != null)
  			builder.append(String.format("  %.1f mi", distance));
  		
  		return builder.toString();		
  	}
  	
  	/*
  	 * What is the Station's bearing?
  	 */
  	public int getBearing(){
  		return bearing;
  	}
  	
  	public float getRange(){
  		return distance;
  	}
  	
  	/*
  	 * How far off this heading is the Station?
  	 */
  	public int getBearingOffset(int heading){
  		return (bearing - heading);
  	}
  	
  	/*
  	 * A simple comparison of two Stations so the closest comes first.
  	 */
  	public int compareTo(Station other) {
  		if (distance != null)
  			return (distance < other.distance) ? -1 : 1;
  		else
  			return 1;
  	}
}