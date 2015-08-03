package com.echo5bravo.govre.UI;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.echo5bravo.govre.ADAPTERS.ScheduleGridAdapter;
import com.echo5bravo.govre.BLL.BusinessCalDates;
import com.echo5bravo.govre.BLL.BusinessSchedule;
import com.echo5bravo.govre.BLL.BusinessStation;
import com.echo5bravo.govre.DAL.BusinessBaseCalDates;
import com.echo5bravo.govre.INFO.CalDates;
import com.echo5bravo.govre.INFO.Device;
import com.echo5bravo.govre.INFO.Schedule;
import com.echo5bravo.govre.INFO.Station;
import com.echo5bravo.govre.UTILS.Common;
import com.echo5bravo.govre.R;

public class ActivityTabStationSchedule extends Activity {
	
	private static final String TAG = ActivityTabStationSchedule.class.getSimpleName();  
	
	private String UserSelectedStationId = "";
	private Integer uLINE = 0;
	private Integer uHEADING = 0;
	
	private ScheduleGridAdapter mAdapter;
	private ArrayList<String> listScheduleItem;					//What you want the user to see
	private ArrayList<Integer> listScheduleIcon;				//Graphic you want the user to see
	private ArrayList<String> listHiddenDepartureTime;			//Clean Time of Departure
	private ArrayList<String> listScheduleHiddenToast;			//Test you want the user to Not see but Toast Message
	private ArrayList<String> listScheduleHiddenSelectedRow;	//Station Row Selected by User
	
	ArrayList<Schedule> mySchedule;
	ArrayList<CalDates> myCalDates;
	ArrayList<Station> myStationStops; 
	ArrayList<Schedule> currentTrains;
	ArrayList<Schedule> evalSchedule;
	ArrayList<Station> evalStations;
	String TodaysScheduleType;

	private GridView gridView;
	
	private ScheduledExecutorService scheduleTaskExecutorScheduleUpdate;
	private ScheduledExecutorService scheduleTaskExecutorUpdateSpinner;
	
	private Context context;
	private ImageView spinner;
	private AnimationDrawable spinnerAnim;
	private boolean spinnerOn;
	private ImageView imgNoSignal;
	private boolean noSignalOn;


	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BlueMoon);
        this.setContentView(R.layout.tab_station_schedules);
        
        context = this;
        
        spinner = (ImageView) findViewById(R.id.splashSpinner);
		spinner.setVisibility(View.INVISIBLE); //Added this to test Layout
		
		spinnerAnim = (AnimationDrawable) spinner.getBackground();
		imgNoSignal  = (ImageView) findViewById(R.id.imgNoSignal);
        
        //LOAD BUNDLE VALUES			
		Bundle state = getIntent().getExtras();
		UserSelectedStationId = state.getString("UserSelectedStationId");	
		uLINE = state.getInt("LINE");	
		uHEADING = state.getInt("HEADING");	
        
        // Set custom adapter to gridview
        gridView = (GridView) findViewById(R.id.gridView1);
    	     
    }	
	
	@Override
	protected void onStart(){
		super.onStart();
		
		//Log.d(TAG, "OnStart Called");
		
		scheduleTaskExecutorScheduleUpdate = Executors.newSingleThreadScheduledExecutor();

		    // This schedule a task to run every 59 seconds:
		scheduleTaskExecutorScheduleUpdate.scheduleAtFixedRate(new Runnable() {
		      public void run() {		    	  
		    		
		    	spinnerOn = true;		    	
		    		    	
		    	//Local Local Collection Variables
		    	//Get Alerts on separate thread (Requires Network) 
		    	
		    	if (Device.isDeviceOnline(context.getApplicationContext())){							
			
			       	noSignalOn = false;			    	
			    				    	
			    	//TODO: Add realtime info to GridView
			    	//doRefreshGTFSFeeds();	 
			    	
			    	//LoadVechicles();
			    	//LoadUpdates();					
				}
			   	else
			   	{			   		
			   		noSignalOn = true;
			   	} 
		    	
		    	
		    	LoadStationStops();	
		        LoadCalDates();
		    	TodaysScheduleType = Common.TodaysScheduleType(context, myCalDates, true);
		    	
		    	//Special Condition: Always display the "REGULAR" schedule 
		    	//on Holidays or Weekends so  users can plan 
		    	if (TodaysScheduleType.equals("NOSCHEDULEWKND") | TodaysScheduleType.equals("NOSCHEDULE"))
		    		TodaysScheduleType = "REGULAR";		    		
		    		
		    	LoadSchedule();
		    	BuildGrid();
	
		    	// Update the UI Thread
		        runOnUiThread(new Runnable() {
		          public void run() {
		            // Update ListView
		        	 updateGridView();  
		        	
		        	spinnerOn = false;
		          }
		        });
		      }
		    }, 0, 59, TimeUnit.SECONDS);		    
		    
			//scheduleTaskExecutorUpdateSpinner = Executors.newSingleThreadScheduledExecutor();
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
		        	
		        	if (noSignalOn){
		        	  //imgNoSignal.setVisibility(View.VISIBLE); 
		        	  imgNoSignal.setVisibility(View.INVISIBLE);
		        	}	        		  
		        	else
		        	{
		        	  imgNoSignal.setVisibility(View.INVISIBLE); 
		        	}
		        	
		            // Update ListView
		        	updateGridView(); 	
	  	          }
		        });
		      }			
			}, 0, 500, TimeUnit.MILLISECONDS);
			
	}
	
	private void LoadStationStops(){
		
		//Current Station Collection
		myStationStops = BusinessStation.getAllStations(this);		 
	}

	private void LoadSchedule(){
		
		//Current Schedule Collection
		mySchedule = BusinessSchedule.getAllSchedules(this, TodaysScheduleType, true);		
	}
	
	private void LoadCalDates(){
		
		//Current CalDates Collection
		//myCalDates = BusinessBaseCalDates.getAllCalDates(this);	
		myCalDates = BusinessCalDates.getAllCalDates(this);
	}
	
	/*private void LoadVechicles(){
		
		//Current Station Collection
		myVehicles = BusinessVehicle.getAllVehicles(context);
	}
	
	private void LoadUpdates(){
		
		//Current Station Collection
		myUpdates = BusinessUpdate.getAllUpdates(context);
	}*/
	
	private void updateGridView(){

		if (mySchedule != null && myCalDates != null && myStationStops != null){
			
			if (gridView.getAdapter() == null){
			
				// prepared arraylist and passed it to the Adapter class
		        mAdapter = new ScheduleGridAdapter(this, listScheduleItem, listScheduleIcon, listScheduleHiddenToast, listScheduleHiddenSelectedRow, listHiddenDepartureTime);
		       
		        gridView.setAdapter(mAdapter);
		        
	
		        // Implement On Item click listener
		        gridView.setOnItemClickListener(new OnItemClickListener()
		        {
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
						Toast.makeText(ActivityTabStationSchedule.this, mAdapter.getItem(position), Toast.LENGTH_SHORT).show();
					}
				});	
			}
			else 
			{
				gridView.invalidate();
				((ScheduleGridAdapter)gridView.getAdapter()).refill(this, listScheduleItem, listScheduleIcon, listScheduleHiddenToast, listScheduleHiddenSelectedRow, listHiddenDepartureTime);

			}
		}
	}
	
	private void BuildGrid(){
		
		listScheduleItem = new ArrayList<String>();
		listHiddenDepartureTime = new ArrayList<String>();
		listScheduleHiddenToast = new ArrayList<String>();
		listScheduleHiddenSelectedRow = new ArrayList<String>();
		listScheduleIcon = new ArrayList<Integer>();
		currentTrains = new ArrayList<Schedule>();
		
		int colCounter = 1;  
		int NORTH = uHEADING;	 //1 = NORTH 0 = SOUTH
		int LINE = uLINE;		 //Fredericksburg = 2 and Manassas = 4
		int rowCount = 1;
		int colCount = 1;
		int scheduleUpperBound = 0;  //Used to track the upperbound of our array
		
		String sImgStation = "";	
	    int imageStationResource;
	    
	    //ListItems to be bound to GridView		
		listScheduleItem.add("TRAIN");
		listHiddenDepartureTime.add("");
		listScheduleHiddenToast.add("Row of VRE Trains");
		listScheduleHiddenSelectedRow.add("false");
		listScheduleIcon.add(R.drawable.clear_spacer);
		
		boolean scheduleExists = false;
		boolean selectedStation = false;
		
		//Load User's Time format preference
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		String timeformat = getPrefs.getString("listTimeFormat", "12");
				
		try{		
	    	////////////////////////////////////////////////////////////////
			// STEP 1: CREATE HEADER ROW, DISPLAY ONLY THE TRAINS
			///////////////////////////////////////////////////////////////
	    	for (Station eachStop : myStationStops) {
	    		
	    		rowCount++;  //One Row fore every Station Stop
	    		
	    		for (Schedule eachSchedule : mySchedule) {
	    		
		    		if(eachSchedule.TRIP_DIRECTION_ID() == NORTH && 
		    		   eachSchedule.ROUTE_ID() == LINE && 
		    		   eachSchedule.SCHEDULE_STOP_ID().equals(eachStop.STATION_ID())){
		    			
		    			//If 1st Column, enter the default values		    			
						String sImgTrain = "drawable/map_train_green_" + eachSchedule.TRIP_SHORT_NAME().toLowerCase();	
					    int imageResource = getResources().getIdentifier(sImgTrain, null, getPackageName());
									    
					    listScheduleItem.add("M-F");  
					    listHiddenDepartureTime.add("");
					    listScheduleHiddenToast.add("Train Number: " + eachSchedule.TRIP_SHORT_NAME());
					    listScheduleHiddenSelectedRow.add("false");
		    			listScheduleIcon.add(imageResource);
		    			
		    			//Collect all Trains, this will be used to check the schedule in Step2
		    			currentTrains.add(eachSchedule); 
		    			
		    			colCount++; //One Column for every Train
		    		}	    			
	    		}	    			
    			break;	    		
	    	}	

	    	//IMPORTANT: Set Grid column count  Use colCount
	    	gridView.setNumColumns(colCount);	  
	    	
			////////////////////////////////////////////////////////////////
			// STEP 2: Create a collection of all Stations for the Line
	    	//         bing reported on.  The Stations should be FRED or MANASSAS
			///////////////////////////////////////////////////////////////
	    	evalStations = new ArrayList<Station>();
	    	
	    	for (Station eachStop : myStationStops) {
	    		
	    		for (Schedule eachSchedule : mySchedule) {
					
					if(eachSchedule.TRIP_DIRECTION_ID() == NORTH && 
							eachSchedule.ROUTE_ID() == LINE && 
							eachSchedule.SCHEDULE_STOP_ID().equals(eachStop.STATION_ID())){
						
						eachSchedule.STATION_ABBREVIATION(eachStop.ABBREVIATION());
						eachSchedule.STATION_STOP_NAME(eachStop.STATION_STOP_NAME());
						evalStations.add(eachStop);
						break;
					}
				}	    		
	    	}
	    	
			////////////////////////////////////////////////////////////////
			// STEP 3: From STEP 1, loop through all Stations for this Line
			///////////////////////////////////////////////////////////////
			for (Station eachStop : evalStations) {
				
				evalSchedule = new ArrayList<Schedule>();
			
				////////////////////////////////////////////////////////////////
				// STEP 4: From STEP 1, loop through all Schedules
				///////////////////////////////////////////////////////////////
				for (Schedule eachSchedule : mySchedule) {
				
					if(eachSchedule.TRIP_DIRECTION_ID() == NORTH && 
							eachSchedule.ROUTE_ID() == LINE && 
							eachSchedule.SCHEDULE_STOP_ID().equals(eachStop.STATION_ID())){
						
						eachSchedule.STATION_ABBREVIATION(eachStop.ABBREVIATION());
						eachSchedule.STATION_STOP_NAME(eachStop.STATION_STOP_NAME());
						evalSchedule.add(eachSchedule);
					}					
				}
				
				////////////////////////////////////////////////////////////////
				// STEP 5: Loop through All known Trains.  This is done because
				//         not all of the trains stop at all the stations. This
				//		   means that there will not be a schedule record to 
				//		   evaluate.  So we force the loop of current trains and
				// 		   if the train does not have a match in the inner loop
				//		   of schedules, then we know to skip that station and
				//         add an artificial filler
				//
				//EXAMPLES:  Fredericksburg NORTH Train 300 Skips:
				//			 Quantico
				//		     Rippon
				//			 Lorton
				//	         Springfield
				//
				//			 These positions in the grid will be substituted
				//			 with a "--" character.
				/////////////////////////////////////////////////////////////// 
					
				//Verify All trains are present for the current station schedule
				for (Schedule eachTrain : currentTrains) {
					
					if (eachStop.STATION_ID().toUpperCase().equals(UserSelectedStationId))	
						selectedStation = true;
					else
						selectedStation = false;
					
					for (Schedule eachSchedule : evalSchedule){ 
						
						if (eachSchedule.TRIP_SHORT_NAME().equals(eachTrain.TRIP_SHORT_NAME())){
							
							scheduleExists = true;
							
							//This is the 1st Column, Add the Station Abbreviation 1st, then the 1st Train
							if (colCounter == 1){
								
								//Create a nice Station Icon instead of Name
								sImgStation = "drawable/station_" + eachSchedule.SCHEDULE_STOP_ID().toLowerCase();	
							    imageStationResource = getResources().getIdentifier(sImgStation, null, getPackageName());
											    
							    listScheduleItem.add(""); 
							    listHiddenDepartureTime.add("");
							    listScheduleHiddenToast.add("Station: " + eachSchedule.STATION_STOP_NAME());
							    listScheduleHiddenSelectedRow.add(String.valueOf(selectedStation));
							    listScheduleIcon.add(imageStationResource);
								
								//All other columns enter the Departure Time
								listScheduleItem.add(Common.FormatTime(eachSchedule.SCHEDULE_DEPARTURE_TIME(), timeformat));    
								listHiddenDepartureTime.add(eachSchedule.SCHEDULE_DEPARTURE_TIME());
								listScheduleHiddenToast.add("Train: " + eachSchedule.TRIP_SHORT_NAME() + " Arrive/Departs " +  eachSchedule.STATION_STOP_NAME() + " @" + Common.FormatTime(eachSchedule.SCHEDULE_DEPARTURE_TIME(), timeformat));
								listScheduleHiddenSelectedRow.add(String.valueOf(selectedStation));
								listScheduleIcon.add(R.drawable.clear_spacer);
								colCounter++;
								
							}
							else
							{
								//All other columns enter the Departure Time
								listScheduleItem.add(Common.FormatTime(eachSchedule.SCHEDULE_DEPARTURE_TIME(), timeformat));  
								listHiddenDepartureTime.add(eachSchedule.SCHEDULE_DEPARTURE_TIME());
								listScheduleHiddenToast.add("Train: " + eachSchedule.TRIP_SHORT_NAME() + " Arrive/Departs " +  eachSchedule.STATION_STOP_NAME() + " @" + Common.FormatTime(eachSchedule.SCHEDULE_DEPARTURE_TIME(), timeformat));
								listScheduleHiddenSelectedRow.add(String.valueOf(selectedStation));
								listScheduleIcon.add(R.drawable.clear_spacer);
								colCounter++;
									
								if (colCounter==colCount)
									colCounter=1; //ResetCounter
							}						
						}												
					}
					
					//The Schedule didn't exist for the train, add a empty placeholder
					if (scheduleExists == false){						
						
							if (colCounter == 1){
								
								//Create a nice Station Icon instead of Name
								sImgStation = "drawable/station_" + eachStop.STATION_ID().toLowerCase();	
							    imageStationResource = getResources().getIdentifier(sImgStation, null, getPackageName());
											    
							    listScheduleItem.add(""); 
							    listHiddenDepartureTime.add("");
							    listScheduleHiddenToast.add("Station: " + eachStop.STATION_STOP_NAME());
							    listScheduleHiddenSelectedRow.add(String.valueOf(selectedStation));
				    			listScheduleIcon.add(imageStationResource);
								
								//All other columns enter the Departure Time
				    			listScheduleItem.add("--"); 
				    			listHiddenDepartureTime.add("");
				    			listScheduleHiddenToast.add("No Stop for " + eachTrain.TRIP_SHORT_NAME() + " at Station " + eachStop.STATION_STOP_NAME());
				    			listScheduleHiddenSelectedRow.add(String.valueOf(selectedStation));
								listScheduleIcon.add(R.drawable.clear_spacer);
								colCounter++;
							}
							else
							{
								//All other columns enter the Departure Time
								listScheduleItem.add("--"); 
								listHiddenDepartureTime.add("");
								listScheduleHiddenToast.add("No Stop for " + eachTrain.TRIP_SHORT_NAME() + " at Station " + eachStop.STATION_STOP_NAME());
								listScheduleHiddenSelectedRow.add(String.valueOf(selectedStation));
								listScheduleIcon.add(R.drawable.clear_spacer);
								colCounter++;
									
								if (colCounter==colCount)
									colCounter=1; //ResetCounter
							}
					}
					else
					{
						scheduleExists = false;  //Reset;
					}
				}
			}
		}
		catch(Exception ex)
		{
			ex.toString();
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
		if (UserSelectedStationId != null)		
			outState.putString("UserSelectedStationId", UserSelectedStationId.toString());
		
		if (uLINE != null)
			outState.putInt("LINE", uLINE);
		
		if (uHEADING != null)		
			outState.putInt("HEADING", uHEADING);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  
	  // Restore UI state from the savedInstanceState.
	  // This bundle has also been passed to onCreate.	  	  
	  UserSelectedStationId = savedInstanceState.getString("UserSelectedStationId");
	  uLINE = savedInstanceState.getInt("LINE");
	  uHEADING = savedInstanceState.getInt("HEADING");
	}
	
	/*
	 *  Register for the updates when Activity is in foreground 
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		/* When Activity restored, fetch the current mOldTrainLineFilterValue
	    * this is used in the updateStationList() method to toggle between setting
	    * a New instance of an adapter vs updating the current adapter in the 
	    * event the user changes their Train Line preference.
	    * 
	    * Using the Editor object by the "onStop()" event to make the preference 
	    * change, this will maintain state of our value even if the app is killed
	    */
	    
	}
	
	@Override
    protected void onStop(){
       super.onStop();

      /* Before the Activity stops, save the current mOldTrainLineFilterValue
       * this is used in the updateStationList() method to toggle between setting
       * a New instance of an adapter vs updating the current adapter in the 
       * event the user changes their Train Line preference.
       * 
       * Using the Editor object to make the preference change, this will maintain
       * state of our value even if the app is killed
       * */
       
       scheduleTaskExecutorScheduleUpdate.shutdown();
       scheduleTaskExecutorUpdateSpinner.shutdown();
       
      //Stop Progress Spinner
      spinnerAnim.stop();
 	  spinner.setVisibility(View.INVISIBLE);

    }
}


