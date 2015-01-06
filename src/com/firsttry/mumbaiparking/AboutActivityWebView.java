package com.firsttry.mumbaiparking;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class AboutActivityWebView extends Activity {

	//private static final String webAddress = "http://192.168.0.106/mumbaiparking/";
	private static final String webAddress = "http://www.wheretopark.comze.com/About/";
		
	WebView mWebView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.about_web_view);
		
		String url = webAddress+"MumbaiParkingTutorial.html";
		
		mWebView = new WebView(this);
		
		// mWebView.getSettings().setJavaScriptEnabled(true);
		 
		final Activity activity = this;
		
		mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });
	
		mWebView .loadUrl(url);
        setContentView(mWebView );
	}
}
