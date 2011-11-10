// WebViewApp (LGPL 3.0)
// Service class
// Author: Kristo Vaher - www.waher.net - kristo@waher.net

// This is background action that runs at specific time. In manifest it is also set for this to run at boot time.

package com.webviewapp.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.Bundle;

public class appBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
	    String action = intent.getAction();
	    if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
	        Toast.makeText(context, "appBroadcastReceiver: Boot service starting appService", Toast.LENGTH_LONG).show(); 
			Intent mServiceIntent = new Intent();
			mServiceIntent.setAction("com.webviewapp.app.appService");
			context.startService(mServiceIntent);
	    } else {
			try {
				Bundle bundle = intent.getExtras();
				String message = "appBroadcastReceiver: "+bundle.getString("background_message"); //Using the string sent by original activity
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				/* Starting a new activity */
					//Intent newIntent = new Intent(context, appActivity.class);
					//newIntent.putExtra("alarm_message", message);
					//newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					//context.startActivity(newIntent);
			} catch (Exception e) {
				Toast.makeText(context, "appBroadcastReceiver: Unknown Alarm", Toast.LENGTH_SHORT).show();
				//e.printStackTrace();
			}
	    }
		
	}
}
