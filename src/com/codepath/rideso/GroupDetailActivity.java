package com.codepath.rideso;




import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardView;


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
import android.content.IntentSender;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;

import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.rideso.dialogs.ChoosePhotoDialog;
import com.codepath.rideso.dialogs.CreateGroupDialog;
import com.codepath.rideso.dialogs.ChoosePhotoDialog.OnDataPass;
import com.codepath.rideso.dialogs.CreateGroupDialog.OnActionSelectedListenerCreateGroup;
import com.codepath.rideso.fragments.GroupMemberListFragment;
import com.codepath.rideso.fragments.MyGroupListFragment;
import com.codepath.rideso.models.Group;
import com.codepath.rideso.models.User;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
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
																	 TimePickerDialog.OnTimeSetListener,
																	 GooglePlayServicesClient.ConnectionCallbacks,
																     GooglePlayServicesClient.OnConnectionFailedListener, LocationListener{

	private LocationClient mLocationClient;
	private SupportMapFragment mapFragment;
	private GoogleMap map;
	private Group currentGroup;
	private TextView tvOnwardLocation;
	private TextView tvReturnLocation;
	private CardView cardView;
	private boolean mLiveLocation;
	private Switch liveSwitch;
	private CardView mapCardView;

	
	
    private final static int
    CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 5;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    
    // Define an object that holds accuracy and frequency parameters
    LocationRequest mLocationRequest;
    
	ArrayList<Marker> markers ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_detail);
        //Setting the Title text typeface - Use same format for all activities
		
		//By Default LiveMode is off.
		setupLocationSystem();


		setActionBarTitleView();

		
		String groupObjectId = getIntent().getStringExtra("group");
        boolean isFullMapView = getIntent().getBooleanExtra("fullMap", false);
		       
        //Create mapCard
        Card mapCard = new Card(this,R.layout.carddemo_example_inner_content);
        mapCard.setTitle("Map");
        
        mapCardView = (CardView)findViewById(R.id.carddemo2);
        cardView = (CardView) findViewById(R.id.carddemo);
        

        
        if (isFullMapView)
        {	
	        LayoutParams params = cardView.getLayoutParams();
	        params.height = 0;
	        cardView.setLayoutParams(params); 
	        cardView.setPadding(0, 0, 0, 0);
	        cardView.setVisibility(View.INVISIBLE);
	        
	        params = mapCardView.getLayoutParams();
	        params.height = LayoutParams.MATCH_PARENT;
	        cardView.setPadding(0,0, 0,0);
	        mapCardView.setLayoutParams(params);
        }

  
        mapCardView.setCard(mapCard);
        
		setupSwitch();
		
		if (liveSwitch.isChecked())
			mLiveLocation = true;
		
        View transparentImageView = (View) findViewById(R.id.View1);
        final ScrollView mainScrollView = (ScrollView) findViewById(R.id.main_scrollview);
        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                   case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                   case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mainScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                   case MotionEvent.ACTION_MOVE:
                        mainScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                   default: 
                        return true;
                }   
			}
        });
		tvOnwardLocation = (TextView) findViewById(R.id.tvReturn);
		tvReturnLocation = (TextView) findViewById(R.id.tvOnward);
		
		mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		
		if (isFullMapView)
		{
	        
	        LayoutParams params2 = mapFragment.getView().getLayoutParams();
	        params2.height = LayoutParams.MATCH_PARENT;
	        mapFragment.getView().setLayoutParams(params2);        

		}
		
		if (mapFragment != null) {
			map = mapFragment.getMap();
			if (map != null) {
				//Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
				map.setMyLocationEnabled(true);
			} else {
				Toast.makeText(this, "Error loading maps!!", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "Error loading maps!!", Toast.LENGTH_SHORT).show();
		}

		//resetMap();

		
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
		    		

		            GroupDetailCard card = new GroupDetailCard(getApplicationContext(),currentGroup);
		            card.setId("myId");

		            //Set card in the cardView
		            
		            cardView.setCard(card);
		    		
		    		
		    		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		    		GroupMemberListFragment gmlF = GroupMemberListFragment.newInstance(currentGroup.getObjectId());
		            ft.replace(R.id.flUserList, gmlF);
		            ft.commit();
		    	
		    		setTitle(group.getName());
		    		//String uri = "geo:0,0?q="+ ParseUser.getCurrentUser().getParseGeoPoint("homeAdd").getLatitude() + "," + ParseUser.getCurrentUser().getParseGeoPoint("homeAdd").getLongitude() + " (" + "Sahil" + ")";
		    		//startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
		    		if (mLiveLocation == false)
		    		{
		    			addMarkers();
		    			addRoute();
		    		}
		    		
		    		//Added by Sahil: Dont show edit option if the user is not the owner of the group
		    		if (currentGroup.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
		    			showMenuOption(R.id.miEdit);
		    		}
		    		
		    		boolean isMember = false;
		    		for (int i=0; i<currentGroup.getMembers().size(); i++) {
		    			if (currentGroup.getMembers().get(i).getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
		    				showMenuOption(R.id.miChat);
		    				isMember = true;
		    				break;
		    			}
		    		}
		    		if (!isMember) {
		    			showMenuOption(R.id.miRequest);
		    		}
		            
		    } else {
		        Log.d("MyApp", "oops");
		    }
		  }
		}); 
		
	
	}
	private void setupSwitch() {
		liveSwitch = (Switch) findViewById(R.id.swLive);
		liveSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("MyApp",Boolean.toString(isChecked));
				resetMap();
				if (!isChecked)
				{

					mLocationClient.removeLocationUpdates(GroupDetailActivity.this);
					mLiveLocation = false;
	    			addMarkers();
	    			addRoute();
					
				}
				else
				{
					if (mLocationClient.isConnected())
					{
						addLiveMarkers();
						mLocationClient.requestLocationUpdates(mLocationRequest, GroupDetailActivity.this);
						mLiveLocation = true;
					}
				}
				
			}
		});
        
		
	}
	protected void addLiveMarkers() {
		markers.clear();
		ArrayList<User> groupMembers = (ArrayList<User>) currentGroup.getMembers();
		int i;
		for (i = 0; i < groupMembers.size(); i++)
		{
			User user = groupMembers.get(i);
			ParseGeoPoint geoPoint = user.getParseGeoPoint("currentPosition");
			if (geoPoint == null)
				geoPoint = user.getParseGeoPoint("homeAdd");
			else
				Log.d("MyApp", "Current pos: " + Double.toString(geoPoint.getLatitude()) + "," + Double.toString(geoPoint.getLongitude()));
			
	
			Marker m = map.addMarker(new MarkerOptions()
							.position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
							.title(user.getFirstName()));
							//.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bmap, 40, 40, false))));	
			markers.add(m);
			new MarkerImageDownloadTask(markers.size()-1).execute((String)user.get("fbId"));		
		}
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Marker marker : markers) {
		    builder.include(marker.getPosition());
		}
		LatLngBounds bounds = builder.build();
		int padding = 100; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
		map.animateCamera(cu);
		
	}
	protected void resetMap() {
		map.clear();
		
	}
	private void setActionBarTitleView() {
        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        Typeface robotoBoldCondensedItalic = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        if(actionBarTitleView != null){
            actionBarTitleView.setTypeface(robotoBoldCondensedItalic);
        }
		
	}
	private void setupLocationSystem() {
		// TODO Auto-generated method stub
		mLocationClient = new LocationClient(this, this,this);
		
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
	}
	@Override
	protected void onStart() {
	    super.onStart();
	    // Connect the client.
	    mLocationClient.connect();
	}
	
	@Override
    protected void onStop() {
        // If the client is connected
        if (mLocationClient.isConnected()) {
            /*
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             * the argument is "this".
             */
            mLocationClient.removeLocationUpdates(this);
        }
        /*
         * After disconnect() is called, the client is
         * considered "dead".
         */
        mLocationClient.disconnect();
        super.onStop();
    }
	
	@Override
	public void onConnected(Bundle dataBundle) {
	    Location mCurrentLocation = mLocationClient.getLastLocation();
	    if (mCurrentLocation != null)
	    {
	    	//Toast.makeText(this, "current location: " + mCurrentLocation.toString(), Toast.LENGTH_SHORT).show();
	    	LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
	    }
	}
	@Override
	public void onDisconnected() {
	    // Display the connection status
	    Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}
	

	public void addMarkers() {
		// TODO Auto-generated method stub
		int i;
		ArrayList<User> groupMembers = (ArrayList<User>) currentGroup.getMembers();
		markers = new ArrayList<Marker>();
	
		ParseGeoPoint startPoint = currentGroup.getOnwardLocation();
		ParseGeoPoint endPoint = currentGroup.getReturnLocation();
		
		//getOnwardAddFromCoord(startPoint);
	//	getReturnAddFromCoord(endPoint);
	
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
		
		int padding = 100; // offset from edges of the map in pixels
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
        
        hideMenuOption(R.id.miChat);
        hideMenuOption(R.id.miEdit);
        hideMenuOption(R.id.miRequest);

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
            	requestAddToGroup();
                break; 
            default:
            	break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void requestAddToGroup() {
		JSONObject obj;
		try {
			obj = new JSONObject();
			obj.put("alert", "Request to join " + currentGroup.getName() + " form " + User.getCurrentUser().getString("firstName"));
			obj.put("action", MyCustomReceiver.intentActionRequestAdd);
			obj.put("customdata", "RequestToGroup");
			obj.put("groupObjectId", currentGroup.getObjectId());
			obj.put("requestorObjectId", User.getCurrentUser().getObjectId());

				ParsePush push = new ParsePush();

				ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
				query.whereEqualTo("userObjectId", currentGroup.getUser().getObjectId());
				
				push.setQuery(query);
				push.setData(obj);
				push.sendInBackground();

			// Push the notification to Android users
			//query.whereEqualTo("deviceType", "android");
			//push.setQuery(query);
			//push.setData(obj);
			//push.sendInBackground();
			
			
			//addChannelToInstallation(newGroup.getObjectId());
		} catch (JSONException e) {

			e.printStackTrace();
		}
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
    	
    	//getActionBar().hide();
    	//fadeInBlur();
    	View v = findViewById(R.id.rlGroupDetailActivity);
    	
    	captureMapScreen(v);
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
    
    public void captureMapScreen(final View mView) {
        SnapshotReadyCallback callback = new SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                try {
                    Bitmap backBitmap = Utils.getBitmapFromView(mView);
                    Bitmap bmOverlay = Bitmap.createBitmap(
                            backBitmap.getWidth(), backBitmap.getHeight(),
                            backBitmap.getConfig());
                    Canvas canvas = new Canvas(bmOverlay);
  
                    Rect r = new Rect();
                    Point c =  new Point();
                    mapFragment.getView().getGlobalVisibleRect(r,c);
                    
                    Resources res = getResources();
                    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 73, res.getDisplayMetrics());
                    
                    canvas.drawBitmap(snapshot,c.x,c.y - px,null) ;
                    canvas.drawBitmap(backBitmap, 0, 0, null);
                  

                    fadeInBlur(bmOverlay);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        map.snapshot(callback);

    }
	private void fadeInBlur(Bitmap bm) {
		
		Bitmap blurbm = Utils.fastblur(bm,20);
		//Bitmap blurbm = bm;

		final ImageView imgBlur = (ImageView) findViewById(R.id.imgBlur);
		//FrameLayout flBlur = (FrameLayout) findViewById(R.id.flBlur);
		
	
		imgBlur.setImageBitmap(blurbm);
		Animation fadeIn = new AlphaAnimation(0,1);
		fadeIn.setInterpolator(new DecelerateInterpolator());
		fadeIn.setDuration(1000);
		
		imgBlur.setAnimation(fadeIn);
		
		fadeIn.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				imgBlur.setVisibility(View.VISIBLE);
			}
			
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				
			}


			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		
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
	public final String APP_TAG = "RideSoApp";
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
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		 if (connectionResult.hasResolution()) {
	            try {
	                // Start an Activity that tries to resolve the error
	                connectionResult.startResolutionForResult(
	                        this,
	                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
	                /*
	                * Thrown if Google Play services canceled the original
	                * PendingIntent
	                 	*/
	            } catch (IntentSender.SendIntentException e) {
	                // Log the error
	                e.printStackTrace();
	            }
	        } else {
	            /*
	             * If no resolution is available, display a dialog to the
	             * user with the error.
	             */
	            Toast.makeText(this, connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
	        }
		
	}
	public void onLocationChanged(Location location) {
	    // Report to the UI that the location was updated
	    String msg = "Updated Location: " +
	        Double.toString(location.getLatitude()) + "," +
	        Double.toString(location.getLongitude());
	    ParseGeoPoint currentPos = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
	    ParseUser.getCurrentUser().put("currentPosition", currentPos);
	    ParseUser.getCurrentUser().saveInBackground();
	    
	    updateMarkerLocations();
	
	    //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		    
	}
	private void updateMarkerLocations() {
		int i;
		ArrayList<User> groupMembers = (ArrayList<User>) currentGroup.getMembers();
		
		for (i = 0; i < markers.size(); i++)
		{
			String userId = groupMembers.get(i).getObjectId();
			final Marker m = markers.get(i);
			ParseQuery<User> queryUsers = ParseQuery.getQuery(User.class);
		
			queryUsers.getInBackground(userId, new GetCallback<User>() {
			  public void done(User user, ParseException e) {
			    if (e == null) {

					ParseGeoPoint geoPoint = user.getParseGeoPoint("currentPosition");
					
					if (geoPoint != null)
						m.setPosition(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
		        		Log.d("MyApp", user.toString());
	        		
			    } else {
			        Log.d("MyApp", "oops");
			    }
			  }
			});

			
		}
		
	}
}
