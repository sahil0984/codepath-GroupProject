package com.codepath.rideso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseGeoPoint;

public class GeoCoderResponseHandler extends JsonHttpResponseHandler {

	private Context context;
	private ParseGeoPoint LatLng;
	private String checkedAdd;
	
	public GeoCoderResponseHandler (Context context) {
		this.context = context;
	}


	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

		String status = null;
		JSONArray results;
		checkedAdd = null;
		String lat = null;
		String lng = null;
		//String latLng = null;
		try {
			status = response.getString("status");
			results = response.getJSONArray("results");

			if (status.equals("OK") && results.length()>0) {
				checkedAdd = results.getJSONObject(0).getString("formatted_address");
				lat = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lat");
				lng = results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getString("lng");
				
				LatLng = new ParseGeoPoint(Double.parseDouble(lat), Double.parseDouble(lng));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(context, "Error checking address.", Toast.LENGTH_SHORT).show();
			return;
		}

	}
	
	@Override
	public void onFailure(int statusCode, Header[] headers,
			Throwable throwable, JSONObject errorResponse) {
		Toast.makeText(context, "Error checking address", Toast.LENGTH_SHORT).show();
	}
	
	public ParseGeoPoint getLatLng() {
		return LatLng;
	}
	
	public String getCheckedAdd() {
		return checkedAdd;
	}

}