package com.truck.alexander;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.truck.alexander.db.DBConnector;

public class StartPage extends Activity{
	//result constants
		public static final int SUCCESS_RESULT = 10;
		public static final int BAD_RESULT = 20;
		
		// test fields
		SQLiteDatabase db;
		DBConnector dbOpenHelper;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.start);
			
			// authorization check
	        openDB();
	        Cursor authorization = dbOpenHelper.select(db, "auth");
	        Log.e("AUTH rows",authorization.getCount()+"");
	        if(authorization.getCount() < 2){
	        	startActivityForResult(new Intent(StartPage.this, MenuOptions.class), SUCCESS_RESULT);
	        }
	        else{	        	
	        	startActivity(new Intent(StartPage.this, PageMainGUI.class));
	        	this.finish();
	        }
	        authorization.close();
	        closeDB();
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
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);
			switch(resultCode){
			case MenuOptions.OPTIONS_OK:
				startActivity(new Intent(StartPage.this,PageMainGUI.class));
				StartPage.this.finish();
				break;
			case BAD_RESULT:
				finish();
				break;
			case RESULT_CANCELED:
				finish();
			}
		}
}
