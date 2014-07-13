package com.codepath.groupproject.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.codepath.groupproject.models.User;

public class AddUserListFragment extends UserListFragment {
	
	private OnItemSelectedListener listener;
	

	public interface OnItemSelectedListener {
		public void onUserClick(User user);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stu
		super.onCreate(savedInstanceState);
		
		
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

    public static AddUserListFragment newInstance(String id)
    {
	    AddUserListFragment fragmentUser = new AddUserListFragment();
	    Bundle args = new Bundle();
	    args.putString("id", id);	
	    fragmentUser.setArguments(args);
	    return fragmentUser;
    }
	@Override
	public void onUserClick(User user) {
		listener.onUserClick(user);

		
		
	}

}
