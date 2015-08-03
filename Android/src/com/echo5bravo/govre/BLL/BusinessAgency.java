package com.echo5bravo.govre.BLL;

import android.content.Context;
import com.echo5bravo.govre.DAL.BusinessBaseAgency;
import com.echo5bravo.govre.INFO.Agency;
import java.util.ArrayList;

public class BusinessAgency extends BusinessBaseAgency {
	
	//CONSTRUCTORS
	public BusinessAgency(Context context) {
		super(context);	
	}
	
	//METHODS	
	/**
	* Return all VRE Agencies, Always check for NULL allAgencys because 
	* this data is being queried from SQLite.
	*/
	public static ArrayList<Agency> getAllAgencies(Context context) 
	{
		try{
			@SuppressWarnings("unused")
			ArrayList<Agency> allAgencys; 
			return allAgencys = BusinessBaseAgency.getAllAgencies(context); 
		}
		catch (Exception e)
		{
			e.toString();
		}
		return null;
	}		
}
