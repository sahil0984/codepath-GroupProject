package com.codepath.groupproject.fragments;

import java.util.ArrayList;
import java.util.List;

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

import com.codepath.groupproject.R;
import com.codepath.groupproject.adapters.UserArrayAdapter;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public abstract class UserListFragment extends Fragment {
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
		lvUsers.setAdapter(aUsers);

		//populateUsers(getArguments().getString("group"));
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
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inflate the layout
		View v = inflater.inflate(R.layout.fragment_users_list, container, false);
		//Assign our view references
		lvUsers = (ListView) v.findViewById(R.id.lvUsers);
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
		queryUsers.whereEqualTo("firstName", query);
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
