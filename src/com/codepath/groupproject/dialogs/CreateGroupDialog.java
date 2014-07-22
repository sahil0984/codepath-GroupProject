package com.codepath.groupproject.dialogs;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import com.codepath.groupproject.AddUsersActivity;
import com.codepath.groupproject.GeoCoderResponseHandler;
import com.codepath.groupproject.MyFragment;
import com.codepath.groupproject.R;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.loopj.android.http.AsyncHttpClient;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.wrapp.floatlabelededittext.FloatLabeledEditText;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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

	private ImageView ivGroupPhoto;
	private FloatLabeledEditText etGroupName;
	private TextView tvOnwardTime;
	private TextView tvReturnTime;
	
	private TextView tvDate;

	private CheckBox cbRecurring;
	private LinearLayout llCheckBoxDays;
	private CheckBox cbMonday;
	private CheckBox cbTuesday;
	private CheckBox cbWednesday;
	private CheckBox cbThursday;
	private CheckBox cbFriday;
	private CheckBox cbSaturday;
	private CheckBox cbSunday;
	
	private FloatLabeledEditText etOnwardLocation;
	private FloatLabeledEditText etReturnLocation;
	
	private CheckBox cbIsPublic;
	
	private Button btnAddUsers;
	private Button btnCreate;
	private Button btnCancel;
	
	private String onwardTime;
	private String returnTime;
	private String date;

	
	public final String APP_TAG = "GroupProjectApp";
	public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
	public final static int PICK_PHOTO_CODE = 1046;
	private static final int ADD_USERS_REQUEST_CODE = 20;
	public String photoFileName = "photo.jpg";
	
	byte[] byteArray;
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
	}
	
	// Now we can fire the event when the user selects something in the fragment
	//public void onSomeClick(View v) {
	//	listener.onActionSelected(aTweets.getItem(0), "");
	//}
//////////////	

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
		
		return view;

	}

	private void setupViews(View v) {
		ivGroupPhoto = (ImageView) v.findViewById(R.id.ivGroupPhoto);
		etGroupName = (FloatLabeledEditText) v.findViewById(R.id.etGroupName);
		tvOnwardTime = (TextView) v.findViewById(R.id.tvOnwardTime);
		tvReturnTime = (TextView) v.findViewById(R.id.tvReturnTime);
		
		tvDate = (TextView) v.findViewById(R.id.tvDate);

		cbRecurring = (CheckBox) v.findViewById(R.id.cbRecurring);
		
		llCheckBoxDays = (LinearLayout) v.findViewById(R.id.llCheckBoxDays);
		cbMonday = (CheckBox) v.findViewById(R.id.cbMonday);
		cbTuesday = (CheckBox) v.findViewById(R.id.cbTuesday);
		cbWednesday = (CheckBox) v.findViewById(R.id.cbWednesday);
		cbThursday = (CheckBox) v.findViewById(R.id.cbThursday);
		cbFriday = (CheckBox) v.findViewById(R.id.cbFriday);
		cbSaturday = (CheckBox) v.findViewById(R.id.cbSaturday);
		cbSunday = (CheckBox) v.findViewById(R.id.cbSunday);
		
		etOnwardLocation = (FloatLabeledEditText) v.findViewById(R.id.etOnwardLocation);
		etReturnLocation = (FloatLabeledEditText) v.findViewById(R.id.etReturnLocation);
		
		cbIsPublic = (CheckBox) v.findViewById(R.id.cbIsPublic);
		
		btnAddUsers = (Button) v.findViewById(R.id.btnAddUsers);
		btnCreate = (Button) v.findViewById(R.id.btnCreate);
		btnCancel = (Button) v.findViewById(R.id.btnCancel);
		
		cbIsPublic.setChecked(true);
		cbIsPublic.setText("Public Group");
		
		cbRecurring.setChecked(false);
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
		
		cbRecurring.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {			
				if (isChecked) {
					llCheckBoxDays.setVisibility(View.VISIBLE);
					tvDate.setVisibility(View.INVISIBLE);
				} else {
					llCheckBoxDays.setVisibility(View.INVISIBLE);
					tvDate.setVisibility(View.VISIBLE);
				}
			}
		});
		
		cbIsPublic.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					buttonView.setText("Public Group");
				} else {
					buttonView.setText("Local Group");
				}
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
		if (cbRecurring.isChecked()) {
			tmpDate = tvDate.getText().toString();
		} else {
			tmpDate = "01/01/3001";
		}
		
		newGroup.setName(etGroupName.getText().toString());
		newGroup.setOnwardTime(tmpDate+" "+tvOnwardTime.getText().toString());
		newGroup.setReturnTime(tmpDate+" "+tvReturnTime.getText().toString());
		newGroup.setRecurring(cbRecurring.isChecked());
		newGroup.setDaysOfWeek(daysOfWeek());
		newGroup.setOnwardLocation(onwardLatLng);
		newGroup.setReturnLocation(returnLatLng);
		newGroup.setMembers(groupMembers);
		newGroup.setIsPublic(cbIsPublic.isChecked());
		newGroup.setOwner(ParseUser.getCurrentUser());

		if (byteArray != null) {
			ParseFile photoFile = new ParseFile("group_photo.jpg", byteArray);
			newGroup.setPhotoFile(photoFile);	    	 
		}
				
	}
	
	private boolean[] daysOfWeek() {
		boolean[] daysOfWeekArray = new boolean[7];
		daysOfWeekArray[0] = cbMonday.isChecked();
		daysOfWeekArray[1] = cbTuesday.isChecked();
		daysOfWeekArray[2] = cbWednesday.isChecked();
		daysOfWeekArray[3] = cbThursday.isChecked();
		daysOfWeekArray[4] = cbFriday.isChecked();
		daysOfWeekArray[5] = cbSaturday.isChecked();
		daysOfWeekArray[6] = cbSunday.isChecked();
		return daysOfWeekArray;
	}

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
			byteArray = stream.toByteArray();
			// photoFile = new ParseFile("group_photo.jpg", byteArray);
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
				groupMembersStr.add(ParseUser.getCurrentUser().getObjectId());
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
