package com.truck.alexander.addedit;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.Set;
import com.truck.alexander.*;
import com.truck.alexander.db.DBConnector;



import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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

public class PageFuelEdit extends Activity implements OnClickListener{
	// test fields
		SQLiteDatabase db;
		DBConnector dbOpenHelper;
	
	EditText etdate;
	EditText etodometer;
	EditText etvolumeprice;
	EditText ettotalcost;
	EditText etfuel;
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
	int item ;
	String id;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // receive data
        setContentView(R.layout.pagefueledit);
        item = getIntent().getExtras().getInt("item");
        
        Log.e("New Page Edit", "ON CREATE");
         etdate = (EditText)findViewById(R.fueledit.date);
         etodometer = (EditText)findViewById(R.fueledit.odometer);
         etvolumeprice = (EditText)findViewById(R.fueledit.price);
         ettotalcost = (EditText)findViewById(R.fueledit.totalcost);
         etfuel = (EditText)findViewById(R.fueledit.fuel);
         etmemo = (EditText)findViewById(R.fueledit.memo);
        
         btOk = (Button)findViewById(R.fueledit.ok);
         btCancel = (Button)findViewById(R.fueledit.cancel);
        
      //set Listeners
        etdate.setOnClickListener(this);
        btOk.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        // initialize fields
        initFromDB();
        dateInit();
    }
   
    private void dateInit() {
		// getting current time 
    	StringTokenizer str = new StringTokenizer(etdate.getText().toString(), " /:");
    	
	    mYear = Integer.parseInt(str.nextToken());
	    mMonth = Integer.parseInt(str.nextToken());
	    mDay = Integer.parseInt(str.nextToken());
	    mhour = Integer.parseInt(str.nextToken());
	    mminute = Integer.parseInt(str.nextToken());
    }

	/*
	 * get options from DB and save in local variable
	 */
	private void initFromDB() {
		openDB();
		Cursor cr = dbOpenHelper.select(db, "fuel" + PageMainGUI.tripid);
		cr.moveToPosition(item);
		id = cr.getString(8);
		cr.close();
		
		String sql = "select * from fuel" + PageMainGUI.tripid + " where id = " + id;
		Log.e("QUERY", sql);
		cr = db.rawQuery(sql, null);
		cr.moveToFirst();
		Log.e("Count", cr.getCount() + "");
		        
		data = new HashMap<String, String>();
		etdate.setText(cr.getString(0));
		etodometer.setText(cr.getString(1));
		// etodometerplus.setText(cr.getString(2));
		//data.put("odometerplus", cr.getString(2));
		etvolumeprice.setText(cr.getString(3));
		ettotalcost.setText(cr.getString(4));
		etfuel.setText(cr.getString(5));
		// etmpg.setText(cr.getString(6));
		//data.put("mpg", cr.getString(6));
		etmemo.setText(cr.getString(7));
		cr.close();
		closeDB();
	}

	
	String shortLength(String str){
		return str.length()==1?"0"+str:str;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.fueledit.ok:
			if(checkDataSet()){
				//data is correct
				//finish "new trip" procedure
			    openDB();
			    String sql = "UPDATE fuel"+PageMainGUI.tripid+" " +
			    		   "SET "+DBConnector.date+"= \""+etdate.getText().toString()+"\", " +
			    		     DBConnector.odometer+" = "+etodometer.getText().toString()+", "+
			    		     DBConnector.odometerplus+" = \""+data.get("odometerplus")+"\", "+
			    		     DBConnector.price+" = "+etvolumeprice.getText().toString()+", "+
			    		     DBConnector.totalcost+" = "+ettotalcost.getText().toString()+", "+
			    		     DBConnector.fuel+" = "+etfuel.getText().toString()+", "+
			    		     DBConnector.mpg+" =  \""+data.get("mpg")+"\", "+
			    		     DBConnector.memo+" = \""+etmemo.getText().toString()+"\" "+
			                "WHERE id = "+id;
			    Log.e("Query", sql);
			    db.execSQL(sql);			    
			    Intent ite = new Intent();
			    ite.putExtra("data", data);
			    data.put("date", etdate.getText().toString());
			    data.put("odometer", etodometer.getText().toString());
			    data.put("odometerplus", "not work yet");
			    data.put("price", etvolumeprice.getText().toString());
			    data.put("totalcost", ettotalcost.getText().toString());
			    data.put("fuel", etfuel.getText().toString());
			    data.put("mpg", "not work yet");
			    data.put("memo", etmemo.getText().toString());
			    data.put("item", item+"");
			    closeDB();
			    setResult(TabFuel.EDIT_OK,ite);
				finish();
			}
			break;
			
        case R.fueledit.cancel:
        	setResult(RESULT_CANCELED);
			finish();
			break;
			
        case R.fueledit.date:
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
			
			if(isNull(etodometer)){
				toast("Odometer");
				return false;
			}
			if(isNull(ettotalcost)){
				toast("Total cost");
				return false;
			}
			if(isNull(etvolumeprice)){
				toast("Volume price");
				return false;
			}
			if(isNull(etfuel)){
				toast("Fuel");
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
		    			mYear+"/"+shortLength(""+(mMonth+1))+"/"+
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
	
	private void toast(String msg){
		Toast tst = Toast.makeText(this, "The field \""+msg+"\" is empty. Please fill it to start" , Toast.LENGTH_SHORT);
	    tst.setGravity(Gravity.CENTER, 0 , 0);
	    tst.show();
	}
}