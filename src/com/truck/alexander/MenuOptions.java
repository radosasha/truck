package com.truck.alexander;

import com.truck.alexander.db.DBConnector;
import com.truck.alexander.modules.UrlConnector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MenuOptions extends Activity{
	
		
	Context ct;	
	// db fields
	SQLiteDatabase db;
	DBConnector dbOpenHelper;
	
	//CONSTANTS 
	final static int OPTIONS_OK = 10;
	
	// buttons and text fields
	Button unitBt;
	Button bindD1;
	Button bindD2;
	Button cont;
	TextView unitTV;
	TextView companyTV;
	TextView drTV1;
	TextView drTV2;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menuoptions);
		Log.e("onCreate","options");
		// if unit and drivers are bind yet, break this page, go to MAINPAGE
		/*if(canStart()==false & PageMainGUI.calledFromMainGUI == false) {
			startActivity(new Intent(getApplicationContext(),PageMainGUI.class));
			MenuOptions.this.finish();
		}*/
		
		// default
		setResult(StartPage.BAD_RESULT);
		
		ct=this;
		// inic buttons
		unitBt = (Button) findViewById(R.options.bindunit);
		bindD1 = (Button) findViewById(R.options.binddriver1);
		bindD2 = (Button) findViewById(R.options.binddriver2);
		cont = (Button) findViewById(R.options.continue2);
		// inic text fields
		unitTV = (TextView) findViewById(R.options.currunit);
		companyTV = (TextView) findViewById(R.options.company);
		drTV1 = (TextView) findViewById(R.options.currdriver1);
		drTV2 = (TextView) findViewById(R.options.currdriver2);

		/*// set click listeners
		unitBt.setOnClickListener(bindUnitListener);
		bindD1.setOnClickListener(bindDriverListener);
		bindD2.setOnClickListener(bindDriverListener);*/
		cont.setOnClickListener(continueListener);

		// refresh buttons listeners/clickable and text fields
		refresh();
	}

	/*private boolean canStart() {
		openDB();
		Cursor cs = dbOpenHelper.select(db, "auth");
		int count = cs.getCount();
		cs.close();
		closeDB();
		if(count >= 2) return false;
		return true;
	}*/

	/*
	 * немножко говнокода
	 */
	public void refresh() {
		openDB();
		// select vehicle rows
		String sqlVehicle = "select * from auth where field4 = \"vehicle\"";
		Cursor cr = db.rawQuery(sqlVehicle, null);
		// 
		String unit =    "not bind yet";
		String company = "not bind yet";
		String driver1 = "not bind yet";
		String driver2 = "not bind yet";
		
		switch(cr.getCount()){
		case 0:
			if(cont.isClickable())cont.setClickable(false);	
			//cont.setText("Continue");
			setButtons("Bind Unit", "Bind Driver #1", "Bind Driver #2");
			setListeners(bindUnitListener,bindDriverListener,bindDriverListener);
			break;
		case 1:
			cr.moveToFirst();
			company = cr.getString(0);
			unit = cr.getString(1);
			// select drivers
			String sqlDrivers = "select * from auth where field4 = \"driver\"";
			Cursor drivers = db.rawQuery(sqlDrivers, null);
			Log.e("driver row count", drivers.getCount()+"");
			switch(drivers.getCount()){
			case 0:
				if(cont.isClickable())cont.setClickable(false);
				setListeners(unbindUnitListener,bindDriverListener,bindDriverListener);
				setButtons("Unbind",  "Bind Driver #1", "Bind Driver #2");
				break;
			case 1:
				drivers.moveToFirst();
				cont.setClickable(true);
				setListeners(unbindUnitListener,unbindDriver1Listener,bindDriverListener);
				setButtons("Unbind",  "Unbind", "Bind Driver #2");
				driver1 = drivers.getString(1);
				break;
			case 2:
				cont.setClickable(true);
				setListeners(unbindUnitListener,unbindDriver1Listener,unbindDriver2Listener);
				setButtons("Unbind",  "Unbind", "Unbind");
				drivers.moveToNext();
				driver1 = drivers.getString(1);
				drivers.moveToNext();
				driver2 = drivers.getString(1);
			}
			drivers.close();
			break;
		/*case 2:
			//if(cont.isClickable())cont.setClickable(false);
			cont.setClickable(true);
			// company & unit
			cr.moveToFirst();
			company = cr.getString(0);
			unit = cr.getString(1);
			// driver 1
			cr.moveToNext();
			driver1 = cr.getString(1);
			setListeners(unbindUnitListener,unbindDriver1Listener,bindDriverListener);
			setButtons("Unbind", "Unbind", "Bind Driver #2");
			break;
		case 3:			
			cont.setClickable(true);
			//setBackOrContinueButton();
			// company & unit
			cr.moveToFirst();
			company = cr.getString(0);
			unit = cr.getString(1);
			// driver 1
			cr.moveToNext();
			driver1 = cr.getString(1);
			//driver 2			
			cr.moveToNext();
			driver2 = cr.getString(1);
			setListeners(unbindUnitListener,unbindDriver1Listener,unbindDriver2Listener);
			setButtons("Unbind", "Unbind", "Unbind");
			*/
		}
		cr.close();
		
		closeDB();
		setTextFields(unit, company, driver1, driver2);
	}
	
	/*private void setBackOrContinueButton() {
		if(PageMainGUI.calledFromMainGUI){
			cont.setText("Back");
			cont.setOnClickListener(new View.OnClickListener() {				
				@Override
				public void onClick(View v) {
					MenuOptions.this.finish();
				}
			});
		}
		else{
			cont.setText("Continue");
			cont.setOnClickListener(continueListener);
		}
	}*/

	void setListeners(View.OnClickListener l1,View.OnClickListener l2, View.OnClickListener l3){
		unitBt.setOnClickListener(l1);
		bindD1.setOnClickListener(l2);
		bindD2.setOnClickListener(l3);
	}
	
	private void setButtons(String string, String string2, String string3) {
		unitBt.setText(string);
		bindD1.setText(string2);
		bindD2.setText(string3);
	}

	void setTextFields(String t1,String t2, String t3,String t4){
		unitTV.setText("Current Unit: "+t1);
		companyTV.setText("Current Company: "+t2);
		drTV1.setText("Current Driver #1: "+t3);
		drTV2.setText("Current Driver #2: "+t4);
	}
	
	View.OnClickListener bindUnitListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			startActivityForResult(new Intent(getApplication(),PageAuthorization.class),0);
		}
	};
	
	View.OnClickListener unbindUnitListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			AlertDialog.Builder alertbox = new AlertDialog.Builder(ct);
			alertbox.setTitle("Warning");
			alertbox.setMessage("This action clear all \"binds\" include drivers");
			/*
			 * on click YES
			 */
			alertbox.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							openDB();
							dbOpenHelper.sqlRequest(db, "DELETE from auth");
							closeDB();
							//refresh display
							refresh();
						}
					});
			alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
						}
					});
			alertbox.show();
		}
	};

	
	View.OnClickListener bindDriverListener = new View.OnClickListener() {	
		@Override
		public void onClick(View v) {
			if(dbSize() == 0){
				toast("You need to bind \"Unit\" before");
				return;
			}
			startActivityForResult(new Intent(getApplication(),PageAuthorization2.class),0);
		}
	};
	
	/*View.OnClickListener bindDriverListener = new View.OnClickListener() {	
		@Override
		public void onClick(View v) {
			startActivityForResult(new Intent(getApplication(),PageAuthorization2.class),0);
		}
	};*/
	
	View.OnClickListener unbindDriver1Listener = new View.OnClickListener() {	
		@Override
		public void onClick(View v) {
			openDB();
			Cursor cs = dbOpenHelper.select(db, "auth");
			if(cs.getCount()>2){
				toast("You need to remove driver #2 before");
				closeDB();
				cs.close();
				return;
			}
			if(sendDataToServer(1))dbOpenHelper.sqlRequest(db, "DELETE FROM auth where field1 = 'driver1' ;");
			cs.close();
			closeDB();
			refresh();
			return;
		}
	};
	private boolean sendDataToServer(int position) {
		//sendData type == 3 -  sending dr1/2 dr1/2 pin, company, unit
		//openDB(); - already open
		Cursor cr = dbOpenHelper.select(db, "auth");	
		cr.moveToFirst();
		String company = cr.getString(0);
		String unit = cr.getString(1);
		String pin= cr.getString(2);
		// position(1) = driver1
		// position(2) = driver2
		cr.moveToPosition(position);
		UrlConnector uc = new UrlConnector(company,unit,pin, cr.getString(1), cr.getString(2));
		cr.close();
		//closeDB(); -- close soon...
		int resultCode =  uc.sendData(3);
		
	    switch(resultCode){
	    // data have been sent success
	    case 0:
	    	Log.e("Result code",""+resultCode);
	    	return true;
	    default:
	    	Log.e("Result code",""+resultCode);
	    	toast(ErrorsTypes.getErrorDiscr(resultCode));
	    	return false;
	    }
	}
	View.OnClickListener unbindDriver2Listener = new View.OnClickListener() {	
		@Override
		public void onClick(View v) {
			openDB();
			if(sendDataToServer(1))dbOpenHelper.sqlRequest(db, "DELETE FROM auth where field1 = 'driver2' ;");			
			closeDB();
			refresh();
		}
	};
	
	View.OnClickListener continueListener = new View.OnClickListener() {	
		@Override
		public void onClick(View v) {
			/*startActivity(new Intent(getApplication(), PageMainGUI.class));
			MenuOptions.this.finish();*/
			Intent msg = new Intent();
			setResult(OPTIONS_OK);
			MenuOptions.this.finish();
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		refresh();
	};
	
	
	
	private int dbSize() {
		openDB();
		Cursor cr = dbOpenHelper.select(db, "auth");
		int res = cr.getCount();
		cr.close();
		closeDB();
		return res;		
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
	
	private void toast(String msg) {
		Toast.makeText(ct, msg , Toast.LENGTH_SHORT).show();
	}
}