package com.codepath.groupproject.fragments;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.ParserCursor;
import org.json.JSONArray;
import org.json.JSONException;

import com.codepath.groupproject.HomeActivity;
import com.codepath.groupproject.R;
import com.codepath.groupproject.adapters.GroupArrayAdapter;
import com.codepath.groupproject.models.Group;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class GroupsListFragment extends Fragment {
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
		
		ParseQuery<Group> queryGroups = ParseQuery.getQuery(Group.class);
		// Define our query conditions
		queryGroups.whereEqualTo("owner", ParseUser.getCurrentUser());
		// Execute the find asynchronously
		queryGroups.findInBackground(new FindCallback<Group>() {
			@Override
			public void done(List<Group> groupList, ParseException e) {
		        if (e == null) {
		        	if (groupList.size()!=0) {
		        		// Access the array of results here
		        		//String firstItemId = groupList.get(0).getString("name");
		        		//Toast.makeText(getActivity(), firstItemId, Toast.LENGTH_SHORT).show();
		        		
		        		aGroups.addAll(groupList);
		        		
		        		ParseUser.getCurrentUser().put("groups", groupList);
		        		ParseUser.getCurrentUser().saveInBackground();
		        		
		        	} else {
		        		Toast.makeText(getActivity(), "No group found.", Toast.LENGTH_SHORT).show();
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
	
		pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
		
		
		//Return the layout view
		return v;
	}
	
	
}
