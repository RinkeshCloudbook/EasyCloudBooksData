package com.easycloudbooks.easycloudbooks.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Application;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.easycloudbooks.easycloudbooks.animation.ECBBounceInterpolator;
import com.easycloudbooks.easycloudbooks.R;

public class ECB extends Application {
    public static void setVisibility(boolean visible, final View view) {
        setVisibility(visible, view, "short");
    }

    public static void setVisibility(boolean visible, final View view, String type) {
        if (visible && view.getVisibility() == View.GONE) {
            Animation myAnim = null;
            view.setVisibility(View.VISIBLE);
            if (type.equals("long")) {
                myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce_in_long);
                ECBBounceInterpolator interpolator = new ECBBounceInterpolator(0.2, 20);
                myAnim.setInterpolator(interpolator);
            } else {
                myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce_in);
            }
            view.startAnimation(myAnim);
        } else if (!visible && view.getVisibility() == View.VISIBLE) {
            final Animation myAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.bounce_out);
            view.startAnimation(myAnim);
            myAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
}
