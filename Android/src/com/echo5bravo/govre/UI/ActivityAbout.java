package com.echo5bravo.govre.UI;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import com.echo5bravo.govre.INFO.Device;
import com.echo5bravo.govre.R;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ActivityAbout extends PreferenceActivity {
	
	private static final String TAG = ActivityAbout.class.getSimpleName();
	
	private GoogleAnalyticsTracker googleTracker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
		googleTracker.setCustomVar(CV_SLOT_1,			 										//SLOT (Can only track up to 5)
								   "Device Orientation", 										//NAME
								   Device.getDeviceOrientation(this.getApplicationContext()), 	//VALUE
								   1);															//SCOPE
		/*-------------------------------------------------------------------------------------------------
		NOTE: Add to Activity Handlers:
		 onResume():  googleTracker.trackPageView("/" + TAG);
		 onDestroy(): googleTracker.stopSession();
		-------------------------------------------------------------------------------------------------*/
		
		/* Some initializations */
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		ListView listView = new ListView(this); 
		listView.setId(android.R.id.list);
		listView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, 1));
		layout.addView(listView);

		this.setContentView(layout);
		/* Preferences time! (we build the preferences) */
		Preference version = getPreference("GoVRE", "Version 2.0.1.0", null);
		Preference author = getPreference("Author", "Jason Brannon - ECHO5BRAVO, LLC", null);
		Preference marketLink = getPreference("Developer",	"More about the GoVRE", new Intent(Intent.ACTION_VIEW, Uri.parse("http://echo5bravo.com")));
		
			
		//DialogPreference license = new MyDialogPreference(this, "License", "Go To: http://www.google.com/GoVRE License");
		
		Preference dedication = getPreference("Dedication", "This app is dedicated to my son, Blaine, who enjoys riding the VRE and always expresses his natural love for trains.", null);
		
		Preference acknowledgment1 = getPreference("Acknowledgment", "Kevin Waite - All the way from Scotland, providing great insight, source and knowledge on SensorEvents and LocationProviders.  \"Moran taing\"", new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fishbox-tales.org.uk/")));
		
		Preference acknowledgment2 = getPreference("Acknowledgment", "Scott Roth - Provided great feedback and quality assurance during the testing of version 2.x", null);

		/* Now we add the preferences to the preference screen */
		PreferenceScreen preferenceScreen = this.getPreferenceManager().createPreferenceScreen(this);
		addPreferenceCategory(preferenceScreen, "Preferences Tutorial",	version, author, marketLink, dedication, acknowledgment1, acknowledgment2);
		this.setPreferenceScreen(preferenceScreen);
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

	private boolean addPreferenceCategory(PreferenceScreen preferenceScreen,
			String titleCategory, Preference... preferences) {
		boolean addPreference = false;
		for (Preference preference : preferences) {
			if (preference != null)
				addPreference = true;
		}
		if (addPreference) {
			PreferenceCategory preferenceCategory = new PreferenceCategory(this);
			preferenceCategory.setTitle(titleCategory);
			preferenceScreen.addPreference(preferenceCategory);
			for (Preference preference : preferences) {
				if (preference != null)
					preferenceCategory.addPreference(preference);
			}
			return true;
		} else
			return false;
	}

	private Preference getPreference(String title, String summary, Intent intent) {
		Preference pref = new Preference(this);
		pref.setTitle(title);
		pref.setSummary(summary);
		if (intent != null)
			pref.setIntent(intent);
		return pref;
	}

	public class MyDialogPreference extends DialogPreference {
		public MyDialogPreference(Context context, String title, String text) {
			super(context, null);
			this.setTitle(title);
			this.setDialogMessage(text);
		}
	}
}
