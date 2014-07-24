/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package com.codepath.groupproject;

import org.apache.http.Header;
import org.json.JSONObject;

import com.codepath.groupproject.models.Group;
import com.loopj.android.http.AsyncHttpClient;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;



import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * This class provides a simple card as Google Now Birthday
 *

 */
public class GroupDetailCard extends Card {

    public int USE_VIGNETTE=0;
    private Group currentGroup;
    
    public GroupDetailCard(Context context, Group group) {
        super(context,R.layout.carddemo_groupdetail_inner_main);
        currentGroup = group;
        init();
    }

    public GroupDetailCard(Context context, int innerLayout) {
        super(context, innerLayout);
        init();
    }
    
	protected void getOnwardAddFromCoord(ParseGeoPoint pCoord) {
    	Double lat = pCoord.getLatitude();
    	Double lng = pCoord.getLongitude();
	    String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng;
	    AsyncHttpClient client = new AsyncHttpClient();
	    client.get(url, null, new GeoCoderResponseHandler(this.mContext) {
	    	
	    	@Override
	    	public void onSuccess(int statusCode, Header[] headers,
	    			JSONObject response) {
	    		super.onSuccess(statusCode, headers, response);
		    		//tvOnwardLocation.setText(getCheckedAdd());
	    	}
	    	
	    });
		return;
	}
	protected void getReturnAddFromCoord(ParseGeoPoint pCoord) {
    	Double lat = pCoord.getLatitude();
    	Double lng = pCoord.getLongitude();
	    String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng;
	    AsyncHttpClient client = new AsyncHttpClient();
	    client.get(url, null, new GeoCoderResponseHandler(this.mContext) {
	    	
	    	@Override
	    	public void onSuccess(int statusCode, Header[] headers,
	    			JSONObject response) {
	    		super.onSuccess(statusCode, headers, response);
	    	
	    		//tvReturnLocation.setText(getCheckedAdd());
	    			

	    	}
	    	
	    });
		return;
	}
    private void init() {

        //Add Header
        GroupDetailHeader header = new GroupDetailHeader(getContext(), R.layout.carddemo_groupdetail_inner_header);
        header.setButtonExpandVisible(true);
        header.mName = currentGroup.getName();
        header.mSubName = "Birthday today";
        
        addCardHeader(header);

        //Add Expand Area
        CardExpand expand = new GroupDetailExpandCard(getContext());
        addCardExpand(expand);

        //Set clickListener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(getContext(), "Click Listener card", Toast.LENGTH_LONG).show();
            }
        });

        //Add Thumbnail
        /*
        GoogleNowBirthThumb thumbnail = new GoogleNowBirthThumb(getContext());
        float density = getContext().getResources().getDisplayMetrics().density;
        int size= (int)(125*density);
        thumbnail.setUrlResource("https://lh5.googleusercontent.com/-squZd7FxR8Q/UyN5UrsfkqI/AAAAAAAAbAo/VoDHSYAhC_E/s"+size+"/new%2520profile%2520%25282%2529.jpg");
        thumbnail.setErrorResource(R.drawable.ic_launcher);
        addCardThumbnail(thumbnail);
        */
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        TextView title = (TextView) view.findViewById(R.id.card_main_inner_simple_title);
        title.setText("Wish Happy Birthday");
        title.setTextColor(mContext.getResources().getColor(R.color.black));
        title.setGravity(Gravity.CENTER_VERTICAL);

    }


    class GoogleNowBirthThumb extends CardThumbnail {

        public GoogleNowBirthThumb(Context context) {
            super(context);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View viewImage) {
            /*
            viewImage.getLayoutParams().width = 250;
            viewImage.getLayoutParams().height = 250;
            */
        }

        @Override
        public boolean applyBitmap(View imageView, Bitmap bitmap) {
            switch (USE_VIGNETTE){
                case  0:
                    return false;
                default:
                    return false;
            }
        }
    }


    class GroupDetailHeader extends CardHeader {

        String mName;
        String mSubName;

        public GroupDetailHeader(Context context, int innerLayout) {
            super(context, innerLayout);
        }

        @Override
        public void setupInnerViewElements(ViewGroup parent, View view) {

            TextView txName = (TextView) view.findViewById(R.id.text_birth1);
            TextView txSubName = (TextView) view.findViewById(R.id.text_birth2);
            ParseImageView ivGroupImage = (ParseImageView) view.findViewById(R.id.ivGroupImage);
            ParseFile photoFile = currentGroup.getPhotoFile();
            if (photoFile != null) {
            	ivGroupImage.setParseFile(photoFile);
            	ivGroupImage.loadInBackground(new GetDataCallback() {

         		   @Override
         		   public void done(byte[] data, ParseException e) {
                        // nothing to do
         		   }
         	   });
            } else {
         	   ivGroupImage.setImageResource(android.R.color.transparent);
            }
            txName.setText(mName);
            txSubName.setText(mSubName);
        }
    }
}
