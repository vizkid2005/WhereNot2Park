package com.firsttry.mumbaiparking;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import com.firsttry.mumbaiparking.helpers.JSONSendReceiveHelper;
import com.makeshift.wheretopark.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

public class FeedbackActivity extends Activity {

	//private static final String webAddress = "http://192.168.0.106/mumbaiparking/";
	private static final String webAddress = "http://www.wheretopark.comze.com/";
	String userName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feedback_layout);
		
		Button doneButton = (Button) findViewById(R.id.doneFeedback);
		final RatingBar designBar = (RatingBar) findViewById(R.id.designRatingBar);
		final RatingBar usabilityBar = (RatingBar) findViewById(R.id.usabilityRatingBar);
		final RatingBar availabilityBar = (RatingBar) findViewById(R.id.AvailabilityRatingBar);
		final RatingBar intuitivityBar = (RatingBar) findViewById(R.id.IntuitivityRatingBar);
		final EditText commentsBox = (EditText) findViewById(R.id.commentsBoxFeedback);
		
		SharedPreferences prefs = getSharedPreferences("General", 0);
		userName = prefs.getString("userName", null);
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
		doneButton.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				
				float design = designBar.getRating();
				float usability = usabilityBar.getRating();
				float availability = availabilityBar.getRating();
				float intuitivity = intuitivityBar.getRating();
				String comments = commentsBox.getText().toString();
				JSONObject jsonObject;
				if(design == 0)
				{
					Toast.makeText(getApplicationContext(), "Rate our Overall App Design !!", Toast.LENGTH_SHORT).show();
				}
				else if(usability == 0)
				{
					Toast.makeText(getApplicationContext(), "Tell us how Usable is the App !!", Toast.LENGTH_SHORT).show();
				}
				else if(availability ==0 )
				{
					Toast.makeText(getApplicationContext(), "Rate the availabilty of the servers !!", Toast.LENGTH_SHORT).show();
				}
				else if(intuitivity == 0)
				{
					Toast.makeText(getApplicationContext(), "Tell us how easy it is to understand the features !!", Toast.LENGTH_SHORT).show();
				}
				else if(comments.equals(""))
				{
					Toast.makeText(getApplicationContext(), "Please tell us how we can improve !!", Toast.LENGTH_SHORT).show();
				}
				else
				{
					String response=null;
					try {
						jsonObject = new JSONObject();
						jsonObject.put("design", design);
						jsonObject.put("usability", usability);
						jsonObject.put("availability", availability);
						jsonObject.put("intuitivity", intuitivity);
						jsonObject.put("comments", comments);
						jsonObject.put("username", userName);
						response = new sendFeedbackToServer().execute(jsonObject).get();
					} catch (Exception e) {
						e.printStackTrace();
					} 
					
					Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
					finish();
				}
			}
		});
	}
	private class sendFeedbackToServer extends AsyncTask<JSONObject, Void, String>
	{
		@Override
		protected String doInBackground(JSONObject... params) {
			// TODO Auto-generated method stub
			String response=null;
			
			JSONObject jsonObject = params[0];
			
			response = JSONSendReceiveHelper.arbitraryHttpPost(webAddress+"giveFeedback.php", jsonObject);
			return response;
		}
	}
}