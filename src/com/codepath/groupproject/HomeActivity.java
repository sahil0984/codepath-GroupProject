package com.codepath.groupproject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;



import com.codepath.groupproject.dialogs.ChoosePhotoDialog;
import com.codepath.groupproject.dialogs.SavingsDialog;
import com.codepath.groupproject.fragments.MyGroupListFragment;
import com.codepath.groupproject.fragments.MyNetworkGroupListFragment;
import com.codepath.groupproject.fragments.PublicGroupListFragment;


import com.codepath.groupproject.listeners.SupportFragmentTabListener;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class HomeActivity extends ActionBarActivity {

	private final int REQUEST_CODE = 20;
	ArrayList<User> groupMembers;
	
	private TextView tvPageTitle;
	
	MyGroupListFragment myGroupListFragment;
	
	Group newGroup;
	int queriesReturned;
	
    private SmartFragmentStatePagerAdapter adapterViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		groupMembers = new ArrayList<User>();
		//setupTabs();
		
		//Instantiating the view pager
		ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
		adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
		vpPager.setClipToPadding(false);
		vpPager.setPageMargin(12);
		vpPager.setAdapter(adapterViewPager);
		//vpPager.setPageTransformer(true, new ZoomOutPageTransformer());
		
		tvPageTitle = (TextView) findViewById(R.id.tvPageTitle);
		tvPageTitle.setText("My Groups");
		
		vpPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			// This method will be invoked when a new page becomes selected.
			@Override
			public void onPageSelected(int position) {
				String title;
				switch (position) {
				case 0:
					title = "My Groups";
					break;
				case 1:
					title = "My Network List";
					break;
				case 2:
					title = "Public List";
					break;
				default:
					title = "";
					break;
				}
				
				tvPageTitle.setText(title);
				
				
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {				
			}
			
		});
		
		
		
		String classFrom = getIntent().getStringExtra("classFrom");
		String myCustomReceiverClass = MyCustomReceiver.class.toString();
		if (classFrom != null && classFrom.equals(myCustomReceiverClass)) {
			if (!getIntent().getStringExtra("ownersObjectId").equals(
					ParseUser.getCurrentUser().getObjectId())) {
				String objectId = getIntent().getStringExtra("customdata");

				Toast.makeText(getApplicationContext(),
						"Home called from MyCustomReceiver", Toast.LENGTH_SHORT)
						.show();

				ParseQuery<Group> queryGroup = ParseQuery.getQuery(Group.class);
				queryGroup.include("members");
				queryGroup.getInBackground(objectId, new GetCallback<Group>() {

					@Override
					public void done(Group foundGroup, ParseException e) {
						if (e == null) {
							MyGroupListFragment myGroupListFragment = (MyGroupListFragment) adapterViewPager.getRegisteredFragment(0);
							myGroupListFragment.appendNewGroup(foundGroup);
							
							// Adding Parse Push channel for the new group in current users installation
							addChannelToInstallation(foundGroup.getObjectId());

							Toast.makeText(getApplicationContext(),
									"added group: " + foundGroup.getName(),
									Toast.LENGTH_SHORT).show();
						} else {
							Log.d("item", "Error: " + e.getMessage());
						}

					}

				});

			}
		}

	}
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {        	
        	Toast.makeText(getApplicationContext(), "onReceive invoked!", Toast.LENGTH_LONG).show();
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
        
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, new IntentFilter(MyCustomReceiver.intentAction));
    }
	
	//Following helper methods need to be updated whenever we update the User model and User ParseObject
	//BOZO: Move it to utils??
	private User getUserFromParseUser(ParseUser pUser) {
		User mUser = new User();
		mUser.setFirstName(pUser.get("first_name").toString());
		mUser.setLastName(pUser.get("last_name").toString());
		//Add more stuff here
		return mUser;
	}
	private ParseUser getParseUserFromUser(User mUser) {
		ParseUser pUser = new ParseUser();
		pUser.put("first_name", mUser.getFirstName());
		pUser.put("last_name", mUser.getLastName());
		//Add more stuff here
		return pUser;
	}
	


//	private void setupTabs() {
//		ActionBar actionBar = getSupportActionBar();
//		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//		actionBar.setDisplayShowTitleEnabled(true);
//
//		Tab tab1 = actionBar
//		    .newTab()
//		    .setText("My Groups")
//		    //.setIcon(R.drawable.ic_profile)
//		    .setTag("MyGroupListFragment")
//			.setTabListener(new SupportFragmentTabListener<MyGroupListFragment>(R.id.flContainer, this,
//                        "MyGroupListFragment", MyGroupListFragment.class));
//
//		actionBar.addTab(tab1);
//		actionBar.selectTab(tab1);
//
//		Tab tab2 = actionBar
//		    .newTab()
//		    .setText("My Network Groups")
//		    //.setIcon(R.drawable.ic_profile)
//		    .setTag("MyNetworkGroupListFragment")
//			.setTabListener(new SupportFragmentTabListener<MyNetworkGroupListFragment>(R.id.flContainer, this,
//                        "MyNetworkGroupListFragment", MyNetworkGroupListFragment.class));
//		actionBar.addTab(tab2);
//		
//		Tab tab3 = actionBar
//			    .newTab()
//			    .setText("Public Groups")
//			    //.setIcon(R.drawable.ic_profile)
//			    .setTag("PublicGroups")
//				.setTabListener(new SupportFragmentTabListener<PublicGroupListFragment>(R.id.flContainer, this,
//	                        "PublicGroups", PublicGroupListFragment.class));
//		actionBar.addTab(tab3);
//	}
	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_activity_actions, menu); //BOZO: This is a placeholder. change it for this activity.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miProfile:
            	gotoProfileActivity();
                break;
            case R.id.miCreateGroup:
            	gotoCreateGroupActivity();
                break;
            case R.id.miSavings:
            	gotoSavingsDialogFragment();
                break;  
            default:
            	break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    //BOZO: Create a util for this since it is used at multiple places
	public void gotoProfileActivity() {
		Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
		startActivity(i);
	}
	
	public void gotoCreateGroupActivity() {
		Intent i = new Intent(getApplicationContext(), CreateGroupActivity.class);
		startActivityForResult(i, REQUEST_CODE);
	}
	public void gotoSavingsDialogFragment() {
	  	FragmentManager fm = getSupportFragmentManager();
	  	SavingsDialog savingsDialog = new SavingsDialog();
	  	savingsDialog.show(fm, "dialog_savings");
	}

	public void createUserListfromObjectId(ArrayList<String> userListStr)
	{
		int i;
		//ArrayList<User> userList = new ArrayList<User>();
		final int userListSize = userListStr.size();
		queriesReturned=0;
		for (i = 0; i < userListSize; i++)
		{
			ParseQuery<User> queryUsers = ParseQuery.getQuery(User.class);
			String objectId = userListStr.get(i);
			// Define our query conditions
			Log.d("geUserFromObjectId", "objectId: " + objectId);
			
			queryUsers.include("groups");
			// Execute the find asynchronously
			
			queryUsers.getInBackground(objectId, new GetCallback<User>() {
			  public void done(User user, ParseException e) {
			    if (e == null) {
		        		// Access the array of results here
			    		//Adding member to the group adapter
			    		groupMembers.add(user);
		        		Log.d("MyApp", user.toString());
		        		
		        		//ParseUser.getCurrentUser().put("groups", groupList);
		        		//ParseUser.getCurrentUser().saveInBackground();
		        		

		        		
		        		if (queriesReturned==userListSize-1) {
		        			newGroup.setMembers(groupMembers);
		        			saveGroupToParse();
		        		} else {
			        		queriesReturned = queriesReturned + 1;
		        		}

		        		
			    } else {
			        Log.d("MyApp", "oops");
			    }
			  }
			});
		}

		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  // REQUEST_CODE is defined above
	  if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
		  
	     // Extract name value from result extras
	     //final Group 
		     newGroup = new Group(data.getStringExtra("groupName"));
	     
	     newGroup.setOwner(ParseUser.getCurrentUser());
	     newGroup.setOnwardTime(data.getStringExtra("onwardTime"));
	     newGroup.setReturnTime(data.getStringExtra("returnTime"));
	     newGroup.setRecurring(data.getBooleanExtra("recurring", false));
	     newGroup.setDaysOfWeek(data.getBooleanArrayExtra("daysOfWeek"));
	     ParseGeoPoint onwardLocation = new ParseGeoPoint(data.getDoubleExtra("onwardLat", 0), 
	    		 										  data.getDoubleExtra("onwardLng", 0));
	     newGroup.setOnwardLocation(onwardLocation);
	     ParseGeoPoint returnLocation = new ParseGeoPoint(data.getDoubleExtra("returnLat", 0), 
				  										  data.getDoubleExtra("returnLng", 0));
	     newGroup.setReturnLocation(returnLocation);
	     
	     newGroup.setIsPublic(data.getBooleanExtra("isPublic", false));

	     
	     //newGroup.set	     
	     if (data.getByteArrayExtra("photoBytes") != null) {
		     ParseFile photoFile = new ParseFile("group_photo.jpg", data.getByteArrayExtra("photoBytes"));
		     newGroup.setPhotoFile(photoFile);	    	 
	     }
	     
	     
	     //Hack to add current user
	     ArrayList<String> groupMembersStr =  data.getStringArrayListExtra("groupMembers");
	     groupMembersStr.add(ParseUser.getCurrentUser().getObjectId());
	     
	     try {
	    	 createUserListfromObjectId(groupMembersStr);
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     }
	     
	     //Toast.makeText(getApplicationContext(), newGroup.getName(), Toast.LENGTH_SHORT).show();
	     
	  }
	}
	

	public void saveGroupToParse() {
	    newGroup.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e == null) {
					MyGroupListFragment myGroupListFragment = (MyGroupListFragment) adapterViewPager.getRegisteredFragment(0);
					myGroupListFragment.appendNewGroup(newGroup);
			        //Toast.makeText(getApplicationContext(), newGroup.getObjectId(), Toast.LENGTH_SHORT).show();
			        
			        sendPushNotification();
			        
				} else {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void sendPushNotification() {
		JSONObject obj;
		try {
			obj = new JSONObject();
			obj.put("alert", "Added to " + newGroup.getName() + " group!");
			obj.put("action", MyCustomReceiver.intentAction);
			obj.put("customdata", "AddedToGroup");
			obj.put("groupsObjectId", newGroup.getObjectId());
			obj.put("ownersObjectId", newGroup.getUser().getObjectId());

			for (int i=0; i<newGroup.getMembers().size(); i++) {
				ParsePush push = new ParsePush();

				ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
				query.whereEqualTo("userObjectId", newGroup.getMembers().get(i).getObjectId());
				
				push.setQuery(query);
				push.setData(obj);
				push.sendInBackground();
			}
			Toast.makeText(getApplicationContext(), "Num members: " + newGroup.getMembers(), Toast.LENGTH_SHORT).show();
			// Push the notification to Android users
			//query.whereEqualTo("deviceType", "android");
			//push.setQuery(query);
			//push.setData(obj);
			//push.sendInBackground();
			
			
			addChannelToInstallation(newGroup.getObjectId());
		} catch (JSONException e) {

			e.printStackTrace();
		}
	}
	
	public void addChannelToInstallation (String groupObjectId) {
		PushService.subscribe(getApplicationContext(), "channel_" + groupObjectId, HomeActivity.class);
		
		//Useful code for implementing messaging
        //ParsePush push = new ParsePush();
        //push.setChannel("channel_" + currentGroup.getObjectId());
        //push.setMessage("The Giants just scored! It's now 2-2 against the Mets.");
        //push.sendInBackground();
	}



	//PagerAdapter for ViewPager		
    public static class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
    	private static int NUM_ITEMS = 3;
    		
            public MyPagerAdapter(FragmentManager fragmentManager) {
                super(fragmentManager);
            }
            
    	    @Override
    		public float getPageWidth(int position) {
    	    	return 0.93f;
    		}
            
            // Returns total number of pages
            @Override
            public int getCount() {
                return NUM_ITEMS;
            }
     
            // Returns the fragment to display for that page
            @Override
            public Fragment getItem(int position) {
    	        switch (position) {
    	        case 0:
    	            return MyGroupListFragment.newInstance(0, "MyGroups");
    	        case 1:
    	            return MyNetworkGroupListFragment.newInstance(1, "MyNetworkGroups");
    	        case 2:
    	            return PublicGroupListFragment.newInstance(2, "PublicGroups");
    	        default:
    	        	return null;
                }
            }
            
            // Returns the page title for the top indicator
            @Override
            public CharSequence getPageTitle(int position) {
            	return "Page " + position;
            }
            
            
        }

}
