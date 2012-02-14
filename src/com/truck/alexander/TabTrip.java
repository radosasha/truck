package com.truck.alexander;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.truck.alexander.addedit.PageNewTrip;
import com.truck.alexander.addedit.PageNewTripEdit;
import com.truck.alexander.db.DBConnector;
import com.truck.alexander.modules.ReceiveCoordinateModule;
import com.truck.alexander.modules.ReportSender;
import com.truck.alexander.modules.ReportsService;
import com.truck.alexander.modules.UrlConnector;
import com.truck.tools.DateTools;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Path.FillType;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TabTrip extends Activity{
	
	protected static final int BIND_UNIT_AND_DRIVER = 0;
	protected static final int START_APP = 1;
	protected static final int GPS_CHECKER = 2;
	private   static final int DEFAULT = -1;
	
	
	/*
	 * test fields
	 */
	Context ct;
	UrlConnector uc = null;
	TextView tv1; // last coord
	TextView tv2; // accuracy
	TextView tv3; // coord count
	TextView tv4; // curr address
	TextView tv5; // curr speed
	TextView tv6; // aver speed
	TextView tv7; // aver distance
	TextView tv8; // get coars freq
	TextView trace;
	Button refresh;
	Button showdb;
	EditText dbEdit;
	Geocoder gcd;
	List<Address> addresses;
	private ProgressDialog pd;

	final int OPTIONS_OK = 10;
	final int IDD_PROGRESS = 0;
	ProgressThread mProgressThread;
	ProgressDialog mProgressDialog;
	static int count = 0;
	
	ProgressThread mProgressThreadReport;
	ProgressDialog mProgressDialogReport;
	private ProgressDialog pdReport;

	
	
	// test fields
	SQLiteDatabase db;
	DBConnector dbOpenHelper;
	String[] stat;
	
	//
	Vibrator vibra;
	Intent runProgramm;
	Button btStartEdit ;
	Button btPauseResume;
	Button btStop;
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.e("ON CONFIGURATION_CHANGED","...");
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		
		ct = this;
		runProgramm = new Intent(TabTrip.this, ReceiveCoordinateModule.class);
		vibra = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		Log.e("Trip ","ON CREATE");
		/* Trip Tab Content */ 
		setContentView(R.layout.tabtrip);
		
		btStartEdit = (Button) findViewById(R.trip.STARTEDIT);
		btPauseResume = (Button) findViewById(R.trip.PAUSERESUME);
		btStop = (Button) findViewById(R.trip.STOP);
		//set new button listeners 
		showdb = (Button) findViewById(R.test.showDB);
		dbEdit = (EditText)findViewById(R.test.showtext);
		dbEdit.setVisibility(4);
		showdb.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				openDB();
				Cursor cr = db.rawQuery("select * from "+dbEdit.getText().toString(), null);
				while(cr.moveToNext()){
					String rep = "";
					for(int y = 0;y<cr.getColumnCount();y++){
						rep += cr.getString(y)+" ";
					}
					//double x = cr.getDouble(0);
					//double y = cr.getDouble(1);
					Log.e("row "+cr.getPosition(), rep);
					//Log.e("row "+cr.getPosition(), x+" "+y);
				}
				cr.close();
				closeDB();
			}
		});
		showdb.setClickable(false);
		showdb.setVisibility(4);
		refreshButtonListeners();		
		
		// statistic fields
		tv1 = (TextView) findViewById(R.test.lastcoord2);
		tv2 = (TextView) findViewById(R.test.coordacc2);
		tv3 = (TextView) findViewById(R.test.coordcount2);
		tv4 = (TextView) findViewById(R.test.address2);
		tv5 = (TextView) findViewById(R.test.currspeed2);
		tv6 = (TextView) findViewById(R.test.summspeed2);
		tv7 = (TextView) findViewById(R.test.trip2);
		tv8 = (TextView) findViewById(R.test.freq);
		trace = (TextView) findViewById(R.test.trace);
		refresh = (Button) findViewById(R.test.refresh);
		refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					synchronized (DBConnector.synchList) {
						if(!PageMainGUI.TRIP_STARTED) {
							Toast.makeText(ct, "Refresh error  (position3)", Toast.LENGTH_SHORT);
							return;
						}
							refreshStat();
					}
				} catch (Exception e) {
					toast("Refresh error");
				}
			}
		});
		
		/*send = (Button) findViewById(R.test.send);
		send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub		
//uncomment
				//send();
//uncomment
				//showDialog(IDD_PROGRESS);
			}			
		});*/
	}
	
	private void refreshStat() throws Exception{
		try{
		gcd = new Geocoder(this);		
		pd = ProgressDialog.show(ct, "Please wait...", "Refreshing data...", true,
                false);
		Thread thread =  new Thread(null, refreshData);
        thread.start();
		}
		catch(Exception e){
			Log.e("ERROR","on refresh error");
		}
	}
		
	
	String locality = "Need GPS data";
	String countryName = "...";
	private Runnable refreshData = new Runnable() {			
		@Override
		public void run() {
			try{
    		openDB();
    		stat = dbOpenHelper.getStat(db);
    		try {
    			addresses = gcd.getFromLocation(Double.parseDouble(stat[1]), Double.parseDouble(stat[2]), 1);
    		} catch (Exception e) {
    		    //zaglushka
    			addresses = new LinkedList();
    			toast("Can't show current place. No connection.");
    			locality = "Some error occured";
    			countryName = "...";
    			//e.printStackTrace();
    		}
    		// if internet connection established
    		if (addresses.size() > 0){ 
    			locality ="City: "+addresses.get(0).getLocality()+"\n";
    			countryName ="State: "+addresses.get(0).getAdminArea()+"\nCountry: "+ addresses.get(0).getCountryName();
    		    Log.e("getLocality",locality);
    		    Log.e("getCountryName",countryName);
    		}
    		closeDB();
			//Done! now continue on the UI thread        
    		/*Message ms = new Message();
    		Bundle bndl = new Bundle();
    		bndl.putString("data", locality+", "+countryName);
    		ms.setData(bndl);
			refreshHandler.sendMessage(ms);	   */
			sendMessage();
		}		
		catch(Exception e){
			toast("Refresh error (position3)");
			sendMessage();
		}
		}
	};	
	
	void sendMessage(){
		Message ms = new Message();
		Bundle bndl = new Bundle();
		bndl.putString("data", countryName+", "+locality);
		ms.setData(bndl);
		refreshHandler.sendMessage(ms);	      
	}
	
	
	private Handler refreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
    		//
    		//tv1.setText("\nx = " + stat[1] + " \ny = " + stat[2]);
        	tv1.setText(ReportsService.lastSendedTime);
    		tv2.setText(stat[4] + " (meters)");
    		tv3.setText(stat[0]);
    		//tv4.setText(stat[3] );
    		tv4.setText(locality+", "+ countryName);
    		//tv4.setText(msg.getData().getString("data"));
    		tv5.setText(stat[5] + " kph");
    		tv6.setText(stat[6] + " kph");
    		tv7.setText(stat[7] + " (meters)");
    		tv8.setText("Частота получения коорд.:   "+(ReceiveCoordinateModule.receiveFrequency/(1000*60)+" min")); 
            pd.dismiss();                
        }
};


protected Dialog onCreateDialog(int id) {
    switch(id) {
    case IDD_PROGRESS:
    	Log.e("Progress","create");
        mProgressDialog = new ProgressDialog(TabTrip.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //mProgressDialog.setMax(1400);
        //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setMessage("Sending data...");
        mProgressThread = new ProgressThread(sendDandler);
        mProgressThread.start();
        return mProgressDialog;
    default:
        return null;
    }
}

	// Define the Handler that receives messages from the thread and update the
	// progress
	final Handler sendDandler = new Handler() {
		public void handleMessage(Message msg) {
			int total = msg.getData().getInt("total");
			mProgressDialog.setProgress(total);
			if (total >= count) {
				// dismissDialog(IDD_PROGRESS);
				removeDialog(IDD_PROGRESS);
				mProgressThread.setState(ProgressThread.STATE_DONE);
				Toast.makeText(getApplicationContext(),
						count + " coordinates have benn sent success!",
						Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
				openDB();
				dbOpenHelper.deleteFrom(db, "coords");
				closeDB();
			}
		}
	};

class ProgressThread extends Thread {
	
    Handler mHandler;
    final static int STATE_DONE = 0;
    final static int STATE_RUNNING = 1;
    int mState;
    int mTotal;
   
    ProgressThread(Handler h) {
        mHandler = h;
    }
   
    public void run() {
        mState = STATE_RUNNING;   
        mTotal = 0;
        openDB(); 
		LinkedList cData = dbOpenHelper.getAllCoordinatesFromDB(db,ReportsService.currTripName);
		count = cData.size();
		mProgressDialog.setMax(count);
		closeDB(); 
		uc = new UrlConnector(cData,ct,mHandler);
		}

		public void setState(int state) {
			mState = state;
		}
	}

// FIX
// fields START INFO, STOP INFO, sendstatus
	private void saveReportOnClient( int odomEnd, String dateEnd) {
		
		Cursor cursor = dbOpenHelper.select(db, "tripinfo");
		cursor.moveToFirst();
		String sql = "INSERT INTO reports VALUES(" + "'" + 
				cursor.getString(0)	+ "' , '" + // trip id
				cursor.getString(1) + "' , " + // date start
				cursor.getInt(2) + " , '" + // odometer start
				cursor.getString(3) + "' , '" + // destination
				cursor.getString(4) + "' , " + // memo
				cursor.getString(6) + " , " + // send status
				odomEnd + " , '" + 
				dateEnd + "', null)";
		cursor.close();
		db.execSQL(sql);
		Log.e("send report later", sql);
	}
	
	
	void refreshButtonListeners() {
		// set onCliclListeners

		// if application have been STARTED, set button "EDIT"
		// else set button "START
		openDB();
		switch (dbOpenHelper.isDbNull(db)) {
		// 0 - null
		// 1 - not null
		case 0:// start button
			// set "START" button and "PAUSE" button as default
			btStartEdit.setText("START");
			btStartEdit.setOnClickListener(startListener); // start listener
			// set start flag true
			PageMainGUI.TRIP_STARTED = false;
			btPauseResume.setText("PAUSE");
			btPauseResume.setOnClickListener(null);
			btStop.setOnClickListener(null);

			break;
		case 1:// edit button
			// set "EDIT" button as default
			btStartEdit.setText("EDIT");
			btStartEdit.setOnClickListener(editListener);
			// set start flag true
			PageMainGUI.TRIP_STARTED = true;
			PageMainGUI.tripid = dbOpenHelper.getValueByIndex(db, 0);
			String status = dbOpenHelper.getValueByIndex(db, 5);// 5 - "status"
																// index
			if(!isModuleServiceStarted("com.truck.alexander.modules.ReportsService"))startService(new Intent(TabTrip.this,ReportsService.class));
			// set listener on "PAUSE/RESUME" button
			if (status.equals("pause")) {
				// pause
				btPauseResume.setOnClickListener(pauseListener);
				btPauseResume.setText("PAUSE");
				if(!isModuleServiceStarted("com.truck.alexander.modules.ReceiveCoordinateModule"))startService(runProgramm);
			} else {
				// resume
				btPauseResume.setOnClickListener(resumeListener);
				btPauseResume.setText("RESUME");
				if(isModuleServiceStarted("com.truck.alexander.modules.ReceiveCoordinateModule"))stopService(runProgramm);
			}
			btStop.setOnClickListener(stopListener);
			// start receive module if it stop later
			break;
		}
		closeDB();
	}	
	
	private boolean isModuleServiceStarted(String moduleName) {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (moduleName.equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	View.OnClickListener startListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// call New Trip page,
			// send empty HashMap to fill it
			Log.e("BUTTON", "START");
			//
			if(!getGPSStatus()){
				showDialogMessage(
						"To start APP you need  turn GPS on",
						"Select item", "Turn GPS on", "Cancel", GPS_CHECKER);
			}
			else{
				offerToStartApp();
			}
		}
	};
	
	
	void offerToStartApp(){
		if (canStart()) {
			Intent startEditIntent = new Intent(TabTrip.this,
					PageNewTrip.class);
			startActivityForResult(startEditIntent, 1);
			// set start flag true
			
		} else {
			showDialogMessage(
					"To start you need to bind Unit and Drivers to the client",
					"Do it now ?", "Yes", "Cancel", BIND_UNIT_AND_DRIVER);
			// showMessage();
		}
	}
	
	/*
	 * shows dialog messages to user
	 */
	void showDialogMessage(String title, String message, String but1, String but2, final int number){
		AlertDialog.Builder alertbox = new AlertDialog.Builder(ct);
		alertbox.setTitle(title);
		alertbox.setMessage(message);
		alertbox.setPositiveButton(but1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				switch(number){
				case GPS_CHECKER:
					turnGPSOn();
					offerToStartApp();
					break;
				/*
				 * offer to bind unit and drivers
				 * deliver to options page
				 */
				case BIND_UNIT_AND_DRIVER:
					startActivityForResult(new Intent(getApplicationContext(),MenuOptions.class), 1);
					break;
				/*
				 * offer to fill "start" page
				 */
				case START_APP:
					startActivityForResult(new Intent(TabTrip.this,PageNewTrip.class), 1);
					break;
				default:
				}
			}
		});
		if (!(number == DEFAULT)) {
			alertbox.setNegativeButton(but2,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
						}
					});
		}
		alertbox.show();
	}
	
	/*
	 * shows dialog messages to user
	 */
	int odomVal;
	String currDate;
	void showStopDialog(final int odomVal, final String currDate) {
		Log.e("BUTTON", "STOP");
		this.odomVal = odomVal;
		this.currDate = currDate;
		
		AlertDialog.Builder alertbox = new AlertDialog.Builder(ct);
		alertbox.setTitle("Stop trip");
		alertbox.setMessage("Choose report action");
		alertbox.setPositiveButton("Send to server",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						 pd = ProgressDialog.show(ct, "Please wait...", "Sending report...", true,
					                false);
						Thread thread =  new Thread(null, sendReport);
					    thread.start();
					}
				});

		alertbox.setNeutralButton("Send later",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						//message to user
						eraseClientFromTrip(true);
						refreshButtonListeners();
						Toast.makeText(
								ct,
								"The report have been saved to local database",
								10).show();						
					}
				});
		
		alertbox.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
				});
		

		alertbox.show();
	}
	
	boolean resultCode;
	private Runnable sendReport = new Runnable() {
		@Override
		public void run() {
			try {
				 ReportSender rs = new ReportSender(ct);
				 resultCode = rs.sendCurrentReport(rs,PageMainGUI.tripid,odomVal,currDate);
				 if(resultCode){
					 eraseClientFromTrip(false);							 
					 
					// stopService(runProgramm);
					 //stopService(new Intent(TabTrip.this,ReportsService.class));
					 
					 //openDB();
					// db.execSQL("delete from tripinfo;");
					// closeDB();
					 //clearTabs();
				 }		
				reportHandler.sendEmptyMessage(0);
			} catch (Exception e) {
				Log.e("Tab Trip","sending error occured");
				reportHandler.sendEmptyMessage(0);
			}
		}
	};
	
	private Handler reportHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			refreshButtonListeners();
			if(resultCode){
				Toast.makeText(ct, "Report have been sent success!", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(ct, "Report sending error", Toast.LENGTH_LONG).show();
			}
			pd.dismiss();
		}
	};
	
	
	/*
	 * stop all services, clear trip database 
	 */
	private void eraseClientFromTrip(boolean saveReport) {
		
		openDB();		
		if(saveReport == true)saveReportOnClient(odomVal,currDate);		
		// save to reports if can't send in this moment		
		dbOpenHelper.dropTables(db,PageMainGUI.tripid);		
		dbOpenHelper.deleteFrom(db, "tripinfo");
		closeDB();
		
		// stop receive service
		stopService(runProgramm);
		stopService(new Intent(TabTrip.this, ReportsService.class));		
		// clear flags
		clearFlags();
		clearTabs();
	}
	
	void clearFlags() {
		// set start flag "false"
		PageMainGUI.TRIP_STARTED = false;
		PageMainGUI.tripid = null;
		ReportsService.lastSendedTime = "Never sended";
	}
	
	void clearTabs(){
		if (TabFuel.fillMaps != null)			TabFuel.clearList();
		if (TabCheckPoint.fillMaps != null)			TabCheckPoint.clearList();
		if (TabCost.fillMaps != null)			TabCost.clearList();
	}
	
	/*
	 *@return true if user authorized (vehicle and drivers are binded to client)
	 */
	private boolean canStart() {
		openDB();
		Cursor cs = dbOpenHelper.select(db, "auth");
		int count = cs.getCount();
		cs.close();
		closeDB();
		if (count >= 2)
			return true;
		return false;
	}
	
//    Listener on START button
    View.OnClickListener editListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Log.e("BUTTON", "EDIT");
			Intent startEditIntent = new Intent(TabTrip.this, PageNewTripEdit.class);
					startActivity(startEditIntent);
					refreshButtonListeners();			
		}
	};
	 
//	Listener on STOP button
    View.OnClickListener stopListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			final Dialog dialog = new Dialog(TabTrip.this);
            dialog.setContentView(R.layout.stopodometer);
            dialog.setTitle("Input current odometer value");
            dialog.setCancelable(true);
            final EditText odom = (EditText)dialog.findViewById(R.stop.etodometer);
            TextView date = (TextView)dialog.findViewById(R.stop.date);
            final String currDate = DateTools.getTime();
            date.setText("Current date:\n"+currDate);
			//showStopDialog();
            Button btOkStop = (Button)dialog.findViewById(R.stop.ok);
			btOkStop.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String odomValue = odom.getText().toString();
					if (odomValue.length() == 0) {
						Toast.makeText(ct, "'Odometer' field is emmpty", Toast.LENGTH_SHORT).show();
					} else {
						showStopDialog(Integer.parseInt(odomValue), currDate);
						dialog.cancel();
					}
				}
			});
            
            Button btCancelStop = (Button)dialog.findViewById(R.stop.cancel);
            btCancelStop.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});
            dialog.show();
		}
	};
	
//	listener on PAUSE button
    View.OnClickListener pauseListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			Log.e("BUTTON", "PAUSE");
			stopService(runProgramm);
			openDB();
			dbOpenHelper.setStatus(db,"resume");
			closeDB();
			refreshButtonListeners();	
		}
	};
	
//	Listener on RESUME button
    View.OnClickListener resumeListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			//refreshButtonListeners();	
			Log.e("BUTTON", "RESUME");
            startService(runProgramm);
			openDB();
			dbOpenHelper.setStatus(db,"pause");
			closeDB();
			refreshButtonListeners();	
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("RESULT","HERE");
		
		if(resultCode==RESULT_OK){
			HashMap<String,String> hConfig= (HashMap<String, String>) data.getSerializableExtra("data");
			/*Log.e("RESULT", hConfig.get("tripid"));
			Log.e("RESULT", hConfig.get("date"));
			Log.e("RESULT", hConfig.get("odometer"));
			Log.e("RESULT", hConfig.get("destination"));
			Log.e("RESULT", hConfig.get("memo"));*/
			//btStartEdit.setText("EDIT");		
			/*
			 * put config data into DB
			 */
			openDB();
			 //write trip info data
			 //dbOpenHelper.insertConfig(db, hConfig);
			 String sqlReq = "insert into tripinfo values("+
			                 " '"+hConfig.get("tripid")+"' ,"+
			                 " '"+hConfig.get("date")+"' ,"+
			                      hConfig.get("odometer")+" ,"+
			                 " '"+hConfig.get("destination")+"' ,"+
			                 " '"+hConfig.get("memo")+"' ,"+
			                 " '"+hConfig.get("status")+"' ,"+
			                 "0 "+
			                 " )";
			              //   "0, "+
			              //   "0 )";
			 
			 dbOpenHelper.sqlRequest(db, sqlReq);
			 // create tables "trip", "cost", "checkp" + tripID name
			 String tableName = hConfig.get("tripid");
			 PageMainGUI.tripid = tableName;
			 //create appropriate tables "fuel", "cost", "checkpoint"
			 dbOpenHelper.createReportTables(db,tableName);
			 
			 closeDB();
			 //change button to "EDIT"
			 refreshButtonListeners();
			// run programm 
	    		startService(runProgramm);
	    		//set START flag
	    		
	    		showDialogMessage("Notification", "The application have been started successfully", "Ok", null, DEFAULT);
		}		
		if(resultCode==RESULT_CANCELED){
			toast("\"START\" canceled",Toast.LENGTH_SHORT);
		}
		if(resultCode==OPTIONS_OK){
			//showOkMessage();
			showDialogMessage("Now you can start the app", "Go on ?", "Yes", "Cancel", START_APP);
		}
	}
	
	 /*private boolean isMyServiceRunning() {
		    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
		        if ("com.truck.alexander.modules.ReceiveCoordinateModule".equals(service.service.getClassName())) {
		            return true;
		        }
		    }
		    return false;
		}*/
	
	/*
	 * update tab list content if they already been onCreate called
	 */
	
	/*
	 * update tab, specified in parameters
	 */
	static void updateTabLists(SimpleAdapter adapter, List<HashMap<String, Object>> fillMaps, String tableName, int columnsCount,DBConnector dbOpenHelper,SQLiteDatabase db ){
		if (!(adapter == null)) {
			Cursor cr = dbOpenHelper.select(db, tableName + PageMainGUI.tripid);
			cr.moveToFirst();
			for (int i = 0; i < cr.getCount(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				for (int y = 0; y < columnsCount; y++) {
					map.put("rowid" + y, cr.getString(y));
				}
				fillMaps.add(map);
				cr.moveToNext();
			}
			adapter.notifyDataSetChanged();
			cr.close();
		}
	}
	
	public boolean getGPSStatus() {
		try {
			String allowedLocationProviders = Settings.System.getString(
					ct.getContentResolver(),
					Settings.System.LOCATION_PROVIDERS_ALLOWED);
			return allowedLocationProviders
					.contains(LocationManager.GPS_PROVIDER);
		} catch (Exception e) {
			return false;
		}
	}
	 
	public boolean turnGPSOn() {
		String provider = Settings.Secure.getString(getContentResolver(),
				Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		if (!provider.contains("gps")) {
			final Intent poke = new Intent();
			poke.setClassName("com.android.settings",
					"com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			sendBroadcast(poke);
			return true;
		}
		return false;
	}
	/*
	 * Shows text message
	 */
	void toast(String msg, int howLong){
		Toast tst= Toast.makeText(this, msg, howLong);
		//tst.setGravity(Gravity.CENTER, 0, 0);
		tst.show();
	}
		
	/*
	 *  test methods
	 */
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
	
	void toast(String msg){
		//Toast.makeText(ct, msg, Toast.LENGTH_SHORT).show();
	}
	
		
}



