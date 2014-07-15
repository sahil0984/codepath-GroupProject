package com.codepath.groupproject;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;


import com.codepath.groupproject.fragments.GroupMemberListFragment;
import com.codepath.groupproject.fragments.UserListFragment;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class GroupDetailActivity extends FragmentActivity {

	private LocationClient mLocationClient;
	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private Group currentGroup;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_detail);
		
		
		mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		if (mapFragment != null) {
			map = mapFragment.getMap();
			if (map != null) {
				Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
				map.setMyLocationEnabled(true);
			} else {
				Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
		}
        
        
        ParseQuery<Group> queryGroup = ParseQuery.getQuery(Group.class);
		String gobjectId = getIntent().getStringExtra("group");
		// Define our query conditions
		Log.d("MyApp", "gobjectId: " + gobjectId);
		
		// Execute the find asynchronously
		
		queryGroup.getInBackground(gobjectId,new GetCallback<Group>() {
		  @Override
		  public void done(Group group, ParseException e) {
		    if (e == null) {
	        		// Access the array of results here
		    		Log.d("MyApp","Loading: " + group.getName());
		    		currentGroup = group;	 
		    		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		            GroupMemberListFragment gmlF = new GroupMemberListFragment();
		            User user_0 = group.getMembers().get(0);
		            gmlF.appendUser(user_0);
		            ft.replace(R.id.flUserList, gmlF);
		            ft.commit();
		            

		   
		            
		    } else {
		        Log.d("MyApp", "oops");
		    }
		  }
		});


	}
}
