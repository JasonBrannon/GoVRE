package com.echo5bravo.govre.UTILS;

import com.echo5bravo.govre.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class SquareView extends View {
	
	 	private Paint paint_upper_half;
	    private Path path_upper_half;
	    
	    private Paint paint_lower_half;
	    private Path path_lower_half;
    
	    private boolean isAnimating;
	    private int heading = 0;

		final int preferredSize = 50;

	    public SquareView(Context context) {
	        super(context);
			init();
	      }	    

		public SquareView(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}

		public SquareView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			init();
		}		
				
		private void init(){

		}
		
		/*
		 * The VRE rail has 2 lines (FBG and MSS) lines, but at the Alexandria through Union Station
		 * both the MSS and FBG line share stations.  In order to display this to the user, break out 
		 * 2 opposing triangles to make a square and use the lineTye (FBG, MSS, ALL) to display the 
		 * correct color for the square.  If it's FBG, then both triangles are FBG, if both FBG/BLUE.
		 */
		public void draw(String lineType) {
			
			/*
			 * UPPER HALF OF SQUARE
			 */
			paint_upper_half = new Paint();
		    path_upper_half = new Path();		    

	        // Construct Upper Half of Square 
	        path_upper_half.moveTo(-21,-20);
		    path_upper_half.lineTo(20,-20);
	        path_upper_half.lineTo(20,21);	
	        
	        path_upper_half.close();		    
		    paint_upper_half.setAntiAlias(true);		    
		    paint_upper_half.setColor(CalculateLineColor(lineType, true));  //Custom Color Decision
		    paint_upper_half.setStyle(Paint.Style.FILL);
		    
		    /*
			 * LOWER HALF OF SQUARE
			 */		    
		    paint_lower_half = new Paint();
		    path_lower_half = new Path();		  
       
	               
	        // Construct Lower Half of Square  
	        path_lower_half.moveTo(21,20);
		    path_lower_half.lineTo(-20,20);
	        path_lower_half.lineTo(-20,-21);
	        
	        path_lower_half.close();		    
		    paint_lower_half.setAntiAlias(true);		    
		    paint_lower_half.setColor(CalculateLineColor(lineType, false));	 //Custom Color Decision
		    paint_lower_half.setStyle(Paint.Style.FILL);
		}
		
		private int CalculateLineColor(String lineType, boolean IsUpperSquare){
			
			int redlineColor = getResources().getColor(R.color.redline);
		    int bluelineColor = getResources().getColor(R.color.blueline);	
		    
		    /*
			 * COLOR CALCULATE
			 * 
			 * if/elseif easy: return FBG or MSS
			 * else: have a ALL, always make the TOP RED and BOTTOM BLUE
			 */
		    
		    if (lineType.equals("FBG"))
		    	return redlineColor;
		    else if  (lineType.equals("MSS"))
		    	return bluelineColor;
		    else
		    {
		    	if (IsUpperSquare && lineType.equals("ALL"))
		    		return redlineColor;
		    	else
		    		return bluelineColor;		    	
		    }	
		}
		
		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(preferredSize, preferredSize);
		}
	   
	    
	    @Override protected void onDraw(Canvas canvas) {
	    	
	    	if (!isAnimating) return;
        		
	        int centre = preferredSize / 2;
	    	canvas.translate(centre, centre);
	        canvas.rotate(heading);
	        
	        canvas.drawPath(path_upper_half, paint_upper_half);
	        canvas.drawPath(path_lower_half, paint_lower_half);
	    }	    
	    
	    @Override
	    protected void onAttachedToWindow() {
	    	isAnimating = true;
	        super.onAttachedToWindow();
	    }

	    @Override
	    protected void onDetachedFromWindow() {
	    	isAnimating = false;
	        super.onDetachedFromWindow();
	    }
	}
