package com.codepath.groupproject.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.groupproject.R;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;

public class GroupArrayAdapter extends ArrayAdapter<Group> {
    private Context context;

	public GroupArrayAdapter(Context context, List<Group> objects) {
		super(context, R.layout.group_item_no_repeat, objects);
		this.context = context;
	}
	
	@Override
	public int getItemViewType(int position) {
	    // Define a way to determine which layout to use, here it's just evens and odds.
	    if (getItem(position).getRecurring()) {
	    	return 1;
	    } else {
	    	return 0;
	    }
	    	
	}
	
	@Override
	public int getViewTypeCount() {
	    return 2; // Count of different layouts
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Get the data item for this position
		Group group = getItem(position);
		ViewHolder holder;
		
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
    	   
  		LayoutInflater inflater = LayoutInflater.from(getContext());
  		
  		if (getItemViewType(position) == 1) {
  			convertView = inflater.inflate(R.layout.group_item_repeat, parent, false);
  		} else {
  			convertView = inflater.inflate(R.layout.group_item_no_repeat, parent, false);
  		}
  		
  		holder = new ViewHolder();
  	    

  
  	    holder.ivGroupImage = (ParseImageView) convertView.findViewById(R.id.ivGroupImage);
  	    holder.tvGroupName = (TextView) convertView.findViewById(R.id.tvName);
  	    holder.tvOnwardTime = (TextView) convertView.findViewById(R.id.tvOnwardTime);
  	    holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
  	    holder.tvReturnTime = (TextView) convertView.findViewById(R.id.tvReturnTime);
  	    holder.tvMembersList = (TextView) convertView.findViewById(R.id.tvLocation);
  	    
  	    holder.tvMon = (TextView) convertView.findViewById(R.id.tvMon);
  	    holder.tvTue = (TextView) convertView.findViewById(R.id.tvTue);
  	    holder.tvWed = (TextView) convertView.findViewById(R.id.tvWed);
  	    holder.tvThu = (TextView) convertView.findViewById(R.id.tvThu);
  	    holder.tvFri = (TextView) convertView.findViewById(R.id.tvFri);
  	    holder.tvSat = (TextView) convertView.findViewById(R.id.tvSat);
  	    holder.tvSun = (TextView) convertView.findViewById(R.id.tvSun);

  		convertView.setTag(holder);

  	    
       } else {
    	   
    	   holder = (ViewHolder) convertView.getTag();

   	   }
       
       
       ParseFile photoFile = group.getPhotoFile();
       if (photoFile != null) {
    	   holder.ivGroupImage.setParseFile(photoFile);
    	   holder.ivGroupImage.loadInBackground(new GetDataCallback() {

    		   @Override
    		   public void done(byte[] data, ParseException e) {
                   // nothing to do
    		   }
    	   });
       } else {
    	   //holder.ivGroupImage.setImageResource(android.R.color.transparent);
    	   holder.ivGroupImage.setImageResource(R.drawable.ic_launcher_new);
       }
       
       //holder.ivGroupImage.setImageResource(android.R.color.transparent);       
       //Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(holder.ivGroupImage);

       holder.tvGroupName.setText(group.getName());
       
       String[] formattedOnwardDateTime = new String[2];
       formattedOnwardDateTime = stringToDateTime(group.getOnwardTime());
       String[] formattedReturnDateTime = new String[2];
       formattedReturnDateTime = stringToDateTime(group.getOnwardTime());      
       
 		if (getItemViewType(position) == 0) {
 			holder.tvDate.setText(formattedOnwardDateTime[0]);
 		} else {
 			boolean[] daysOfWeek = group.getDaysofWeek();
 			
 			holder.tvMon.setBackgroundResource(R.color.background_grey);
 			holder.tvTue.setBackgroundResource(R.color.background_grey);
 			holder.tvWed.setBackgroundResource(R.color.background_grey);
 			holder.tvThu.setBackgroundResource(R.color.background_grey);
 			holder.tvFri.setBackgroundResource(R.color.background_grey);
 			holder.tvSat.setBackgroundResource(R.color.background_grey);
 			holder.tvSun.setBackgroundResource(R.color.background_grey);

 			
 			if (daysOfWeek[0]) {
 				holder.tvMon.setBackgroundResource(R.color.theme_color4);
 			}
 			if (daysOfWeek[1]) {
 				holder.tvTue.setBackgroundResource(R.color.theme_color4);
 			}
 			if (daysOfWeek[2]) {
 				holder.tvWed.setBackgroundResource(R.color.theme_color4);
 			}
 			if (daysOfWeek[3]) {
 				holder.tvThu.setBackgroundResource(R.color.theme_color4);
 			}
 			if (daysOfWeek[4]) {
 				holder.tvFri.setBackgroundResource(R.color.theme_color4);
 			}
 			if (daysOfWeek[5]) {
 				holder.tvSat.setBackgroundResource(R.color.theme_color4);
 			}
 			if (daysOfWeek[6]) {
 				holder.tvSun.setBackgroundResource(R.color.theme_color4);
 			}
 		}
       
       holder.tvOnwardTime.setText(formattedOnwardDateTime[1]);
       holder.tvReturnTime.setText(formattedReturnDateTime[1]);
       
       int groupMembersCount;
       try {
    	   groupMembersCount = group.getMembers().size();
       } catch (Exception e) {
    	   groupMembersCount = 0;
    	   e.printStackTrace();
       }
              
       String membersList = "";
       for (int i=0; i<groupMembersCount; i++) {
    	   //Toast.makeText(context, group.getMembers().get(i).toString(), Toast.LENGTH_LONG).show();
    	   //Log.d("FBJSON", group.getMembers().get(i).getFirstName());
    	   membersList = membersList + group.getMembers().get(i).getFirstName();        
           if (i != groupMembersCount-1) {
        	 membersList = membersList + ", ";
           }
       }
       holder.tvMembersList.setText(membersList);
		
		// Return the completed view to render on screen
	    return convertView;
	}
	
	
	static class ViewHolder {
		
		ParseImageView ivGroupImage;
		TextView tvGroupName;
		TextView tvOnwardTime;
		TextView tvDate;
		TextView tvReturnTime;
		TextView tvMembersList;
		
		TextView tvMon;
		TextView tvTue;
		TextView tvWed;
		TextView tvThu;
		TextView tvFri;
		TextView tvSat;
		TextView tvSun;	
	}
	
	public String[] stringToDateTime(String dateTime) {
		
		String date = "";
		String time = "";
		
		date = dateTime.replaceAll(" .*:.*", "");
		time = dateTime.replaceAll(".*/.*/.* ", "");

		if (date.equals("01/01/3001")) {
			date = "";
		}
		if (time.equals("25:25")) {
			time = "";
		}
				
		String[] dateTimeQualified = new String[2];
		
		dateTimeQualified[0] = date;
		dateTimeQualified[1] = time;
		
		return dateTimeQualified;
	}
	
	public String stringToDateTimeRelative(String dateTime) {
		
		long dateMillis;
		String relativeDate = "";
		
		// Add Recurring parameter.(repeat/or not) absolute or relative depending on Ans.
		try {
			dateMillis = new SimpleDateFormat("MM/dd/yyyy hh:mm", Locale.ENGLISH).parse(dateTime).getTime();
			//dateMillis = new SimpleDateFormat("MM/dd/yyyy'T'hh:mm", Locale.ENGLISH).parse(dateTime).getTime();
			relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
					System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
			
			relativeDate = (String) DateUtils.getRelativeDateTimeString(context, dateMillis, DateUtils.SECOND_IN_MILLIS,
						DateUtils.WEEK_IN_MILLIS, 0);
		} catch (java.text.ParseException e) {
			dateMillis = 0;
			e.printStackTrace();
		}
		
		return relativeDate;
	}
	
}
