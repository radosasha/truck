package com.truck.alexander.modules;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import com.truck.alexander.db.DBConnector;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


public class ReceiveCoordinateModule extends Service{
	
	 //current service 
     Service service;
	
     public static String currTripName;
	 /*
	 * CONSTANTS
	 */
    static long SECOND = 1000; //in milliseconds
    //time between two coords receiving
	public static long receiveFrequency = SECOND*5; //5 min
	//min distance between two coordinates
	final long DISTANCE = 50;// in meters
	//max inflicity oc coords
	final long INFELICITY = 20; //in meters
	
	SimpleDateFormat timeFormatter;//date
	
	// location service constants
	LocationManager lm = null; 
	LocationListener ll = null;
	
	// db constants
	SQLiteDatabase db;
	DBConnector dbOpenHelper;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/////////////////////////"GPS RECEIVER" MODULE//////////////////////////////
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e("Coordinate Service", "on create");

		// get current service
		service = this;

		// inic GUI buttons
		openDB();
		currTripName = dbOpenHelper.getValueByIndex(db, 0); //select tripid from tripinfo
		// stop service if "start" never been clicked
		boolean canBeRun = canRun();
		closeDB();
		Log.e("Can Run", "" + canBeRun);
		if (canBeRun == false)
			killService();

		// inic GPS manager
		Log.e("ON START", "GPS");
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// receive only good points
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		lm.getBestProvider(criteria, true);
		// inic GPS listener
		ll = new GPSListener();

		// run GPS
		startGPSListener();
		// lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, receiveFrequency, DISTANCE, ll);

		// inic date
		timeFormatter = (SimpleDateFormat) DateFormat.getDateTimeInstance(
				DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
		timeFormatter.setTimeZone(TimeZone.getTimeZone("GMT-05:00"));
		timeFormatter.applyPattern("yyyy-MM-dd HH:mm:ss");
		Log.e("INIC", "OK");
		
		/*
		 * run send coors module
		 */
		if(!isReportServiceStarted()){
			startService(new Intent(ReceiveCoordinateModule.this,ReportsService.class));
			Log.e("REPORT SERVICE","started");
		}
	}
	
	
	/*
	 * determine if service can be started
	 * @return true if "start" clicked and "pause" on display
	 */
	private boolean canRun() {
		HashMap<String, String> hm = dbOpenHelper.selectFromConfigTable(db);
		// button never been clicked
		if (hm == null)
			return false;
		// get current status ("pause" or "resume")
		// if pause - start service, return true
		// if resume - need to click "resume" button to start service, return
		// false
		if (hm.get("status").equals("resume"))
			return false;
		return true;
	}

    /*
     * stop current service
     */
	private void killService() {
		stopSelf();		
	}

	@Override
	public void onDestroy() {		
		super.onDestroy();
		Log.e("Coordinate Service","on destroy");
		if(lm != null)stopGPSListener();
	}
	
	/*
	 * open database
	 */
	void openDB() {
		dbOpenHelper = new DBConnector(this);
		// open connection
		db = dbOpenHelper.getWritableDatabase();
	}

	/*
	 * close database
	 */
	void closeDB() {
		// close connection
		db.close();
		dbOpenHelper.close();
	}

	/*
	 * @return current date/time
	 */
	String getTime() {
		return timeFormatter.format(new Date());
	}
	
	/*
	 *  start GPS LISTENER
	 */
	void startGPSListener(){
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, receiveFrequency, DISTANCE, ll);
		//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
	}
	
	/*
	 *  stop/pause GPS LISTENER 
	 */
	void stopGPSListener(){
		lm.removeUpdates(ll);
	}
	
	
	
	private class GPSListener implements LocationListener {
		public void onLocationChanged(Location location) {
			Log.e("Coords Receiver", "RECEIVE COORDINATE");
			// current point
			double gpsX = location.getLatitude();
			double gpsY = location.getLongitude();

			/*
			 * it's need internet connection to determine cur pos
			 */
			String locality = "need internet connection";
			String countryName = "...";

			/*
			 * insert receiver coordinate into database
			 */
			insertBlock: synchronized (DBConnector.synchList) {
				// if coars infelicity to long, abort coars data inserting
				// 0.0 is an error situation
				float acc = location.getAccuracy();
				if (acc > INFELICITY
						|| location.getAccuracy() == 0.0)
					break insertBlock;
				openDB();
				try {
					dbOpenHelper.insertCoords(
					db,
					gpsX+"",
					gpsY+"",
					locality + ", " + countryName,
					location.getAccuracy()+"", 
					location.getSpeed()+"",
					getTime()
					);
					Log.e("Inserted", "...");
				} catch (Exception e) {
					Log.e("Insert", "FAIL");
					toast("Insert fail! Too much coordinater in DB.");
				}
				closeDB();
			}
		}
		
		/*
		 * implemented methods
		 */
		public void onProviderDisabled(String provider) {
			Log.e("GPS", "ВЫКЛючено");
		}
		public void onProviderEnabled(String provider) {
			Log.e("GPS", "ВКЛючено");
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
	
	/*
	 * @return true if Reports Service started
	 */
	private boolean isReportServiceStarted() {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("com.truck.alexander.modules.ReportsService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	/*
	 * shows message to user
	 */
	void toast(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
	
	
}
