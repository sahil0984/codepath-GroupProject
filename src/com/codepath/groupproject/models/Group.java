package com.codepath.groupproject.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
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
	// Get the user for this item
	public ParseUser getUser()  {
		return getParseUser("owner");
	}
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
    public ParseFile getPhotoFile() {
        return getParseFile("photoFile");
    }
    public boolean getRecurring() {
    	return getBoolean("recurring");
    }
    public boolean[] getDaysofWeek() {
    	String daysOfweek =  getString("daysOfWeek");
    	boolean[] daysOfWeekArray = new boolean[7];
    	for (int i=0; i<daysOfweek.length(); i++) {
    		daysOfWeekArray[i] = daysOfweek.charAt(i)=='1'?true:false;
    	}
    	return daysOfWeekArray;
    }
	public ParseGeoPoint getOnwardLocation() {
		return getParseGeoPoint("onwardLocation");
	}
	public ParseGeoPoint getReturnLocation() {
		return getParseGeoPoint("returnLocation");
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
    public void setPhotoFile(ParseFile file) {
        put("photoFile", file);
    }
	public void setRecurring(boolean value) {
		put("recurring", value);
	}	
	public void setDaysOfWeek(boolean[] value) {
		char[] daysOfWeek = new char[7];
    	for (int i=0; i<value.length; i++) {
    		daysOfWeek[i]= value[i]==true?'1':'0';
    	}
		put("daysOfWeek", String.valueOf(daysOfWeek));
	}
	public void setOnwardLocation(ParseGeoPoint value) {
		put("onwardLocation", value);
	}
	public void setReturnLocation(ParseGeoPoint value) {
		put("returnLocation", value);
	}
	public void setIsPublic(Boolean value) {
		put("isPublic", value);
	}
}
