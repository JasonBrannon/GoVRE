package com.echo5bravo.govre.INFO;

public class Update {
	
	//PRIVATE MEMBERS
	private String _TRIP_ID = null;
	private String _TRIP_SCHED_RELATION = null;
	private Integer _ROUTE_ID;
	private String _VEHICLE_ID = null;
	private String _VEHICLE_LABEL = null;
	private Integer _STOP_SEQUENCE;
	private String _STOP_ID = null;
	private String _STOP_SCHED_RELATION = null;
	private Integer _DELAY;
	private String  _TIME;	
	
	//PUBLIC ACCESSORS
	public String TRIP_ID()
	{  
	    return this._TRIP_ID;
	}
	public void TRIP_ID(String value)
	{	  
	    this._TRIP_ID = value;
	}
	
	public String TRIP_SCHED_RELATION()
	{  
	    return this._TRIP_SCHED_RELATION;
	}
	public void TRIP_SCHED_RELATION(String value)
	{	  
	    this._TRIP_SCHED_RELATION = value;
	}
	public Integer ROUTE_ID()
	{  
	    return this._ROUTE_ID;
	}
	public void ROUTE_ID(Integer value)
	{	  
	    this._ROUTE_ID = value;
	}
	
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
	    return this._VEHICLE_LABEL;
	}
	public void VEHICLE_LABEL(String value)
	{	  
	    this._VEHICLE_LABEL = value;
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
	
	public String STOP_SCHED_RELATION()
	{  
	    return this._STOP_SCHED_RELATION;
	}
	public void STOP_SCHED_RELATION(String value)
	{	  
	    this._STOP_SCHED_RELATION = value;
	}
	
	public Integer DELAY()
	{  
	    return this._DELAY;
	}
	public void DELAY(Integer value)
	{	  
	    this._DELAY = value;
	}
	
	public String TIME()
	{  
	    return this._TIME;
	}
	public void TIME(String value)
	{	  
	    this._TIME = value;
	}
		
		
	//CONSTRUCTORS
	public Update() {}	
			
	public Update(String TRIP_ID,
					String TRIP_SCHED_RELATION,
					Integer ROUTE_ID,
					String VEHICLE_ID,
					String VEHICLE_LABEL,
					Integer STOP_SEQUENCE,
					String STOP_ID,
					String STOP_SCHED_RELATION,
					Integer DELAY,
					String TIME
				) {
		
		this._TRIP_ID = TRIP_ID;
		this._TRIP_SCHED_RELATION = TRIP_SCHED_RELATION;
		this._ROUTE_ID = ROUTE_ID;
		this._VEHICLE_ID = VEHICLE_ID;
		this._VEHICLE_LABEL = VEHICLE_LABEL;
		this._STOP_SEQUENCE = STOP_SEQUENCE;
		this._STOP_ID = STOP_ID;
		this._STOP_SCHED_RELATION = STOP_SCHED_RELATION;
		this._DELAY = DELAY;
		this._TIME = TIME;
		
	}
}

