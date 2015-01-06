package com.firsttry.mumbaiparking;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.firsttry.mumbaiparking.helpers.ExpandableListAdapter;
import com.firsttry.mumbaiparking.helpers.ParkingOffence;
import com.makeshift.wheretopark.R;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.Toast;

public class FinesActivity extends Activity {
	
	ArrayList<ParkingOffence> offenceList;
	HashMap<String, ArrayList<ParkingOffence>> parkingHashMap;
	ExpandableListView expListView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fines_layout);
		
		
		/*
		 * We first Parse the xml files into objects 
		 */
		new parseAllXmlFiles().execute();
		
	}
	 
	
	private class parseAllXmlFiles extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			 String[] list ={"Documents","Parking","Towing Of Vehicles","Pollution",
					 "Traffic police/Traffic signal", "Speed & Overtake", "Miscellaneous" };
			for(int i=0;i<list.length;i++)
			{
				String tag=list[i];
				Log.i("onpostexec","Inside i = "+i+" Tag = "+list[i]);
				offenceList = parkingHashMap.get(tag);
				
				for(int j=0;j<offenceList.size();j++){
					Log.i("onpostexec","offense "+j);
				}
			}
		//	Toast.makeText(getApplicationContext(), "Parking offences : "+offenceList.size(), Toast.LENGTH_SHORT).show();
			expListView = (ExpandableListView) findViewById(R.id.lvExp);
	        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(FinesActivity.this, parkingHashMap);
	        expListView.setAdapter(expListAdapter);
		}
		@Override
		protected Void doInBackground(Void... params) {
			
			 final String KEY_OFFENCE = "offence";
			 final String KEY_PENALTY = "penalty";
			 final String KEY_SECTION = "section";
			 
			 String[] list ={"Documents","Parking","Towing Of Vehicles","Pollution",
					 "Traffic police/Traffic signal", "Speed & Overtake", "Miscellaneous" };
			
			 String[] listOffence = {"Documents.xml", "Parking.xml", 
					 					"Towing.xml", "Pollution.xml", "Signals.xml",
					 					"Overtaking.xml", "Miscellaneous.xml"};
			
			 offenceList = new ArrayList<ParkingOffence>();
			parkingHashMap = new HashMap<String, ArrayList<ParkingOffence>>();
			 
			for(int j=0;j<listOffence.length;j++)
			{
				offenceList.clear();
				String fileName = listOffence[j];
				String hashMapTag = list[j];
				try{
					
					AssetManager assetManager = getBaseContext().getAssets();
					InputStream is=assetManager.open(fileName);
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc = dBuilder.parse(is);
					
					doc.getDocumentElement().normalize();			 
					NodeList nl = doc.getElementsByTagName("record");
					// looping through all item nodes <item>
					for (int i = 0; i < nl.getLength(); i++) 
					{
						Element e = (Element) nl.item(i);
						String offence = e.getElementsByTagName(KEY_OFFENCE).item(0).getTextContent();
						String section = e.getElementsByTagName(KEY_SECTION).item(0).getTextContent();
						String penalty = e.getElementsByTagName(KEY_PENALTY).item(0).getTextContent();
						ParkingOffence po = new ParkingOffence(offence, section, penalty, hashMapTag);
						offenceList.add(po);
					}
				}catch(Exception e)
				{
					e.printStackTrace();
				}
				/*
				 * After spending more than 1 hour on this, I finally figured it out.
				 * #Saavn : Meri Saamne waali khidki mein 
				 * In this scenario, we are reading each xml file,
				 * 1 offence is stored in 1 Parking offence object
				 * Multiple offence objects of same xml file or category eg.Parking are
				 * grouped into one Arraylist, these arraylists are then 
				 * put in a hashmap with the type as key. The arraylist is then 
				 * cleared to store the next type of offences from the next xml file.
				 * 
				 *  My earler declaration was like this ,
				 *  parkingHashMap.put(hashMapTag, offenceList);
				 *  
				 *  When I tried to run it, I was constantly getting empty Arraylists
				 *  Again and  again and again. I got very frustrated and impatient.
				 *  At a point, I was putting Log tags after every line to see where exactly the
				 *  problem was. 
				 *  Then I removed the offenceList.clear();
				 *  
				 *  That resulted in each tag in the hashmap to show all the offence of all types,
				 *  which was not what I wanted, so I put it back in there, now I was getting empty
				 *  arraylists. This all-or-nothing response rang a bell in my already worked out brain.
				 *  What was happening was that the Hashmap was just storing the reference to the
				 *  arraylist, whenever I cleared the old list to make a new one, the previous 
				 *  values I put in the hashmap also cleared out. 
				 *  
				 *  I googled and stackoverflowed, my above inference was correct indeed. 
				 *  Java was passing the reference to the offenceList and not the values stored in it.
				 * 	To work around this, I had to figure out a way to pass the value. 
				 * 
				 * 	#Saavn : Aaj mausam bada baiman hai ... 
				 * 
				 * 	With code assist, I tried a method called clone(), and it worked.
				 * 	#feelingRelieved #Workedup #3:23AM 
				 * 	
				 * 
				 */
				parkingHashMap.put(hashMapTag, (ArrayList<ParkingOffence>) offenceList.clone());
				
			}
			
		
		
		return null;
		}
	}
}	
