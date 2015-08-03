package com.echo5bravo.govre.ADAPTERS;

import com.echo5bravo.govre.R;
import com.echo5bravo.govre.INFO.Schedule;
import com.echo5bravo.govre.UTILS.Common;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ScheduleAdapter extends ArrayAdapter<Schedule>{
		
		private static final String TAG = "Schedule Adapter"; 
		
		private Context context; 
		private int layoutResourceId;    
		private Schedule[] data = null;
		private String timeformat;
		
		public ScheduleAdapter(Context context, int textViewResourceId, Schedule[] data, String timeformat) {
			super(context, textViewResourceId, data);
			this.context = context;
			this.layoutResourceId = textViewResourceId;
			this.data = data;
			this.timeformat = timeformat;
		}  

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View row = convertView;
	        ScheduleHolder holder = null;
	        
	        if (row == null)
	        {
	            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            row = inflater.inflate(layoutResourceId, parent, false);
	            
	            holder = new ScheduleHolder();	            
	 	           
	            holder.txtTime = (TextView)row.findViewById(R.id.txtTime);
	            holder.txtTrain = (TextView)row.findViewById(R.id.txtTrain);	            
	            holder.txtCodes = (TextView)row.findViewById(R.id.txtCodes);
		           
	            row.setTag(holder);
	        }
	        else
	        {
	            holder = (ScheduleHolder)row.getTag();
	        }
	        
	        Schedule schedule = data[position];
	        
	        /*********************************************
	         *  TIME
	         *********************************************/	      
	             
	        //Might use the logic above?
	        holder.txtTime.setText(Common.FormatTime(schedule.SCHEDULE_DEPARTURE_TIME(), timeformat) + " Operates: M-F"); 
	        
	        /*********************************************
	         *  TRAIN
	         *********************************************/	 
	        holder.txtTrain.setText("Train: " + schedule.TRIP_SHORT_NAME() + " (Heading - " + schedule.TRIP_HEADSIGN() + ")"); 	
        
	        return row;
	    }
	    
	    /*
	     * This shared class allows us to re-use the views for each row in a ListView.
	     */
	    static class ScheduleHolder
	    {   	
	        TextView txtTime;
	        TextView txtTrain;
	        TextView txtCodes; 
	    }
	}


