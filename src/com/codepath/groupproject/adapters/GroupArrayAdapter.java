package com.codepath.groupproject.adapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.groupproject.R;
import com.codepath.groupproject.models.Group;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;

public class GroupArrayAdapter extends ArrayAdapter<Group> {
    private Context context;

	public GroupArrayAdapter(Context context, List<Group> objects) {
		super(context, R.layout.group_item, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Get the data item for this position
		Group group = getItem(position);
		ViewHolder holder;
		
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
    	   
  		LayoutInflater inflater = LayoutInflater.from(getContext());
  		convertView = inflater.inflate(R.layout.group_item, parent, false);
  	    holder = new ViewHolder();
  	    

  
  	    holder.ivGroupImage = (ParseImageView) convertView.findViewById(R.id.ivGroupImage);
  	    holder.tvGroupName = (TextView) convertView.findViewById(R.id.tvName);
  	    holder.tvOnwardTime = (TextView) convertView.findViewById(R.id.tvOnwardTime);
  	    holder.tvReturnTime = (TextView) convertView.findViewById(R.id.tvReturnTime);
  	    holder.tvMembersList = (TextView) convertView.findViewById(R.id.tvLocation);

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
    	   holder.ivGroupImage.setImageResource(android.R.color.transparent);
       }
       
       //holder.ivGroupImage.setImageResource(android.R.color.transparent);       
       //Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(holder.ivGroupImage);

       holder.tvGroupName.setText(group.getName());
       holder.tvOnwardTime.setText(group.getOnwardTime());
       holder.tvReturnTime.setText(group.getReturnTime());
       
       int groupMemberCount;
       try {
    	   groupMemberCount = group.getMembers().size();
       } catch (Exception e) {
    	   groupMemberCount = 0;
    	   e.printStackTrace();
       }
              
       String membersList = "";
       for (int i=0; i<groupMemberCount; i++) {
    	   membersList = membersList + group.getMembers().get(i).getFirstName();        
           if (i != groupMemberCount-1) {
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
		TextView tvReturnTime;
		TextView tvMembersList;
		

	}
	
}
