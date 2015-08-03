package com.echo5bravo.govre.INFO;

public class Alert {
	
	//PRIVATE MEMBERS
	private String _ALERT_TRAIN_NUM = null;
	private String _ALERT_MESSAGE = null;
	private String _ALERT_DT_UPDATE = null;
	private String _ALERT_MINUTES_DELAY = null;
	private boolean _ALERT_TRAIN_DELAYED;
	private boolean _ALERT_TRAIN_CANCELLED;
	
	//PUBLIC ACCESSORS
	public String getALERT_TRAIN_NUM()
	{  
	    return this._ALERT_TRAIN_NUM;
	}
	public void setALERT_TRAIN_NUM(String value)
	{	  
	    this._ALERT_TRAIN_NUM = value;
	}
		
	public String getALERT_MESSAGE()
	{  
	    return this._ALERT_MESSAGE;
	}
	public void setALERT_MESSAGE(String value)
	{	  
	    this._ALERT_MESSAGE = value;
	}
	
	public String getALERT_DT_UPDATE()
	{  
	    return this._ALERT_DT_UPDATE;
	}
	public void setALERT_DT_UPDATE(String value)
	{	  
	    this._ALERT_DT_UPDATE = value;
	}
	
	public String getALERT_MINUTES_DELAY()
	{  
	    return this._ALERT_MINUTES_DELAY;
	}
	public void setALERT_MINUTES_DELAY(String value)
	{	  
	    this._ALERT_MINUTES_DELAY = value;
	}
	
	public Boolean getALERT_TRAIN_DELAYED ()
	{  
	    return this._ALERT_TRAIN_DELAYED ;
	}
	public void setALERT_TRAIN_DELAYED (boolean value)
	{	  
	    this._ALERT_TRAIN_DELAYED = value;
	}
	
	public Boolean getALERT_TRAIN_CANCELLED()
	{  
	    return this._ALERT_TRAIN_CANCELLED;
	}
	public void setALERT_TRAIN_CANCELLED(boolean value)
	{	  
	    this._ALERT_TRAIN_CANCELLED = value;
	}
	
	//CONSTRUCTORS
	public Alert() {}	
			
	public Alert(String sAlertTrainNum
				, String sAlertMessage
				, String sAlertDtUpdate
				, String sAlertMinutesDelay
				, boolean sAlertTrainDelayed
				, boolean sAlertTrainCancelled) {
		
		this._ALERT_TRAIN_NUM = sAlertTrainNum;
		this._ALERT_MESSAGE = sAlertMessage;
		this._ALERT_DT_UPDATE = sAlertDtUpdate;
		this._ALERT_MINUTES_DELAY = sAlertMinutesDelay;
		this._ALERT_TRAIN_DELAYED = sAlertTrainDelayed;
		this._ALERT_TRAIN_CANCELLED = sAlertTrainCancelled;
	}

}
