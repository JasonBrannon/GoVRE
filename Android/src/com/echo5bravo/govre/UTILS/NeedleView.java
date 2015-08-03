package com.echo5bravo.govre.UTILS;

import com.echo5bravo.govre.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/*
 * This view shows how close a Station is to the current compass bearing.
 * We show a small needle.  If it points straight up then the Station is directly
 * ahead; any deviation shows how far it is off this heading.
 */
public class NeedleView extends View {
    private Paint paint;
    private Path path;
    
    private boolean isAnimating;
    private int heading = 0;

	final int preferredSize = 40;

    public NeedleView(Context context) {
        super(context);
		init();
      }    

	public NeedleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NeedleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}	
	
	private void init(){
	    paint = new Paint();
	    path = new Path();

	    int needleColour = getResources().getColor(R.color.needle);

        // Construct a wedge-shaped path for the needle.
        path.moveTo(0, -15);
        path.lineTo(-8, 15);
        path.lineTo(8, 15);
        path.close();
        
        paint.setAntiAlias(true);
        
        paint.setColor(needleColour);
        paint.setStyle(Paint.Style.FILL);
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(preferredSize, preferredSize);
	}
	
    public void setHeading(int bearing){
    	heading = bearing;
    	invalidate();
    }    
    
    @Override protected void onDraw(Canvas canvas) {
    	
    	if (!isAnimating) return;
    	        		
        int centre = preferredSize / 2;
    	canvas.translate(centre, centre);
        canvas.rotate(heading);
        
        canvas.drawPath(path, paint);
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
