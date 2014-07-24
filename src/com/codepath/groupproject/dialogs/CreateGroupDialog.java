package com.codepath.groupproject.dialogs;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.codepath.groupproject.AddUsersActivity;
import com.codepath.groupproject.GeoCoderResponseHandler;
import com.codepath.groupproject.GroupDetailActivity;
import com.codepath.groupproject.MyFragment;
import com.codepath.groupproject.R;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CreateGroupDialog extends MyFragment {

	private ParseImageView ivGroupPhoto;
	private FloatLabeledEditText etGroupName;
	private TextView tvOnwardTime;
	private TextView tvReturnTime;
	
	private TextView tvDate;

	private TextView tvIsRecurring;
	private boolean isRecurring;
	private LinearLayout llCheckBoxDays;
	private TextView tvMon;
	private TextView tvTue;
	private TextView tvWed;
	private TextView tvThu;
	private TextView tvFri;
	private TextView tvSat;
	private TextView tvSun;
	private boolean[] daysOfWeek;
	
	private FloatLabeledEditText etOnwardLocation;
	private FloatLabeledEditText etReturnLocation;
	
	private TextView cbIsPublic;
	private boolean isPublic;
	
	private BootstrapButton btnAddUsers;
	private BootstrapButton btnCreate;
	private BootstrapButton btnCancel;
	
	private String onwardTime;
	private String returnTime;
	private String date;

	
	public final String APP_TAG = "GroupProjectApp";
	public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
	public final static int PICK_PHOTO_CODE = 1046;
	private static final int ADD_USERS_REQUEST_CODE = 20;
	public String photoFileName = "photo.jpg";
	
	ParseFile photoFile;
	
	private ParseGeoPoint onwardLatLng;
	private ParseGeoPoint returnLatLng;
	private int oneAddressVerifDoneFlag;
	private int State_GeoCodeTask;
	//private String onwardAdd;
	//private String returnAdd;
	
	Group newGroup;
	private ArrayList<String> groupMembersStr;
	ArrayList<User> groupMembers;
	int queriesReturned;
	
	Activity fromActivity;
		
//INTERFACE LOGIC for passing data form fragment to activity	
	// Define the listener of the interface type
	// listener is the activity itself
	private OnActionSelectedListenerCreateGroup listener;
	
	// Define the events that the fragment will use to communicate
	public interface OnActionSelectedListenerCreateGroup {
			public void onActionSelectedCreateGroup(Group group, String action);
	}
	
	// Store the listener (activity) that will have events fired once the fragment is attached
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	    if (activity instanceof OnActionSelectedListenerCreateGroup) {
	    	listener = (OnActionSelectedListenerCreateGroup) activity;
	    } else {
	        throw new ClassCastException(activity.toString()
	            + " must implement MyListFragment.OnItemSelectedListenerDetails");
	    }
	    
	    if (activity instanceof GroupDetailActivity) {
	    	//((GroupDetailActivity) activity).sendGroupToPopulateCreateGroupFragment();
	    	fromActivity = activity;
	    } else {
	    	fromActivity = null;
	    }
	}
	
	// Now we can fire the event when the user selects something in the fragment
	//public void onSomeClick(View v) {
	//	listener.onActionSelected(aTweets.getItem(0), "");
	//}
//////////////	
	
	public void populateExistingGroup(Group fromGroup) {
		//Toast.makeText(getActivity(), "Yes, called from activity!", Toast.LENGTH_LONG).show();

		btnAddUsers.setText("Members (" + fromGroup.getMembers().size() + ")");
		
		
	       if (fromGroup.getPhotoFile() != null) {
	    	   ivGroupPhoto.setParseFile(fromGroup.getPhotoFile());
	    	   ivGroupPhoto.loadInBackground(new GetDataCallback() {
	
	    		   @Override
	    		   public void done(byte[] data, ParseException e) {
	                   // nothing to do
	    		   }
	    	   });
	    	   photoFile = fromGroup.getPhotoFile();
	       }// else {
	    	//   ivGroupPhoto.setImageResource(android.R.color.transparent);
	       //}
		
	    
		etGroupName.setText(fromGroup.getName());
		//tvOnwardTime.setText(fromGroup.getOnwardTime());
		//tvReturnTime.setText(fromGroup.getReturnTime());
		//tvDate.setText(fromGroup.);
		//etOnwardLocation.setText(fromGroup.getOnwardLocation());
		//etReturndLocation.setText(fromGroup.getReturnLocation());
		//cbIsPublic.setChecked(fromGroup.getIsPublic());
		isPublic = fromGroup.getIsPublic();
		updateIsPublicIcon();
		
		
		//tvIsRecurring.setChecked(fromGroup.getRecurring());
		isRecurring = fromGroup.getRecurring();
		updateIsRecurringIcon();
		
		//tvMon.setChecked(fromGroup.getDaysofWeek()[0]);
		//tvTue.setChecked(fromGroup.getDaysofWeek()[1]);
		//tvWed.setChecked(fromGroup.getDaysofWeek()[2]);
		//tvThu.setChecked(fromGroup.getDaysofWeek()[3]);
		//tvFri.setChecked(fromGroup.getDaysofWeek()[4]);
		//tvSat.setChecked(fromGroup.getDaysofWeek()[5]);
		//tvSun.setChecked(fromGroup.getDaysofWeek()[6]);
		daysOfWeek = fromGroup.getDaysofWeek();
		updateColorsOfDays();
		
		String time1 = fromGroup.getOnwardTime();
		String[] time1Arr = time1.split(" ");
		if (!time1Arr[1].equals("25:25")) {
			tvOnwardTime.setText(time1Arr[1]);
		}
		
		String time2 = fromGroup.getReturnTime();
		String[] time2Arr = time2.split(" ");		
		if (!time2Arr[1].equals("25:25")) {
			tvReturnTime.setText(time2Arr[1]);
		}
		
		if (!time1Arr[0].equals("01/01/3001")) {
			tvDate.setText(time1Arr[0]);
		}
		
		if (fromGroup.getOnwardLocation()!=null) {
		    String url1 = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + fromGroup.getOnwardLocation().getLatitude() 
		    												   				    + "," + fromGroup.getOnwardLocation().getLongitude();
		    AsyncHttpClient client1 = new AsyncHttpClient();
		    client1.get(url1, null, new GeoCoderResponseHandler(getActivity()) {
		    	
		    	@Override
		    	public void onSuccess(int statusCode, Header[] headers,
		    			JSONObject response) {
		    		super.onSuccess(statusCode, headers, response);
		    		etOnwardLocation.setText(getCheckedAdd());
		    	}
		    	
		    	@Override
		    	public void onFailure(int statusCode, Header[] headers,
		    			String responseString, Throwable throwable) {
		    		super.onFailure(statusCode, headers, responseString, throwable);
		    		//BOZO: Handle this case
		    	}
		    	
		    });
		}

		if (fromGroup.getReturnLocation()!=null) {
			String url2 = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + fromGroup.getReturnLocation().getLatitude() 
					   															+ "," + fromGroup.getReturnLocation().getLongitude();
		    AsyncHttpClient client2 = new AsyncHttpClient();
		    client2.get(url2, null, new GeoCoderResponseHandler(getActivity()) {
		    	
		    	@Override
		    	public void onSuccess(int statusCode, Header[] headers,
		    			JSONObject response) {
		    		super.onSuccess(statusCode, headers, response);
		    		etReturnLocation.setText(getCheckedAdd());
		    	}
		    	
		    	@Override
		    	public void onFailure(int statusCode, Header[] headers,
		    			String responseString, Throwable throwable) {
		    		super.onFailure(statusCode, headers, responseString, throwable);
		    		//BOZO: Handle this case
		    	}
		    	
		    });
		}

		groupMembersStr = new ArrayList<String>();
		//add group members as string array of objectId and store in groupMembersStr
		for (int i=0; i<fromGroup.getMembers().size(); i++) {
			groupMembersStr.add(fromGroup.getMembers().get(i).getObjectId());
		}
		groupMembers = new ArrayList<User>();
		groupMembers = (ArrayList<User>) fromGroup.getMembers();
		
		newGroup = new Group();
		newGroup.setObjectId(fromGroup.getObjectId());
	}
	
	


	@Override
	protected void onAnimationEnded() {
		super.onAnimationStarted();
		listener.onActionSelectedCreateGroup(null, "animationEnded");
	}
	
	public CreateGroupDialog () {
		//Empty constructor required for Dialog Fragment
	}
	
	public static CreateGroupDialog newInstance(String callFor) {
		CreateGroupDialog  frag = new CreateGroupDialog();
		Bundle args = new Bundle();
		
		args.putSerializable("callFor", callFor);
	    frag.setArguments(args);
	    return frag;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		newGroup = new Group();
		groupMembers = new ArrayList<User>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        //getDialog().setTitle("Edit Filters");
        
		View view = inflater.inflate(R.layout.activity_create_group, container, false);
		
		//String callFor = (String) getArguments().getSerializable("callFor");

		setupViews(view);
		
		if (fromActivity!=null) {
			((GroupDetailActivity) fromActivity).sendGroupToPopulateCreateGroupFragment();
			btnCreate.setText("Update");
		}
		
		return view;

	}

	private void setupViews(View v) {
		ivGroupPhoto = (ParseImageView) v.findViewById(R.id.ivGroupPhoto);
		etGroupName = (FloatLabeledEditText) v.findViewById(R.id.etGroupName);
		tvOnwardTime = (TextView) v.findViewById(R.id.tvOnwardTime);
		tvReturnTime = (TextView) v.findViewById(R.id.tvReturnTime);
		
		tvDate = (TextView) v.findViewById(R.id.tvDate);

		tvIsRecurring = (TextView) v.findViewById(R.id.tvIsRecurring);
		
		llCheckBoxDays = (LinearLayout) v.findViewById(R.id.llCheckBoxDays);
		tvMon = (TextView) v.findViewById(R.id.tvMon);
		tvTue = (TextView) v.findViewById(R.id.tvTue);
		tvWed = (TextView) v.findViewById(R.id.tvWed);
		tvThu = (TextView) v.findViewById(R.id.tvThu);
		tvFri = (TextView) v.findViewById(R.id.tvFri);
		tvSat = (TextView) v.findViewById(R.id.tvSat);
		tvSun = (TextView) v.findViewById(R.id.tvSun);
		daysOfWeek = new boolean[7];
		Arrays.fill(daysOfWeek, Boolean.FALSE);
		
		
		etOnwardLocation = (FloatLabeledEditText) v.findViewById(R.id.etOnwardLocation);
		etReturnLocation = (FloatLabeledEditText) v.findViewById(R.id.etReturnLocation);
		
		cbIsPublic = (TextView) v.findViewById(R.id.tvIsPublic);
		
		btnAddUsers = (BootstrapButton) v.findViewById(R.id.btnAddUsers);
		btnCreate = (BootstrapButton) v.findViewById(R.id.btnCreate);
		btnCancel = (BootstrapButton) v.findViewById(R.id.btnCancel);
						
		
        Typeface fontAwesome = Typeface.createFromAsset(getActivity().getAssets(), "fonts/fontawesome-webfont.ttf");
        tvIsRecurring.setTypeface(fontAwesome);
        cbIsPublic.setTypeface(fontAwesome);
		//cbIsPublic.setChecked(true);
		//cbIsPublic.setText("Public Group");
		isPublic = false;
		updateIsPublicIcon();
		
		//tvIsRecurring.setChecked(false);
		isRecurring = false;
		updateIsRecurringIcon();
		
		llCheckBoxDays.setVisibility(View.INVISIBLE);
		tvDate.setVisibility(View.VISIBLE);	
		
		//Listeners
		ivGroupPhoto.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.onActionSelectedCreateGroup(null, "choosePhoto");
			}
		});
		
		tvDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onActionSelectedCreateGroup(null, "pickDate");
			}
		});
		
		tvOnwardTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onActionSelectedCreateGroup(null, "pickOnwardTime");
			}
		});
		
		tvReturnTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onActionSelectedCreateGroup(null, "pickReturnTime");
			}
		});	
		
		tvIsRecurring.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isRecurring = !isRecurring;
				updateIsRecurringIcon();
				if (isRecurring) {
					llCheckBoxDays.setVisibility(View.VISIBLE);
					tvDate.setVisibility(View.INVISIBLE);
				} else {
					llCheckBoxDays.setVisibility(View.INVISIBLE);
					tvDate.setVisibility(View.VISIBLE);
				}
			}
		});
		
		cbIsPublic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				isPublic = !isPublic;
				updateIsPublicIcon();
			}
		});
		
		btnAddUsers.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), AddUsersActivity.class);
				i.putExtra("currentGroupMembers",groupMembersStr);
				startActivityForResult(i, ADD_USERS_REQUEST_CODE);	
			}
		});
		
		
		btnCreate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				oneAddressVerifDoneFlag = 0;
				State_GeoCodeTask = 0;
				onAddGroupTasks();
			}
		});
		
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				listener.onActionSelectedCreateGroup(null, "cancel");
			}
		});
		
		
		//Listeners for days of week check clicks
		tvMon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				daysOfWeek[0] = !daysOfWeek[0];
				updateColorsOfDays();
			}
		});
		tvTue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				daysOfWeek[1] = !daysOfWeek[1];
				updateColorsOfDays();
			}
		});
		tvWed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				daysOfWeek[2] = !daysOfWeek[2];
				updateColorsOfDays();
			}
		});
		tvThu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				daysOfWeek[3] = !daysOfWeek[3];
				updateColorsOfDays();
			}
		});
		tvFri.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				daysOfWeek[4] = !daysOfWeek[4];
				updateColorsOfDays();
			}
		});
		tvSat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				daysOfWeek[5] = !daysOfWeek[5];
				updateColorsOfDays();
			}
		});
		tvSun.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				daysOfWeek[6] = !daysOfWeek[6];
				updateColorsOfDays();
			}
		});
	}
	
	
	private void updateIsPublicIcon() {
		if (isPublic) {
			cbIsPublic.setText(getResources().getString(R.string.icon_microphone) + " Public Group");
		} else {
			cbIsPublic.setText(getResources().getString(R.string.icon_headphone) + " Local Group");
		}
	}
	
	private void updateIsRecurringIcon() {
		if (isRecurring) {
			tvIsRecurring.setText(R.string.icon_repeat);
		} else {
			tvIsRecurring.setText(R.string.icon_calendar);
		}
	}

	public void updateColorsOfDays() {
		int colorTrue = R.color.theme_color4;
		int colorFalse = R.color.background_grey;
		if (daysOfWeek[0]) {tvMon.setBackgroundColor(getResources().getColor(colorTrue));}
		else			   {tvMon.setBackgroundColor(getResources().getColor(colorFalse));}
		if (daysOfWeek[1]) {tvTue.setBackgroundColor(getResources().getColor(colorTrue));}
		else			   {tvTue.setBackgroundColor(getResources().getColor(colorFalse));}
		if (daysOfWeek[2]) {tvWed.setBackgroundColor(getResources().getColor(colorTrue));}
		else			   {tvWed.setBackgroundColor(getResources().getColor(colorFalse));}
		if (daysOfWeek[3]) {tvThu.setBackgroundColor(getResources().getColor(colorTrue));}
		else			   {tvThu.setBackgroundColor(getResources().getColor(colorFalse));}
		if (daysOfWeek[4]) {tvFri.setBackgroundColor(getResources().getColor(colorTrue));}
		else			   {tvFri.setBackgroundColor(getResources().getColor(colorFalse));}
		if (daysOfWeek[5]) {tvSat.setBackgroundColor(getResources().getColor(colorTrue));}
		else			   {tvSat.setBackgroundColor(getResources().getColor(colorFalse));}
		if (daysOfWeek[6]) {tvSun.setBackgroundColor(getResources().getColor(colorTrue));}
		else			   {tvSun.setBackgroundColor(getResources().getColor(colorFalse));}
	}
	
//Code for adding a group	
	private void onAddGroupTasks() {
		switch (State_GeoCodeTask) {
		case 0:
			if (!isFormOkay()) {
				break;
			}
			getVerifySetAdd("onward", etOnwardLocation.getText().toString());
			getVerifySetAdd("return", etReturnLocation.getText().toString());
			break;
		//case 1:
			//sendPushNotification();
			//break;
		case 2:
			prepareIntent();
			if (newGroup != null) {
				listener.onActionSelectedCreateGroup(newGroup, "createGroup");					
			}
			//finish();
	        //break;
		default:
			break;
		}
	}
	
	public boolean isFormOkay() {
		if (etGroupName.getText().toString().equals("") || 
			etOnwardLocation.getText().toString().equals("") || 
			etReturnLocation.getText().toString().equals("")) {
			Toast.makeText(getActivity(), "Please enter the required fields!", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
    public void getVerifySetAdd(final String tag, String address) {
		String formattedAddress = address.trim().replaceAll(" +", "+");
		//Toast.makeText(getApplicationContext(), formattedAddress, Toast.LENGTH_LONG).show();

    	//https://developers.google.com/maps/documentation/geocoding/
	    String url = "http://maps.google.com/maps/api/geocode/json?address=" + formattedAddress;
	    
	    
	    AsyncHttpClient client = new AsyncHttpClient();
	    client.get(url, null, new GeoCoderResponseHandler(getActivity()) {

	    	@Override
	    	public void onSuccess(int statusCode, Header[] headers,
	    			JSONObject response) {
	    		super.onSuccess(statusCode, headers, response);
	    		
		        if (tag.equals("onward")) {
		        	onwardLatLng = getLatLng();
		    		//homeAdd = getCheckedAdd();
		    	} else if (tag.equals("return")) {
		    		returnLatLng = getLatLng();
		    		//workAdd = getCheckedAdd();
		    	}
		        
				if (oneAddressVerifDoneFlag==1) {
					
					oneAddressVerifDoneFlag = 0;
					State_GeoCodeTask = 2;
					onAddGroupTasks();
				} else {
			    	oneAddressVerifDoneFlag = oneAddressVerifDoneFlag + 1;
				}
	    	}
	    	
	    	@Override
	    	public void onFailure(int statusCode, Header[] headers,
	    			String responseString, Throwable throwable) {
	    		super.onFailure(statusCode, headers, responseString, throwable);
	    		
	    		//BOZO: Handle this case
	    	}
	    	
	    });
    }
    
	public void prepareIntent() {
		String tmpDate;
		if (!isRecurring && !tvDate.getText().toString().equals("")) {
			tmpDate = tvDate.getText().toString();
		} else {
			tmpDate = "01/01/3001";
		}
		
		String tmpOnwardTime;
		if (tvOnwardTime.getText().toString().equals("")) {
			tmpOnwardTime = "25:25";
		} else {
			tmpOnwardTime = tvOnwardTime.getText().toString();
		}
		
		String tmpReturnTime;
		if (tvReturnTime.getText().toString().equals("")) {
			tmpReturnTime = "25:25";
		} else {
			tmpReturnTime = tvReturnTime.getText().toString();
		}		
		
		newGroup.setName(etGroupName.getText().toString());
		newGroup.setOnwardTime(tmpDate+" "+tmpOnwardTime);
		newGroup.setReturnTime(tmpDate+" "+tmpReturnTime);
		newGroup.setRecurring(isRecurring);
		newGroup.setDaysOfWeek(this.daysOfWeek);
		newGroup.setOnwardLocation(onwardLatLng);
		newGroup.setReturnLocation(returnLatLng);
		newGroup.setMembers(groupMembers);
		newGroup.setIsPublic(isPublic);
		newGroup.setOwner(ParseUser.getCurrentUser());

		if (photoFile != null) {
			newGroup.setPhotoFile(photoFile);	    	 
		}
				
	}
	
//	private boolean[] daysOfWeek() {
//		boolean[] daysOfWeekArray = new boolean[7];
//		//daysOfWeekArray[0] = tvMon.isChecked();
//		//daysOfWeekArray[1] = tvTue.isChecked();
//		//daysOfWeekArray[2] = tvWed.isChecked();
//		//daysOfWeekArray[3] = tvThu.isChecked();
//		//daysOfWeekArray[4] = tvFri.isChecked();
//		//daysOfWeekArray[5] = tvSat.isChecked();
//		//daysOfWeekArray[6] = tvSun.isChecked();
//		daysOfWeekArray = this.daysOfWeek;
//		return daysOfWeekArray;
//	}

//Code for updating date and time returned from home activity
	public void populateSetDate(int year, int month, int day) {
		DecimalFormat formatter = new DecimalFormat("00");
		tvDate.setText(formatter.format(month) + "/" + formatter.format(day) + "/" + year);
	}
	public void populateOnwardSetTime(int hour, int minute) {
		DecimalFormat formatter = new DecimalFormat("00");
		tvOnwardTime.setText(formatter.format(hour) + ":" + formatter.format(minute));
	}
	public void populateReturnSetTime(int hour, int minute) {
		DecimalFormat formatter = new DecimalFormat("00");
		tvReturnTime.setText(formatter.format(hour) + ":" + formatter.format(minute));
	}
	
//Code for managing group photo uri returned from the home activity
	public void manageGroupPhtotoUri(Uri photoUri) {
		// by this point we have the camera photo on disk
		// Load the taken image into a preview
		Picasso.with(getActivity()).load(photoUri).resize(80, 80).into(ivGroupPhoto);
		getBitmap(photoUri);
	}

	private void getBitmap(Uri photoUri) {
		Picasso.with(getActivity()).load(photoUri).resize(80, 80).into(target);
	}

	// Creating a target class to get the resized bitmap data from Picasso
	private Target target = new Target() {
		@Override
		public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			if (byteArray != null) {
				photoFile = new ParseFile("group_photo.jpg", byteArray);
			}
		}

		@Override
		public void onBitmapFailed(Drawable arg0) {
		}

		@Override
		public void onPrepareLoad(Drawable arg0) {
		}
	};
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == getActivity().RESULT_OK) {
			if (requestCode == ADD_USERS_REQUEST_CODE) {
				
				groupMembersStr = data.getStringArrayListExtra("groupMembers");
				if (!groupMembersStr.contains(ParseUser.getCurrentUser().getObjectId()))
				{
					groupMembersStr.add(ParseUser.getCurrentUser().getObjectId());
				}
						
				btnAddUsers.setText("Members (" + groupMembersStr.size() + ")");

			     try {
			    	 //BOZO: Start progress bar
			    	 createUserListfromObjectId(groupMembersStr);
			     } catch (Exception e) {
			    	 e.printStackTrace();
			     }
			}
		}
	}
	
	public void createUserListfromObjectId(ArrayList<String> userListStr) {
		int i;
		// ArrayList<User> userList = new ArrayList<User>();
		final int userListSize = userListStr.size();
		queriesReturned = 0;
		for (i = 0; i < userListSize; i++) {
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
						// Adding member to the group adapter
						groupMembers.add(user);
						Log.d("MyApp", user.toString());

						if (queriesReturned == userListSize - 1) {
							//BOZO: hide progress bar
						} else {
							queriesReturned = queriesReturned + 1;
						}

					} else {
						Log.d("MyApp", "Trouble finding users.");
						//BOZO: hide progress bar
					}
				}
			});
		}

	}
}
