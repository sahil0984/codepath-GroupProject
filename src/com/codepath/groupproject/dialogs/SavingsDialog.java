package com.codepath.groupproject.dialogs;


import java.util.List;

import com.codepath.groupproject.R;
import com.codepath.groupproject.models.Group;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SavingsDialog extends DialogFragment {

	private TextView tvCarbonFootprint;
	private TextView tvDollarSavings;
	private Button btnGoGack;

	
	public SavingsDialog () {
		//Empty constructor required for Dialog Fragment
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_savings, container);
			
		setupViews(view);
		
		return view;

	}
	
	private void setupViews(View view) {
		tvCarbonFootprint = (TextView) view.findViewById(R.id.tvCarbonFootprint);
		tvDollarSavings = (TextView) view.findViewById(R.id.tvDollarSavings);
		btnGoGack = (Button) view.findViewById(R.id.btnGoBack);
		
		tvCarbonFootprint.setText("Carbon Foorprint: 0.2 k tons CO2 per year");
		tvDollarSavings.setText("Dollars Saved: $600");
		
		calculateSavings();
		
		btnGoGack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			    dismiss();
			}
		});
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	  Dialog dialog = super.onCreateDialog(savedInstanceState);

	  // request a window without the title
	  dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	  	  
	  return dialog;
	}
	
	public void calculateSavings() {
		ParseQuery<Group> queryGroups = ParseQuery.getQuery(Group.class);
		// Define our query conditions
		queryGroups.whereEqualTo("owner", ParseUser.getCurrentUser());
		// Execute the find asynchronously
		queryGroups.findInBackground(new FindCallback<Group>() {
			@Override
			public void done(List<Group> groupList, ParseException e) {
		        if (e == null) {
		        	if (groupList.size()!=0) {
		        		// Access the array of results here
		        		//String firstItemId = groupList.get(0).getString("name");
		        		//Toast.makeText(getActivity(), firstItemId, Toast.LENGTH_SHORT).show();
		        		
		        		//aGroups.addAll(groupList);
		        		
		        		//ParseUser.getCurrentUser().put("groups", groupList);
		        		//ParseUser.getCurrentUser().saveInBackground();
		        		
		        		for (int i=0; i<groupList.size(); i++) {
		        			String onwardTime = groupList.get(i).getString("onwardTime");
		        			String returnTime = groupList.get(i).getString("returnTime");
		        			String onwardLocation = groupList.get(i).getString("onwardLocation");
		        			String returnLocation = groupList.get(i).getString("returnLocation");
		        			
		        			String homeAdd = ParseUser.getCurrentUser().getString("homeAdd");
		        			String workAdd = ParseUser.getCurrentUser().getString("workAdd");
		        			
		        			//total distance = homeAdd - workAdd;
		        			//Average miles/year / Average mpg = Gallons per year
		        			//Gallons per year x 19.8 lbs per gallon = Lbs of carbon dioxide per year
		        			
		        			//For the group multiply with groupList(i).getMembers().size()
		        			
		        			//http://www.sutmundo.com/calculating-your-cars-carbon-footprint/
		        		}
		        		
		        		
		        		
		        	} else {
		        		Toast.makeText(getActivity(), "No group found.", Toast.LENGTH_SHORT).show();
		        	}
		        } else {
		            Log.d("item", "Error: " + e.getMessage());
		        }
			}
		});
	}
	
	
}
