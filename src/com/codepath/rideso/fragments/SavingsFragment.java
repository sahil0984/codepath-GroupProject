package com.codepath.rideso.fragments;

import java.util.List;

import com.codepath.rideso.R;
import com.codepath.rideso.ViewProfileActivity;
import com.codepath.rideso.models.Group;
import com.codepath.rideso.models.User;
import com.codepath.rideso.models.UserAction;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public final class SavingsFragment extends Fragment {
	
	private ProgressBar pbLoading;

	//private LinearLayout llCarbonMask;
	//private LinearLayout llDollarsMask;
	
	
	private TextView tvCarbonContribution;
	private TextView tvDollarsSpent;
	
	private TextView tvCarbonSavings;
	private TextView tvDollarSavings;
	
	private TextView tvPageTitle;
	
	private PieGraph pgCarbon;
	private PieGraph pgDollar;

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
		pbLoading.setVisibility(ProgressBar.VISIBLE);

        
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
		
        //calculateSavings();
        calculateSavingsRealTime();
		//updateSavings();

        return v;
    }

    
	private void setupViews(View view) {
		//llCarbonMask = (LinearLayout) view.findViewById(R.id.llCarbonMask);
		//llDollarsMask = (LinearLayout) view.findViewById(R.id.llDollarsMask);
		
		tvCarbonContribution = (TextView) view.findViewById(R.id.tvCarbonContribution);
		tvDollarsSpent = (TextView) view.findViewById(R.id.tvDollarsSpent);
		
		tvCarbonSavings = (TextView) view.findViewById(R.id.tvCarbonSavings);
		tvDollarSavings = (TextView) view.findViewById(R.id.tvDollarSavings);
		
		//TextView tvCarbonMax = (TextView) view.findViewById(R.id.tvCarbonMax);
		//TextView tvDollarsMax = (TextView) view.findViewById(R.id.tvDollarsMax);
		
		tvPageTitle = (TextView) view.findViewById(R.id.tvPageTitle);
		tvPageTitle.setText(title);
		
		
	    Typeface robotoBoldCondensedItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
	    //tvCarbonMax.setTypeface(robotoBoldCondensedItalic);
	    //tvDollarsMax.setTypeface(robotoBoldCondensedItalic);
	    
	    tvCarbonSavings.setTypeface(robotoBoldCondensedItalic);
	    tvDollarSavings.setTypeface(robotoBoldCondensedItalic);
	    
	    tvCarbonContribution.setTypeface(robotoBoldCondensedItalic);
	    tvDollarsSpent.setTypeface(robotoBoldCondensedItalic);
	    
		pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);
		pbLoading.setVisibility(ProgressBar.INVISIBLE);
		
		
		pgDollar = (PieGraph) view.findViewById(R.id.pgDollar);
		pgCarbon = (PieGraph) view.findViewById(R.id.pgCarbon);
		
		
		tvCarbonSavings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startViewProfileActivity();		
			}
		});
		tvDollarSavings.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				startViewProfileActivity();		
			}
		});		

	}
	
	public void startViewProfileActivity() {
		  Intent i = new Intent(getActivity(), ViewProfileActivity.class);
		  // put "extras" into the bundle for access in the second activity
		  i.putExtra("objectId", ParseUser.getCurrentUser().getObjectId()); 
		  // brings up the second activity
		  getActivity().startActivity(i); 
	}

	public void calculateSavingsRealTime() {
		ParseQuery<UserAction> queryUserAction = ParseQuery.getQuery(UserAction.class);
		queryUserAction.whereEqualTo("userObjectId", ParseUser.getCurrentUser().getObjectId());
		queryUserAction.findInBackground(new FindCallback<UserAction>() {

			@Override
			public void done(List<UserAction> userActionList, ParseException e) {
		        if (e == null) {
		        	if (userActionList.size()!=0) {
		        		for (int i=0; i<userActionList.size(); i++) {
		        			//totalDist = totalDist + userActionList.get(i).getDistance();
		        			savingsMath(Double.parseDouble(userActionList.get(i).getDistance()), 
		        						Integer.parseInt(userActionList.get(i).getMemberCount()));
		        		}
		        		
		        	} else {
		        		Toast.makeText(getActivity(), "No user action found.", Toast.LENGTH_SHORT).show();
		        		
		        		maxCarbonContribution = 0;
		        		actualCarbonContribution = 0;
		        		maxDollarsSpent = 0;
		        		actualDollarsSpent = 0;
		        		
		        		pbLoading.setVisibility(ProgressBar.INVISIBLE);
		        	}
		        	
	        		
	        		actualCarbonContribution = actualCarbonContribution * scale;
	        		maxCarbonContribution = maxCarbonContribution * scale;
	        		actualDollarsSpent = actualDollarsSpent * scale;
	        		maxDollarsSpent = maxDollarsSpent * scale;
	        		
	        		float actualCarbonPercentOfMax = actualCarbonContribution/maxCarbonContribution;
	        		float actualDollarsPercentOfMax = actualDollarsSpent/maxDollarsSpent;
	        		
	        		animateCounter(Math.round(actualCarbonContribution), "tvCarbonContribution");
	        		animateCounter(Math.round(actualDollarsSpent), "tvDollarsSpent");
	        		
	        		animateCounter(Math.round(maxCarbonContribution-actualCarbonPercentOfMax), "tvCarbonSavings");
	        		animateCounter(Math.round(maxDollarsSpent-actualDollarsPercentOfMax), "tvDollarSavings");
	        			        		
	        		updatePieGraph(pgCarbon, actualCarbonPercentOfMax);
	        		updatePieGraph(pgDollar, actualDollarsPercentOfMax);
	        		
	        		pbLoading.setVisibility(ProgressBar.INVISIBLE);
	        		
		        } else {
		            Log.d("item", "Error: " + e.getMessage());
		        }
			}

		});
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
		        			
		        			
		        			savingsMath(homeAdd.distanceInMilesTo(returnLocation), groupList.get(i).getMembers().size());
			        		

		        		}
		        		
		        		actualCarbonContribution = actualCarbonContribution * scale;
		        		maxCarbonContribution = maxCarbonContribution * scale;
		        		actualDollarsSpent = actualDollarsSpent * scale;
		        		maxDollarsSpent = maxDollarsSpent * scale;
		        		
		        		float actualCarbonPercentOfMax = actualCarbonContribution/maxCarbonContribution;
		        		float actualDollarsPercentOfMax = actualDollarsSpent/maxDollarsSpent;
		        		
		        		//Toast.makeText(getActivity(), "Total groups:" + groupList.size(), Toast.LENGTH_SHORT).show();
		        		//animateGraphBar(actualCarbonPercentOfMax, llCarbonMask);
		        		//animateGraphBar(actualDollarsPercentOfMax, llDollarsMask);
		        		animateCounter(Math.round(actualCarbonContribution), "tvCarbonContribution");
		        		animateCounter(Math.round(actualDollarsSpent), "tvDollarsSpent");
		        		
		        		animateCounter(Math.round(maxCarbonContribution-actualCarbonPercentOfMax), "tvCarbonSavings");
		        		animateCounter(Math.round(maxDollarsSpent-actualDollarsPercentOfMax), "tvDollarSavings");
		        		
		        		//Toast.makeText(getActivity(), "actualCarbonContribution" + actualCarbonContribution, Toast.LENGTH_SHORT).show();
		        		//Toast.makeText(getActivity(), "maxCarbonContribution" + maxCarbonContribution, Toast.LENGTH_SHORT).show();
		        		//Toast.makeText(getActivity(), "actualDollarsSpent" + actualDollarsSpent, Toast.LENGTH_SHORT).show();
		        		//Toast.makeText(getActivity(), "maxDollarsSpent" + maxDollarsSpent, Toast.LENGTH_SHORT).show();
		        		
		        		
		        		updatePieGraph(pgCarbon, actualCarbonPercentOfMax);
		        		updatePieGraph(pgDollar, actualDollarsPercentOfMax);
		        		
		        		pbLoading.setVisibility(ProgressBar.INVISIBLE);

		        	} else {
		        		Toast.makeText(getActivity(), "No group found.", Toast.LENGTH_SHORT).show();
		        		pbLoading.setVisibility(ProgressBar.INVISIBLE);
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
//		if (tv.equals("tvCarbonContribution") || tv.equals("tvCarbonSavings")) {
//			if (count > 999999) {
//				count = (int) (count/2204.62);  // pounds to tons conversion
//				
//			}
//		}
		
		ValueAnimator animator = new ValueAnimator();
		animator.setObjectValues(0, count);
		animator.addUpdateListener(new AnimatorUpdateListener() {
			public void onAnimationUpdate(ValueAnimator animation) {
				String valueOfString;
				valueOfString = String.valueOf(animation.getAnimatedValue());
				if (tv.equals("tvCarbonContribution")) {
					tvCarbonContribution.setText(
							Html.fromHtml("Added <br>" + valueOfString + " lbs"));
				} else if (tv.equals("tvDollarsSpent")) {
					tvDollarsSpent.setText(
							Html.fromHtml("Spent <br> $ " + valueOfString));
				} else if (tv.equals("tvCarbonSavings")) {
					tvCarbonSavings.setText(
							Html.fromHtml("<b>" + valueOfString + "</b> lbs<br>of CO<sub><small><small>2</small></small></sub>"));
				} else if (tv.equals("tvDollarSavings")) {
					tvDollarSavings.setText(
							Html.fromHtml("$ <b>" + valueOfString + "</b>"));
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
	
	public void updatePieGraph(PieGraph pg, float goalFraction) {
	
		PieSlice slice = new PieSlice();
		slice.setColor(Color.parseColor("#99CC00"));
		slice.setValue(1);
		pg.addSlice(slice);
		slice = new PieSlice();
		slice.setColor(Color.parseColor("#FFBB33"));
		slice.setValue(0);
		pg.addSlice(slice);
		//slice = new PieSlice();
		//slice.setColor(Color.parseColor("#AA66CC"));
		//slice.setValue(8);
		//pg.addSlice(slice);
		
		
		pg.setInnerCircleRatio(200);
		pg.setPadding(5);
	    
		pg.getSlice(0).setGoalValue(1-goalFraction);
		pg.getSlice(1).setGoalValue(goalFraction);
		
	    pg.setDuration(2000);//default if unspecified is 300 ms
	    pg.setInterpolator(new AccelerateDecelerateInterpolator());//default if unspecified is linear; constant speed
	    //pg.setAnimationListener(getAnimationListener());//optional
	    pg.animateToGoalValues();
	}
	
	
	public void savingsMath(double dist, int memberCount) {
		//Double onwardMiles = homeAdd.distanceInMilesTo(returnLocation);
		Double onwardMiles = dist;
		//Toast.makeText(getActivity(), "Dist;"+onwardMiles.toString()+"miles", Toast.LENGTH_SHORT).show();
		
		float avgMilesPerYear = (float) (onwardMiles*2*53*5); //x2 return journey; x53 weeks; x5 days
		
		float avgMilesPerGallon = 24;
		float avgGallonsPerYear = (float) (avgMilesPerYear/avgMilesPerGallon);
		
		maxCarbonContribution = maxCarbonContribution + (float) (19.8 * avgGallonsPerYear);
		actualCarbonContribution = actualCarbonContribution 
								 + (float) (19.8 * avgGallonsPerYear / memberCount);
		
		
		float gasPrice = (float) 4.50;
		maxDollarsSpent = maxDollarsSpent + (avgGallonsPerYear * gasPrice);
		actualDollarsSpent = actualDollarsSpent
						   + (avgGallonsPerYear * gasPrice / memberCount);
		
		//total distance = homeAdd - workAdd;
		//Average miles/year / Average mpg = Gallons per year
		//Gallons per year x 19.8 lbs per gallon = Lbs of carbon dioxide per year
		
		//For the group multiply with groupList(i).getMembers().size()
		
		//http://www.sutmundo.com/calculating-your-cars-carbon-footprint/
	}
	

}