package com.codepath.groupproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		
    	//This accesses cached current user.
    	ParseUser currentUser = ParseUser.getCurrentUser();
    	if (currentUser != null && ParseFacebookUtils.isLinked(currentUser)) {
    		// do stuff with the user
    		//signInParseUser(); //Don't do this: User does not need to login if its already cached.
    		gotoProfileActivity();
    	} else {
    		// show the signup or login screen
    	}
    	
	}
	
	
	public void gotoProfileActivity() {
		Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
		startActivity(i);
	}
	
	public void gotoHomeActivity() {
		Intent i = new Intent(getApplicationContext(), HomeActivity.class);
		startActivity(i);
	}
	
	//Arrays.asList("email", Permissions.User.EMAIL),
	public void onLoginWithFacebook(View v) {
		ParseFacebookUtils.logIn(Arrays.asList("email"), this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				if (user == null) {
					Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
					err.printStackTrace();
				} else if (user.isNew()) {
					Log.d("MyApp", "User signed up and logged in through Facebook!");
					getFacebookDetailsInBackground();
					gotoProfileActivity(); //Not the right place. Add it onCompleted of newMeRequest
				} else {
					Log.d("MyApp", "User logged in through Facebook!");
					getFacebookDetailsInBackground();
					gotoProfileActivity(); //Not the right place. Add it onCompleted of newMeRequest
				}
			}
		});
	}
	
	
	private static void getFacebookDetailsInBackground() {
		
		  Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
			  
			  // callback after Graph API response with user object
			  @Override
			  public void onCompleted(GraphUser user, Response response) {
			      if (user != null) {
			          ParseUser.getCurrentUser().put("fbId", user.getId());
			          ParseUser.getCurrentUser().put("firstName", user.getFirstName());
			          ParseUser.getCurrentUser().put("lastName", user.getLastName());
			          //ParseUser.getCurrentUser().put("username", user.getUsername());  In v2.0, there is no username.
			          ParseUser.getCurrentUser().put("personalEmail", user.asMap().get("email"));
			          String fbProfileImageUrl = "http://graph.facebook.com/"+user.getId()+"/picture?type=large";
			          ParseUser.getCurrentUser().put("profileImageUrl", fbProfileImageUrl);
			          
			          ParseUser.getCurrentUser().saveInBackground();
			          //gotoProfileActivity();
			      }
			  }
			}).executeAsync();
		}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  super.onActivityResult(requestCode, resultCode, data);
	  ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	
// Methods below this are unused but useful for testing	
//-----------------------------------------------------
	private void signInParseUser() {
		ParseUser.logInInBackground("sahil0984", "secret123", new LogInCallback() {
			  public void done(ParseUser user, ParseException e) {
			    if (user != null) {
			    	// Hooray! The user is logged in.
			    	Toast.makeText(getApplicationContext(), "Logged In!", Toast.LENGTH_SHORT).show();
			    	gotoHomeActivity();
			    } else {
			    	// Signup failed. Look at the ParseException to see what happened.
			    	e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Invalid login credentials!", Toast.LENGTH_SHORT).show();
					createParseUser();
			    }
			  }
			});	
	}

	private void createParseUser() {
		// Create the ParseUser
		ParseUser user = new ParseUser();
		// Set core properties
		user.setUsername("sahil0984");
		user.setPassword("secret123");
		user.setEmail("sahil.ec@gmail.com");
		// Set custom properties
		user.put("phone", "650-253-0000");
		// Invoke signUpInBackground
		user.signUpInBackground(new SignUpCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {
					// Hooray! Let them use the app now.
					gotoHomeActivity();
				} else {
					// Sign up didn't succeed. Look at the ParseException
					// to figure out what went wrong
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Username already taken. Signing in with default user.", Toast.LENGTH_SHORT).show();
				}			
			}
		});		
	}
}
