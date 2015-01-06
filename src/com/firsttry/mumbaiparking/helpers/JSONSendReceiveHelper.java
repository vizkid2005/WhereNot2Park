package com.firsttry.mumbaiparking.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import com.firsttry.mumbaiparking.*;

import android.content.SharedPreferences;
import android.content.SharedPreferences.*;
import android.util.Log;

public class JSONSendReceiveHelper {

	private static final String TAG = "sending";
	//private static final String webAddress = "http://192.168.0.106/mumbaiparking/";
	private static final String webAddress = "http://www.wheretopark.comze.com/";
	String userName;

	public static JSONObject SendHttpPost(String URL, JSONObject jsonObjSend) {

		try {
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);

			StringEntity se;
			se = new StringEntity(jsonObjSend.toString());

			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");
			httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

			long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
			Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");
	        Log.i("postData", response.getStatusLine().toString());
			
			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				// Read the content stream
				InputStream instream = entity.getContent();
				String resultString= convertStreamToString(instream);
				instream.close();
				resultString = resultString.substring(0,resultString.length()); // remove wrapping "[" and "]"
				Log.i("received", resultString);
			/*	// Transform the String into a JSONObject
				JSONObject jsonObjRecv = new JSONObject(resultString);
				// Raw DEBUG output of our received JSON object:
				Log.i(TAG,"<JSONObject>\n"+jsonObjRecv.toString()+"\n</JSONObject>");

				return jsonObjRecv;*/
				return null;
				
			} 

		}
		catch (Exception e)
		{
			// More about HTTP exception handling in another tutorial.
			// For now we just print the stack trace.
			e.printStackTrace();
		}
		return null;
	}
	
	public static String arbitraryHttpPost(String URL, JSONObject jsonObjSend){
		
		try
		{
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost httpPostRequest = new HttpPost(URL);

			StringEntity se;
			se = new StringEntity(jsonObjSend.toString());

			// Set HTTP parameters
			httpPostRequest.setEntity(se);
			httpPostRequest.setHeader("Accept", "application/json");
			httpPostRequest.setHeader("Content-type", "application/json");
			httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

			long t = System.currentTimeMillis();
			HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
			Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");
	        Log.i("postData", response.getStatusLine().toString());
			
			// Get hold of the response entity (-> the data):
			HttpEntity entity = response.getEntity();

			if (entity != null) 
			{
				// Read the content stream
				InputStream instream = entity.getContent();
				String resultString= convertStreamToString(instream);
				instream.close();
				resultString = resultString.substring(0,resultString.length()); // remove wrapping "[" and "]"
				Log.i("received", resultString);
				return resultString;
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return "No Response Received";
	}	
		

	public static int getMaxSubId()
	{
		long t = System.currentTimeMillis();
		HttpURLConnection connection;
		OutputStreamWriter request = null;
		URL url = null;   
		String responseString = null;         
		int temp=0;
		try
		{
			url = new URL(webAddress+"getSubId.php");
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestMethod("POST");
			responseString = convertStreamToString(connection.getInputStream());
			Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");
			Log.i("DataFromServer", responseString);
			temp = Integer.parseInt(responseString);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return temp;
	}
	
	public static JSONArray getMarkersFromDatabase(String type)
	{
		long t = System.currentTimeMillis();
		HttpURLConnection connection;
		URL url = null;   
		JSONArray jsonObjRecv =null;
		String responseString = null;     
		
		try
		{
			if(type.equals("parking"))
			{
			url = new URL(webAddress+"getMarkersFromDb.php");
			}
			else if(type.equals("towing"))
			{
				url = new URL(webAddress+"getTowingFromDb.php");
			}
			else if(type.equals("puncture"))
			{
				url = new URL(webAddress+"getPunctureFromDb.php");
			}
			
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestMethod("POST");
			Log.i(TAG, "List of markers received in [" + (System.currentTimeMillis()-t) + "ms]");
			responseString = convertStreamToString(connection.getInputStream());
			Log.i("Response",responseString);
			jsonObjRecv = new JSONArray(responseString);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return jsonObjRecv;
	}
	
	/*Implementing Polymorphism :p */
	public static JSONArray getMarkersFromDatabase(String type, String userName)
	{
		long t = System.currentTimeMillis();
		HttpURLConnection connection;
		URL url = null;   
		JSONArray jsonObjRecv =null;
		String responseString = null;     
		
		try
		{
			
			String userNameNew = userName.replace(' ', '*');
			String tempUrl = webAddress+"getFavoriteFromDb.php?userName="+userNameNew;
			Log.i("webaddressTemp",tempUrl);
			url = new URL(tempUrl);
			Log.i("webaddress",url.toString());
			
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestMethod("POST");
			Log.i(TAG, "List of markers received in [" + (System.currentTimeMillis()-t) + "ms]");
			responseString = convertStreamToString(connection.getInputStream());
			Log.i("Response",responseString);
			jsonObjRecv = new JSONArray(responseString);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return jsonObjRecv;
	}
	
	
	
	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 * 
		 * 
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
