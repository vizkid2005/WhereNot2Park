package com.firsttry.mumbaiparking;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.makeshift.wheretopark.R;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.firsttry.mumbaiparking.helpers.GetNameInForeground;
import com.firsttry.mumbaiparking.helpers.AbstractGetNameTask;;

public class GoogleLoginActivity extends Activity {
	
	private static final String TAG = "PlayHelloActivity";
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    public static final String EXTRA_ACCOUNTNAME = "extra_accountname";

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

    private String mEmail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.google_login_activity);
		
		ImageButton googleButton = (ImageButton) findViewById(R.id.googleButton);
		
		googleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			//Toast.makeText(getApplicationContext(), "Google Button CLicked", Toast.LENGTH_SHORT).show();
				getUsername();
			}
		});
		
	}
	
	/** Attempt to get the user name. If the email address isn't known yet,
     * then call pickUserAccount() method so the user can pick an account.
     */
    private void getUsername() {
    	Log.i("Flow","getUsername");
        if (mEmail == null) {
            pickUserAccount();
        } else {
            if (isDeviceOnline()) {
            	Log.i("Flow","getUsername : isDeviceOnline");
                getTask(GoogleLoginActivity.this, mEmail, SCOPE).execute();
            } else {
                Toast.makeText(this, "No network connection available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** Starts an activity in Google Play Services so the user can pick an account */
    private void pickUserAccount() {
    	Log.i("Flow","pickUserAccount");
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    /** Checks whether the device currently has a network connection */
    private boolean isDeviceOnline() {
    	Log.i("Flow","isDeviceOnline");
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }


    /**
     * This method is a hook for background threads and async tasks that need to update the UI.
     * It does this by launching a runnable under the UI thread.
     */
    public void show(final String message) {
    	Log.i("Flow","show");
    	runOnUiThread(new Runnable() {
            @Override
            public void run() {
	              Log.i("Final Name", message);
	              if(!message.equals("Unknown error, click the button again")&&
	            	!message.equals("User rejected authorization.")	){
	            	
	              SharedPreferences prefs = getSharedPreferences("General", 0);
				  String userName = message;
				  SharedPreferences.Editor editor = prefs.edit();
				  editor.putString("userName", userName);
				  Log.i("OnCompleted", "usernameSet = "+userName);
				  editor.commit();
				  Intent intent = new Intent(GoogleLoginActivity.this, MainActivity.class);
				  startActivity(intent);
				  finish();
	              }
            }
        });
    }

    /**
     * This method is a hook for background threads and async tasks that need to provide the
     * user a response UI when an exception occurs.
     */
    public void handleException(final Exception e) {
    	Log.i("Flow","handleException");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException)e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                    		GoogleLoginActivity.this,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException)e).getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }
    
    private AbstractGetNameTask getTask(
    		GoogleLoginActivity activity, String email, String scope) {
    	Log.i("Flow","getTask");
    	
    	return new GetNameInForeground(activity, email, scope);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	Log.i("Flow","onActivityResult");
        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == RESULT_OK) {
            	
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                getUsername();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You must pick an account", Toast.LENGTH_SHORT).show();
            }
        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR ||
                requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == RESULT_OK) {
            handleAuthorizeResult(resultCode, data);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleAuthorizeResult(int resultCode, Intent data) {
    	Log.i("Flow","handleAuthorizeResult");
    	if (data == null) {
            show("Unknown error, click the button again");
            return;
        }
        if (resultCode == RESULT_OK) {
            Log.i(TAG, "Retrying");
            getTask(this, mEmail, SCOPE).execute();
            return;
        }
        if (resultCode == RESULT_CANCELED) {
            show("User rejected authorization.");
            return;
        }
        show("Unknown error, click the button again");
    }

}