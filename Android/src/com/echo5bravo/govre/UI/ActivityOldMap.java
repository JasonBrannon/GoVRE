package com.echo5bravo.govre.UI;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.echo5bravo.govre.INFO.Device;
import com.echo5bravo.govre.UTILS.ImageDownloader;
import com.echo5bravo.govre.UTILS.ProxyNetworkTrainMapImage;
import com.echo5bravo.govre.UTILS.ImageZoom.ImageViewTouch;
import com.echo5bravo.govre.R;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class ActivityOldMap extends Activity {
	
	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = ActivityOldMap.class.getSimpleName();
	
	private GoogleAnalyticsTracker googleTracker;
	
	private ScheduledExecutorService scheduleTaskExecutorVreMap;
	private ScheduledExecutorService scheduleTaskExecutorUiUpdates;
	static ImageViewTouch imgTrainMap;
	private String mapURL;
	private Context context;
	
	private ImageView imgTwitter, imgMap, imgAlerts, imgSched;  
	private ImageView spinner;
	private ImageView imgNoSignal;
	private AnimationDrawable spinnerAnim;
	private boolean spinnerOn;
	private boolean noSignalOn;
	private boolean isOnline;
	private Bitmap mBitmap;
		
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setTheme(R.style.Theme_BlueMoon);		
		this.setContentView(R.layout.old_train_map_layout);
		
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
	
		spinner = (ImageView) findViewById(R.id.splashSpinner);
		imgNoSignal  = (ImageView) findViewById(R.id.imgNoSignal);
		spinnerAnim = (AnimationDrawable) spinner.getBackground();
	
		context = this;	

		addListenerOnTwitterImage();  
        addListenerOnMapImage(); 
        addListenerOnAlert(); 
        addListenerOnSched(); 
	}	

	@Override
	public void onContentChanged()
	{
		super.onContentChanged();
		imgTrainMap = (ImageViewTouch)findViewById(R.id.imgTrainMap);
	}
	
	@Override
    protected void onStop(){ 
       super.onStop();
       
       scheduleTaskExecutorVreMap.shutdown(); 
       scheduleTaskExecutorUiUpdates.shutdown();
	}
	
	protected void onStart(){
		super.onStart();
		
		//Log.d(TAG, "OnStart Called");	
		
		scheduleTaskExecutorVreMap = Executors.newSingleThreadScheduledExecutor();

	    // This schedule a task to run every 59 seconds:
		scheduleTaskExecutorVreMap.scheduleAtFixedRate(new Runnable() {
	      public void run() {
	    	  
	    	  try{	    	  
		    	if (Device.isDeviceOnline(context.getApplicationContext())){
		    		spinnerOn = true;
		    		noSignalOn = false;
			    	  
				    //Get Alerts on separate thread (Requires Network) 
		    		// Update Map
		    		mapURL = ProxyNetworkTrainMapImage.fetchTrainImageLink(context);
		        	if (mapURL.length() > 0){
					    mBitmap = ImageDownloader.downloadBitmap(mapURL);
		        	}
		    	}
		    	else
		    	{
		    		spinnerOn = false;
		    		noSignalOn = true;
		    	}
	    	  }
	    	  catch(Exception ex)
	    	  {
	    		  ex.toString();
	    	  }

	        // Update the UI Thread
	        runOnUiThread(new Runnable() {
	          public void run() {	
	        	  
	        	  try{
	      		        		  
	        	  if (mBitmap != null){
	        		 imgTrainMap.setImageBitmapReset(mBitmap, 0, true);	
	        		 imgTrainMap.setVisibility(View.VISIBLE);
	        	  }
	        	  else
	        		 imgTrainMap.setVisibility(View.INVISIBLE);

	        	    spinnerOn = false;
	          }
	    	  catch(Exception ex)
	    	  {
	    		  ex.toString();
	    	  }
	          }
	        });
	      }
	    }, 0, 59, TimeUnit.SECONDS);
		
		scheduleTaskExecutorUiUpdates = Executors.newScheduledThreadPool(4);

	    // This schedule a task to run every 10 minutes:
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
	        		  imgTrainMap.setVisibility(View.INVISIBLE);   //Set to Invisible or Errors will occur		        		  
	        	  }	        		  
	        	  else
	        	  {
	        		  imgNoSignal.setVisibility(View.INVISIBLE);
	        		  imgTrainMap.setVisibility(View.VISIBLE);
	        	  }	        
	          }
	        });
	      }
	    }, 0, 500, TimeUnit.MILLISECONDS);	
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
	
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	     MenuInflater inflater = getMenuInflater();
	     inflater.inflate(R.menu.oldmapmenu, menu);
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
			Intent a = new Intent(ActivityOldMap.this, ActivityAbout.class);
			a.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(a);
		} else if (item.getItemId() == R.id.options) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "options",			//label
									 0);				//value
			Intent p = new Intent(ActivityOldMap.this, ActivityPreferences.class);
			p.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(p);
		} else if (item.getItemId() == R.id.googlemap) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "googlemap",		//label
									 0);				//value
			Intent o = new Intent(ActivityOldMap.this, ActivityGoogleMap.class);
			o.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(o);
		} else if (item.getItemId() == R.id.alerts) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "alerts",			//label
									 0);				//value
			Intent x = new Intent(ActivityOldMap.this, ActivityAlerts.class);
			x.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(x);
		} else if (item.getItemId() == R.id.schedules) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "schedules",		//label
									 0);				//value
			Intent e = new Intent(ActivityOldMap.this, ActivityStationSchedule.class);
			e.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(e);
		}
	    return true;
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
				Intent myIntent = new Intent(ActivityOldMap.this, ActivityTwitterFeed.class);
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
				Intent myIntent = new Intent(ActivityOldMap.this, ActivityGoogleMap.class);
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
				Intent myIntent = new Intent(ActivityOldMap.this, ActivityAlerts.class);
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
				Intent myIntent = new Intent(ActivityOldMap.this, ActivityStationSchedule.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);     			
			} 
		}); 
	}	
}

