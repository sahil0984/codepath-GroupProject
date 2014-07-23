package com.codepath.groupproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.groupproject.R.string;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends ActionBarActivity {
	
	ProgressBar pbLoading;
	
	String userType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
		
		pbLoading.setVisibility(ProgressBar.INVISIBLE);
		
		getActionBar().hide();
		
    	//This accesses cached current user.
    	ParseUser currentUser = ParseUser.getCurrentUser();
    	if (currentUser != null && ParseFacebookUtils.isLinked(currentUser)) {
    		// do stuff with the user
    		userType = "existingUser";
    		getFacebookDetailsInBackground();
    		pbLoading.setVisibility(ProgressBar.VISIBLE);
    	} else {
    		// show the signup or login screen
    		userType = "newUser";
    	}
    	
	}
	
	
	public void gotoProfileActivity() {
		pbLoading.setVisibility(ProgressBar.INVISIBLE);
		Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
		startActivity(i);
		overridePendingTransition(R.anim.fade_in, R.anim.slide_up);
	}
	
	public void gotoHomeActivity() {
		pbLoading.setVisibility(ProgressBar.INVISIBLE);
		pbLoading.setEnabled(false);
		Intent i = new Intent(getApplicationContext(), HomeActivity.class);
		startActivity(i);
		overridePendingTransition(R.anim.fade_in, R.anim.slide_up);
	}
	
	//Arrays.asList("email", Permissions.User.EMAIL),
	public void onLoginWithFacebook(View v) {
		//if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
			//Dont do anything.
			
		//} else {
		
		pbLoading.setVisibility(ProgressBar.VISIBLE);


			ParseFacebookUtils.logIn(Arrays.asList("email", "user_friends"), this, new LogInCallback() {
			//ParseFacebookUtils.logIn(Arrays.asList("email", "user_friends", "user_friendlists"), this, new LogInCallback() {
				
								
				@Override
				public void done(ParseUser user, ParseException err) {
					if (user == null) {
						Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
						err.printStackTrace();
					} else if (user.isNew()) {
						Log.d("MyApp", "User signed up and logged in through Facebook!");
						getFacebookDetailsInBackground();
					} else {
						Log.d("MyApp", "User logged in through Facebook!");
						getFacebookDetailsInBackground();
					}
				}
			});
			
	}
	
	
	private void getFacebookDetailsInBackground() {
		
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
			          
			          //ParseUser.getCurrentUser().put("", user.getInnerJSONObject());
			          //Log.d("FBJSON", response.toString());
			     
			          
			          String URL = "https://graph.facebook.com/" + user.getId() + "?fields=cover&access_token=" + ParseFacebookUtils.getSession().getAccessToken();
			          Log.d("FBJSON", URL.toString());

			         
			          ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException pE) {
							if (pE==null) {
								//Get current installation and save the userObjectId corresponding to the installation
								ParseInstallation.getCurrentInstallation().put("userObjectId", ParseUser.getCurrentUser().getObjectId().toString());
								ParseInstallation.getCurrentInstallation().saveInBackground();
								
								
								ParseQuery<User> innerUserQuery = ParseQuery.getQuery(User.class);
								innerUserQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
								
								ParseQuery<Group> queryGroup = ParseQuery.getQuery(Group.class);
								queryGroup.include("members");
								queryGroup.whereMatchesQuery("members", innerUserQuery);
								queryGroup.findInBackground(new FindCallback<Group>() {

									@Override
									public void done(List<Group> groupList, ParseException e) {
								        if (e == null) {
								        	if (groupList.size()!=0) {
								        		for (int i=0; i<groupList.size(); i++) {
								        			PushService.subscribe(getApplicationContext(),
								        					"channel_" + groupList.get(i).getObjectId(), HomeActivity.class);

								        		}
								        	} else {
								        		//Toast.makeText(getApplicationContext(), "No groups found.", Toast.LENGTH_SHORT).show();
								        	}
								        } else {
								        	Log.d("item", "Error: " + e.getMessage());
								        }
									}
								});
								
								
								if (userType=="newUser") {
									gotoProfileActivity();									
								} else {
									gotoHomeActivity();
								}
								
							} else {
								Log.d("FBJSON", pE.toString());
							}
						}
					});
			          //gotoProfileActivity();
			          
			      }
			  }
			}).executeAsync();    
          
		  getFriends();
          //getFriendLists();
		  
		  
		  
	        new Request(
					ParseFacebookUtils.getSession(),
	    		    "/me/friends",
	    		    null,
	    		    HttpMethod.GET,
	    		    new Request.Callback() {
	    		        public void onCompleted(Response response) {
	    		            /* handle the result */
	    		        	//Log.d("FBJSON", response.toString());
	    		        	
	    		        	//Create a list of friends with fbId and add it to the current Parse user
	    		        	JSONArray fbFriendsJsonArray;
	    		        	ArrayList<String> fbFriendsIds = new ArrayList<String>();
	    		        	try {
								fbFriendsJsonArray = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
		      		        	for (int i=0; i<fbFriendsJsonArray.length(); i++) {
		      		        		Log.d("FacebookFrndId", fbFriendsJsonArray.getJSONObject(i).getString("id"));
		      		        		fbFriendsIds.add(fbFriendsJsonArray.getJSONObject(i).getString("id"));
			      					
			      		        }
		      					
							} catch (JSONException e) {
								e.printStackTrace();
							}
	    		        	
	    		        	//Now fbFriendsIds contains the array of fb friends id's
	    		        	ParseUser.getCurrentUser().remove("fbFriendsIds");
	    		        	ParseUser.getCurrentUser().saveInBackground();
	    		        	ParseUser.getCurrentUser().add("fbFriendsIds", fbFriendsIds);
	    		        	ParseUser.getCurrentUser().saveInBackground();
	    		        }
	    		    }
	    		).executeAsync(); 
          
	}
	
	public void getFriends() {
		  //Gets friends who are using the app
        new Request(
				ParseFacebookUtils.getSession(),
    		    "/me/friends",
    		    null,
    		    HttpMethod.GET,
    		    new Request.Callback() {
    		        public void onCompleted(Response response) {
    		            /* handle the result */
    		        	//Log.d("FBJSON", response.toString());
    		        	
    		        	//Create a list of friends with fbId and add it to the current Parse user
    		        	JSONArray fbFriendsJsonArray;
    		        	ArrayList<String> fbFriendsIds = new ArrayList<String>();
    		        	try {
							fbFriendsJsonArray = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
	      		        	for (int i=0; i<fbFriendsJsonArray.length(); i++) {
	      		        		Log.d("FacebookFrndId", fbFriendsJsonArray.getJSONObject(i).getString("id"));
	      		        		fbFriendsIds.add(fbFriendsJsonArray.getJSONObject(i).getString("id"));
		      					
		      		        }
	      					
						} catch (JSONException e) {
							e.printStackTrace();
						}
    		        	
    		        	//Now fbFriendsIds contains the array of fb friends id's
    		        	ParseUser.getCurrentUser().remove("fbFriendsIds");
    		        	ParseUser.getCurrentUser().saveInBackground();
    		        	ParseUser.getCurrentUser().add("fbFriendsIds", fbFriendsIds);
    		        	ParseUser.getCurrentUser().saveInBackground();
    		        }
    		    }
    		).executeAsync(); 
	}
	
	public void getFriendLists() {
		  //Gets lists of the friend lists names of the user
        new Request(
				ParseFacebookUtils.getSession(),
    		    "/me/friendlists",
    		    null,
    		    HttpMethod.GET,
    		    new Request.Callback() {
    		        public void onCompleted(Response response) {
    		            /* handle the result */
    		        	//Log.d("FBJSON", response.toString());
    		        	
    		        	//Create a list of work and education and add it to the current Parse user
    		        	JSONArray fbListsJsonArray;
    		        	ArrayList<String> fbWorkList = new ArrayList<String>();
    		        	ArrayList<String> fbSchoolList = new ArrayList<String>();
    		        	try {
							fbListsJsonArray = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
	      		        	//Log.d("FacebookLists", fbListsJsonArray.toString());
	      		        	for (int i=0; i<fbListsJsonArray.length(); i++) {
	      		        		String listType = fbListsJsonArray.getJSONObject(i).getString("list_type");
	      		        		String listValue = fbListsJsonArray.getJSONObject(i).getString("name");
	      		        		if (listType.matches("work")) {
	      		        			fbWorkList.add(listValue);
	      		        			//Log.d("FacebookWorkList", listValue);
	      		        		} else if (listType.matches("education")) {
	      		        			fbSchoolList.add(listValue);
	      		        			//Log.d("FacebookSchoolList", listValue);
	      		        		}
	      		        		
	      		        	}

						} catch (JSONException e) {
							e.printStackTrace();
						}
    		        	
    		        	//Now fbWorkList contains work networks and fbSchoolList contains school networks
    		        	ParseUser.getCurrentUser().add("fbWorkList", fbWorkList);
    		        	ParseUser.getCurrentUser().add("fbSchoolList", fbSchoolList);
    		        	ParseUser.getCurrentUser().saveInBackground();
    		        }
    		    }
    		).executeAsync();    
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
