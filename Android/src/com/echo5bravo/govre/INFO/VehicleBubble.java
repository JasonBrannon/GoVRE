package com.echo5bravo.govre.INFO;

public class VehicleBubble {
	
	//PRIVATE MEMBERS
	private String _VEHICLE_ID = null;		//ex: 303 
	private String _VEHICLE_LABLE = null;	//ex: 303 / V53
	private String _STATUS = null;			//ex: Delayed 10 minutes or On-Time or At Station
	private String _NEXT_STOP = null;		//ex: Quantico
	private String _NEXT_STOP_AND_TIME = null; //ex: Quantico, 2:20 PM
	private String _HEADING = null;			//ex: Union Station, 4:30 PM
	private String _STOPS_REMAINING = null;	//ex: 4 of 12 Stops Remaining
	private boolean _AT_STATION	= false; 	//ex: identifies if the train is at the station if true
	private boolean _DELAYED	= false;	//ex: identifies if the train is running under delay if true
	private boolean _LOST_COMM	= false;	//ex: identifies if GTFS has lost communications with train if true
	private String _CURRENT_STOP = null;	//ex: Quantico
		
	//PUBLIC ACCESSORS
	public String VEHICLE_ID()
	{  
	    return this._VEHICLE_ID;
	}
	public void VEHICLE_ID(String value)
	{	  
	    this._VEHICLE_ID = value;
	}
	
	public String VEHICLE_LABLE()
	{  
	    return this._VEHICLE_LABLE;
	}
	public void VEHICLE_LABLE(String value)
	{	  
	    this._VEHICLE_LABLE = value;
	}
	
	public String STATUS()
	{  
	    return this._STATUS;
	}
	public void STATUS(String value)
	{	  
	    this._STATUS = value;
	}
	
	public String NEXT_STOP()
	{  
	    return this._NEXT_STOP;
	}
	public void NEXT_STOP(String value)
	{	  
	    this._NEXT_STOP = value;
	}
	
	public String NEXT_STOP_AND_TIME()
	{  
	    return this._NEXT_STOP_AND_TIME;
	}
	public void NEXT_STOP_AND_TIME(String value)
	{	  
	    this._NEXT_STOP_AND_TIME = value;
	}
	
	public String HEADING()
	{  
	    return this._HEADING;
	}
	public void HEADING(String value)
	{	  
	    this._HEADING = value;
	}
	
	public String STOPS_REMAINING()
	{  
	    return this._STOPS_REMAINING;
	}
	public void STOPS_REMAINING(String value)
	{	  
	    this._STOPS_REMAINING = value;
	}
	
	public String CURRENT_STOP()
	{  
	    return this._CURRENT_STOP;
	}
	public void CURRENT_STOP(String value)
	{	  
	    this._CURRENT_STOP = value;
	}
	
	public boolean AT_STATION()
	{  
	    return this._AT_STATION;
	}
	public void AT_STATION(boolean value)
	{	  
	    this._AT_STATION = value;
	}
	
	public boolean DELAYED()
	{  
	    return this._DELAYED;
	}
	public void DELAYED(boolean value)
	{	  
	    this._DELAYED = value;
	}
	
	public boolean LOST_COMM()
	{  
	    return this._LOST_COMM;
	}
	public void LOST_COMM(boolean value)
	{	  
	    this._LOST_COMM = value;
	}
	
	//CONSTRUCTORS
	public VehicleBubble() {}	

}
