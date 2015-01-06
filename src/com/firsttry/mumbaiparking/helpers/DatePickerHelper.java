package com.firsttry.mumbaiparking.helpers;

import com.makeshift.wheretopark.R.id;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.DatePicker;
import android.widget.EditText;


/*Created this class after spending more than 2 hrs on understanding the Fragment 
 * and how it works.
 * This class is being used to pick dates for the ReminderActivity  
 * #Saavn : Ehsaan tera hoga mujhpar, Junglee
 * #Saavn : Aaj kal tere mere pyaar ke charche
 */
public class DatePickerHelper extends DialogFragment implements
		OnDateSetListener {

	EditText pucBox,insuranceBox,licenseBox;
	Context context;
	SharedPreferences prefs;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		prefs = getActivity().getSharedPreferences("Reminders", 0);
		
		int year, monthOfYear, dayOfMonth;
		year=dayOfMonth=monthOfYear=0;
		
		
		//The tag value will decide which values ie:puc,insurance,license will be set as default
		if(getTag().equals("PUC")){
			year = prefs.getInt("PUC_YEAR", 0);
			monthOfYear = prefs.getInt("PUC_MONTH", 0);
			dayOfMonth = prefs.getInt("PUC_DATE", 0);
		}
		else if(getTag().equals("INSURANCE")){
			year = prefs.getInt("INSURANCE_YEAR", 0);
			monthOfYear = prefs.getInt("INSURANCE_MONTH", 0);
			dayOfMonth = prefs.getInt("INSURANCE_DATE", 0);
		}
		else if(getTag().equals("LICENSE")){
			year = prefs.getInt("LICENSE_YEAR", 0);
			monthOfYear = prefs.getInt("LICENSE_MONTH", 0);
			dayOfMonth = prefs.getInt("LICENSE_DATE", 0);
		}
		
		return new DatePickerDialog(getActivity(), this, year, monthOfYear, dayOfMonth);
	}
	
	@Override
	public void onDateSet(DatePicker picker, int year, int monthOfYear, int dayOfMonth) {
		prefs = getActivity().getSharedPreferences("Reminders", 0);
		SharedPreferences.Editor edit = prefs.edit();
		
		if(getTag().equals("PUC")){
			edit.putInt("PUC_YEAR", year);
			edit.putInt("PUC_MONTH", monthOfYear);
			edit.putInt("PUC_DATE", dayOfMonth);
		}
		else if(getTag().equals("INSURANCE")){
			edit.putInt("INSURANCE_YEAR", year);
			edit.putInt("INSURANCE_MONTH", monthOfYear);
			edit.putInt("INSURANCE_DATE", dayOfMonth);
		}
		else if(getTag().equals("LICENSE")){
			edit.putInt("LICENSE_YEAR", year);
			edit.putInt("LICENSE_MONTH", monthOfYear);
			edit.putInt("LICENSE_DATE", dayOfMonth);
		}
		
		edit.commit();
		Log.i("parking", "calling updateddisplay");
		
		Log.i("parking", "inside updateddisplay");
		
		updateDisplay();
	}
	
	public void updateDisplay()
	{
		pucBox = (EditText) getActivity().findViewById(id.pucBox);
		insuranceBox = (EditText) getActivity().findViewById(id.insuranceBox);
		licenseBox = (EditText) getActivity().findViewById(id.licenseBox);
		
		String pucString = prefs.getInt("PUC_DATE", 0)+"/"+(int)(prefs.getInt("PUC_MONTH", 0)+1)+"/"+prefs.getInt("PUC_YEAR", 0);
		pucBox.setText(pucString);
		
		Log.i("parking", "puc value :"+pucString);
		String insuranceString = prefs.getInt("INSURANCE_DATE", 0)+"/"+(int)(prefs.getInt("INSURANCE_MONTH", 0)+1)+"/"+prefs.getInt("INSURANCE_YEAR", 0);
		insuranceBox.setText(insuranceString);
		Log.i("parking", "ins value :"+insuranceString);
		String licenseString = prefs.getInt("LICENSE_DATE", 0)+"/"+(int)(prefs.getInt("LICENSE_MONTH", 0)+1)+"/"+prefs.getInt("LICENSE_YEAR", 0);
		licenseBox.setText(licenseString);
		Log.i("parking", "license value :"+licenseString);
	}

}
