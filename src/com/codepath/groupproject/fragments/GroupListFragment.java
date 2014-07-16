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

public class GroupListFragment extends Fragment {
	private ArrayList<Group> groups;
	private ArrayAdapter<Group> aGroups;
	private ListView lvGroups;

	private ProgressBar pbLoading;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Non-view initialization
		groups = new ArrayList<Group>();
		aGroups = new GroupArrayAdapter(getActivity(), groups);
				
		populateGroups();
	}
	
	
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
		        		aGroups.addAll(groupList);
		        	} else {
		        		Toast.makeText(getActivity(), "No current user found.", Toast.LENGTH_SHORT).show();
		        	}
		        } else {
		        	Log.d("item", "Error: " + e.getMessage());
		        }
			}
		});

	}
	
	
	public void appendNewGroup(Group newGroup) {
		aGroups.add(newGroup);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inflate the layout
		View v = inflater.inflate(R.layout.fragment_groups_list, container, false);
		//Assign our view references
		lvGroups = (ListView) v.findViewById(R.id.lvGroups);
		lvGroups.setAdapter(aGroups);
		lvGroups.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int pos, long id) {
                Intent i = new Intent(getActivity(),GroupDetailActivity.class);
                i.putExtra("group",groups.get(pos).getObjectId());
                Log.d("MyApp", "My objectId before sending in is:" + groups.get(pos).getObjectId());
                //Use the Request Code to send the index of the list (pos)
                startActivityForResult(i,pos);				
			}
			
		});
		pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
		
		
		//Return the layout view
		return v;
	}
	
	
}
