package com.codepath.groupproject;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		Group newGroup = new Group("1234");
		// Set the current user, assuming a user is signed in
		newGroup.setOwner(ParseUser.getCurrentUser());
		// Immediately save the data asynchronously
		newGroup.saveInBackground();
		// or for a more robust offline save
		// newGroup.saveEventually();
		
		
		//User newUser = new User("testr");
		//newUser.saveInBackground();

		
		
		
		// Define the class we would like to query
		ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
		// Define our query conditions
		query.whereEqualTo("owner", ParseUser.getCurrentUser());
		// Execute the find asynchronously
		query.findInBackground(new FindCallback<Group>() {
			@Override
			public void done(List<Group> itemList, ParseException e) {
		        if (e == null) {
		            // Access the array of results here
		            String firstItemId = itemList.get(0).getName();
		            //Toast.makeText(HomeActivity.this, firstItemId, Toast.LENGTH_SHORT).show();
		        } else {
		            Log.d("item", "Error: " + e.getMessage());
		        }				
			}
		});
		
		
		//Toast.makeText(HomeActivity.this, ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
		
		// Define the class we would like to query
		ParseQuery<ParseUser> queryUser = ParseQuery.getQuery(ParseUser.class);
		// Define our query conditions
		queryUser.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
		// Execute the find asynchronously
		queryUser.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> itemList, ParseException e) {
		        if (e == null) {
		        	if (itemList.size()!=0) {
		        		// Access the array of results here
		        		String firstItemId = itemList.get(0).getString("phone");
		        		Toast.makeText(HomeActivity.this, firstItemId, Toast.LENGTH_SHORT).show();
		        	} else {
		        		Toast.makeText(HomeActivity.this, "No user found.", Toast.LENGTH_SHORT).show();
		        	}
		        } else {
		            Log.d("item", "Error: " + e.getMessage());
		        }				
			}
		});
	}


}
