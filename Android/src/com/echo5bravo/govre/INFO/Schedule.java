package com.echo5bravo.govre.INFO;

public class Schedule{
	
	//PRIVATE MEMBERS
	private Integer _ROUTE_ID;
	private String _SERVICE_ID = null;
	private String _TRIP_ID = null;
	private String _TRIP_HEADSIGN = null;
	private String _TRIP_SHORT_NAME = null;
	private Integer _TRIP_DIRECTION_ID;
	private String _TRIP_BLOCK_ID = null;
	private Integer _TRIP_SHAPE_ID;
	private String _SCHEDULE_TYPE = null;
	private String _SCHEDULE_ARRIVAL_TIME = null;
	private String _SCHEDULE_ARRIVAL_TIME_ACTUAL = null;	
	private String _SCHEDULE_DEPARTURE_TIME = null;
	private String _SCHEDULE_DEPARTURE_TIME_ACTUAL = null;
	private String _SCHEDULE_STOP_ID = null;
	private Integer _SCHEDULE_STOP_SEQUENCE;
	private String _SCHEDULE_STOP_HEADSIGN = null;
	private Integer _SCHEDULE_STOP_PICK_TYPE;
	private Integer _SCHEDULE_STOP_DROP_TYPE;
	private String _SCHEDULE_STOP_DIST_TRAV = null;
	private String _MSG_ESTIMATED_TIME_ARRIVAL = null;
	private String _MSG_ALERT = null;
	private String _STATION_ABBREVIATION = null;
	private String _STATION_STOP_NAME = null;

	
	//PUBLIC ACCESSORS
	public Integer ROUTE_ID()
	{  
	    return this._ROUTE_ID;
	}
	public void ROUTE_ID(Integer value)
	{	  
	    this._ROUTE_ID = value;
	}
	
	public String SERVICE_ID()
	{  
	    return this._SERVICE_ID;
	}
	public void SERVICE_ID(String value)
	{	  
	    this._SERVICE_ID = value;
	}
	
	public String TRIP_ID()
	{  
	    return this._TRIP_ID;
	}
	public void TRIP_ID(String value)
	{	  
	    this._TRIP_ID = value;
	}
	
	public String TRIP_HEADSIGN()
	{  
	    return this._TRIP_HEADSIGN;
	}
	public void TRIP_HEADSIGN(String value)
	{	  
	    this._TRIP_HEADSIGN = value;
	}
	
	public String TRIP_SHORT_NAME()
	{  
	    return this._TRIP_SHORT_NAME;
	}
	public void TRIP_SHORT_NAME(String value)
	{	  
	    this._TRIP_SHORT_NAME = value;
	}
	
	public Integer TRIP_DIRECTION_ID()
	{  
	    return this._TRIP_DIRECTION_ID;
	}
	public void TRIP_DIRECTION_ID(Integer value)
	{	  
	    this._TRIP_DIRECTION_ID = value;
	}
	
	public String TRIP_BLOCK_ID()
	{  
	    return this._TRIP_BLOCK_ID;
	}
	public void TRIP_BLOCK_ID(String value)
	{	  
	    this._TRIP_BLOCK_ID = value;
	}
	
	public Integer TRIP_SHAPE_ID()
	{  
	    return this._TRIP_SHAPE_ID;
	}
	public void TRIP_SHAPE_ID(Integer value)
	{	  
	    this._TRIP_SHAPE_ID = value;
	}
	
	public String SCHEDULE_TYPE()
	{  
	    return this._SCHEDULE_TYPE;
	}
	public void SCHEDULE_TYPE(String value)
	{	  
	    this._SCHEDULE_TYPE = value;
	}
	
	public String SCHEDULE_ARRIVAL_TIME()
	{  
	    return this._SCHEDULE_ARRIVAL_TIME;
	}
	public void SCHEDULE_ARRIVAL_TIME(String value)
	{	  
	    this._SCHEDULE_ARRIVAL_TIME = value;
	}
	
	//Contains the Arrival Time but calculated with Delay to produce the Actual
	public String SCHEDULE_ARRIVAL_TIME_ACTUAL()
	{  
	    return this._SCHEDULE_ARRIVAL_TIME_ACTUAL;
	}
	public void SCHEDULE_ARRIVAL_TIME_ACTUAL(String value)
	{	  
	    this._SCHEDULE_ARRIVAL_TIME_ACTUAL = value;
	}
	
	public String SCHEDULE_DEPARTURE_TIME()
	{  
	    return this._SCHEDULE_DEPARTURE_TIME;
	}
	public void SCHEDULE_DEPARTURE_TIME(String value)
	{	  
	    this._SCHEDULE_DEPARTURE_TIME = value;
	}
	
	//Contains the Departure Time but calculated with Delay to produce the Actual
	public String SCHEDULE_DEPARTURE_TIME_ACTUAL()
	{  
	    return this._SCHEDULE_DEPARTURE_TIME_ACTUAL;
	}
	public void SCHEDULE_DEPARTURE_TIME_ACTUAL(String value)
	{	  
	    this._SCHEDULE_DEPARTURE_TIME_ACTUAL = value;
	}
	
	public String SCHEDULE_STOP_ID()
	{  
	    return this._SCHEDULE_STOP_ID;
	}
	public void SCHEDULE_STOP_ID(String value)
	{	  
	    this._SCHEDULE_STOP_ID = value;
	}
	
	public Integer SCHEDULE_STOP_SEQUENCE()
	{  
	    return this._SCHEDULE_STOP_SEQUENCE;
	}
	public void SCHEDULE_STOP_SEQUENCE(Integer value)
	{	  
	    this._SCHEDULE_STOP_SEQUENCE = value;
	}
	
	public String SCHEDULE_STOP_HEADSIGN()
	{  
	    return this._SCHEDULE_STOP_HEADSIGN;
	}
	public void SCHEDULE_STOP_HEADSIGN(String value)
	{	  
	    this._SCHEDULE_STOP_HEADSIGN = value;
	}
	
	public Integer SCHEDULE_STOP_PICK_TYPE()
	{  
	    return this._SCHEDULE_STOP_PICK_TYPE;
	}
	public void SCHEDULE_STOP_PICK_TYPE(Integer value)
	{	  
	    this._SCHEDULE_STOP_PICK_TYPE = value;
	}
	
	
	public Integer SCHEDULE_STOP_DROP_TYPE()
	{  
	    return this._SCHEDULE_STOP_DROP_TYPE;
	}
	public void SCHEDULE_STOP_DROP_TYPE(Integer value)
	{	  
	    this._SCHEDULE_STOP_DROP_TYPE = value;
	}
	
	public String SCHEDULE_STOP_DIST_TRAV()
	{  
	    return this._SCHEDULE_STOP_DIST_TRAV;
	}
	public void SCHEDULE_STOP_DIST_TRAV(String value)
	{	  
	    this._SCHEDULE_STOP_DIST_TRAV = value;
	}
	
	public String MSG_ESTIMATED_TIME_ARRIVAL()
	{  
	    return this._MSG_ESTIMATED_TIME_ARRIVAL;
	}
	public void MSG_ESTIMATED_TIME_ARRIVAL(String value)
	{	  
	    this._MSG_ESTIMATED_TIME_ARRIVAL = value;
	}
	
	public String MSG_ALERT()
	{  
	    return this._MSG_ALERT;
	}
	public void MSG_ALERT(String value)
	{	  
	    this._MSG_ALERT = value;
	}	
	
	public String STATION_ABBREVIATION()
	{  
	    return this._STATION_ABBREVIATION;
	}
	public void STATION_ABBREVIATION(String value)
	{	  
	    this._STATION_ABBREVIATION = value;
	}
	
	public String STATION_STOP_NAME()
	{  
	    return this._STATION_STOP_NAME;
	}
	public void STATION_STOP_NAME(String value)
	{	  
	    this._STATION_STOP_NAME = value;
	}
	
	//CONSTRUCTORS
	public Schedule() {}	
			
	public Schedule(Integer ROUTE_ID,
			String SERVICE_ID,
			String TRIP_ID,
			String TRIP_HEADSIGN,
			String TRIP_SHORT_NAME,
			Integer TRIP_DIRECTION_ID,
			String TRIP_BLOCK_ID,
			Integer TRIP_SHAPE_ID,
			String SCHEDULE_TYPE,
			String SCHEDULE_ARRIVAL_TIME,
			String SCHEDULE_DEPARTURE_TIME,
			String SCHEDULE_STOP_ID,
			Integer SCHEDULE_STOP_SEQUENCE,
			String SCHEDULE_STOP_HEADSIGN,
			Integer SCHEDULE_STOP_PICK_TYPE,
			Integer SCHEDULE_STOP_DROP_TYPE,
			String SCHEDULE_STOP_DIST_TRAV,
			String ESTIMATED_TIME_ARRIVAL
				) {		
	
		this._ROUTE_ID = ROUTE_ID;
		this._SERVICE_ID = SERVICE_ID;
		this._TRIP_ID = TRIP_ID;
		this._TRIP_HEADSIGN = TRIP_HEADSIGN;
		this._TRIP_SHORT_NAME = TRIP_SHORT_NAME;
		this._TRIP_DIRECTION_ID = TRIP_DIRECTION_ID;
		this._TRIP_BLOCK_ID = TRIP_BLOCK_ID;
		this._TRIP_SHAPE_ID = TRIP_SHAPE_ID;
		this._SCHEDULE_TYPE = SCHEDULE_TYPE;
		this._SCHEDULE_ARRIVAL_TIME = SCHEDULE_ARRIVAL_TIME;
		this._SCHEDULE_DEPARTURE_TIME = SCHEDULE_DEPARTURE_TIME;
		this._SCHEDULE_STOP_ID = SCHEDULE_STOP_ID;
		this._SCHEDULE_STOP_SEQUENCE = SCHEDULE_STOP_SEQUENCE;
		this._SCHEDULE_STOP_HEADSIGN = SCHEDULE_STOP_HEADSIGN;
		this._SCHEDULE_STOP_PICK_TYPE = SCHEDULE_STOP_PICK_TYPE;
		this._SCHEDULE_STOP_DROP_TYPE = SCHEDULE_STOP_DROP_TYPE;
		this._SCHEDULE_STOP_DIST_TRAV = SCHEDULE_STOP_DIST_TRAV;
	}
		
}