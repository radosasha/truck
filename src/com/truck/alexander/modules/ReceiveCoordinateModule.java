package com.truck.alexander.modules;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.truck.alexander.TabTrip;

public class ReceiveCoordinateModule {
	
	/*
	 * CONSTANTS
	 */
	long SECOND = 1000;
	// location service variables
	LocationManager lm = null;
	LocationListener ll = null;
	// main class
	TabTrip mainClass = null;
	
	ReceiveCoordinateModule(TabTrip mainCl){
		//link to main class
		mainClass = mainCl;
		//initialization of location variables
		lm = (LocationManager) mainClass.getSystemService(Context.LOCATION_SERVICE);
		ll = new GPSListener();
	}
	
	
	
	// start GPS LISTENER , write incoming coordinates to database
	void startGPSListener(){		
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5*SECOND, 0, ll);
	}
	
	// stop/pause GPS LISTENER 
	void stopGPSListener(){
		lm.removeUpdates(ll);
	}
	
	
	
	private class GPSListener implements LocationListener {
		public void onLocationChanged(Location location) {

			Log.e("GPS LOCATION CHANGED", location.getLatitude() + "");
			Log.e("GPS LOCATION CHANGED", location.getLongitude() + "");
			double gpsX =  location.getLatitude();
			double gpsY =  location.getLongitude();
			
		}
		public void onProviderDisabled(String provider) {
			Log.e("GPS", "ВЫКЛючено");
		}
		public void onProviderEnabled(String provider) {
			Log.e("GPS", "ВКЛючено");
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}
