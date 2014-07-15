package com.codepath.groupproject;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.codepath.groupproject.fragments.GroupMemberListFragment;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
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
		String groupObjectId = getIntent().getStringExtra("group");
		
        ParseQuery<Group> queryGroup = ParseQuery.getQuery(Group.class);
        queryGroup.include("members");
		// Define our query conditions
		Log.d("MyApp", "gobjectId: " + groupObjectId);
		
		// Execute the find asynchronously
		queryGroup.getInBackground(groupObjectId,new GetCallback<Group>() {
		  @Override
		  public void done(Group group, ParseException e) {
		    if (e == null) {
	        		// Access the array of results here
		    		Log.d("MyApp","Loading: " + group.getName());	 
		    		currentGroup = group;
		    		addMarkers();
		            
		    } else {
		        Log.d("MyApp", "oops");
		    }
		  }
		});

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        GroupMemberListFragment gmlF = GroupMemberListFragment.newInstance(groupObjectId);
        ft.replace(R.id.flUserList, gmlF);
        ft.commit();
	
	}
	public void addMarkers() {
		// TODO Auto-generated method stub
		int i;
		ArrayList<User> groupMembers = (ArrayList<User>) currentGroup.getMembers();
		ArrayList<Marker> markers = new ArrayList<Marker>();
		
		for (i = 0; i < groupMembers.size(); i++)
		{
			User user = groupMembers.get(i);
			ParseGeoPoint geoPoint = user.getParseGeoPoint("homeAdd");

			Marker m = map.addMarker(new MarkerOptions()
							.position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
							.title(user.getFirstName()));	
			markers.add(m);
			
		}
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Marker marker : markers) {
		    builder.include(marker.getPosition());
		}
		LatLngBounds bounds = builder.build();
		
		int padding = 0; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
		map.animateCamera(cu);
		
	}	
}
