package com.codepath.groupproject;

import android.app.Application;
import android.content.Context;

import com.codepath.groupproject.models.Group;
import com.codepath.groupproject.models.User;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

public class GroupProjectApplication extends Application {
	private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        GroupProjectApplication.context = this;
        
	    ParseObject.registerSubclass(Group.class);
	    ParseObject.registerSubclass(User.class);
		
		Parse.initialize(this, "BnDnWs760QS13U0gD2cKZYTWXBtGoR0LepOHyKp6", "njtWAdurtwc23F0CDaRbYPL30MJJUhCsH0sRC3H1");
		
		ParseFacebookUtils.initialize(getString(R.string.app_id));
        
		//Toast.makeText(getApplicationContext(), "First thing.", Toast.LENGTH_SHORT).show();

		
		//Create a test object to test Parse
		//ParseObject testObject = new ParseObject("TestObject");
		//testObject.put("foo", "bar");
		//testObject.saveInBackground();
    }


}
