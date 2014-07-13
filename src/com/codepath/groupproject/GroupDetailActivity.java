package com.codepath.groupproject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;


import com.codepath.groupproject.fragments.UserListFragment;

public class GroupDetailActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_detail);
		
        /*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        UserListFragment utF = UserListFragment.newInstance(getIntent().getStringExtra("group"));
        ft.replace(R.id.flUserList, utF);
        ft.commit();
*/

	}
}
