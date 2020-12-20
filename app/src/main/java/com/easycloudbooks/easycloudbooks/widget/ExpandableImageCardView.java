package com.easycloudbooks.easycloudbooks.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.util.UIUtils;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 *
 * Copyright (c) 2018 Alessandro Sperotti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Created by alessandros on 23/02/2018.
 * @author Alessandro Sperotti
 */

public class ExpandableImageCardView extends LinearLayout {

    public static String TAG = ExpandableImageCardView.class.getSimpleName();
    private String title;
    private String subtitle;

    private View innerView;
    private ViewGroup containerView;

    private ImageButton arrowBtn;
    private ImageView imageView;
    private TextView textViewTitle;
    private TextView textViewSubTitle;

    private TypedArray typedArray;
    private int innerViewRes;
    private String imageDrawable;

    private CardView card;

    public static final int DEFAULT_ANIM_DURATION = 350;
    private long animDuration = DEFAULT_ANIM_DURATION;

    private final static int COLLAPSING = 0;
    private final static int EXPANDING = 1;

    private boolean isExpanded = false;
    private boolean isExpanding = false;
    private boolean isCollapsing = false;
    private boolean expandOnClick = false;
    private boolean startExpanded = false;
    private Context mContext;

    private int previousHeight = 0;
    private int minHeight = 0;

    private OnExpandedListener listener;

    private OnClickListener defaultClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.w(TAG, "onClick: ");
            if(isExpanded()) collapse();
            else expand();
        }
    };

    public ExpandableImageCardView(Context context) {
        super(context);
    }

    public ExpandableImageCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initAttributes(context, attrs);
        initView(context);
    }

    public ExpandableImageCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initAttributes(context, attrs);
        initView(context);
    }

    private void initView(Context context){
        //Inflating View
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_imagecardview, this);
    }

    private void initAttributes(Context context, AttributeSet attrs){
        //Ottengo attributi
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableImageCardView);
        title = typedArray.getString(R.styleable.ExpandableImageCardView_title1);
        subtitle = typedArray.getString(R.styleable.ExpandableImageCardView_subtitle);
        imageDrawable = typedArray.getString(R.styleable.ExpandableImageCardView_image);
        innerViewRes = typedArray.getResourceId(R.styleable.ExpandableImageCardView_inner_view1, View.NO_ID);
        expandOnClick = typedArray.getBoolean(R.styleable.ExpandableImageCardView_expandOnClick1, false);
        animDuration = typedArray.getInteger(R.styleable.ExpandableImageCardView_animationDuration1, DEFAULT_ANIM_DURATION);
        startExpanded = typedArray.getBoolean(R.styleable.ExpandableImageCardView_startExpanded1, false);
        float minHeightf = typedArray.getFloat(R.styleable.ExpandableImageCardView_minHeight1, 60);
        minHeight = (int)UIUtils.convertDpToPixels(context,minHeightf);
        typedArray.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        arrowBtn = findViewById(R.id.card_arrow);
        textViewTitle = findViewById(R.id.card_title);
        textViewSubTitle = findViewById(R.id.card_sub_title);
        imageView = findViewById(R.id.card_image);

        //Setting attributes
        if(!TextUtils.isEmpty(title)) textViewTitle.setText(title);
        if(!TextUtils.isEmpty(subtitle)) textViewSubTitle.setText(subtitle);

          if (imageDrawable != null && !TextUtils.isEmpty(imageDrawable)) {
            Glide.with(mContext).load(imageDrawable)
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply( new RequestOptions().circleCropTransform()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
              imageView.setColorFilter(null);
        } else {
              imageView.setImageResource(R.drawable.bg_circle);
              imageView.setColorFilter(null);
        }

        card = findViewById(R.id.card_layout);

        setInnerView(innerViewRes);

        containerView = findViewById(R.id.card_container);

        setElevation(UIUtils.convertDpToPixels(getContext(), 4));

        if(startExpanded){
            setAnimDuration(0);
            expand();
            setAnimDuration(animDuration);
        }
        if(expandOnClick){
            card.setOnClickListener(defaultClickListener);
            arrowBtn.setOnClickListener(defaultClickListener);
        }
    }

    public void expand() {

        final int initialHeight = card.getHeight();

        if(!isMoving()) {
            previousHeight = initialHeight;
        }

        card.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int targetHeight = card.getMeasuredHeight();

        if(targetHeight - initialHeight != 0) {
            animateViews(initialHeight,
                    targetHeight - initialHeight,
                    EXPANDING,false);
        }
    }

    public void expandfast() {

        final int initialHeight = card.getHeight();

        if(!isMoving()) {
            previousHeight = initialHeight;
        }

        card.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int targetHeight = card.getMeasuredHeight();

        if(targetHeight - initialHeight != 0) {
            animateViews(initialHeight,
                    targetHeight - initialHeight,
                    EXPANDING,true);
        }
    }

    public void collapsefast() {
        int initialHeight = card.getMeasuredHeight();
        if(previousHeight == 0) {
            if (initialHeight - previousHeight != 0) {
                animateViews(initialHeight,
                        (initialHeight  - minHeight),
                        COLLAPSING,true);
            }
        }
        else {
            if (initialHeight - previousHeight != 0) {
                animateViews(initialHeight,
                        (initialHeight - previousHeight),
                        COLLAPSING,true);
            }
        }

    }
    public void collapse() {
        int initialHeight = card.getMeasuredHeight();
        Log.w(TAG, "collapse: "+initialHeight );
        if(previousHeight == 0) {
            if (initialHeight - previousHeight != 0) {
                animateViews(initialHeight,
                        (initialHeight  - minHeight),
                        COLLAPSING,false);
            }
        }
        else {
            if (initialHeight - previousHeight != 0) {
                animateViews(initialHeight,
                        (initialHeight - previousHeight),
                        COLLAPSING,false);
            }
        }

    }

    public boolean isExpanded() {
        return isExpanded;
    }

    private void animateViews(final int initialHeight, final int distance, final int animationType, boolean Initialize){
        Long animDuration1 = animDuration;
        if(Initialize)
        {
            animDuration1 = 0L;
        }
        Animation expandAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1){
                    //Setting isExpanding/isCollapsing to false
                    isExpanding = false;
                    isCollapsing = false;

                    if(listener != null){
                        if(animationType == EXPANDING){
                            listener.onExpandChanged(card,true);
                        }
                        else{
                            listener.onExpandChanged(card,false);
                        }
                    }
                }

                card.getLayoutParams().height = animationType == EXPANDING ? (int) (initialHeight + (distance * interpolatedTime)) :
                        (int) (initialHeight  - (distance * interpolatedTime));
                card.findViewById(R.id.card_container).requestLayout();

                containerView.getLayoutParams().height = animationType == EXPANDING ? (int) (initialHeight + (distance * interpolatedTime)) :
                        (int) (initialHeight  - (distance * interpolatedTime));

            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        RotateAnimation arrowAnimation = animationType == EXPANDING ?
                new RotateAnimation(0,180,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f) :
                new RotateAnimation(180,0,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);

        arrowAnimation.setFillAfter(true);


        arrowAnimation.setDuration(animDuration1);
        expandAnimation.setDuration(animDuration1);

        isExpanding = animationType == EXPANDING;
        isCollapsing = animationType == COLLAPSING;

        startAnimation(expandAnimation);
        Log.d("SO","Started animation: "+ (animationType == EXPANDING ? "Expanding" : "Collapsing"));
        arrowBtn.startAnimation(arrowAnimation);
        isExpanded = animationType == EXPANDING;

    }

    private boolean isExpanding(){
        return isExpanding;
    }

    private boolean isCollapsing(){
        return isCollapsing;
    }

    private boolean isMoving(){
        return isExpanding() || isCollapsing();
    }

    public void setOnExpandedListener(OnExpandedListener listener) {
        this.listener = listener;
    }

    public void removeOnExpandedListener(){
        this.listener = null;
    }

    public void setTitle(String title){
        if(textViewTitle != null) textViewTitle.setText(title);
    }

    public void setTitle(int resId){
        if(textViewTitle != null) textViewTitle.setText(resId);
    }

    public void setImage(String image){
        if (image != null && !TextUtils.isEmpty(image)) {
            Glide.with(mContext).load(image)
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply( new RequestOptions().circleCropTransform()
                            .diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
            imageView.setColorFilter(null);
        } else {
            imageView.setImageResource(R.drawable.bg_circle);
            imageView.setColorFilter(null);
        }
    }


    private void setInnerView(int resId){
        ViewStub stub = findViewById(R.id.card_stub);
        stub.setLayoutResource(resId);
        innerView = stub.inflate();
    }


    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        if(arrowBtn != null) arrowBtn.setOnClickListener(l);
        super.setOnClickListener(l);
    }

    public long getAnimDuration() {
        return animDuration;
    }

    public void setAnimDuration(long animDuration) {
        this.animDuration = animDuration;
    }


    /**
     * Interfaces
     */

    public interface OnExpandedListener {

        void onExpandChanged(View v, boolean isExpanded);

    }

}


