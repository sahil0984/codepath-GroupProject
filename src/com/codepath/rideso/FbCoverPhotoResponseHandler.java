package com.codepath.rideso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;

public class FbCoverPhotoResponseHandler  extends JsonHttpResponseHandler {

	private Context context;
	private String coverPhotoUrl;
	
	public FbCoverPhotoResponseHandler (Context context) {
		this.context = context;
	}

	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
		
		try {
			coverPhotoUrl = response.getJSONObject("cover").getString("source");
		} catch (JSONException e) {
			coverPhotoUrl = "";
			e.printStackTrace();
		}
		
		Log.d("FbCoverPhotoJson", coverPhotoUrl);
	}
	
	@Override
	public void onFailure(int statusCode, Header[] headers,
			Throwable throwable, JSONObject errorResponse) {
		Toast.makeText(context, "Cannot show cover photo. Check your connection.", Toast.LENGTH_SHORT).show();
	}
	
	public String getConverPhotoUrl() {
		return coverPhotoUrl;
	}
	
}