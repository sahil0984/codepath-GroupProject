package com.codepath.groupproject.models;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseFile;
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
//	public String getObjectId() {
//		return getString("objectId");
//	}
	public String getName() {
		return getString("name");
	}
	public String getOnwardTime() {
		return getString("onwardTime");
	}
	public String getReturnTime() {
		return getString("returnTime");
	}
	public List<User> getMembers() {
		return getList("members");
	}	
	// Get the user for this item
	public ParseUser getUser()  {
		return getParseUser("owner");
	}	
    public ParseFile getPhotoFile() {
        return getParseFile("photoFile");
    }
 
	
	// Use put to modify field values
	//public void setObjectId(String value) { //This is created by Parse when a new object is created. So you wont need to set this.
	//	put("objectId", value);
	//}
	public void setName(String value) {
		put("name", value);
	}
	public void setOnwardTime(String value) {
		put("onwardTime", value);
	}
	public void setReturnTime(String value) {
		put("returnTime", value);
	}
	public void setMembers(List<User> value) {
		put("members", value);
	}	
	// Associate each item with a user
	public void setOwner(ParseUser user) {
		put("owner", user);
	}
    public void setPhotoFile(ParseFile file) {
        put("photoFile", file);
    }
}
