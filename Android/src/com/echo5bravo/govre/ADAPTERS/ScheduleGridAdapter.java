package com.echo5bravo.govre.ADAPTERS;

import java.util.ArrayList;

import com.echo5bravo.govre.UTILS.Common;
import com.echo5bravo.govre.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


@SuppressLint("ResourceAsColor")
public class ScheduleGridAdapter extends BaseAdapter{
	private ArrayList<String> listText;
	private ArrayList<String> listHiddenDepartureTime;
	private ArrayList<String> listHiddenToast;
	private ArrayList<String> listHiddenSelectedRow;
	private ArrayList<Integer> listImg;
	private Activity activity;

	public ScheduleGridAdapter(Activity activity
								, ArrayList<String> listText
								, ArrayList<Integer> listImg
								, ArrayList<String> listHiddenToast
								, ArrayList<String> listHiddenSelectedRow
								, ArrayList<String> listHiddenDepartureTime) {
		super();
		this.listText = listText;
		this.listHiddenDepartureTime = listHiddenDepartureTime;
		this.listImg = listImg;
		this.listHiddenToast = listHiddenToast;
		this.listHiddenSelectedRow = listHiddenSelectedRow;
		this.activity = activity;
	}
	
	public void refill(Activity activity
			, ArrayList<String> listText
			, ArrayList<Integer> listImg
			, ArrayList<String> listHiddenToast
			, ArrayList<String> listHiddenSelectedRow
			, ArrayList<String> listHiddenDepartureTime) {
		this.listText = listText;
		this.listHiddenDepartureTime = listHiddenDepartureTime;
		this.listImg = listImg;
		this.listHiddenToast = listHiddenToast;
		this.listHiddenSelectedRow = listHiddenSelectedRow;
		this.activity = activity;
        notifyDataSetChanged();
    }  

	public int getCount() {
		return listHiddenToast.size();
	}

	public String getItem(int position) {
		return listHiddenToast.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	public static class ViewHolder
	{
		public ImageView imgViewFlag;
		public TextView txtViewTitle;
		public TextView txtHiddenDepartureTime;
		public TextView txtViewHiddenToast;
		public TextView txtViewHiddenSelectedRow;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder view;
		LayoutInflater inflator = activity.getLayoutInflater();
		
		long departureDelta = 0;

		if(convertView==null)
		{
			view = new ViewHolder();
			convertView = inflator.inflate(R.layout.schedule_grid_row, null);
			
			view.txtViewTitle = (TextView) convertView.findViewById(R.id.textView1);	
			view.txtHiddenDepartureTime = (TextView) convertView.findViewById(R.id.txtHiddenDepartureTime);
			view.txtViewHiddenToast = (TextView) convertView.findViewById(R.id.texthiddentoast);
			view.txtViewHiddenSelectedRow = (TextView) convertView.findViewById(R.id.texthiddenrowselected);
			view.imgViewFlag = (ImageView) convertView.findViewById(R.id.imageView1);

			convertView.setTag(view);
		}
		else
		{
			view = (ViewHolder) convertView.getTag();
		}
		view.txtViewTitle.setText(listText.get(position));
		//view.txtHiddenDepartureTime.setText(Common.CompareTimeDelta(listHiddenDepartureTime.get(position)));
		if (listHiddenDepartureTime.get(position) != "")
			departureDelta = Common.CompareTimeDelta(listHiddenDepartureTime.get(position));
		
		view.txtViewHiddenToast.setText(listHiddenToast.get(position));
		view.imgViewFlag.setImageResource(listImg.get(position));		
		view.txtViewHiddenSelectedRow.setText(listHiddenSelectedRow.get(position));
		
		if (view.txtViewTitle.getText().length() > 0  && !view.txtViewTitle.getText().equals("M-F")){
			// Gets the layout params that will allow you to resize the layout
			LayoutParams imgViewFlagParams = view.imgViewFlag.getLayoutParams();
			// Changes the height and width to the specified *pixels*
			imgViewFlagParams.height = 0; 
			imgViewFlagParams.width = 0;
			view.imgViewFlag.invalidate();
			
			// Gets the layout params that will allow you to resize the layout
			LayoutParams txtViewTitleParams = view.txtViewTitle.getLayoutParams();
			// Changes the height and width to the specified *pixels*
			txtViewTitleParams.height = 48;
			txtViewTitleParams.width = 200; //LayoutParams.FILL_PARENT;	
			view.txtViewTitle.invalidate();
		}
		else if (view.txtViewTitle.getText().equals("M-F")){
			// Gets the layout params that will allow you to resize the layout
			LayoutParams imgViewFlagParams = view.imgViewFlag.getLayoutParams();
			// Changes the height and width to the specified *pixels*
			imgViewFlagParams.height = 28; 
			imgViewFlagParams.width = LayoutParams.WRAP_CONTENT;
			view.imgViewFlag.invalidate();
			
			// Gets the layout params that will allow you to resize the layout
			LayoutParams txtViewTitleParams = view.txtViewTitle.getLayoutParams();
			// Changes the height and width to the specified *pixels*
			txtViewTitleParams.height = 48;
			txtViewTitleParams.width = 200; //LayoutParams.FILL_PARENT;	
			view.txtViewTitle.invalidate();
		}
		else
			
		{
			// Gets the layout params that will allow you to resize the layout
			LayoutParams imgViewFlagParams = view.imgViewFlag.getLayoutParams();
			// Changes the height and width to the specified *pixels*
			imgViewFlagParams.height = 48;
			imgViewFlagParams.width = LayoutParams.WRAP_CONTENT;
			view.imgViewFlag.invalidate();
						
			// Gets the layout params that will allow you to resize the layout
			LayoutParams txtViewTitleParams = view.txtViewTitle.getLayoutParams();
			// Changes the height and width to the specified *pixels*
			txtViewTitleParams.height = 0;
			txtViewTitleParams.width = 0;
			view.txtViewTitle.invalidate();
		}
		
		
		//if (Boolean.valueOf(view.txtViewHiddenSelectedRow.getText().toString()) && view.txtViewTitle.getText().length() > 0){
		if (Boolean.valueOf(view.txtViewHiddenSelectedRow.getText().toString())){
			
			
			if (departureDelta < 0){
				//view.txtViewTitle.setBackgroundColor(0xff00ffff);  //Cyan
				view.txtViewTitle.setBackgroundColor(0xffffff00);  //Yellow
				view.txtViewTitle.invalidate();
				
				view.imgViewFlag.setBackgroundColor(0xffffff00);  //Yellow	
				view.imgViewFlag.invalidate();
			}
			else
			{
				//view.txtViewTitle.setBackgroundColor(0xff00ffff);  //Cyan
				view.txtViewTitle.setBackgroundColor(0xFFFFD700);  // Gold
				view.txtViewTitle.invalidate();
				
				view.imgViewFlag.setBackgroundColor(0xffffff00);  //Yellow	
				view.imgViewFlag.invalidate();
			}
			
		}
		else
		{
			if (departureDelta < 0){
				view.txtViewTitle.setBackgroundColor(0xffffffff);  //White
				view.txtViewTitle.invalidate();
			}
			else
			{
				view.txtViewTitle.setBackgroundColor(0xFFDCDCDC);  // Gainsboro
				view.txtViewTitle.invalidate();
			}
			
			view.imgViewFlag.setBackgroundColor(0xffffffff);  //White	
			view.imgViewFlag.invalidate();
		}

		return convertView;
	}
}
