package com.codepath.groupproject.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.codepath.groupproject.models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class AddUserListFragment extends UserListFragment {
	
	private OnItemSelectedListener listener;
	private ArrayList<User> addedUsers;

	public interface OnItemSelectedListener {
		public void onUserClick(User user);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stu
		super.onCreate(savedInstanceState);
		addedUsers = new ArrayList<User>();
		populateUsersByObjectId(getArguments().getStringArrayList("currentMembers"));
		
		
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity); 

		if (activity instanceof OnItemSelectedListener) {
			listener = (OnItemSelectedListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
            + " must implement MyListFragment.OnItemSelectedListener");
		}

	}

    public static AddUserListFragment newInstance(ArrayList<String> currentMembers)
    {
	    AddUserListFragment fragmentUser = new AddUserListFragment();
	    Bundle args = new Bundle();
	    args.putStringArrayList("currentMembers", currentMembers);	
	    fragmentUser.setArguments(args);
	    return fragmentUser;
    }
	@Override
	public void onUserClick(User user) {
		if (!addedUsers.contains(user))
		{
			addedUsers.add(user);
			clearList();
			addAll(addedUsers);
			listener.onUserClick(user);
		}		
	}
	public void populateListwithAddedUsers()
	{
		clearList();
		if (addedUsers != null)
			addAll(addedUsers);
	}
	public void populateUsersByObjectId(ArrayList<String> users)
	{
		//ArrayList<User> userList = new ArrayList<User>();
		final int userListSize = users.size();
		int i;
		
		for (i = 0; i < userListSize; i++)
		{
			ParseQuery<User> queryUsers = ParseQuery.getQuery(User.class);
			String objectId = users.get(i);
			// Define our query conditions
			Log.d("geUserFromObjectId", "objectId: " + objectId);
			
			queryUsers.include("groups");
			// Execute the find asynchronously
			
			queryUsers.getInBackground(objectId, new GetCallback<User>() {
			  public void done(User user, ParseException e) {
			    if (e == null) {
		        		// Access the array of results here
			    		//Adding member to the group adapter
			    		addedUsers.add(user);
			    		appendUser(user);
		        		Log.d("MyApp", user.toString());
	        		
			    } else {
			        Log.d("MyApp", "oops");
			    }
			  }
			});
		}
	}

}
