package com.codepath.groupproject;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.codepath.groupproject.adapters.ChatArrayAdapter;
import com.codepath.groupproject.adapters.GroupArrayAdapter;
import com.codepath.groupproject.models.Chat;
import com.codepath.groupproject.models.Group;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ChatActivity extends Activity {

	Group currentGroup;
	
	Chat newChat;
	
	private ArrayList<Chat> chats;
	private ArrayAdapter<Chat> aChats;
	private ListView lvChat;
	
	private EditText etNewMsg;
	private Button btnSendMsg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		
		chats = new ArrayList<Chat>();
		aChats = new ChatArrayAdapter(this, chats);
		
		lvChat = (ListView) findViewById(R.id.lvChat);
		lvChat.setAdapter(aChats);
		
		etNewMsg = (EditText) findViewById(R.id.etNewMsg);
		btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
		
		btnSendMsg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
				Calendar cal = Calendar.getInstance();
				
				newChat = new Chat();
				newChat.setGroupObjectId(currentGroup.getObjectId());
				newChat.setSenderObjectId(ParseUser.getCurrentUser().getObjectId());
				newChat.setMessage(etNewMsg.getText().toString());
				newChat.setSender(ParseUser.getCurrentUser().get("firstName") + " " + ParseUser.getCurrentUser().get("lastName"));
				newChat.setTimeStamp(dateFormat.format(cal.getTime()).toString());
				
				//Toast.makeText(getApplicationContext(), dateFormat.format(cal.getTime()).toString(), Toast.LENGTH_SHORT).show();
				
				newChat.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException arg0) {
						etNewMsg.setText("");
						
						// send parse push on this channel
						JSONObject obj;
						try {
							// Adding new chat message to listview
							aChats.add(newChat);						
							
							
							// Sending Parse Push
							DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
							Calendar cal = Calendar.getInstance();
							
							obj = new JSONObject();
							obj.put("alert", "New message from " + ParseUser.getCurrentUser().get("firstName"));
							obj.put("action", MyCustomReceiver.intentAction);
							obj.put("customdata", "ChatToGroup");
							obj.put("groupObjectId", currentGroup.getObjectId());
							obj.put("senderObjectId", ParseUser.getCurrentUser().getObjectId());
							obj.put("chatMessage", etNewMsg.getText().toString());
							obj.put("sender", ParseUser.getCurrentUser().get("firstName") + " " + ParseUser.getCurrentUser().get("lastName"));
							obj.put("sentAt", dateFormat.format(cal.getTime()).toString());
							
						
							ParsePush push = new ParsePush();
							push.setChannel("channel_" + currentGroup.getObjectId());
							//push.setMessage(newGroup.getName() + "has been updated");
							push.setData(obj);
							push.sendInBackground();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
						// Now, handle the onRecieve and open the ChatActivity..
						
					}
				});
				
			}
		});
		
		

//		if (getIntent().getStringExtra("customdata").equals("ChatToGroup")) {
//			String groupObjectId;
//			groupObjectId = getIntent().getStringExtra("groupObjectId");
//			//chatMessage = getIntent().getStringExtra("chatMessage");
//			//sender = getIntent().getStringExtra("sender");
//			//sentAt = getIntent().getStringExtra("sentAt");
//			populateExistingChat(null, groupObjectId);
//		} else {
//			//groupObjectId = getIntent().getStringExtra("group");
//			//populateExistingChat(null);
//		}
		
		String groupObjectId;
		groupObjectId = getIntent().getStringExtra("groupObjectId");
		
		ParseQuery<Group> queryGroup = ParseQuery.getQuery(Group.class);
		queryGroup.getInBackground(groupObjectId,new GetCallback<Group>() {

			@Override
			public void done(Group group, ParseException e) {
			    if (e == null) {
	        		// Access the array of results here
		    		Log.d("MyApp","Loading: " + group.getName());	 
		    		currentGroup = group;
		    		setTitle(group.getName());
		    		
					populateExistingChat(currentGroup.getObjectId());
		            
			    } else {
			        Log.d("MyApp", "Cannot update the chat at this time.");
			    }
			}
		});
		
	}
	
	public void populateExistingChat(String groupObjectId) {
		ParseQuery<Chat> queryGroupChats = ParseQuery.getQuery(Chat.class);
		queryGroupChats.whereEqualTo("groupObjectId", groupObjectId);
		queryGroupChats.findInBackground(new FindCallback<Chat>() {
			@Override
			public void done(List<Chat> chatList, ParseException e) {
		        if (e == null) {
		        	if (chatList.size()!=0) {
		        		aChats.clear();
		        		aChats.addAll(chatList);
		        		lvChat.setSelection(aChats.getCount() - 1);
		        		
		        	} else {
		        		Toast.makeText(getApplicationContext(), "Empty Chat.", Toast.LENGTH_SHORT).show();
		        	}
		        } else {
		        	Log.d("item", "Error: " + e.getMessage());
		        }					
			}
			
		});
	}
	
	
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		 // Inflate the menu; this adds items to the action bar if it is present.
		 getMenuInflater().inflate(R.menu.chat_activity_actions, menu);
		 
		 return true;
	 }
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miGroupDetails:
            	gotoGroupDetails();
                break;
            default:
            	break;
        }
        return super.onOptionsItemSelected(item);
    }

	private void gotoGroupDetails() {
        Intent i = new Intent(this,GroupDetailActivity.class);
        i.putExtra("group", currentGroup.getObjectId());
        //Use the Request Code to send the index of the list (pos)
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
    @Override
    public void onBackPressed() {
	finish();
	overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
	
	
	//Local broadcast receivers	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

	    @Override
	    public void onReceive(Context context, Intent intent) {        	
	        	Toast.makeText(getApplicationContext(), "onReceive invoked!", Toast.LENGTH_LONG).show();
	        	populateExistingChat(currentGroup.getObjectId());
	    }
    };
	@Override
    public void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }   
	@Override
    public void onResume() {
        super.onResume();
        
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(MyCustomReceiver.intentActionChat));
    }
}
