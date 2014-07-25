package com.codepath.groupproject.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.groupproject.GroupDetailActivity;
import com.codepath.groupproject.R;
import com.codepath.groupproject.adapters.GroupArrayAdapter;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MyGroupListFragment extends GroupListFragment {

	private ListView lvGroups;
	
	public static MyGroupListFragment newInstance(int page, String title) {
		MyGroupListFragment myGroupListFragment = new MyGroupListFragment();
		Bundle args = new Bundle();
		args.putInt("pageNum", page);
		args.putString("pageTitle", title);
		myGroupListFragment.setArguments(args);
		return myGroupListFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Non-view initialization
		
		page = getArguments().getInt("pageNum", 0);
		title = getArguments().getString("pageTitle");
				
	}
	
	@Override
	public void populateGroups() {
		ParseQuery<User> innerUserQuery = ParseQuery.getQuery(User.class);
		innerUserQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
		
		
		ParseQuery<Group> queryGroup = ParseQuery.getQuery(Group.class);
		queryGroup.include("members");
		queryGroup.whereMatchesQuery("members", innerUserQuery);
		queryGroup.findInBackground(new FindCallback<Group>() {

			@Override
			public void done(List<Group> groupList, ParseException e) {
		        if (e == null) {
		        	if (groupList.size()!=0) {
		        		addAllGroups(groupList);
		        		pbLoading.setVisibility(ProgressBar.INVISIBLE);
		        	} else {
		        		Toast.makeText(getActivity(), "No groups found.", Toast.LENGTH_SHORT).show();
		        	}
		        } else {
		        	Log.d("item", "Error: " + e.getMessage());
		        }
			}
		});

	}
	
	
}
