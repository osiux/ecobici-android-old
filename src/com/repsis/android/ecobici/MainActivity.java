package com.repsis.android.ecobici;

import java.util.List;

import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import com.repsis.android.ecobici.Ecobici;

public class MainActivity extends MapActivity {
	private MapController mapController;
	private MapView mapView;
	private LocationManager locationManager;
	
	private int latitude, longitude;
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);	
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 100.0f, new GeoUpdateHandler());
		
		mapController = mapView.getController();
		mapController.setCenter(new GeoPoint(19420337, -991727042));
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.bike_icon);
		StationOverlay itemizedoverlay = new StationOverlay(drawable, null);
		
		String stations = Ecobici.getStations();
		
		if (stations == "error") {
			Toast.makeText(this, "Imposible obtener las estaciones por el momento.", Toast.LENGTH_SHORT).show();
		}
		
		Gson json = new Gson();
		Stations[] station = json.fromJson(stations, Stations[].class);
		
		for (int i = 0; i < station.length; i++) {
			int latitude = (int) (station[i].latitude * 1E6);
			int longitude = (int) (station[i].longitude * 1E6);
			GeoPoint point = new GeoPoint(latitude, longitude);
			OverlayItem overlayitem = new 	OverlayItem(point, station[i].address, "");
			itemizedoverlay.addOverlay(overlayitem);
		}
		mapOverlays.add(itemizedoverlay);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	 super.onCreateOptionsMenu(menu);
    	 
    	 MenuInflater inflater = new MenuInflater(this);
    	 inflater.inflate(R.layout.menu, menu);
    	 
    	 return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.openAbout:
    			ShowAboutWindow();
    			break;
    	}
    	
    	return true;
    }
    protected void ShowAboutWindow() {
    	Intent aboutWindowScreen = new Intent(this, About.class);
    	startActivity(aboutWindowScreen);
    }
    
    public class GeoUpdateHandler implements LocationListener {
    	public void onStatusChanged(String provider, int status, Bundle extras) {
			// called when the provider status changes. Possible status: OUT_OF_SERVICE, TEMPORARILY_UNAVAILABLE or AVAILABLE.
		}
    	
    	public void onProviderEnabled(String provider) {
			// called when the provider is enabled by the user
		}
    	
    	public void onProviderDisabled(String provider) {
			// called when the provider is disabled by the user, if it's already disabled, it's called immediately after requestLocationUpdates
		}
		
    	public void onLocationChanged(Location location) {
			latitude = (int) (location.getLatitude() * 1E6);
			longitude = (int) (location.getLongitude() * 1E6);
			
			GeoPoint center = new GeoPoint(latitude, longitude);
			
			mapController.setCenter(center);
			mapController.setZoom(16);
		}
    }
}