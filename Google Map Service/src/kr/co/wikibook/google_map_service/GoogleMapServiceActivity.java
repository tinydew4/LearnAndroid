package kr.co.wikibook.google_map_service;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class GoogleMapServiceActivity extends MapActivity {
    /** Called when the activity is first created. */

	private MapView mapView;
	private MapController mc;
	private List<Overlay> overlay;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mapView = (MapView)findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setSatellite(true);

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mc = mapView.getController();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 3, mLocationListener);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
    	lm.removeUpdates(mLocationListener);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
    	return false;
    }

    private LocationListener mLocationListener = new LocationListener() {
    	public void onLocationChanged(Location location) {
    		if (location != null) {
				GeoPoint gp = new GeoPoint((int)(location.getLatitude() * 1000000), (int)(location.getLongitude() * 1000000));
		        
		        mc.animateTo(gp);
		        mc.setZoom(14);
		        
		        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.comment_write);
		        bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
		        Drawable drawable = new BitmapDrawable(bitmap);
		        
		        MyIconItemizedOverlay mdio = new MyIconItemizedOverlay(drawable);
		        OverlayItem overlayitem = new OverlayItem(gp, "", "");
		        mdio.addOverlayItem(overlayitem);
		        overlay = mapView.getOverlays();
		        overlay.add(mdio);
    		}
    	}
    	
    	public void onProviderDisabled(String arg0) {
    		
    	}
    	
    	public void onProviderEnabled(String arg0) {
    		
    	}
    	
    	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    		
    	}
    };
}