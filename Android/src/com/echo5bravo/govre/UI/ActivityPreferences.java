package com.echo5bravo.govre.UI;

import com.echo5bravo.govre.INFO.Device;
import com.echo5bravo.govre.R;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ActivityPreferences extends PreferenceActivity {
	
	private static final String TAG = ActivityPreferences.class.getSimpleName();
	private GoogleAnalyticsTracker googleTracker;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	addPreferencesFromResource(R.xml.preferences);
    	
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
}