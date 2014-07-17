package com.codepath.groupproject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.http.Header;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
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
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ProfileActivity extends ActionBarActivity {
	
	private ProfilePictureView ivProfileImage;
	private EditText etFirstName;
	private EditText etLastName;
	private EditText etHomeAdd;
	private EditText etWorkAdd;
	private EditText etPhone;
	private EditText etPersonalEmail;
	private EditText etEmail;
	private CheckBox cbIsPublic;
	
	private Button btnVerifyEmail;
	private Button btnGoHome;
	
	private Menu mOptionsMenu;
	
	private ParseGeoPoint homeLatLng;
	private ParseGeoPoint workLatLng;
	private int oneAddressVerifDoneFlag;
	private int State_GeoCodeTask;
	private String homeAdd;
	private String workAdd;

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
		cbIsPublic		= (CheckBox) findViewById(R.id.cbIsPublic);
		
		btnVerifyEmail = (Button) findViewById(R.id.btnVerifyEmail);
		btnGoHome      = (Button) findViewById(R.id.btnDone);

		btnVerifyEmail.setEnabled(false);
		
		etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
		
		oneAddressVerifDoneFlag = 0;
		State_GeoCodeTask = 2;
		doDoneEditProfileTasks();
		
		//stopEditProfile();
	}
	
	public void stopEditProfile() {
		
		//This sends a request and callback changes the states and call this function to finish things.
		//String homeAdd = getAddFromCoor((ParseGeoPoint) ParseUser.getCurrentUser().get("homeAdd"));
		//String workAdd = getAddFromCoor((ParseGeoPoint) ParseUser.getCurrentUser().get("workAdd"));
		
		ivProfileImage.setProfileId((String) ParseUser.getCurrentUser().get("fbId"));
		
		String firstName = (String) ParseUser.getCurrentUser().get("firstName");
		String lastName = (String) ParseUser.getCurrentUser().get("lastName");
		String homeAdd = this.homeAdd;
		String workAdd = this.workAdd;
		String phone = (String) ParseUser.getCurrentUser().get("phone");
		String personalEmail = (String) ParseUser.getCurrentUser().get("personalEmail");
		String email = (String) ParseUser.getCurrentUser().get("email");
		Boolean isPublic = (Boolean) ParseUser.getCurrentUser().get("isPublic");
		
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
		//IsPublic
		cbIsPublic.setChecked(isPublic);
		cbIsPublic.setEnabled(false);
		
		//etEmail.setBackgroundColor(android.graphics.Color.CYAN);
		//etEmail.setBackgroundResource(0);
		

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
		//IsPublic
		cbIsPublic.setEnabled(true);

		//etPersonalEmail.setBackgroundResource(R.drawable.backtext);
		//etEmail.setBackgroundResource(R.drawable.backtext);
		//etEmail.setBackgroundResource(0);

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
		
		//if (etHomeAdd.getText().equals("") || etWorkAdd.getText().equals("") ) {
			Intent i = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(i);			
		//} else {
		//	Toast.makeText(getApplicationContext(), "Please enter the required fields.", Toast.LENGTH_SHORT).show();
		//}

	}
	
	public void saveUpdatedInfo() {
		ParseUser.getCurrentUser().put("firstName", etFirstName.getText().toString());
		ParseUser.getCurrentUser().put("lastName", etLastName.getText().toString());
		//ParseUser.getCurrentUser().put("homeAdd", etHomeAdd.getText().toString());
		//ParseUser.getCurrentUser().put("workAdd", etWorkAdd.getText().toString());
		if (homeLatLng != null) {
			ParseUser.getCurrentUser().put("homeAdd", homeLatLng);			
		}
		if (workLatLng != null) {
			ParseUser.getCurrentUser().put("workAdd", workLatLng);			
		}		
		ParseUser.getCurrentUser().put("phone", etPhone.getText().toString());
		ParseUser.getCurrentUser().put("personalEmail", etPersonalEmail.getText().toString());
		ParseUser.getCurrentUser().put("email", etEmail.getText().toString());
		ParseUser.getCurrentUser().put("isPublic", cbIsPublic.isChecked());
		ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException arg0) {
				oneAddressVerifDoneFlag = 0;
				State_GeoCodeTask = 2;
				doDoneEditProfileTasks();
			}
		});
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
            	doEditProfileTasks();
                return true;
            case R.id.miDoneEditProfile:
    			oneAddressVerifDoneFlag = 0;
    			State_GeoCodeTask = 0;
            	doDoneEditProfileTasks();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void doEditProfileTasks() {
        hideOption(R.id.miEditProfile);
        showOption(R.id.miDoneEditProfile);
        btnVerifyEmail.setEnabled(true);
        btnGoHome.setEnabled(false);
        editProfile();
    }
    
    public void doDoneEditProfileTasks() {
    	switch (State_GeoCodeTask) {
		case 0:
	        showOption(R.id.miEditProfile);
	        hideOption(R.id.miDoneEditProfile);
	        btnVerifyEmail.setEnabled(false);
	        btnGoHome.setEnabled(true);
	        checkAddresses();
			break;
		case 1:
	        saveUpdatedInfo();
	        break;
		case 2:
	        updateAddresses();
	        break;
		case 3:
	        stopEditProfile(); 			
			break;
		default:
			break;
		}
    }


    private void checkAddresses() {
		getVerifySetAdd("home", etHomeAdd.getText().toString());
		getVerifySetAdd("work", etWorkAdd.getText().toString());
	}
    private void updateAddresses() {
		ParseGeoPoint pUserHomeAdd = ParseUser.getCurrentUser().getParseGeoPoint("homeAdd");
		ParseGeoPoint pUserWorkAdd = ParseUser.getCurrentUser().getParseGeoPoint("workAdd");
		if (pUserHomeAdd!=null && pUserWorkAdd!=null) {
			getAddFromCoor("home", pUserHomeAdd);
			getAddFromCoor("work", pUserWorkAdd);
		} else {
			this.homeAdd = "";
			this.workAdd = "";
			oneAddressVerifDoneFlag = 0;
			State_GeoCodeTask = 3;
			doDoneEditProfileTasks();
		}
	}
    
	public void getVerifySetAdd (final String tag, String address) {
		
		String formattedAddress = address.trim().replaceAll(" +", "+");
		//Toast.makeText(getApplicationContext(), formattedAddress, Toast.LENGTH_LONG).show();

    	//https://developers.google.com/maps/documentation/geocoding/
	    String url = "http://maps.google.com/maps/api/geocode/json?address=" + formattedAddress;
	    AsyncHttpClient client = new AsyncHttpClient();
	    client.get(url, null, new JsonHttpResponseHandler() {
	    	
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				
				String status = null;
				JSONArray results;
				String checkedAdd = null;
				String lat = null;
				String lng = null;
				String latLng = null;
				try {
					status = response.getString("status");
					results = response.getJSONArray("results");
					//Toast.makeText(getApplicationContext(), "Length:" + results.length() + " Status="+ status, Toast.LENGTH_SHORT).show();

					if (status.equals("OK") && results.length()>0) {
						checkedAdd = results.getJSONObject(0).getString("formatted_address");
						lat = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat");
						lng = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng");
						//latLng = lat + "," + lng;
						
						setCoord(tag, lat, lng);
						
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Error checking address", Toast.LENGTH_SHORT).show();
					return;
				}				
				//Toast.makeText(getApplicationContext(), checkedAdd + ":" + lat + "," + lng, Toast.LENGTH_SHORT).show();
				if (oneAddressVerifDoneFlag==1) {
					oneAddressVerifDoneFlag = 0;
					State_GeoCodeTask = 1;
					doDoneEditProfileTasks();
				} else {
			    	oneAddressVerifDoneFlag = oneAddressVerifDoneFlag + 1;
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(getApplicationContext(), "Error checking address", Toast.LENGTH_SHORT).show();
				//BOZO: Handle failure by asking user to try again. And resetting to edit profile state.
			}

	    });
	    
    }
    
    public void setCoord(String tag, String lat, String lng) {    	
    	
    	if (tag.equals("home")) {
    		homeLatLng = new ParseGeoPoint(Double.parseDouble(lat), Double.parseDouble(lng));
    	} else if (tag.equals("work")) {
    		workLatLng = new ParseGeoPoint(Double.parseDouble(lat), Double.parseDouble(lng));
    	}
    	
    }
    
    public String getAddFromCoor(final String tag, ParseGeoPoint pCoord) {
    	Double lat = pCoord.getLatitude();
    	Double lng = pCoord.getLongitude();
	    String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng;
	    AsyncHttpClient client = new AsyncHttpClient();
	    client.get(url, null, new JsonHttpResponseHandler() {
	    	
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				
				String status = null;
				JSONArray results;
				String checkedAdd = null;
				String lat = null;
				String lng = null;
				String latLng = null;
				try {
					status = response.getString("status");
					results = response.getJSONArray("results");
					//Toast.makeText(getApplicationContext(), "Length:" + results.length() + " Status="+ status, Toast.LENGTH_SHORT).show();

					if (status.equals("OK") && results.length()>0) {
						checkedAdd = results.getJSONObject(0).getString("formatted_address");
						//lat = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat");
						//lng = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng");
						//latLng = lat + "," + lng;
						
						setAddress(tag, checkedAdd);
						
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Error checking address", Toast.LENGTH_SHORT).show();
					return;
				}				
				//Toast.makeText(getApplicationContext(), checkedAdd + ":" + lat + "," + lng, Toast.LENGTH_SHORT).show();
				if (oneAddressVerifDoneFlag==1) {
					oneAddressVerifDoneFlag = 0;
					State_GeoCodeTask = 3;
					doDoneEditProfileTasks();
				} else {
			    	oneAddressVerifDoneFlag = oneAddressVerifDoneFlag + 1;
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				Toast.makeText(getApplicationContext(), "Error retrieving address", Toast.LENGTH_SHORT).show();
				//BOZO: Handle failure by asking user to try again. And resetting to edit profile state.
			}
	    });
    	return null;
    }
    
    public void setAddress(String tag, String checkedAdd) {    	
    	
    	if (tag.equals("home")) {
    		homeAdd = checkedAdd;
    	} else if (tag.equals("work")) {
    		workAdd = checkedAdd;
    	}  	
		
    }
}
