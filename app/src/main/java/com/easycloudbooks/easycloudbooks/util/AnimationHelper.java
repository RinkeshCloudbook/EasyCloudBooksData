package com.easycloudbooks.easycloudbooks.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.easycloudbooks.easycloudbooks.font.FButton;

/**
 * Created by Mitul on 03-08-2017.
 */

public class AnimationHelper {

    public static void Hide(final View view) {
        view.animate()
                .alpha(0.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }

    public static void scale(final View view, boolean Show) {
        Animation lastAnimation = view.getAnimation();
        if (lastAnimation == null || lastAnimation.hasEnded()) {
            Log.d("AnimationHelper",lastAnimation == null ? "true" : "false");
            Log.d("AnimationHelper",lastAnimation != null ? (lastAnimation.hasEnded() ? "true" : "false") : "none");
            Float startScale = view.getScaleX();
            Float endScale = 1.0f;
            if (!Show) {
                endScale = 0.0f;
            }
            if ((view.getVisibility() == View.GONE) == Show) {
                view.animate()
                        .scaleXBy(startScale)
                        .scaleYBy(startScale)
                        .scaleX(endScale)
                        .scaleY(endScale)
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                if (view.getVisibility() == View.GONE)
                                    view.setVisibility(View.VISIBLE);
                                else
                                    view.setVisibility(View.GONE);
                            }
                        });
            }
        }
    }

    public static void Show(final View view) {
        view.animate()
                .alpha(1.0f)
                .setDuration(300)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }

    // To animate view slide out from left to right
    public void slideToRight(final View view) {
        TranslateAnimation animate = new TranslateAnimation(0, view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
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
        view.startAnimation(animate);
    }

    // To animate view slide out from right to left
    public void slideToLeft(final View view) {
        TranslateAnimation animate = new TranslateAnimation(0, -view.getWidth(), 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
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
        view.startAnimation(animate);
    }

    // To animate view slide out from top to bottom
    public void slideToBottom(final View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
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
        view.startAnimation(animate);
    }

    // To animate view slide out from bottom to top
    public void slideToTop(final View view) {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, -view.getHeight());
        animate.setDuration(500);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
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
        view.startAnimation(animate);
    }
}
