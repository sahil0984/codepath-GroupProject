package com.codepath.rideso;

import org.apache.http.Header;
import org.json.JSONObject;

import com.codepath.rideso.fragments.UserActionFragment;
import com.codepath.rideso.models.User;
import com.facebook.widget.ProfilePictureView;
import com.loopj.android.http.AsyncHttpClient;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewProfileActivity extends ActionBarActivity {
	
	private Menu mOptionsMenu;

	private ImageView ivCoverPhoto;
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
					
			        String URL = "https://graph.facebook.com/" + currUser.getFbId() + "?fields=cover&access_token=" + ParseFacebookUtils.getSession().getAccessToken();
			        //Toast.makeText(getApplicationContext(), URL, Toast.LENGTH_SHORT).show();
					//new FbCoverPhotoTask().execute(URL);
			        
				    AsyncHttpClient client = new AsyncHttpClient();
				    client.get(URL, null, new FbCoverPhotoResponseHandler(getApplicationContext()) {

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							super.onSuccess(statusCode, headers, response);
							String coverPhotoUrl = getConverPhotoUrl();
							
							if (!coverPhotoUrl.equals("")) {
								Picasso.with(getApplicationContext()).load(coverPhotoUrl).into(ivCoverPhoto);
							}
							
						}
				    	
				    });

			    } else {
			        Log.d("MyApp", "Can't find User.");
			    }
			}
			
		});
		
	}
	
	private void setupViews() {
		ivCoverPhoto = (ImageView) findViewById(R.id.ivCoverPhoto);
		
		ivProfileImage  = (ProfilePictureView) findViewById(R.id.ivProfileImage);
		ivProfileImage.setProfileId((String) currUser.get("fbId"));
		
		String firstName = (String) currUser.get("firstName");
		String lastName = (String) currUser.get("lastName");
		getActionBar().setTitle(firstName + " " + lastName);
		
		// Begin the transaction
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// Replace the container with the new fragment
		ft.replace(R.id.flUserActions, new UserActionFragment());
		// or ft.add(R.id.your_placeholder, new FooFragment());
		// Execute the changes specified
		ft.commit();
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
