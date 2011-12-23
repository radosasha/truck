package com.truck.alexander;

import java.util.Calendar;
import java.util.HashMap;



import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
        initialization();
    }

	private void initialization() {
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
				//finish "new trip" procedure
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
		rst.put("tripid" , ettripid.getText().toString());
		rst.put("date", etdate.getText().toString());
		rst.put("odometer", etodometer.getText().toString());
		rst.put("destination", etdestination.getText().toString());
		rst.put("memo", etmemo.getText().toString());
		rst.put("command", "start");
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
			EditText tripID = (EditText)findViewById(R.newtrip.tripid);
			EditText date = (EditText)findViewById(R.newtrip.date);
			EditText odometer = (EditText)findViewById(R.newtrip.odometer);
			EditText destination = (EditText)findViewById(R.newtrip.destination);
			if(isNull(tripID)){
				toast("Trip ID");
				return false;
			}
			/*if(isNull(date)){
				toast("Date");
				return false;
			}*/
			if(isNull(odometer)){
				toast("Odometer");
				return false;
			}
			if(isNull(destination)){
				toast("Destination");
				return false;
			}
		return true;
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