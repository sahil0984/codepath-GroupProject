package com.codepath.rideso.models;

import java.util.List;


import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserAction")
public class UserAction extends ParseObject {
	
	// Ensure that your subclass has a public default constructor
	public UserAction() {
		super();
	}

	// Add a constructor that contains core properties
	public UserAction(String userObjectId) {
		super();
		setUserObjectId(userObjectId);
	}
	
	@Override
	public int hashCode() {
		return this.getObjectId().hashCode();
	}
	// Use getString and others to access fields
	//public String getObjectId() {
	//	return getString("objectId");
	//}
	// Get the user for this item
	public String getUserObjectId()  {
		return getString("userObjectId");
	}
	public String getDistance() {
		return getString("distance");
	}
	public String getEndTime() {
		return getString("endTime");
	}
	public String getStartTime() {
		return getString("startTime");
	}
//	public ParseGeoPoint getHomeAdd() {
//		return getParseGeoPoint("homeAdd");
//	}

	
	// Use put to modify field values
	//public void setObjectId(String value) { //This is created by Parse when a new object is created. So you wont need to set this.
	//	put("objectId", value);
	//}
	// Associate each item with a user
	public void setUserObjectId(String value) {
		put("userObjectId", value);
	}
	public void setDistance(String value) {
		put("distance", value);
	}
	public void setEndTime(String value) {
		put("endTime", value);
	}	
	public void setStartTime(String value) {
		put("startTime", value);
	}
//	public void setHomeAdd(ParseGeoPoint value) {
//		put("homeAdd", value);
//	}

}
