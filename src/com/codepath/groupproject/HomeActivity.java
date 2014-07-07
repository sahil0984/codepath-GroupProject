package com.codepath.groupproject;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.groupproject.fragments.GroupsListFragment;
import com.codepath.groupproject.listeners.SupportFragmentTabListener;
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
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class HomeActivity extends ActionBarActivity {

	private final int REQUEST_CODE = 20;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		setupTabs();
		
	}
	
	
	
	//Following helper methods need to be updated whenever we update the User model and User ParseObject
	//BOZO: Move it to utils??
	private User getUserFromParseUser(ParseUser pUser) {
		User mUser = new User();
		mUser.setFirstName(pUser.get("first_name").toString());
		mUser.setLastName(pUser.get("last_name").toString());
		//Add more stuff here
		return mUser;
	}
	private ParseUser getParseUserFromUser(User mUser) {
		ParseUser pUser = new ParseUser();
		pUser.put("first_name", mUser.getFirstName());
		pUser.put("last_name", mUser.getLastName());
		//Add more stuff here
		return pUser;
	}
	


	private void setupTabs() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(true);

		Tab tab1 = actionBar
		    .newTab()
		    .setText("My Groups")
		    //.setIcon(R.drawable.ic_profile)
		    .setTag("GroupsListFragment")
			.setTabListener(new SupportFragmentTabListener<GroupsListFragment>(R.id.flContainer, this,
                        "GroupsListFragment", GroupsListFragment.class));

		actionBar.addTab(tab1);
		actionBar.selectTab(tab1);

		Tab tab2 = actionBar
		    .newTab()
		    .setText("Timeline")
		    //.setIcon(R.drawable.ic_profile)
		    .setTag("TimelineFragment")
			.setTabListener(new SupportFragmentTabListener<GroupsListFragment>(R.id.flContainer, this,
                        "TimelineFragment", GroupsListFragment.class));
		actionBar.addTab(tab2);
		
	}
	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_activity_actions, menu); //BOZO: This is a placeholder. change it for this activity.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miProfile:
            	gotoProfileActivity();
                return true;
            case R.id.miCreateGroup:
                gotoCreateGroup();
                return true;                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    //BOZO: Create a util for this since it is used at multiple places
	public void gotoProfileActivity() {
		Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
		startActivity(i);
	}
	
	public void gotoCreateGroup() {
		Intent i = new Intent(getApplicationContext(), CreateGroupActivity.class);
		startActivityForResult(i, REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  // REQUEST_CODE is defined above
	  if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
		  
	     // Extract name value from result extras
	     final Group newGroup = new Group(data.getStringExtra("groupName"));
	     newGroup.setOwner(ParseUser.getCurrentUser());
	     newGroup.setOnwardTime(data.getStringExtra("onwardTime"));
	     newGroup.setReturnTime(data.getStringExtra("returnTime"));
	     
			Toast.makeText(getApplicationContext(), newGroup.getName(), Toast.LENGTH_SHORT).show();

	     newGroup.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e == null) {
			        GroupsListFragment groupsListFragment = (GroupsListFragment) getSupportFragmentManager().findFragmentByTag("GroupsListFragment");
			        groupsListFragment.appendNewGroup(newGroup);
			        //Toast.makeText(getApplicationContext(), newGroup.getObjectId(), Toast.LENGTH_SHORT).show();
				} else {
					e.printStackTrace();
				}
			}
		});
	     
	  }
	}
	
	
}
		



//Everything below this is old and was used for testing
//-----------------------------------------------------
//		Group newGroup = new Group("1234");
//		// Set the current user, assuming a user is signed in
//		newGroup.setOwner(ParseUser.getCurrentUser());
//		// Immediately save the data asynchronously
//		newGroup.saveInBackground();
//		// or for a more robust offline save
//		// newGroup.saveEventually();
//		
//		
//		//User newUser = new User("testr");
//		//newUser.saveInBackground();
//
//		
//		
//		
//		// Define the class we would like to query
//		ParseQuery<Group> query = ParseQuery.getQuery(Group.class);
//		// Define our query conditions
//		query.whereEqualTo("owner", ParseUser.getCurrentUser());
//		// Execute the find asynchronously
//		query.findInBackground(new FindCallback<Group>() {
//			@Override
//			public void done(List<Group> itemList, ParseException e) {
//		        if (e == null) {
//		            // Access the array of results here
//		            String firstItemId = itemList.get(0).getName();
//		            //Toast.makeText(HomeActivity.this, firstItemId, Toast.LENGTH_SHORT).show();
//		        } else {
//		            Log.d("item", "Error: " + e.getMessage());
//		        }				
//			}
//		});
//		
//		
//		//Toast.makeText(HomeActivity.this, ParseUser.getCurrentUser().getUsername(), Toast.LENGTH_SHORT).show();
//		
//		// Define the class we would like to query
//		ParseQuery<ParseUser> queryUser = ParseQuery.getQuery(ParseUser.class);
//		// Define our query conditions
//		queryUser.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
//		// Execute the find asynchronously
//		queryUser.findInBackground(new FindCallback<ParseUser>() {
//			@Override
//			public void done(List<ParseUser> itemList, ParseException e) {
//		        if (e == null) {
//		        	if (itemList.size()!=0) {
//		        		// Access the array of results here
//		        		String firstItemId = itemList.get(0).getString("phone");
//		        		Toast.makeText(HomeActivity.this, firstItemId, Toast.LENGTH_SHORT).show();
//		        	} else {
//		        		Toast.makeText(HomeActivity.this, "No user found.", Toast.LENGTH_SHORT).show();
//		        	}
//		        } else {
//		            Log.d("item", "Error: " + e.getMessage());
//		        }				
//			}
//		});
