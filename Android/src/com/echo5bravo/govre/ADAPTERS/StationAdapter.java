package com.echo5bravo.govre.ADAPTERS;

//GoVRE** = Indicates Original GoVRE Code

import com.echo5bravo.govre.INFO.Station;
//GoVRE** = Indicates Original GoVRE Codeimport com.echo5bravo.govre.R;
import com.echo5bravo.govre.UTILS.NeedleView;
import com.echo5bravo.govre.UTILS.SquareView;
import com.echo5bravo.govre.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/*
 * This Adapter allows ListViews to display an array of Station objects.
 */
public class StationAdapter extends ArrayAdapter<Station>{

    private Context context; 
    private int layoutResourceId;    
    private Station data[] = null;
    private int heading;
    
    public StationAdapter(Context context, int layoutResourceId, Station[] data, int bearing) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.heading = bearing;
    }    
  
	public void refill(Station[] data, int bearing) {
    	this.data = data;
        this.heading = bearing;
        notifyDataSetChanged();
    }   

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StationHolder holder = null;
        
        if (row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new StationHolder();
            
            holder.txtName = (TextView)row.findViewById(R.id.txtName);            
            holder.txtAlert = (TextView)row.findViewById(R.id.txtAlert);
            holder.txtStatus = (TextView)row.findViewById(R.id.txtStatus);
            holder.square = (SquareView)row.findViewById(R.id.square);
            holder.needle = (NeedleView)row.findViewById(R.id.needle);  
            holder.txtDistance = (TextView)row.findViewById(R.id.txtDistance);
           
            row.setTag(holder);
        }
        else
        {
            holder = (StationHolder)row.getTag();
        }
        
        Station station = data[position];
        holder.txtName.setText(station.STATION_STOP_NAME());
                
        // We need to calculate how far off this heading this station is.
        int delta = station.getBearingOffset(heading);
        holder.needle.setHeading(delta);        
        
        String howFar = station.getDistance();        
        holder.txtDistance.setText(howFar);         
        
        holder.txtStatus.setText(station.MSG_NEXT_DEPARTURE());
        
  
        if (station.MSG_ALERT() != null && station.MSG_ALERT().trim().contains("On-Time")){      
        	
	        holder.txtAlert.setText(station.MSG_ALERT()); 
	        holder.txtAlert.setTextColor(Color.parseColor("#8ac600")); //Custom Green
        }
        else if (station.MSG_ALERT() != null && station.MSG_ALERT().contains("Press for Schedule")){       
        	
	        holder.txtAlert.setText(station.MSG_ALERT()); 
	        holder.txtAlert.setTextColor(Color.parseColor("#FF9900")); //Custom Orange
        }
        else if (station.MSG_ALERT() != null && station.MSG_ALERT().contains("No Live Updates")){      
        	
	        holder.txtAlert.setText(station.MSG_ALERT()); 
	        holder.txtAlert.setTextColor(Color.parseColor("#9900FF")); //Custom Purple
        }
        else if (station.MSG_ALERT() != null && station.MSG_ALERT().contains("Weekend") | 
        		 station.MSG_ALERT() != null && station.MSG_ALERT().contains("Holiday")){      
        	
	        holder.txtAlert.setText(station.MSG_ALERT()); 
	        holder.txtAlert.setTextColor(Color.parseColor("#FFDC143C")); //Custom Crimson
        }        
        else
        {
        	holder.txtAlert.setText(station.MSG_ALERT());
            holder.txtAlert.setTextColor(Color.RED);
        }
				        
        holder.square.draw(station.STATION_LINES());
        
        
        /* If the Device has no GPS or Wifi/Enabled ability to connect and get a location
         * the howFar value will = "NULL"
         * 
         * When "NULL" then hide the compass needle and distance value, as it provides no 
         * valued information without a known device geo-location
         */
        if (howFar.equals("NULL")){
          	 holder.needle.setVisibility(View.INVISIBLE);   
          	 holder.txtDistance.setVisibility(View.INVISIBLE);
        }
        else
        {
        	holder.needle.setVisibility(View.VISIBLE); 
         	holder.txtDistance.setVisibility(View.VISIBLE);
        }        
        return row;
    }
    
    /*
     * This shared class allows us to re-use the views for each row in a ListView.
     */
    static class StationHolder
    {
    	NeedleView needle;
        TextView txtName; 
        TextView txtStatus;
        TextView txtAlert;        
        SquareView square;
        TextView txtDistance;        
    }	
}
