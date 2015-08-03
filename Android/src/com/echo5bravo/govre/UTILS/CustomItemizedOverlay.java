package com.echo5bravo.govre.UTILS;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import com.readystatesoftware.mapviewballoons.BalloonItemizedOverlay;
import com.readystatesoftware.mapviewballoons.BalloonOverlayView;

public class CustomItemizedOverlay<Item extends OverlayItem> extends BalloonItemizedOverlay<CustomOverlayItem> {

	private ArrayList<CustomOverlayItem> m_overlays = new ArrayList<CustomOverlayItem>();
	private Context c;
	
	public CustomItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(boundCenter(defaultMarker), mapView);
		c = mapView.getContext();
	}

	public void addOverlay(CustomOverlayItem overlay) {
	    m_overlays.add(overlay);
	    populate();
	}

	@Override
	protected CustomOverlayItem createItem(int i) {
		return m_overlays.get(i);
	}

	@Override
	public int size() {
		return m_overlays.size();
	}

	@Override
	protected boolean onBalloonTap(int index, CustomOverlayItem item) {
		Toast.makeText(c, "Press the Bubble's \"X\" to close", Toast.LENGTH_LONG).show();

		return true;
	}

	@Override
	protected BalloonOverlayView<CustomOverlayItem> createBalloonOverlayView() {
		// use our custom balloon view with our custom overlay item type:
		return new CustomBalloonOverlayView<CustomOverlayItem>(getMapView().getContext(), getBalloonBottomOffset());
	}

}
