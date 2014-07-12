package com.codepath.groupproject.fragments;

import java.util.ArrayList;
import java.util.List;

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

import com.codepath.groupproject.R;
import com.codepath.groupproject.adapters.UserArrayAdapter;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class UserListFragment extends Fragment {
	private ArrayList<User> users;
	private ArrayAdapter<User> aUsers;
	private ListView lvUsers;

	private ProgressBar pbLoading;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Non-view initialization
		users = new ArrayList<User>();
		aUsers = new UserArrayAdapter(getActivity(), users);
		
		//populateUsers(getArguments().getString("group"));
	}
    public static UserListFragment newInstance(String id)
    {
	    UserListFragment fragmentUser = new UserListFragment();
	    Bundle args = new Bundle();
	    args.putString("id", id);
	    fragmentUser.setArguments(args);
	    return fragmentUser;
    }

	public void populateUsersByGroupId(String groupId) {
		ParseQuery<Group> queryGroups = ParseQuery.getQuery(Group.class);
		// Define our query conditions
		queryGroups.whereEqualTo("objectId", groupId);
		Log.d("MyApp","My groupId is" + groupId);
		// Execute the find asynchronously
		queryGroups.findInBackground(new FindCallback<Group>() {
			@Override
			public void done(List<Group> groupList, ParseException e) {
		        if (e == null) {
		        	if (groupList.size()!=0) {
		        		// Access the array of results here
		        		ArrayList<User> groupUsers = (ArrayList<User>) groupList.get(0).getMembers();
		        		//Toast.makeText(getActivity(), firstItemId, Toast.LENGTH_SHORT).show();
		        		if (groupUsers != null)
		        		{
		        			aUsers.addAll(groupUsers);
		        		}
		        		//ParseUser.getCurrentUser().put("groups", groupList);
		        		//ParseUser.getCurrentUser().saveInBackground();
		        		
		        	} else {
		        		Toast.makeText(getActivity(), "No group found.", Toast.LENGTH_SHORT).show();
		        	}
		        } else {
		            Log.d("item", "Error: " + e.getMessage());
		        }
			}
		});

	}
	
	public void appendUser(User newUser) {
		aUsers.add(newUser);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inflate the layout
		View v = inflater.inflate(R.layout.fragment_users_list, container, false);
		//Assign our view references
		lvUsers = (ListView) v.findViewById(R.id.lvUsers);
		lvUsers.setAdapter(aUsers);
	
		pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
		
		
		//Return the layout view
		return v;
	}
	public void populateUserByName(String query) {
		ParseQuery<User> queryUsers = ParseQuery.getQuery(User.class);
		// Define our query conditions
		queryUsers.whereEqualTo("firstName", query);
		
		// Execute the find asynchronously
		queryUsers.findInBackground(new FindCallback<User>() {
			@Override
			public void done(List<User> userList, ParseException e) {
		        if (e == null) {
		        	if (userList.size()!=0) {
		        		// Access the array of results here
		        	
		        		//Toast.makeText(getActivity(), firstItemId, Toast.LENGTH_SHORT).show();
		        			aUsers.clear();
		        			aUsers.addAll(userList);
		        		
		        		//ParseUser.getCurrentUser().put("groups", groupList);
		        		//ParseUser.getCurrentUser().saveInBackground();
		        		
		        	} else {
		        		Toast.makeText(getActivity(), "No users found.", Toast.LENGTH_SHORT).show();
		        	}
		        } else {
		            Log.d("item", "Error: " + e.getMessage());
		        }
			}
		});
		
	}
}
