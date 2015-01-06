package com.firsttry.mumbaiparking.helpers;

import java.util.Calendar;

import com.makeshift.wheretopark.R;
import com.firsttry.mumbaiparking.ReminderActivity;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class MyAlarmManager extends BroadcastReceiver {


	@Override
	public void onReceive(Context context, Intent intent) {
		
		Intent i = new Intent(context,ReminderActivity.class);
		
		PendingIntent pi = PendingIntent.getActivity(context, 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);
	
		int pucDays = intent.getIntExtra("pucDays", 100);
		int insuranceDays = intent.getIntExtra("insuranceDays", 100);
		int licenseDays = intent.getIntExtra("licenseDays", 100);
		
		if(pucDays != 100)
		{	Log.i("alarmmanager","inside on  puc ");
			NotificationCompat.Builder mBuilder =
				    new NotificationCompat.Builder(context)
				    .setSmallIcon(R.drawable.action_bar_ico)
				    .setContentTitle("PUC Expiring")
				    .setContentText("PUC expiring in "+pucDays+" days !!")
				    .setContentIntent(pi)
				    .setAutoCancel(true)
				    ;
	  
			NotificationManager notificationManager = 
					(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

			notificationManager.notify(786, mBuilder.build()); 
		}
		Log.i("alarmmanager","coming till here on license "+licenseDays);
		
		if(licenseDays !=100)
		{	Log.i("alarmmanager","inside on  license ");
			
			NotificationCompat.Builder mBuilder =
				    new NotificationCompat.Builder(context)
				    .setSmallIcon(R.drawable.action_bar_ico)
				    .setContentTitle("License Expiring")
				    .setContentText("License expiring in "+licenseDays+" days !!")
				    .setContentIntent(pi)
				    .setAutoCancel(true)
				    ;
	  
			NotificationManager notificationManager = 
					(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

			notificationManager.notify(110, mBuilder.build()); 
		}
		
		Log.i("alarmmanager","coming till here on insurance "+insuranceDays);
		if(insuranceDays !=100)
		{
			Log.i("alarmmanager","inside on insurance ");
			NotificationCompat.Builder mBuilder =
				    new NotificationCompat.Builder(context)
				    .setSmallIcon(R.drawable.action_bar_ico)
				    .setContentTitle("Insurance Expiring")
				    .setContentText("Insurance expiring in "+insuranceDays+" days !!")
				    .setContentIntent(pi)
				    .setAutoCancel(true)
				    ;
	  
			NotificationManager notificationManager = 
					(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

			notificationManager.notify(5253, mBuilder.build()); 
		
		}
		
	}
	
	public void setAlarm(Context context)
	{
		SharedPreferences prefs = context.getSharedPreferences("Reminders", 0);
		
		Calendar cal = Calendar.getInstance();
		int currentDate = cal.get(cal.DATE);
		int currentMonth = cal.get(cal.MONTH);
		int currentYear = cal.get(cal.YEAR);
		
		int pucDate = prefs.getInt("PUC_DATE", 0);
		int pucMonth = prefs.getInt("PUC_MONTH",0);
		int pucYear = prefs.getInt("PUC_YEAR", 0);
		
		int insuranceDate = prefs.getInt("INSURANCE_DATE", 0);
		int insuranceMonth = prefs.getInt("INSURANCE_MONTH", 0);
		int insuranceYear = prefs.getInt("INSURANCE_YEAR", 0);
		
		int licenseDate = prefs.getInt("LICENSE_DATE", 0);
		int licenseMonth = prefs.getInt("LICENSE_MONTH", 0);
		int licenseYear = prefs.getInt("LICENSE_YEAR", 0);
		
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, MyAlarmManager.class);
		
		Log.i("Dates","PUC : "+pucDate+" "+pucMonth+" "+pucYear);
		Log.i("Dates","Licence : "+licenseDate+" "+licenseMonth+" "+licenseYear);
		Log.i("Dates","Insurance : "+insuranceDate+" "+insuranceMonth+" "+insuranceYear);
		Log.i("Dates","current : "+currentDate+" "+currentMonth+" "+currentYear);
		
		if(pucDate!=0 && (pucMonth-currentMonth)==0 && (pucYear-currentYear)==0 &&
				(pucDate-currentDate)<7  &&  (pucDate-currentDate)>=0){
			
			Log.i("setAlarm","Inside if puc");
			i.putExtra("pucDays",(pucDate-currentDate)); 
		}
		
		if(licenseDate !=0 && (licenseMonth-currentMonth)==0 && (licenseYear-currentYear)==0 &&
				(licenseDate-currentDate)<7  &&  (licenseDate-currentDate)>=0){
			Log.i("setAlarm","Inside if license");
			i.putExtra("licenseDays",(licenseDate-currentDate));
		}
		
		if(insuranceDate !=0 && (insuranceMonth-currentMonth)==0 && (insuranceYear-currentYear)==0 &&
				(insuranceDate-currentDate)<7  &&  (insuranceDate-currentDate)>=0){
			Log.i("setAlarm","Inside if insurance");
			//i.putExtra("type", "Insurance");
			i.putExtra("insuranceDays",(insuranceDate-currentDate));
		}
		
		 PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		
		 
         manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 10, pi);
	}

}
