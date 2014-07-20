package com.codepath.groupproject;

import java.util.ArrayList;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.codepath.groupproject.fragments.AddUserListFragment;
import com.codepath.groupproject.models.User;
import com.parse.ParseUser;

public class AddUsersActivity extends FragmentActivity implements AddUserListFragment.OnItemSelectedListener {
	private SearchView searchView;
	AddUserListFragment utF;
	private ArrayList<String> toAddUsers;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_users);
		toAddUsers = getIntent().getStringArrayListExtra("currentGroupMembers");
		
		if (toAddUsers == null)
		{
			toAddUsers = new ArrayList<String>();
		}
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        utF = AddUserListFragment.newInstance(toAddUsers);
        ft.replace(R.id.flAddUserList, utF);
        ft.commit();
        
	}
	@Override
	public void onUserClick(User user)
	{
	
		if (user != null)
		{
			Toast.makeText(this,"Added User: " + user.getFirstName(),Toast.LENGTH_SHORT).show();
			toAddUsers.add(user.getObjectId());
		}
		else
			Log.d("MyApp", "Added User is null");
		//Add User to List of Added Users
	}
	public void onDoneClick(View v) {
	  // Prepare data intent 
	  Intent data = new Intent();
	  // Pass relevant data back as a result
	  data.putExtra("groupMembers", toAddUsers);
	  // Activity finished ok, return the data
	  setResult(RESULT_OK, data); // set result code and bundle data for response
	  finish(); // closes the activity, pass data to parent

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.add_user_activity_actions, menu);
	    MenuItem searchItem = menu.findItem(R.id.action_search);
	    searchView = (SearchView) searchItem.getActionView();
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
	       @Override
	       public boolean onQueryTextSubmit(String query) {
	            Toast.makeText(getApplicationContext(), "Searching for" + query, Toast.LENGTH_SHORT).show();
		    	   if (query.equals(""))
		    	   {
		    		   utF.populateListwithAddedUsers();
		    	   }
		    	   else
		    	   {
			    	   Log.d("MyApp", "Query: " + query);
		    		   utF.populateUserByName(query);
		    	   }
	            return true;
	       }

	       @Override
	       public boolean onQueryTextChange(String newText) {

	    	   if (newText.equals(""))
	    	   {
	    		   utF.populateListwithAddedUsers();
	    	   }
	    	   else
	    	   {
		    	   Log.d("MyApp", "Query: " + newText);
	    		   utF.populateUserByName(newText);
	    	   }
	           return true;
	       }
	   });
		return super.onCreateOptionsMenu(menu);
	}
}
