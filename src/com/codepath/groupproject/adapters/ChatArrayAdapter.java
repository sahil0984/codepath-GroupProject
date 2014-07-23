package com.codepath.groupproject.adapters;

import java.util.List;

import com.codepath.groupproject.R;
import com.codepath.groupproject.models.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatArrayAdapter extends ArrayAdapter<Chat> {
    private Context context;

	public ChatArrayAdapter(Context context, List<Chat> objects) {
		super(context, R.layout.group_item, objects);
		this.context = context;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Get the data item for this position
		Chat chat = getItem(position);
		ViewHolder holder;
		
       // Check if an existing view is being reused, otherwise inflate the view
       if (convertView == null) {
    	   
  		LayoutInflater inflater = LayoutInflater.from(getContext());
  		convertView = inflater.inflate(R.layout.chat_item, parent, false);
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
