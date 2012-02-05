package com.truck.alexander;

import com.truck.alexander.db.DBConnector;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast; 

public class PageMainGUI extends TabActivity {
    /** Called when the activity is first created. */
	//if "true" tabs will be activated, and you can add new elements in lists
	//"false" will not displayed, and add-buttons "clickable" became false
	static boolean TRIP_STARTED = false;
	public static String tripid = null;
	Context ct ;
	
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagetab);
        ct=this;
        
        
        /* TabHost will have Tabs */
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        
        /* TabSpec used to create a new tab. 
         * By using TabSpec only we can able to setContent to the tab.
         * By using TabSpec setIndicator() we can set name to tab. */
        
        /* tid1 is firstTabSpec Id. Its used to access outside. */
        TabSpec TRIPTabSpec = tabHost.newTabSpec("tid1");
        TabSpec FUELTabSpec = tabHost.newTabSpec("tid2");
        TabSpec COSTTabSpec = tabHost.newTabSpec("tid3");
        TabSpec CHECKPOINTTabSpec = tabHost.newTabSpec("tid4");
         
        /* TabSpec setIndicator() is used to set name for the tab. */
        /* TabSpec setContent() is used to set content for a particular tab. */
        TRIPTabSpec.setIndicator("TRIP").setContent(new Intent(this,TabTrip.class));
        FUELTabSpec.setIndicator("FUEL").setContent(new Intent(this,TabFuel.class));
        COSTTabSpec.setIndicator("COST").setContent(new Intent(this,TabCost.class));
        CHECKPOINTTabSpec.setIndicator("CHECK POINT").setContent(new Intent(this,TabCheckPoint.class));
        
        /* Add tabSpec to the TabHost to display. */
        tabHost.addTab(TRIPTabSpec);
        tabHost.addTab(FUELTabSpec);
        tabHost.addTab(COSTTabSpec);
        tabHost.addTab(CHECKPOINTTabSpec);
    }
    
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    } 
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.menu.options:    
            	startActivityForResult(new Intent(getApplicationContext(),MenuOptions.class),0);
            	//Toast.makeText(this, "options!", Toast.LENGTH_SHORT).show();
                break;
            case R.menu.reports:    
            	startActivity(new Intent(getApplicationContext(),ReportsStorage.class));
            	//Toast.makeText(this, "reports!", Toast.LENGTH_SHORT).show();
                break;
            case R.menu.exit:       
            	Toast.makeText(this, "exit!", Toast.LENGTH_SHORT).show();
            	PageMainGUI.this.finish();
            	break;
        }
        return true;
    }
    
    /*
     * on "back" button listener
     */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN
					&& event.getRepeatCount() == 0) {
				mess();
				return true;
			} else if (event.getAction() == KeyEvent.ACTION_UP) {
				// getKeyDispatcherState().handleUpEvent(event);
				if (event.isTracking() && !event.isCanceled()) {
					return true;
				}
			}
			return super.dispatchKeyEvent(event);
		} else {
			return super.dispatchKeyEvent(event);
		}
	}
    
    /*
     * notify user he want to quit app 
     */
	void mess() {
		
		AlertDialog.Builder alertbox = new AlertDialog.Builder(ct);
		alertbox.setTitle("Quit");
		alertbox.setMessage("It doesn't stop program, shutdown GUI only.");
		alertbox.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						exit();
					}
				});

		alertbox.setNeutralButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				
			}
		});
		alertbox.show();
	}

	/*
	 * close app
	 */
	void exit() {
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(resultCode){
		case StartPage.BAD_RESULT:
			finish();
			break;
		case RESULT_CANCELED:
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
