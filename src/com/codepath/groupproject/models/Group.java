package com.codepath.groupproject.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Group")
public class Group extends ParseObject {
	

	// Ensure that your subclass has a public default constructor
	public Group() {
		super();
	}
	// Add a constructor that contains core properties
	public Group(String name) {
		super();
		setName(name);
	}
	

	// Use getString and others to access fields
	public String getName() {
		return getString("name");
	}
	// Get the user for this item
	public ParseUser getUser()  {
		return getParseUser("owner");
	}

	
	
	// Use put to modify field values
	public void setName(String value) {
		put("name", value);
	}
	// Associate each item with a user
	public void setOwner(ParseUser user) {
		put("owner", user);
	}
	  
}
