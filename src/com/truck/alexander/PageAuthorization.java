package com.truck.alexander;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PageAuthorization extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pageauthorization);
        Button bt = (Button)findViewById(R.id.button2);
        bt.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent ite = new Intent();
				ite.setClass(getApplicationContext(), PageRegistration.class);
				startActivity(ite);
			}
		});
    }
}