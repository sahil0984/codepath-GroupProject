package com.codepath.rideso.dialogs;


import com.codepath.rideso.R;
import com.parse.FindCallback;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

public class ChoosePhotoDialog extends DialogFragment {

	private Button btnCamera;
	private Button btnGallery;
	private Button btnCancel;
	
	public ChoosePhotoDialog () {
		//Empty constructor required for Dialog Fragment
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_choose_photo, container);
			
		setupViews(view);
		
		return view;

	}
	
	private void setupViews(View view) {
		btnCamera = (Button) view.findViewById(R.id.btnCamera);
		btnGallery = (Button) view.findViewById(R.id.btnGallery);
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		
		btnCamera.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dataPasser.onDataPass("camera");
			    dismiss();
			}
		});
		
		btnGallery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dataPasser.onDataPass("gallery");
			    dismiss();
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
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
	
	
	//Passing data from fragment to activity
	//----------------------------------------
	//To pass data back to main activity, create an interface and implement it in main activity
	OnDataPass dataPasser;
	public interface OnDataPass {
	    public void onDataPass(String action);
	}
	@Override
	public void onAttach(Activity a) {
	    super.onAttach(a);
	    dataPasser = (OnDataPass) a;
	}
}
