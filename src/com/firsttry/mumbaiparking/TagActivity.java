package com.firsttry.mumbaiparking;
import java.util.ArrayList;
import java.util.List;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.makeshift.wheretopark.R;

public class TagActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener {

	private static final int GPS_ERRORDIALOG_REQUEST = 2001;
	private static final double MUMBAI_LAT = 19.0188675;
	private static final double MUMBAI_LNG = 72.8433237;
	private static final double INIT_ZOOM = 11;
	
	GoogleMap mMap;
	
	LocationClient mlocation;
	Marker marker;
	Context context;
	Polygon drawnPolygon;
	
	ArrayList<Marker> markers = new ArrayList<Marker>();
	PolygonOptions polyOptions = new PolygonOptions();
	
	//Do not call findViewById() before calling onCreate()
	Button done;
	Button cancel;
	
	TextView findBox;
	Button findButton;
	
	//These are the done and cancel buttons of the dialog
	Button dialogDone,dialogCancel;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
       
        if(servicesOK() && isInternetConnected() ){
        	
        	setContentView(R.layout.tag_activity);
        	if(initMap())
        	{
        		//Normal Map to be shown
        		mMap.setMapType(1);
        		mMap.setPadding(0, 0, 0, 0);
        		
        		done = (Button) findViewById(R.id.doneTagButton);
        		cancel = (Button) findViewById(R.id.cancelTagButton);
        		
        		//Initial Location 
        		gotoLocation(MUMBAI_LAT, MUMBAI_LNG, INIT_ZOOM);
        		mlocation = new LocationClient(this,this,this);
        		mlocation.connect();
        		
        		findBox = (EditText) findViewById(R.id.searchBarSearchActivity);
        		findButton = (Button) findViewById(R.id.searchButtonSearchActivity);
        		
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
        		
        		//Setting Long Click Listener to add Marker
        		mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
					
					@Override
					public void onMapLongClick(LatLng ll) {
						
						//To allow space for buttons, the Map is padded at the bottom
						//This method screws up on some small devices
						final float scale = getResources().getDisplayMetrics().density;
		        		int padding =  (int) (48 * scale + 0.5f);
						
		        		/*
		        		 * Before adding the point to the map, check if the point is valid
		        		 * ie: it is not a natural feature. The point cannot be in the sea, or a desert, etc.
		        		 * For that we use Google Maps Geocoding API.
		        		 */
		        		if(pointOnLand(ll))
		        		{
		        			
			        		mMap.setPadding(0, 0, 0, padding);
			        		done.setVisibility(View.VISIBLE);
			        		cancel.setVisibility(View.VISIBLE);
		        			addMarkerToArrayList(ll);
		        		}
		        		else
		        		{
		        			Toast.makeText(getApplicationContext(), "Select a valid point !!", Toast.LENGTH_SHORT).show();
		        		}
					}
				});
        	
        		cancel.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						
						if(event.getAction() == MotionEvent.ACTION_DOWN)
						{
							cancel.setBackgroundResource(R.drawable.buttonbgpressed);
							emptyMarkerArray();
							onBackPressed();
						}
						else
						{
							cancel.setBackgroundResource(R.drawable.buttonbg);
						}
						return false;
					}
				});
        		
        		done.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						
						if(event.getAction() == MotionEvent.ACTION_DOWN)
						{
							done.setBackgroundResource(R.drawable.buttonbgpressed);
							//addMarkersToDatabase();
							Intent intent = new Intent(TagActivity.this, TagFormActivity.class);
							
							/*
							 * I needed to pass the marker values tagged in this activity to be
							 * passed into TagFormActivity, the basic concept is to pass the
							 * required extras into the intent as key value pairs, I wanted to 
							 * pass the entire ArrayList<Marker> , but there was no way of doing it.
							 * So, I found this : 
							 * http://shri.blog.kraya.co.uk/2010/04/26/android-parcel-data-to-pass-between-activities-using-parcelable-classes/
							 * Just when I was going to write this, it struck me 
							 * I will just add append numbers to the keys like : lat1,lng1,lat2,lng2
							 * then extract using a for loop, limited to the number of elements I pass.
							 * I will just pass the lat,lng values. The subId and rest values will be
							 * calculated in TagFormActivity
							 * #Saavn:Pyaar hua chuppke se, 1942 Love Story 
							 */
							
							int noOfMarkers = markers.size();
						//	intent.putExtra("markerscount", noOfMarkers);
							
							ArrayList<LatLng> latLngList = new ArrayList<LatLng>();
							
							for(int i=0;i<noOfMarkers;i++)
							{
								LatLng temp = new LatLng(roundOffToFive(markers.get(i).getPosition().latitude),roundOffToFive(markers.get(i).getPosition().longitude));
								latLngList.add(temp);
								
							//	intent.putExtra("lat"+i, roundOffToFive(markers.get(i).getPosition().latitude));
							//	intent.putExtra("lng"+i, roundOffToFive(markers.get(i).getPosition().longitude));
								
							}
							
							Dialog dialog = new TagFormActivityTest(TagActivity.this,latLngList);
							
							dialog.setTitle("Tag a Spot");
							dialog.setOwnerActivity(TagActivity.this);
							
							dialogDone = (Button) dialog.findViewById(R.id.doneButtonTagFormAlt);
							dialogCancel = (Button) dialog.findViewById(R.id.cancelButtonTagFormAlt);
							
							dialog.show();
							//startActivity(intent);
						}
						else
						{
							done.setBackgroundResource(R.drawable.buttonbg);
							mMap.setPadding(0, 0, 0, 0);
							//mMap.getProjection().getVisibleRegion().latLngBounds;
			        		done.setVisibility(View.INVISIBLE);
			        		cancel.setVisibility(View.INVISIBLE);
						}
						
						return false;
					}
				});
        		
        		mMap.setOnMapClickListener(new OnMapClickListener() {
					
					@Override
					public void onMapClick(LatLng ll) {
						
						//Draw the polygon only if points exist on the map, else let the map scroll normally
						if(!(polyOptions.getPoints().isEmpty()))
						drawPolygon();
						
					}
				});
        		
        		
        	}
        	else{
        		//If some problem to connect with the map
        		Toast.makeText(this, "Map not available", Toast.LENGTH_SHORT).show();
        	}
        }
        else //If Play Services not available, show the home screen
        {
        	setContentView(R.layout.no_internet_view);
        }
    }
	
	protected boolean pointOnLand(LatLng ll) {
		List<Address> addresses = null;
		try{
		Geocoder geo = new Geocoder(TagActivity.this);
		addresses = geo.getFromLocation(ll.latitude, ll.longitude, 1);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		if(addresses.isEmpty())
		{
			return false;
		}
		
		Address a = addresses.get(0);
		float oldLat = (float) ll.latitude;
		float newLat = (float) a.getLatitude();
		float oldLng = (float) ll.longitude;
		float newLng = (float) a.getLongitude();
		
		if(Math.abs(oldLat-newLat)>0.003 || Math.abs(oldLng-newLng)>0.003)
		{
			return false;
		}
		
		return true;
	}

	
	
	private void goToLocationOnFind(double lat, double lng) {
    	LatLng ll = new LatLng(lat, lng);
    	CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 15);
    	mMap.animateCamera(update);
    	String latlng = "Latitude : "+ll.latitude+" Logitude : "+ll.longitude;
    	//setMarker("Current Location", lat, lng);
    //	Toast.makeText(this, latlng, Toast.LENGTH_SHORT).show();
    	
    }

	public float roundOffToFive(double n)
	{
		float rounded = Math.round(n*100000)/100000f;
		return rounded;
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
    
    private void gotoLocation(double lat, double lng,
			double zoom) {
    	LatLng ll = new LatLng(lat,lng);
    	CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll,(float) zoom);
    	mMap.moveCamera(update);
		
	}
    

    /*
	 * These methods have been copied from SearchActivity.java, Should have used the concept of reusability
	 * by just calling the methods, but it didn't work, #Tobedone
	 */
	public void goToCurrentLocation(){
		
		Location currentLocation = mlocation.getLastLocation();
		
		if(currentLocation == null)
		{
			Toast.makeText(this, "Can't get location Line 334", Toast.LENGTH_SHORT).show();
		}
		else
		{
			LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, (float) 17);
			mMap.animateCamera(update);
		}
		
	}

	//Temporary list to store the reference of the polyOptions, we then clear the points one by one 
    List<LatLng> tempList;

    public void emptyMarkerArray()
    {
    	
    	
    	//Remove each and every marker
    	for(int i=0;i<markers.size();i++)
    	{
    		//Get a reference for that marker from the array and remove each
    		Marker temp = markers.get(i);
    		temp.remove();
    	}
    	//This is working, the arraylist is getting cleared
    	markers.clear();
    	
    	//Get the list of points
    	tempList = polyOptions.getPoints();
    	int numberOfMarkers= tempList.size();
    	/*
    	 * Crucial : Looping through the list and removing the first element
    	 * 				Ultimately the List becomes null
    	 */
    	while(tempList.size()!=0)
    	{
    		tempList.remove(0);
    	}
    	
    	
    	//Remove only if polygon is drawn
    	if(numberOfMarkers !=1)
    	{	
    		//Then clear the polygon
    		if(drawnPolygon != null)
    			drawnPolygon.remove();
    		
    	}
    	
    	
    }
    
    public void addMarkerToArrayList(LatLng ll)
    {
    	MarkerOptions options = new MarkerOptions()
    								.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
    								.position(ll)
    								.title("Point "+markers.size());
    	
    	markers.add(mMap.addMarker(options));
    
    	//Setting the color and other properties for the filling of the polygon
    	polyOptions.fillColor(0x3033b5e5)
    	.strokeColor(0x3033b5e5)
    	.strokeWidth(5.0f)
    	.add(ll);
    }
    
	public void drawPolygon()
    {
    	//Draw Polygon only if does not exist already
    	//Otherwise multiple touches trigger multiple polygons to be drawn
    	if(drawnPolygon == null)
    	{
    		drawnPolygon = mMap.addPolygon(polyOptions);
    	}
    		
    }
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
				
		if(done.getVisibility()==View.VISIBLE)
		{
			done.setVisibility(View.INVISIBLE);
			cancel.setVisibility(View.INVISIBLE);
			mMap.setPadding(0, 0, 0, 0);
		}
		else
		{
			super.onBackPressed();

		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tag_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int itemId = item.getItemId();
		if (itemId == R.id.tagCurrentLocation) {
			goToCurrentLocation();
		}
		
		return super.onOptionsItemSelected(item);
	}

	public boolean isInternetConnected()
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		
		return netInfo != null && netInfo.isConnected();
	}
	
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
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(done.getVisibility()== View.INVISIBLE)
		{
			emptyMarkerArray();
		}
	}
    
}
