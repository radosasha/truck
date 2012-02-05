package com.truck.alexander.modules;

import com.truck.alexander.db.DBConnector;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class ReportsService extends Service{
	static boolean stopThread = true;
	Context ct;
	public static String currTripName;
	public static String lastSendedTime = "Never sended";
	ReportSender rs;
	//db fields
	SQLiteDatabase db;
	DBConnector dbOpenHelper;
	// thread
	Thread start = null;
	Thread send = null;
	//time constants
	public final int SECOND = 1000;
	public final int MINUTE = SECOND*60; // send interval
	
	void writeStatus(){
		openDB();
		db.execSQL("");
		closeDB();
	}
	void readStatus(){
		openDB();
		Cursor cr = db.rawQuery("", null);
		closeDB();
	}
	
	@Override
	public void onCreate() {	
		super.onCreate();
		ct = this;
		//start sender thread
		Log.e("Report Service","on create");
		stopThread = true;
		start = new Thread(new DataSenderThread());
		start.setDaemon(true);
		start.start();
		
	}
	
	class DataSenderThread implements Runnable {
		@Override
		public void run() {
			
			openDB();
			Cursor cr = db.rawQuery("select * from tripinfo", null);
			cr.moveToFirst();
			Log.e("row count",""+cr.getCount());
			Log.e("Thread","started");
			
			 rs = new ReportSender(ct);
			// send "start" command if need
			 currTripName = cr.getString(0);
			 int startStatus = cr.getInt(6);
			 cr.close();
			 closeDB();
			if(startStatus == 0){
				while(true){
					if ( sendStartCommand() )break;
					// bad aattempt, repeat after 10 minutes
					sleepThread(MINUTE*10);
				}
			}
			
			while(stopThread == true){
				switch(rs.sendCoors(ReportsService.currTripName)){
				case 0:
					sleepThread(10*MINUTE);
					break;
					default: 
						sleepThread(10*MINUTE);
				}				
			}
		}

		private void sleepThread(long d) {
			try {
				Log.e("Thread sleep...","  Repeat after "+(d/SECOND)+" seconds");
				Thread.sleep(d);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/*
		 * send "start" command to server
		 */
		public boolean sendStartCommand() {
		    /*
		     * send "start" command
		     */
			Log.e("Start command", "try to send");
			if (rs.sendStart(0,0))
				return true;
			return false;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private void openDB() {
		dbOpenHelper = new DBConnector(this);
		// open connection
		db = dbOpenHelper.getWritableDatabase();
	}

	private void closeDB() {
		// close connection
		db.close();
		dbOpenHelper.close();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("Repost Service","on destroy");
		if(db != null) closeDB();
		Log.e("Error","stop thread error");
		stopThread = false;
	}
}