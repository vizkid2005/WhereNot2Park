package com.firsttry.mumbaiparking;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.firsttry.mumbaiparking.helpers.JSONSendReceiveHelper;
import com.google.android.gms.maps.model.LatLng;
import com.makeshift.wheretopark.R;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;


/*
 * This class was made as a result of criticism from Aamir that I had not
 * implemented the parking constraints of Even Odd days of parking and timing.
 * Its a good thing this happened, I got rid of the ugly green rating bar which 
 * was an eye sore.
 * 
 */
public class TagFormActivityTest extends Dialog {

	//private static final String webAddress = "http://192.168.0.106/mumbaiparking/";
	private static final String webAddress = "http://www.wheretopark.comze.com/";
	ArrayList<LatLng> latLngList;
	Context tagActivityContext;
	EditText costBox;
	String type;
	Spinner spinner,timeFromSpinner,timeToSpinner;
	int cost;
	float safetyRating;
	int subId;
	int fromTime;
	int toTime;
	int noOfMarkers;
	String vehicle;
	String days;
	RatingBar ratingBar;
	Button done,cancel;
	RadioGroup vehicleRadio;
	RadioGroup daysRadio;

	/*
	 * type value will be single,multiple,tow or puncture
	 * Keep these in mind when handling requests to and from database
	 * #Saavn: Zindagi ke safar mein
	 */
	
	public TagFormActivityTest(Context context, ArrayList<LatLng> latLngList) {
		super(context);
		// TODO Auto-generated constructor stub
		//May need to initialize latlng list
		this.latLngList = latLngList;
		tagActivityContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tagformactivityalt);
		
		Log.i("DialogTag", "No of MArkers : "+latLngList.size());
	//	ArrayList<LatLng> latlng = new ArrayList<LatLng>();
	//	int noOfMarkers;
		
		/*
		 * When there are multiple markers, it can only signify a free parking area.
		 * When there is just one marker, it can be a parking spot, a puncturewala, or a towing station
		 * We have to also pass in the type to the database
		 * For selecting the type, we use a spinner
		 * #Saavn: Dum Dum Diga Diga 
		 */
		
		
		
		//The above subId will determine the next index of the markers
		
		spinner = (Spinner) findViewById(R.id.spinner1);
		costBox = (EditText) findViewById(R.id.costBox);
		ratingBar = (RatingBar) findViewById(R.id.ratingBarTagForm);
		cancel = (Button) findViewById(R.id.cancelButtonTagFormAlt);
		done = (Button) findViewById(R.id.doneButtonTagFormAlt);
		timeFromSpinner = (Spinner) findViewById(R.id.fromTimeSpinner);
		timeToSpinner = (Spinner) findViewById(R.id.toTimeSpinner);
		vehicleRadio = (RadioGroup) findViewById(R.id.radioGroup1);
		daysRadio = (RadioGroup) findViewById(R.id.radioGroup2);
		final Handler handler;
		
		/*
		 * By default, these values will be set
		 * fromTime and toTime is 99 if nothing is selected, it means
		 * parking is not constrained to any time 
		 */
		days = "All";
		vehicle = "Both";
		fromTime = 99;
		toTime = 99;
		
		
		
		
		/*A single spot is marked , we now initialize the spinner with an array 
		 * specified in the Strings resource.
		 */
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getOwnerActivity(),
		        R.array.spotTypeSpinner, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		
		/*These set the time spinners*/ 
		ArrayAdapter<CharSequence> timeFromAdapter = ArrayAdapter.createFromResource(getOwnerActivity(), R.array.timeOfDay, 
														android.R.layout.simple_spinner_item);
		timeFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timeFromSpinner.setAdapter(timeFromAdapter);
		timeToSpinner.setAdapter(timeFromAdapter);
		
		spinner.setSelection(0);
		timeFromSpinner.setSelection(0);
		timeToSpinner.setSelection(0);
		vehicleRadio.check(R.id.radioBoth);
		daysRadio.check(R.id.radioAll);
		
		noOfMarkers = latLngList.size();
		if(noOfMarkers == 0)
		{
			/*If no markers are present then display error and exit activity*/
			Log.i("TagForm","No Markers");
			Toast.makeText(tagActivityContext,"No Markers Selected", Toast.LENGTH_LONG).show();
			dismiss();
			
		}
		else if(noOfMarkers >10)
		{
			/*Cancel this operation 
				Possible Violation of Security
			 */
			Toast.makeText(tagActivityContext,"Select Less Markers", Toast.LENGTH_LONG).show();
			dismiss();
			
		}
		else if(noOfMarkers>1)
		{	
			/* Multiple markers encountered, hence save the user from some trouble */
			spinner.setVisibility(View.GONE);
			type="multiple";
		}
		else 
		{
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int pos, long id) {
					// TODO Auto-generated method stub
				//	Toast.makeText(tagActivityContext, 
					//		"OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
					//		Toast.LENGTH_SHORT).show();
					String temp=parent.getItemAtPosition(pos).toString();
					if(temp.equals("Parking Spot"))
					{
						costBox.setText("");
						costBox.setFocusable(true);
						costBox.setFocusableInTouchMode(true);
						type="single";
						slideToBottom(findViewById(R.id.parkingSpotDetails));
						
					}
					else if(temp.equals("Towing Station"))
					{
						type="tow";
						costBox.setText("Not Applicable");
						costBox.setFocusable(false);
						slideToTop(findViewById(R.id.parkingSpotDetails));
						
					}
					else if(temp.equals("Puncturewala"))
					{
						costBox.setText("Not Applicable");
						costBox.setFocusable(false);
						type="puncture";
						slideToTop(findViewById(R.id.parkingSpotDetails));
					}
					else if(temp.equals("Type of Spot"))
					{
						type="notSelected";
					//	Toast.makeText(tagActivityContext, 
							//	"Select a Type of Spot !!",
							//	Toast.LENGTH_SHORT).show();
					}
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					// TODO Auto-generated method stub
				//	Toast.makeText(tagActivityContext, "Nothing Selected", Toast.LENGTH_SHORT).show();
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
		
			vehicleRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
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
		
			daysRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
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
		
		/*
		 * Lesson Learnt : By default, DialogueInterface OnClickListener is called, 
		 * It is important to explicitly mention the View.OnclickLstener
		 */
		done.setOnClickListener(new View.OnClickListener() {
			
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
					/*
					 * If the cost is Not Applicable then the parking constraints also dont apply(Assumption)
					 * This assumption may cause probs 
					 * Come back here in that case .. 
					 * #Saavn: Humma Humma
					 * 
					 * Will handle wheter or not to enter type into the db at the server.
					 */
				}
				else
					cost = Integer.parseInt(costBox.getText().toString());
				
				safetyRating = ratingBar.getRating();
				
				/*
				 * Some validation
				 */
				if(type.equals("notSelected"))
				{
					Toast.makeText(tagActivityContext,"Select a Type of Spot", Toast.LENGTH_SHORT).show();
				}
				else if(fromTime!=99 && toTime!=99 && fromTime==toTime)
				{
					Toast.makeText(tagActivityContext,"Select valid timings", Toast.LENGTH_SHORT).show();
				}
				else
				{
					try {
						String resultString = new sendMarkersToServer().execute("").get();
						Toast.makeText(tagActivityContext, resultString, Toast.LENGTH_SHORT).show();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cancel.setBackgroundResource(R.drawable.buttonbgpressed);
				dismiss();
			}
		});
	}
	
	private class sendMarkersToServer extends AsyncTask<String, Void, String>
	{
		JSONObject jObject = new JSONObject();
		JSONArray jArray = new JSONArray();
	
		/* Add post execute to close the activity and display a success toast */
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			SharedPreferences prefs = tagActivityContext.getSharedPreferences("General", 0);
			String userName = prefs.getString("userName", null);
			String s;
			try
			{
				int i;
				for(i=0;i<noOfMarkers;i++)
				{
					JSONObject jObject2 = new JSONObject();
					jObject2.put("latitude",latLngList.get(i).latitude);
					jObject2.put("longitude", latLngList.get(i).longitude);
					jObject2.put("id",i);
					jArray.put(jObject2);
				}
				
				/*
				 * Try without sending subId, it will save time, do subId calculations at server
				 */
				/*
				 * Update : subId calculations are done at the server;
				 */
				jObject.put("cost",cost);
				jObject.put("rating", safetyRating);
				jObject.put("type",type);
				jObject.put("vehicle", vehicle);
				jObject.put("days", days);
				jObject.put("fromTime",fromTime);
				jObject.put("toTime", toTime);
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
			Log.i("TagTestResult", resultString);
			return resultString;
			
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			done.setBackgroundResource(R.drawable.buttonbgpressed);
			dismiss();
			
		}
	}
	
	public void slideToTop(View view){
		TranslateAnimation animate = new TranslateAnimation(0,0,0,0);
		animate.setDuration(500);
		animate.setFillAfter(false);
		view.startAnimation(animate);
		view.setVisibility(View.GONE);
		}
	
	public void slideToBottom(View view){
		TranslateAnimation animate = new TranslateAnimation(0,0,0,view.getHeight());
		animate.setDuration(0);
		animate.setFillAfter(false);
		view.startAnimation(animate);
		view.setVisibility(View.VISIBLE);
		}
}
