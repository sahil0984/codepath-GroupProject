package com.codepath.rideso;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MyFragment extends android.support.v4.app.Fragment {

	protected void onAnimationStarted () {}

	protected void onAnimationEnded () {}

	protected void onAnimationRepeated () {}

	@Override
	public Animation onCreateAnimation (int transit, boolean enter, int nextAnim) {
	    //Check if the superclass already created the animation
	    Animation anim = super.onCreateAnimation(transit, enter, nextAnim);

	    //If not, and an animation is defined, load it now
	    if (anim == null && nextAnim != 0) {
	        anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
	    }

	    //If there is an animation for this fragment, add a listener.
	    if (anim != null) {
	        anim.setAnimationListener(new Animation.AnimationListener() {
	            @Override
	            public void onAnimationStart (Animation animation) {
	                onAnimationStarted();
	            }

	            @Override
	            public void onAnimationEnd (Animation animation) {
	                onAnimationEnded();
	            }

	            @Override
	            public void onAnimationRepeat (Animation animation) {
	                onAnimationRepeated();
	            }
	        });
	    }

	    return anim;
	}
}