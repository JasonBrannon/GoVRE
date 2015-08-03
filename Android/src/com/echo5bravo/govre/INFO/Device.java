package com.echo5bravo.govre.INFO;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Device {
	
	 static ConnectivityManager connectivityManager;
	 static NetworkInfo wifiInfo;
	 static NetworkInfo mobileInfo;
	 
	 /**
	 * Check for <code>TYPE_WIFI</code> and <code>TYPE_MOBILE</code> connection using <code>isConnected()</code>
	 * Checks for generic Exceptions and writes them to logcat as <code>CheckConnectivity Exception</code>.
	 * Make sure AndroidManifest.xml has appropriate permissions.
	 * @param con Application context
	 * @return Boolean
	 */
	 public static boolean isDeviceOnline(Context con){  
		 
		ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;	   
   }
	 /*
	  * Returns Device Orientation (landscape, portrait, unknown)
	  */
	 public static String getDeviceOrientation(Context context){
		 
		 String orientation = "unknown";
		 switch(context.getResources().getConfiguration().orientation){
		 case Configuration.ORIENTATION_LANDSCAPE:
			 orientation = "landscape";
			 break;
		 case Configuration.ORIENTATION_PORTRAIT:
			 orientation = "portrait";
			 break;		 
		 }	
		 return orientation;
	 }
}
