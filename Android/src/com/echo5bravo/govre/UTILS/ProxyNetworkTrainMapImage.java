package com.echo5bravo.govre.UTILS;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.echo5bravo.govre.R;
import android.content.Context;

public class ProxyNetworkTrainMapImage {

	private static final String TAG = ProxyNetworkTrainMapImage.class.getSimpleName();
	static String imgLink;

	//CONSTRUCTORS
	public ProxyNetworkTrainMapImage(Context context) {			
			
	}
		
	//METHODS	
	public static String fetchTrainImageLink(Context context)
	{
		return fetchTrainImageUrlFromVRE(context);
	}
	
	//METHODS	
	private static String fetchTrainImageUrlFromVRE(Context context)
	{
		try {      
			
			String imgUrl = "";
			String url = context.getResources().getString(R.string.urlVREImgMap);			
        	
			Document doc = Jsoup.connect(url).get();
			
			//Focus on all tags with source attributes
			Elements media = doc.select("[src]");		
			
			for (Element src : media) {
				
				//Verify this is an image 
	            if (src.tagName().equals("img")){
	            	imgUrl = src.attr("abs:src");
	            
	                //Check if link contains the action query string, the map is the only image that will have it.
	            	if (imgUrl.contains("app?action=getimg")){
	            		return imgUrl;
	            	}	
	            }
	        }
	
			//Else Return Empty String
			return "";
			
			
		} catch (IOException e) {
			//e.printStackTrace();
		}
		
		return null;
		  
	}
}
