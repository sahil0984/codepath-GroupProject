package com.codepath.groupproject;


import org.apache.http.Header;
import org.json.JSONObject;

import com.facebook.widget.ProfilePictureView;
import com.loopj.android.http.AsyncHttpClient;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class ProfileActivity extends ActionBarActivity {
	
	private ProfilePictureView ivProfileImage;
	private FloatLabeledEditText etFirstName;
	private FloatLabeledEditText etLastName;
	private FloatLabeledEditText etHomeAdd;
	private FloatLabeledEditText etWorkAdd;
	//private FloatLabeledEditText etPhone;
	private FloatLabeledEditText etPersonalEmail;
	private FloatLabeledEditText etEmail;
	private CheckBox cbIsPublic;
	
	private Button btnVerifyEmail;
	//private Button btnGoHome;
	
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
		
		oneAddressVerifDoneFlag = 0;
		State_GeoCodeTask = 2;
		doDoneEditProfileTasks();
	}

	private void setupViews() {
		ivProfileImage  = (ProfilePictureView) findViewById(R.id.ivProfileImage);
		etFirstName     = (FloatLabeledEditText) findViewById(R.id.etFirstName);
		etLastName 	    = (FloatLabeledEditText) findViewById(R.id.etLastName);
		etHomeAdd       = (FloatLabeledEditText) findViewById(R.id.etHomeAdd);
		etWorkAdd       = (FloatLabeledEditText) findViewById(R.id.etWorkAdd);
		//etPhone         = (FloatLabeledEditText) findViewById(R.id.etPhone);
		etPersonalEmail = (FloatLabeledEditText) findViewById(R.id.etPersonalEmail);
		etEmail         = (FloatLabeledEditText) findViewById(R.id.etEmail);
		cbIsPublic		= (CheckBox) findViewById(R.id.cbIsPublic);
		
		btnVerifyEmail = (Button) findViewById(R.id.btnVerifyEmail);
		//btnGoHome      = (Button) findViewById(R.id.btnDone);

		btnVerifyEmail.setEnabled(false);
		
		cbIsPublic.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					buttonView.setText("Public Profile");
				} else {
					buttonView.setText("Local Profile");
				}
			}
		});
		
		//etPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
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
		if (isPublic==null) {
			isPublic = false;
		}
		
		//First Name
		etFirstName.setText(firstName);
		etFirstName.requestFocus();
		
		//Last Name
		etLastName.setText(lastName);
		etLastName.requestFocus();

		//Home Address
		etHomeAdd.setText(homeAdd);
		etHomeAdd.requestFocus();
		
		//Work Address
		etWorkAdd.setText(workAdd);
		etWorkAdd.requestFocus();

		//Phone number
		//etPhone.setText(phone);

		//Personal Email
		etPersonalEmail.setText(personalEmail);
		etPersonalEmail.requestFocus();
		
		//Email
		etEmail.setText(email);
		etEmail.requestFocus();

		//IsPublic
		cbIsPublic.setChecked(isPublic);

		
		etFirstName.requestFocus();

		
		etFirstName.setBackgroundResource(0);
		etLastName.setBackgroundResource(0);
		etHomeAdd.setBackgroundResource(0);
		etWorkAdd.setBackgroundResource(0);
		etPersonalEmail.setBackgroundResource(0);
		etEmail.setBackgroundResource(0);
		

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
		//etPhone.setFocusableInTouchMode(true);
		//etPhone.setFocusable(true);
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
		
        btnVerifyEmail.setEnabled(true);
        //btnGoHome.setEnabled(false);
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
	
	public void onDone () {
		oneAddressVerifDoneFlag = 0;
		State_GeoCodeTask = 0;
		doDoneEditProfileTasks();
		
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
		//ParseUser.getCurrentUser().put("phone", etPhone.getText().toString());
		ParseUser.getCurrentUser().put("personalEmail", etPersonalEmail.getText().toString());
		ParseUser.getCurrentUser().put("email", etEmail.getText().toString());
		ParseUser.getCurrentUser().put("isPublic", cbIsPublic.isChecked());
		ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e==null) {
					oneAddressVerifDoneFlag = 0;
					State_GeoCodeTask = 2;
					doDoneEditProfileTasks();
				} else {
					e.printStackTrace();
					Toast.makeText(getApplicationContext(), "Error Saving info!", Toast.LENGTH_SHORT).show();
					//doEditProfileTasks();
					editProfile();
				}
			}
		});
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile_activity_actions, menu);
        mOptionsMenu = menu;

        //showOption(R.id.miEditProfile);
        //hideOption(R.id.miDoneEditProfile);
        return true;
    }
    
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      // Handle presses on the action bar items
      switch (item.getItemId()) {
          case R.id.miDone:
  			onDone();
              return true;
          default:
              return super.onOptionsItemSelected(item);
      }
  }
    
    
//    
//    private void hideOption(int id)
//    {
//        MenuItem item = mOptionsMenu.findItem(id);
//        item.setVisible(false);
//    }
//
//    private void showOption(int id)
//    {
//        MenuItem item = mOptionsMenu.findItem(id);
//        item.setVisible(true);
//    }
//    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
//            case R.id.miEditProfile:
//            	doEditProfileTasks();
//                return true;
//            case R.id.miDoneEditProfile:
//    			oneAddressVerifDoneFlag = 0;
//    			State_GeoCodeTask = 0;
//            	doDoneEditProfileTasks();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//    
//    public void doEditProfileTasks() {
//        hideOption(R.id.miEditProfile);
//        showOption(R.id.miDoneEditProfile);
//        btnVerifyEmail.setEnabled(true);
//        btnGoHome.setEnabled(false);
//        editProfile();
//    }
    
    public void doDoneEditProfileTasks() {
    	switch (State_GeoCodeTask) {
		case 0:
	        //showOption(R.id.miEditProfile);
	        //hideOption(R.id.miDoneEditProfile);
	        //btnVerifyEmail.setEnabled(false);
	        //btnGoHome.setEnabled(true);
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
    
    public void getVerifySetAdd(final String tag, String address) {
		String formattedAddress = address.trim().replaceAll(" +", "+");
		//Toast.makeText(getApplicationContext(), formattedAddress, Toast.LENGTH_LONG).show();

    	//https://developers.google.com/maps/documentation/geocoding/
	    String url = "http://maps.google.com/maps/api/geocode/json?address=" + formattedAddress;
	    
	    
	    AsyncHttpClient client = new AsyncHttpClient();
	    client.get(url, null, new GeoCoderResponseHandler(getApplicationContext()) {

	    	@Override
	    	public void onSuccess(int statusCode, Header[] headers,
	    			JSONObject response) {
	    		super.onSuccess(statusCode, headers, response);
	    		
		        if (tag.equals("home")) {
		    		homeLatLng = getLatLng();
		    		//homeAdd = getCheckedAdd();
		    	} else if (tag.equals("work")) {
		    		workLatLng = getLatLng();
		    		//workAdd = getCheckedAdd();
		    	}
		        
				if (oneAddressVerifDoneFlag==1) {
					oneAddressVerifDoneFlag = 0;
					State_GeoCodeTask = 1;
					doDoneEditProfileTasks();
				} else {
			    	oneAddressVerifDoneFlag = oneAddressVerifDoneFlag + 1;
				}
	    	}
	    	
	    });
    }
    
    public void getAddFromCoor(final String tag, ParseGeoPoint pCoord) {
    	Double lat = pCoord.getLatitude();
    	Double lng = pCoord.getLongitude();
	    String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng;
	    AsyncHttpClient client = new AsyncHttpClient();
	    client.get(url, null, new GeoCoderResponseHandler(getApplicationContext()) {
	    	
	    	@Override
	    	public void onSuccess(int statusCode, Header[] headers,
	    			JSONObject response) {
	    		super.onSuccess(statusCode, headers, response);
	    		
		        if (tag.equals("home")) {
		    		//homeLatLng = getLatLng();
		    		homeAdd = getCheckedAdd();
		    	} else if (tag.equals("work")) {
		    		//workLatLng = getLatLng();
		    		workAdd = getCheckedAdd();
		    	}
		        
				if (oneAddressVerifDoneFlag==1) {
					oneAddressVerifDoneFlag = 0;
					State_GeoCodeTask = 3;
					doDoneEditProfileTasks();
				} else {
			    	oneAddressVerifDoneFlag = oneAddressVerifDoneFlag + 1;
				}
	    	}
	    	
	    });
		return;
    }
}
