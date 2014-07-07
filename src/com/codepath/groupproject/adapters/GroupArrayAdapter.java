package com.codepath.groupproject.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.groupproject.R;
import com.codepath.groupproject.models.Group;
import com.squareup.picasso.Picasso;

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
  	    
  	    holder.ivGroupImage = (ImageView) convertView.findViewById(R.id.ivGroupImage);
  	    holder.tvGroupName = (TextView) convertView.findViewById(R.id.tvGroupName);
  	    holder.tvOnwardTime = (TextView) convertView.findViewById(R.id.tvOnwardTime);
  	    holder.tvReturnTime = (TextView) convertView.findViewById(R.id.tvReturnTime);
  	    holder.tvMembersList = (TextView) convertView.findViewById(R.id.tvMembersList);

  		convertView.setTag(holder);

  	    
       } else {
    	   
    	   holder = (ViewHolder) convertView.getTag();

   	   }
       
       holder.ivGroupImage.setImageResource(android.R.color.transparent);
       
       Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(holder.ivGroupImage);

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
		
		ImageView ivGroupImage;
		TextView tvGroupName;
		TextView tvOnwardTime;
		TextView tvReturnTime;
		TextView tvMembersList;
		

	}
	
}
