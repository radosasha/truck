package com.truck.alexander.addedit;


import java.util.Date;
import java.util.HashMap;
import com.truck.alexander.R;
import com.truck.alexander.db.DBConnector;



import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class PageNewTripEdit extends Activity implements OnClickListener{
	
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
        setContentView(R.layout.pagetripedit);
        
        Log.e("New Page Edit", "ON CREATE");
         ettripid = (EditText)findViewById(R.newtrip.tripid);
         ettripid.setFocusableInTouchMode(false);
         ettripid.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
			Toast.makeText(PageNewTripEdit.this, "Change is prohibited", Toast.LENGTH_SHORT).show();
			}
		});
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
        // initialize fields
        initFromDB();
    }

    /*
     * initialize fields from local variable "data"
     */
	private void initLocal() {
		ettripid.setText(data.get("tripid"));
        etdate .setText(data.get("date"));
        etodometer.setText(data.get("odometer"));
        etdestination.setText(data.get("destination"));
        etmemo.setText(data.get("memo"));
	}

	/*
	 * get options from DB and save in local variable
	 */
	private void initFromDB() {
		 DBConnector dbOpenHelper = new DBConnector(PageNewTripEdit.this);
	     // open connection
		 SQLiteDatabase  db = dbOpenHelper.getWritableDatabase(); 
		 data =dbOpenHelper.selectFromConfigTable(db);
		 //set initialized flag 
		 initLocal();
		 db.close();
		 dbOpenHelper.close();
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
				 DBConnector dbOpenHelper = new DBConnector(PageNewTripEdit.this);
			     // open connection
				 SQLiteDatabase  db = dbOpenHelper.getWritableDatabase(); 
				 //dbOpenHelper.deleteFrom(db,"tripinfo");
				 //dbOpenHelper.insertConfig(db, data);
				 String sql = "update tripinfo set " +
				 		"tripid = '"+ettripid.getText().toString()+"' ,"+
						"date =  '"+etdate.getText().toString()+"' ,"+
						"odometer = "+ etodometer.getText().toString()+" ,"+
						"destination = '"+etdestination.getText().toString()+"' , "+
						"memo = '"+etmemo.getText().toString()+"'";
				 db.execSQL(sql);
				 dbOpenHelper.close();
				 db.close();
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

	/*
	 * check fields content
	 * if field is empty, return true
	 * if field containt correct data return false
	 */
	private boolean checkDataSet() {
		// for each field , except "MEMO"
			EditText tripID = (EditText)findViewById(R.newtrip.tripid);
			//EditText date = (EditText)findViewById(R.newtrip.date);
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
		           /* mYear = year;
		            mMonth = monthOfYear;
		            mDay = dayOfMonth;*/
		        	Date dt = new Date();
		        	mYear = dt.getYear();
		        	mDay = dt.getDay();
		        	mMonth = dt.getMonth()%12;
		        	Log.e("",mYear+"");
		        	Log.e("",mDay+"");
		        	Log.e("",mMonth+"");
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
	   // tst.setGravity(Gravity.CENTER, 0 , 0);
	    tst.show();
	}
}