/*package com.truck.alexander;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.truck.alexander.db.DBConnector;
import com.truck.alexander.modules.ReceiveCoordinateModule;
import com.truck.alexander.modules.UrlConnector;



public class GPS extends Activity implements OnClickListener {
	*//** Called when the activity is first created. *//*
	SQLiteDatabase db;
	DBConnector dbOpenHelper;
	Intent it;

	TextView tv1; // last coord
	TextView tv2; // accuracy
	TextView tv3; // coord count
	TextView tv4; // curr address
	TextView tv5; // curr speed
	TextView tv6; // aver speed
	TextView tv7; // aver distance
	Button refresh;
	Button send;

	String[] stat;
	static TextView bt1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);
		Button start = (Button) findViewById(R.id.start);
		Button stop = (Button) findViewById(R.id.stop);
		Button select = (Button) findViewById(R.id.select);
		Button clear = (Button) findViewById(R.id.clear);

		// statistic fields
		tv1 = (TextView) findViewById(R.test.lastcoord2);
		tv2 = (TextView) findViewById(R.test.coordacc2);
		tv3 = (TextView) findViewById(R.test.coordcount2);
		tv4 = (TextView) findViewById(R.test.address2);
		tv5 = (TextView) findViewById(R.test.currspeed2);
		tv6 = (TextView) findViewById(R.test.summspeed2);
		tv7 = (TextView) findViewById(R.test.trip2);
		refresh = (Button) findViewById(R.test.refresh);
		refresh.setOnClickListener(this);
		send = (Button) findViewById(R.test.send);
		send.setOnClickListener(this);

		start.setOnClickListener(this);
		stop.setOnClickListener(this);
		select.setOnClickListener(this);

		bt1 = (TextView) findViewById(R.test.text);
		it = new Intent();
		it.setClass(GPS.this, ReceiveCoordinateModule.class);
		clear.setOnClickListener(this);

		Log.e("ON CREATE", "...");
		startService(new Intent(GPS.this, ReceiveCoordinateModule.class));
		Log.e("start service", "...");

		new Thread(new Runnable() {

			@Override
			public void run() {
				// refreshStat(stat);
			}
		}).start();
	}

	private void refreshStat(String[] stat2) {
		openDB();
		stat = dbOpenHelper.getStat(db);
		closeDB();
		//
		tv1.setText("\nx = " + stat[1] + " \ny = " + stat[2]);
		tv2.setText(stat[4] + " (meters)");
		tv3.setText(stat[0]);
		tv4.setText(stat[3] );
		tv5.setText(stat[5] + " kph");
		tv6.setText(stat[6] + " kph");
		tv7.setText(stat[7] + " (meters)");
	}
    
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.start:
			Log.e("START", "...");
			startSrvs();
			break;
		//button stop
		case R.id.stop:
			Log.e("STOP", "...");
			stopService(it);
			break;
		//button select
		case R.id.select:
			Log.e("SELECT", "...");
			openDB();
			Cursor cursor = db.query("coords", null, null, null, null, null,
					null);
			Log.e("Cursor size", "" + cursor.getCount());
			if (cursor.moveToFirst()) {
				do {
					System.out.println(cursor.getString(0) + "  "
							+ cursor.getString(1) + " " + cursor.getString(2)
							+ " " + cursor.getString(3) + " "
							+ cursor.getString(4) + " " + cursor.getString(5));
				} while (cursor.moveToNext());
			}
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			closeDB();
			break;
	   // button clear
		case R.id.clear:
			Log.e("CLEAR", "...");
			openDB();
			db.execSQL("DELETE FROM coords;");
			closeDB();
			break;
		// button refresh
		case R.test.refresh: 
			refreshStat(stat);
			break;
			
		case R.test.send:
			openDB();  
			LinkedList cData = dbOpenHelper.getAllCoordinatesFromDB(db);
			UrlConnector uc = new UrlConnector(cData,getApplicationContext());		
			if(uc.getFlag() == 1){
				dbOpenHelper.deleteFrom(db, "coords");
			}
			closeDB();
			break;
		}
	}
    
	void startSrvs() {
		System.out.println("start");
		startService(it);
	}

	void openDB() {
		dbOpenHelper = new DBConnector(this);
		// open connection
		db = dbOpenHelper.getWritableDatabase();
	}

	void closeDB() {
		// close connection
		db.close();
		dbOpenHelper.close();
	}
}*/
