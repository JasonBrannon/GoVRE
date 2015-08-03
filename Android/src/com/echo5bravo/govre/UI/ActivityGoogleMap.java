package com.echo5bravo.govre.UI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.echo5bravo.govre.BLL.BusinessRoute;
import com.echo5bravo.govre.BLL.BusinessSchedule;
import com.echo5bravo.govre.BLL.BusinessStation;
import com.echo5bravo.govre.BLL.BusinessUpdate;
import com.echo5bravo.govre.BLL.BusinessVehicle;
import com.echo5bravo.govre.DAL.BusinessBaseCalDates;
import com.echo5bravo.govre.INFO.CalDates;
import com.echo5bravo.govre.INFO.Device;
import com.echo5bravo.govre.INFO.Route;
import com.echo5bravo.govre.INFO.Station;
import com.echo5bravo.govre.INFO.Update;
import com.echo5bravo.govre.INFO.Vehicle;
import com.echo5bravo.govre.INFO.VehicleBubble;
import com.echo5bravo.govre.UTILS.Common;
import com.echo5bravo.govre.UTILS.CustomItemizedOverlay;
import com.echo5bravo.govre.UTILS.CustomOverlayItem;
import com.echo5bravo.govre.UTILS.RouteOverlay;
import com.echo5bravo.govre.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;



public class ActivityGoogleMap extends MapActivity {
	/** Called when the activity is first created. */
	
	private static final String TAG = ActivityGoogleMap.class.getSimpleName();
	
	private GoogleAnalyticsTracker googleTracker;
	
	private static final String PREFS_NAME = "OldPreferences"; 
	private static final String PREFS_TIME_FORMAT_KEY = "OldTimeFormatKEY";
	private static final String PREFS_STATION_MARKER_KEY = "OldStationMarkerKEY";
	private static final String PREFS_SATELLITE_KEY = "OldSatelliteKEY";
	private static final String PREFS_TRAFFIC_KEY = "OldTrafficKEY";
	private String mOldTimeFormatValue;
	private String mOldStationMarkerValue;
	private boolean mOldSatelliteValue;
	private boolean mOldTrafficValue;

	MapView mapView;
	private Context context; 	
	
	private ImageView imgTwitter, imgAlerts, imgSched; 
	private ImageView spinner;
	private TextView txtRefresh;
	private TextView txtArrivals;
	private AnimationDrawable spinnerAnim;
	private boolean spinnerOn;
	private ImageView imgNoSignal;
	private boolean noSignalOn;
	private boolean blnGTFSDataLoaded;
	private boolean blnLocalDataLoaded;
	//private int iRefreshCount;
	
	List<Overlay> mapOverlays;
	ArrayList<Route> myFBGRoutes;
	ArrayList<Route> myMSSRoutes;
	ArrayList<Station> myStations;
	ArrayList<Vehicle> myVehicles;
	ArrayList<Update> myUpdates;
	ArrayList<CalDates> myCalDates;
	//ArrayList<VehicleBubble> myVehicleBubbles;   STILL TESTING
	@SuppressWarnings("rawtypes")
	ArrayList<CustomItemizedOverlay> overlayStations;
	@SuppressWarnings("rawtypes")
	ArrayList<CustomItemizedOverlay> overlayVehicles;
	
	private MyLocationOverlay myLocationOverlay = null;
	
	String TodaysScheduleType;
	
	private ExecutorService TaskExecutorMap;
	private ScheduledExecutorService scheduleTaskExecutorVehicles;
	private ScheduledExecutorService scheduleTaskExecutorUiUpdates; 
	
	
	double stationLat;
	double stationLong;
	double vehicleLat;
	double vehicleLong;
	
	private String stationIcon; 
	private String stationIconString;
	private String stationAddress;
	private String stationIconName;
	
	private String vehicleIcon; 
	private String vehicleIconString;
	private String vehicleInfo;
	private String vehicleIconName;
	
	private String stationMarker;
	
	GeoPoint srcGeoPoint;
	GeoPoint destGeoPoint;
	
	int LoopCounter = 0;
	
	String lblMsg = "";
	
	
	
	Drawable drawable2;
	CustomItemizedOverlay<CustomOverlayItem> itemizedOverlay;   
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_BlueMoon);
		this.setContentView(R.layout.googlemap_layout);
		
		//myVehicleBubbles = new ArrayList<VehicleBubble>();  STILL TESTING
		
		//Start Google Analytics Tracker
		//-------------------------------------------------------------------------------------------------
		googleTracker = GoogleAnalyticsTracker.getInstance();
		googleTracker.setDebug(Boolean.parseBoolean(this.getString(R.bool.ga_debug)));
		googleTracker.setDryRun(Boolean.parseBoolean(this.getString(R.bool.ga_dryrun)));
		googleTracker.startNewSession(this.getString(R.string.ga_api_key), 60, this);  

		int CV_SLOT_1 = 1;	//Slot 1 Tracks Device Orientation
		//int CV_SLOT_2 = 2;	//Slot 2 Unassigned
		//int CV_SLOT_3 = 3;	//Slot 3 Unassigned
		//int CV_SLOT_4 = 4;	//Slot 4 Unassigned
		//int CV_SLOT_5 = 5;	//Slot 5 Unassigned

		//Track Device's Current Orientation
		googleTracker.setCustomVar(CV_SLOT_1,	 //SLOT (Can only track up to 5)
		   "Device Orientation", //NAME
		   Device.getDeviceOrientation(this.getApplicationContext()), 	//VALUE
		   1);	//SCOPE

		/*-------------------------------------------------------------------------------------------------
		NOTE: Add to Activity Handlers:
		 onResume():  googleTracker.trackPageView("/" + TAG);
		 onDestroy(): googleTracker.stopSession();
		-------------------------------------------------------------------------------------------------*/
		
		context = this;

		mapView = (MapView) findViewById(R.id.mapview);	
		
		spinner = (ImageView) findViewById(R.id.splashSpinner);
		txtRefresh = (TextView) findViewById(R.id.txtrefresh);
		txtArrivals = (TextView) findViewById(R.id.txtarrivals);
		spinnerAnim = (AnimationDrawable) spinner.getBackground();
		imgNoSignal  = (ImageView) findViewById(R.id.imgNoSignal);
		
		
		double src_lat = 0.0;
		double src_long = 0.0;
		
		//Orient the Map Center Point based on the user's device orientation
		if (Device.getDeviceOrientation(this.getApplicationContext()).equals("portrait")){
			//Location: Rippon
			src_lat = 38.613158999999996;
			src_long = -77.254272;
		}
		else
		{
			//Location: Lorton
			src_lat = 38.71252799999999;
			src_long = -77.217865;
		}
		
		//Location: Union Station
		double dest_lat = 38.897346;
		double dest_long = -77.007337;
		
		//Set GeoPoints as defaults until the TrainLines are drawn
		srcGeoPoint = new GeoPoint((int) (src_lat * 1E6), (int) (src_long * 1E6));
		destGeoPoint = new GeoPoint((int) (dest_lat * 1E6), (int) (dest_long * 1E6));

		//DrawMap(srcGeoPoint, destGeoPoint, mapView);
		
		mapView.setBuiltInZoomControls(true);
		
		mapView.getController().animateTo(srcGeoPoint);  //Focus to Quantico, both TrainLines are visible, looks cleaner.
		mapView.getController().setZoom(11);
		
		addListenerOnTwitterImage(); 
        addListenerOnAlert();
        addListenerOnSched();
        
        //Location and Compass Managers
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        //**mapView.getOverlays().add(myLocationOverlay);
        
    
	}
	
	@Override
    protected void onStop(){
       super.onStop();
       
       SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
       SharedPreferences.Editor editor = settings.edit();
       editor.putString(PREFS_TIME_FORMAT_KEY, mOldTimeFormatValue); 
       editor.putString(PREFS_STATION_MARKER_KEY, mOldStationMarkerValue);       
       editor.putBoolean(PREFS_SATELLITE_KEY, mOldSatelliteValue); 
       editor.putBoolean(PREFS_TRAFFIC_KEY, mOldTrafficValue); 
       
       // Commit the edits!
       editor.commit();
       
       //Moved to OnPause();
       TaskExecutorMap.shutdown();
	   scheduleTaskExecutorVehicles.shutdown();
	   scheduleTaskExecutorUiUpdates.shutdown();
	   
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		
		//Stop Progress Spinner
	    spinnerAnim.stop();
		spinner.setVisibility(View.INVISIBLE);
		
		//Disable location manager 
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		 TaskExecutorMap = Executors.newSingleThreadExecutor();
			TaskExecutorMap.execute(new Runnable() {
			    public void run() {
			    	
			    	//Log.d(TAG, "LOCAL ARRAYS LOADING...."); 
		    		
			    	//Load Singleton ArrayLists
			    	LoadCalDates();
				    TodaysScheduleType = Common.TodaysScheduleType(context, myCalDates, true);
		    		LoadFBGRoute();
			    	LoadMSSRoute();
			    	LoadStationStop();			   	
				   	
				   	//Log.d(TAG, "LOCAL ARRAYS LOADED."); /
				   	
				   	// Update the UI Thread
			        runOnUiThread(new Runnable() {
			        public void run() { 
			        	
			        	//Ensure local values are loaded before committing to blnLocalDataLoaded=true
					   	if (myFBGRoutes != null && myMSSRoutes != null && myStations != null){
					   		//Log.d(TAG, "DRAWING RAIL LINES AND STATIONS."); 
					   		
					   		//Pull user's preferred Map Settings
				    		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				    		String prefLine = getPrefs.getString("listTrainLine", "FBG,ALL");
				    		stationMarker = getPrefs.getString("listStationMarker", "abbv");
				    		
				    		//Draw user lines by preference.  This will insure the preferred line
				    		//overlays correctly for when the lines Merger in Alexandria
				    		if (prefLine.equals("MSS,ALL")){			    			
				    			DrawFBGRouteOverlay(srcGeoPoint, destGeoPoint);
							    DrawMSSRouteOverlay(srcGeoPoint, destGeoPoint);
				    		}
				    		else{			    			
							    DrawMSSRouteOverlay(srcGeoPoint, destGeoPoint);
							    DrawFBGRouteOverlay(srcGeoPoint, destGeoPoint);
				    		}
				    					    				    		
					   		if (mOldStationMarkerValue.equals(stationMarker) || mOldStationMarkerValue.length() == 0){
					   			mOldStationMarkerValue = stationMarker;
					   			DrawStationStopOverlay(srcGeoPoint, destGeoPoint);
					   		}
					   		
						   	//Log.d(TAG, "DRAWING RAIL LINES AND STATIONS... COMPLETE"); 
						   	blnLocalDataLoaded = true;	
						   	
						   	//Log.d(TAG, "CHECKING MAPVIEW SETTINGS FOR CHANGES."); 
				    		  
				    		//Pull user's preferred Map Settings			    		
				    		boolean satelliteOn = getPrefs.getBoolean("chkSatellite", false);
				    		boolean trafficOn = getPrefs.getBoolean("chkTraffic", false);
				    		
				    		mapView.setSatellite(satelliteOn);
				    		mapView.setTraffic(trafficOn);
				    		
				    		//Log.d(TAG, "CHECK COMPLETE"); 
					   	}
					   	else{
					   		blnLocalDataLoaded = false;	
					   	}		        		    	
			        }
			        });	    	
			    }
			});	
			
			
			scheduleTaskExecutorVehicles = Executors.newSingleThreadScheduledExecutor();

		    // This schedule a task to run every X Seconds:
			scheduleTaskExecutorVehicles.scheduleAtFixedRate(new Runnable() {	    	
		    public void run() {
		    	
		    	try{
		    		
		    		if (Device.isDeviceOnline(context.getApplicationContext()))
			    		noSignalOn = false;
		    		else
		    			noSignalOn = true;
		    		
		    		spinnerOn = true;   	  
			    	
				    		
				   	//Get GTFS data on separate thread (Requires Network)
			    	if (noSignalOn==false && blnGTFSDataLoaded == false && blnLocalDataLoaded == true){
			    		
			    		//Log.d(TAG, "CALLING VRE GTFS INTERFACE..."); 
						doRefreshGTFSFeeds();	
						//Log.d(TAG, "CALL COMPELTE."); 
						    
						//Log.d(TAG, "GTFS ARRAYS LOADING...."); 
						LoadVehicleStop();	
			    		LoadUpdates();
			    		//Log.d(TAG, "GTFS ARRAYS LOADED."); 
			    		
			    		//Ensure local values are loaded before committing to blnLocalDataLoaded=true
					   	if (myVehicles != null && myUpdates != null)
					   		blnGTFSDataLoaded = true;	
					   	else
					   		blnGTFSDataLoaded = false;	
			    	}
					
					spinnerOn = false;
					
		    	}
		    	catch(Exception ex)
		    	{
		    		spinnerOn = false;
		    	}
		    	
		    	if (LoopCounter == 0){
		    		LoopCounter = 59;
		    		blnGTFSDataLoaded = false;
		    	}
		    	else
		    	{
		    		LoopCounter--;
		    		    		
		    	}

		        // Update the UI Thread
		        runOnUiThread(new Runnable() {
		          public void run() {  
		        	  
			          try{			        		  
			        	  /***********************************************************************************
			        	   * Since there are 1,000s of GeoPoints for the Rail Lines and the Stations
			        	   * are static locations, to redraw these every time a vehicle update is posted
			        	   * causes the map to crash.
			        	   * 
			        	   * The logic below keeps the FBG, MSS Lines and the Stations on the map
			        	   * and only re-draws the Vehicles. 
			        	   * 
			        	   * -Check the mapView for our itemizedOverlay, both Station and Train use them
			        	   * but the overlayVehicles ArrayList collection will contain the old objects we 
			        	   * want to clear before adding the new Vehicle locations to the map.
			        	   **********************************************************************************/
			        	  if (!spinnerOn){
						      if (mapView.getOverlays().contains(itemizedOverlay)){ 
							    	  
							   	  //If user selected a new Station Marker Icon, destroy the old stations and add new
							   	  if (!(mOldStationMarkerValue.equals(stationMarker))){
							    		  
						    		  for (@SuppressWarnings("rawtypes") CustomItemizedOverlay each: overlayStations){
							    		  //mapOverlays.remove(each);
								    	  mapView.getOverlays().remove(each);
							    	  }						    		  
						    		  
						    		  DrawStationStopOverlay(srcGeoPoint, destGeoPoint);
						    		  mOldStationMarkerValue = stationMarker;
						    		  mapView.postInvalidate();
						    		 
						    	  }
							    	  
						    	 //Always Destroy and re-draw vehicles
							     for (CustomItemizedOverlay each: overlayVehicles){
							    	  //mapOverlays.remove(each);
								      mapView.getOverlays().remove(each);
							      }				    			    	
							      DrawVehicleOverlay(srcGeoPoint, destGeoPoint);
							      DrawUserLocation();  		  
							  	  mapView.postInvalidate();
							 }
							 else
							 {					   	 
							      DrawVehicleOverlay(srcGeoPoint, destGeoPoint);
							      DrawUserLocation();  	
							      mapView.postInvalidate();	  				
							  }
			        	  }
				        	  
				       	  spinnerOn = false;
						        			  
			          }
			          catch(Exception e)
			          {
			        	  //Log.d(TAG, "runOnUiThread: " + e.toString());
			          }	
		        	
		        	  
		        	  if (LoopCounter == 0)
		        		  txtRefresh.setText("Loading...");
		        	  else
		        		  txtRefresh.setText(" Refreshing: " + Integer.toString(LoopCounter) + " ");
		        	  
		        	  txtArrivals.setText(lblMsg);
		          }
		        });
		      }	  
		   }, 0, 1, TimeUnit.SECONDS);
			
			
			
			scheduleTaskExecutorUiUpdates = Executors.newScheduledThreadPool(4);

		    // This schedule a task to run every x minutes:
			scheduleTaskExecutorUiUpdates.scheduleAtFixedRate(new Runnable() {	    	
		      public void run() {
		    	
		    	
		        // Update the UI Thread
		        runOnUiThread(new Runnable() {
		          public void run() {  
		       	        	  
		        	  if (spinnerOn){
		        		  spinnerAnim.start();
		        		  spinner.setVisibility(View.VISIBLE); 
		        	  }	        		  
		        	  else
		        	  {
		        		  spinnerAnim.stop();
		        		  spinner.setVisibility(View.INVISIBLE);
		        	  }	
		        	  
		        	  if (noSignalOn){
		        		  imgNoSignal.setVisibility(View.VISIBLE);
		        	  }	        		  
		        	  else
		        	  {
		        		  imgNoSignal.setVisibility(View.INVISIBLE);  
		        	  }
		          }
		        });
		      }
			}, 0, 1, TimeUnit.SECONDS);	
		    //}, 0, 500, TimeUnit.MILLISECONDS);
		
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		//Track current Activity
		googleTracker.trackPageView("/" + TAG);
		
		/* Variables to check if user preferences have changed */
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);	
	    mOldTimeFormatValue = settings.getString(PREFS_TIME_FORMAT_KEY, "");
	    mOldStationMarkerValue = settings.getString(PREFS_STATION_MARKER_KEY, "");	    
	    mOldSatelliteValue = settings.getBoolean(PREFS_SATELLITE_KEY, false);	
	    mOldTrafficValue = settings.getBoolean(PREFS_TRAFFIC_KEY, false);
	    
	    //Manage Location Manager
	    if (myLocationOverlay != null){
	    	myLocationOverlay.enableMyLocation();
	    	myLocationOverlay.enableCompass();	    	
	    }
	}
	
	private void doRefreshGTFSFeeds() {
		
		/* Verify the Device is Connected before invoking a method requiring INTERNET*/
    	//if (Device.isDeviceOnline(context.getApplicationContext())){	    	  
    		
    		//Pull Vehicles from VRE GTFS and Load them to SQL
    		BusinessVehicle.LoadVehiclesFromGTFS(this);
    		
    		//Pull Trip Updates from VRE GTFS and Load them to SQL
    		BusinessUpdate.LoadUpdatesFromGTFS(this);    		
    	//}		
	}
	
	private void LoadFBGRoute(){
		
		//Get Route Based on route_id
		myFBGRoutes = BusinessRoute.getAllRoutes(context, 2); //FBG (RED)
	}
	
	private void LoadMSSRoute(){
		
	    //Get Route Based on route_id
		myMSSRoutes = BusinessRoute.getAllRoutes(context, 4); //MSS (BLUE)		
	}
	
	private void LoadStationStop(){
		
		//Get Stations
		myStations = BusinessStation.getAllStations(context);
	}
	
	private void LoadVehicleStop(){
		
		//Get Vehicles
		myVehicles = BusinessVehicle.getAllVehicles(context);
	}
	
	private void LoadUpdates(){
		
		//Current Station Collection
		myUpdates = BusinessUpdate.getAllUpdates(context);
	}
	
	private void LoadCalDates(){
		
		//Current CalDates Collection
		myCalDates = BusinessBaseCalDates.getAllCalDates(this);		
	}
	
	private void DrawFBGRouteOverlay(GeoPoint src, GeoPoint dest){
		
		DrawRoute(src, dest, myFBGRoutes, Color.RED); 
	}
	
	private void DrawMSSRouteOverlay(GeoPoint src, GeoPoint dest){
		
		DrawRoute(src, dest, myMSSRoutes, Color.BLUE);
	}
	
	//Draws User Location, must draw LocationOverlay last so it appears on-top of
	//All other layers: Rail Line, Stations, Vehicles.  Note: LocationOverlay is created
	//OnCreate and is Enabled/Disabled from OnResume and OnPause
	private void DrawUserLocation(){
		
		//Remove Old
		if (myLocationOverlay != null)
			mapView.getOverlays().remove(myLocationOverlay);
		
		//Add New
		mapView.getOverlays().add(myLocationOverlay);
		
	}
	
	private void DrawStationStopOverlay(GeoPoint src, GeoPoint dest){
		
		overlayStations = new ArrayList<CustomItemizedOverlay>();		
		
		try{
			for (Station each : myStations) {			
				
				stationLat = Double.parseDouble(each.STATION_LATITUDE());
				stationLong = Double.parseDouble(each.STATION_LONGITUDE());
				
				//Train Station Icon
				stationIconString = "drawable/station_icon_" + each.STATION_ID().toLowerCase();
				stationIcon = String.valueOf(getResources().getIdentifier(stationIconString, null, getPackageName()));
				stationAddress = each.ZONE() + "\n";
				stationAddress += each.ADDRESS() + "\n";
				stationAddress += each.CITY() + ", " + each.STATE() + " " + each.ZIP() + "\n";
				
				mapOverlays = mapView.getOverlays();  
								
				//Train Station Map Icon
				if (stationMarker.equals("abbv"))
					stationIconName = "drawable/station_" + each.STATION_ID().toLowerCase();
				else
					stationIconName = "drawable/stop_marker";
				
			    int imageResource = getResources().getIdentifier(stationIconName, null, getPackageName());
			    Drawable drawable = getResources().getDrawable(imageResource);
			    
				itemizedOverlay = new CustomItemizedOverlay<CustomOverlayItem>(drawable, mapView);
			    
				GeoPoint point = new LatLonPoint(stationLat, stationLong);	
				CustomOverlayItem overlayItem = new CustomOverlayItem(point, each.STATION_STOP_NAME(), stationAddress, stationIcon);
				itemizedOverlay.addOverlay(overlayItem);

				overlayStations.add(itemizedOverlay);	
			}
			
			if (!(overlayStations == null))
				mapOverlays.addAll(overlayStations);
			
		}
		catch(Exception e)
		{
			//Log.d(TAG, "DrawStationStopOverlay ERROR: " + e.toString());	
		}			
	}
	
	/*
	 * DrawVehicleOverlay
	 * 
	 * Requires Special Logic:  Since it is too expensive to re-draw the mapView everytime a Vehicle
	 * change occurs, load the vehicles into a collection and clear their old location and draw the
	 * new location on the background thread.
	 */
	@SuppressWarnings("unused")
	private void DrawVehicleOverlay(GeoPoint src, GeoPoint dest){
		
		overlayVehicles = new ArrayList<CustomItemizedOverlay>();		
		
		boolean VehicleAtStation = false;
		
		String StationLat = "";
		String StationLong = "";
		String VehicLat = "";
		String VehicLong = "";
		String iconColor = ""; //Default is Green  
		
		lblMsg = "";	//Clear 	
		
		VehicleBubble myVehicleBubble = null;  //IDEA!  Convert to Collection refresh collection only when Spinner recycles
		
		if (!(myVehicles == null)){		
				
		try{
			
			for (Vehicle eachVehicle : myVehicles) { 
				
				vehicleLat = Double.parseDouble(eachVehicle.VEHICLE_LATITUDE());  
				vehicleLong = Double.parseDouble(eachVehicle.VEHCILE_LONGITUDE());  
				
				/* Pull user's preferred default train line */  
				SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				String timeformat = getPrefs.getString("listTimeFormat", "12");				
							
				if (myStations != null && myUpdates != null && TodaysScheduleType != null)
				    myVehicleBubble = BusinessSchedule.fetchVehicleBubble(this, eachVehicle, myUpdates, myStations, TodaysScheduleType, timeformat);  
				
				
				if (myVehicleBubble != null){ 
				
					if (myVehicleBubble.AT_STATION())
					{
						
						if (lblMsg.length() == 0)
							lblMsg = (" " + myVehicleBubble.VEHICLE_ID() + "@" + myVehicleBubble.CURRENT_STOP() + " ");
						else
							lblMsg = (lblMsg + "\n " + myVehicleBubble.VEHICLE_ID() + "@" + myVehicleBubble.CURRENT_STOP() + " ");
					}
					
					//Train Station Icon				
					vehicleIconString = "drawable/train";
					vehicleIcon = String.valueOf(getResources().getIdentifier(vehicleIconString, null, getPackageName()));			
					
					//vehicleInfo = myVehicleBubble.VEHICLE_LABLE() + " \n"; 
					vehicleInfo = myVehicleBubble.STATUS() + " \n"; 
					vehicleInfo += myVehicleBubble.NEXT_STOP_AND_TIME() + " \n"; 
					vehicleInfo += "To: " + myVehicleBubble.HEADING() + " \n"; 
					vehicleInfo += myVehicleBubble.STOPS_REMAINING();
					
					
					//Determine which color icon to display
					iconColor = "green";  //Default: green
					
					if (myVehicleBubble.DELAYED())  //Delayed turns orange
						iconColor = "orange";
					
					if (myVehicleBubble.LOST_COMM()) 	//Lost Comms overrides Default and Delay turns gray
						iconColor = "gray";
					
					mapOverlays = mapView.getOverlays(); 
					
					int imageResource;				
													
					//Train Station Map Icon
					if (myVehicleBubble.AT_STATION()){
						if (LoopCounter%2==0)
							vehicleIconName = "drawable/map_train_" + iconColor + "_" + eachVehicle.VEHICLE_ID().toLowerCase();
						else
							vehicleIconName = "drawable/map_train_" + iconColor + "_arrive";					
					}
					else
					{
						vehicleIconName = "drawable/map_train_" + iconColor + "_" + eachVehicle.VEHICLE_ID().toLowerCase();
					}
															
					try{
						
						imageResource = getResources().getIdentifier(vehicleIconName, null, getPackageName());
					}
					catch(Exception e)
					{
						vehicleIconName = "drawable/map_train_green";
						imageResource = getResources().getIdentifier(vehicleIconName, null, getPackageName());
					}					
					
				    Drawable drawable = getResources().getDrawable(imageResource);
				    
					itemizedOverlay = new CustomItemizedOverlay<CustomOverlayItem>(drawable, mapView);
				    
					GeoPoint point = new LatLonPoint(vehicleLat, vehicleLong);	
					CustomOverlayItem overlayItem = new CustomOverlayItem(point, myVehicleBubble.VEHICLE_LABLE(), vehicleInfo, vehicleIcon);
					itemizedOverlay.addOverlay(overlayItem);
					
					overlayVehicles.add(itemizedOverlay);	
				}
			} 
			if (!(overlayVehicles == null))
				mapOverlays.addAll(overlayVehicles);			
		}
		catch(Exception e)
		{
			//Log.d(TAG, "DrawVehicleOverlay: " + e.toString());	
		}	
		
		}
	}
	
	private static final class LatLonPoint extends GeoPoint {  
	    public LatLonPoint(double latitude, double longitude) {
	        super((int) (latitude * 1E6), (int) (longitude * 1E6));
	    }
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	private void DrawRoute(GeoPoint src, GeoPoint dest, ArrayList<Route> route, int DefaultColor){
		
		Integer counter = 0;
		GeoPoint startGP = null;
		GeoPoint gp1;
		GeoPoint gp2 = null;
		
		for (Route each : route) {			
			if (counter == 0){			
				startGP = new GeoPoint((int) (Double.parseDouble(each.ROUTE_LATITUDE()) * 1E6), (int) (Double.parseDouble(each.ROUTE_LONGITUDE()) * 1E6));
				mapView.getOverlays().add(new RouteOverlay(startGP, startGP, DefaultColor));
				gp2 = startGP;				
				counter++;
			}
			else
			{		
				gp1 = gp2;
				gp2 = new GeoPoint((int) (Double.parseDouble(each.ROUTE_LATITUDE()) * 1E6),	(int) (Double.parseDouble(each.ROUTE_LONGITUDE()) * 1E6));
				mapView.getOverlays().add(new RouteOverlay(gp1, gp2, 3, DefaultColor));
			}					
		}	
		mapView.getOverlays().add(new RouteOverlay(dest, dest, DefaultColor)); 	
	}
	
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	     MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.mapmenu, menu);
	     return true;
	 }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if (item.getItemId() == R.id.about) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "about",			//label
									 0);				//value
			Intent a = new Intent(ActivityGoogleMap.this, ActivityAbout.class);
			a.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(a);
		} else if (item.getItemId() == R.id.options) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "options",			//label
									 0);				//value
			Intent p = new Intent(ActivityGoogleMap.this, ActivityPreferences.class);
			p.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(p);
		} else if (item.getItemId() == R.id.oldmap) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "oldmap",		//label
									 0);				//value
			Intent o = new Intent(ActivityGoogleMap.this, ActivityOldMap.class);
			o.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(o);
		} else if (item.getItemId() == R.id.alerts) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "alerts",			//label
									 0);				//value
			Intent x = new Intent(ActivityGoogleMap.this, ActivityAlerts.class);
			x.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(x);
		} else if (item.getItemId() == R.id.schedules) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "schedules",		//label
									 0);				//value
			Intent e = new Intent(ActivityGoogleMap.this, ActivityStationSchedule.class);
			e.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(e);
		}
	    return true;
	}	
	
	private void  addListenerOnAlert() {
		 
		imgAlerts = (ImageView) findViewById(R.id.imgAlerts); 

		imgAlerts.setOnClickListener(new View.OnClickListener() {
 
			public void onClick(View arg0) {
				//Track GoogleAnalytics Event
				googleTracker.trackEvent("ui_interaction",		//category
					        			 "from_icon",			//action
					        			 "alerts",				//label
					        			 0);					//value
				
				//Do Something
				Intent myIntent = new Intent(ActivityGoogleMap.this, ActivityAlerts.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);     			
			} 
		}); 
	}
	
	private void  addListenerOnSched() {
		 
		imgSched = (ImageView) findViewById(R.id.imgSched); 

		imgSched.setOnClickListener(new View.OnClickListener() {
 
			public void onClick(View arg0) {
				//Track GoogleAnalytics Event
				googleTracker.trackEvent("ui_interaction",		//category
					        			 "from_icon",			//action
					        			 "schedule",			//label
					        			 0);					//value
				
				//Do Something
				Intent myIntent = new Intent(ActivityGoogleMap.this, ActivityStationSchedule.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);     			
			} 
		}); 
	}
	
	
	private void addListenerOnTwitterImage() {
		 
		imgTwitter = (ImageView) findViewById(R.id.imgTwitter); 

		imgTwitter.setOnClickListener(new View.OnClickListener() {
 
			public void onClick(View arg0) {
				//Track GoogleAnalytics Event
				googleTracker.trackEvent("ui_interaction",		//category
					        			 "from_icon",			//action
					        			 "twitter",				//label
					        			 0);					//value
				
				//Do Something				
				Intent myIntent = new Intent(ActivityGoogleMap.this, ActivityTwitterFeed.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent); 
			} 
		}); 
	}
	
	@Override
	protected void onDestroy() {
		 super.onDestroy();
		    
		// Stop the tracker when it is no longer needed.
		googleTracker.stopSession();
	}
}