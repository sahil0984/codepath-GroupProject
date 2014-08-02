package com.codepath.rideso;

import java.util.ArrayList;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.codepath.rideso.fragments.AddUserListFragment;
import com.codepath.rideso.fragments.QueryUserListFragment;
import com.codepath.rideso.models.User;
import com.google.android.gms.identity.intents.AddressConstants;
import com.parse.ParseUser;
import com.parse.ParseQueryAdapter.QueryFactory;

public class AddUsersActivity extends FragmentActivity implements QueryUserListFragment.OnItemSelectedListener, AddUserListFragment.OnItemSelectedListener{
	private SearchView searchView;
	QueryUserListFragment utF;
	AddUserListFragment atF;
	
	private ArrayList<String> toAddUsers;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_users);
        //Setting the Title text typeface - Use same format for all activities
        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        Typeface robotoBoldCondensedItalic = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        if(actionBarTitleView != null){
            actionBarTitleView.setTypeface(robotoBoldCondensedItalic);
        }
				
		toAddUsers = getIntent().getStringArrayListExtra("currentGroupMembers");
		
		if (toAddUsers == null)
		{
			toAddUsers = new ArrayList<String>();
		}
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        utF = QueryUserListFragment.newInstance(toAddUsers);
        ft.replace(R.id.flAddUserList, utF);
        ft.commit();
        
        FragmentTransaction ft_a = getSupportFragmentManager().beginTransaction();
        atF = AddUserListFragment.newInstance(toAddUsers);
        ft_a.replace(R.id.flAddedUsersList, atF);
        ft_a.commit();
        
	}
	@Override
	public void onUserClick(User user)
	{
		if (atF.addUser(user))
		{
			toAddUsers.add(user.getObjectId());
			utF.clearList();
			InputMethodManager imm = (InputMethodManager)getSystemService(
					Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
		}
		
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
	            //Toast.makeText(getApplicationContext(), "Searching for" + query, Toast.LENGTH_SHORT).show()
	            Log.d("MyApp", "Query: " + query);
	            if (!query.equals(""))
	            	utF.populateUserByName(query);
	            else
	            	utF.clearList();
	            return true;
	       }

	       @Override
	       public boolean onQueryTextChange(String newText) {
	    	   Log.d("MyApp", "Query: " + newText);
	    	   if (!newText.equals(""))
	    		   utF.populateUserByName(newText);
	    	   else
	    		   utF.clearList();
    		   return true;
	       }
	   });
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public void onAddedUserClick(User user) {
		toAddUsers.remove(user.getObjectId());	
	}
}
