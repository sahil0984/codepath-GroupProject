package com.codepath.groupproject.fragments;

import java.util.List;

import com.codepath.groupproject.R;
import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class SavingsFragment extends Fragment {

	private LinearLayout llCarbonMask;
	private LinearLayout llDollarsMask;
	
	
	private TextView tvCarbonContribution;
	private TextView tvDollarsSpent;
	
	private TextView tvCarbonSavings;
	private TextView tvDollarSavings;
	
	private TextView tvPageTitle;

	private float maxCarbonContribution;
	private float maxDollarsSpent;
	private float actualCarbonContribution;
	private float actualDollarsSpent;
	
	private float carbonSavings;
	private float dollarSavings;
	
	private float scale;
	
	private ParseGeoPoint homeAdd;
	private ParseGeoPoint workAdd;
	
	public int page;
	public String title;
    
	public static SavingsFragment newInstance(int page, String title) {
		SavingsFragment mySavingsFragment = new SavingsFragment();
		Bundle args = new Bundle();
		args.putInt("pageNum", page);
		args.putString("pageTitle", title);
		mySavingsFragment.setArguments(args);
		return mySavingsFragment;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		page = getArguments().getInt("pageNum", 0);
		title = getArguments().getString("pageTitle");
		
		if (page==0) {
			scale = 7;
		} else if (page==1) { 
			scale = 30;
		} else if (page==2) { 
			scale = 365;
		} else if (page==3) { 
			scale = 1;
		}
        
		maxCarbonContribution = (float) 0;
		maxDollarsSpent = (float) 0;
		actualCarbonContribution = (float) 0;
		actualDollarsSpent = (float) 0;
        
		carbonSavings = (float) 0;
		dollarSavings = (float) 0;
		
		homeAdd = ParseUser.getCurrentUser().getParseGeoPoint("homeAdd");
		workAdd = ParseUser.getCurrentUser().getParseGeoPoint("workAdd");
		

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
		View v = inflater.inflate(R.layout.fragment_savings, container, false);

    	
        TextView text = new TextView(getActivity());
        text.setGravity(Gravity.CENTER);
        text.setText(title);
        text.setTextSize(20 * getResources().getDisplayMetrics().density);
        text.setPadding(20, 20, 20, 20);

        setupViews(v);
        
        calculateSavings();
		//updateSavings();

        return v;
    }

    
	private void setupViews(View view) {
		llCarbonMask = (LinearLayout) view.findViewById(R.id.llCarbonMask);
		llDollarsMask = (LinearLayout) view.findViewById(R.id.llDollarsMask);
		
		tvCarbonContribution = (TextView) view.findViewById(R.id.tvCarbonContribution);
		tvDollarsSpent = (TextView) view.findViewById(R.id.tvDollarsSpent);
		
		tvCarbonSavings = (TextView) view.findViewById(R.id.tvCarbonSavings);
		tvDollarSavings = (TextView) view.findViewById(R.id.tvDollarSavings);
		
		tvPageTitle = (TextView) view.findViewById(R.id.tvPageTitle);
		tvPageTitle.setText(title);
	}
    
	public void calculateSavings() {
		
		ParseQuery<User> innerUserQuery = ParseQuery.getQuery(User.class);
		innerUserQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
		
		
		ParseQuery<Group> queryGroup = ParseQuery.getQuery(Group.class);
		queryGroup.include("members");
		queryGroup.whereMatchesQuery("members", innerUserQuery);
		queryGroup.findInBackground(new FindCallback<Group>() {

			@Override
			public void done(List<Group> groupList, ParseException e) {
		        if (e == null) {
		        	if (groupList.size()!=0) {
		        		// Access the array of results here
		        		//String firstItemId = groupList.get(0).getString("name");
		        		//Toast.makeText(getActivity(), firstItemId, Toast.LENGTH_SHORT).show();
		        		
		        		//aGroups.addAll(groupList);
		        		
		        		//ParseUser.getCurrentUser().put("groups", groupList);
		        		//ParseUser.getCurrentUser().saveInBackground();
		        		
		        		for (int i=0; i<groupList.size(); i++) {
		        			String onwardTime = groupList.get(i).getString("onwardTime");
		        			String returnTime = groupList.get(i).getString("returnTime");
		        			ParseGeoPoint onwardLocation = groupList.get(i).getOnwardLocation();
		        			ParseGeoPoint returnLocation = groupList.get(i).getReturnLocation();
		        			
		        			
		        			Double onwardMiles = homeAdd.distanceInMilesTo(returnLocation);
		        			//Toast.makeText(getActivity(), "Dist;"+onwardMiles.toString()+"miles", Toast.LENGTH_SHORT).show();
		        			
		        			float avgMilesPerYear = (float) (onwardMiles*2*53*5); //x2 return journey; x53 weeks; x5 days
		        			
		        			float avgMilesPerGallon = 24;
		        			float avgGallonsPerYear = (float) (avgMilesPerYear/avgMilesPerGallon);
		        			
		        			maxCarbonContribution = maxCarbonContribution + (float) (19.8 * avgGallonsPerYear);
		        			actualCarbonContribution = actualCarbonContribution 
		        									 + (float) (19.8 * avgGallonsPerYear / groupList.get(i).getMembers().size());
		        			
		        			
		        			float gasPrice = (float) 4.50;
			        		maxDollarsSpent = maxDollarsSpent + (avgGallonsPerYear * gasPrice);
			        		actualDollarsSpent = actualDollarsSpent
			        						   + (avgGallonsPerYear * gasPrice / groupList.get(i).getMembers().size());
		        			
		        			//total distance = homeAdd - workAdd;
		        			//Average miles/year / Average mpg = Gallons per year
		        			//Gallons per year x 19.8 lbs per gallon = Lbs of carbon dioxide per year
		        			
		        			//For the group multiply with groupList(i).getMembers().size()
		        			
		        			//http://www.sutmundo.com/calculating-your-cars-carbon-footprint/
			        		

		        		}
		        		
		        		actualCarbonContribution = actualCarbonContribution * scale;
		        		maxCarbonContribution = maxCarbonContribution * scale;
		        		actualDollarsSpent = actualDollarsSpent * scale;
		        		maxDollarsSpent = maxDollarsSpent * scale;
		        		
		        		float actualCarbonPercentOfMax = actualCarbonContribution/maxCarbonContribution;
		        		float actualDollarsPercentOfMax = actualDollarsSpent/maxDollarsSpent;
		        		
		        		//Toast.makeText(getActivity(), "Total groups:" + groupList.size(), Toast.LENGTH_SHORT).show();
		        		animateGraphBar(actualCarbonPercentOfMax, llCarbonMask);
		        		animateGraphBar(actualDollarsPercentOfMax, llDollarsMask);
		        		animateCounter(Math.round(actualCarbonContribution), "tvCarbonContribution");
		        		animateCounter(Math.round(actualDollarsSpent), "tvDollarsSpent");
		        		
		        		animateCounter(Math.round(maxCarbonContribution-actualCarbonPercentOfMax), "tvCarbonSavings");
		        		animateCounter(Math.round(maxDollarsSpent-actualDollarsPercentOfMax), "tvDollarSavings");
		        		
		        		//Toast.makeText(getActivity(), "actualCarbonContribution" + actualCarbonContribution, Toast.LENGTH_SHORT).show();
		        		//Toast.makeText(getActivity(), "maxCarbonContribution" + maxCarbonContribution, Toast.LENGTH_SHORT).show();
		        		//Toast.makeText(getActivity(), "actualDollarsSpent" + actualDollarsSpent, Toast.LENGTH_SHORT).show();
		        		//Toast.makeText(getActivity(), "maxDollarsSpent" + maxDollarsSpent, Toast.LENGTH_SHORT).show();

		        	} else {
		        		Toast.makeText(getActivity(), "No group found.", Toast.LENGTH_SHORT).show();
		        	}
		        } else {
		            Log.d("item", "Error: " + e.getMessage());
		        }
			}
		});
	}
	
	
	protected void animateGraphBar(float scaleBy, LinearLayout ll) {

		float maskScalingBy = 1.0f - scaleBy;
		float finalScaling = (1 + 0.001f) * maskScalingBy;
		
		ObjectAnimator scaleAnim = ObjectAnimator.ofFloat(ll, "scaleX", 1, finalScaling);
		ll.setPivotX(ll.getWidth());
		ll.setPivotY(ll.getHeight());
		scaleAnim.setDuration(2000);
		scaleAnim.start();				
	}
	
	protected void animateCounter(int count, final String tv) {
		 ValueAnimator animator = new ValueAnimator();
	        animator.setObjectValues(0, count);
	        animator.addUpdateListener(new AnimatorUpdateListener() {
	            public void onAnimationUpdate(ValueAnimator animation) {
	            	if (tv.equals("tvCarbonContribution")) {
	            		tvCarbonContribution.setText(String.valueOf(animation.getAnimatedValue()) + " lbs");
	            	} else if (tv.equals("tvDollarsSpent")) {
	            		tvDollarsSpent.setText("$ " + String.valueOf(animation.getAnimatedValue()));
	            	} else if (tv.equals("tvCarbonSavings")) {
	            		tvCarbonSavings.setText("Saved, " + String.valueOf(animation.getAnimatedValue()) + " lbs of CO2");
	            	} else if (tv.equals("tvDollarSavings")) {
	            		tvDollarSavings.setText("Saved, $ " + String.valueOf(animation.getAnimatedValue()));
	            	}
	            }
	        });
	        animator.setEvaluator(new TypeEvaluator<Integer>() {
	            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
	                return Math.round((endValue - startValue) * fraction);
	            }
	        });
	        animator.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animator animation) {					
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {					
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {					
				}
			});
	        animator.setDuration(2000);
	        animator.start();
	}
	

}