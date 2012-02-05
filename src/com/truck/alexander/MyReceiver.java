package com.truck.alexander;

import com.truck.alexander.modules.ReceiveCoordinateModule;
 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver{ 

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("RECEIVER", "MyReceiver");
        Intent serviceIntent = new Intent(context, ReceiveCoordinateModule.class);
        context.startService(serviceIntent);
    }
}

