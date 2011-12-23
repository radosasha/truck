package com.truck.alexander;

import java.util.HashMap;

import com.truck.alexander.db.DBConnector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TabTrip extends Activity{
	
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
        
		Log.e("Trip ","ON CREATE");
		/* Trip Tab Content */ 
		setContentView(R.layout.tabtrip);
		
		btStartEdit = (Button) findViewById(R.trip.STARTEDIT);
		btPauseResume = (Button) findViewById(R.trip.PAUSERESUME);
		btStop = (Button) findViewById(R.trip.STOP);
		//set new button listeners 
		refreshButtonListeners();		
	}
	
	
	void refreshButtonListeners(){
		// set onCliclListeners
								
				//if application have been STARTED, set button "EDIT"
		        //else set button "START
		        DBConnector dbOpenHelper = new DBConnector(TabTrip.this);
			     // open connection
				 SQLiteDatabase  db = dbOpenHelper.getWritableDatabase(); 
		        switch(dbOpenHelper.isDbNull(db)){
		        //0 - null
		        //1 - not null
		        case 0://start button
		       	 //set "START" button and "PAUSE" button as default
		        	btStartEdit.setText("START"); 
		        	btStartEdit.setOnClickListener(startListener); // start listener
		        	btPauseResume.setText("PAUSE"); 
		        	btPauseResume.setOnClickListener(pauseListener);
		       	 break;
		        case 1://edit button
		       	//set "EDIT" button as default
		        	btStartEdit.setText("EDIT");
		        	btStartEdit.setOnClickListener(editListener);
		        	String status = dbOpenHelper.getValueByIndex(db,7);// 7 - "status" index\
		        	//set listener on "PAUSE/RESUME" button
		        	if(status.equals("pause")){
		        		//pause
		        		btPauseResume.setOnClickListener(pauseListener);
		        		btPauseResume.setText("PAUSE");
		        	}
		        	else{
		        		//resume
		        		btPauseResume.setOnClickListener(resumeListener);
		        		btPauseResume.setText("RESUME");
		        	}
		       	 break;
		        }
				btStop.setOnClickListener(stopListener);		
				dbOpenHelper.close();
				db.close();
	}
	
	View.OnClickListener startListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// call New Trip page,
			// send empty HashMap to fill it
			Log.e("BUTTON", "START");
			Intent startEditIntent = new Intent(TabTrip.this, PageNewTrip.class);
			startActivityForResult(startEditIntent, 1);
			
		}
	};
	
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
			Log.e("BUTTON", "STOP");
		}
	};
	
//	listener on PAUSE button
    View.OnClickListener pauseListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			//refreshButtonListeners();	
			Log.e("BUTTON", "PAUSE");
		}
	};
	
//	Listener on RESUME button
    View.OnClickListener resumeListener = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			//refreshButtonListeners();	
		}
	};

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("RESULT","HERE");
		
		if(resultCode==RESULT_OK){
			HashMap<String,String> hConfig= (HashMap<String, String>) data.getSerializableExtra("data");
			Log.e("RESULT", hConfig.get("tripid"));
			Log.e("RESULT", hConfig.get("date"));
			Log.e("RESULT", hConfig.get("odometer"));
			Log.e("RESULT", hConfig.get("destination"));
			Log.e("RESULT", hConfig.get("memo"));
			//btStartEdit.setText("EDIT");		
			
			/*
			 * put config data into DB
			 */
			 DBConnector dbOpenHelper = new DBConnector(TabTrip.this);
		     // open connection
			 SQLiteDatabase  db = dbOpenHelper.getWritableDatabase(); 
			 //write data
			 dbOpenHelper.insertConfig(db, hConfig);	
			 dbOpenHelper.close();
			 db.close();
			 //change button to "EDIT"
			 refreshButtonListeners();
		}		
		
		if(resultCode==RESULT_CANCELED){
			toast("\"START\" canceled",Toast.LENGTH_SHORT);
		}
	}
	
	void toast(String msg, int howLong){
		Toast tst= Toast.makeText(this, msg, howLong);
		//tst.setGravity(Gravity.CENTER, 0, 0);
		tst.show();
	}
}


