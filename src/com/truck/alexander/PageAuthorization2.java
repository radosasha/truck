package com.truck.alexander;



import com.truck.alexander.TabTrip.ProgressThread;
import com.truck.alexander.db.DBConnector;
import com.truck.alexander.modules.UrlConnector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PageAuthorization2 extends Activity {
    /** Called when the activity is first created. */
	Context ct;
	Intent regPage;
	
	final int CONFIRM  = 0;
	final int REJECT = 1;
	// butoons 
	Button confirm;
    Button cancel;
	// input fields
	EditText dr1;
	EditText dr1pin;
	EditText dr2;
	EditText dr2pin;
	// fields content
	String dr1str;
	String dr1pinstr;
	String dr2str;
	String dr2pinstr;
	
	//db fields
	SQLiteDatabase db;
	DBConnector dbOpenHelper;
	ProgressThread mProgressThread;
	ProgressDialog mProgressDialog;
	private ProgressDialog pd;
	public boolean sendResult = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pageauthorization2);
        
		// Buttons
        confirm = (Button) findViewById(R.auth2.confirm);
		Button cancel = (Button) findViewById(R.auth2.cancel);

		// Edit fields
		dr1 = (EditText) findViewById(R.auth2et.dr1);
		dr1pin = (EditText) findViewById(R.auth2et.dr1pin);
		dr2 = (EditText) findViewById(R.auth2et.dr2);
		dr2pin = (EditText) findViewById(R.auth2et.dr2pin);

		//
		inicTextFields();
		// current context
		ct = this;
		
        confirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				confirm.setClickable(false);
				// show dialog messages CONFIRM/REJECT
				// bad data
				if(!checkInputData()) {
					confirm.setClickable(true);
					//showDialog(REJECT);
					return;
				}
				// data is correct
				// send data ti server
				pd = ProgressDialog.show(ct, "Please wait...", "Binding drivers with server...", true,
		                false);
				Thread thread =  new Thread(null, sendData);
		        thread.start();
		        
				/*if(sendDataToServer()){
					//showDialog(CONFIRM);
			    	showMessage(CONFIRM);
				}*/
			}

		});
        cancel.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				PageAuthorization2.this.finish();
			}
		});
    }
    

	private void inicTextFields() {
		openDB();
		//Cursor cr = dbOpenHelper.select(db, "auth");
		String sqlDrivers = " select * from auth where field4 = \"driver\"";
		Cursor cr = db.rawQuery(sqlDrivers, null);
		switch(cr.getCount()){
		case 0:
			Log.e("FIELD INIC","0 ROW");
			break;
		case 1:
			Log.e("FIELD INIC","1 ROW");
			cr.moveToFirst();
			dr1.setText(cr.getString(1));
			dr1pin.setText(cr.getString(2));
			break;
		case 2:
			Log.e("FIELD INIC","2 ROW");
			cr.moveToFirst();
			dr1.setText(cr.getString(1));
			dr1pin.setText(cr.getString(2));
			cr.moveToNext();
			dr2.setText(cr.getString(1));
			dr2pin.setText(cr.getString(2));			
			break;
		}
		cr.close();
		closeDB();
	}
	
	int resultCode;
	private Runnable sendData = new Runnable() {
		@Override
		public void run() {
			try {
				//sendData type == 2 - second steP, sending dr1 id, dr2 id, dr1 pin, dr2 pin, company, unit
				openDB();
				Cursor cr = db.rawQuery("select * from auth where field4 = \"vehicle\"", null);
				cr.moveToFirst();
				UrlConnector uc = new UrlConnector(dr1str, dr1pinstr, dr2str, dr2pinstr,cr.getString(0), cr.getString(1), cr.getString(2));
				cr.close();
				closeDB();
				int resultCode =  uc.sendData(2);
				
			    switch(resultCode){
			    // data have been sent success
			    case 0:
			    	Log.e("Result code",""+resultCode);
			    	sendResult = true;
			    	break;
			    default:
			    	Log.e("Result code",""+resultCode);
			    	toast(ErrorsTypes.getErrorDiscr(resultCode),2);
			    	sendResult = true;
			    	break;
			    }				
				refreshHandler.sendEmptyMessage(0);
			} catch (Exception e) {
				refreshHandler.sendEmptyMessage(0);
			}
		}
	};

	/*private boolean sendDataToServer() {
		//sendData type == 2 - second steP, sending dr1 id, dr2 id, dr1 pin, dr2 pin, company, unit
		openDB();
		Cursor cr = db.rawQuery("select * from auth where field4 = \"vehicle\"", null);
		cr.moveToFirst();
		UrlConnector uc = new UrlConnector(dr1str, dr1pinstr, dr2str, dr2pinstr,cr.getString(0), cr.getString(1), cr.getString(2));
		cr.close();
		closeDB();
		int resultCode =  uc.sendData(2);
		
	    switch(resultCode){
	    // data have been sent success
	    case 0:
	    	Log.e("Result code",""+resultCode);
	    	return true;
	    default:
	    	Log.e("Result code",""+resultCode);
	    	toast(ErrorsTypes.getErrorDiscr(resultCode),2);
	    	return false;
	    }
	}*/
	
	private Handler refreshHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(sendResult){
				//bound truck with client
				//write options into DB
				openDB();
				//dbOpenHelper.sqlRequest(db,"DELETE FROM auth WHERE field1 = \"driver1\" OR field1 = \"driver2\"");		
				dbOpenHelper.sqlRequest(db,"DELETE FROM auth WHERE field4 = \"driver\"");	
				switch(dbInsertCode){
				case 1:
					dbOpenHelper.sqlRequest(db, "INSERT INTO auth VALUES(\"driver1\", \""+dr1str+"\", \""+dr1pinstr+"\", \"driver\")");
				break;
				case 2:
					dbOpenHelper.sqlRequest(db, "INSERT INTO auth VALUES(\"driver1\", \""+dr1str+"\", \""+dr1pinstr+"\", \"driver\")");
					dbOpenHelper.sqlRequest(db, "INSERT INTO auth VALUES(\"driver2\", \""+dr2str+"\", \""+dr2pinstr+"\", \"driver\")");
				}
                //dbOpenHelper.insertInto(db, "auth", comp+", "+unit+", "+pin);
                closeDB();
			showMessage(CONFIRM);			
			}else
				toast(ErrorsTypes.getErrorDiscr(resultCode),2);
			pd.dismiss();
		}
	};
   
	void toast(String msg,int type){
		switch(type){
		case 1:
			Toast.makeText(ct, msg+" occured", Toast.LENGTH_SHORT).show();
		break;
		case 2:
			Toast.makeText(ct, msg, Toast.LENGTH_SHORT).show();
		}
	}
    /*
     * send typed data to server
     * receive response
     * show response result
     * if it correct, offer save it
     */
	//code = 0 ,  fail
	//code = 1 , insert 1 row
	//code = 2 , insert 2 row
	int dbInsertCode = 0;
    private boolean checkInputData() {
		 dr1str = dr1.getText().toString();
		 dr1pinstr = dr1pin.getText().toString();
		 dr2str = dr2.getText().toString();
		 dr2pinstr = dr2pin.getText().toString();
		// if typed correct data 
		// frields 
		if(dr1str.trim().length() == 0){
			toast("Input driver#1 ID please",2);
			return false;
		}
		if(dr1pinstr.trim().length() == 0){
			toast("Input driver#1 PIN CODE please",2);
			return false;
		}
		//insert 1 row
		dbInsertCode = 1;
		
		//if one of dr2 fields not fill , show message
		if((dr2str.trim().length() == 0 & dr2pinstr.trim().length() > 0) |
				(dr2str.trim().length() > 0 & dr2pinstr.trim().length() == 0)){
			toast("Driver#2 fields not filled etirely. Fill it, or clean",2);
			return false;
		}
		//insert 2 row
		if((dr2str.trim().length() > 0 & dr2pinstr.trim().length() > 0)) dbInsertCode = 2;
		if((dr1str.trim()).equals((dr2str.trim()))){
			toast("Error. Drivers have the same name",2);
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
			alertbox.setTitle("Approved");
			alertbox.setMessage("You are input correct data");
		/*
		 * on click YES
		 */
		alertbox.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						
                        PageAuthorization2.this.finish();
                        //into has been bounded, go to driver authorization page
							//startActivity(new Intent(getApplicationContext(),));
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
			alertbox.setMessage("You are typed not correct data");
			alertbox.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							// do nothing
						}
					});
		}
		alertbox.show();
		confirm.setClickable(true);
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