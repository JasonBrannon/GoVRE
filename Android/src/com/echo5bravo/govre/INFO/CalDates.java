package com.echo5bravo.govre.INFO;

public class CalDates {
	
	//PRIVATE MEMBERS	
	private String _SERVICE_ID = null;	
	private String _CALDATES_DATE = null;	
	private Integer _CALDATES_EXCEPTION_TYPE;

	
	//PUBLIC ACCESSORS
	public String SERVICE_ID()
	{  
	    return this._SERVICE_ID;
	}
	public void SERVICE_ID(String value)
	{	  
	    this._SERVICE_ID = value;
	}
	
	public String CALDATES_DATE()
	{  
	    return this._CALDATES_DATE;
	}
	public void CALDATES_DATE(String value)
	{	  
	    this._CALDATES_DATE = value;
	}
	
	public Integer CALDATES_EXCEPTION_TYPE()
	{  
	    return this._CALDATES_EXCEPTION_TYPE;
	}
	public void CALDATES_EXCEPTION_TYPE(Integer value)
	{	  
	    this._CALDATES_EXCEPTION_TYPE = value;
	}
	
	
	//CONSTRUCTORS
	public CalDates() {}	
			
	public CalDates(String SERVICE_ID, String CALDATES_DATE, Integer CALDATES_EXCEPTION_TYPE
				) {
			
		this._SERVICE_ID = SERVICE_ID;
		this._CALDATES_DATE = CALDATES_DATE;
		this._CALDATES_EXCEPTION_TYPE = CALDATES_EXCEPTION_TYPE;

	}
}
