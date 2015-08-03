package com.echo5bravo.govre.INFO;

public class Vehicle {
	
	//PRIVATE MEMBERS
	private String _VEHICLE_ID = null;
	private String _VEHICLE_LABEL = null;
	private String _VEHICLE_LATITUDE = null;
	private String _VEHCILE_LONGITUDE = null;
	private String _TRIP_ID = null;
	private Integer _ROUTE_ID;
	private String _TIMESTAMP = null;
	private Integer _STOP_SEQUENCE;
	private String _STOP_ID = null;
	private String _CURRENT_STATUS = null;
	
	//PUBLIC ACCESSORS
	public String VEHICLE_ID()
	{  
	    return this._VEHICLE_ID;
	}
	public void VEHICLE_ID(String value)
	{	  
	    this._VEHICLE_ID = value;
	}
	
	public String VEHICLE_LABEL()
	{		
	    return this._VEHICLE_LABEL.replace(" - Wabtec", "");
	}
	public void VEHICLE_LABEL(String value)
	{	  
	    this._VEHICLE_LABEL = value;
	}
	
	public String VEHICLE_LATITUDE()
	{  
	    return this._VEHICLE_LATITUDE;
	}
	public void VEHICLE_LATITUDE(String value)
	{	  
	    this._VEHICLE_LATITUDE = value;
	}
	
	public String VEHCILE_LONGITUDE()
	{  
	    return this._VEHCILE_LONGITUDE;
	}
	public void VEHCILE_LONGITUDE(String value)
	{	  
	    this._VEHCILE_LONGITUDE = value;
	}
	
	public String TRIP_ID()
	{  
	    return this._TRIP_ID;
	}
	public void TRIP_ID(String value)
	{	  
	    this._TRIP_ID = value;
	}
	
	public Integer ROUTE_ID()
	{  
	    return this._ROUTE_ID;
	}
	public void ROUTE_ID(Integer value)
	{	  
	    this._ROUTE_ID = value;
	}
	
	public String TIMESTAMP()
	{  
	    return this._TIMESTAMP;
	}
	public void TIMESTAMP(String value)
	{	  
	    this._TIMESTAMP = value;
	}
	
	public Integer STOP_SEQUENCE()
	{  
	    return this._STOP_SEQUENCE;
	}
	public void STOP_SEQUENCE(Integer value)
	{	  
	    this._STOP_SEQUENCE = value;
	}
	
	public String STOP_ID()
	{  
	    return this._STOP_ID;
	}
	public void STOP_ID(String value)
	{	  
	    this._STOP_ID = value;
	}
	
	public String CURRENT_STATUS()
	{  
	    return this._CURRENT_STATUS;
	}
	public void CURRENT_STATUS(String value)
	{	  
	    this._CURRENT_STATUS = value;
	}
		
	//CONSTRUCTORS
	public Vehicle() {}	
			
	public Vehicle(String VEHICLE_ID,
					String VEHICLE_LABEL,
					String VEHICLE_LATITUDE,
					String VEHCILE_LONGITUDE,
					String TRIP_ID,
					Integer ROUTE_ID,
					String TIMESTAMP,
					Integer STOP_SEQUENCE,
					String STOP_ID,
					String CURRENT_STATUS
				) {
		this._VEHICLE_ID = VEHICLE_ID;
		this._VEHICLE_LABEL = VEHICLE_LABEL;
		this._VEHICLE_LATITUDE = VEHICLE_LATITUDE;
		this._VEHCILE_LONGITUDE = VEHCILE_LONGITUDE;
		this._TRIP_ID = TRIP_ID;
		this._ROUTE_ID = ROUTE_ID;
		this._TIMESTAMP = TIMESTAMP;
		this._STOP_SEQUENCE = STOP_SEQUENCE;
		this._STOP_ID = STOP_ID;
		this._CURRENT_STATUS = CURRENT_STATUS;
	}
}

