package com.echo5bravo.govre.UI;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.echo5bravo.govre.INFO.Device;
import com.echo5bravo.govre.UTILS.ProxyNetworkAlerts;
import com.echo5bravo.govre.R;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class ActivityAlerts extends Activity {
	
	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = ActivityAlerts.class.getSimpleName();
	
	private GoogleAnalyticsTracker googleTracker;
	
	private static TextView txtAlerts;
	private static TextView txtE5BAlerts;
	private String sAlertMessage;
	private String sE5BAlertMessage;
	
	
	private ScheduledExecutorService scheduleTaskExecutorVreAlerts;
	private ScheduledExecutorService scheduleTaskExecutorUiUpdates;
	private ScheduledExecutorService scheduleTaskExecutorE5BAlerts;
	private ScheduledExecutorService scheduleTaskExecutorE5BUiUpdates;
	private Context context;
	
	private ImageView imgTwitter, imgMap, imgSched;  
	private ImageView spinner;
	private ImageView spinner2;
	private AnimationDrawable spinnerAnim;
	private AnimationDrawable spinnerAnim2;
	private boolean spinnerOn;
	private boolean spinnerOn2;
	private ImageView imgNoSignal;
	private ImageView imgNoSignal2;  
	private boolean noSignalOn;
	private boolean noSignalOn2;
	
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);	
		setTheme(R.style.Theme_BlueMoon);
		
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
		
		this.setContentView(R.layout.alerts_layout);
		txtAlerts = (TextView) findViewById(R.id.txtAlertMessage);
		txtAlerts.setMovementMethod(new ScrollingMovementMethod());
		txtE5BAlerts = (TextView) findViewById(R.id.txtE5BAlertMessage);
		txtE5BAlerts.setMovementMethod(new ScrollingMovementMethod());
		
		spinner = (ImageView) findViewById(R.id.splashSpinner);
		spinnerAnim = (AnimationDrawable) spinner.getBackground();	
		spinner2 = (ImageView) findViewById(R.id.splashSpinner2);
		spinnerAnim2 = (AnimationDrawable) spinner2.getBackground();	
		imgNoSignal  = (ImageView) findViewById(R.id.imgNoSignal);
		imgNoSignal2  = (ImageView) findViewById(R.id.imgNoSignal2);
		
		context = this;		
		
		addListenerOnTwitterImage();  
        addListenerOnMapImage(); 
        addListenerOnSchedImage(); 
	}
	
	@Override
    protected void onStop(){
       super.onStop();
       
       scheduleTaskExecutorVreAlerts.shutdown();      
       scheduleTaskExecutorUiUpdates.shutdown();
       scheduleTaskExecutorE5BAlerts.shutdown();
       scheduleTaskExecutorE5BUiUpdates.shutdown();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		
		//Log.d(TAG, "OnStart Called");	
		
		scheduleTaskExecutorVreAlerts = Executors.newSingleThreadScheduledExecutor();

	    // This schedule a task to run every 10 minutes:
		scheduleTaskExecutorVreAlerts.scheduleAtFixedRate(new Runnable() {	    	
	      public void run() {
	    	
	    	try{	    	  
			   	if (Device.isDeviceOnline(context.getApplicationContext())){
				   	spinnerOn = true;
				   	noSignalOn = false;
				    	
				   	//Get Alerts on separate thread (Requires Network) 
				   	sAlertMessage = ProxyNetworkAlerts.fetchAlertDump(context);
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
	        	  
	        	  txtAlerts.setText(sAlertMessage);	
	        	  
	        	  spinnerOn = false;
	        	 
	        	   //Log.w(TAG, "SPINNER STOPPED FROM runOnUiThread");
	          }
	        });
	      }
	    }, 0, 60, TimeUnit.SECONDS);
		
		scheduleTaskExecutorE5BAlerts = Executors.newSingleThreadScheduledExecutor();

	    // This schedule a task to run every 10 minutes:
		scheduleTaskExecutorE5BAlerts.scheduleAtFixedRate(new Runnable() {	    	
	      public void run() {
	    	
	    	try{	    	  
			   	if (Device.isDeviceOnline(context.getApplicationContext())){
				   	spinnerOn2 = true;
				   	noSignalOn2 = false;
				    	
				   	//Get Alerts on separate thread (Requires Network)
				   	sE5BAlertMessage = ProxyNetworkAlerts.fetchEcho5BravoAlert(context);  
			    }
			   	else
			   	{
			   		spinnerOn2 = false;
			   		noSignalOn2 = true;
			   	}
		    }
		    catch(Exception ex)
		    {
		    		  ex.toString();
		    }

	        // Update the UI Thread
	        runOnUiThread(new Runnable() {
	          public void run() {
	        	  
	        	  txtE5BAlerts.setText(sE5BAlertMessage);
				  Linkify.addLinks(txtE5BAlerts, Linkify.ALL);
	        	  
	        	  spinnerOn2 = false;
	        	 
	        	   //Log.w(TAG, "SPINNER STOPPED FROM runOnUiThread");
	          }
	        });
	      }
	    }, 0, 60, TimeUnit.SECONDS);
		
		
		scheduleTaskExecutorUiUpdates = Executors.newSingleThreadScheduledExecutor();

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
	        	  }	        		  
	        	  else
	        	  {
	        		  imgNoSignal.setVisibility(View.INVISIBLE);	        		
	        	  }
	          }
	        });
	      }
	    }, 0, 500, TimeUnit.MILLISECONDS);	
		
		
		scheduleTaskExecutorE5BUiUpdates = Executors.newSingleThreadScheduledExecutor();

	    // This schedule a task to run every 10 minutes:
		scheduleTaskExecutorE5BUiUpdates.scheduleAtFixedRate(new Runnable() {	    	
	      public void run() {
	    	
	    	
	        // Update the UI Thread
	        runOnUiThread(new Runnable() {
	          public void run() {          
	        	  
	        	  if (spinnerOn2){
	        		  spinnerAnim2.start();
	        		  spinner2.setVisibility(View.VISIBLE);
	        	  }	        		  
	        	  else
	        	  {
	        		  spinnerAnim2.stop();
	        		  spinner2.setVisibility(View.INVISIBLE);
	        	  }
	        	  
	        	  if (noSignalOn2){	        		
	        		  imgNoSignal2.setVisibility(View.VISIBLE);
	        	  }	        		  
	        	  else
	        	  {	        		
	        		  imgNoSignal2.setVisibility(View.INVISIBLE);
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
	   inflater.inflate(R.menu.alertsmenu, menu);
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
			Intent a = new Intent(ActivityAlerts.this, ActivityAbout.class);
			a.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(a);
		} else if (item.getItemId() == R.id.options) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "options",			//label
									 0);				//value
			Intent p = new Intent(ActivityAlerts.this, ActivityPreferences.class);
			p.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(p);
		} else if (item.getItemId() == R.id.googlemap) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "googlemap",		//label
									 0);				//value
			Intent o = new Intent(ActivityAlerts.this, ActivityGoogleMap.class);
			o.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(o);
		} else if (item.getItemId() == R.id.schedules) {
			//Track GoogleAnalytics Event
			googleTracker.trackEvent("ui_interaction",	//category
									 "from_menu",		//action
									 "schedules",		//label
									 0);				//value
			Intent e = new Intent(ActivityAlerts.this, ActivityStationSchedule.class);
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
				Intent myIntent = new Intent(ActivityAlerts.this, ActivityTwitterFeed.class);
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
				Intent myIntent = new Intent(ActivityAlerts.this, ActivityGoogleMap.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);     			
			} 
		}); 
	}
	
	private void addListenerOnSchedImage() {
		 
		imgSched = (ImageView) findViewById(R.id.imgSched); 

		imgSched.setOnClickListener(new View.OnClickListener() {
 
			public void onClick(View arg0) {
				//Track GoogleAnalytics Event
				googleTracker.trackEvent("ui_interaction",		//category
					        			 "from_icon",			//action
					        			 "schedule",			//label
					        			 0);					//value
				//Do Something
				Intent myIntent = new Intent(ActivityAlerts.this, ActivityStationSchedule.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(myIntent);     			
			} 
		}); 
	}
}

