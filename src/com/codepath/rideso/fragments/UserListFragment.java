package com.codepath.rideso.fragments;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.rideso.R;
import com.codepath.rideso.adapters.UserArrayAdapter;
import com.codepath.rideso.models.Group;
import com.codepath.rideso.models.User;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter.Section;

public abstract class UserListFragment extends Fragment {
	private ArrayList<User> users;
	private UserArrayAdapter aUsers;
	private ListView lvUsers;
	private ProgressBar pbLoading;
	AnimationAdapter alphaInAnimationAdapter;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Non-view initialization

		


		//populateUsers(getArguments().getString("group"));
	}
	
	public void initializeUsers(int layoutItem)
	{
		users = new ArrayList<User>();
		aUsers = new UserArrayAdapter(getActivity(), users, layoutItem);
		alphaInAnimationAdapter = new AlphaInAnimationAdapter(aUsers);

	}

	public void appendUser(User newUser) {
		aUsers.add(newUser);
	}

	public void addAll(ArrayList<User> users)
	{
		aUsers.addAll(users);
	}
	public void clearList()
	{
		aUsers.clear();
	}
	
	public void removeUser(User user)
	{
		aUsers.remove(user);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inflate the layout
		View v = inflater.inflate(R.layout.fragment_users_list, container, false);
		//Assign our view references
		lvUsers = (ListView) v.findViewById(R.id.lvUsers);
		alphaInAnimationAdapter.setAbsListView(lvUsers);
		
		lvUsers.setAdapter(alphaInAnimationAdapter);
		
		lvUsers.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("MyApp", aUsers.getItem(position).getFirstName());
				onUserClick(aUsers.getItem(position));
			}
		});
		pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
		
		
		//Return the layout view
		return v;
	}	
	public abstract void onUserClick(User user);
	public void populateUserByName(String query) {
		ParseQuery<User> queryUsers = ParseQuery.getQuery(User.class);
		// Define our query conditions
		queryUsers.whereStartsWith("lowerFirstName", query.toLowerCase(Locale.ENGLISH));
		Log.d("MyApp", "Query: " + query);
		// Execute the find asynchronously
		queryUsers.findInBackground(new FindCallback<User>() {
		  public void done(List<User> users, ParseException e) {
		    if (e == null) {
	        	if (users.size()!=0) {
	        		// Access the array of results here
	
	        		//Toast.makeText(getActivity(), firstItemId, Toast.LENGTH_SHORT).show();
	        		aUsers.clear();
	        		aUsers.addAll(users);

	        		//ParseUser.getCurrentUser().put("groups", groupList);
	        		//ParseUser.getCurrentUser().saveInBackground();
	        		
	        	} else {
	        		aUsers.clear();
	        		Toast.makeText(getActivity(), "No group found.", Toast.LENGTH_SHORT).show();
	        	}		      
		    } else {
		        Log.d("MyApp", "oops");
		    }
		  }
		});
		
	}
}


