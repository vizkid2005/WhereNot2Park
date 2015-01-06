package com.firsttry.mumbaiparking.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.makeshift.wheretopark.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	ArrayList<ParkingOffence> poArrayList;
	ParkingOffence po;
	HashMap<String, ArrayList<ParkingOffence>> offenceMap;
	String[] list= { "Documents","Parking","Towing Of Vehicles","Pollution",
			 "Traffic police/Traffic signal", "Speed & Overtake", "Miscellaneous" };
	Activity context;
	
	public ExpandableListAdapter(Activity context, HashMap<String, ArrayList<ParkingOffence>> offenceMap){
		this.offenceMap=offenceMap;
		this.context= context;
		Log.i("Constructor","Inside Constructor");
		for(int i=0;i<list.length;i++)
		{
			String tag=list[i];
			Log.i("Constructor","Inside i = "+i+" Tag = "+list[i]);
			poArrayList = offenceMap.get(tag);
			Set<String> keys= offenceMap.keySet();
			
			for(int j=0;j<keys.size();j++){
				Log.i("Constructor","keys : "+keys.toArray().toString());
			}
		}
	}
	
	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		Log.i("Method","getChild + grpPos "+arg0+" childpos : "+arg1 );
		return offenceMap.get(list[arg0]).get(arg1);
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		Log.i("Method","getChildId + childid :"+arg1);
		return arg1;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		Log.i("Method","getChildView + grpPos "+groupPosition+" childpos : "+childPosition);
		//final String offence= (String) getChild(groupPosition, childPosition);
		Log.i("ChildClicked","Child : "+childPosition+" Parent: "+groupPosition);
		LayoutInflater inflater = context.getLayoutInflater();
		if (convertView == null) {
            convertView = inflater.inflate(R.layout.fines_list_item_layout, null);
        }
		
		TextView item = (TextView) convertView.findViewById(R.id.lblListItem);
		item.setText(offenceMap.get(list[groupPosition]).get(childPosition).getOffence());
		TextView section = (TextView) convertView.findViewById(R.id.sectionOffence);
		section.setText(offenceMap.get(list[groupPosition]).get(childPosition).getSection());
		TextView fine = (TextView) convertView.findViewById(R.id.fineOffence);
		fine.setText(offenceMap.get(list[groupPosition]).get(childPosition).getPenalty());
		Log.i("Child Text : ",offenceMap.get(list[groupPosition]).get(childPosition).getOffence());
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		Log.i("Method","getChildrenCount, mapTag : "+list[groupPosition]);
		Log.i("Method","getChildrenCount, offensemapcount : "+offenceMap.get(list[groupPosition]).size());
		ArrayList<ParkingOffence> temp = offenceMap.get(list[groupPosition]);
		int size = temp.size();
		Log.i("Method","getChildrenCount, empty : "+temp.isEmpty());
		for(int i=0;i<temp.size();i++)
		{
			Log.i("Method","getChildrenCount, Element : "+temp.get(0).offence);
		}
		Log.i("Method","getChildrenCount, haskey : "+offenceMap.containsKey(list[groupPosition]));
		return offenceMap.get(list[groupPosition]).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		Log.i("Method","getGroup, group Pos "+list[groupPosition]);
		return list[groupPosition];
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		Log.i("Method","getGroupCount :" +list.length);
		return list.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		Log.i("Method","getGroupId : " + groupPosition);
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		// TODO Auto-generated method stub
		if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.fines_list_group_layout,
                    null);
		}
		TextView item = (TextView) convertView.findViewById(R.id.lblListHeader);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(list[groupPosition]);
        Log.i("Method","getGroupView, text : "+list[groupPosition]);
        return convertView;
	}

	@Override
	public boolean hasStableIds() {
		Log.i("Method","hasStableIds");
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		Log.i("Method","isChildSelectable");
		return false;
	}

}
