package com.codepath.groupproject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.codepath.groupproject.dialogs.ChoosePhotoDialog;
import com.codepath.groupproject.dialogs.ChoosePhotoDialog.OnDataPass;
import com.codepath.groupproject.models.Group;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CreateGroupActivity extends FragmentActivity implements OnDataPass {
	
	private ImageView ivGroupPhoto;
	private EditText etGroupName;
	private EditText etOnwardTime;
	private EditText etReturnTime;
	
	public final String APP_TAG = "GroupProjectApp";
	public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
	public final static int PICK_PHOTO_CODE = 1046;
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
		etOnwardTime = (EditText) findViewById(R.id.etOnwardTime);
		etReturnTime = (EditText) findViewById(R.id.etReturnTime);
		
		ivGroupPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			  	FragmentManager fm = getSupportFragmentManager();
			  	ChoosePhotoDialog photoDialog = new ChoosePhotoDialog();
			  	photoDialog.show(fm, "dialog_choose_photo");
			}
		});
	}
	
	public void onAddGroup(View v) {	
		Intent data = new Intent();
		data.putExtra("groupName", etGroupName.getText().toString());
		data.putExtra("onwardTime", etOnwardTime.getText().toString());
		data.putExtra("returnTime", etReturnTime.getText().toString());
		data.putExtra("photoBytes", byteArray);
		setResult(RESULT_OK, data);
		finish();
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
			} else if (requestCode == PICK_PHOTO_CODE) {
				photoUri = data.getData();
				// Do something with the photo based on Uri
			}
			Picasso.with(this).load(photoUri).resize(80, 80).into(ivGroupPhoto);
			getBitmap(photoUri);
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
