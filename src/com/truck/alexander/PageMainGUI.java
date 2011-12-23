package com.truck.alexander;



import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class PageMainGUI extends TabActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pagetab);
        
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
}
