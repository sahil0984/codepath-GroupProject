package com.codepath.rideso.fragments;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Text;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.rideso.GroupDetailActivity;
import com.codepath.rideso.R;
import com.codepath.rideso.adapters.GroupArrayAdapter;
import com.codepath.rideso.models.Group;
import com.codepath.rideso.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public abstract class GroupListFragment extends Fragment {
	private ArrayList<Group> groups;
	private ArrayAdapter<Group> aGroups;
	private ListView lvGroups;

	public TextView tvPageTitle;
	public ProgressBar pbLoading;
	
	public int page;
	public String title;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Non-view initialization
		groups = new ArrayList<Group>();
		aGroups = new GroupArrayAdapter(getActivity(), groups);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//Inflate the layout
		View v = inflater.inflate(R.layout.fragment_groups_list, container, false);
		//Assign our view references
		lvGroups = (ListView) v.findViewById(R.id.lvGroups);
		lvGroups.setAdapter(aGroups);
		lvGroups.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int pos, long id) {
                Intent i = new Intent(getActivity(),GroupDetailActivity.class);
                i.putExtra("group",groups.get(pos).getObjectId());
                Log.d("MyApp", "My objectId before sending in is:" + groups.get(pos).getObjectId());
                //Use the Request Code to send the index of the list (pos)
                startActivityForResult(i,pos);				
			}
			
		});
		
		//tvPageTitle = (TextView) v.findViewById(R.id.tvPageTitle);
		pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
		pbLoading.setVisibility(ProgressBar.VISIBLE);

		
		populateGroups();

		
		//tvPageTitle.setText(title);

		//Return the layout view
		return v;
	}
	
	
	public void appendNewGroup(Group newGroup) {
		aGroups.add(newGroup);
	}
	public void addAllGroups(List<Group> groupListp) {
		aGroups.addAll(groupListp);
	}
	
    abstract public void populateGroups();

	
}
