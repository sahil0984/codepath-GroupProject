package com.codepath.groupproject;

import android.app.Application;
import android.content.Context;

import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.Installation;
import com.codepath.groupproject.models.User;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class GroupProjectApplication extends Application {
	private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        GroupProjectApplication.context = this;
        
		//Parse.initialize(this, "E6dfGnFJaoyFAfHzCZsv615xfwUehoX5mhyDLfac", "eiP1T45E7qEqWYkrZ7aPa1gXVRIxonyhVeRkNUD3");

	    ParseObject.registerSubclass(User.class);
	    ParseObject.registerSubclass(Group.class);
	    ParseObject.registerSubclass(Installation.class);
		

		Parse.initialize(this, "BnDnWs760QS13U0gD2cKZYTWXBtGoR0LepOHyKp6", "njtWAdurtwc23F0CDaRbYPL30MJJUhCsH0sRC3H1");

		ParseFacebookUtils.initialize(getString(R.string.app_id));

		
		PushService.setDefaultPushCallback(this, HomeActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();
        
		//Toast.makeText(getApplicationContext(), "First thing.", Toast.LENGTH_SHORT).show();

		
		//Create a test object to test Parse
		//ParseObject testObject = new ParseObject("TestObject");
		//testObject.put("foo", "bar");
		//testObject.saveInBackground();
    }


}
