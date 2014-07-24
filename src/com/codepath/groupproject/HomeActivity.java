package com.codepath.groupproject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;



import com.codepath.groupproject.adapters.MyPagerAdapter;
import com.codepath.groupproject.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.groupproject.dialogs.ChoosePhotoDialog;
import com.codepath.groupproject.dialogs.ChoosePhotoDialog.OnDataPass;
import com.codepath.groupproject.dialogs.CreateGroupDialog;
import com.codepath.groupproject.dialogs.CreateGroupDialog.OnActionSelectedListenerCreateGroup;
import com.codepath.groupproject.dialogs.SavingsDialog;
import com.codepath.groupproject.fragments.MyGroupListFragment;
import com.codepath.groupproject.fragments.MyNetworkGroupListFragment;
import com.codepath.groupproject.fragments.PublicGroupListFragment;
import com.codepath.groupproject.fragments.SavingsFragment;

import com.codepath.groupproject.listeners.SupportFragmentTabListener;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
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
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class HomeActivity extends ActionBarActivity implements OnActionSelectedListenerCreateGroup,
															   OnDataPass,
															   OnDateSetListener,
															   TimePickerDialog.OnTimeSetListener {

	private final int REQUEST_CODE = 20;
	ArrayList<User> groupMembers;
	
	private TextView tvPageTitleLeft;
	private TextView tvPageTitle;
	private TextView tvPageTitleRight;
		
	int animationDone;
	
	Group newGroup;
	int queriesReturned;
	
    //private SmartFragmentStatePagerAdapter adapterViewPager;
    
    private GroupPagerAdapter adapterViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		groupMembers = new ArrayList<User>();
		//setupTabs();
		
		animationDone=0;

		
		
		
		
		
		adapterViewPager = new GroupPagerAdapter(getSupportFragmentManager());

		ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
		
		vpPager.setClipToPadding(false);
		vpPager.setPageMargin(12);
		vpPager.setAdapter(adapterViewPager);
		
		TitlePageIndicator mIndicator = (TitlePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(vpPager);
        
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
		

        
        //Setting the Title text typeface - Use same format for all activities
        int actionBarTitle = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        TextView actionBarTitleView = (TextView) getWindow().findViewById(actionBarTitle);
        Typeface robotoBoldCondensedItalic = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        if(actionBarTitleView != null){
            actionBarTitleView.setTypeface(robotoBoldCondensedItalic);
        }
        
		
		String classFrom = getIntent().getStringExtra("classFrom");
		String myCustomReceiverClass = MyCustomReceiver.class.toString();
		if (classFrom != null && classFrom.equals(myCustomReceiverClass)) {
			if (getIntent().getStringExtra("customdata").equals("AddedToGroup")) {
				//if (!getIntent().getStringExtra("ownersObjectId").equals(
				//		ParseUser.getCurrentUser().getObjectId())) {
					String objectId = getIntent().getStringExtra("groupsObjectId");
	
					Toast.makeText(getApplicationContext(),
							"Home called from MyCustomReceiver", Toast.LENGTH_SHORT)
							.show();
	
					ParseQuery<Group> queryGroup = ParseQuery.getQuery(Group.class);
					queryGroup.include("members");
					queryGroup.getInBackground(objectId, new GetCallback<Group>() {
	
						@Override
						public void done(Group foundGroup, ParseException e) {
							if (e == null) {
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
	
				//}
			} else if (getIntent().getStringExtra("customdata").equals("UpdateToGroup")) {
				Toast.makeText(getApplicationContext(),
						"Group updated.",
						Toast.LENGTH_SHORT).show();
			}
		}

	}
	
	
	
	
	
	static class GroupPagerAdapter extends FragmentPagerAdapter {
    	private static int NUM_ITEMS = 3;

	    public GroupPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }
	    
	    @Override
		public float getPageWidth(int position) {
	    	return 0.93f;
		}

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

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
        	if (position==0) {
        		return "My Groups";
        	} else if (position==1) {
        		return "Network Groups";
        	} else if (position==2) {
        		return "Public List";
        	} else {
        		return "No List Found";
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
            	openCreateGroupDialog();
            	//gotoCreateGroupActivity();
                break;
            case R.id.miSavings:
            	gotoSavingsDialogFragment();
                break;  
            default:
            	break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void openCreateGroupDialog() {
    	getActionBar().hide();
    	
    	FrameLayout flCreateGroup = (FrameLayout)  findViewById(R.id.flCreateGroup);
    	flCreateGroup.setVisibility(View.VISIBLE);
    	
    	// Begin the transaction
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	ft.setCustomAnimations(R.anim.slide_down, R.anim.hide);
    	ft.disallowAddToBackStack();
    	// Replace the container with the new fragment
    	ft.replace(R.id.flCreateGroup, new CreateGroupDialog(), "createGroupFragmentTag");
    	// Execute the changes specified
    	ft.commit();
    	
	}

	public void gotoProfileActivity() {
		Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
		startActivity(i);
	}
	
//	public void gotoCreateGroupActivity() {
//		Intent i = new Intent(getApplicationContext(), CreateGroupActivity.class);
//		startActivityForResult(i, REQUEST_CODE);
//	}
	public void gotoSavingsDialogFragment() {
	  	FragmentManager fm = getSupportFragmentManager();
	  	SavingsDialog savingsDialog = new SavingsDialog();
	  	savingsDialog.show(fm, "dialog_savings");
	}



    
    

	@Override
	public void onActionSelectedCreateGroup(Group newGroup, String action) {
		if (action.equals("choosePhoto")) {
			
		  	FragmentManager fm = getSupportFragmentManager();
		  	ChoosePhotoDialog photoDialog = new ChoosePhotoDialog();
		  	photoDialog.show(fm, "dialog_choose_photo");
		  	
		} else if (action.equals("pickDate")) {
			
	       	final Calendar calendar = Calendar.getInstance();
	        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
	        
            datePickerDialog.setYearRange(1985, 2028);
            datePickerDialog.setCloseOnSingleTapDay(false);
            datePickerDialog.show(getSupportFragmentManager(), "datepicker");
            
		} else if (action.equals("pickOnwardTime")) {
			
		    final Calendar calendar = Calendar.getInstance();
	        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

	        timePickerDialog.setCloseOnSingleTapMinute(false);
		    timePickerDialog.show(getSupportFragmentManager(), "OnwardTimePicker");
		    
		} else if (action.equals("pickReturnTime")) {
			
		    final Calendar calendar = Calendar.getInstance();
	        final TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);

	        timePickerDialog.setCloseOnSingleTapMinute(false);
		    timePickerDialog.show(getSupportFragmentManager(), "ReturnTimePicker");
		    
		} else if (action.equals("createGroup")) {
	    	this.newGroup = newGroup;
	    	saveGroupToParse();
	    	
	    	exitFragment();
		} else if (action.equals("cancel")) {
	    	exitFragment();
		} else if (action.equals("animationEnded")) {
			
			//if (getSupportFragmentManager().findFragmentByTag("createGroupFragmentTag")!=null) {
			if (animationDone==0) {
				//Toast.makeText(getApplicationContext(),
				//		"Animation half done",
				//		Toast.LENGTH_SHORT).show();
				animationDone=1;
			} else {
				//Toast.makeText(getApplicationContext(),
				//		"Animation ended",
				//		Toast.LENGTH_SHORT).show();
				getActionBar().show();
				animationDone=0;
		    	//getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentByTag("createGroupFragmentTag")).commit();
		    	FrameLayout flCreateGroup = (FrameLayout)  findViewById(R.id.flCreateGroup);
		    	flCreateGroup.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public void exitFragment() {
    	// Begin the transaction
    	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    	ft.setCustomAnimations(R.anim.hide, R.anim.slide_up);

    	// Replace the container with the new fragment
    	//ft.remove(getSupportFragmentManager().findFragmentByTag("createGroupFragmentTag"));
    	ft.replace(R.id.flCreateGroup, new CreateGroupDialog(), "dummyTag");
    	// Execute the changes specified
    	ft.disallowAddToBackStack();
    	ft.commit();
	}
	
//Create group related code:
//------------------------------------------------------
	public void saveGroupToParse() {
	    newGroup.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException e) {
				if (e == null) {
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
			
			
			//addChannelToInstallation(newGroup.getObjectId());
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
	
//Pick date and pick time related code:
//------------------------------------------------------
	//Interface method for SelectDateFragment Class
	@Override
	public void onDateSet(
			com.fourmob.datetimepicker.date.DatePickerDialog datePickerDialog,
			int year, int month, int day) {
		
		CreateGroupDialog createGroupFragment = (CreateGroupDialog) 
                getSupportFragmentManager().findFragmentById(R.id.flCreateGroup);
		createGroupFragment.populateSetDate(year, month + 1, day);
		
	}
	//Interface method for SelectDateFragment Class
	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		CreateGroupDialog createGroupFragment = (CreateGroupDialog) 
                getSupportFragmentManager().findFragmentById(R.id.flCreateGroup);
		
        TimePickerDialog tpdOnwardTime = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag("OnwardTimePicker");
        TimePickerDialog tpdReturnTime = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag("ReturnTimePicker");
        if (tpdOnwardTime!=null) {
        	createGroupFragment.populateOnwardSetTime(hourOfDay, minute);
            //tvOnwardTime.setText(formatter.format(hour) + ":" + formatter.format(minute));
        } else if (tpdReturnTime!=null) {
        	createGroupFragment.populateReturnSetTime(hourOfDay, minute);
        	//tvReturnTime.setText(formatter.format(hour) + ":" + formatter.format(minute));
        }

	}
	
//Camera/Gallery related code:
//------------------------------------------------------
	public final String APP_TAG = "GroupProjectApp";
	public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
	public final static int PICK_PHOTO_CODE = 1046;
	private static final int ADD_USERS_REQUEST_CODE = 20;
	public String photoFileName = "photo.jpg";
	
	byte[] byteArray;
	ParseFile photoFile;
	
	@Override
	public void onDataPass(String action) {
		if (action == "gallery") {
			onLaunchGallery();
		} else if (action == "camera") {
			onLaunchCamera();
		}
			
	}
	
	public void onLaunchCamera() {
	    // create Intent to take a picture and return control to the calling application
	    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name
	    // Start the image capture intent to take photo
	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}
	public void onLaunchGallery() {
	    // Create intent for picking a photo from the gallery
	    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	    // Bring up gallery to select a photo
	    startActivityForResult(intent, PICK_PHOTO_CODE);
	}

	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Uri photoUri = null;
			if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
				photoUri = getPhotoFileUri(photoFileName);
				// by this point we have the camera photo on disk
				// Load the taken image into a preview
				
				CreateGroupDialog createGroupFragment = (CreateGroupDialog) 
		                getSupportFragmentManager().findFragmentById(R.id.flCreateGroup);
				createGroupFragment.manageGroupPhtotoUri(photoUri);
		            
			} else if (requestCode == PICK_PHOTO_CODE) {
				photoUri = data.getData();
				// Do something with the photo based on Uri
				
				CreateGroupDialog createGroupFragment = (CreateGroupDialog) 
		                getSupportFragmentManager().findFragmentById(R.id.flCreateGroup);
				createGroupFragment.manageGroupPhtotoUri(photoUri);
			}
		}	
	}

	// Returns the Uri for a photo stored on disk given the fileName
	public Uri getPhotoFileUri(String fileName) {
	    // Get safe storage directory for photos
	    File mediaStorageDir = new File(
	        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_TAG);

	    // Create the storage directory if it does not exist
	    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
	        Log.d(APP_TAG, "failed to create directory");
	    }

	    // Return the file target for the photo based on filename
	    return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
	}


}
