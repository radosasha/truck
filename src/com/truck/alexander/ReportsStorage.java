package com.truck.alexander;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.truck.alexander.db.DBConnector;
import com.truck.alexander.modules.ReportSender;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ReportsStorage extends Activity{
	// db fields
	SQLiteDatabase db;
	DBConnector dbOpenHelper;
	Context ct;
	
	private ProgressDialog pd;
	final static int SEND_ALL = 0;
	final static int REMOVE_ALL =1;
	final static int SEND_ITEM = 2;
	final static int REMOVE_ITEM =3;
	
	// list data
	 public  static SimpleAdapter adapter = null;
	 public  static List<HashMap<String, Object>> fillMaps = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.repostorage);
		
		ct = this;
		ListView lv = (ListView) findViewById(R.reports.list);
		String[] from = new String[] { "rowid0" };
		int[] to = new int[] { R.listreports.text, };
		fillMaps = new ArrayList<HashMap<String, Object>>();
		adapter = new SimpleAdapter(this, fillMaps, R.layout.listreports, from,	to);
		lv.setAdapter(adapter);
       /* lv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toast("longclick");
			}
		});*/
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos,
					long id) {
				Log.e("click", "long");
				showDialogForSelectedItems(pos);
				return false;
			}
		});
       // map =new  HashMap<String, Object>();
       // map.put("rowid0", "pp");
       // fillMaps.add(map);
        updateList();
        //adapter.notifyDataSetChanged();
        showReports();
/*		openDB();
		Cursor cursor = db.rawQuery(
				"select * from reports where report like \"f%\"", null);
		Log.e("SIZE", cursor.getCount() + "");
		cursor.close();*/
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menureports, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.menu.send:
			sendReport(SEND_ALL, -1);
			Toast.makeText(this, "Send!", Toast.LENGTH_SHORT).show();
			break;
		case R.menu.remove:
			// Toast.makeText(this, "Remove!", Toast.LENGTH_SHORT).show();
			showDialogForAllItems(REMOVE_ALL, "Delete reports",
					"Shure you want to delete all reports?", -1);
		}
		return true;
	}

	void showReports() {
		openDB();
		Cursor cr = dbOpenHelper.select(db, "reports");
		while (cr.moveToNext()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("rowid0", cr.getString(0));
			fillMaps.add(map);
		}
		updateList();
		cr.close();
		closeDB();
	}

	void showDialogForAllItems(final int type, String title, String body,
			final int item) {
		AlertDialog.Builder alertbox = new AlertDialog.Builder(ct);
		alertbox.setTitle(title);
		alertbox.setMessage(body);
		alertbox.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						openDB();
						Cursor cr = dbOpenHelper.select(db, "reports");
						switch (type) {
						case SEND_ALL:
							// send all
							toast("send all");
							break;
						case REMOVE_ALL:
							toast("remove all");
							removeAllReports(cr);
						}
						closeDB();
					}
				});

		alertbox.setNeutralButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		alertbox.show();
	}
	
	// list clicked item
	int item;
	void showDialogForSelectedItems(final int item){
		this.item = item;
		AlertDialog.Builder alertbox = new AlertDialog.Builder(ct);
		alertbox.setTitle("Reports");
		alertbox.setMessage("Choose action");		
		alertbox.setPositiveButton("Send Report",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						pd = ProgressDialog.show(ct, "Sending report...", "Please wait...",true,
				                false);
						Thread thread =  new Thread(null, sendHistoryReport);
				        thread.start();
						//toast("Not work yet");
					}
				});
		alertbox.setNeutralButton("Remove Report",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						openDB();
						Cursor cr = dbOpenHelper.select(db, "reports");
						removeItem(item, cr);
						cr.close();
						closeDB();
					}
				});

		alertbox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		alertbox.show();
	}
	
	
	private Runnable sendHistoryReport = new Runnable() {
		@Override
		public void run() {
			try {
				ReportSender rs = new ReportSender(ct);
				rs.sendHistoryReport(item, rs,sendReportHandler);
			} catch (Exception e) {
				e.printStackTrace();
				Message stopPB = new Message();
				Bundle data = new Bundle();
				// stop handler 
				data.putInt("cmnd", 4);		
				stopPB.setData(data);
				sendReportHandler.sendMessage(stopPB);
				// show message
				Message showMessage = new Message();
				data.clear();
				data.putInt("cmnd", 5);					
				showMessage.setData(data);
				sendReportHandler.sendMessage(showMessage);
			}
		}
	};
	/*Message ms = new Message();
	Bundle bndl = new Bundle();
	bndl.putString("data", locality+", "+countryName);
	ms.setData(bndl);
	refreshHandler.sendMessage(ms);	   */
	private Handler sendReportHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int commandToHandler = msg.getData().getInt("cmnd");
			switch(commandToHandler){
			// on start send
			case 0:
				pd.setMessage("Sending start info...");
				break;
			// on coordinates send
			case 1:
				pd.setMessage("Sending coordinates...");
				break;
			// on stop send
			case 2:
				pd.setMessage("Sending stop info...");
				break;
			//on title change
			case 3:
				pd.setTitle(msg.getData().getString("title"));
				break;
			// stop progressbar
			case 4:
				pd.dismiss();
				break;
			// show message
			case 5:
				Toast.makeText(ct, "Some error occured", Toast.LENGTH_LONG).show();
			}			
		}
	};
	
	private void removeItem(int  itemNumber,Cursor cr) {
		cr.moveToPosition(itemNumber);
		String tableName = cr.getString(0); // report name
		int id = cr.getInt(8); // report id
		dbOpenHelper.dropTables(db,tableName);
		fillMaps.remove(itemNumber);
		//adapter.notifyDataSetChanged();
		db.execSQL("delete from reports where id = "+id+"");
		updateList();
	}
	
	/*
	 * @param int type
	 * @param int item
	 * @return true if data sent to server success and received "done" answer from server
	 */
	boolean sendReport(int type, int item){
		return false;
	}
	
	void removeAllReports(Cursor cr) {
		cr.moveToFirst();
		for (int i = 0; i < cr.getCount(); i++) {
			String secondName = cr.getString(0);
			dbOpenHelper.dropTables(db,secondName);
		} 
		db.execSQL("DELETE FROM reports");
		// clear list
		fillMaps.clear();
		 updateList();
		cr.close();
	}

	
	
	static void updateList(){
		adapter.notifyDataSetChanged();
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
	
	void toast(String msg){
		Toast.makeText(ct, msg, Toast.LENGTH_SHORT).show();
	}
}
