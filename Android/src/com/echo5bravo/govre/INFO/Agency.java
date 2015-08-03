package com.echo5bravo.govre.INFO;

public class Agency {
	
	//PRIVATE MEMBERS	
	private String _AGENCY_ID = null;	
	private String _AGENCY_NAME = null;	
	private String _AGENCY_URL = null;	
	private String _AGENCY_TIMEZONE = null;	
	private String _AGENCY_LANG = null;	
	private String _AGENCY_PHONE = null;	
	private String _AGENCY_FARE_URL = null;	

	
	//PUBLIC ACCESSORS
	public String AGENCY_ID()
	{  
	    return this._AGENCY_ID;
	}
	public void AGENCY_ID(String value)
	{	  
	    this._AGENCY_ID = value;
	}
	
	public String AGENCY_NAME()
	{  
	    return this._AGENCY_NAME;
	}
	public void AGENCY_NAME(String value)
	{	  
	    this._AGENCY_NAME = value;
	}
	
	public String AGENCY_URL()
	{  
	    return this._AGENCY_URL;
	}
	public void AGENCY_URL(String value)
	{	  
	    this._AGENCY_URL = value;
	}
	
	public String AGENCY_TIMEZONE()
	{  
	    return this._AGENCY_TIMEZONE;
	}
	public void AGENCY_TIMEZONE(String value)
	{	  
	    this._AGENCY_TIMEZONE = value;
	}
	
	public String AGENCY_LANG()
	{  
	    return this._AGENCY_LANG;
	}
	public void AGENCY_LANG(String value)
	{	  
	    this._AGENCY_LANG = value;
	}
	
	public String AGENCY_PHONE()
	{  
	    return this._AGENCY_PHONE;
	}
	public void AGENCY_PHONE(String value)
	{	  
	    this._AGENCY_PHONE = value;
	}
	
	public String AGENCY_FARE_URL()
	{  
	    return this._AGENCY_FARE_URL;
	}
	public void AGENCY_FARE_URL(String value)
	{	  
	    this._AGENCY_FARE_URL = value;
	}
	
	
	//CONSTRUCTORS
	public Agency() {}	
			
	public Agency(String AGENCY_ID,
					String AGENCY_NAME,
					String AGENCY_URL,	
					String AGENCY_TIMEZONE,	
					String AGENCY_LANG,	
					String AGENCY_PHONE,	
					String AGENCY_FARE_URL
				) {	
		
		this._AGENCY_ID = AGENCY_ID;	
		this._AGENCY_NAME = AGENCY_NAME;	
		this._AGENCY_URL = AGENCY_URL;	
		this._AGENCY_TIMEZONE = AGENCY_TIMEZONE;	
		this._AGENCY_LANG = AGENCY_LANG;	
		this._AGENCY_PHONE = AGENCY_PHONE;	
		this._AGENCY_FARE_URL = AGENCY_FARE_URL;
	}
}