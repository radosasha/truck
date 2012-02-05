package com.truck.alexander.addedit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.truck.alexander.db.DBConnector;



import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import com.truck.alexander.*;
public class PageCostAdd extends Activity implements OnClickListener{
	
	EditText date;
	EditText title;
	EditText odometer;
	EditText totalcost;
	EditText memo;
	Button btAdd;
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
        setContentView(R.layout.pagecostadd);
        
        Log.e("New Page Edit", "ON CREATE");
        
         date = (EditText)findViewById(R.costadd.date);
         title = (EditText)findViewById(R.costadd.title);
         odometer = (EditText)findViewById(R.costadd.odometer);
         totalcost = (EditText)findViewById(R.costadd.totalcost);
         memo = (EditText)findViewById(R.costadd.memo);
        
         btAdd = (Button)findViewById(R.costadd.ok);
         btCancel = (Button)findViewById(R.costadd.cancel);
        
      //set Listeners
        date.setOnClickListener(this);
        btAdd.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        // initialize date field
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
	    date.setText(mYear+"/"+
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
		case R.costadd.ok:
			if (checkDataSet()) {
				// data is correct
				DBConnector dbOpenHelper = new DBConnector(PageCostAdd.this);
				// open connection
				SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
				// добавить insert
				dbOpenHelper.insertInto(db, "cost"+PageMainGUI.tripid, createRequest());
				dbOpenHelper.close();
				db.close();
				// finish "add new fuel" activity
				Intent msg = new Intent();
				msg.putExtra("data", createResultData());
				setResult(RESULT_OK, msg);
				finish();
			}
			break;

		case R.costadd.cancel:
			setResult(RESULT_CANCELED);
			finish();
			break;

		case R.costadd.date:
			showDialog(DATE_DIALOG_ID);
			break;

		default:
			break;
		}
	}
	

	private String createRequest() {
		// creare bata base request from input fields
		date = (EditText)findViewById(R.costadd.date);
        title = (EditText)findViewById(R.costadd.title);
        odometer = (EditText)findViewById(R.costadd.odometer);
        totalcost = (EditText)findViewById(R.costadd.totalcost);
        memo = (EditText)findViewById(R.costadd.memo);
		String request =
				//"\""+DBConnector.currentTripID+"\","+
				"\""+  date.getText().toString()+ "\", "+
				"\""+ title.getText().toString()+ "\", "
				+ odometer.getText().toString()+ ", "
				+ totalcost.getText().toString()+", "
				+"\""+ memo.getText().toString()+"\", null";
		return request;
		
	}
	
	private HashMap<String,String> createResultData() {
		HashMap<String,String> rst = new HashMap<String,String>();
		rst.put("date" , date.getText().toString());
		rst.put("odometer", odometer.getText().toString());
		rst.put("totalcost", totalcost.getText().toString());
		rst.put("title", title.getText().toString());
		rst.put("memo", memo.getText().toString());
		return rst;
	}

	/*
	 * check fields content
	 * if field is empty, return true
	 * if field containt correct data return false
	 */
	private boolean checkDataSet() {
		// for each field , except "MEMO"
			
			if(isNull(odometer)){
				toast("Odometer");
				return false;
			}
			if(isNull(totalcost)){
				toast("Total Cost");
				return false;
			}
			if(isNull(title)){
				toast("Title");
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
		    	date.setText(
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