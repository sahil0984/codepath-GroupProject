package com.codepath.rideso.dialogs;


import java.util.List;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.rideso.R;
import com.codepath.rideso.adapters.SmartFragmentStatePagerAdapter;
import com.codepath.rideso.fragments.MyGroupListFragment;
import com.codepath.rideso.fragments.MyNetworkGroupListFragment;
import com.codepath.rideso.fragments.PublicGroupListFragment;
import com.codepath.rideso.fragments.SavingsFragment;
import com.codepath.rideso.models.Group;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.IconPagerAdapter;


public class SavingsDialog extends DialogFragment {


	
	public SavingsDialog () {
		//Empty constructor required for Dialog Fragment
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_savings, container);

		
		SavingsFragmentAdapter mAdapter = new SavingsFragmentAdapter(getChildFragmentManager());

		ViewPager mPager = (ViewPager) view.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        CirclePageIndicator mIndicator = (CirclePageIndicator)view.findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        
        mIndicator.setViewPager(mPager);
        mIndicator.setSnap(true);

        //We set this on the indicator, NOT the pager
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(getActivity(), "Changed to page " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
		
		return view;

	}
	


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	  Dialog dialog = super.onCreateDialog(savedInstanceState);

	  // request a window without the title
	  dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
	  	  
	  return dialog;
	}
	


	
	static class SavingsFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    	private static int NUM_ITEMS = 4;

	    public SavingsFragmentAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public Fragment getItem(int position) {
	        switch (position) {
	        case 0:
	            return SavingsFragment.newInstance(0, "Weekly Savings");
	        case 1:
	            return SavingsFragment.newInstance(1, "Monthly Savings");
	        case 2:
	            return SavingsFragment.newInstance(2, "Annual Savings");
	        case 3:
	            return SavingsFragment.newInstance(3, "Overall Savings");
	        default:
	        	return null;
            }	    
	    }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
        	return "Page " + position;
        }

		@Override
		public int getIconResId(int index) {
			return 0;
		}
	}
	
}
