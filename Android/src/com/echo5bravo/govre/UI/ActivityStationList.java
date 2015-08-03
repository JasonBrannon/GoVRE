package com.echo5bravo.govre.UI;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.echo5bravo.govre.INFO.CalDates;
import com.echo5bravo.govre.INFO.Device;
import com.echo5bravo.govre.INFO.Schedule;
import com.echo5bravo.govre.INFO.Station;
import com.echo5bravo.govre.INFO.Update;
import com.echo5bravo.govre.INFO.Vehicle;
import com.echo5bravo.govre.UTILS.Common;
import com.echo5bravo.govre.ADAPTERS.StationAdapter;
import com.echo5bravo.govre.BLL.BusinessCalDates;
import com.echo5bravo.govre.BLL.BusinessSchedule;
import com.echo5bravo.govre.BLL.BusinessStation;
import com.echo5bravo.govre.BLL.BusinessUpdate;
import com.echo5bravo.govre.BLL.BusinessVehicle;
import com.echo5bravo.govre.DAL.BusinessBaseCalDates;
import com.echo5bravo.govre.R;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
/*
 * Activity Displays the VRE Train Stations
 */

public class ActivityStationList extends Activity implements LocationListener, SensorEventListener {  

	private static final String TAG = ActivityStationList.class.getSimpleName();   
	
	private GoogleAnalyticsTracker googleTracker;
	
	private static final String PREFS_NAME = "OldPreferences"; 
	private static final String PREFS_TRAIN_LINE_KEY = "OldTrainLineFilterKEY";
	private static final String PREFS_STATION_SORT_KEY = "OldTrainLineSortKEY";
	private static final String PREFS_TIME_FORMAT_KEY = "OldTimeFormatKEY";
	private String mOldTrainLineFilterValue; 
	private String mOldTrainLineSortValue;  
	private String mOldTimeFormatValue;
	
	// Any Station whose distance is less that this value 
	// is going to give spurious results so we discount them.
	public static final int NOISE = 1;

	private SensorManager sensorManager; 
	private boolean holdCompass = false;  
	private int myBearing = 0;
	private int newBearing = 0;
	private Location currentLocation;
	private ListView myListView;

	//----------- Added from Location Activity
	private LocationManager locationManager;
	
	private StationAdapter adapter;
	
	boolean blnLocalDataLoaded = false;
	private ScheduledExecutorService scheduleTaskExecutorLoadData;
	private ScheduledExecutorService scheduleTaskExecutorUpdateSpinner;
	
	private ImageView imgTwitter, imgMap, imgAlerts, imgSched;  
	private Context context;
	
	private ImageView spinner;
	private AnimationDrawable spinnerAnim;
	private boolean spinnerOn;
	
	ArrayList<Station> myStationStops;  
	ArrayList<Schedule> mySchedule;
	ArrayList<CalDates> myCalDates;
	ArrayList<Vehicle> myVehicles;
	ArrayList<Update> myUpdates;
	String TodaysScheduleType;	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setTheme(R.style.Theme_BlueMoon);
		this.setContentView(R.layout.station_listview_layout); 
		
		//Start Google Analytics Tracker
		//-------------------------------------------------------------------------------------------------
		googleTracker = GoogleAnalyticsTracker.getInstance();
		googleTracker.setDebug(Boolean.parseBoolean(this.getString(R.bool.ga_debug)));
		googleTracker.setDryRun(Boolean.parseBoolean(this.getString(R.bool.ga_dryrun)));
		googleTracker.startNewSession(this.getString(R.string.ga_api_key), 30, this);

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
		
		Device.isDeviceOnline(this.getApplicationContext());
		
		myListView = (ListView) findViewById(R.id.listView); 		
		
		spinner = (ImageView) findViewById(R.id.splashSpinner);
		spinner.setVisibility(View.INVISIBLE); //Added this to test Layout
		
		spinnerAnim = (AnimationDrawable) spinner.getBackground();	
	
	
		// Get the location manager
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);		 	
				
		/*
		 * Since getting the current provider's current location is costly, we're using
		 * the last known location to speed the load time of this activity.  We'll set 
		 * the current location onResume() so our ListView will populate 1st and get an 
		 * update when the current location becomes available.
		 */		
		try{
			if(this.getBestLocationProvider() != null)
				currentLocation = locationManager.getLastKnownLocation(this.getBestLocationProvider());	 
		

			// Remember where we are. If the Device has or enabled WiFi/Service location tracking 
			if (currentLocation != null)
				this.setCurrentLocation(savedInstanceState);
			
			// Get the compass service to give our bearings.
			//Log.d(TAG, "Connecting to the compass sensor");		
			sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		}
		catch(Exception ex)
		{
			ex.toString();
		}        
        addListenerOnTwitterImage();  
        addListenerOnMapImage(); 
        addListenerOnAlert();     
        addListenerOnSched(); 
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
				Intent myIntent = new Intent(ActivityStationList.this, ActivityTwitterFeed.class); 
				myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent); 
			} 
		}); 
	}
	
	private void addListenerOnMapImage() {
		 
		imgMap = (ImageView) findViewById(R.id.imgMap); 

		imgMap.setOnClickListener(new View.OnClickListener() {
 
			public void onClick(View arg0) {
				
				//Track GoogleAnalytics Event
				googleTracker.trackEvent("ui_interaction",		//category
					        			 "from_icon",			//action
					        			 "googlemap",			//label
					        			 0);					//value
				
				//Do Something
				Intent myIntent = new Intent(ActivityStationList.this, ActivityGoogleMapV2.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);     			
			} 
		}); 
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
				Intent myIntent = new Intent(ActivityStationList.this, ActivityAlerts.class);
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
				Intent myIntent = new Intent(ActivityStationList.this, ActivityStationSchedule.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);     			
			} 
		}); 
	}
	
	private void doRefreshGTFSFeeds() {
		
		/* Verify the Device is Connected before invoking a method requiring INTERNET*/
    	if (Device.isDeviceOnline(context.getApplicationContext())){	    	  
    		
    		//Pull Vehicles from VRE GTFS and Load them to SQL
    		BusinessVehicle.LoadVehiclesFromGTFS(this);
    		
    		//Pull Trip Updates from VRE GTFS and Load them to SQL
    		BusinessUpdate.LoadUpdatesFromGTFS(this);
    	}		
	}
	
	private String getBestLocationProvider(){
		
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setSpeedRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);	
        
        //note getBestProvider using true = only return services that are enabled
        return locationManager.getBestProvider(criteria, true);               
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		//Log.d(TAG, "OnStart Called");
		
		scheduleTaskExecutorLoadData = Executors.newSingleThreadScheduledExecutor();

	    // This schedule a task to run every 59 seconds:
		scheduleTaskExecutorLoadData.scheduleAtFixedRate(new Runnable() {
	      public void run() {
	    	
	    	try{
	    		
	    		spinnerOn = true;
	    	  
		    	if (blnLocalDataLoaded == false){		    		
		    		
			    	//Load Singleton ArrayLists
			    	LoadStationStops();	
				   	LoadCalDates();
				   	TodaysScheduleType = Common.TodaysScheduleType(context, myCalDates, false);
				   	LoadSchedule();	
				   	
				   	//Ensure local values are loaded before committing to blnLocalDataLoaded=true
				   	if (myStationStops != null && mySchedule != null && myCalDates != null)
				   		blnLocalDataLoaded = true;	
				   	else
				   		blnLocalDataLoaded = false;	
			   	}
			    		
			   	//Get GTFS data on separate thread (Requires Network) 
				doRefreshGTFSFeeds();	
				    
				LoadVechicles();
				LoadUpdates();	
				
				spinnerOn = false;
				
	    	}
	    	catch(Exception ex)
	    	{
	    		spinnerOn = false;
	    	}
	    		
	    	// Update the UI Thread
	        runOnUiThread(new Runnable() {
	          public void run() {
	            // Update ListView
	        	updateStationList(true);  
	        	
	        	spinnerOn = false;
	          }
	        });
	      }
	    }, 0, 59, TimeUnit.SECONDS);	    
		
		scheduleTaskExecutorUpdateSpinner = Executors.newScheduledThreadPool(4);
	    // This schedule a task to run every second:
		scheduleTaskExecutorUpdateSpinner.scheduleAtFixedRate(new Runnable() { 
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
	            // Update ListView
	        	updateStationList(false); 	
  	          }
	        });
	      }			
		}, 0, 500, TimeUnit.MILLISECONDS);						
	}
	
	private void LoadStationStops(){		
		//Current Station Collection
		myStationStops = BusinessStation.getAllStations(currentLocation, context);		
	}
	
	private void LoadSchedule(){		
		//Current Schedule Collection
		mySchedule = BusinessSchedule.getAllSchedules(context, TodaysScheduleType);		
	}
	
	private void LoadCalDates(){		
		//Current CalDates Collection
		//myCalDates = BusinessBaseCalDates.getAllCalDates(context);	
		myCalDates = BusinessCalDates.getAllCalDates(context);
	}
	
	private void LoadVechicles(){		
		//Current Station Collection
		myVehicles = BusinessVehicle.getAllVehicles(context);
	}
	
	private void LoadUpdates(){		
		//Current Station Collection
		myUpdates = BusinessUpdate.getAllUpdates(context);
	}
	
	/*
	 *  Register for the updates when Activity is in foreground  
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		//Track current Activity
		googleTracker.trackPageView("/" + TAG);
		
		//Log.d(TAG, "OnResume Called");
		
		/* When Activity restored, fetch the current mOldTrainLineFilterValue
	    * this is used in the updateStationList() method to toggle between setting
	    * a New instance of an adapter vs updating the current adapter in the 
	    * event the user changes their Train Line preference.
	    * 
	    * Using the Editor object by the "onStop()" event to make the preference 
	    * change, this will maintain state of our value even if the app is killed
	    */
	    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	    mOldTrainLineFilterValue = settings.getString(PREFS_TRAIN_LINE_KEY, "");	
	    mOldTrainLineSortValue = settings.getString(PREFS_STATION_SORT_KEY, "");	
	    mOldTimeFormatValue = settings.getString(PREFS_TIME_FORMAT_KEY, "");
	    	    
	    /*Register the ideal sensor listener*/	
	    sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
		//Log.d(TAG, "Started listening for compass updates.");	
			
		provideTheLocationProvider();		
	}

	/*
	 *  Stop the sensor updates when Activity is paused 
	 */
	@Override
	protected void onPause() {
		super.onPause();
		//Log.d(TAG, "OnPause Called");
		
		sensorManager.unregisterListener(this);		
		locationManager.removeUpdates(this);	
		
		//Stop Progress Spinner
	    spinnerAnim.stop();
		spinner.setVisibility(View.INVISIBLE);		
	}	
	
	@Override
    protected void onStop(){
       super.onStop();
       
       //Log.d(TAG, "OnStop Called");

      /* Before the Activity stops, save the current mOldTrainLineFilterValue
       * this is used in the updateStationList() method to toggle between setting
       * a New instance of an adapter vs updating the current adapter in the 
       * event the user changes their Train Line preference.
       * 
       * Using the Editor object to make the preference change, this will maintain
       * state of our value even if the app is killed
       * */
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putString(PREFS_TRAIN_LINE_KEY, mOldTrainLineFilterValue);
      editor.putString(PREFS_STATION_SORT_KEY, mOldTrainLineSortValue); 
      editor.putString(PREFS_TIME_FORMAT_KEY, mOldTimeFormatValue);
      
      // Commit the edits!
      editor.commit();      
      
      //Stop Progress Spinner
      spinnerAnim.stop();
	  spinner.setVisibility(View.INVISIBLE);
	  
	  //Shutdown the Threads
	  scheduleTaskExecutorLoadData.shutdown();
	  scheduleTaskExecutorUpdateSpinner.shutdown();
    }
	
	/*
	 * This is listening for changes in the compass. When it happens we update
	 * our heading so the view can refresh itself.
	 */
	public void onSensorChanged(SensorEvent event) {
			
		if (holdCompass) return;
		int newBearing = (int) event.values[0];

		int delta = newBearing - myBearing;
		if (delta < 0)
			delta += 360;
		else if (delta > 180)
			delta -= 360;

		if (delta != 0) {
			myBearing += delta;
			if (myBearing > 360) myBearing -= 360;
			else if (myBearing < 0) myBearing += 360;			
		}		
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// I Don't Care				
	}
	
	/*
	 * When the activity starts we get the user's current location. This can
	 * either come from the given bundle if we are resuming, or from the intent
	 * if we are starting from fresh. We show this in the view so they know
	 * where they are, and set it as a fixed location so we can compute our
	 * distance from it.
	 */
	private void setCurrentLocation(Bundle saveState) {
		Bundle extras = (saveState == null) ? getIntent().getExtras() : saveState;

		if (extras != null) {
			double latitude = extras.getDouble(BusinessStation.KEY_LATITUDE);
			double longitude = extras.getDouble(BusinessStation.KEY_LONGITUDE);			
			String provider = extras.getString(Station.KEY_PROVIDER);

			currentLocation = new Location(provider);
			currentLocation.setLatitude(latitude);
			currentLocation.setLongitude(longitude);	
		}
	}
	
	/*
	 * Now we have a location, by whatever means, we can shift to the activity that does the real
	 * work of showing where all the Stations are.  We pass the location to the activity in a bundle
	 * and terminate this activity.
	 */
	private void startUsingLocation() {		
				
		/* If the currentLocation is NULL, then the Device has no WiFi, GPS or means to 
		 * get the current or last known location and save it, so don't save these values 
		 * in the Activity Bundle.
		 */
		if (currentLocation != null){
			Bundle extras = new Bundle();
			
			extras.putDouble(BusinessStation.KEY_LATITUDE, currentLocation.getLatitude());
			extras.putDouble(BusinessStation.KEY_LONGITUDE, currentLocation.getLongitude());
			extras.putString(Station.KEY_PROVIDER, currentLocation.getProvider());	 
		}	
	}

	/*
	 * Remember our current location if we have one.
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);		
		/*
		 * 	Save UI state changes to the savedInstanceState.
		 *  This bundle will be passed to onCreate if the process is
		 *  killed and restarted.
		 */
		if (currentLocation != null) {
			outState.putDouble(BusinessStation.KEY_LATITUDE, currentLocation.getLatitude());
			outState.putDouble(BusinessStation.KEY_LONGITUDE, currentLocation.getLongitude());
			outState.putString(BusinessStation.KEY_NAME, currentLocation.getProvider());
		}
	}	
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {  
	  super.onRestoreInstanceState(savedInstanceState);	  
	  // Restore UI state from the savedInstanceState.
	  // This bundle has also been passed to onCreate.
	}
	
	/*
	 *  Rebuilds the Stations and refreshes the view based on the current device heading
	*/
	private void updateStationList(boolean blnCalledByTimer) {	
		
		/*
		 * ListView Adapter check, if null set the initial binding, else refresh the adapter datasource
		 * to maintain state of the listview (ie.. user scroll position).
		 * 
		 * Must Check New vs Current Train Line selected (ie RED, BLUE or ALL).  If they don't match,
		 * the data adapter must be re-created because we're filtering out unwanted stations, this prevents 
		 * NULL exceptions in out ListView.
		 */
		
		/* Pull user's preferred default train line */
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String lineFilter = getPrefs.getString("listTrainLine", "FBG,ALL,MSS");
		if (lineFilter.equals("BOTH")) //Regression Support for v1.0.0.4 for "BOTH"
      		lineFilter = "FBG,ALL,MSS";
		
		String lineSortOrder = getPrefs.getString("listTrainLineSort", "proximity");
		String timeformat = getPrefs.getString("listTimeFormat", "12");
		
		if (myListView.getAdapter() == null  || mOldTrainLineFilterValue != lineFilter || mOldTrainLineSortValue != lineSortOrder || mOldTimeFormatValue != timeformat) {	 
			
			if (myStationStops != null && mySchedule != null && myCalDates != null){
								
				Station[] Station_data = BusinessStation.fetchAllStationsAtBearing(currentLocation
																					,myBearing
																					,NOISE
																					,lineFilter
																					,this
																					,blnCalledByTimer																				
																					,lineSortOrder
																					,myStationStops
																					,mySchedule
																					,TodaysScheduleType
																					,myVehicles
																					,myUpdates
																					,blnCalledByTimer
																					,timeformat);
				
				adapter = new StationAdapter(this, R.layout.station_row, Station_data, myBearing);
				myListView.setAdapter(adapter);
					
				//Re-establish the new preference because they have changed
				mOldTrainLineFilterValue = lineFilter;
				mOldTrainLineSortValue = lineSortOrder;
				mOldTimeFormatValue = timeformat;					
								
				myListView.setOnItemClickListener(new OnItemClickListener() {  
						
				public void onItemClick(AdapterView<?> a, View v, int position, long id) {
							
					Object o = myListView.getItemAtPosition(position);
					Station fullObject = (Station) o;
							
					Intent myIntent = new Intent(ActivityStationList.this, ActivityStationSchedule.class);
					myIntent.putExtra("UserSelectedStationId", fullObject.STATION_ID());
					myIntent.putExtra("UserSelectedStationName", fullObject.STATION_STOP_NAME());	
										
					myIntent.putExtra("UserSelectedStationLines", fullObject.STATION_LINES());
					myIntent.putExtra("UserSelectedStationZone", fullObject.ZONE());
					myIntent.putExtra("UserSelectedStationAddress", fullObject.ADDRESS());
					myIntent.putExtra("UserSelectedStationCity", fullObject.CITY());
					myIntent.putExtra("UserSelectedStationState", fullObject.STATE());
					myIntent.putExtra("UserSelectedStationZip", fullObject.ZIP());
					startActivity(myIntent); 
			
					}
				});
			}
				
		} else {	
				
			try{
				if (myStationStops != null && mySchedule != null && myCalDates != null){
					
					myListView.invalidate();					
					Station[] Station_data = BusinessStation.fetchAllStationsAtBearing(currentLocation
																						,myBearing
																						,NOISE
																						,lineFilter
																						,this
																						,blnCalledByTimer																				
																						,lineSortOrder
																						,myStationStops
																						,mySchedule
																						,TodaysScheduleType
																						,myVehicles
																						,myUpdates
																						,blnCalledByTimer
																						,timeformat);
					
					((StationAdapter)myListView.getAdapter()).refill(Station_data, myBearing);	
				}
			}
			catch(Exception e)
			{
				e.toString();
			}
		}	
	}		
	 
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	     MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.menu, menu);
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
			Intent a = new Intent(ActivityStationList.this, ActivityAbout.class);
			a.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(a);
		} else if (item.getItemId() == R.id.options) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "options",			//label
									 0);				//value
			Intent p = new Intent(ActivityStationList.this, ActivityPreferences.class);
			p.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(p);
		} else if (item.getItemId() == R.id.quit) {
			
			//Pull user's preferred Settings
			SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    		boolean chkPrompt = getPrefs.getBoolean("chkPromptClose", false);
    		
    		if (chkPrompt)
    			this.SummonCloseDialog();
    		else
    			finish();
    		
			
		} else if (item.getItemId() == R.id.googlemap) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "googlemap",		//label
									 0);				//value
			Intent o = new Intent(ActivityStationList.this, ActivityGoogleMapV2.class);
			o.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(o);
		} else if (item.getItemId() == R.id.alerts) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "alerts",			//label
									 0);				//value
			Intent x = new Intent(ActivityStationList.this, ActivityAlerts.class);
			x.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(x);
		} else if (item.getItemId() == R.id.schedules) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "schedules",		//label
									 0);				//value
			Intent e = new Intent(ActivityStationList.this, ActivityStationSchedule.class);
			e.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(e);
		}
	    return true;
	}
	
	//Generates Confirmation Dialog Confirmation Before Exit
	private void SummonCloseDialog()
	{
		//Ask the user if they want to quit
		new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setTitle(R.string.quit)
		.setMessage(R.string.really_quit)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				//Stop the activity
				finish(); 
			}
		})
		.setNegativeButton(R.string.no, null)
		.show(); 
	}
		
	/* Give the user a chance to cancel closing the app */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//Handle the back button 
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			
			//Pull user's preferred Settings
			SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    		boolean chkPrompt = getPrefs.getBoolean("chkPromptClose", false);
    		
    		if (chkPrompt)
    			this.SummonCloseDialog();
    		else
    			finish(); 
    		
    		
			return true;
		}
		else {
			return super.onKeyDown(keyCode, event);
		}
	} 
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		//Refresh the datasource which will notify listening objects to our adapter.
		updateStationList(false); 
	}	

    /*
    * GeoLocationListener: decides which location object to use.
    * 
    * if our currentLocation is null try to use the location that was just created
    * but in the even the device has no GPS or NETWORK provider available then
    * getLastKnownLocation. attempting 1st by getting the last known location 
    * from the NETWORK_PROVIDER else try the GPS_PROVIDER.
    * 
    * getLastKnownLocation location can be dated but we're not trying to fly a missile
    * so it will work for generating the compass bearings.	 * 
    */
	public void onLocationChanged(Location location) { 
	
		if (location != null)
			this.ReturnLocation(location);			
	}
	
	public void onProviderDisabled(String provider) {	
		/*
	    * If the current GPS SERVICE PROVIDER is disabled by the user
	    * go to the next optimal service provider (ie.. GPS, WiFi, Cell-Tower (3-4G))
	    */        	
		provideTheLocationProvider();
	}
	
	public void onProviderEnabled(String provider) {			
		/*
	    * GPS SERVICE Was enabled, try to get a better GeoLoc Code
	    */  
		provideTheLocationProvider();		
	}

	private void provideTheLocationProvider(){ 
		// Check if provider is enabled		
		if (this.getBestLocationProvider() != null){		
			locationManager.requestLocationUpdates(this.getBestLocationProvider(),
					0,  //30000, //  Check Every 30 Seconds OR
					0, 	//100,   //  Check if device moves more than 500 meters
					this); 	
								
			startUsingLocation(); // Cache It!		
		}	
	}
	
	public void onStatusChanged(String provider, int status, Bundle extras) {
		//Log.d(TAG, "Provider Status Changed: " + provider + ", Extras=" + extras);
	}		
	
	public void ReturnLocation(Location location){
		
		if(location != null){
			if (currentLocation != null){
				currentLocation.setLatitude(location.getLatitude());
				currentLocation.setLongitude(location.getLongitude());
			}
			else
			{
				currentLocation = locationManager.getLastKnownLocation(this.getBestLocationProvider());	 
				currentLocation.setLatitude(location.getLatitude());
				currentLocation.setLongitude(location.getLongitude()); 
			}
		}
	}
		
	@Override
	protected void onDestroy() {
		 super.onDestroy();
		    
		// Stop the tracker when it is no longer needed.
		googleTracker.stopSession();
	}
}
