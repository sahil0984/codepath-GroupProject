package com.codepath.groupproject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.codepath.groupproject.dialogs.ChoosePhotoDialog;
import com.codepath.groupproject.dialogs.ChoosePhotoDialog.OnDataPass;
import com.codepath.groupproject.models.Group;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
//import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

public class CreateGroupActivity extends FragmentActivity implements OnDataPass, OnDateSetListener, TimePickerDialog.OnTimeSetListener {
	
	private ImageView ivGroupPhoto;
	private FloatLabeledEditText etGroupName;
	private TextView tvOnwardTime;
	private TextView tvReturnTime;
	
	private TextView tvDate;

	private CheckBox cbRecurring;
	private LinearLayout llCheckBoxDays;
	private CheckBox cbMonday;
	private CheckBox cbTuesday;
	private CheckBox cbWednesday;
	private CheckBox cbThursday;
	private CheckBox cbFriday;
	private CheckBox cbSaturday;
	private CheckBox cbSunday;
	
	private FloatLabeledEditText etOnwardLocation;
	private FloatLabeledEditText etReturnLocation;
	
	private CheckBox cbIsPublic;
	
	private Button btnCreate;
	
	private String onwardTime;
	private String returnTime;
	private String date;
	private ArrayList<String> groupMembers;

	
	public final String APP_TAG = "GroupProjectApp";
	public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
	public final static int PICK_PHOTO_CODE = 1046;
	private static final int ADD_USERS_REQUEST_CODE = 20;
	public String photoFileName = "photo.jpg";
	
	byte[] byteArray;
	ParseFile photoFile;
	
	private ParseGeoPoint onwardLatLng;
	private ParseGeoPoint returnLatLng;
	private int oneAddressVerifDoneFlag;
	private int State_GeoCodeTask;
	//private String onwardAdd;
	//private String returnAdd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		
		setupViews();
		
	}

	private void setupViews() {
		ivGroupPhoto = (ImageView) findViewById(R.id.ivGroupPhoto);
		etGroupName = (FloatLabeledEditText) findViewById(R.id.etGroupName);
		tvOnwardTime = (TextView) findViewById(R.id.tvOnwardTime);
		tvReturnTime = (TextView) findViewById(R.id.tvReturnTime);
		
		tvDate = (TextView) findViewById(R.id.tvDate);

		cbRecurring = (CheckBox) findViewById(R.id.cbRecurring);
		
		llCheckBoxDays = (LinearLayout) findViewById(R.id.llCheckBoxDays);
		cbMonday = (CheckBox) findViewById(R.id.cbMonday);
		cbTuesday = (CheckBox) findViewById(R.id.cbTuesday);
		cbWednesday = (CheckBox) findViewById(R.id.cbWednesday);
		cbThursday = (CheckBox) findViewById(R.id.cbThursday);
		cbFriday = (CheckBox) findViewById(R.id.cbFriday);
		cbSaturday = (CheckBox) findViewById(R.id.cbSaturday);
		cbSunday = (CheckBox) findViewById(R.id.cbSunday);
		
		etOnwardLocation = (FloatLabeledEditText) findViewById(R.id.etOnwardLocation);
		etReturnLocation = (FloatLabeledEditText) findViewById(R.id.etReturnLocation);
		
		cbIsPublic = (CheckBox) findViewById(R.id.cbIsPublic);
		
		btnCreate = (Button) findViewById(R.id.btnCreate);
		
		cbIsPublic.setChecked(true);
		cbIsPublic.setText("Public Group");
		
		cbRecurring.setChecked(false);
		llCheckBoxDays.setVisibility(View.INVISIBLE);
		tvDate.setVisibility(View.VISIBLE);
		
		ivGroupPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			  	FragmentManager fm = getSupportFragmentManager();
			  	ChoosePhotoDialog photoDialog = new ChoosePhotoDialog();
			  	photoDialog.show(fm, "dialog_choose_photo");
			}
		});
		
		tvDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onPickDate();
			}
		});
		
		tvOnwardTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onPickTime(v);
			}
		});
		
		tvReturnTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onPickTime(v);
			}
		});		
		
		btnCreate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				oneAddressVerifDoneFlag = 0;
				State_GeoCodeTask = 0;
				onAddGroupTasks();
			}
		});
		
		cbRecurring.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {			
				if (isChecked) {
					llCheckBoxDays.setVisibility(View.VISIBLE);
					tvDate.setVisibility(View.INVISIBLE);
				} else {
					llCheckBoxDays.setVisibility(View.INVISIBLE);
					tvDate.setVisibility(View.VISIBLE);
				}
			}
		});
		
		cbIsPublic.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					buttonView.setText("Public Group");
				} else {
					buttonView.setText("Local Group");
				}
			}
		});
		

	}
	
//---- Pick Date fragment code starts here -------
	//onClick method for picking date
	public void onPickDate() {
	       	final Calendar calendar = Calendar.getInstance();
	        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
	        
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(false);
            datePickerDialog.show(getSupportFragmentManager(), "datepicker");
	}
	//Interface method for SelectDateFragment Class
	public void populateSetDate(int year, int month, int day) {
		DecimalFormat formatter = new DecimalFormat("00");
		tvDate.setText(formatter.format(month) + "/" + formatter.format(day) + "/" + year);
	}

	@Override
	public void onDateSet(
			com.fourmob.datetimepicker.date.DatePickerDialog datePickerDialog,
			int year, int month, int day) {
		populateSetDate(year, month + 1, day);
	}
//----- End of Pick Date Fragment -----

//---- Pick Time fragment code starts here -------
	//onClick method for picking date
	public void onPickTime(View v) {
	    final Calendar calendar = Calendar.getInstance();
        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

        timePickerDialog.setCloseOnSingleTapMinute(false);
		if (v.getId() == tvOnwardTime.getId()) {
	        timePickerDialog.show(getSupportFragmentManager(), "OnwardTimePicker");
		} else {
	        timePickerDialog.show(getSupportFragmentManager(), "ReturnTimePicker");
		}
	}
	
	//Interface method for SelectDateFragment Class
	public void populateSetTime(int hour, int minute) {
		DecimalFormat formatter = new DecimalFormat("00");
		
        TimePickerDialog tpdOnwardTime = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag("OnwardTimePicker");
        TimePickerDialog tpdReturnTime = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag("ReturnTimePicker");
        if (tpdOnwardTime!=null) {
            tvOnwardTime.setText(formatter.format(hour) + ":" + formatter.format(minute));
        } else if (tpdReturnTime!=null) {
        	tvReturnTime.setText(formatter.format(hour) + ":" + formatter.format(minute));
        }
	}
	
	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		populateSetTime(hourOfDay, minute);
	}
//----- End of Pick Time Fragment -----
	
	
	public void onAddGroupTasks() {
		
		switch (State_GeoCodeTask) {
		case 0:
			if (!isFormOkay()) {
				break;
			}
			getVerifySetAdd("onward", etOnwardLocation.getText().toString());
			getVerifySetAdd("return", etReturnLocation.getText().toString());
			break;
		//case 1:
			//sendPushNotification();
			//break;
		case 2:
			prepareIntent();
			finish();
	        break;
		default:
			break;
		}
		
	}
	
	public boolean isFormOkay() {
		if (etGroupName.getText().toString().equals("") || 
			etOnwardLocation.getText().toString().equals("") || 
			etReturnLocation.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "Please enter the required fields!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	public void prepareIntent() {
		String tmpDate;
		if (cbRecurring.isChecked()) {
			tmpDate = tvDate.getText().toString();
		} else {
			tmpDate = "01/01/3001";
		}

		
		Intent data = new Intent();
		data.putExtra("groupName", etGroupName.getText().toString());
		data.putExtra("onwardTime", tmpDate+" "+tvOnwardTime.getText().toString());
		data.putExtra("returnTime", tmpDate+" "+tvReturnTime.getText().toString());
		data.putExtra("recurring", cbRecurring.isChecked());
		data.putExtra("daysOfWeek", daysOfWeek());
		data.putExtra("photoBytes", byteArray);
		data.putExtra("onwardLat", onwardLatLng.getLatitude());
		data.putExtra("onwardLng", onwardLatLng.getLongitude());
		data.putExtra("returnLat", returnLatLng.getLatitude());
		data.putExtra("returnLng", returnLatLng.getLongitude());
		data.putExtra("groupMembers",groupMembers);
		data.putExtra("isPublic", cbIsPublic.isChecked());
				
		setResult(RESULT_OK, data);
	}
	
	public void onAddUsers(View v) {
		Intent i = new Intent(getApplicationContext(), AddUsersActivity.class);
		i.putExtra("currentGroupMembers",groupMembers);
		startActivityForResult(i, ADD_USERS_REQUEST_CODE);	
	}

	private boolean[] daysOfWeek() {
		boolean[] daysOfWeekArray = new boolean[7];
		daysOfWeekArray[0] = cbMonday.isChecked();
		daysOfWeekArray[1] = cbTuesday.isChecked();
		daysOfWeekArray[2] = cbWednesday.isChecked();
		daysOfWeekArray[3] = cbThursday.isChecked();
		daysOfWeekArray[4] = cbFriday.isChecked();
		daysOfWeekArray[5] = cbSaturday.isChecked();
		daysOfWeekArray[6] = cbSunday.isChecked();
		return daysOfWeekArray;
	}

	public void onLaunchCamera() {
	    // create Intent to take a picture and return control to the calling application
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name
	    // Start the image capture intent to take photo
	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	public void onLaunchGallery() {
	    // Create intent for picking a photo from the gallery
	    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	    // Bring up gallery to select a photo
	    startActivityForResult(intent, PICK_PHOTO_CODE);
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			Uri photoUri = null;
			if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
				photoUri = getPhotoFileUri(photoFileName);
				// by this point we have the camera photo on disk
				// Load the taken image into a preview
				Picasso.with(this).load(photoUri).resize(80, 80).into(ivGroupPhoto);
				getBitmap(photoUri);
			} else if (requestCode == PICK_PHOTO_CODE) {
				photoUri = data.getData();
				// Do something with the photo based on Uri
				Picasso.with(this).load(photoUri).resize(80, 80).into(ivGroupPhoto);
				getBitmap(photoUri);
			} else if (requestCode == ADD_USERS_REQUEST_CODE) {
				groupMembers = data.getStringArrayListExtra("groupMembers");
			}
		}	
	}

	
	//Creating a target class to get the resized bitmap data from Picasso
	private Target target = new Target() {
		@Override
		public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {	
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byteArray = stream.toByteArray();
			//photoFile = new ParseFile("group_photo.jpg", byteArray);
		}

		@Override
		public void onBitmapFailed(Drawable arg0) {
		}
		@Override
		public void onPrepareLoad(Drawable arg0) {			
		}
	};
	private void getBitmap(Uri photoUri) {
	   Picasso.with(this).load(photoUri).resize(80, 80).into(target);
	}
	
	// Returns the Uri for a photo stored on disk given the fileName
	public Uri getPhotoFileUri(String fileName) {
	    // Get safe storage directory for photos
	    File mediaStorageDir = new File(
	        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

	    // Create the storage directory if it does not exist
	    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
	        Log.d(APP_TAG, "failed to create directory");
	    }

	    // Return the file target for the photo based on filename
	    return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
	}

	@Override
	public void onDataPass(String action) {
		if (action == "gallery") {
			onLaunchGallery();
		} else if (action == "camera") {
			onLaunchCamera();
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
	    		
		        if (tag.equals("onward")) {
		        	onwardLatLng = getLatLng();
		    		//homeAdd = getCheckedAdd();
		    	} else if (tag.equals("return")) {
		    		returnLatLng = getLatLng();
		    		//workAdd = getCheckedAdd();
		    	}
		        
				if (oneAddressVerifDoneFlag==1) {
					
					oneAddressVerifDoneFlag = 0;
					State_GeoCodeTask = 2;
					onAddGroupTasks();
				} else {
			    	oneAddressVerifDoneFlag = oneAddressVerifDoneFlag + 1;
				}
	    	}
	    	
	    	@Override
	    	public void onFailure(int statusCode, Header[] headers,
	    			String responseString, Throwable throwable) {
	    		super.onFailure(statusCode, headers, responseString, throwable);
	    		
	    		//BOZO: Handle this case
	    	}
	    	
	    });
    }


}

