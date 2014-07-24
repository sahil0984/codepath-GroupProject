package com.codepath.groupproject.adapters;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.codepath.groupproject.R;
import com.codepath.groupproject.ViewProfileActivity;
import com.codepath.groupproject.adapters.GroupArrayAdapter.ViewHolder;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.facebook.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;

public class UserArrayAdapter extends ArrayAdapter<User> {
	private Context context;
	
	public UserArrayAdapter(Context context,List<User> objects) {
		super(context, R.layout.group_item_no_repeat, objects);
		this.context = context;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Get the data item for this position
		final User user = getItem(position);
		ViewHolder holder;
		
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
    	   
  		LayoutInflater inflater = LayoutInflater.from(getContext());
  		convertView = inflater.inflate(R.layout.user_item, parent, false);
  	    holder = new ViewHolder();
  	    
  	    holder.ivProfileImage = (ProfilePictureView) convertView.findViewById(R.id.ivProfileImage);
  	    holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
  	    holder.tvOnwardTime = (TextView) convertView.findViewById(R.id.tvOnwardTime);
  	    holder.tvReturnTime = (TextView) convertView.findViewById(R.id.tvReturnTime);
  	    holder.tvLocation = (TextView) convertView.findViewById(R.id.tvLocation);

  		convertView.setTag(holder);
  		
  		holder.ivProfileImage.setTag(user);
  		holder.ivProfileImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				  Intent i = new Intent(context, ViewProfileActivity.class);
				  // put "extras" into the bundle for access in the second activity
				  i.putExtra("objectId", user.getObjectId()); 
				  // brings up the second activity
				  context.startActivity(i); 
			}
		});

  	    
       } else {
    	   
    	   holder = (ViewHolder) convertView.getTag();
    	   holder.ivProfileImage.setTag(user);


   	   }
       
       holder.ivProfileImage.setProfileId((String)user.get("fbId"));

       holder.tvName.setText(user.getFirstName());
       holder.tvOnwardTime.setText("Estimated");
       holder.tvReturnTime.setText("Estimated");
       holder.tvLocation.setText("Location");
       
		
		// Return the completed view to render on screen
	    return convertView;
	}
	
	
	static class ViewHolder {
		
		ProfilePictureView ivProfileImage;
		TextView tvName;
		TextView tvOnwardTime;
		TextView tvReturnTime;
		TextView tvLocation;
		

	}
}
