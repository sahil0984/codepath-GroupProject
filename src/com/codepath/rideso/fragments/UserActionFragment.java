package com.codepath.rideso.fragments;

import java.util.ArrayList;
import java.util.List;

import com.codepath.rideso.R;
import com.codepath.rideso.Utils;
import com.codepath.rideso.adapters.UserActionArrayAdapter;
import com.codepath.rideso.models.Group;
import com.codepath.rideso.models.User;
import com.codepath.rideso.models.UserAction;
import com.parse.FindCallback;
import com.parse.GetCallback;
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

public class UserActionFragment extends Fragment {
	private ArrayList<UserAction> userActions;
	private ArrayAdapter<UserAction> aUserActions;
	private ListView lvUserActions;
	
	private ProgressBar pbLoading;
	
	public static UserActionFragment newInstance(String userObjectId) {
		UserActionFragment userActionFragment = new UserActionFragment();
		Bundle args = new Bundle();
		args.putString("userObjectId", userObjectId);
		userActionFragment.setArguments(args);
		return userActionFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Non-view initialization
		userActions = new ArrayList<UserAction>();
		aUserActions = new UserActionArrayAdapter(getActivity(), userActions);


		populateUserActions();

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inflate the layout
		View v = inflater.inflate(R.layout.fragment_user_action_list, container, false);
		//Assign our view references
		lvUserActions = (ListView) v.findViewById(R.id.lvUserActions);
		lvUserActions.setAdapter(aUserActions);
		
		pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
		pbLoading.setVisibility(ProgressBar.VISIBLE);

		//Return the layout view
		return v;
	}

	private void populateUserActions() {
		String currUserObjectId = getArguments().getString("userObjectId");

		
		ParseQuery<UserAction> queryUserActions = ParseQuery.getQuery(UserAction.class);
		queryUserActions.whereEqualTo("userObjectId", currUserObjectId);
		queryUserActions.orderByDescending("startTime");
		queryUserActions.findInBackground(new FindCallback<UserAction>() {
			
			@Override
			public void done(List<UserAction> userActionList, ParseException e) {
		        if (e == null) {
		        	if (userActionList.size()!=0) {
		        		
		        		List<UserAction> sortedList = new ArrayList<UserAction>();
		        		
		        		String currDate = "";
		        		for (int i=0; i<userActionList.size(); i++) {
		        			String tmpDate[];
		        			tmpDate = Utils.stringToDateTime(userActionList.get(i).getStartTime());
		        			
		        			if (!currDate.equals(tmpDate[0])) {
		        				UserAction tmpHeader = new UserAction();
		        				tmpHeader.setObjectId("header");
		        				tmpHeader.setStartTime(userActionList.get(i).getStartTime());
		        				sortedList.add(tmpHeader);
		        			}
		        			
	        				sortedList.add(userActionList.get(i));  			
		        			currDate = tmpDate[0];

		        		}
		        		
		        		
		        		aUserActions.addAll(sortedList);
		        		pbLoading.setVisibility(ProgressBar.INVISIBLE);

		        	} else {
		        		Toast.makeText(getActivity(), "No user activity found.", Toast.LENGTH_SHORT).show();
		        		pbLoading.setVisibility(ProgressBar.INVISIBLE);
		        	}
		        }  else {
		        	Log.d("item", "Error: " + e.getMessage());
		        }
			}
		});
		
	}
	
	
}
