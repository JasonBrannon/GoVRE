package com.echo5bravo.govre.INFO;


public class Trip {
	
	//PRIVATE MEMBERS	
	private Integer _ROUTE_ID;
	private String _SERVICE_ID = null;	
	private String _TRIP_ID = null;	
	private String _TRIP_HEADSIGN = null;	
	private String _TRIP_SHORT_NAME = null;	
	private Integer _TRIP_DIRECTION_ID;
	private String _TRIP_BLOCK_ID = null;	
	private Integer _TRIP_SHAPE_ID;
	private String _TRIP_SCHEDULE_TYPE = null;	

	
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
	
	public String TRIP_SCHEDULE_TYPE()
	{  
	    return this._TRIP_SCHEDULE_TYPE;
	}
	public void TRIP_SCHEDULE_TYPE(String value)
	{	  
	    this._TRIP_SCHEDULE_TYPE = value;
	}
	
	
	//CONSTRUCTORS
	public Trip() {}	
			
	public Trip(Integer ROUTE_ID,
				String SERVICE_ID,	
				String TRIP_ID,	
				String TRIP_HEADSIGN,	
				String TRIP_SHORT_NAME,	
				Integer TRIP_DIRECTION_ID,
				String TRIP_BLOCK_ID,	
				Integer TRIP_SHAPE_ID,
				String TRIP_SCHEDULE_TYPE
				) {
				
		this._ROUTE_ID = ROUTE_ID;
		this._SERVICE_ID = SERVICE_ID;	
		this._TRIP_ID = TRIP_ID;	
		this._TRIP_HEADSIGN = TRIP_HEADSIGN;	
		this._TRIP_SHORT_NAME = TRIP_SHORT_NAME;	
		this._TRIP_DIRECTION_ID = TRIP_DIRECTION_ID;
		this._TRIP_BLOCK_ID = TRIP_BLOCK_ID;	
		this._TRIP_SHAPE_ID = TRIP_SHAPE_ID;
		this._TRIP_SCHEDULE_TYPE = TRIP_SCHEDULE_TYPE;
	}
}