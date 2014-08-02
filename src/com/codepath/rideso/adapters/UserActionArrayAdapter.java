package com.codepath.rideso.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.rideso.ChatActivity;
import com.codepath.rideso.GroupDetailActivity;
import com.codepath.rideso.R;
import com.codepath.rideso.Utils;
import com.codepath.rideso.models.Group;
import com.codepath.rideso.models.User;
import com.codepath.rideso.models.UserAction;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseUser;

public class UserActionArrayAdapter extends ArrayAdapter<UserAction> {
    private Context context;

	public UserActionArrayAdapter(Context context, List<UserAction> objects) {
		super(context, R.layout.user_action_item, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Get the data item for this position
		UserAction userAction = getItem(position);
		ViewHolder holder;
		
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
    	   
  		LayoutInflater inflater = LayoutInflater.from(getContext());
  		
  		convertView = inflater.inflate(R.layout.user_action_item, parent, false);
  		
  		holder = new ViewHolder();
  	    

  
  	    holder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
  	    holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);

  		convertView.setTag(holder);

  	    
       } else {
    	   
    	   holder = (ViewHolder) convertView.getTag();

   	   }
		
       
 	   String diffDateTime;
 	   diffDateTime = Utils.getDiffInDateTime(userAction.getStartTime(), userAction.getEndTime());


       holder.tvDistance.setText(userAction.getDistance() + "miles");
       holder.tvTime.setText(diffDateTime);
       
       
		// Return the completed view to render on screen
	    return convertView;
	}
	
	
	static class ViewHolder {
		private TextView tvDistance;
		private TextView tvTime;

	}
	
}
