package com.truck.alexander;



import java.util.LinkedList;

import com.truck.alexander.TabTrip.ProgressThread;
import com.truck.alexander.db.DBConnector;
import com.truck.alexander.modules.ReceiveCoordinateModule;
import com.truck.alexander.modules.ReportsService;
import com.truck.alexander.modules.UrlConnector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PageAuthorization extends Activity {
    /** Called when the activity is first created. */
	Context ct;
	Intent regPage;
	final int CONFIRM  = 0;
	final int REJECT = 1;
	// input fields
	EditText companyET;
	EditText unitET;
	EditText pinET;
	// fields content
	String comp;
	String unit;
	String pin;
	
	//db fields
	SQLiteDatabase db;
	DBConnector dbOpenHelper;
	Button registrBt;
	Button confirmBt;
	
	ProgressThread mProgressThread;
	ProgressDialog mProgressDialog;
	private ProgressDialog pd;
	public boolean sendResult = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pageauthorization);
        // Buttons
        confirmBt = (Button)findViewById(R.auth1.confirm);
        registrBt = (Button)findViewById(R.auth1.reg);
        // Edit fields
        companyET = (EditText)findViewById(R.auth1et.companyid);
        unitET = (EditText)findViewById(R.auth1et.unitid);
        pinET = (EditText)findViewById(R.auth1et.unitpin);
        ct = this;
        regPage = new Intent();
        regPage.setClass(getApplicationContext(), PageRegistration.class);
        confirmBt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// show dialog messages CONFIRM/REJECT
				// bad data
				if(!checkInputData()) {
					//showDialog(REJECT);
					showMessage(REJECT);
					return;
				}
				// data is correct
				// send data it server
				
				pd = ProgressDialog.show(ct, "Please wait...", "Binding vehicle with server...", true,
		                false);
				Thread thread =  new Thread(null, sendData);
		        thread.start();
				/*if(sendDataToServer()){
					openDB();
                    dbOpenHelper.insertInto(db, "auth" , "\""+comp+"\", \""+unit+"\", \""+pin+"\", \"vehicle\"");
                    closeDB();
					//showDialog(CONFIRM);
                    showMessage(CONFIRM);
				}*/
			}

		});
        registrBt.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),PageRegistration.class));
			}
		});
    }
    
    int resultCode;
	private Runnable sendData = new Runnable() {
		@Override
		public void run() {
			try {
				UrlConnector uc = new UrlConnector(comp, unit, pin);
				resultCode = uc.sendData(1);

				switch (resultCode) {
				// data have been sent success
				case 0:
					sendResult = true;
					break;
				default:
					sendResult = false;
				}
				refreshHandler.sendEmptyMessage(0);
			} catch (Exception e) {
				refreshHandler.sendEmptyMessage(0);
			}
		}
	};

	private Handler refreshHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (sendResult) {
				openDB();
				dbOpenHelper.insertInto(db, "auth", "\"" + comp + "\", \""
						+ unit + "\", \"" + pin + "\", \"vehicle\"");
				closeDB();
				// showDialog(CONFIRM);
				showMessage(CONFIRM);
			} else
				toast(ErrorsTypes.getErrorDiscr(resultCode));
			pd.dismiss();
		}
	};

	
/*	private boolean sendDataToServer() {
		
		UrlConnector uc = new UrlConnector(comp,unit,pin);
		int resultCode = uc.sendData(1);
		
	    switch(resultCode){
	    // data have been sent success
	    case 0:
	    	return true;
	    default:
	    	toast(ErrorsTypes.getErrorDiscr(resultCode));
	    	return false;
	    }
	}
   */
	void toast(String msg){
		Toast.makeText(ct, msg+" occured", Toast.LENGTH_SHORT).show();
	}
    /*
     * send typed data to server
     * receive response
     * show response result
     * if it correct, offer save it
     */
    private boolean checkInputData() {
		 comp = companyET.getText().toString();
		 unit = unitET.getText().toString();
		 pin = pinET.getText().toString();
		// if typed correct data 
		if(comp.trim().length() == 0 || unit.trim().length() == 0 || pin.trim().length() == 0 ){
			return false;
		}
		return true;
	}
    
    /*
     * offer to save data into DB and bound it with client
     */
    void showMessage(int flag){
    	//flag == 0 if data is correct
    	//flag == 1 otherwise
    	AlertDialog.Builder alertbox = new AlertDialog.Builder(ct);
		
		switch(flag){
		case CONFIRM:
			alertbox.setTitle("Confirm");
			alertbox.setMessage("Now you need bind drivers to start app");
		/*
		 * on click YES
		 */
		alertbox.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						//bound truck with client
						//write options into DB
						
                        startActivityForResult(new Intent(getApplication(),PageAuthorization2.class),0);
                        //into has been bounded, go to driver authorization page
                       
							//startActivity(new Intent(getApplicationContext(), PageAuthorization2.class));
							//finish();
					}
				});
		alertbox.setNegativeButton("Later", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				//bound truck with client
				//write options into DB
                PageAuthorization.this.finish();
                //into has been bounded, go to driver authorization page
               
					//startActivity(new Intent(getApplicationContext(), PageAuthorization2.class));
					//finish();
			}
		});
		/*
		 * on click NOkj 
		 */
		/*alertbox.setNeutralButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				// dont save options
			}
		});*/
		break;
		
		case REJECT:
			alertbox.setTitle("Error");
			alertbox.setMessage("You are input not correct data");
			alertbox.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							// do nothing
						}
					});
		}
		try {
			alertbox.show();
		} catch (Exception e) {
			Log.e("ERROR","activity leak");
		}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	PageAuthorization.this.finish();
    	super.onActivityResult(requestCode, resultCode, data);
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
}