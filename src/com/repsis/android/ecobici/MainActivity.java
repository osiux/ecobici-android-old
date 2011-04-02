package com.repsis.android.ecobici;

import java.util.List;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
/*import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;*/
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import com.repsis.android.ecobici.Ecobici;

public class MainActivity extends MapActivity implements Runnable {
	private MapController mapController;
	private MapView mapView;
	/*private LocationManager locationManager;
	private LocationListener locationListener;
	private int latitude, longitude;*/
	private ProgressDialog pd;
	private static String stations;
	private static Drawable drawable;
	private static StationOverlay itemizedoverlay;
	private static MyLocationOverlay myLocationOverlay;
	List<Overlay> mapOverlays;
	private static Context myContext;
	
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        if (myContext == null) {
        	myContext = this;
        }
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);	
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
		
		mapController = mapView.getController();
		mapController.setZoom(17);
		
		if (myLocationOverlay == null) {
			myLocationOverlay = new MyLocationOverlay(this, mapView);
		}
        mapView.getOverlays().add(myLocationOverlay);
        myLocationOverlay.enableCompass();
        //myLocationOverlay.enableMyLocation();
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                mapController.animateTo(myLocationOverlay.getMyLocation());
            }
        });
        
        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GeoUpdateHandler();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);*/
		
		mapOverlays = mapView.getOverlays();
		if (drawable == null) {
			drawable = this.getResources().getDrawable(R.drawable.bike_icon);
		}
		
		if (itemizedoverlay == null) {
			itemizedoverlay = new StationOverlay(drawable, this);
			
			pd = ProgressDialog.show(this, this.getString(R.string.loadingTitle), this.getString(R.string.loadingText), true, false);

			Thread thread = new Thread(this);
			thread.start();
		}else{
			mapOverlays.add(itemizedoverlay);
		}
    }
    
    @Override
    public void onPause() {
		//locationManager.removeUpdates(locationListener);
		myLocationOverlay.disableMyLocation();
		
		super.onPause();
	}
    
    @Override   
    protected void onResume() {
        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new GeoUpdateHandler();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);*/
		
		myLocationOverlay.enableMyLocation();

        super.onResume();
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
    		case R.id.openAboutWindow:
    			ShowAboutWindow();
    			break;
    	}
    	
    	return true;
    }
    
    protected void ShowAboutWindow() {
    	Intent aboutWindowScreen = new Intent(this, About.class);
    	startActivity(aboutWindowScreen);
    }
    
    /*public class GeoUpdateHandler implements LocationListener {
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
			
			GeoPoint me = new GeoPoint(latitude, longitude);
			
			mapController.animateTo(me);
			mapView.invalidate();
		}
    }*/

	public void run() {
		stations = Ecobici.getStations();

		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();

			if (stations == "error" || stations == "" || stations == "[]") {
				Toast.makeText(myContext, myContext.getString(R.string.loadingStationError), Toast.LENGTH_LONG).show();
				((Activity) myContext).finish();
			}else{
				Gson json = new Gson();
				Stations[] station = json.fromJson(stations, Stations[].class);
				
				for (int i = 0; i < station.length; i++) {
					int latitude = (int) (station[i].latitude * 1E6);
					int longitude = (int) (station[i].longitude * 1E6);
					GeoPoint point = new GeoPoint(latitude, longitude);
					OverlayItem overlayitem = new OverlayItem(point, station[i].id, station[i].address);
					itemizedoverlay.addOverlay(overlayitem);
				}
				mapOverlays.add(itemizedoverlay);
			}
		}
	};
}