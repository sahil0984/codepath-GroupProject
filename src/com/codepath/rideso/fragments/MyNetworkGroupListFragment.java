package com.codepath.rideso.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

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

import com.codepath.rideso.GroupDetailActivity;
import com.codepath.rideso.R;
import com.codepath.rideso.adapters.GroupArrayAdapter;
import com.codepath.rideso.models.Group;
import com.codepath.rideso.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MyNetworkGroupListFragment extends GroupListFragment {

	private ListView lvGroups;

	public static MyNetworkGroupListFragment newInstance(int page, String title) {
		MyNetworkGroupListFragment myNetworkGroupListFragment = new MyNetworkGroupListFragment();
		Bundle args = new Bundle();
		args.putInt("pageNum", page);
		args.putString("pageTitle", title);
		myNetworkGroupListFragment.setArguments(args);
		return myNetworkGroupListFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Non-view initialization
		
		page = getArguments().getInt("pageNum", 1);
		title = getArguments().getString("pageTitle");
				
	}
	
	@Override
	public void populateGroups() {
		//Need to show groups created by friends
		//Get users fbFriendsIds
		//Iterate on each friend to find their user Id
		//Then find each group that the friend is an owner of
		//Make sure the group is marked public
		

		ArrayList<ArrayList<String>> usersFbFriendsIds = (ArrayList<ArrayList<String>>) ParseUser.getCurrentUser().get("fbFriendsIds");
		//Log.d("fbFriends", usersFbFriendsIds.get(0).get(0));
		
		
		for(int i=0; i<usersFbFriendsIds.get(0).size(); i++) {
			ParseQuery<User> innerUserQuery = ParseQuery.getQuery(User.class);
			innerUserQuery.whereEqualTo("fbId", usersFbFriendsIds.get(0).get(i));
			Log.d("fbFriends", usersFbFriendsIds.get(0).get(i).toString());

			
			ParseQuery<Group> queryGroup = ParseQuery.getQuery(Group.class);
			queryGroup.include("members");
			queryGroup.whereMatchesQuery("owner", innerUserQuery);
			queryGroup.findInBackground(new FindCallback<Group>() {

				@Override
				public void done(List<Group> groupList, ParseException e) {
			        if (e == null) {
			        	if (groupList.size()!=0) {
			        		addAllGroups(groupList);
			        		pbLoading.setVisibility(ProgressBar.INVISIBLE);
			        	} else {
			        		//Toast.makeText(getActivity(), "No groups found.", Toast.LENGTH_SHORT).show();
			        	}
			        } else {
			        	Log.d("item", "Error: " + e.getMessage());
			        }
				}
				
			});
		}
		
	}
	
	
}
