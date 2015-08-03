package com.echo5bravo.govre.UI;


import java.util.Calendar;
import java.util.GregorianCalendar;
import com.echo5bravo.govre.ADAPTERS.ScheduleAdapter;
import com.echo5bravo.govre.INFO.Device;
import com.echo5bravo.govre.INFO.Station;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.echo5bravo.govre.R;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class ActivityStationSchedule extends TabActivity {
	
	private static final String TAG = ActivityStationSchedule.class.getSimpleName();
	private GoogleAnalyticsTracker googleTracker;
	
	private ListView myListView;
	private TextView txtStationName, txtAddress, txtAddress2;
	private ScheduleAdapter adapter;
	private String UserSelectedStationLines = "";
	private String UserSelectedStationId = "";
	private String UserSelectedStationName = "";
	private String UserSelectedStationZone = "";
	private String UserSelectedStationAddress = "";
	private String UserSelectedStationCity = "";
	private String UserSelectedStationState = "";
	private String UserSelectedStationZip = "";
	private Station station;
	
	private static final String PREFS_NAME = "OldPreferences";
	private static final String PREFS_DISPLAY_AMTRAK_KEY = "OldDisplayAmtrakKEY";
	private boolean mOldDisplayAmtrakValue;
	
	private TabHost mTabHost;

	private void setupTabHost() {
		mTabHost = getTabHost();
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BlueMoon);
        this.setContentView(R.layout.station_schedule_layout);
        
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
        
        //Load User's Time format preference
      	SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
      	String lineFilter = getPrefs.getString("listTrainLine", "FBG,ALL,MSS");
      	if (lineFilter.equals("BOTH")) //Regression Support for v1.0.0.4 for "BOTH"
      		lineFilter = "FBG,ALL,MSS";
       
        /*txtStationName = (TextView) findViewById(R.id.txtStationName);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        txtAddress2 = (TextView) findViewById(R.id.txtAddress2);*/
        
        Intent intent;                   // Reusable Intent for each tab

		setupTabHost();
		
		//LOAD BUNDLE VALUES			
		Bundle state = getIntent().getExtras();
		
		if (!(state==null)){
			UserSelectedStationId = state.getString("UserSelectedStationId");
			UserSelectedStationName = state.getString("UserSelectedStationName");
			UserSelectedStationLines = state.getString("UserSelectedStationLines");
			UserSelectedStationZone = state.getString("UserSelectedStationZone");
			UserSelectedStationAddress = state.getString("UserSelectedStationAddress");
			UserSelectedStationCity = state.getString("UserSelectedStationCity");
			UserSelectedStationState = state.getString("UserSelectedStationState");
			UserSelectedStationZip = state.getString("UserSelectedStationZip");
			
			//TAB 0
			intent = new Intent().setClass(this, ActivityTabStationSchedule.class);
			intent.putExtra("UserSelectedStationId", UserSelectedStationId);
			intent.putExtra("LINE", 2);		//Fredericksburg = 2 and Manassas = 4
			intent.putExtra("HEADING", 1);	//1 = NORTH 0 = SOUTH
			setupTab(new TextView(this), "FBG North", intent);	
					
			//TAB 1
			intent = new Intent().setClass(this, ActivityTabStationSchedule.class);
			intent.putExtra("UserSelectedStationId", UserSelectedStationId);
			intent.putExtra("LINE", 2);		//Fredericksburg = 2 and Manassas = 4
			intent.putExtra("HEADING", 0);	//1 = NORTH 0 = SOUTH
			setupTab(new TextView(this), "FBG South", intent);
			
			//TAB 2
			intent = new Intent().setClass(this, ActivityTabStationSchedule.class);
			intent.putExtra("UserSelectedStationId", UserSelectedStationId);
			intent.putExtra("LINE", 4);		//Fredericksburg = 2 and Manassas = 4
			intent.putExtra("HEADING", 1);	//1 = NORTH 0 = SOUTH
			setupTab(new TextView(this), "MSS North", intent);
			
			//TAB 3
			intent = new Intent().setClass(this, ActivityTabStationSchedule.class);
			intent.putExtra("UserSelectedStationId", UserSelectedStationId);
			intent.putExtra("LINE", 4);		//Fredericksburg = 2 and Manassas = 4
			intent.putExtra("HEADING", 0);	//1 = NORTH 0 = SOUTH
			setupTab(new TextView(this), "MSS South", intent);
			
			
			
			Calendar calendar = new GregorianCalendar(); 
			
			/* Determine the Train Line to Display
			 * 
			 */
			if (UserSelectedStationLines.equals("FBG")){
				/* Determine if the time of day is AM or PM
				 * If AM default to NORTH tab
				 * If PM default to SOUTH tab
				 */
				if (calendar.get(Calendar.AM_PM) == 0)
					mTabHost.setCurrentTab(0);		
				else
					mTabHost.setCurrentTab(1);
			}
			else if (UserSelectedStationLines.equals("MSS")){
				if (calendar.get(Calendar.AM_PM) == 0)
					mTabHost.setCurrentTab(2);		
				else
					mTabHost.setCurrentTab(3);
				
			}			
			else if (UserSelectedStationLines.equals("ALL")){			
				//User selected a station that supports both lines, 
				//look at preferences to see which line to display
				if (lineFilter.equals("FBG,ALL")){
					//Fredericksburg Line
					if (calendar.get(Calendar.AM_PM) == 0) 
						mTabHost.setCurrentTab(0);		
					else
						mTabHost.setCurrentTab(1);
				
				}
				else if (lineFilter.equals("MSS,ALL")){
					//Manassas Line
					if (calendar.get(Calendar.AM_PM) == 0)
						mTabHost.setCurrentTab(2);		
					else
						mTabHost.setCurrentTab(3);
				}
				else if (lineFilter.equals("FBG,ALL,MSS")){
					//Default to Mannassas Line
					if (calendar.get(Calendar.AM_PM) == 0)
						mTabHost.setCurrentTab(0);		
					else
						mTabHost.setCurrentTab(1);	
				}
			}	
		}
		else
		{
			//TAB 0
			intent = new Intent().setClass(this, ActivityTabStationSchedule.class);
			intent.putExtra("UserSelectedStationId", 9999);
			intent.putExtra("LINE", 2);		//Fredericksburg = 2 and Manassas = 4
			intent.putExtra("HEADING", 1);	//1 = NORTH 0 = SOUTH
			setupTab(new TextView(this), "FBG North", intent);	
					
			//TAB 1
			intent = new Intent().setClass(this, ActivityTabStationSchedule.class);
			intent.putExtra("UserSelectedStationId", 9999);
			intent.putExtra("LINE", 2);		//Fredericksburg = 2 and Manassas = 4
			intent.putExtra("HEADING", 0);	//1 = NORTH 0 = SOUTH
			setupTab(new TextView(this), "FBG South", intent);
			
			//TAB 2
			intent = new Intent().setClass(this, ActivityTabStationSchedule.class);
			intent.putExtra("UserSelectedStationId", 9999);
			intent.putExtra("LINE", 4);		//Fredericksburg = 2 and Manassas = 4
			intent.putExtra("HEADING", 1);	//1 = NORTH 0 = SOUTH
			setupTab(new TextView(this), "MSS North", intent);
			
			//TAB 3
			intent = new Intent().setClass(this, ActivityTabStationSchedule.class);
			intent.putExtra("UserSelectedStationId", 9999);
			intent.putExtra("LINE", 4);		//Fredericksburg = 2 and Manassas = 4
			intent.putExtra("HEADING", 0);	//1 = NORTH 0 = SOUTH
			setupTab(new TextView(this), "MSS South", intent);
		}
    }
	
	@Override
	protected void onResume(){
		super.onResume();
			
		//Track current Activity
		googleTracker.trackPageView("/" + TAG);
	}
		
	@Override
	protected void onDestroy() {
		 super.onDestroy();
		    
		// Stop the tracker when it is no longer needed.
		googleTracker.stopSession();
	}
	
	private void setupTab(final View view, final String tag, final Intent myintent) {
		View tabview = createTabView(mTabHost.getContext(), tag);
		
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(myintent);

		try{
		mTabHost.addTab(setContent);
		}
		catch(Exception ex)
		{
			ex.toString();
		}
	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
	
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	     MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.schedulemenu, menu);
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
			Intent a = new Intent(ActivityStationSchedule.this, ActivityAbout.class);
			a.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(a);
		} else if (item.getItemId() == R.id.options) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "options",			//label
									 0);				//value
			Intent p = new Intent(ActivityStationSchedule.this, ActivityPreferences.class);
			p.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(p);
		} else if (item.getItemId() == R.id.googlemap) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "googlemap",		//label
									 0);				//value
			Intent o = new Intent(ActivityStationSchedule.this, ActivityGoogleMap.class);
			o.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(o);
		} else if (item.getItemId() == R.id.alerts) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "alerts",			//label
									 0);				//value
			Intent x = new Intent(ActivityStationSchedule.this, ActivityAlerts.class);
			x.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(x);
		}
	    return true;
	}	
}
