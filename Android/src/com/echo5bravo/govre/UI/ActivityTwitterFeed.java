package com.echo5bravo.govre.UI;


import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.echo5bravo.govre.ADAPTERS.TwitterAdapter;
import com.echo5bravo.govre.INFO.Device;
import com.echo5bravo.govre.INFO.Tweet;
import com.echo5bravo.govre.UTILS.ProxyNetworkTwitter;
import com.echo5bravo.govre.R;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

public class ActivityTwitterFeed extends Activity {
	
	private static final String TAG = ActivityTwitterFeed.class.getSimpleName();
	private GoogleAnalyticsTracker googleTracker;

	
	private ArrayList<Tweet> tweets;
	private ListView myListView;
	private TwitterAdapter adapter;
	private Context context;
	
	private String tweetFilter = "";
	private String tweetCount;
	
	private ScheduledExecutorService scheduleTaskExecutorTwitterAlerts;
	private ScheduledExecutorService scheduleTaskExecutorUiUpdates;
	
	private static final String PREFS_NAME = "OldTweetPreferences";
	private static final String PREFS_TWEET_FOLLOW = "OldTweetFollow";
	private static final String PREFS_TWEET_COUNT = "OldTweetCount";
	private String mOldTweetFollowValue;
	private String mOldTweetCountValue;

	private ImageView imgMap, imgAlerts, imgVRE, imgSched;  
	private ImageView spinner;
	private AnimationDrawable spinnerAnim;
	private boolean spinnerOn;
	private ImageView imgNoSignal;
	private boolean noSignalOn;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_BlueMoon);
        this.setContentView(R.layout.twitter_layout);
        
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
        
        myListView = (ListView) findViewById(R.id.TwitterListView); 
        spinner = (ImageView) findViewById(R.id.splashSpinner);
		spinnerAnim = (AnimationDrawable) spinner.getBackground();
		imgNoSignal  = (ImageView) findViewById(R.id.imgNoSignal);
        
		context = this;	
		
        addListenerOnMapImage(); 
        addListenerOnAlert();
        addListenerOnSched();
    }	
	
	@Override
    protected void onStop(){
       super.onStop();
       
       SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
       SharedPreferences.Editor editor = settings.edit();
       editor.putString(PREFS_TWEET_FOLLOW, mOldTweetFollowValue);
       editor.putString(PREFS_TWEET_COUNT, mOldTweetCountValue);  
       
       // Commit the edits!
       editor.commit();
       
       scheduleTaskExecutorTwitterAlerts.shutdown();
       scheduleTaskExecutorUiUpdates.shutdown();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		//Track current Activity
		googleTracker.trackPageView("/" + TAG);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		mOldTweetFollowValue = settings.getString(PREFS_TWEET_FOLLOW, "");	
		mOldTweetCountValue = settings.getString(PREFS_TWEET_COUNT, "");
		
		 /* Pull user's preferred default train line */
		SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		
		try{
		tweetFilter = getPrefs.getString("listTweetFollow", "@VaRailXpress");  //Default to All VRE Tweets
		tweetCount = getPrefs.getString("listTweetDisplayCount", "15");  //Default to 15 Tweets
		}
		catch(Exception ex)
		{
			Log.e(TAG, ex.toString());
		}
			
		
		scheduleTaskExecutorTwitterAlerts = Executors.newSingleThreadScheduledExecutor();

	    // This schedule a task to run every 10 minutes:
		scheduleTaskExecutorTwitterAlerts.scheduleAtFixedRate(new Runnable() {	    	
	      public void run() {
	    	
	    	try{	    	  
				if (Device.isDeviceOnline(context.getApplicationContext())){
					
					//onResume, clear collection and notify the listview because the information
					//bound to the adapter may have changed (i.e., the user changes a Tweet preference
					//adapter.clear();
					
			
			    	spinnerOn = true;
			    	noSignalOn = false;
			    	
			    				    	
			    	if (tweetFilter.equals("VRE"))
						tweetFilter = "from:VaRailXpress";
					else
						tweetFilter = "VaRailXpress";
				   
						   
					 //Get Twitter Tweets on separate thread (Requires Network) 	
					   
					 //More on Twitter API queries: https://dev.twitter.com/docs/using-search
					 //API Info: https://dev.twitter.com/docs/api/1/get/search
					   
					 //Twitters To VRE  
					 //ArrayList<Tweet> tweets = getTweets("to:VaRailXpress", 1);
					 
					 //Twitters From  VRE  
					 //ArrayList<Tweet> tweets = getTweets("from:VaRailXpress", 1);
					 
					 //Twitters Mentioning VaRailXpress 
					 //ArrayList<Tweet> tweets = getTweets("VaRailXpress", 1);
					 
					 //Twitters By User Android Preference		    	
			    		
					   
					int icount = Integer.parseInt(tweetCount);
					tweets = ProxyNetworkTwitter.getTweets(tweetFilter, icount, context);
					
				}
			   	else
			   	{
			   		spinnerOn = false;
			   		noSignalOn = true;
			   	}
			
			}
			catch(Exception ex)
			{
				//Log.e(TAG, ex.toString());
			}
				

	        // Update the UI Thread
	        runOnUiThread(new Runnable() {
	          public void run() {   
	        	 
	        	 if (tweets == null){	        	
	        		//Do Nothing
	        	 }
	        	 else
	        	 {
	        		 updateTweets(tweets);
	        	 }
	     
	        	 spinnerOn = false;
	        	 
	          }
	        });
	      }
	    }, 0, 59, TimeUnit.SECONDS);
		
		
		scheduleTaskExecutorUiUpdates = Executors.newSingleThreadScheduledExecutor();

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
	    }, 0, 500, TimeUnit.MILLISECONDS);	
	}
	
	
	/*
	 *  Rebuilds the Tweets on UIThread
	 */
	public void updateTweets(ArrayList<Tweet> tweets){	
		
		if (myListView.getAdapter() == null || (tweetFilter != mOldTweetFollowValue) || (mOldTweetCountValue != tweetCount)) {			
			
			adapter = new TwitterAdapter(this, R.layout.twitter_listitem, tweets);
			myListView.setAdapter(adapter);
			
			mOldTweetFollowValue = tweetFilter;
			mOldTweetCountValue = tweetCount;
				
		} else {
					
			adapter.notifyDataSetChanged();					
		}		
    }
	
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	     MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.twittermenu, menu);
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
			Intent a = new Intent(ActivityTwitterFeed.this, ActivityAbout.class);
			a.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(a);
		} else if (item.getItemId() == R.id.options) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "options",			//label
									 0);				//value
			Intent p = new Intent(ActivityTwitterFeed.this, ActivityPreferences.class);
			p.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(p);
		} else if (item.getItemId() == R.id.googlemap) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "googlemap",		//label
									 0);				//value
			Intent o = new Intent(ActivityTwitterFeed.this, ActivityGoogleMap.class);
			o.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(o);
		} else if (item.getItemId() == R.id.alerts) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "alerts",			//label
									 0);				//value
			Intent x = new Intent(ActivityTwitterFeed.this, ActivityAlerts.class);
			x.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(x);
		} else if (item.getItemId() == R.id.schedules) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "schedules",		//label
									 0);				//value
			Intent e = new Intent(ActivityTwitterFeed.this, ActivityStationSchedule.class);
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
				Intent myIntent = new Intent(ActivityTwitterFeed.this, ActivityAlerts.class);
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
				Intent myIntent = new Intent(ActivityTwitterFeed.this, ActivityGoogleMap.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);     			
			} 
		}); 
	}
	
	private void addListenerOnSched() {
		 
		imgSched = (ImageView) findViewById(R.id.imgSched); 

		imgSched.setOnClickListener(new View.OnClickListener() {
 
			public void onClick(View arg0) {
				//Track GoogleAnalytics Event
				googleTracker.trackEvent("ui_interaction",		//category
					        			 "from_icon",			//action
					        			 "schedule",			//label
					        			 0);					//value
				
				//Do Something
				Intent myIntent = new Intent(ActivityTwitterFeed.this, ActivityStationSchedule.class);
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
