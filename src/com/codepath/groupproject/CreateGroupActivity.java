package com.codepath.groupproject;

import com.codepath.groupproject.models.Group;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreateGroupActivity extends Activity {
	
	private EditText etGroupName;
	private EditText etOnwardTime;
	private EditText etReturnTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		
		setupViews();
	}

	private void setupViews() {
		etGroupName = (EditText) findViewById(R.id.etGroupName);
		etOnwardTime = (EditText) findViewById(R.id.etOnwardTime);
		etReturnTime = (EditText) findViewById(R.id.etReturnTime);
	}
	
	public void onAddGroup(View v) {
		
		
		Intent data = new Intent();
		data.putExtra("groupName", etGroupName.getText().toString());
		data.putExtra("onwardTime", etOnwardTime.getText().toString());
		data.putExtra("returnTime", etReturnTime.getText().toString());
		setResult(RESULT_OK, data);
		finish();
	}

	
}
