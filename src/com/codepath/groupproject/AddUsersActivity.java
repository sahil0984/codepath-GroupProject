package com.codepath.groupproject;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.codepath.groupproject.fragments.UserListFragment;

public class AddUsersActivity extends FragmentActivity {
	private SearchView searchView;
	UserListFragment utF;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_users);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        utF = UserListFragment.newInstance("");
        ft.replace(R.id.flAddUserList, utF);
        ft.commit();
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
	            Toast.makeText(getApplicationContext(), "Searching for", Toast.LENGTH_SHORT).show();
	            utF.populateUserByName(query);
	            return true;
	       }

	       @Override
	       public boolean onQueryTextChange(String newText) {
	           return false;
	       }
	   });
		return super.onCreateOptionsMenu(menu);
	}
}
