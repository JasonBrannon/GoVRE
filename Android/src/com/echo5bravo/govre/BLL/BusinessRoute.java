package com.echo5bravo.govre.BLL;

import android.content.Context;
import com.echo5bravo.govre.DAL.BusinessBaseRoute;
import com.echo5bravo.govre.INFO.Route;
import java.util.ArrayList;

public class BusinessRoute extends BusinessBaseRoute {
	
	//CONSTRUCTORS
	public BusinessRoute(Context context) {
		super(context);	
	}
	
	//METHODS	
	/**
	* Return all VRE Routes, Always check for NULL allRoutes because 
	* this data is being queried from SQLite.
	*/
	public static ArrayList<Route> getAllRoutes(Context context, Integer RouteId) 
	{
		try{
			@SuppressWarnings("unused")
			ArrayList<Route> allRoutes; 
			return allRoutes = BusinessBaseRoute.getAllRoutes(context, RouteId); 
		}
		catch (Exception e)
		{
			e.toString();
		}
		return null;
	}		
	
}