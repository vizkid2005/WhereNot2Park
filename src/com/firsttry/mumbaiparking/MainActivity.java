package com.firsttry.mumbaiparking;


import org.json.JSONException;
import org.json.JSONObject;

import com.firsttry.mumbaiparking.helpers.EasterEgg;
import com.firsttry.mumbaiparking.helpers.JSONSendReceiveHelper;
import com.makeshift.wheretopark.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Intent mainIntent;
	String userName;
	ProgressDialog pd;
	ImageButton tagButton,searchButton,reminderButton,favButton,towButton,aboutButton,feedbackButton,fineButton,punctureButton;
	//private static final String webAddress = "http://192.168.0.106/mumbaiparking/";
	private static final String webAddress = "http://www.wheretopark.comze.com/";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alt);
		
		SharedPreferences prefs = getSharedPreferences("General", 0);
		userName = prefs.getString("userName", null);
		
		if(userName == null)
		{
			Intent intent = new Intent(MainActivity.this, GoogleLoginActivity.class);
			startActivity(intent);
		}
		else
		{
			TextView tv = (TextView) findViewById(R.id.mainLayoutUserName);
			tv.setText("Welcome "+userName+" !!");
			
			/*
			 * Be sure to remove the below code
			 */
			tv.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
				
				}
				
				
			});
			
			new usageStatisticsReport().execute("");
		}
		
		tagButton = (ImageButton) findViewById(R.id.TagButton);
	    searchButton = (ImageButton) findViewById(R.id.SearchButton);
	    reminderButton = (ImageButton) findViewById(R.id.ReminderButton);
	    favButton = (ImageButton) findViewById(R.id.FavButton);
	    towButton = (ImageButton) findViewById(R.id.TowButton);
	    aboutButton = (ImageButton) findViewById(R.id.AboutButton);
	    feedbackButton = (ImageButton) findViewById(R.id.FeedbackButton);
	    fineButton = (ImageButton) findViewById(R.id.FineButton);
	    punctureButton = (ImageButton) findViewById(R.id.PunctureButton);
		
	    searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				/*The code looked like this before 
				 * 
				 * mainIntent = new Intent(MainActivity.this, AllInOneActivity.class);
				mainIntent.putExtra("activityName", "Search");
				startActivity(mainIntent);
				*/
				
				/*
				 * Lesson Learnt : Only Main UI thread can manipulate views 
				 * Do not attempt to manipulate views from background threads
				 */
				searchButton.setImageResource(R.drawable.ico1pressed);
				new launchActivity(AllInOneActivity.class, "Search").execute();
				
			}
		});
		
		tagButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				tagButton.setImageResource(R.drawable.ico2pressed);
				new launchActivity(TagActivity.class).execute();
			}
		});
		
		reminderButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				reminderButton.setImageResource(R.drawable.ico4pressed);
				new launchActivity(ReminderActivity.class).execute();
			}
		});
		
		favButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				favButton.setImageResource(R.drawable.ico6pressed);
				new launchActivity(AllInOneActivity.class, "Favorite").execute();
			}
		});
		
		towButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				towButton.setImageResource(R.drawable.ico8pressed);
				new launchActivity(AllInOneActivity.class, "Towing").execute();
			}
		});
		
		fineButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fineButton.setImageResource(R.drawable.ico3pressed);
				new launchActivity(FinesActivity.class).execute();
			}
		});
		
		aboutButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				aboutButton.setImageResource(R.drawable.ico5pressed);
				new launchActivity(AboutActivityWebView.class).execute();
			}
		});
		
		feedbackButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new launchActivity(FeedbackActivity.class).execute();
				feedbackButton.setImageResource(R.drawable.ico9pressed);
			}
		});
		
		punctureButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				punctureButton.setImageResource(R.drawable.ico7pressed);
				new launchActivity(AllInOneActivity.class, "Puncture").execute();
			}
		});
		
	}
	
	//Using OnPause to reset icon image once it is pressed
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		final ImageButton tagButton = (ImageButton) findViewById(R.id.TagButton);
	    final ImageButton searchButton = (ImageButton) findViewById(R.id.SearchButton);
	    final ImageButton reminderButton = (ImageButton) findViewById(R.id.ReminderButton);
	    final ImageButton favButton = (ImageButton) findViewById(R.id.FavButton);
	    final ImageButton towButton = (ImageButton) findViewById(R.id.TowButton);
	    final ImageButton aboutButton = (ImageButton) findViewById(R.id.AboutButton);
	    final ImageButton feedbackButton = (ImageButton) findViewById(R.id.FeedbackButton);
	    final ImageButton fineButton = (ImageButton) findViewById(R.id.FineButton);
	    final ImageButton punctureButton = (ImageButton) findViewById(R.id.PunctureButton);
	    
	    searchButton.setImageResource(R.drawable.ico1);
	    tagButton.setImageResource(R.drawable.ico2);
	    reminderButton.setImageResource(R.drawable.ico4);
	    favButton.setImageResource(R.drawable.ico6);
	    towButton.setImageResource(R.drawable.ico8);
	    aboutButton.setImageResource(R.drawable.ico5);
	    feedbackButton.setImageResource(R.drawable.ico9);
	    fineButton.setImageResource(R.drawable.ico3);
	    punctureButton.setImageResource(R.drawable.ico7);
	    
	    SharedPreferences prefs = getSharedPreferences("General", 0);
		String userName = prefs.getString("userName", null);
		TextView tv = (TextView) findViewById(R.id.mainLayoutUserName);
		tv.setText("Welcome "+userName+" !!");
		
		if(userName==null)
		{
			finish();
		}
	   
		TextView copyRight = (TextView) findViewById(R.id.copyRightText);
		counter =0;
		copyRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				easterEgg();
			}
		});
		
	}
	 int counter;
	 long initialTime;
	 long lastTime;
	public void easterEgg()
	{
		Log.i("EasterEgg", "Inside Method");
		lastTime = System.currentTimeMillis();
		if(counter == 0)
		{
			Log.i("EasterEgg", "counter 0");
			Log.i("EasterEgg", "Initial Time Set");
			initialTime = System.currentTimeMillis();
		}
		counter++;
		if(counter == 7 && (lastTime - initialTime) < 5000)
		{
			Log.i("EasterEgg", "success");
			Dialog dialog = new EasterEgg(MainActivity.this);
			dialog.setCancelable(false);
			dialog.setTitle("Makeshift Developers");
			dialog.show();
			counter = 0;
		}
		if(counter > 7 || (lastTime - initialTime) > 5000)
		{
			Log.i("EasterEgg", "clicked out or timeout");
			counter = 0;
		}
		
		Log.i("EasterEgg", "value of counter " + counter);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	
	}
	
	private class usageStatisticsReport extends AsyncTask<String, Void, Void>
	{

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("userName", userName);
				String result = JSONSendReceiveHelper.arbitraryHttpPost(webAddress+"addUsageStatistics.php", jsonObject);
				Log.i("usage Report", result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
			return null;
		}
		
	}
	
	private class launchActivity extends AsyncTask<Void, Integer, Void>
	{
		private Class<?> className;
		String extra;
		private Intent intent;
		ProgressDialog progress;
		
		launchActivity(Class<?> className, String extra)
		{
			this.className = className;
			
			this.extra = extra;
			
			
		}
		launchActivity(Class<?> className)
		{
			this.className = className;
			
			this.extra = null;
			
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			progress = new ProgressDialog(MainActivity.this);
			progress.setCancelable(false);
			progress.setTitle("Loading");
			progress.setMessage("Please Wait ...");
			progress.setIndeterminate(true);
			progress.show();
		
		}
		@Override
		protected Void doInBackground(Void... params) {
			
			if(extra == null)
			{
				intent = new Intent(MainActivity.this, className);
				startActivity(intent);
				
			}
			else
			{
				intent = new Intent(MainActivity.this, className);
				intent.putExtra("activityName", extra);
				startActivity(intent);
				
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(progress != null)
			{
				progress.dismiss();
				
			}
		}
		
	}
}