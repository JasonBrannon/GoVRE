package com.echo5bravo.govre.INFO;

public class GtfsUpdateTracker {
	
		//PRIVATE MEMBERS	
		private String _FeedName = null;	
		private String _LastUpdated = null;	
		
		//PUBLIC ACCESSORS
		public String FeedName()
		{  
		    return this._FeedName;
		}
		public void FeedName(String value)
		{	  
		    this._FeedName = value;
		}
		
		public String LastUpdated()
		{  
		    return this._LastUpdated;
		}
		public void LastUpdated(String value)
		{	  
		    this._LastUpdated = value;
		}
		
		//CONSTRUCTORS
		public GtfsUpdateTracker() {}	
				
		public GtfsUpdateTracker(String FeedName,
						String LastUpdated
					) {	
			
			this._FeedName = FeedName;	
			this._LastUpdated = LastUpdated;	
		}
}
