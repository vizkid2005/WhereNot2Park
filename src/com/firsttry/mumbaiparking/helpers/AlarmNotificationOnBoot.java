package com.firsttry.mumbaiparking.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/*
 * This class is just required to call the setAlarm method on Boot
 * Also look at the receiver added in the manifest. It will be clearer.
 */
public class AlarmNotificationOnBoot extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		Log.i("alarm", "Inside On Receive on boot ");
	MyAlarmManager am = new MyAlarmManager();
	am.setAlarm(arg0);
	}

}
