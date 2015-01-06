package com.firsttry.mumbaiparking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.firsttry.mumbaiparking.helpers.JSONSendReceiveHelper;
import com.firsttry.mumbaiparking.helpers.MyCustomMarker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.makeshift.wheretopark.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AllInOneActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

	private static final int GPS_ERRORDIALOG_REQUEST = 2001;
	private static final double MUMBAI_LAT = 19.0188675;
	private static final double MUMBAI_LNG = 72.8433237;
	private static final double INIT_ZOOM = 11;
	private static final double FIND_ZOOM = 15;
	private static final int REQUEST_CODE = 5253;
	
	//private static final String webAddress = "http://192.168.0.106/mumbaiparking/";
	private static final String webAddress = "http://www.wheretopark.comze.com/";
	
	GoogleMap mMap;
	MapView mMapView;
	LocationClient mlocation;
	Marker marker;
	Context context;
	Button findButton;
	EditText findBox;
	Map<Marker, Integer> allMarkerSubId;
	String activityName;
	int markerColor;
	String userName;
	BitmapDescriptor markerIcon;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        userName = getUserName();
        
        Intent tempIntent = getIntent();
        activityName = tempIntent.getStringExtra("activityName");
        this.setTitle(activityName);
        
       
        //Check if Play Services is available
        if(servicesOK()&& isInternetConnected()){
        	setContentView(R.layout.searchactivity);
        	if(initMap())
        	{
        		
        		//GetMarkers from database only if the list is empty
        		runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(myCustomMarkerList.isEmpty()){
			        		try {
								String result = new getInitialMarkersFromDb().execute("").get();
								Log.i("Success", result);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (ExecutionException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
			        	}
					}
				});
        		
        		
        		allMarkerSubId = new HashMap<Marker, Integer>();
        		//Initialize the search bar and button
        		findBox = (EditText) findViewById(R.id.searchBarSearchActivity);
        		findButton = (Button) findViewById(R.id.searchButtonSearchActivity);
        		goToInitialLocation();
        			
        		findButton.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getAction() == MotionEvent.ACTION_DOWN)
						{   //Onclick 
							String enteredValue = findBox.getText().toString();
							findButton.setBackgroundResource(R.drawable.buttonbgpressed);
							try{
								//Get the address using the Async class
								/*
								 * Lesson Learnt : Never do network dependent tasks on the main thread
								 * Use AsyncTask class to do that 
								 */
								List<Address> address =  new SearchAddressAsync().execute(enteredValue+", Maharashtra").get();
								if(!address.isEmpty()) //Try to go to location only if valid address is returned
								{
									double lat =address.get(0).getLatitude();
									double lng = address.get(0).getLongitude();
									goToLocationOnFind(lat, lng);
									//Hide Keyboard , Better Aesthetics
									InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
									inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
								}
							}
							catch(Exception e)
							{
								//Just catch and show the exception
								e.printStackTrace();
							}
						}
						else
						{// On button release, Just better aesthetics 
							findButton.setBackgroundResource(R.drawable.buttonbg);
						}
						return false;
					}
				});
        		
        		/*mlocation is used for getting current location*/
        		mlocation = new LocationClient(this,this,this);
        		mlocation.connect();
        		mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
				
					@Override
					public void onMapLongClick(LatLng ll) {
						// TODO Auto-generated method stub
						/*
						 * Not Needed, just for debugging and getting the coordinates of
						 * the pressed location
						 * #Saavn : Piya tu ab toh aaja 
						 */
						String latlng = "";
						double lat = ll.latitude;
						double lng = ll.longitude;
						latlng = "Latitude : "+lat+"  Long : "+lng;
						context =getApplicationContext();
						Toast.makeText(context, latlng, Toast.LENGTH_LONG).show();
					}
				});
        		
        		
        		
        		/*
    			 * When all is said and done , its time to plot the markers on the map
    			 * 
    			 */
        		runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						markSpotsFromList();
					}
				});
    			
    			/*
    			 * Setting the OnMarkerClickListener will allow us to get the request data that will be used in 
    			 * data mining and visualization
    			 */
    			
    			
    			mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
					
					@Override
					public boolean onMarkerClick(Marker marker) {
						
						marker.showInfoWindow();
						LatLng currentLatLng = marker.getPosition();
						String response= null;
						try {
							response = new setRequestData().execute(currentLatLng).get();
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e)
						{
							e.printStackTrace();
						}
						
						Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
						return false;
					}
				});
    			
    			/*
    			 * Setting the onInfoWindowClickListener, it will open a new activity
    			 * to edit the values
    			 * 
    			 */
    			mMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
					
					@Override
					public void onInfoWindowClick(Marker marker) {
						
						int clickedMarkerSubId = allMarkerSubId.get(marker);
						Toast.makeText(getApplicationContext(), "SubId of this marker : "+clickedMarkerSubId, Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(AllInOneActivity.this,UpdateSpotActivity.class);
						intent.putExtra("subId", clickedMarkerSubId);
						intent.putExtra("activityName", activityName);
						startActivityForResult(intent,REQUEST_CODE);
					}
				});
    			
    			/*
    			 * Setting up custom infowindow layout 
    			 * 
    			 */
    			
    		/*	mMap.setInfoWindowAdapter(new InfoWindowAdapter() {
    				
					
					@Override
					public View getInfoWindow(Marker arg0) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public View getInfoContents(Marker arg0) {
						// TODO Auto-generated method stub
						
						View v = getLayoutInflater().inflate(R.layout.map_infowindow_layout, null);
						
						TextView ratingText = (TextView) findViewById(R.id.ratingInfoWindow);
						TextView daysText = (TextView) findViewById(R.id.daysInfoWindow);
						TextView timingText = (TextView) findViewById(R.id.timingInfoWindow);
						TextView vehiclesText = (TextView) findViewById(R.id.vehicleInfoWindow);
						
						MyCustomMarker clickedMarker = markersAndObjects.get(arg0);
						
						
						
						Log.i("infowindow", "Inside info window getinfo contents");
						
						return null;
					}
				});*/
        	}
        	else{
        		Toast.makeText(this, "Map not available", Toast.LENGTH_SHORT).show();
        	}
        }
        else
        {
        	setContentView(R.layout.no_internet_view);
        }
    }
	
	//This class searches the address entered in the search bar
    // This is a class within class implementation
    private class SearchAddressAsync extends AsyncTask<String, Void, List<Address>>
    {

		@Override
		protected List<Address> doInBackground(String... params) {
			
			Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;
 
            try {
                // Getting a maximum of 3 Address that matches the input text
                
            	addresses = geocoder.getFromLocationName(params[0], 1);
                if(addresses.isEmpty())
                {
                	return null;
                }
                Log.i("Geocoder",addresses.get(0).getLatitude()+" ");
                Log.i("Geocoder",addresses.get(0).getLongitude()+" ");
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            return addresses;
		}
    
    }
    
    private class setRequestData extends AsyncTask<LatLng, Void, String>
    {

		@Override
		protected String doInBackground(LatLng... params) {
			
			Location currentLocation=null;
			//First we get the current location 
			try{
				currentLocation = mlocation.getLastLocation();
				}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
			
			double currentLat = currentLocation.getLatitude();
			double currentLng = currentLocation.getLongitude();
			
			//Then we get the Lat and Lng of the clicked location
			//LatLng clickedMarker = params[0].getPosition();
			double clickedLat = params[0].latitude;
			double clickedLng = params[0].longitude;
			
			String response = "No response received";
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("currentLat", currentLat);
				jsonObject.put("currentLng", currentLng);
				jsonObject.put("clickedLat", clickedLat);
				jsonObject.put("clickedLng", clickedLng);
				jsonObject.put("userName", userName);
				jsonObject.put("activityName",activityName);
				
				String url = webAddress+"setRequestData.php";
				
				response = JSONSendReceiveHelper.arbitraryHttpPost(url, jsonObject);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return response;
		}
    	
    }
	
  //Below Declarations are relevant only for the class below
    ProgressDialog pd;
    List<MyCustomMarker> myCustomMarkerList = new ArrayList<MyCustomMarker>();
    MyCustomMarker myCustomMarkerObject;
    private class getInitialMarkersFromDb extends AsyncTask<String, Void, String>
    {

    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		//Setting and showing the progress dialog
    		pd = new ProgressDialog(AllInOneActivity.this);
    		pd.setCancelable(false);
    		pd.setTitle(R.string.loading);
    		/*
    		 * Was setting the message using R.string.pleaseWait, 
    		 * but it was giving and error, stating it was an int
    		 */
    		pd.setMessage("Please Wait ... ");
    		pd.setIndeterminate(true);
    		pd.show();
    	}
    	
		@Override
		protected String doInBackground(String... params) {
			
			JSONArray jsonArray = null;
			
			if(activityName.equals("Favorite"))
	        {
				jsonArray = JSONSendReceiveHelper.getMarkersFromDatabase("favorite",userName);
	        }
			else if(activityName.equals("Search"))
				jsonArray = JSONSendReceiveHelper.getMarkersFromDatabase("parking");
			
			else if(activityName.equals("Towing"))
				jsonArray = JSONSendReceiveHelper.getMarkersFromDatabase("towing");
			
			else if(activityName.equals("Puncture"))
				jsonArray = JSONSendReceiveHelper.getMarkersFromDatabase("puncture");
			/*
			 * This is whats going to happen , 
			 * We will extract each subId and its details and make it into an object
			 * of a custom class we will define, MyCustomMarker.java
			 * We will then make a list of objects of MyCustomMarker and return that.
			 * Then from that list we will extract each marker and set the values on the map.
			 */
			
			int noOfNodes = jsonArray.length();
			for(int i=0;i<noOfNodes;i++)
			{
				/*
				 * Lesson Learnt : List is an Interface and you can not initialize 
				 * an object with an interface 
				 * ie : List<LatLng> latlngList = new List<LatLng>(); will not work
				 * You have to initialize it using an implementation of List
				 * ie: ArrayList, LinkedList, etc.
				 * 
				 */
				List<LatLng> latlngList = new ArrayList<LatLng>();
				try {
					/*
					 * Adding the variable that will contain the various constraints
					 * #Saavn : iski uski, 2 states
					 */
					int timeFrom,timeTo;
					String days,vehicleType;
					
					//This is as before, unchanged
					JSONObject object = (JSONObject) jsonArray.get(i);
					int cost = object.getInt("cost");
					int subId = object.getInt("subid");
					String type = object.getString("type");
					float rating = (float) object.getDouble("rating");
					String userId= object.getString("userid");
					//Bogus values
		    		timeFrom=timeTo=1234;
		    		days=vehicleType="nonsense";
		    		
					if(activityName.equals("Search"))
					{
						timeFrom=object.getInt("timefrom");
						timeTo=object.getInt("timeto");
						days=object.getString("days");
						vehicleType=object.getString("vehicletype");
					}
					
					JSONArray markerArray = object.getJSONArray("markers");
					for(int j =0;j<markerArray.length();j++)
					{
						JSONObject markerObject = (JSONObject) markerArray.get(j);
						double lat = markerObject.getDouble("latitude");
						double lng = markerObject.getDouble("longitude");
						int id = markerObject.getInt("id");
						Log.i("MarkerObject", " Getting marker object"+j);
						Log.i("Lat = ",lat+" ");
						Log.i("Lng = ",lng+" ");
						Log.i("ID = ",id+" ");
						
						LatLng ll = new LatLng(lat, lng);
						latlngList.add(ll);
					}
					
					if(activityName.equals("Search")|| activityName.equals("Favorite"))
					{
						myCustomMarkerObject = new MyCustomMarker(subId, latlngList, cost, type, rating, userId, days, timeFrom, timeTo, vehicleType);
					}
					else
					{
						myCustomMarkerObject = new MyCustomMarker(subId, latlngList, cost, type, rating, userId);
					}
					myCustomMarkerList.add(myCustomMarkerObject);
					
					Log.i("Cost = ",cost+" ");
					Log.i("subid = ",subId+" ");
					Log.i("Type = ",type+" ");
					Log.i("Rating = ",rating+" ");
					Log.i("UserId = ",userId+" ");
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return "Great Success !!";
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(pd != null)
			{
				pd.dismiss();
			}
		}
    	
    }
    
    /*
     * Instead of plottedMarkers, I'm using a HashMap, with each marker I'm storing its subid
     * It will be helpful to bundle the markers' subid when I open the activity to update the marker properties
     */

    List<Polygon> plottedPolygons;
    PolygonOptions polyOptions;
    MarkerOptions markerOptions;
    HashMap<Marker, MyCustomMarker> markersAndObjects;
    public void markSpotsFromList() 
    {
    	/*
    	 * Now that we have the list of myCustomMarkers, we will now begin to 
    	 * plot the markers on the map
    	 * 
    	 */
    	/*
    	 * We have set the options for filling the polygon below
    	 */
    	
    	plottedPolygons = new ArrayList<Polygon>();
    	markersAndObjects = new HashMap<Marker, MyCustomMarker>();
    	int cost,subId,timeFrom,timeTo;
    	String userId,days,vehicleType;
    	float rating,sumLat,sumLng;
    	LatLng centroidOfPolygon;
    	String type;
    	List<LatLng> llList;
    	int sizeOfList = myCustomMarkerList.size();
    	
    	for(int i=0;i<sizeOfList;i++)
    	{
    		MyCustomMarker currentCustomMarker = myCustomMarkerList.get(i);
    		
    		polyOptions= new PolygonOptions().fillColor(0x3033b5e5)
        	.strokeColor(0x3033b5e5)
        	.strokeWidth(5.0f);
    		cost = currentCustomMarker.getCost();
    		rating = currentCustomMarker.getRating();
    		userId = currentCustomMarker.getUserId();
    		subId = currentCustomMarker.getSubId();
    		type = currentCustomMarker.getType();
    		llList = currentCustomMarker.getLatLngList();
    		
    		//Bogus values
    		timeFrom=timeTo=1234;
    		days=vehicleType="nonsense";
    		
    		if(activityName.equals("Search"))
    		{
    			timeTo = currentCustomMarker.getTimeTo();
    			timeFrom = currentCustomMarker.getTimeFrom();
    			days = currentCustomMarker.getDays();
    			vehicleType = currentCustomMarker.getVehicleType();
    			Log.i("markervalues"," "+timeFrom+" "+timeTo+" "+days+" "+vehicleType);
    		}
    		
			sumLat=sumLng=0;
			for(int j=0;j<llList.size();j++)
    		{
				Log.i("marker #494 : ", "i : "+i+" j:"+j);
    			LatLng llObject = llList.get(j);
    			sumLat += llObject.latitude;
    			sumLng += llObject.longitude;
    			polyOptions.add(llObject);
    		}
			Log.i("plotting polygon #533 : ", polyOptions.toString());
			centroidOfPolygon = findCentroidOfPolygon(sumLat, sumLng, llList.size());
			String markerTitle = "";
			if(type.equals("single") || type.equals("multiple"))
	        {
	        	markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.parkingmarker);
	        	if(activityName.equals("Favorite"))
	        	{
	        		markerTitle="Favorite Spot";
	        	}
	        	else
	        	{	        		
	        		if(timeFrom==99 || timeTo ==99)
	        			markerTitle = "Cost:"+cost+"|Days:"+days+"|Vehicle:"+vehicleType;
	        		else
	        			markerTitle="Cost:"+cost+"|Days:"+days+"|"+timeFrom+":00->"+timeTo+":00|Vehicle:"+vehicleType;
	        	}
	        }
	        else if(type.equals("tow"))
	        {
	        	markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.towingmarker);
	        	markerTitle = "Towing Station";
	        }
	        else if(type.equals("puncture"))
	        {
	        	markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.puncturemarker);
	        	markerTitle = "Puncturewala";
	        }
			
			markerOptions = new MarkerOptions()
			.icon(markerIcon)
			.position(centroidOfPolygon)
			.title(markerTitle+"")
			.snippet("Rating : "+rating+"/5| Marked by : "+userId);
			Marker plottedMarker = mMap.addMarker(markerOptions);
			allMarkerSubId.put(plottedMarker, subId);
			plottedPolygons.add(mMap.addPolygon(polyOptions));
			
			/*
			 * Add the marker and the related MyCustomMArker Object to the HAshMAp 
			 * Will be used to fill in the info window details 
			 */
			try
			{
				markersAndObjects.put(plottedMarker, currentCustomMarker);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
    	}
    }
    
  //This method is invoked when user searches the location in the Search bar
    private void goToLocationOnFind(double lat, double lng) {
    	LatLng ll = new LatLng(lat, lng);
    	CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, (float) FIND_ZOOM);
    	mMap.animateCamera(update);
    	String latlng = "Latitude : "+ll.latitude+" Logitude : "+ll.longitude;
    	//setMarker("Current Location", lat, lng);
    //	Toast.makeText(this, latlng, Toast.LENGTH_SHORT).show();
    	
    }
    
    public  String getUserName()
    {
    	SharedPreferences prefs = getSharedPreferences("General", 0);
    	String userName = prefs.getString("userName", null);
    	
    	return userName;
    }
    
    //Invoked just when the activity is invoked
    private void goToInitialLocation() {
    	LatLng ll = new LatLng(MUMBAI_LAT, MUMBAI_LNG);
    	CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, (float) INIT_ZOOM);
    	mMap.animateCamera(update);
    	String latlng = "Latitude : "+ll.latitude+" Logitude : "+ll.longitude;
    	//setMarker("Current Location", lat, lng);
    //	Toast.makeText(this, latlng, Toast.LENGTH_SHORT).show();
    	
    }
    
  //When the user presses the go to current location button
    public void goToCurrentLocation(){
    	
    	Location currentLocation = mlocation.getLastLocation();
    	
    	if(currentLocation == null)
    	{
    		Toast.makeText(this, "Can't get location Line 49", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
    		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, (float) 17);
    		mMap.animateCamera(update);
    		String latlng = "Latitude : "+ll.latitude+" Logitude : "+ll.longitude;
    		//setMarker("Current Location", currentLocation.getLatitude(), currentLocation.getLongitude() );
    	//	Toast.makeText(this, latlng, Toast.LENGTH_SHORT).show();
    	}
    	
    }
    
    public void setMarker(String title, double lat, double lng)
    {
    	/*
    	 * Will have to add more info for the information window
    	 */
    	LatLng ll = new LatLng(lat,lng);
    	
    	if(marker != null)
    	{
    		marker.remove();
    	}
    	MarkerOptions options = new MarkerOptions();
    	mMap.addMarker(options.title(title).position(ll).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }
    
    public boolean servicesOK()
	{
		int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		
		if(isAvailable == ConnectionResult.SUCCESS)
		{
			return true;
		}
		else if(GooglePlayServicesUtil.isUserRecoverableError(isAvailable))
		{
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
			dialog.show();
		}
		else
		{
			Toast.makeText(this, "Cant Connect to play services ", Toast.LENGTH_SHORT).show();
		}
		return false;
	}
    
    
    public boolean initMap(){
		if(mMap == null)
		{
			SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			mMap = mapFrag.getMap();
		}
		return(mMap !=null);
	}
    
	/*
     * Provide this method with a LatLng Object and it will give you the
     * address(Eg : Andheri,Byculla)  of the object
     */
    public String reverseGeo(LatLng ll)
    {
    	String locality=null;
    	List<Address> address = null;
    	Geocoder geo = new Geocoder(this, Locale.getDefault());
    	try{
    	address = geo.getFromLocation(ll.latitude, ll.longitude, 1);
    	}
    	catch(Exception e)
    	{
    		
    	}
    	Address temp = address.get(0);
    	locality = temp.getSubLocality();
    	
    	return locality;
    }
    
    public boolean isInternetConnected()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		
		return netInfo != null && netInfo.isConnected();
	}

	public LatLng findCentroidOfPolygon(float sumLat, float sumLng, int numberOfMarkers)
    {
    	float centerLat = sumLat/numberOfMarkers;
    	float centerLng = sumLng/numberOfMarkers;
		
    	LatLng centroid = new LatLng(centerLat, centerLng);
    	
    	return centroid;
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(isInternetConnected())
        	getMenuInflater().inflate(R.menu.main, menu);
        else
        	getMenuInflater().inflate(R.menu.no_menu, menu);
        return true;
    }
 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		int itemId = item.getItemId();
		if (itemId == R.id.currentLoc) {
			goToCurrentLocation();
		} else if (itemId == R.id.action_settings) {
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
    protected void onStop() {
    	super.onStop();
    }
    @Override
    protected void onResume() {
    	super.onResume();
    /*	String action = getIntent().getAction();
    	
    	if(action==null || !action.equals("onCreate"))
    	{
    		
        	Intent i = new Intent(this,AllInOneActivity.class);
        	i.putExtra("activityName", activityName);
        	startActivity(i);
        	finish();
    	}
    	else
    	{
    		getIntent().setAction(null);
    	}*/
    	
    }
    
    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(arg0, arg1, arg2);
    	Intent i = new Intent(this,AllInOneActivity.class);
    	i.putExtra("activityName", activityName);
    	startActivity(i);
    	finish();
    }
    
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	//finish();
    }
    
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "Internet Connection Failed", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnected(Bundle arg0) {
		Toast.makeText(this, "Connected to Mumbai Parking", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDisconnected() {
		
	}
	
	public void restartActivity()
	{
		finish();
    	Intent i = getIntent();
    	startActivity(i);
	}
		
	
}
