package com.codepath.groupproject.models;


import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Chat")
public class Chat extends ParseObject {

	// Ensure that your subclass has a public default constructor
	public Chat() {
		super();
	}
	// Add a constructor that contains core properties
	public Chat(String name) {
		super();
		setName(name);
	}
	
	// Get the user for this item
	public ParseUser getUser()  {
		return getParseUser("owner");
	}
	public String getName() {
		return getString("name");
	}
	public String getMessage() {
		return getString("message");
	}
	public String getSender() {
		return getString("sender");
	}
	public String getTimeStamp() {
		return getString("timeStamp");
	}
	public String getGroupObjectId() {
		return getString("groupObjectId");
	}
	public String getSenderObjectId() {
		return getString("senderObjectId");
	}
	
	// Use put to modify field values
	// Associate each item with a user
	public void setOwner(ParseUser user) {
		put("owner", user);
	}
	public void setName(String value) {
		put("name", value);
	}
	public void setMessage(String value) {
		put("message", value);
	}
	public void setSender(String value) {
		put("sender", value);
	}
	public void setTimeStamp(String value) {
		put("timeStamp", value);
	}
	public void setGroupObjectId(String value) {
		put("groupObjectId", value);
	}
	public void setSenderObjectId(String value) {
		put("senderObjectId", value);
	}
	
}
