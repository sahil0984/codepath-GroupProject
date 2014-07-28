package com.codepath.rideso.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("_Installation")
public class Installation extends ParseObject {

	
	// Ensure that your subclass has a public default constructor
	public Installation() {
		super();
	}
	// Add a constructor that contains core properties
	public Installation(String userObjectId) {
		super();
		setUserObjectId(userObjectId);
	}
	
	
	
	
	private void setUserObjectId(String value) {
		put("userObjectId", value);
	}
	
	
	
}
