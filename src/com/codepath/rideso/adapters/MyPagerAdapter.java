package com.codepath.rideso.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.codepath.rideso.fragments.MyGroupListFragment;
import com.codepath.rideso.fragments.MyNetworkGroupListFragment;
import com.codepath.rideso.fragments.PublicGroupListFragment;


//PagerAdapter for ViewPager		
public class MyPagerAdapter extends SmartFragmentStatePagerAdapter {
	private static int NUM_ITEMS = 3;
		
        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        
	    @Override
		public float getPageWidth(int position) {
	    	return 0.93f;
		}
        
        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }
 
        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
	        switch (position) {
	        case 0:
	            return MyGroupListFragment.newInstance(0, "MyGroups");
	        case 1:
	            return MyNetworkGroupListFragment.newInstance(1, "MyNetworkGroups");
	        case 2:
	            return PublicGroupListFragment.newInstance(2, "PublicGroups");
	        default:
	        	return null;
            }
        }
        
        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
        	return "Page " + position;
        }
        
        
    }