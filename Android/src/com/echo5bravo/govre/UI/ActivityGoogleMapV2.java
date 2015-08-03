package com.echo5bravo.govre.UI;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.echo5bravo.govre.R;
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
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;



public class ActivityGoogleMapV2 extends FragmentActivity implements OnMarkerClickListener,ConnectionCallbacks,
OnConnectionFailedListener, LocationListener  {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
	private static final String TAG = ActivityGoogleMapV2.class.getSimpleName();
	
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
	
    private GoogleMap mMap;
    private LocationClient mLocationClient;
    
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
    
    private Marker mStation;
    private Marker mVehicle;
    private String stationMarker;
    private String vehicleMarker;
    
    String TodaysScheduleType;
	
	private ExecutorService TaskExecutorMap;
	private ScheduledExecutorService scheduleTaskExecutorVehicles;
	private ScheduledExecutorService scheduleTaskExecutorUiUpdates; 
    
    ArrayList<Route> myFBGRoutes;
	ArrayList<Route> myMSSRoutes;
	ArrayList<Station> myStations;
	ArrayList<Vehicle> myVehicles;
	ArrayList<Update> myUpdates;
	ArrayList<CalDates> myCalDates;
	ArrayList<Marker> mapStations;   //Stores Station Markers objects after they are committed to the Map
	ArrayList<Marker> mapVehicles;	//Stores Vehicle Markers objects after they are committed to the Map
	
	LatLng srcLatLng; 
	LatLng destLatLng; 
	
	int LoopCounter = 0;
	
	String lblMsg = "";
	
	// These settings are the same as the settings for the map. They will in fact give you updates
    // at the maximal rates currently possible.
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms = 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	/*  LIFECYCLE EVENTS -----------------------  onCreate()  -------------------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BlueMoon);
        setContentView(R.layout.googlemap_v2_layout);
        
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
               
		spinner = (ImageView) findViewById(R.id.splashSpinner);
		txtRefresh = (TextView) findViewById(R.id.txtrefresh);
		txtArrivals = (TextView) findViewById(R.id.txtarrivals);
		spinnerAnim = (AnimationDrawable) spinner.getBackground();
		imgNoSignal  = (ImageView) findViewById(R.id.imgNoSignal);
		
		addListenerOnTwitterImage(); 
        addListenerOnAlert();
        addListenerOnSched();
        
        setUpMapIfNeeded();        
        
    }    
    
    /*  LIFECYCLE EVENTS -----------------------  onResume()  -------------------------------------------------*/
    
    @Override
    protected void onResume() {
        super.onResume();
        
	    //Track current Activity
	    googleTracker.trackPageView("/" + TAG);  
	      		
	    /* Variables to check if user preferences have changed */
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);	
	    mOldTimeFormatValue = settings.getString(PREFS_TIME_FORMAT_KEY, "");
	    mOldStationMarkerValue = settings.getString(PREFS_STATION_MARKER_KEY, "");	    
	    mOldSatelliteValue = settings.getBoolean(PREFS_SATELLITE_KEY, false);	
	    mOldTrafficValue = settings.getBoolean(PREFS_TRAFFIC_KEY, false);
      	    
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        
        //Location Management
	    mLocationClient.connect();
    }
    
    /*  LIFECYCLE EVENTS -----------------------  onPause()  -------------------------------------------------*/	
	@Override
	protected void onPause(){
		super.onPause();
		
		//Stop Progress Spinner
	    spinnerAnim.stop();
		spinner.setVisibility(View.INVISIBLE);
		
		//Disable location manager 
		if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
	}
    
    /*  LIFECYCLE EVENTS -----------------------  onStop()  -------------------------------------------------*/
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
	       
	       //Close down Threads;
	       TaskExecutorMap.shutdown();
		   scheduleTaskExecutorVehicles.shutdown();
		   scheduleTaskExecutorUiUpdates.shutdown();   
		   
	}
	 
	/*  LIFECYCLE EVENTS -----------------------  onStart()  -------------------------------------------------*/
	 
	 @Override
		protected void onStart(){
			super.onStart();
			
			 TaskExecutorMap = Executors.newSingleThreadExecutor();
				TaskExecutorMap.execute(new Runnable() {
				    public void run() {
				    	
				    	Log.w(TAG, "LOCAL ARRAYS LOADING...."); 
			    		
				    	//Load Singleton ArrayLists
				    	LoadCalDates();
					    TodaysScheduleType = Common.TodaysScheduleType(context, myCalDates, true);
			    		LoadFBGRoute();
				    	LoadMSSRoute();
				    	LoadStationStop();			   	
					   	
					   	Log.w(TAG, "LOCAL ARRAYS LOADED."); 
					   	
					   	// Update the UI Thread
				        runOnUiThread(new Runnable() {
				        public void run() { 
				        	
				        	//Ensure local values are loaded before committing to blnLocalDataLoaded=true
						   	if (myFBGRoutes != null && myMSSRoutes != null && myStations != null){
						   		Log.w(TAG, "DRAWING RAIL LINES AND STATIONS."); 
						   		
						   		//Pull user's preferred Map Settings
					    		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
					    		String prefLine = getPrefs.getString("listTrainLine", "FBG,ALL");
					    		stationMarker = getPrefs.getString("listStationMarker", "abbv");
					    		
					    		//Draw user lines by preference.  This will insure the preferred line
					    		//overlays correctly for when the lines Merger in Alexandria
					    		if (prefLine.equals("MSS,ALL")){
					    			
								    DrawFBGRoute(srcLatLng, destLatLng);
							        DrawMSSRoute(srcLatLng, destLatLng);
					    		}
					    		else{								    
								    DrawMSSRoute(srcLatLng, destLatLng);
								    DrawFBGRoute(srcLatLng, destLatLng);							        
					    		}
					    					    				    		
						   		if (mOldStationMarkerValue.equals(stationMarker) || mOldStationMarkerValue.length() == 0){
						   			mOldStationMarkerValue = stationMarker;
						   			
						   			DrawStationStopOverlay();
						   		}
						   		
							   	Log.w(TAG, "DRAWING RAIL LINES AND STATIONS... COMPLETE"); 
							   	blnLocalDataLoaded = true;	
							   	
							   	Log.w(TAG, "CHECKING MAPVIEW SETTINGS FOR CHANGES."); 					    	
								
						    	//Pull user's preferred Map Settings
								boolean trafficOn = getPrefs.getBoolean("chkTraffic", false);
								
								String sMapType = getPrefs.getString("listMapType", "MAP_TYPE_NORMAL");
								
								if (sMapType.equals("MAP_TYPE_NORMAL")) {
						            mMap.setMapType(MAP_TYPE_NORMAL);
						        } else if (sMapType.equals("MAP_TYPE_HYBRID")) {
						            mMap.setMapType(MAP_TYPE_HYBRID);
						        } else if (sMapType.equals("MAP_TYPE_SATELLITE")) {
						            mMap.setMapType(MAP_TYPE_SATELLITE);
						        } else if (sMapType.equals("MAP_TYPE_TERRAIN")) {
						            mMap.setMapType(MAP_TYPE_TERRAIN);
						        } 
								
								mMap.setTrafficEnabled(trafficOn);
					    		
					    		Log.w(TAG, "CHECK COMPLETE"); 
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
				    		
				    		Log.w(TAG, "CALLING VRE GTFS INTERFACE..."); 
							doRefreshGTFSFeeds();	
							Log.w(TAG, "CALL COMPELTE."); 
							    
							Log.w(TAG, "GTFS ARRAYS LOADING...."); 
							LoadVehicleStop();	
				    		LoadUpdates();
				    		Log.w(TAG, "GTFS ARRAYS LOADED."); 
				    		
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
			    		Log.w(TAG, "run(): " + ex.toString());
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
				        		  
				        		//Check if Station marker icon preference has changed, if so, redraw with new Icon
				        		  if (!(mOldStationMarkerValue.equals(stationMarker))){
				        			  
				        			  if (mapStations == null || myStations.size() == 0){
				        				  DrawStationStopOverlay();
							    		  mOldStationMarkerValue = stationMarker;
					        		  }
					        		  
				        			  
				        			//Always Destroy and re-draw vehicles
					        		  if (mapStations != null){
						        		  for(Marker m : mapStations) {
						        			  for(Station each : myStations){
						        				  
						        				  String rawTitle = m.getTitle(); //Contains both the Station Name and ID
						  	  		              String delims = "[|]";
						  	  		              String[] title = rawTitle.split(delims);
						  	  		            
						        				  if(title[1].equals(each.STATION_ID())){
						        					  
						        					  Log.w(TAG, "-----------------------------------------------------------------------------------------------------");
						        					  Log.w(TAG, "UPDATE STATION: " + title[1] + " ID: " + each.STATION_ID());
						        				      Log.w(TAG, "LAT: " + each.STATION_LATITUDE() + " LONG: " + each.STATION_LONGITUDE());
						        				      Log.w(TAG, "-----------------------------------------------------------------------------------------------------");
						        				      
						        				      UpdateStationStopMarker(m, each);
						        				  
						        				  }
						        			  }
						        		  }
					        		  }
						    	  }
				        		  
				        		  if (mapVehicles == null || mapVehicles.size() == 0){
				        			  DrawVehicleOverlay();
				        		  }
				        		  
				        		  //Always Destroy and re-draw vehicles
				        		  if (mapVehicles != null){
					        		  for(Marker v : mapVehicles) {
					        			  for(Vehicle each : myVehicles){
					        				  if(v.getTitle().equals(each.VEHICLE_ID())){
					        					  Log.w(TAG, "-----------------------------------------------------------------------------------------------------");
					        					  Log.w(TAG, "MOVED VEHICLE: " + v.getTitle() + " ID: " + each.VEHICLE_ID());
					        				      Log.w(TAG, "LAT: " + each.VEHICLE_LATITUDE() + " LONG: " + each.VEHCILE_LONGITUDE());
					        				      Log.w(TAG, "-----------------------------------------------------------------------------------------------------");
					        				  		//LatLng GP = new LatLng(Double.parseDouble(each.VEHICLE_LATITUDE()), Double.parseDouble(each.VEHCILE_LONGITUDE()));
					        				  		//v.setPosition(GP);
					        				  		
					        				  		UpdateVehicleMarker(v, each);
					        				  	  //v.remove();
					        				  }
					        			  }
					        		  }	
				        		  }		
				        	  }
					        	  
					       	  spinnerOn = false;
							        			  
				          }
				          catch(Exception e)
				          {
				        	  Log.w(TAG, "runOnUiThread: " + e.toString());
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
	 
	 private void setUpLocationClientIfNeeded() {
	        if (mLocationClient == null) {
	            mLocationClient = new LocationClient(
	                    getApplicationContext(),
	                    this,  // ConnectionCallbacks
	                    this); // OnConnectionFailedListener
	        }
	    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not have been
     * completely destroyed during this process (it is likely that it would only be stopped or
     * paused), {@link #onCreate(Bundle)} may not be called again so we should call this method in
     * {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {  
    	
    	//Pull user's preferred Map Settings
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String prefLine = getPrefs.getString("listTrainLine", "FBG,ALL");
		stationMarker = getPrefs.getString("listStationMarker", "abbv");
		
    	//Pull user's preferred Map Settings			    		
		boolean satelliteOn = getPrefs.getBoolean("chkSatellite", false);
		boolean trafficOn = getPrefs.getBoolean("chkTraffic", false);
		
		if (!satelliteOn){
			mMap.setMapType(MAP_TYPE_TERRAIN);
		}
		else
		{
			mMap.setMapType(MAP_TYPE_HYBRID);
		}
		
		mMap.setTrafficEnabled(trafficOn);
    	
    	// Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
     
        
        //Move to Thread
        /////////////////// USED TO SET STARTING POINT  //////////////////
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
		srcLatLng = new LatLng(src_lat, src_long);	
		destLatLng = new LatLng(dest_lat, dest_long);
		
		/////////////////// END USED TO SET STARTING POINT  //////////////////
 
        
        // Set listeners for marker events.  See the bottom of this class for their behavior.
        mMap.setOnMarkerClickListener(this); 
        
        // Move the map so that it is centered on the polyline.
        mMap.moveCamera(CameraUpdateFactory.newLatLng(srcLatLng)); 
        mMap.moveCamera(CameraUpdateFactory.zoomTo(9));
        mMap.setMyLocationEnabled(true);
       
    }
    
    /*  LOAD EVENTS -------------------------------------------------------------------------*/
    
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
	
	private void LoadCalDates(){
		
		//Current CalDates Collection
		myCalDates = BusinessBaseCalDates.getAllCalDates(this);		
	}
	
	private void LoadUpdates(){
		
		//Current Station Collection
		myUpdates = BusinessUpdate.getAllUpdates(context);
	}
	
	/*  MAP LOAD EVENTS -------------------------------------------------------------------------*/
	
	private void DrawFBGRoute(LatLng src, LatLng dest){
		
		DrawRoute(src, dest, myFBGRoutes, Color.RED); 
	}
	
	private void DrawMSSRoute(LatLng src, LatLng dest){
		
		DrawRoute(src, dest, myMSSRoutes, Color.BLUE);
	}
	
	private void DrawRoute(LatLng src, LatLng dest, ArrayList<Route> route, int DefaultColor){
		
		Integer counter = 0;
		
		LatLng startGP = null;
		LatLng gp1 = null;
		LatLng gp2 = null;
		
		ArrayList<LatLng> routePoints = new ArrayList<LatLng>();		
		
		for (Route each : route) {			
			if (counter == 0){	
				
				startGP = new LatLng(Double.parseDouble(each.ROUTE_LATITUDE()), Double.parseDouble(each.ROUTE_LONGITUDE()));
				routePoints.add(startGP); 				
				gp2 = startGP;
								
				counter++;
			}
			else
			{		
				gp1 = gp2;
				gp2 = new LatLng(Double.parseDouble(each.ROUTE_LATITUDE()), Double.parseDouble(each.ROUTE_LONGITUDE()));
				
				routePoints.add(gp2); 	
			}					
		}	
		
		routePoints.add(dest); 	
		
		mMap.addPolyline((new PolylineOptions())
                .addAll(routePoints)
                .width(5)
                .color(DefaultColor)
                .geodesic(true));
		
	}	
		
	private void DrawStationStopOverlay(){		
		
		LatLng stationLatLng = null;
		String stationIconString, stationTitle, stationSnippet;
		int stationIcon;
		
		mapStations = new ArrayList<Marker>();
		
		try{
			for (Station each : myStations) {					
			
				//Set local variables for each object in collection
				
				//Station Location (Lat Long)
				stationLatLng = new LatLng(Double.parseDouble(each.STATION_LATITUDE()), Double.parseDouble(each.STATION_LONGITUDE()));
				
				//Station Marker Icon
				if (stationMarker.equals("abbv"))
					stationIconString = "drawable/station_" + each.STATION_ID().toLowerCase();
				else
					stationIconString = "drawable/stop_marker";
				
				stationIcon = (int)(getResources().getIdentifier(stationIconString, null, getPackageName()));
				
				//Station Marker Title (HACK ALERT!!!: Since maps.model Marker does not have an ID property, 
				//concatenate Station Name and ID and parse out ID later.)
				stationTitle = each.STATION_STOP_NAME() + "|" + each.STATION_ID();
				
				//Station Snippet
				stationSnippet = each.ZONE() + "\n";
				stationSnippet += each.ADDRESS() + "\n";
				stationSnippet += each.CITY() + ", " + each.STATE() + " " + each.ZIP() + "\n";
				
				
				// Uses a custom icon with the info window popping out of the center of the icon.	
		        mStation = mMap.addMarker(new MarkerOptions()
		                .position(stationLatLng)
		                .title(stationTitle)		              
		                .snippet(stationSnippet)
		                .icon(BitmapDescriptorFactory.fromResource(stationIcon))
		                .infoWindowAnchor(0.5f, 0.5f));	
		        
		        //Build Stations list collection once they are commited to the Map
		        mapStations.add(mStation);
			}		
			
		}
		catch(Exception e)
		{
			Log.w(TAG, "DrawStationStopOverlay ERROR: " + e.toString());	
		}			
	}
	
	private void UpdateStationStopMarker(Marker marker, Station myStation){		
		
		LatLng stationLatLng = null;
		String stationIconString, stationTitle, stationSnippet;
		int stationIcon;
		
		
		try{
							
			
				//Set local variables for each object in collection
				
				//Station Location (Lat Long)
				stationLatLng = new LatLng(Double.parseDouble(myStation.STATION_LATITUDE()), Double.parseDouble(myStation.STATION_LONGITUDE()));
				
				//Station Marker Icon
				if (stationMarker.equals("abbv"))
					stationIconString = "drawable/station_" + myStation.STATION_ID().toLowerCase();
				else
					stationIconString = "drawable/stop_marker";
				
				stationIcon = (int)(getResources().getIdentifier(stationIconString, null, getPackageName()));
				
				//Station Marker Title (HACK ALERT!!!: Since maps.model Marker does not have an ID property, 
				//concatenate Station Name and ID and parse out ID later.)
				stationTitle = myStation.STATION_STOP_NAME() + "|" + myStation.STATION_ID();
				
				//Station Snippet
				stationSnippet = myStation.ZONE() + "\n";
				stationSnippet += myStation.ADDRESS() + "\n";
				stationSnippet += myStation.CITY() + ", " + myStation.STATE() + " " + myStation.ZIP() + "\n";
				
				
				// Uses a custom icon with the info window popping out of the center of the icon.	
		        //mStation = mMap.addMarker(new MarkerOptions()
		        //        .position(stationLatLng)
		        //        .title(stationTitle)		              
		        //        .snippet(stationSnippet)
		         //       .icon(B;tmapDescriptorFactory.fromResource(stationIcon))
		        //        .infoWindowAnchor(0.5f, 0.5f));	
		        
		        marker.setIcon(BitmapDescriptorFactory.fromResource(stationIcon));
					
			
		}
		catch(Exception e)
		{
			Log.w(TAG, "DrawStationStopOverlay ERROR: " + e.toString());	
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
	private void DrawVehicleOverlay(){
			
		mapVehicles = new ArrayList<Marker>();
		
		boolean VehicleAtStation = false;
		
		LatLng vehicleLatLng = null;
		String vehicleIconString, vehicleTitle, vehicleIconName, vehicleSnippet;
		int vehicleIcon;
		
		
		String StationLat = "";
		String StationLong = "";
		String iconColor = ""; //Default is Green  
		
		lblMsg = "";	//Clear 	
		
		VehicleBubble myVehicleBubble = null;  //IDEA!  Convert to Collection refresh collection only when Spinner recycles
		
		if (!(myVehicles == null)){		
				
		try{
			
			for (Vehicle eachVehicle : myVehicles) { 
				
				//Station Location (Lat Long)
				vehicleLatLng = new LatLng(Double.parseDouble(eachVehicle.VEHICLE_LATITUDE()), Double.parseDouble(eachVehicle.VEHCILE_LONGITUDE()));
				
				////vehicleLat = Double.parseDouble(eachVehicle.VEHICLE_LATITUDE());  
				////vehicleLong = Double.parseDouble(eachVehicle.VEHCILE_LONGITUDE());  
				
				// Pull user's preferred default train line // 
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
					vehicleIcon = (int)(getResources().getIdentifier(vehicleIconString, null, getPackageName()));
					
					//Vehicle Title
					vehicleTitle = myVehicleBubble.VEHICLE_ID();
					
					//vehicleInfo = myVehicleBubble.VEHICLE_LABLE() + " \n"; 
					vehicleSnippet = myVehicleBubble.STATUS() + " \n"; 
					vehicleSnippet += myVehicleBubble.NEXT_STOP_AND_TIME() + " \n"; 
					vehicleSnippet += "To: " + myVehicleBubble.HEADING() + " \n"; 
					vehicleSnippet += myVehicleBubble.STOPS_REMAINING();
					
					
					//Determine which color icon to display
					iconColor = "green";  //Default: green
					
					if (myVehicleBubble.DELAYED())  //Delayed turns orange
						iconColor = "orange";
					
					if (myVehicleBubble.LOST_COMM()) 	//Lost Comms overrides Default and Delay turns gray
						iconColor = "gray";
					
					
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
						//In case a new Train is added and there is no graphic for it, Use this catch-all
						vehicleIconName = "drawable/map_train_green";
						imageResource = getResources().getIdentifier(vehicleIconName, null, getPackageName());
					}					
					
				   
				    
				   // Uses a custom icon with the info window popping out of the center of the icon.	
			        mVehicle = mMap.addMarker(new MarkerOptions()
			                .position(vehicleLatLng)
			                .title(vehicleTitle)		              
			                .snippet(vehicleSnippet)
			                .icon(BitmapDescriptorFactory.fromResource(imageResource))
			                .infoWindowAnchor(0.5f, 0.5f));	
			        
			        //Build Stations list collection once they are committed to the Map
			        mapVehicles.add(mVehicle);						
				}
			} 
						
		}
		catch(Exception e)
		{
			Log.w(TAG, "DrawVehicleOverlay: " + e.toString());	
		}	
		
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
	private void UpdateVehicleMarker(Marker vMarker, Vehicle myVehicle){
			
				
		boolean VehicleAtStation = false;
		
		LatLng vehicleLatLng = null;
		String vehicleIconString, vehicleTitle, vehicleIconName, vehicleSnippet;
		int vehicleIcon;
		
		
		String StationLat = "";
		String StationLong = "";
		String iconColor = ""; //Default is Green  
		
		lblMsg = "";	//Clear 	
		
		VehicleBubble myVehicleBubble = null;  //IDEA!  Convert to Collection refresh collection only when Spinner recycles
		
		if (!(vMarker == null)){		
				
		try{
			
			//for (Vehicle eachVehicle : myVehicles) { 
				
				//Station Location (Lat Long)
				vehicleLatLng = new LatLng(Double.parseDouble(myVehicle.VEHICLE_LATITUDE()), Double.parseDouble(myVehicle.VEHCILE_LONGITUDE()));
				
				////vehicleLat = Double.parseDouble(eachVehicle.VEHICLE_LATITUDE());  
				////vehicleLong = Double.parseDouble(eachVehicle.VEHCILE_LONGITUDE());  
				
				// Pull user's preferred default train line // 
				SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				String timeformat = getPrefs.getString("listTimeFormat", "12");				
							
				if (myStations != null && myUpdates != null && TodaysScheduleType != null)
				    myVehicleBubble = BusinessSchedule.fetchVehicleBubble(this, myVehicle, myUpdates, myStations, TodaysScheduleType, timeformat);  
				
				
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
					vehicleIcon = (int)(getResources().getIdentifier(vehicleIconString, null, getPackageName()));
					
					//Vehicle Title
					vehicleTitle = myVehicleBubble.VEHICLE_ID();
					
					//vehicleInfo = myVehicleBubble.VEHICLE_LABLE() + " \n"; 
					vehicleSnippet = myVehicleBubble.STATUS() + " \n"; 
					vehicleSnippet += myVehicleBubble.NEXT_STOP_AND_TIME() + " \n"; 
					vehicleSnippet += "To: " + myVehicleBubble.HEADING() + " \n"; 
					vehicleSnippet += myVehicleBubble.STOPS_REMAINING();
					
					
					//Determine which color icon to display
					iconColor = "green";  //Default: green
					
					if (myVehicleBubble.DELAYED())  //Delayed turns orange
						iconColor = "orange";
					
					if (myVehicleBubble.LOST_COMM()) 	//Lost Comms overrides Default and Delay turns gray
						iconColor = "gray";
					
					
					int imageResource;				
													
					//Train Station Map Icon
					if (myVehicleBubble.AT_STATION()){
						if (LoopCounter%2==0)
							vehicleIconName = "drawable/map_train_" + iconColor + "_" + myVehicle.VEHICLE_ID().toLowerCase();
						else
							vehicleIconName = "drawable/map_train_" + iconColor + "_arrive";					
					}
					else
					{
						vehicleIconName = "drawable/map_train_" + iconColor + "_" + myVehicle.VEHICLE_ID().toLowerCase();
					}
															
					try{
						
						imageResource = getResources().getIdentifier(vehicleIconName, null, getPackageName());
					}
					catch(Exception e)
					{
						//In case a new Train is added and there is no graphic for it, Use this catch-all
						vehicleIconName = "drawable/map_train_green";
						imageResource = getResources().getIdentifier(vehicleIconName, null, getPackageName());
					}					
					
				  			        
			        vMarker.setPosition(vehicleLatLng);
			        vMarker.setSnippet(vehicleSnippet);
			        
			        //TODO://Commented showInfoWindow method, causing issues because last marker to render always displayed InfoWindow		        
			        //if (VehicleAtStation){
			        	vMarker.setIcon(BitmapDescriptorFactory.fromResource(imageResource)); 
			        	//vMarker.showInfoWindow();
			       // }
			        					
				}
			//} 
						
		}
		catch(Exception e)
		{
			Log.w(TAG, "DrawVehicleOverlay: " + e.toString());	
		}	
		
		}
	}
	
	
	/** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements InfoWindowAdapter {
    
        // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);          
        }

        @Override
        public View getInfoWindow(Marker marker) {
            
            render(marker, mWindow);
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
        	
        	// This means that the default info contents will be used.
            return null;
        }

        private void render(Marker marker, View view) {
            //int badge = 0; 
            String stationIconString = null, sTitle = null;
    		int stationIcon = 0;
    		
    		if (mapStations != null){
  			  for(Marker m : mapStations) {
	  				if (marker.equals(m)) {
	  					
	  					//HACK ALERT!!!  ;-)
	  		            //My HACK to get the Station Name and Station ID together from the Marker.Title property
	  		            //tokens[0] = Station Name (exp: Union Station)
	  		            //tokens[1] = Station ID   (exp: 00000000000000000000000000000001)            
	  		            String rawTitle = marker.getTitle(); //Contains both the Station Name and ID
	  		            String delims = "[|]";
	  		            String[] tokens = rawTitle.split(delims);
	  		            
	  		            //Station Marker Icon
	  					stationIconString = "drawable/station_icon_" + tokens[1].toLowerCase();
	  					stationIcon = (int)(getResources().getIdentifier(stationIconString, null, getPackageName())); 
	  					
	  					sTitle = tokens[0];
	  					
	  				}
	        	  }
			  }
    		
    		if (mapVehicles != null){
    			  for(Marker v : mapVehicles) {
  	  				if (marker.equals(v)) {  	  					
  	  					           
  	  		            sTitle = marker.getTitle(); //Contains both the Station Name and ID 
  	  		            //Station Marker Icon
  	  					stationIconString = "drawable/train";
  	  					stationIcon = (int)(getResources().getIdentifier(stationIconString, null, getPackageName())); 
  	  					
  	  				}
  	        	  }
  			  }
    		
    		Log.w(TAG, "OOOOOOO  TITLE: " + sTitle);	
    		Log.w(TAG, "OOOOOOO  StationIconString: " + stationIconString);	
    		Log.w(TAG, "OOOOOOO  StationIcon: " + stationIcon);	
            
            ((ImageView) view.findViewById(R.id.badge)).setImageResource(stationIcon);

            String title = sTitle;
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();     
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null) {
                SpannableString snippetText = new SpannableString(snippet);
                //snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                //snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);   
                snippetText.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
        }
    }
    
    @Override
    public boolean onMarkerClick(final Marker marker) {
    	
        //if (marker.equals(mStation)) {
            // This causes the marker at Stations to bounce into position when it is clicked.
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final long duration = 1500;

            final Interpolator interpolator = new BounceInterpolator();

            
            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = Math.max(1 - interpolator
                            .getInterpolation((float) elapsed / duration), 0);
                    marker.setAnchor(0.5f, 1.0f + 2 * t);
                    

                    if (t > 0.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });
            
            
        //} 
            
            
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }
    
    /*  MENU AND NAVIGATION ------------------------------------------------------------------------*/
     
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
			Intent a = new Intent(ActivityGoogleMapV2.this, ActivityAbout.class);
			a.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(a);
		} else if (item.getItemId() == R.id.options) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "options",			//label
									 0);				//value
			Intent p = new Intent(ActivityGoogleMapV2.this, ActivityPreferences.class);
			p.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(p);
		} else if (item.getItemId() == R.id.oldmap) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "oldmap",		//label
									 0);				//value
			Intent o = new Intent(ActivityGoogleMapV2.this, ActivityOldMap.class);
			o.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(o);
		} else if (item.getItemId() == R.id.alerts) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "alerts",			//label
									 0);				//value
			Intent x = new Intent(ActivityGoogleMapV2.this, ActivityAlerts.class);
			x.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(x);
		} else if (item.getItemId() == R.id.schedules) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "schedules",		//label
									 0);				//value
			Intent e = new Intent(ActivityGoogleMapV2.this, ActivityStationSchedule.class);
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
				Intent myIntent = new Intent(ActivityGoogleMapV2.this, ActivityAlerts.class);
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
				Intent myIntent = new Intent(ActivityGoogleMapV2.this, ActivityStationSchedule.class);
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
				Intent myIntent = new Intent(ActivityGoogleMapV2.this, ActivityTwitterFeed.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent); 
			} 
		}); 
	}
	
	/*  END ---- MENU AND NAVIGATION ------------------------------------------------------------------------*/
	
	@Override
	protected void onDestroy() {
		 super.onDestroy();
		    
		// Stop the tracker when it is no longer needed.
		googleTracker.stopSession();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  // LocationListener
    }

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}
     
}

