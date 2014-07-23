package com.codepath.groupproject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.groupproject.dialogs.ChoosePhotoDialog;
import com.codepath.groupproject.dialogs.CreateGroupDialog;
import com.codepath.groupproject.dialogs.ChoosePhotoDialog.OnDataPass;
import com.codepath.groupproject.dialogs.CreateGroupDialog.OnActionSelectedListenerCreateGroup;
import com.codepath.groupproject.fragments.GroupMemberListFragment;
import com.codepath.groupproject.fragments.MyGroupListFragment;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

public class GroupDetailActivity extends FragmentActivity implements OnActionSelectedListenerCreateGroup,
																	 OnDataPass,
																	 OnDateSetListener,
																	 TimePickerDialog.OnTimeSetListener {

	private LocationClient mLocationClient;
	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private Group currentGroup;
	private TextView tvOnwardLocation;
	private TextView tvReturnLocation;
	
	ArrayList<Marker> markers ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_detail);
		
		tvOnwardLocation = (TextView) findViewById(R.id.tvOnwardLocation);
		tvReturnLocation = (TextView) findViewById(R.id.tvReturnLocation);
		
		mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		if (mapFragment != null) {
			map = mapFragment.getMap();
			if (map != null) {
				Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
				map.setMyLocationEnabled(true);
			} else {
				Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
		}
		String groupObjectId = getIntent().getStringExtra("group");
		
        ParseQuery<Group> queryGroup = ParseQuery.getQuery(Group.class);
        queryGroup.include("members");
		// Define our query conditions
		Log.d("MyApp", "gobjectId: " + groupObjectId);
		
		// Execute the find asynchronously
		queryGroup.getInBackground(groupObjectId,new GetCallback<Group>() {
		  @Override
		  public void done(Group group, ParseException e) {
		    if (e == null) {
	        		// Access the array of results here
		    		Log.d("MyApp","Loading: " + group.getName());	 
		    		currentGroup = group;
		    		setTitle(group.getName());
		    		addMarkers();
		    		addRoute();
		    		
		    		//Added by Sahil: Dont show edit option if the user is not the owner of the group
		    		if (!currentGroup.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
		    			hideMenuOption(R.id.miEdit);
		    		}
		    		
		    		hideMenuOption(R.id.miChat);
		    		boolean isMember = false;
		    		for (int i=0; i<currentGroup.getMembers().size(); i++) {
		    			if (currentGroup.getMembers().get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
		    				showMenuOption(R.id.miChat);
		    				isMember = true;
		    				break;
		    			}
		    		}
	    			hideMenuOption(R.id.miRequest);
		    		if (isMember) {
		    			showMenuOption(R.id.miRequest);
		    		}
		            
		    } else {
		        Log.d("MyApp", "oops");
		    }
		  }
		});

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        GroupMemberListFragment gmlF = GroupMemberListFragment.newInstance(groupObjectId);
        ft.replace(R.id.flUserList, gmlF);
        ft.commit();
	
	}
	protected void getOnwardAddFromCoord(ParseGeoPoint pCoord) {
    	Double lat = pCoord.getLatitude();
    	Double lng = pCoord.getLongitude();
	    String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng;
	    AsyncHttpClient client = new AsyncHttpClient();
	    client.get(url, null, new GeoCoderResponseHandler(getApplicationContext()) {
	    	
	    	@Override
	    	public void onSuccess(int statusCode, Header[] headers,
	    			JSONObject response) {
	    		super.onSuccess(statusCode, headers, response);
		    		tvOnwardLocation.setText(getCheckedAdd());
	    	}
	    	
	    });
		return;
	}
	protected void getReturnAddFromCoord(ParseGeoPoint pCoord) {
    	Double lat = pCoord.getLatitude();
    	Double lng = pCoord.getLongitude();
	    String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng;
	    AsyncHttpClient client = new AsyncHttpClient();
	    client.get(url, null, new GeoCoderResponseHandler(getApplicationContext()) {
	    	
	    	@Override
	    	public void onSuccess(int statusCode, Header[] headers,
	    			JSONObject response) {
	    		super.onSuccess(statusCode, headers, response);
	    	
	    		tvReturnLocation.setText(getCheckedAdd());
	    			

	    	}
	    	
	    });
		return;
	}
	public void addMarkers() {
		// TODO Auto-generated method stub
		int i;
		ArrayList<User> groupMembers = (ArrayList<User>) currentGroup.getMembers();
		markers = new ArrayList<Marker>();
	
		ParseGeoPoint startPoint = currentGroup.getOnwardLocation();
		ParseGeoPoint endPoint = currentGroup.getReturnLocation();
		
		getOnwardAddFromCoord(startPoint);
		getReturnAddFromCoord(endPoint);
	
		Marker s = map.addMarker(new MarkerOptions()
									.position(new LatLng(startPoint.getLatitude(), startPoint.getLongitude()))
									.title("Start"));
		markers.add(s);
		
		Marker e = map.addMarker(new MarkerOptions()
		.position(new LatLng(endPoint.getLatitude(), endPoint.getLongitude()))
		.title("End"));
		
		for (i = 0; i < groupMembers.size(); i++)
		{
			User user = groupMembers.get(i);
			ParseGeoPoint geoPoint = user.getParseGeoPoint("homeAdd");
			
	
			Marker m = map.addMarker(new MarkerOptions()
							.position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
							.title(user.getFirstName()));
							//.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmap, 40, 40, false))));	
			markers.add(m);
			new MarkerImageDownloadTask(markers.size()-1).execute((String)user.get("fbId"));		
		}
		

		markers.add(e);
		
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Marker marker : markers) {
		    builder.include(marker.getPosition());
		}
		LatLngBounds bounds = builder.build();
		
		int padding = 0; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
		map.animateCamera(cu);
		
	}
	
	public void addRoute()
	{
		AsyncHttpClient client = new AsyncHttpClient();
		String directionsUrl = makeURL(markers);
		Log.d("MyApp", directionsUrl);
		client.get(directionsUrl,
			new JsonHttpResponseHandler(){

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						JSONObject response) {
					// TODO Auto-generated method stub
				   try {
					   Log.d("MyApp", response.toString());
					   JSONArray routeArray = response.getJSONArray("routes");
			           JSONObject routes = routeArray.getJSONObject(0);
			           JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
			           String encodedString = overviewPolylines.getString("points");
			           List<LatLng> list = decodePoly(encodedString);
	
			           for(int z = 0; z<list.size()-1;z++){
			                LatLng src= list.get(z);
			                LatLng dest= list.get(z+1);
			                Polyline line = map.addPolyline(new PolylineOptions()
			                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
			                .width(7)
			                .color(Color.BLUE).geodesic(true));
			            }					
						super.onSuccess(statusCode, headers, response);
				   }
				   catch (JSONException e)
				   {
					   
				   }
				}
					
				});	
		
	}
	private class MarkerImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
		int markerPos;
		
		public MarkerImageDownloadTask(int pos) {
			markerPos = pos;
		}
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			Bitmap bmap;
			try {
				URL linkUrl =
						new URL("https://graph.facebook.com/" + params[0] + "/picture");
			      Log.d("image",linkUrl.getPath());
		        bmap = BitmapFactory.decodeStream(linkUrl.openConnection().getInputStream());
		        Log.d("image", bmap.toString());
		        Log.d("image",linkUrl.getPath());

		    	return bmap;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d("image", e.toString());
				
				return null;
			}
		}
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			markers.get(markerPos).setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(result, 40, 40, false)));
		}

	}
	private List<LatLng> decodePoly(String encoded) {

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        LatLng p = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(p);
	    }

	    return poly;
	}
	 public String makeURL (ArrayList<Marker> markerList){
		 	int numOfMarkers = markerList.size();
		 	Marker s = markerList.get(0);
		 	Marker e = markerList.get(numOfMarkers - 1);
		 	
		 	double sourcelat = s.getPosition().latitude;
		 	double sourcelog = s.getPosition().longitude;
		 	
		 	double destlat = e.getPosition().latitude;
		 	double destlog = e.getPosition().longitude;
		 	
	        StringBuilder urlString = new StringBuilder();
	        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
	        urlString.append("?origin=");// from
	        urlString.append(Double.toString(sourcelat));
	        urlString.append(",");
	        urlString
	                .append(Double.toString( sourcelog));
	        urlString.append("&destination=");// to
	        urlString
	                .append(Double.toString( destlat));
	        urlString.append(",");
	        urlString.append(Double.toString( destlog));
	        urlString.append("&waypoints=");
	        for (int i = 1;i < numOfMarkers; i++)
	        {
	        	Marker m = markerList.get(i);
	        	LatLng pos = m.getPosition();
	        	
	        	try {
					urlString.append(URLEncoder.encode(Double.toString(pos.latitude) + "," + Double.toString(pos.longitude), "UTF-8"));
		        	
		        	if (i != numOfMarkers - 1){
		        		urlString.append(URLEncoder.encode("|","UTF-8"));
		        	}
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	

	        }	        
	        urlString.append("&sensor=false&mode=driving&alternatives=true");

	        return urlString.toString();
	 }

// --------------------------------- SAHIL's code below this ---------------------------------	 
// NEAL, I added the following code to add edit group functionality. I have to replicate a lot of code for now.
// But, if I get a chance, I will try to make most of it shared.
		Group newGroup;
		int queriesReturned;
		
		private Menu mOptionsMenu;
		
		int animationDone;

	 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.group_detail_activity_actions, menu);
        mOptionsMenu = menu;
        
        animationDone = 0;

        return true;
    }
   private void hideMenuOption(int id)
   {
       MenuItem item = mOptionsMenu.findItem(id);
       item.setVisible(false);
   }
   private void showMenuOption(int id)
   {
       MenuItem item = mOptionsMenu.findItem(id);
       item.setVisible(true);
   }
   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miEdit:
            	openEditGroupDialog();
                break;
            case R.id.miChat:
            	openChatActivity();
                break; 
            case R.id.miRequest:
            	//requestAddToGroup();
                break; 
            default:
            	break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void openRequestDialog() {
    	// Begin the transaction
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	ft.setCustomAnimations(R.anim.slide_down, R.anim.hide);
    	ft.disallowAddToBackStack();
    	// Replace the container with the new fragment
    	//ft.replace(R.id.flAddToGroup, new AddToGroupRequestFragment(), "addToGroupRequestFragmentTag");
    	// Execute the changes specified
    	ft.commit();
    }
      
    
    private void openChatActivity() {
        Intent i = new Intent(getApplicationContext(),ChatActivity.class);
        i.putExtra("customdata", "fromGroupDetailActivity");
        i.putExtra("groupObjectId", currentGroup.getObjectId());
        //Use the Request Code to send the index of the list (pos)
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
    
	public void sendGroupToPopulateCreateGroupFragment() {
        CreateGroupDialog tmp = (CreateGroupDialog) 
                getSupportFragmentManager().findFragmentById(R.id.flCreateGroup);
        tmp.populateExistingGroup(currentGroup);
    }
    
    private void openEditGroupDialog() {
    	
    	//Create the existing group object and pass it to dialog and populate in there.
    	
    	getActionBar().hide();
    	
    	FrameLayout flCreateGroup = (FrameLayout)  findViewById(R.id.flCreateGroup);
    	flCreateGroup.setVisibility(View.VISIBLE);
    	
    	// Begin the transaction
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	ft.setCustomAnimations(R.anim.slide_down, R.anim.hide);
    	ft.disallowAddToBackStack();
    	// Replace the container with the new fragment
    	ft.replace(R.id.flCreateGroup, new CreateGroupDialog(), "editGroupFragmentTag");
    	// Execute the changes specified
    	ft.commit();

	}
    
	@Override
	public void onActionSelectedCreateGroup(Group newGroup, String action) {
		if (action.equals("choosePhoto")) {
			
		  	FragmentManager fm = getSupportFragmentManager();
		  	ChoosePhotoDialog photoDialog = new ChoosePhotoDialog();
		  	photoDialog.show(fm, "dialog_choose_photo");
		  	
		} else if (action.equals("pickDate")) {
			
	       	final Calendar calendar = Calendar.getInstance();
	        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
	        
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(false);
            datePickerDialog.show(getSupportFragmentManager(), "datepicker");
            
		} else if (action.equals("pickOnwardTime")) {
			
		    final Calendar calendar = Calendar.getInstance();
	        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

	        timePickerDialog.setCloseOnSingleTapMinute(false);
		    timePickerDialog.show(getSupportFragmentManager(), "OnwardTimePicker");
		    
		} else if (action.equals("pickReturnTime")) {
			
		    final Calendar calendar = Calendar.getInstance();
	        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

	        timePickerDialog.setCloseOnSingleTapMinute(false);
		    timePickerDialog.show(getSupportFragmentManager(), "ReturnTimePicker");
		    
		} else if (action.equals("createGroup")) {
	    	this.newGroup = newGroup;
	    	saveGroupToParse();
	    	
	    	exitFragment();
		} else if (action.equals("cancel")) {
	    	exitFragment();
		} else if (action.equals("animationEnded")) {
			
			//if (getSupportFragmentManager().findFragmentByTag("createGroupFragmentTag")!=null) {
			if (animationDone==0) {
				//Toast.makeText(getApplicationContext(),
				//		"Animation half done",
				//		Toast.LENGTH_SHORT).show();
				animationDone=1;
			} else {
				//Toast.makeText(getApplicationContext(),
				//		"Animation ended",
				//		Toast.LENGTH_SHORT).show();
				getActionBar().show();
				animationDone=0;
		    	//getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("createGroupFragmentTag")).commit();
		    	FrameLayout flCreateGroup = (FrameLayout)  findViewById(R.id.flCreateGroup);
		    	flCreateGroup.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public void exitFragment() {
    	// Begin the transaction
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	ft.setCustomAnimations(R.anim.hide, R.anim.slide_up);

    	// Replace the container with the new fragment
    	//ft.remove(getSupportFragmentManager().findFragmentByTag("createGroupFragmentTag"));
    	ft.replace(R.id.flCreateGroup, new CreateGroupDialog(), "dummyTag");
    	// Execute the changes specified
    	ft.disallowAddToBackStack();
    	ft.commit();
	}
	
	
	
//Create group related code:
//------------------------------------------------------
	public void saveGroupToParse() {
	    newGroup.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e == null) {
			        //Toast.makeText(getApplicationContext(), newGroup.getObjectId(), Toast.LENGTH_SHORT).show();
			        
			        sendPushNotification();
			        
				} else {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void sendPushNotification() {
		JSONObject obj;
		try {
			obj = new JSONObject();
			obj.put("alert", "Updates for " + newGroup.getName() + " group!");
			obj.put("action", MyCustomReceiver.intentAction);
			obj.put("customdata", "UpdateToGroup");
			obj.put("groupsObjectId", newGroup.getObjectId());
			obj.put("ownersObjectId", newGroup.getUser().getObjectId());

//			for (int i=0; i<newGroup.getMembers().size(); i++) {
//				ParsePush push = new ParsePush();
//
//				ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
//				query.whereEqualTo("userObjectId", newGroup.getMembers().get(i).getObjectId());
//				
//				push.setQuery(query);
//				push.setData(obj);
//				push.sendInBackground();
//			}
//			Toast.makeText(getApplicationContext(), "Num members: " + newGroup.getMembers(), Toast.LENGTH_SHORT).show();
			
			
			ParsePush push = new ParsePush();
			push.setChannel("channel_" + newGroup.getObjectId());
			//push.setMessage(newGroup.getName() + "has been updated");
			push.setData(obj);
			push.sendInBackground();
			
		} catch (JSONException e) {

			e.printStackTrace();
		}
	}
	
//Pick date and pick time related code:
//------------------------------------------------------
	//Interface method for SelectDateFragment Class
	@Override
	public void onDateSet(
			com.fourmob.datetimepicker.date.DatePickerDialog datePickerDialog,
			int year, int month, int day) {
		
		CreateGroupDialog createGroupFragment = (CreateGroupDialog) 
                getSupportFragmentManager().findFragmentById(R.id.flCreateGroup);
		createGroupFragment.populateSetDate(year, month + 1, day);
		
	}
	//Interface method for SelectDateFragment Class
	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		CreateGroupDialog createGroupFragment = (CreateGroupDialog) 
                getSupportFragmentManager().findFragmentById(R.id.flCreateGroup);
		
        TimePickerDialog tpdOnwardTime = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag("OnwardTimePicker");
        TimePickerDialog tpdReturnTime = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag("ReturnTimePicker");
        if (tpdOnwardTime!=null) {
        	createGroupFragment.populateOnwardSetTime(hourOfDay, minute);
            //tvOnwardTime.setText(formatter.format(hour) + ":" + formatter.format(minute));
        } else if (tpdReturnTime!=null) {
        	createGroupFragment.populateReturnSetTime(hourOfDay, minute);
        	//tvReturnTime.setText(formatter.format(hour) + ":" + formatter.format(minute));
        }

	}
	
//Camera/Gallery related code:
//------------------------------------------------------
	public final String APP_TAG = "GroupProjectApp";
	public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
	public final static int PICK_PHOTO_CODE = 1046;
	private static final int ADD_USERS_REQUEST_CODE = 20;
	public String photoFileName = "photo.jpg";
	
	byte[] byteArray;
	ParseFile photoFile;
	
	@Override
	public void onDataPass(String action) {
		if (action == "gallery") {
			onLaunchGallery();
		} else if (action == "camera") {
			onLaunchCamera();
		}
			
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
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Uri photoUri = null;
			if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
				photoUri = getPhotoFileUri(photoFileName);
				// by this point we have the camera photo on disk
				// Load the taken image into a preview
				
				CreateGroupDialog createGroupFragment = (CreateGroupDialog) 
		                getSupportFragmentManager().findFragmentById(R.id.flCreateGroup);
				createGroupFragment.manageGroupPhtotoUri(photoUri);
		            
			} else if (requestCode == PICK_PHOTO_CODE) {
				photoUri = data.getData();
				// Do something with the photo based on Uri
				
				CreateGroupDialog createGroupFragment = (CreateGroupDialog) 
		                getSupportFragmentManager().findFragmentById(R.id.flCreateGroup);
				createGroupFragment.manageGroupPhtotoUri(photoUri);
			}
		}	
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
}
