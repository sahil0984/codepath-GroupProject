package com.codepath.groupproject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.groupproject.fragments.GroupMemberListFragment;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
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
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;

public class GroupDetailActivity extends FragmentActivity {

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

}
