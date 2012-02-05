package com.truck.alexander.addedit;

import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.truck.alexander.R;
import com.truck.alexander.R.layout;
import com.truck.alexander.R.newtrip;
import com.truck.alexander.db.DBConnector;



import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class PageNewTrip extends Activity implements OnClickListener{
	
	Context ct;
	// db fields
		SQLiteDatabase db;
		DBConnector dbOpenHelper;
	
	
	EditText ettripid;
	EditText etdate;
	EditText etodometer;
	EditText etdestination;
	EditText etmemo;
	Button btOk;
	Button  btCancel;
	
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mhour;
	private int mminute;

	final int TIME_DIALOG_ID = 1;
	final int DATE_DIALOG_ID = 0;
	
	//retrieve data
	HashMap<String,String> data;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagenewtrip);
        ct= this;
         Log.e("New Page", "ON CREATE");
         ettripid = (EditText)findViewById(R.newtrip.tripid);
         etdate = (EditText)findViewById(R.newtrip.date);
         etodometer = (EditText)findViewById(R.newtrip.odometer);
         etdestination = (EditText)findViewById(R.newtrip.destination);
         etmemo = (EditText)findViewById(R.newtrip.memo);
        
         btOk = (Button)findViewById(R.newtrip.ok);
         btCancel = (Button)findViewById(R.newtrip.cancel);
        
      //set Listeners
        etdate.setOnClickListener(this);
        btOk.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        
        //initialize date in DateTextField
        dateInit();
    }

	private void dateInit() {
		// getting current time 
		final Calendar c = Calendar.getInstance();
	    mYear = c.get(Calendar.YEAR);
	    mMonth = c.get(Calendar.MONTH);
	    mDay = c.get(Calendar.DAY_OF_MONTH);
	    mhour = c.get(Calendar.HOUR_OF_DAY);
	    mminute = c.get(Calendar.MINUTE);
	    
	    //set current time to Date Field
	    // YEAR/MONTH/DAY hour:minute
	    etdate.setText(mYear+"/"+
	            shortLength(""+mMonth)+"/"+
	    		shortLength(""+mDay)+" "+
	            shortLength(""+mhour)+":"+
	    		shortLength(""+mminute));
	}
	
	String shortLength(String str){
		return str.length()==1?"0"+str:str;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.newtrip.ok:
			
			if(checkDataSet()){
				//data is correct
				//finish "new trip" activity
				Intent msg = new Intent();
				msg.putExtra("data",createResultData());
				setResult(RESULT_OK, msg);
				finish();
			}
			break;
			
        case R.newtrip.cancel:
        	setResult(RESULT_CANCELED);
			finish();
			break;
			
        case R.newtrip.date:
        	showDialog(DATE_DIALOG_ID);
        	break;

		default:
			break;
		}
		
	}

	private HashMap<String,String> createResultData() {
		HashMap<String,String> rst = new HashMap<String,String>();
		rst.put("tripid" , ettripid.getText().toString().trim().replaceAll(" ", "_"));
		rst.put("date", etdate.getText().toString());
		rst.put("odometer", etodometer.getText().toString());
		rst.put("destination", etdestination.getText().toString());
		rst.put("memo", etmemo.getText().toString());
		//rst.put("command", "start");
		rst.put("status", "pause");
		return rst;
	}

	/*
	 * check fields content
	 * if field is empty, return true
	 * if field containt correct data return false
	 */
	private boolean checkDataSet() {
		// for each field , except "MEMO"
			
			if(isNull(ettripid)){
				toast("Trip ID");
				return false;
			}
			/*if(isNull(date)){
				toast("Date");
				return false;
			}*/
			if(databaseExist()){
				Toast.makeText(ct, "The trip with name '"+ettripid.getText().toString()+"' already exist in reports", Toast.LENGTH_SHORT).show();
				//toast("Trip witn id '"+ettripid.toString()+"' already exist");
				return false;
			}
			if(isNull(etodometer)){
				toast("Odometer");
				return false;
			}
			if(isNull(etdestination)){
				toast("Destination");
				return false;
			}
		return true;
		}
	
	private boolean databaseExist() {
		openDB();
		Cursor cr = db.rawQuery("select report from reports", null);
		String inputName = ettripid.getText().toString().trim().replaceAll(" ", "_");
		while(cr.moveToNext()){
			if(cr.getString(0).equals(inputName)){
				cr.close();
				closeDB();
				return true;
			}
		}
		cr.close();
		closeDB();
		return false;
	}

	@Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);

        case TIME_DIALOG_ID:
            return new TimePickerDialog(this,
                    mTimeSetListener, mhour, mminute, false);

        }
        return null;
    }
	
	private DatePickerDialog.OnDateSetListener mDateSetListener =
		    new DatePickerDialog.OnDateSetListener() {

		        public void onDateSet(DatePicker view, int year, 
		                              int monthOfYear, int dayOfMonth) {
		            mYear = year;
		            mMonth = monthOfYear;
		            mDay = dayOfMonth;
		            showDialog(TIME_DIALOG_ID);
		        }
		    };
		    
		    public void updatetime()
		    {
		    	etdate.setText(
		    			mYear+"/"+shortLength(""+mMonth)+"/"+
		    	                  shortLength(""+mDay)+" "+
		    					  shortLength(""+mhour)+":"+
		    	                  shortLength(""+mminute));		    			    	 
		    }


		 // Timepicker dialog generation
		    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
		        new TimePickerDialog.OnTimeSetListener() {
		            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		                mhour = hourOfDay;
		                mminute = minute;
		                updatetime();
		            }
		    };


	
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
	private boolean isNull(EditText et){
		if(et.getText().toString().trim().equals(""))return true;
		return false;		
	}
	
	private void toast(String msg){
		Toast tst = Toast.makeText(this, "The field \""+msg+"\" is empty. Please fill it to start" , Toast.LENGTH_SHORT);
	    tst.setGravity(Gravity.CENTER, 0 , 0);
	    tst.show();
	}
}