package com.truck.alexander;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.truck.alexander.addedit.PageCostAdd;
import com.truck.alexander.addedit.PageCostEdit;
import com.truck.alexander.addedit.PageFuelAdd;
import com.truck.alexander.addedit.PageFuelEdit;
import com.truck.alexander.db.DBConnector;


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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TabCost extends Activity {
	Context ct;
	// db fields
			SQLiteDatabase db;
			DBConnector dbOpenHelper;
	// list data
	static HashMap<String, Object> map = null;
	static SimpleAdapter adapter = null;
	static List<HashMap<String, Object>> fillMaps = null;
	Button addCost;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("COST", "ON CREATE");
		/* Second Tab Content */
		setContentView(R.layout.tabcost);
		 // create the grid item mapping
		ct = this;
		
		ListView lv = (ListView)findViewById(R.tabcost.costlist);
        String[] from = new String[] {"rowid0","rowid1","rowid2","rowid3","rowid4"};
        int[] to = new int[] { 
        		R.listcost.date , 
        		R.listcost.service,
        		R.listcost.miles, 
        		R.listcost.totalcost,
        		R.listcost.memo
        		};
        // prepare the list of all records
        fillMaps = new ArrayList<HashMap<String, Object>>();
        adapter = new SimpleAdapter(this, fillMaps, R.layout.listcost, from, to);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(adapterListener);
        adapter.notifyDataSetChanged();
        
        // set button click listener
        addCost = (Button)findViewById(R.tabcost.addcost);
        addCost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!PageMainGUI.TRIP_STARTED) {
					toast("Trip not started");
					return;
				}	
				Intent ite= new Intent();
				ite.setClass(TabCost.this,PageCostAdd.class);
				startActivityForResult(ite,1);
			}
        });
        
     // fill "fuel" list if some data already exist
     		if(!PageMainGUI.TRIP_STARTED )return;
     		openDB();
     		TabTrip.updateTabLists(adapter, fillMaps, "cost",5,dbOpenHelper,db);
     		closeDB();
	}
	
	OnItemClickListener adapterListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			showQuestions(arg2);
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("RESULT","HERE");
		if( resultCode == RESULT_CANCELED) return;
		HashMap<String,String> hConfig= (HashMap<String, String>) data.getSerializableExtra("data");
		/*Log.e("RESULT", hConfig.get("date"));
		Log.e("RESULT", hConfig.get("title"));
		Log.e("RESULT", hConfig.get("odometer"));
		Log.e("RESULT", hConfig.get("totalcost"));
		Log.e("RESULT", hConfig.get("memo"));*/
		map = new HashMap<String, Object>();
		map.put("rowid0", hConfig.get("date"));
		map.put("rowid1", hConfig.get("title"));
		map.put("rowid2", hConfig.get("odometer")+" miles");
		map.put("rowid3", hConfig.get("totalcost")+" USD");
		map.put("rowid4", hConfig.get("memo"));
		switch(resultCode){
		case RESULT_OK:
			fillMaps.add(map);
			break;
		case TabFuel.EDIT_OK:
			fillMaps.set(Integer.parseInt(hConfig.get("item")), map);
		}
			adapter.notifyDataSetChanged();
	}
	
	private void showQuestions(final int item) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(ct);
		alertbox.setTitle("Edit/Remove");
		alertbox.setMessage("Confirm action");

		alertbox.setPositiveButton("Edit",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						Intent it = new Intent();
						it.setClass(TabCost.this, PageCostEdit.class);
						it.putExtra("item", item);
						startActivityForResult(it, TabFuel.EDIT_OK);
					}
				});
		alertbox.setNeutralButton("Remove",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						openDB();
						Cursor cr = dbOpenHelper.select(db, "cost" + PageMainGUI.tripid);
						cr.moveToPosition(item);
						String id = cr.getString(5);
						cr.close();
						String sql = "delete from cost" + PageMainGUI.tripid + " where id = " + id;
						Log.e("QUERY", sql);
						db.execSQL(sql);
						fillMaps.remove(item);
						adapter.notifyDataSetChanged();
						closeDB();
					}
				});

		alertbox.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
		alertbox.show();
	}
	
	static void clearList(){
		fillMaps.clear();
		if(adapter!=null)adapter.notifyDataSetChanged();
	}
	
	private void toast(String msg) {
		Toast.makeText(TabCost.this, msg, Toast.LENGTH_SHORT).show();	
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