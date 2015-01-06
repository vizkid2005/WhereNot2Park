package com.firsttry.mumbaiparking;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.makeshift.wheretopark.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firsttry.mumbaiparking.helpers.JSONSendReceiveHelper;;

public class TagFormActivity extends Activity {

	Button cancel,done;
	TextView view;
	ArrayList<LatLng> latlng = new ArrayList<LatLng>();
	int noOfMarkers;
	//private static final String webAddress = "http://192.168.0.106/mumbaiparking/";
	private static final String webAddress = "http://www.wheretopark.comze.com/";
	
	/*
	 * When there are multiple markers, it can only signify a free parking area.
	 * When there is just one marker, it can be a parking spot, a puncturewala, or a towing station
	 * We have to also pass in the type to the database
	 * For selecting the type, we use a spinner
	 * #Saavn: Dum Dum Diga Diga 
	 */
	
	String type;
	/*
	 * type value will be single,multiple,tow or puncture
	 * Keep these in mind when handling requests to and from database
	 * #Saavn: Zindagi ke safar mein
	 */
	int cost;
	float safetyRating;
	int subId;
	//The above subId will determine the next index of the markers
	
	/*Thinking to perform date calculations at the server
	 * as it is only needed for visualization
	 * 
	 * Calendar cal = Calendar.getInstance();
	DecimalFormat f = new DecimalFormat("00.00000");
	
	int date = Calendar.DATE;
	int month = Calendar.MONTH; // Jan is 0,feb is 1
	int year = Calendar.YEAR;
	int hour = Calendar.HOUR_OF_DAY; //24 hr format
	int minute = Calendar.MINUTE;
	int day = Calendar.DAY_OF_WEEK; //Sunday=1,monday=2, and so on
	*/
	
	EditText costBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tagformactivity);
	
		/*
		 * Now we will extract the values passed with the intent from 
		 * TagActivity. 
		 */
		
		Intent intent = getIntent();
		noOfMarkers = intent.getIntExtra("markerscount",0);
		if(noOfMarkers == 0)
		{
			/*If no markers are present then display error and exit activity*/
			Log.i("TagForm","No Markers");
			Toast.makeText(TagFormActivity.this,"No Markers Selected", Toast.LENGTH_LONG).show();
			finish();
		}
		else if(noOfMarkers >10)
		{
			/*Cancel this operation 
				Possible Violation of Security
			 */
			Toast.makeText(TagFormActivity.this,"Select Less Markers", Toast.LENGTH_LONG).show();
			finish();
		}
		
		else
		{
			for(int i=0;i<noOfMarkers;i++)
			{
				float lat  = (float) intent.getFloatExtra("lat"+i, 0);
				float lng = (float) intent.getFloatExtra("lng"+i, 0);
				
				if(lat==0)
				{
					Log.i("TagForm","Values not properly passed");
				}
				else
				{
					LatLng ll = new LatLng(lat, lng);
					latlng.add(ll);
				}
			}
		}
		new getSubIdFromServer().execute("");
		
		cancel = (Button) findViewById(R.id.cancelButtonTagFormAlt);
		done = (Button) findViewById(R.id.doneButtonTagFormAlt);
		Spinner spinner = (Spinner) findViewById(R.id.spinner12);
		costBox = (EditText) findViewById(R.id.costBox1);
		
		if(noOfMarkers>1)
		{	
			/* Multiple markers encountered, hence save the user from some trouble */
			spinner.setVisibility(View.GONE);
			type="multiple";
		}
		else
		{
			/*A single spot is marked , we now initialize the spinner with an array 
			 * specified in the Strings resource.
			 */
			// Create an ArrayAdapter using the string array and a default spinner layout
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.spotTypeSpinner, android.R.layout.simple_spinner_item);
			// Specify the layout to use when the list of choices appears
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			spinner.setAdapter(adapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
					// TODO Auto-generated method stub
					Toast.makeText(TagFormActivity.this, 
							"OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
							Toast.LENGTH_SHORT).show();
					String temp=parent.getItemAtPosition(pos).toString();
					if(temp.equals("Parking Spot"))
					{
						costBox.setText("");
						costBox.setFocusable(true);
						costBox.setFocusableInTouchMode(true);
						type="single";
					}
					else if(temp.equals("Towing Station"))
					{
						type="tow";
						costBox.setText("Not Applicable");
						costBox.setFocusable(false);
					}
					else if(temp.equals("Puncturewala"))
					{
						costBox.setText("Not Applicable");
						costBox.setFocusable(false);
						type="puncture";
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
					
				}
				
			});
			
			/*
			 * Setting up the spinner ends here
			 */
		}//else ends
		
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cancel.setBackgroundResource(R.drawable.buttonbgpressed);
				/*Intent intent = new Intent(TagFormActivity.this,TagActivity.class);
				startActivity(intent);*/
				finish();
			}
		});
		
		done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				done.setBackgroundResource(R.drawable.buttonbgpressed);
				
				/*
				 * Lesson Learnt : Always retrieve textbox and other values on click of the 
				 * Done button, not at the beginning of the class.
				 */
				
				if(costBox.getText().toString().equals(""))
				{
					cost=0;
				}
				else if(costBox.getText().toString().equals("Not Applicable"))
				{
					cost = 1000;
				}
				else
					cost = Integer.parseInt(costBox.getText().toString());
				//Assuming there will be no bad entries for cost , add a failsafe here
				
				RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBarTag1);
				safetyRating = ratingBar.getRating();
				
				try {
					String resultString = new sendMarkersToServer().execute("").get();
					Toast.makeText(getApplicationContext(), resultString, Toast.LENGTH_SHORT).show();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	
	private class getSubIdFromServer extends AsyncTask<String, Void, String>
	{
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			subId = JSONSendReceiveHelper.getMaxSubId();
			Log.i("subid", "Sub id received as "+ subId);
			return null;
		}
	}
	
	private class sendMarkersToServer extends AsyncTask<String, Void, String>
	{
		JSONObject jObject = new JSONObject();
		JSONArray jArray = new JSONArray();
		String s=null;
	
		/* Add post execute to close the activity and display a success toast */
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			SharedPreferences prefs = getSharedPreferences("General", 0);
			String userName = prefs.getString("userName", null);
			
			try
			{
				int i;
				for(i=0;i<noOfMarkers;i++)
				{
					JSONObject jObject2 = new JSONObject();
					jObject2.put("latitude",latlng.get(i).latitude);
					jObject2.put("longitude", latlng.get(i).longitude);
					jObject2.put("id",i);
					jArray.put(jObject2);
				}
				
				/*
				 * Try without sending subId, it will save time, do subId calculations at server
				 */
				//jObject.put("subid", 1);
				jObject.put("cost",cost);
				jObject.put("rating", safetyRating);
				jObject.put("type",type);
				
				jObject.put("markers", jArray);
				jObject.put("username", userName);
				s = jObject.toString(4);
			}		
			catch(JSONException ex)
			{
				Log.i("JSONTag", "A Problem");
			}
			
		//	JSONSendReceiveHelper.SendHttpPost(webAddress+"insertmarkers.php", jObject);
			String resultString = JSONSendReceiveHelper.arbitraryHttpPost(webAddress+"insertmarkers.php", jObject);
			return resultString;
			
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			finish();
		}
	}

}
