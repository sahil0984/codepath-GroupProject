package com.codepath.rideso;

import com.codepath.rideso.models.User;
import com.facebook.widget.ProfilePictureView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewProfileActivity extends Activity {
	
	private Menu mOptionsMenu;

	private ProfilePictureView ivProfileImage;

	User currUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_profile);
        //Setting the Title text typeface - Use same format for all activities
        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        Typeface robotoBoldCondensedItalic = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        if(actionBarTitleView != null){
            actionBarTitleView.setTypeface(robotoBoldCondensedItalic);
        }
		
		String objectId = getIntent().getStringExtra("objectId");
		
		ParseQuery<User> queryUser = ParseQuery.getQuery(User.class);
		// Define our query conditions
		Log.d("geUserFromObjectId", "objectId: " + objectId);
		// Execute the find asynchronously
		queryUser.getInBackground(objectId, new GetCallback<User>() {

			@Override
			public void done(User user, ParseException e) {
			    if (e == null) {
			    	currUser = user;
					setupViews();
					
					if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
						showOption(R.id.miEdit);
					}

			    } else {
			        Log.d("MyApp", "Can't find User.");
			    }
				}
			
		});
		
	}
	
	private void setupViews() {
		ivProfileImage  = (ProfilePictureView) findViewById(R.id.ivProfileImage);
		ivProfileImage.setProfileId((String) currUser.get("fbId"));
		
		String firstName = (String) currUser.get("firstName");
		String lastName = (String) currUser.get("lastName");
		getActionBar().setTitle(firstName + " " + lastName);
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_profile_activity_actions, menu);
        mOptionsMenu = menu;

        hideOption(R.id.miEdit);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miEdit:
    			onEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void onEdit() {
		Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
		startActivity(i);
    }
	
  private void hideOption(int id)
  {
      MenuItem item = mOptionsMenu.findItem(id);
      item.setVisible(false);
  }
	private void showOption(int id)
	{
	    MenuItem item = mOptionsMenu.findItem(id);
	    item.setVisible(true);
	}
}
