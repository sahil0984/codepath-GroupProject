package com.codepath.groupproject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.widget.ProfilePictureView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ProfileActivity extends Activity {
	
	private ProfilePictureView ivProfileImage;
	private EditText etFirstName;
	private EditText etLastName;
	private EditText etHomeAdd;
	private EditText etWorkAdd;
	private EditText etPhone;
	private EditText etPersonalEmail;
	private EditText etEmail;
	
	private Button btnVerifyEmail;
	private Button btnGoHome;
	
	private Menu mOptionsMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		setupViews();
	}

	private void setupViews() {
		ivProfileImage  = (ProfilePictureView) findViewById(R.id.ivProfileImage);
		etFirstName     = (EditText) findViewById(R.id.etFirstName);
		etLastName 	    = (EditText) findViewById(R.id.etLastName);
		etHomeAdd       = (EditText) findViewById(R.id.etHomeAdd);
		etWorkAdd       = (EditText) findViewById(R.id.etWorkAdd);
		etPhone         = (EditText) findViewById(R.id.etPhone);
		etPersonalEmail = (EditText) findViewById(R.id.etPersonalEmail);
		etEmail         = (EditText) findViewById(R.id.etEmail);
		
		btnVerifyEmail = (Button) findViewById(R.id.btnVerifyEmail);
		btnGoHome      = (Button) findViewById(R.id.btnDone);

		btnVerifyEmail.setEnabled(false);
		
		ivProfileImage.setProfileId((String) ParseUser.getCurrentUser().get("fbId"));

		etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		
		stopEditProfile();
		
	}
	
	public void stopEditProfile() {
		
		String firstName = (String) ParseUser.getCurrentUser().get("firstName");
		String lastName = (String) ParseUser.getCurrentUser().get("lastName");
		String homeAdd = (String) ParseUser.getCurrentUser().get("homeAdd");
		String workAdd = (String) ParseUser.getCurrentUser().get("workAdd");
		String phone = (String) ParseUser.getCurrentUser().get("phone");
		String personalEmail = (String) ParseUser.getCurrentUser().get("personalEmail");
		String email = (String) ParseUser.getCurrentUser().get("email");
		
		//First Name
		etFirstName.setText(firstName);
		etFirstName.setFocusable(false);
		//Last Name
		etLastName.setText(lastName);
		etLastName.setFocusable(false);
		//Home Address
		etHomeAdd.setText(homeAdd);
		etHomeAdd.setFocusable(false);
		//Work Address
		etWorkAdd.setText(workAdd);
		etWorkAdd.setFocusable(false);
		//Phone number
		etPhone.setText(phone);
		etPhone.setFocusable(false);
		//Personal Email
		etPersonalEmail.setText(personalEmail);
		etPersonalEmail.setFocusable(false);
		//Email
		etEmail.setText(email);
		etEmail.setFocusable(false);
	}
	
	private void editProfile() {

		//First Name
		etFirstName.setFocusableInTouchMode(true);
		etFirstName.setFocusable(true);
		//Last Name
		etLastName.setFocusableInTouchMode(true);
		etLastName.setFocusable(true);
		//Home Address
		etHomeAdd.setFocusableInTouchMode(true);
		etHomeAdd.setFocusable(true);
		//Work Address
		etWorkAdd.setFocusableInTouchMode(true);
		etWorkAdd.setFocusable(true);
		//Phone number
		etPhone.setFocusableInTouchMode(true);
		etPhone.setFocusable(true);
		//Personal Email
		etPersonalEmail.setFocusableInTouchMode(true);
		etPersonalEmail.setFocusable(true);
		//Email
		etEmail.setFocusableInTouchMode(true);
		etEmail.setFocusable(true);			

	}
	
	public void onVerifyEmail (View v) {
		String maybeEmail = etEmail.getText().toString();
		if (!android.util.Patterns.EMAIL_ADDRESS.matcher(maybeEmail).matches()) {
			Toast.makeText(getApplicationContext(), "Not a valid email address. Try again.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		ParseUser.getCurrentUser().put("email", maybeEmail);
		//ParseUser.getCurrentUser().saveInBackground();
		Toast.makeText(getApplicationContext(), "A verification link has been sent to your email.", Toast.LENGTH_SHORT).show();

		
		//Better way to save in background. BOZO: Create a method and do messaging with user
		ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
//		 	   ... here, get rid of that message
//
//		  		if (e == null) {
//		      		Utils.Log("resendEmail no problem.");
//
//		      		... here, bring up a message like...
//		      		String un = ParseUser.getCurrentUser().getUsername();
//		      		"We have resent the validation email to " +un +". Please check your email!"
//		      	} else {
//
//		      		int errCodeSimple = e.getCode();
//		      		Utils.Log(", some problem: " + errCodeSimple);
//
//		      		... here, bring up a message like...
//		      		"We could not reach the internet! Try again later!"
//		      	}
			}
		});
	}
	
	public void onDone (View v) {
		
		Intent i = new Intent(getApplicationContext(), HomeActivity.class);
		startActivity(i);
	}
	
	public void saveUpdatedInfo() {
		ParseUser.getCurrentUser().put("firstName", etFirstName.getText().toString());
		ParseUser.getCurrentUser().put("lastName", etLastName.getText().toString());
		ParseUser.getCurrentUser().put("homeAdd", etHomeAdd.getText().toString());
		ParseUser.getCurrentUser().put("workAdd", etWorkAdd.getText().toString());
		ParseUser.getCurrentUser().put("phone", etPhone.getText().toString());
		ParseUser.getCurrentUser().put("personalEmail", etPersonalEmail.getText().toString());
		ParseUser.getCurrentUser().put("email", etEmail.getText().toString());
		ParseUser.getCurrentUser().saveInBackground();
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_activity_actions, menu);
        mOptionsMenu = menu;

        showOption(R.id.miEditProfile);
        hideOption(R.id.miDoneEditProfile);
        return true;
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
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miEditProfile:
                hideOption(R.id.miEditProfile);
                showOption(R.id.miDoneEditProfile);
                btnVerifyEmail.setEnabled(true);
                btnGoHome.setEnabled(false);
                editProfile();
                return true;
            case R.id.miDoneEditProfile:
                showOption(R.id.miEditProfile);
                hideOption(R.id.miDoneEditProfile);
                btnVerifyEmail.setEnabled(false);
                btnGoHome.setEnabled(true);
                saveUpdatedInfo();
                stopEditProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


	
}
