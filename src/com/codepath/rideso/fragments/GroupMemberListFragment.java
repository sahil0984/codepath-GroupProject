package com.codepath.rideso.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.rideso.R;
import com.codepath.rideso.models.Group;
import com.codepath.rideso.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class GroupMemberListFragment extends UserListFragment {

	@Override
	public void onUserClick(User user) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initializeUsers(R.layout.user_item);
		String groupObjectId = getArguments().getString("group");
		populateGroupMembers(groupObjectId);
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public void populateGroupMembers(String groupObjectId)
	{
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

		            ArrayList<User> userList = (ArrayList<User>) group.getMembers();
		            addAll(userList);
		            
		    } else {
		        Log.d("MyApp", "oops");
		    }
		  }
		});
	}


	public static GroupMemberListFragment newInstance(String groupObjectId) {
		// TODO Auto-generated method stub
		GroupMemberListFragment fragmentGML = new GroupMemberListFragment();
		Bundle args = new Bundle();
		args.putString("group", groupObjectId);
		fragmentGML.setArguments(args);
		return fragmentGML;
	}}
