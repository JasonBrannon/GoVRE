package com.echo5bravo.govre.UTILS;

import android.graphics.Bitmap;
import android.graphics.Canvas; 
import android.graphics.Color;
import android.graphics.Paint; 
import android.graphics.Point; 
import android.graphics.RectF; 
import com.google.android.maps.GeoPoint; 
import com.google.android.maps.MapView; 
import com.google.android.maps.Overlay; 
import com.google.android.maps.Projection; 

public class RouteOverlay  extends Overlay 
{ 
	  private GeoPoint gp1;
	  private GeoPoint gp2;
	  private int mRadius=2;
	  private int mode=0;
	  private int defaultColor;
	  private String text="";
	  private Bitmap img = null;
	     

	  public RouteOverlay(GeoPoint gp1,GeoPoint gp2,int mode) //  GeoPoint is a int. (6E)
	  { 
		  
	    this.gp1 = gp1; 
	    this.gp2 = gp2;
	    this.mode = mode;
	    defaultColor = 999; // no defaultColor

	  } 
	  

	  public RouteOverlay(GeoPoint gp1,GeoPoint gp2,int mode, int defaultColor) 
	  { 
	    this.gp1 = gp1; 
	    this.gp2 = gp2;
	    this.mode = mode;
	    this.defaultColor = defaultColor;
	  } 
	  
	  public void setText(String t)
	  {
		  this.text = t;
	  }
	  
	  public void setBitmap(Bitmap bitmap)
	  {
		  this.img = bitmap;
	  }
	  
	  public int getMode()
	  {
		  return mode;
	  }

	  @Override 
	  public boolean draw 
	  (Canvas canvas, MapView mapView, boolean shadow, long when) 
	  { 
		  
	    Projection projection = mapView.getProjection(); 
	    if (shadow == false) 
	    {      
	     
	      Paint paint = new Paint(); 
	      paint.setAntiAlias(true); 
	     
	      
	      Point point = new Point(); 
	      projection.toPixels(gp1, point);
	      // mode=1¡Gstart 
	      if(mode==1)
	      {
	    	if(defaultColor==999)
	    		paint.setColor(Color.BLUE);   
	    	else
	    	 	paint.setColor(defaultColor);   
	    		
	    	     
	        RectF oval=new RectF(point.x - mRadius, point.y - mRadius, point.x + mRadius, point.y + mRadius); 
	        // start point
	        canvas.drawOval(oval, paint);
	        
	      }
	      // mode=2¡Gpath 
	      else if(mode==2)
	      {
	    	  paint.setStrokeWidth(5);
				paint.setAlpha(120);
				
				if (defaultColor == 999)
					paint.setColor(Color.RED);
				else
					paint.setColor(defaultColor);
		
				Point point2 = new Point();
		
				projection.toPixels(gp2, point2);
				
				canvas.drawLine(point.x, point.y, point2.x,point2.y, paint);
				
	      	/*if(defaultColor==999)
	    		paint.setColor(Color.RED);   
	    	else
	    	 	paint.setColor(defaultColor);   
	      	
	        Point point2 = new Point(); 
	        projection.toPixels(gp2, point2);
	        paint.setStrokeWidth(5);
	        paint.setAlpha(120);       
	        canvas.drawLine(point.x, point.y, point2.x,point2.y, paint); */      
	      }
	      /* mode=3¡Gend */
	      else if(mode==3)
	      { 
	    	  paint.setStrokeWidth(5);
			  paint.setAlpha(120);
				
				if (defaultColor == 999)
					paint.setColor(Color.GREEN);
				else
					paint.setColor(defaultColor);
		
				Point point2 = new Point();
		
				projection.toPixels(gp2, point2);
		
				canvas.drawLine(point.x, point.y, point2.x,point2.y, paint);
		
				RectF oval = new RectF(point2.x - mRadius,point2.y - mRadius, point2.x + mRadius,point2.y + mRadius);
		
				paint.setAlpha(255);
		
				canvas.drawOval(oval, paint);
	        /* the last path */

	      	/*if(defaultColor==999)
	    		paint.setColor(Color.GREEN);   
	    	else
	    	 	paint.setColor(defaultColor);   
	    	  
	        Point point2 = new Point(); 
	        projection.toPixels(gp2, point2);
	        paint.setStrokeWidth(1);
	        paint.setAlpha(1);
	        canvas.drawLine(point.x, point.y, point2.x,point2.y, paint);
	        
	        
	        RectF oval=new RectF(point2.x - mRadius,point2.y - mRadius,  
	                             point2.x + mRadius,point2.y + mRadius); 
	         end point 
	        paint.setAlpha(1);
	        canvas.drawOval(oval, paint);*/
	      }
	      /* mode=4¡Gcar */
	      else if(mode==4)
	      {
	    	  
	  	   	if(defaultColor==999)
	    		paint.setColor(Color.GREEN);   
	    	else
	    	 	paint.setColor(defaultColor);   
	    	  
	        Point point2 = new Point(); 
	        projection.toPixels(gp2, point2); 
	        paint.setTextSize(20);
	        paint.setAntiAlias(true); 
	        canvas.drawBitmap(img, point2.x, point2.y,paint);      
	        canvas.drawText(this.text, point2.x, point2.y, paint);
	    //    Log.d(TAG, "Draw the text="+this.text+ " at point="+point2.x + "," + point2.y);
	      }
	      
	      else if(mode==5)
	      {
	    	  
	  	   	if(defaultColor==999)
	    		paint.setColor(Color.GREEN);   
	    	else
	    	 	paint.setColor(defaultColor);   
	    	  
	        Point point2 = new Point(); 
	        projection.toPixels(gp2, point2); 
	        paint.setTextSize(20);
	        paint.setAntiAlias(true); 
	        canvas.drawBitmap(img, point2.x, point2.y,paint);
	    
	      
	        
	    //    Log.d(TAG, "Draw the text="+this.text+ " at point="+point2.x + "," + point2.y);
	      }
	      
	      
	      
	    } 
	    return super.draw(canvas, mapView, shadow, when); 
	  } 
	  

	} 
