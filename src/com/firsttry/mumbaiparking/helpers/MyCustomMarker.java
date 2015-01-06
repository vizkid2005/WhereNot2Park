package com.firsttry.mumbaiparking.helpers;

import java.util.List;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class MyCustomMarker {

	int subId,cost;
	String userId;
	String type;
	List<LatLng> ll;
	float rating;
	
	public MyCustomMarker(int subId, List<LatLng> ll,int cost,String type,float rating, String userId)
	{
		this.subId=subId;
		this.ll=ll;
		this.cost=cost;
		this.type=type;
		this.rating=rating;
		this.userId=userId;
		
		Log.i("Cost from constructor = ",cost+" ");
		Log.i("subid from constructor = ",subId+" ");
		Log.i("Type from constructor = ",type+" ");
		Log.i("Rating from constructor = ",rating+" ");
		Log.i("UserId from constructor = ",userId+" ");
		int i = ll.size();
		for(int j=0;j<i;j++)
		{		
			Log.i("Latitude from constructor = ",ll.get(j).latitude+" ");
			Log.i("Longitude from constructor = ",ll.get(j).longitude+" ");
		}
	}
	
	/*
	 * Using Polymorphism of constructor.
	 * Why ? you ask. This second constructor handles constraints on parking constraints
	 * See line the second line of the parameters.
	 * 
	 */
	int timeTo,timeFrom;
	String days,vehicleType;
	public MyCustomMarker(int subId, List<LatLng> ll, int cost,String type,float rating, String userId, 
			String days, int timeFrom, int timeTo, String vehicleType)
	{
		this.subId=subId;
		this.ll=ll;
		this.cost=cost;
		this.type=type;
		this.rating=rating;
		this.userId=userId;
		this.days=days;
		this.timeTo=timeTo;
		this.timeFrom=timeFrom;
		this.vehicleType=vehicleType;
		
		Log.i("Cost from constructor 2= ",cost+" ");
		Log.i("subid from constructor 2= ",subId+" ");
		Log.i("Type from constructor 2= ",type+" ");
		Log.i("Rating from constructor 2= ",rating+" ");
		Log.i("UserId from constructor 2= ",userId+" ");
		Log.i("days from constructor 2= ",days+" ");
		Log.i("timeTo from constructor 2= ",timeTo+" ");
		Log.i("timeFrom from constructor 2= ",timeFrom+" ");
		Log.i("vehicleType from constructor 2= ",vehicleType+" ");
		
		int i = ll.size();
		for(int j=0;j<i;j++)
		{		
			Log.i("Latitude from constructor 2= ",ll.get(j).latitude+" ");
			Log.i("Longitude from constructor 2= ",ll.get(j).longitude+" ");
		}
		
		
	}
	/*
	 * Lesson Learnt : You can not access the values of the object from outside the 
	 * class, ie: Encapsulation, You have to have to specify methods return values when asked for
	 * Good Job OOPS, I see what you you did there. 
	 * #Saavn : Dil aisa kisine mera toda, amanush
	 */
	public int getCost()
	{
		return cost;
	}
	public int getSubId()
	{
		return subId;
	}
	public String getUserId()
	{
		return userId;
	}
	public float getRating()
	{
		return rating;
	}
	public String getType()
	{
		return type;
	}
	public List<LatLng> getLatLngList()
	{
		return ll;
	}
	public String getVehicleType()
	{
		return vehicleType;
	}
	public String getDays()
	{
		return days;
	}
	public int getTimeTo()
	{
		return timeTo;
	}
	public int getTimeFrom()
	{
		return timeFrom;
	}
}
