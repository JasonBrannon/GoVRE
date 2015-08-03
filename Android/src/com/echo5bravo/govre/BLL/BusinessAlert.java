package com.echo5bravo.govre.BLL;

import android.content.Context;
import com.echo5bravo.govre.DAL.BusinessBaseAlert;
import com.echo5bravo.govre.INFO.Alert;
import com.echo5bravo.govre.UTILS.ProxyNetworkAlerts;
import java.util.ArrayList;

public class BusinessAlert extends BusinessBaseAlert {
	
	//CONSTRUCTORS
	public BusinessAlert(Context context) {
		super(context);	
	}
	
	//METHODS	
	/**
	* Return all VRE Alerts, Always check for NULL allAlerts because 
	* this data is being queried from SQLite.
	*/
	public static ArrayList<Alert> getAllAlerts(Context context) 
	{
		try{
			@SuppressWarnings("unused")
			ArrayList<Alert> allAlerts; 
			return allAlerts = BusinessBaseAlert.getAllAlerts(context); 
		}
		catch (Exception e)
		{
			e.toString();
		}
		return null;
	}	
	
	/* Method calls ProxyNetworkAlerts to attempt to VRE website
	 * and pagescrape alerts.  ArrayList of alerts are returned
	 * and refreshed in the database on each call.
	 */
	public static void LoadAlertsFromWeb(Context context){
		
		ArrayList<Alert> myAlerts = ProxyNetworkAlerts.fetchAlerts(context);
		if (myAlerts != null)
		{		
			/*Delete all Alerts and refresh with new */
			DeleteAll(context);
			
			/*Insert new alerts */
			Insert(context, myAlerts);
		}
	}
}





