package com.codepath.groupproject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import com.codepath.groupproject.dialogs.ChoosePhotoDialog;
import com.codepath.groupproject.dialogs.ChoosePhotoDialog.OnDataPass;
import com.codepath.groupproject.models.Group;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateGroupActivity extends FragmentActivity implements OnDataPass {
	
	private ImageView ivGroupPhoto;
	private EditText etGroupName;
	private TextView tvOnwardTime;
	private Button btnPickOnwardTime;
	private TextView tvReturnTime;
	private Button btnPickReturnTime;
	
	private TextView tvDate;
	private Button btnPickDate;

	private CheckBox cbRecurring;
	private CheckBox cbMonday;
	private CheckBox cbTuesday;
	private CheckBox cbWednesday;
	private CheckBox cbThursday;
	private CheckBox cbFriday;
	private CheckBox cbSaturday;
	private CheckBox cbSunday;
	
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		
		setupViews();
		
	}

	private void setupViews() {
		ivGroupPhoto = (ImageView) findViewById(R.id.ivGroupPhoto);
		etGroupName = (EditText) findViewById(R.id.etGroupName);
		tvOnwardTime = (TextView) findViewById(R.id.tvOnwardTime);
		btnPickOnwardTime = (Button) findViewById(R.id.btnPickOnwardTime);
		tvReturnTime = (TextView) findViewById(R.id.tvReturnTime);
		btnPickReturnTime = (Button) findViewById(R.id.btnPickReturnTime);
		
		tvDate = (TextView) findViewById(R.id.tvDate);
		btnPickDate = (Button) findViewById(R.id.btnPickDate);

		cbRecurring = (CheckBox) findViewById(R.id.cbRecurring);
		cbMonday = (CheckBox) findViewById(R.id.cbMonday);
		cbTuesday = (CheckBox) findViewById(R.id.cbTuesday);
		cbWednesday = (CheckBox) findViewById(R.id.cbWednesday);
		cbThursday = (CheckBox) findViewById(R.id.cbThursday);
		cbFriday = (CheckBox) findViewById(R.id.cbFriday);
		cbSaturday = (CheckBox) findViewById(R.id.cbSaturday);
		cbSunday = (CheckBox) findViewById(R.id.cbSunday);
		

		ivGroupPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			  	FragmentManager fm = getSupportFragmentManager();
			  	ChoosePhotoDialog photoDialog = new ChoosePhotoDialog();
			  	photoDialog.show(fm, "dialog_choose_photo");
			}
		});
	}
	
//---- Pick Date fragment code starts here -------
	//onClick method for picking date
	public void onPickDate(View v) {
		DialogFragment newFragment = new SelectDateFragment();
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}
	//Interface method for SelectDateFragment Class
	public void populateSetDate(int year, int month, int day) {
		DecimalFormat formatter = new DecimalFormat("00");
		tvDate.setText(formatter.format(month) + "/" + formatter.format(day) + "/" + year);
	}
	//SelectDateFragment sub-class
	@SuppressLint("ValidFragment")
	public class SelectDateFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar calendar = Calendar.getInstance();
			int yy = calendar.get(Calendar.YEAR);
			int mm = calendar.get(Calendar.MONTH);
			int dd = calendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, yy, mm, dd);
		}

		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
			populateSetDate(yy, mm + 1, dd);
		}
	}
//----- End of Pick Date Fragment -----
//---- Pick Time fragment code starts here -------
	//onClick method for picking date
	public void onPickTime(View v) {
		DialogFragment newFragment = new SelectTimeFragment();
		if (v.getId() == btnPickOnwardTime.getId()) {
			newFragment.show(getSupportFragmentManager(), "OnwardTimePicker");
		} else {
			newFragment.show(getSupportFragmentManager(), "ReturnTimePicker");
		}	
	}
	
	//Interface method for SelectDateFragment Class
	public void populateSetTime(int hour, int minute, String timeType) {
		DecimalFormat formatter = new DecimalFormat("00");
		if (timeType == "OnwardTimePicker") {
			tvOnwardTime.setText(formatter.format(hour) + ":" + formatter.format(minute));
		} else if (timeType == "ReturnTimePicker") {
			tvReturnTime.setText(formatter.format(hour) + ":" + formatter.format(minute));
		}
	}

	//SelectDateFragment sub-class
	@SuppressLint("ValidFragment")
	public class SelectTimeFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar calendar = Calendar.getInstance();
			int hr = calendar.get(Calendar.HOUR);
			int min = calendar.get(Calendar.MINUTE);
			boolean is24HrView = false;
			return new TimePickerDialog(getActivity(), this, hr, min, is24HrView);
		}

		public void onTimeSet(TimePicker view, int hr, int min) {
			populateSetTime(hr, min, getTag());
		}
	}
//----- End of Pick Time Fragment -----
	
	
	public void onAddGroup(View v) {
		String tmpDate;
		if (cbRecurring.isChecked()) {
			tmpDate = tvDate.getText().toString();
		} else {
			tmpDate = "01/01/3001";
		}
		
		Intent data = new Intent();
		data.putExtra("groupName", etGroupName.getText().toString());
		data.putExtra("onwardTime", tmpDate+"'T'"+tvOnwardTime.getText().toString());
		data.putExtra("returnTime", tmpDate+"'T'"+tvReturnTime.getText().toString());
		data.putExtra("recurring", cbRecurring.isChecked());
		data.putExtra("daysOfWeek", daysOfWeek());
		data.putExtra("photoBytes", byteArray);
		data.putExtra("groupMembers",groupMembers);
		
		setResult(RESULT_OK, data);
		finish();
	}

	public void onAddUsers(View v) {
		Intent i = new Intent(getApplicationContext(), AddUsersActivity.class);
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
}
