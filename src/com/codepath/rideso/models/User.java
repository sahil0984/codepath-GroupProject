package com.codepath.rideso.models;

import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

@ParseClassName("_User")
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
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		boolean results = false;
		if (o instanceof User)
		{
			User u = (User) o ;
			if (this.getObjectId().equals(((User) o).getObjectId()))
			{
				results = true;
			}
		
		}
		return results;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.getObjectId().hashCode();
	}
	// Use getString and others to access fields
	//public String getObjectId() {
	//	return getString("objectId");
	//}
	// Get the user for this item
	public ParseUser getUser()  {
		return getParseUser("owner");
	}
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
	public ParseGeoPoint getHomeAdd() {
		return getParseGeoPoint("homeAdd");
	}
	public ParseGeoPoint getWorkAdd() {
		return getParseGeoPoint("workAdd");
	}
	public List<Group> getGroups() {
		return getList("groups");
	}	
	public boolean getIsPublic() {
		return getBoolean("isPublic");
	}	
	
	// Use put to modify field values
	//public void setObjectId(String value) { //This is created by Parse when a new object is created. So you wont need to set this.
	//	put("objectId", value);
	//}
	// Associate each item with a user
	public void setOwner(ParseUser user) {
		put("owner", user);
	}
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
	public void setHomeAdd(ParseGeoPoint value) {
		put("homeAdd", value);
	}
	public void setWorkAdd(ParseGeoPoint value) {
		put("workAdd", value);
	}
	public void setGroups(List<Group> value) {
		put("groups", value);
	}	
	public void setIsPublic(boolean value) {
		put("isPublic", value);
	}
}
