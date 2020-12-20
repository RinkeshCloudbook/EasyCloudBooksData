package com.easycloudbooks.easycloudbooks.font;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.easycloudbooks.easycloudbooks.R;

public class FontAwesomeTextView extends androidx.appcompat.widget.AppCompatTextView  {
	public FontAwesomeTextView(Context context) {
		super(context);
		if (isInEditMode()) return;
		parseAttributes(null);
	}

	public FontAwesomeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) return;
		parseAttributes(attrs);
	}

	public FontAwesomeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (isInEditMode()) return;
		parseAttributes(attrs);
	}

	private void parseAttributes(AttributeSet attrs) {
		setTypeface(getRoboto());
	}

	public void setRobotoTypeface() {
		setTypeface(getRoboto());
	}

	private Typeface getRoboto() {
		return getRoboto(getContext());
	}

	public static Typeface getRoboto(Context context) {
		return Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");
	}

}
