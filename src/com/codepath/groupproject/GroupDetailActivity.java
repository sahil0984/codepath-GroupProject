package com.codepath.groupproject;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.codepath.groupproject.fragments.GroupMemberListFragment;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
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
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_detail);
		
		
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
		    		addMarkers();
		            
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
	public void addMarkers() {
		// TODO Auto-generated method stub
		int i;
		ArrayList<User> groupMembers = (ArrayList<User>) currentGroup.getMembers();
		ArrayList<Marker> markers = new ArrayList<Marker>();
		
		for (i = 0; i < groupMembers.size(); i++)
		{
			User user = groupMembers.get(i);
			ParseGeoPoint geoPoint = user.getParseGeoPoint("homeAdd");

			Marker m = map.addMarker(new MarkerOptions()
							.position(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
							.title(user.getFirstName()));	
			markers.add(m);
			
		}
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		for (Marker marker : markers) {
		    builder.include(marker.getPosition());
		}
		LatLngBounds bounds = builder.build();
		
		int padding = 0; // offset from edges of the map in pixels
		CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
		map.animateCamera(cu);

		
		//Add Settings to Query
		AsyncHttpClient client = new AsyncHttpClient();
		String directionsUrl = makeURL(markers.get(0).getPosition().latitude, 
				markers.get(0).getPosition().longitude,
				markers.get(1).getPosition().latitude,
				markers.get(1).getPosition().longitude);
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
	 public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
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
	        urlString.append("&sensor=false&mode=driving&alternatives=true");
	        return urlString.toString();
	 }

}
