package com.codepath.groupproject.models;

import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {
	
	// Ensure that your subclass has a public default constructor
	public User() {
		super();
	}

	// Add a constructor that contains core properties
	public User(String username) {
		super();
		setUsername(username);
	}
	
	// Use getString and others to access fields
	//public String getObjectId() {
	//	return getString("objectId");
	//}
	public String getUsername() {
		return getString("username");
	}
	public String getFirstName() {
		return getString("firstName");
	}
	public String getLastName() {
		return getString("lastName");
	}
	public String getPersonalEmail() {
		return getString("personalEmail");
	}
	public String getEmail() {
		return getString("email");
	}
	public String getProfileImageUrl() {
		return getString("profileImageUrl");
	}
	public String getPhone() {
		return getString("phone");
	}
	public String getFbFriendsCount() {
		return getString("fbFriendsCount");
	}
	// Get the user for this item
	public ParseUser getUser()  {
		return getParseUser("owner");
	}
	public List<Group> getMembers() {
		return getList("groups");
	}	
	
	// Use put to modify field values
	//public void setObjectId(String value) { //This is created by Parse when a new object is created. So you wont need to set this.
	//	put("objectId", value);
	//}
	public void setUsername(String value) {
		put("username", value);
	}
	public void setFirstName(String value) {
		put("firstName", value);
	}	
	public void setLastName(String value) {
		put("lastName", value);
	}
	public void setPersonalEmail(String value) {
		put("personalEmail", value);
	}
	public void setEmail(String value) {
		put("email", value);
	}
	public void setProfileImageUrl(String value) {
		put("profileImageUrl", value);
	}
	public void setPhone(String value) {
		put("phone", value);
	}
	public void setFbFriendsCount(int value) {
		put("fbFriendsCount", value);
	}
	// Associate each item with a user
	public void setOwner(ParseUser user) {
		put("owner", user);
	}
	public void setGroups(List<Group> value) {
		put("groups", value);
	}	

}
