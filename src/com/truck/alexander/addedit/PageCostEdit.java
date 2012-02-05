package com.truck.alexander.addedit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import com.truck.alexander.*;
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

public class PageCostEdit extends Activity implements OnClickListener{
	Context ct;
	// test fields
	SQLiteDatabase db;
	DBConnector dbOpenHelper;
			
	EditText etdate;
	EditText ettitle;
	EditText etodometer;
	EditText ettotalcost;
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
	
	int item;
	String id;
	//retrieve data
	HashMap<String,String> data;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagecostedit);
        
        ct = this;
        item = getIntent().getExtras().getInt("item");
        
        Log.e("New Page Edit", "ON CREATE");
         etdate = (EditText)findViewById(R.costedit.date);
         ettitle = (EditText)findViewById(R.costedit.title);
         etodometer = (EditText)findViewById(R.costedit.odometer);
         ettotalcost = (EditText)findViewById(R.costedit.totalcost);
         etmemo = (EditText)findViewById(R.costedit.memo);
        
         btOk = (Button)findViewById(R.costedit.ok);
         btCancel = (Button)findViewById(R.costedit.cancel);
        
      //set Listeners
        etdate.setOnClickListener(this);
        btOk.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        // initialize fields
        initFromDB();
    }

  
    /*
	 * get options from DB and save in local variable
	 */
	private void initFromDB() {
		openDB();
		Cursor cr = dbOpenHelper.select(db, "cost" + PageMainGUI.tripid);
		cr.moveToPosition(item);
		id = cr.getString(5); // field id
		cr.close();
		
		String sql = "select * from cost" + PageMainGUI.tripid + " where id = " + id;
		Log.e("QUERY", sql);
		cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		Log.e("Count", cr.getCount() + "");
		
		data = new HashMap<String, String>();
		etdate.setText(cr.getString(0));
		ettitle.setText(cr.getString(1));
		// etodometerplus.setText(cr.getString(2));
		//data.put("odometerplus", cr.getString(2));
		etodometer.setText(cr.getString(2));
		ettotalcost.setText(cr.getString(3));
		etmemo.setText(cr.getString(4));
		cr.close();
		closeDB();
	}
	

	
	String shortLength(String str){
		return str.length()==1?"0"+str:str;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.costedit.ok:
			if(checkDataSet()){
				//data is correct
				//finish "new trip" procedure
			    openDB();
			    String sql = "UPDATE cost"+PageMainGUI.tripid+" " +
			    		   "SET "+DBConnector.date+"= \""+etdate.getText().toString()+"\", " +
			    		     DBConnector.title+" = \""+ettitle.getText().toString()+"\", "+
			    		     DBConnector.odometer+" = "+etodometer.getText().toString()+", "+
			    		     DBConnector.totalcost+" = "+ettotalcost.getText().toString()+", "+
			    		     DBConnector.memo+" = \""+etmemo.getText().toString()+"\" "+
			                "WHERE id = "+id;
			    Log.e("Query", sql);
			    db.execSQL(sql);			    
			    Intent ite = new Intent();
			    ite.putExtra("data", data);
			    data.put("date", etdate.getText().toString());
			    data.put("title", ettitle.getText().toString());
			    data.put("odometer", etodometer.getText().toString());
			    data.put("totalcost", ettotalcost.getText().toString());
			    data.put("memo", etmemo.getText().toString());
			    data.put("item", item+"");
			    closeDB();
			    setResult(TabFuel.EDIT_OK,ite);
				finish();
			}
			break;
			
        case R.costedit.cancel:
        	setResult(RESULT_CANCELED);
			finish();
			break;
			
        case R.costedit.date:
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

		if (isNull(ettitle)) {
			toast("Title");
			return false;
		}

		if (isNull(etodometer)) {
			toast("Odometer");
			return false;
		}

		if (isNull(ettotalcost)) {
			toast("Total cost");
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