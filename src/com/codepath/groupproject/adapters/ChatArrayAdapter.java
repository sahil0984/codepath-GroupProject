package com.codepath.groupproject.adapters;

import java.util.List;

import com.codepath.groupproject.R;
import com.codepath.groupproject.models.Chat;
import com.parse.ParseUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatArrayAdapter extends ArrayAdapter<Chat> {
    private Context context;

	public ChatArrayAdapter(Context context, List<Chat> objects) {
		super(context, R.layout.group_item_no_repeat, objects);
		this.context = context;
	}
	
	@Override
	public int getItemViewType(int position) {
	    // Define a way to determine which layout to use, here it's just evens and odds.
	    if (getItem(position).getSenderObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
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
		Chat chat = getItem(position);
		ViewHolder holder;
		
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
    	   
  		LayoutInflater inflater = LayoutInflater.from(getContext());
  		
  		if (getItemViewType(position) == 1) {
  			convertView = inflater.inflate(R.layout.chat_item_right, parent, false);
  		} else {
  			convertView = inflater.inflate(R.layout.chat_item_left, parent, false);
  		}
  	    holder = new ViewHolder();
  	    

  
  	    holder.tvSender = (TextView) convertView.findViewById(R.id.tvSender);
  	    holder.tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
  	    holder.tvTimeStamp = (TextView) convertView.findViewById(R.id.tvTimeStamp);

  		convertView.setTag(holder);

  	    
       } else {
    	   
    	   holder = (ViewHolder) convertView.getTag();

   	   }
       
       holder.tvSender.setText(chat.getSender());
       holder.tvMessage.setText(chat.getMessage());
       holder.tvTimeStamp.setText(chat.getTimeStamp());
       
       return convertView;
	}
	
	static class ViewHolder {
		
		TextView tvSender;
		TextView tvMessage;
		TextView tvTimeStamp;

	}
	
	@Override
	public boolean isEnabled(int position) {
	    return false;
	}
}
