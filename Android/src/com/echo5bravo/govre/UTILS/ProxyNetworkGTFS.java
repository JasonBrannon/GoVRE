package com.echo5bravo.govre.UTILS;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.echo5bravo.gtfs.GtfsRealtime.FeedMessage;
import com.echo5bravo.govre.R;
import com.echo5bravo.govre.INFO.CalDates;
import com.echo5bravo.govre.INFO.Tweet;


/*Return GTFS FeedMessage based on URL Request*/
public class ProxyNetworkGTFS extends Application {
	
	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = ProxyNetworkGTFS.class.getSimpleName();
	
	/*Pulls Binary Data From VRE GTFS Interface*/
	public FeedMessage fetchVehiclePositionFeed(Context context){
		
		String url = context.getResources().getString(R.string.GTFSVehiclePositionFeed);
		
		HttpURLConnection conn = null;		
		
		try {
		
			URL urlUse = new URL(url);	        
	        conn = (HttpURLConnection) urlUse.openConnection(); 
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Content-length", "0");
	        conn.setUseCaches(false);
	        conn.setAllowUserInteraction(false);
	        conn.setConnectTimeout(8000);	//Set the connection timeout to 5 seconds
	        conn.setReadTimeout(10000);		//Set the read timeout to 6 seconds
	        conn.connect();
	       	        
	        int status = conn.getResponseCode();
	        
	        // use the stream...
	        switch (status) {
	            case 200:
	            case 201:
				   	return FeedMessage.parseFrom(conn.getInputStream());
	            default:
	            	return null;
	        }
	        
		}catch (MalformedURLException e) {
			//e.printStackTrace();
			return null;
		} catch (IOException e) {			
			//e.printStackTrace();
			return null;
		}
	}
	
	/*Pulls Binary Data From VRE GTFS Interface*/
	public FeedMessage fetchTripUpdateFeed(Context context){
		
		String url = context.getResources().getString(R.string.GTFSTripUpdateFeed);		
		
		HttpURLConnection conn = null;
		
		try {			
			URL urlUse = new URL(url);	        
	        conn = (HttpURLConnection) urlUse.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Content-length", "0");
	        conn.setUseCaches(false);
	        conn.setAllowUserInteraction(false);
	        conn.setConnectTimeout(8000);	//Set the connection timeout to 8 seconds
	        conn.setReadTimeout(10000);		//Set the read timeout to 10 seconds
	        conn.connect();	        
	        
	        int status = conn.getResponseCode();
	        
	        // use the stream...
	        switch (status) {
	            case 200:
	            case 201:		            	
				   	return FeedMessage.parseFrom(conn.getInputStream());
	            default:
	            	return null;
	        }
	        
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			return null;
		} catch (IOException e) {			
			//e.printStackTrace();
			return null;
		}	
	}
	
	
	/*Pulls Binary JSON From Echo5Bravo GTFS API*/
	public ArrayList<CalDates> fetchCalendarDates(Context context){
		
		String url = context.getResources().getString(R.string.GTFSCalendarDates);
		
		ArrayList<CalDates> calDatesList = new ArrayList<CalDates>();
		CalDates currentCalDates = new CalDates();
		
		// JSON Node names
		String TAG_SERVICE_ID = "service_id";
		String TAG_DATE = "date";
		String TAG_EXCEPTION_TYPE = "exception_type";
		
		// contacts JSONArray
		JSONArray contacts = null;
		
		// Creating JSON Parser instance
		JSONParser jParser = new JSONParser();

		// getting JSON string from URL
		JSONArray json = jParser.getJSONFromUrl(url);
		
		if (json != null){
		
			try {
			    // Getting Array of Contacts
			    //contacts = json.getJSONArray("");
	
			    // looping through All Contacts
			    for(int i = 0; i < json.length(); i++){
			        JSONObject c = json.getJSONObject(i);
	
			        // Storing each json item in variable
			        String service_id = c.getString(TAG_SERVICE_ID);
			        String date = c.getString(TAG_DATE);
			        Integer exception_type = c.getInt(TAG_EXCEPTION_TYPE);
			        
			        //Create new instance of object
					currentCalDates = new CalDates();
					currentCalDates.SERVICE_ID(service_id);	
					currentCalDates.CALDATES_DATE(date);	
					currentCalDates.CALDATES_EXCEPTION_TYPE(exception_type);
					
					//Add newly populated object to ArrayList
					calDatesList.add(currentCalDates);
			       
	
			    }
			} catch (JSONException e) {
			    e.printStackTrace();
			}
			
			return calDatesList;
			
		}
		else
		{
			return null;
		}
		
		
		
	}
	
	private class JSONParser {

	    InputStream is = null;
	    JSONArray jObj = null;
	    String json = "";

	    // constructor
	    public JSONParser() {}
	    
	    HttpURLConnection conn = null;

	    public JSONArray getJSONFromUrl(String url) {

	        // Making HTTP request
	        try {
		        	URL urlUse = new URL(url);	        
			        conn = (HttpURLConnection) urlUse.openConnection();
			        conn.setRequestMethod("GET");
			        conn.setRequestProperty("Content-length", "0");
			        conn.setRequestProperty("Content-Type", "application/json");
			        conn.setRequestProperty("Cache-Control", "no-cache");
			        conn.setUseCaches(false);
			        conn.setAllowUserInteraction(false);
			        conn.setConnectTimeout(8000);	//Set the connection timeout to 8 seconds
			        conn.setReadTimeout(10000);		//Set the read timeout to 10 seconds
			        conn.connect();	        
			        
			        int status = conn.getResponseCode();
	
			        // use the stream...
			        switch (status) {
			            case 200:
			            case 201:
			            	
			            	BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream())); // Buffered
			            	
			                StringBuilder sb = new StringBuilder();
			                String line = null;
			                while ((line = reader.readLine()) != null) {
			                    sb.append(line + "\n");
			                }
			               
			                json = sb.toString();
							
							jObj = new JSONArray(json);
							
							String d = json;
							d = "";
							
							return jObj;
						   
			            default:
			            	return null;	
			        }
			       
	        } catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {			
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			
	    }
	}
}
