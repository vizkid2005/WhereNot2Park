package com.firsttry.mumbaiparking;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.json.JSONObject;

import com.firsttry.mumbaiparking.helpers.JSONSendReceiveHelper;
import com.makeshift.wheretopark.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

public class UpdateSpotActivity extends Activity {

	int subId;
	String userName;
	boolean firstClickCheckBox;
	int cost;
	JSONObject jsonObject;
	RatingBar rb;
	EditText costBox;
	EditText commentsBox;
	CheckBox checkBox;
	Spinner timeFromSpinner,timeToSpinner;
	Button bogusButton;
	Button doneButton;
	Button cancelButton;
	String activityName;
	RadioGroup daysRadio,vehicleRadio;
	String days,vehicle;
	int toTime,fromTime;
	
	//private static final String webAddress = "http://192.168.0.106/mumbaiparking/";
	private static final String webAddress = "http://www.wheretopark.comze.com/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updatespot_activity);
		
		SharedPreferences prefs = getSharedPreferences("General", 0);
		userName = prefs.getString("userName", null);
		
		Intent intent = getIntent();
		subId = intent.getIntExtra("subId", 0);
		String subIdString = String.valueOf(subId);
		activityName = intent.getStringExtra("activityName");
		
		getWindow().setSoftInputMode(
			      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		/*TextView titleText = (TextView) findViewById(R.id.updateSpotTitle);
		String title = getResources().getString(R.string.updateSpotTitle);
		titleText.setText(title+" "+subId);*/
		
		rb = (RatingBar) findViewById(R.id.ratingBarUpdateSpot);
		
		costBox = (EditText) findViewById(R.id.editcostUpdateSpot);
		commentsBox = (EditText) findViewById(R.id.commentsUpdateSpot);
		checkBox =(CheckBox) findViewById(R.id.favoriteCheckBox);
		
		bogusButton = (Button) findViewById(R.id.bogusButton);
		doneButton = (Button) findViewById(R.id.doneUpdateSpot);
		cancelButton = (Button) findViewById(R.id.cancelUpdateSpot);
		
		timeFromSpinner = (Spinner) findViewById(R.id.fromTimeSpinner);
		timeToSpinner = (Spinner) findViewById(R.id.toTimeSpinner);
		vehicleRadio = (RadioGroup) findViewById(R.id.radioGroup1UpdateSpot);
		daysRadio = (RadioGroup) findViewById(R.id.radioGroup2UpdateSpot);
		
		ArrayAdapter<CharSequence> timeFromAdapter = ArrayAdapter.createFromResource(UpdateSpotActivity.this, R.array.timeOfDay, 
				android.R.layout.simple_spinner_item);
		timeFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timeFromSpinner.setAdapter(timeFromAdapter);
		timeToSpinner.setAdapter(timeFromAdapter);
		

		//These will be the deafult values if nothing is selected;
		toTime=100;
		fromTime=100;
		days="NotSelected";
		vehicle="NotSelected";
		
		if(activityName.equals("Towing") || activityName.equals("Puncture") || activityName.equals("Favorite"))
		{
			costBox.setText("Not Applicable");
			costBox.setFocusable(false);
		}
		
		try{
			//Set the username and subId of the Spot
			jsonObject = new JSONObject();
			jsonObject.put("subId", subId);
			jsonObject.put("userName", userName);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		String isFavorite = null;
		try {
			isFavorite = new IsFavoriteSet().execute(subIdString,userName).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(isFavorite.equals("false"))
		{
		//	Toast.makeText(getApplicationContext(), isFavorite, Toast.LENGTH_SHORT).show();
			checkBox.setChecked(false);
		}
		else
		{
			//Toast.makeText(getApplicationContext(), isFavorite, Toast.LENGTH_SHORT).show();
			checkBox.setChecked(true);
		}
		
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			//All the code below id for setting and removing favorites
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if(isChecked)
				{
					//Store in the database
					runOnUiThread(new Runnable() {
						public void run() {
							try
							{
								
								String result = new setFavoriteSpot().execute("addFavorite").get();
								Log.i("Insertion",result);
								Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
							}catch(Exception e)
							{
								e.printStackTrace();
							}	
						}
					});
					
				}
				if(!isChecked)
				{
					//Remove from DB
					try{
						String result = new setFavoriteSpot().execute("removeFavorite").get();
						Log.i("Deletion",result);
						Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();		
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		});
		//Adding and removing favorites done
		
		//Now we add the functionality for the bogus spots
		bogusButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			//	Toast.makeText(getApplicationContext(), "Rating and Cost are now useless", Toast.LENGTH_SHORT).show();	
				String comments = commentsBox.getText().toString();
				comments = comments.trim();
				if(comments.equals(""))
				{
					Toast.makeText(getApplicationContext(), "Comment on why the spot is a Bogus", Toast.LENGTH_SHORT).show();
				}
				else
				{
					try {
						jsonObject.put("comments", comments);
						//Lesson Learnt : To Convert any primitive data type into String use String.valueOf()
						String result = new markBogusSpot().execute().get();
						Log.i("Setting Bogus",result);
						Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();		
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
			}
		});
		
		doneButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String comments = commentsBox.getText().toString();
				comments = comments.trim();
				float rating = rb.getRating();
				String costString = costBox.getText().toString();
				costString.trim();
				
				HashMap<String, Boolean> validation = new HashMap<String, Boolean>();
				
				if(rating == 0)
				{
					Toast.makeText(getApplicationContext(), "Give your rating !!", Toast.LENGTH_SHORT).show();
					validation.put("rating", false);
				}
				if(comments.equals(""))
				{
					Toast.makeText(getApplicationContext(), "Write a small comment about it !!! ", Toast.LENGTH_SHORT).show();
					validation.put("comments", false);
				}
				if(costString.equals(""))
				{
					Toast.makeText(getApplicationContext(), "Cost cannot be Empty, Enter 0 if free", Toast.LENGTH_SHORT).show();
					validation.put("cost", false);
				}
				else if(costString.equals("Not Applicable"))
				{
					cost = 1000;
				}
				else
				{
					cost = Integer.parseInt(costString);
				}
				
				if(validation.isEmpty())
				{
					try {
						jsonObject.put("comments", comments);
						jsonObject.put("cost", cost);
						jsonObject.put("rating", rating);
						jsonObject.put("fromTime", fromTime);
						jsonObject.put("toTime",toTime);
						jsonObject.put("days", days);
						jsonObject.put("vehicle",vehicle);
						
						String result = new updateSpotInDB().execute().get();
						Log.i("Setting Update",result);
						Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();		
					}catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	});
	
		timeFromSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				String tempString = parent.getItemAtPosition(pos).toString();
				if(!tempString.equals("--"))
				{
					fromTime = Integer.parseInt(tempString);
				}
				else
				{
					fromTime=99;
				}
			//	Toast.makeText(tagActivityContext, "from : "+fromTime, Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		timeToSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				// TODO Auto-generated method stub
				String tempString = parent.getItemAtPosition(pos).toString();
				if(!tempString.equals("--"))
				{
					toTime = Integer.parseInt(tempString);
				}
				else
				{
					toTime = 99;
				}
				//Toast.makeText(tagActivityContext, "to : "+toTime, Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		vehicleRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch(checkedId)
				{
					case R.id.radioBikes : vehicle="Bikes";
							 				break;
					case R.id.radioCars : vehicle="Cars";
											break;
					case R.id.radioBoth : vehicle="Both";
											break;
				}
			//	Toast.makeText(tagActivityContext, vehicle, Toast.LENGTH_SHORT).show();
			}

		});
	
		daysRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
			
				switch(checkedId)
				{
					case R.id.radioAll : days="All";break;
					case R.id.radioEven : days="Even";break;
					case R.id.radioOdd : days="Odd";break;
				}
			//	Toast.makeText(tagActivityContext, days, Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	
	private class setFavoriteSpot extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... params) {
			String result="Error in setting fav";
			if(params[0].equals("addFavorite"))
				result = JSONSendReceiveHelper.arbitraryHttpPost(webAddress+"/addFavorite.php", jsonObject);
			else if(params[0].equals("removeFavorite"))
				result = JSONSendReceiveHelper.arbitraryHttpPost(webAddress+"/removeFavorite.php", jsonObject);
			
			return result;
		}
	}
	
	private class markBogusSpot extends AsyncTask<String, Void, String>
	{
		String result = "Error in setting Bogus Spot";
		@Override
		protected String doInBackground(String... params) {
				result = JSONSendReceiveHelper.arbitraryHttpPost(webAddress+"/markBogusSpot.php", jsonObject);
				
			return result;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			finish();
		}
	}
	
	private class updateSpotInDB extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... params) {
			String result = JSONSendReceiveHelper.arbitraryHttpPost(webAddress+"/addSpotReview.php", jsonObject);
			return result;
		}
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			finish();
		}
		
	}
	
	private class IsFavoriteSet extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String subId= params[0];
			String userName = params[1];
			
			JSONObject tempObject = new JSONObject();
			try{
				tempObject.put("subId", subId);
				tempObject.put("userName", userName);
				String result = JSONSendReceiveHelper.arbitraryHttpPost(webAddress+"/isSetFavorite.php", tempObject);
				return result;
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		/*	Intent i;
		
		i = new Intent(this,AllInOneActivity.class);
		i.putExtra("activityName", activityName);
		startActivity(i);
		finish();*/
			
	}
}
