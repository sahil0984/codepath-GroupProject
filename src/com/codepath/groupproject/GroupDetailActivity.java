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
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		String groupObjectId = getIntent().getStringExtra("group");
        GroupMemberListFragment gmlF = GroupMemberListFragment.newInstance(groupObjectId);
        ft.replace(R.id.flUserList, gmlF);
        ft.commit();



	}
}
