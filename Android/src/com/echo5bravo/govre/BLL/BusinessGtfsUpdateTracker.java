package com.echo5bravo.govre.BLL;

import java.util.ArrayList;

import android.content.Context;

import com.echo5bravo.govre.DAL.BusinessBaseGtfsUpdateTracker;
import com.echo5bravo.govre.INFO.GtfsUpdateTracker;

public class BusinessGtfsUpdateTracker extends BusinessBaseGtfsUpdateTracker{
	
		//CONSTRUCTORS
		public BusinessGtfsUpdateTracker(Context context) {
			super(context);	
		}
		
		//METHODS	
		/**
		* Return all VRE GtfsUpdateTracker values, Always check for NULL allGtfsUpdateTracker because 
		* this data is being queried from SQLite.
		*/
		public static ArrayList<GtfsUpdateTracker> getAllGtfsFeedUpdates(Context context) 
		{
			try{
				@SuppressWarnings("unused")
				ArrayList<GtfsUpdateTracker> allGtfsUpdateTracker; 
				return allGtfsUpdateTracker = BusinessBaseGtfsUpdateTracker.getAllGtfsFeedUpdates(context); 
			}
			catch (Exception e)
			{
				e.toString();
			}
			return null;
		}	

}
