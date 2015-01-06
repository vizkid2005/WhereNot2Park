package com.firsttry.mumbaiparking;

import java.util.Calendar;

import com.makeshift.wheretopark.R.id;
import com.firsttry.mumbaiparking.helpers.DatePickerHelper;
import com.firsttry.mumbaiparking.helpers.MyAlarmManager;
import com.makeshift.wheretopark.R;

import android.app.Activity;
import android.app.AlarmManager;
//import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

public class ReminderActivity extends FragmentActivity {

	int pucDate,pucMonth,pucYear;
	int insuranceDate,insuranceMonth,insuranceYear;
	int licenseDate,licenseMonth,licenseYear;
	
	static final int DP_PUC_ID = 1;
	static final int DP_INS_ID = 2;
	static final int DP_LIC_ID = 3;
	static final String LOGTAG = "parking";
	
	DatePicker dpPuc, dpInsurance, dpLicense;
	EditText pucBox,insuranceBox,licenseBox;
	Button pucButton,insuranceButton,licenseButton;
	ViewGroup vg;
	
	private SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reminderform);
		vg = (ViewGroup) findViewById(R.layout.reminderform);
		
		//Reminders is the name of the shared preferences
		prefs = getSharedPreferences("Reminders", MODE_PRIVATE);
		
		pucBox = (EditText) findViewById(R.id.pucBox);
		insuranceBox = (EditText) findViewById(id.insuranceBox);
		licenseBox = (EditText) findViewById(id.licenseBox);
		
		//Testing if the preferences exist
		int test = prefs.getInt("PUC_DATE", 0);
		
		if(test == 0)
		{
			//Now we know that the preferences do not exist, so we intialize everything to the current date
			final Calendar c = Calendar.getInstance();
			pucDate=insuranceDate=licenseDate=c.get(Calendar.DAY_OF_MONTH);
			
			/*When read directly the month will be one less than the expected value
			 * For example : Feb will be 1 and Jan will be 0 
			 */
			pucMonth=insuranceMonth=licenseMonth=c.get(Calendar.MONTH);
			pucYear=insuranceYear=licenseYear=c.get(Calendar.YEAR);
			
			//Lesson Learnt : Never try to put integer in place of a string without explicitly casting it in to String
			Toast.makeText(this,"No Settings Exist", Toast.LENGTH_LONG).show();
		
			SharedPreferences.Editor edit = prefs.edit();
			
			edit.putInt("PUC_DATE", pucDate);
			edit.putInt("INSURANCE_DATE", insuranceDate);
			edit.putInt("LICENSE_DATE", licenseDate);
			
			edit.putInt("PUC_MONTH", pucMonth);
			edit.putInt("INSURANCE_MONTH", insuranceMonth);
			edit.putInt("LICENSE_MONTH", licenseMonth);
			
			edit.putInt("PUC_YEAR", pucYear);
			edit.putInt("INSURANCE_YEAR", insuranceYear);
			edit.putInt("LICENSE_YEAR", licenseYear);
			
			//Important : DO NOT FORGET TO COMMIT
			edit.commit();
			
			String pucString = prefs.getInt("PUC_DATE", 0)+"/"+(int)(prefs.getInt("PUC_MONTH", 0)+1)+"/"+prefs.getInt("PUC_YEAR", 0);
			pucBox.setText(pucString);
			String insuranceString = prefs.getInt("INSURANCE_DATE", 0)+"/"+(int)(prefs.getInt("INSURANCE_MONTH", 0)+1)+"/"+prefs.getInt("INSURANCE_YEAR", 0);
			insuranceBox.setText(insuranceString);
			String licenseString = prefs.getInt("LICENSE_DATE", 0)+"/"+(int)(prefs.getInt("LICENSE_MONTH", 0)+1)+"/"+prefs.getInt("LICENSE_YEAR", 0);
			licenseBox.setText(licenseString);
			
			
		}
	
		else
		{
			String pucString = prefs.getInt("PUC_DATE", 0)+"/"+(int)(prefs.getInt("PUC_MONTH", 0)+1)+"/"+prefs.getInt("PUC_YEAR", 0);
			pucBox.setText(pucString);
			String insuranceString = prefs.getInt("INSURANCE_DATE", 0)+"/"+(int)(prefs.getInt("INSURANCE_MONTH", 0)+1)+"/"+prefs.getInt("INSURANCE_YEAR", 0);
			insuranceBox.setText(insuranceString);
			String licenseString = prefs.getInt("LICENSE_DATE", 0)+"/"+(int)(prefs.getInt("LICENSE_MONTH", 0)+1)+"/"+prefs.getInt("LICENSE_YEAR", 0);
			licenseBox.setText(licenseString);
		}
		
		pucButton = (Button) findViewById(id.doneTagButton);
		insuranceButton = (Button) findViewById(id.cancelTagButton);
		licenseButton = (Button) findViewById(id.licenseButton);
		
		/*Setting Touch Listeners 
		 * To show the datepicker and change the bg color of the button */
		pucButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					pucButton.setBackgroundResource(R.drawable.buttonbgpressed);
					showDatePicker(v);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					pucButton.setBackgroundResource(R.drawable.buttonbg);
				}
				return false;
			}
		});
		
		insuranceButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					insuranceButton.setBackgroundResource(R.drawable.buttonbgpressed);
					showDatePicker(v);
					
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					insuranceButton.setBackgroundResource(R.drawable.buttonbg);
				}
				return false;
			}
		});
		
		licenseButton.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					licenseButton.setBackgroundResource(R.drawable.buttonbgpressed);
					showDatePicker(v);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP)
				{
					licenseButton.setBackgroundResource(R.drawable.buttonbg);
				}
				return false;
			}
		});
		/*Done Setting Touch Listeners */
		
	}
	
	public void showDatePicker(View v)
	{
		int buttonPressed= v.getId();
		String tag = null;
		if(buttonPressed == R.id.doneTagButton)
		{
			tag = "PUC";
			Log.i(LOGTAG, "tag is PUC ");
		}
		else if(buttonPressed == R.id.cancelTagButton)
		{
			tag = "INSURANCE";
			Log.i(LOGTAG, "tag is Insurance ");
		}
		else
		{
			tag = "LICENSE";
			Log.i(LOGTAG, "tag is license ");
		}
		
		android.support.v4.app.DialogFragment newFragment = new DatePickerHelper();
	    newFragment.show(getSupportFragmentManager(), tag);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		MyAlarmManager alarm = new MyAlarmManager();
		alarm.setAlarm(getApplicationContext());
		
		super.onDestroy();
		
	}

}
