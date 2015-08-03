package com.echo5bravo.govre.INFO;

public class Route {
	
	//PRIVATE MEMBERS	
	private Integer _ROUTE_ID;
	private String _ROUTE_ID_TYPE = null;	
	private String _ROUTE_AGENCY_ID = null;	
	private String _ROUTE_SHORT_NAME = null;	
	private String _ROUTE_LONG_NAME = null;	
	private String _ROUTE_DESC = null;	
	private Integer _ROUTE_TYPE;
	private String _ROUTE_URL = null;	
	private String _ROUTE_COLOR = null;	
	private String _ROUTE_TEXT_COLOR = null;	
	private String _ROUTE_LATITUDE = null;	
	private String _ROUTE_LONGITUDE = null;	
	private Integer _ROUTE_SEQUENCE;
	private String _ROUTE_DIST_TRAVELED = null;	

	
	//PUBLIC ACCESSORS
	public Integer ROUTE_ID()
	{  
	    return this._ROUTE_ID;
	}
	public void ROUTE_ID(Integer value)
	{	  
	    this._ROUTE_ID = value;
	}
	
	public String ROUTE_ID_TYPE()
	{  
	    return this._ROUTE_ID_TYPE;
	}
	public void ROUTE_ID_TYPE(String value)
	{	  
	    this._ROUTE_ID_TYPE = value;
	}
	
	public String ROUTE_AGENCY_ID()
	{  
	    return this._ROUTE_AGENCY_ID;
	}
	public void ROUTE_AGENCY_ID(String value)
	{	  
	    this._ROUTE_AGENCY_ID = value;
	}
	
	public String ROUTE_SHORT_NAME()
	{  
	    return this._ROUTE_SHORT_NAME;
	}
	public void ROUTE_SHORT_NAME(String value)
	{	  
	    this._ROUTE_SHORT_NAME = value;
	}
	
	public String ROUTE_LONG_NAME()
	{  
	    return this._ROUTE_LONG_NAME;
	}
	public void ROUTE_LONG_NAME(String value)
	{	  
	    this._ROUTE_LONG_NAME = value;
	}
	
	public String ROUTE_DESC()
	{  
	    return this._ROUTE_DESC;
	}
	public void ROUTE_DESC(String value)
	{	  
	    this._ROUTE_DESC = value;
	}
	
	public Integer ROUTE_TYPE()
	{  
	    return this._ROUTE_TYPE;
	}
	public void ROUTE_TYPE(Integer value)
	{	  
	    this._ROUTE_TYPE = value;
	}
	
	public String ROUTE_URL()
	{  
	    return this._ROUTE_URL;
	}
	public void ROUTE_URL(String value)
	{	  
	    this._ROUTE_URL = value;
	}
	
	public String ROUTE_COLOR()
	{  
	    return this._ROUTE_COLOR;
	}
	public void ROUTE_COLOR(String value)
	{	  
	    this._ROUTE_COLOR = value;
	}
	
	public String ROUTE_TEXT_COLOR()
	{  
	    return this._ROUTE_TEXT_COLOR;
	}
	public void ROUTE_TEXT_COLOR(String value)
	{	  
	    this._ROUTE_TEXT_COLOR = value;
	}
	
	public String ROUTE_LATITUDE()
	{  
	    return this._ROUTE_LATITUDE;
	}
	public void ROUTE_LATITUDE(String value)
	{	  
	    this._ROUTE_LATITUDE = value;
	}
	
	public String ROUTE_LONGITUDE()
	{  
	    return this._ROUTE_LONGITUDE;
	}
	public void ROUTE_LONGITUDE(String value)
	{	  
	    this._ROUTE_LONGITUDE = value;
	}
	
	public Integer ROUTE_SEQUENCE()
	{  
	    return this._ROUTE_SEQUENCE;
	}
	public void ROUTE_SEQUENCE(Integer value)
	{	  
	    this._ROUTE_SEQUENCE = value;
	}
	
	public String ROUTE_DIST_TRAVELED()
	{  
	    return this._ROUTE_DIST_TRAVELED;
	}
	public void ROUTE_DIST_TRAVELED(String value)
	{	  
	    this._ROUTE_DIST_TRAVELED = value;
	}
	
	
	//CONSTRUCTORS
	public Route() {}	
			
	public Route(Integer ROUTE_ID,			
			String ROUTE_ID_TYPE, 
			String ROUTE_AGENCY_ID,
			String ROUTE_SHORT_NAME, 
			String ROUTE_LONG_NAME, 
			String ROUTE_DESC, 
			Integer ROUTE_TYPE,
			String ROUTE_URL, 
			String ROUTE_COLOR,
			String ROUTE_TEXT_COLOR, 
			String ROUTE_LATITUDE, 
			String ROUTE_LONGITUDE, 
			Integer ROUTE_SEQUENCE,
			String ROUTE_DIST_TRAVELED 
				) {	
		
		this._ROUTE_ID = ROUTE_ID;
		this._ROUTE_ID_TYPE = ROUTE_ID_TYPE;
		this._ROUTE_AGENCY_ID = ROUTE_AGENCY_ID;
		this._ROUTE_SHORT_NAME = ROUTE_SHORT_NAME;
		this._ROUTE_LONG_NAME = ROUTE_LONG_NAME;
		this._ROUTE_DESC = ROUTE_DESC;
		this._ROUTE_TYPE = ROUTE_TYPE;
		this._ROUTE_URL = ROUTE_URL; 
		this._ROUTE_COLOR = ROUTE_COLOR; 
		this._ROUTE_TEXT_COLOR = ROUTE_TEXT_COLOR;
		this._ROUTE_LATITUDE = ROUTE_LATITUDE;
		this._ROUTE_LONGITUDE = ROUTE_LONGITUDE;
		this._ROUTE_SEQUENCE = ROUTE_SEQUENCE;
		this._ROUTE_DIST_TRAVELED = ROUTE_DIST_TRAVELED;
	}
}

