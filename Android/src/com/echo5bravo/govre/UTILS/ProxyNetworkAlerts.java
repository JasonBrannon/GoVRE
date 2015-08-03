package com.echo5bravo.govre.UTILS;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import android.content.Context;
import com.echo5bravo.govre.INFO.Alert;
import com.echo5bravo.govre.R;

public class ProxyNetworkAlerts {
	
	// LOCAL VARIABLES AND PROPERTIES
	private static final String TAG = ProxyNetworkAlerts.class.getSimpleName();
	
	static Alert myAlert;

	//CONSTRUCTORS
	public ProxyNetworkAlerts(Context context) {			
			
	}
		
	//METHODS	
	public static ArrayList<Alert> fetchAlerts(Context context)
	{
		return fetchAlertsFromVRE(context);
	}
	
	public static String fetchAlertDump(Context context)
	{
		String sAlerts = "";
		sAlerts = GetAlertDump(context);
		if (sAlerts == "" || sAlerts == null)
			return "No LIVE alerts at this time.";
		else
			return sAlerts; 
	}
	
	//METHODS	
	public static String fetchEcho5BravoAlert(Context context){
		
		String sAlertDump = "";
		String sHTML = "";
		
		try{			
			String sAlertVal = "";
			
			String versionName = context.getResources().getString(R.string.app_versionName); 
			versionName = versionName.replace(".", "_");
			//URL jsonurl = new URL(context.getResources().getString(R.string.urlEcho5BravoAlerts) + versionName);
			
			//URLConnection transport = jsonurl.openConnection();
			
			URL urlUse = new URL(context.getResources().getString(R.string.urlEcho5BravoAlerts) + versionName);
	        HttpURLConnection conn = null;
	        conn = (HttpURLConnection) urlUse.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Content-length", "0");
	        conn.setUseCaches(false);
	        conn.setAllowUserInteraction(false);
	        conn.setConnectTimeout(8000);	//Set the connection timeout to 5 seconds
	        conn.setReadTimeout(10000);		//Set the read timeout to 6 seconds
	        conn.connect();
	        
	        int status = conn.getResponseCode();
	        
	        // use the stream...
	        switch (status) {
	            case 200:
	            case 201: 	 
	            	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream())); // Buffered
	            	
	            	
	    			String line;
	    			while ((line = in.readLine()) != null)
	    			{
	    				JSONObject obj = new JSONObject(line);
	    				sHTML = obj.getString("Message");			
	    			}
	    			
	    			//Parse Message To Display Correctly
	    			Document doc = Jsoup.parse(sHTML);
	    			
	    			Elements ils = doc.select("il");  //using <br> as delimiter
	    			for (Element il : ils){
	      				
	    				sAlertDump += il.text() + "\n"; 			   
	    			}			
	    			
	    			conn.disconnect();
	    			return sAlertDump;
	            default:
	            	return null;
	        }
			
						
		}
		catch(Exception e)
		{
			sAlertDump = e.toString();
			return sAlertDump;
		}
	}
	
	@SuppressWarnings("finally")
	private static ArrayList<Alert> fetchAlertsFromVRE(Context context)
	{
		//Add <ArrayList> here to build out message and give user train status updates
		ArrayList<Alert> inView = new ArrayList<Alert>();
		
		try {        	
			
			String url = context.getResources().getString(R.string.urlVREAlers);
        	
			Document doc = Jsoup.connect(url).get();
						
			//Log.w(TAG, "WEB CALL TO VRE: fetchAlertsFromVRE");
			
			Elements divs = doc.select("table:contains(Train Number)");		
			
			
			String printS = "";		
			
			for (org.jsoup.nodes.Element div : divs){ 
			  				
				printS += div.text() + " "; 			   
			}	
			
			String cleanText = printS.replace("Train Number Status ", " ");
			
			String phrase = cleanText;
			String delims = "[ ]+";
			String[] sStatus = phrase.split(delims);
			
					
			printS = ""; //reset			
					
			
			
			String sTrainNum = "";				//VRE Train Number
			String sMessage = "";				//Alert or Message
			int StartLength = sStatus.length;	//Used to track the end of the array
			
			
			/*  
			 *Loop through the Alerts Array, the logic needs to separate the Train Number from the Message
			 *Then if the Message contains any key words like "Delay, Delayed, Cancelled" capture the 
			 *information along with any time (minutes) of a delay.
			*/
			for(int i = 1;i< sStatus.length; i+=1){
				
				/*Skip empty strings, there may be some leading whitespace in the array*/
				if (sStatus[i].length() > 0){
					
					/*Check if the value is a VRE train number, '
					 * there may be minutes in the string */
					if (isVRETrainNumber(sStatus[i])){
						
						/*Used to capture the previous loops information, last loop gets captured below*/
						if (sTrainNum.length() > 0 && sMessage.length() > 0){
							myAlert = new Alert();		
							/*Train Number*/
							myAlert.setALERT_TRAIN_NUM(sTrainNum.trim());
							/*Raw Alert*/
							myAlert.setALERT_MESSAGE(sMessage.trim());	
							
							/*Detect if there is a Delay in the Alert, if so extract the delay time
							 * So far, VRE appears to post in minutes, even if it's 200. */
							if (isDelayed(sMessage)){
								myAlert.setALERT_TRAIN_DELAYED(true);	
								myAlert.setALERT_MINUTES_DELAY(delayedHowLong(sMessage));
							}
							else{
								myAlert.setALERT_TRAIN_DELAYED(false);	
								myAlert.setALERT_MINUTES_DELAY("0");	
							}	
							
							/*Detect if the train is cancelled, I saw this only once on 1June2011*/
							if (isCancelled(sMessage))
								myAlert.setALERT_TRAIN_CANCELLED(true);	
							else
								myAlert.setALERT_TRAIN_CANCELLED(false);	
								
							inView.add(myAlert);
							
							sMessage = "";
						}
						
						/* Capture the train number */
						sTrainNum = sStatus[i];
						StartLength -=1;
						
					}
					else
					{
						/* Capture the alert */
						sMessage += sStatus[i] + " ";
						StartLength -=1;
						
						/* StartLength will == 1 when the last value of the array is reached
						 * if == 1 save the last record, because the for loop is ready to exit */
						if (StartLength == 1){
							myAlert = new Alert();		
							/*Train Number*/
							myAlert.setALERT_TRAIN_NUM(sTrainNum);
							/*Raw Alert*/
							myAlert.setALERT_MESSAGE(sMessage);	
							
							/*Detect if there is a Delay in the Alert, if so extract the delay time
							 * So far, VRE appears to post in minutes, even if it's 200. */
							if (isDelayed(sMessage)){
								myAlert.setALERT_TRAIN_DELAYED(true);	
								myAlert.setALERT_MINUTES_DELAY(delayedHowLong(sMessage));
							}
							else{
								myAlert.setALERT_TRAIN_DELAYED(false);	
								myAlert.setALERT_MINUTES_DELAY("0");	
							}
							
							/*Detect if the train is cancelled, I saw this only once on 1June2011*/
							if (isCancelled(sMessage))
								myAlert.setALERT_TRAIN_CANCELLED(true);	
							else
								myAlert.setALERT_TRAIN_CANCELLED(false);									
								
							inView.add(myAlert);
						}
					}
				}			
			}			
			
			return inView;
			
		} catch (Exception ex){
			ex.toString();
			//e.printStackTrace();
		}finally
		{
			return inView;
		}
				  
	}
	
	private static String GetAlertDump(Context context){
		
		String sAlertDump = "";
		
		try{
			
			String url = context.getResources().getString(R.string.urlVREAlers);
			
			Document doc = Jsoup.connect(url).get();
			
			//Log.w(TAG, "WEB CALL TO VRE: GetAlertDump");
			
			//Elements divs = doc.select("table[border]");
			Elements divs = doc.select("table:contains(Train Number)");		
			
			
			
			//String   tmpString = myString.replace( '\'', '*' );		
			
			
			for (org.jsoup.nodes.Element div : divs){
			  				
				sAlertDump += div.text() + " "; 			   
			}	
			
			String cleanText = sAlertDump.replace("Train Number Status ", ""); 
			
			String phrase = cleanText;
			String delims = "[ ]+";
			String[] sStatus = phrase.split(delims);
			
			String sAlertVal = ""; //reset
			
			String sTrain = "";
			String sAlert = "";
			
			int counter = 0;
			//Add <ArrayList> here to build out message and give user train status updates
			for (String val : sStatus){
				
				//Check for train number (always a 3 digit number)
				if (isInteger(val) && val.length() == 3 && isVRETrainNumber(val))
				{
					if (counter != 0)
						sAlertVal += System.getProperty("line.separator");
						
					sAlertVal += "Train: " + val; // + System.getProperty("line.separator");	
					
				}
				else
				{			
					if (val.length() > 0)
						sAlertVal += " " + val;
				}				
				counter++;				
			}
			
			return sAlertVal;
			
		}
		catch(Exception e)
		{
			return null;			
		}		
	}
	
	public static boolean isInteger(String input)  
    {  
       try  
       {  
          Integer.parseInt(input);   
          return true;  
       }  
       catch(Exception e)  
       {  
          return false;  
       }  
    } 
	
	/* Check if the Alert is a Delay/Delayed notification */
	public static boolean isDelayed(String input)  
    {  
		if (input.indexOf("Delayed" ) > -1 || input.indexOf("Delay" ) > -1) {
			  return true;
		} else {
			  return false;
		}  
    }
	
	/* Check if the Alert is a Cancelled notification */
	public static boolean isCancelled(String input)  
    {  
		if (input.indexOf("Cancelled" ) > -1) {
			  return true;
		} else {
			  return false;
		} 
    }
	
	/* Returns the number of minutes found within a delay message */
	public static String delayedHowLong(String input)  
    {  
		/*RegEx Deletes all non-digits */
		String eval = input.replaceAll("\\D+","");
		if (eval == "") {
			  return "0";
		} else {
			  return eval;
		} 
    }
	
	/*VRE trains are always 300+, had a problem where there was a delay
	 * of 105 minutes and the logic was interpreting 105 as a train number
	 * this will break if the delay in minutes exceeds 299.
	 */
	public static boolean isVRETrainNumber(String input)  
    {  
       try  
       {  
          int trainCheck = Integer.parseInt(input);   
          
          if (trainCheck >= 300)
        	  return true; 
          else
        	  return false;
       }  
       catch(Exception e)  
       {  
          return false;  
       }  
    } 
}
