package com.easycloudbooks.easycloudbooks.font;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import androidx.annotation.Dimension;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton ;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easycloudbooks.easycloudbooks.R;

/**
 * Created by hoang8f on 5/5/14.
 */

public class FButton extends RelativeLayout implements View.OnTouchListener {
    //Custom values
    private boolean isShadowEnabled = true;
    public TextView FAIconView;
    public String FAIcon;
    public TextView ButtonTextView;
    public String ButtonText;
    private int mButtonColor;
    private int mShadowColor;
    private int mFAIconColor;
    private int mShadowHeight;
    private int mCornerRadius;
    private int FAIconMarginTop,FAIconMarginLeft;
    private float FAIconSize;
    private String FAIconSizeType;
    //Native values
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mPaddingBottom;
    //Background drawable
    private Drawable pressedDrawable;
    private Drawable unpressedDrawable;
    LayoutInflater mInflater;

    boolean isShadowColorDefined = false;
    Context context ;
    public FButton(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        this.context = context;
        init();
        BindData();
        this.setOnTouchListener(this);
    }

    public FButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        this.context = context;
        init();
        parseAttrs(context, attrs);
        BindData();
        this.setOnTouchListener(this);
    }

    public static interface OnClickButton {
        public abstract void OnClickButton(View mView);
    }

    private OnClickButton mListener;

    public FButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        init();
        parseAttrs(context, attrs);
        BindData();
        this.setOnTouchListener(this);
    }

    public void BindData() {
        //FAIconLayout = (RelativeLayout)mView.findViewById(R.id.FAIconLayout);
        FAIconView = (TextView) mView.findViewById(R.id.FAIcon);
        ButtonTextView = (TextView) mView.findViewById(R.id.ButtonText);
        ButtonTextView.setText(ButtonText);
        FAIconView.setText(FAIcon);
        RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        float d = getResources().getDisplayMetrics().density;
        int marginLeft = (int) (10 * d);
        int marginTop = (int) (FAIconMarginTop * d);
        llp.setMargins(marginLeft, marginTop, 0, 0);
        FAIconView.setLayoutParams(llp);
        FAIconView.setTextColor(mFAIconColor);
        //FAIconView.setPadding(0,marginTop,0,0);
        // float IconSize = (float) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, FAIconSize, getResources().getDisplayMetrics());
        if (FAIconSizeType != null && FAIconSizeType.equals("dp"))
            FAIconView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, FAIconSize);
        else
            FAIconView.setTextSize(TypedValue.COMPLEX_UNIT_SP, FAIconSize);
    }

    public void SetText(String Text)
    {
        FAIcon = Text;
        FAIconView.setText(FAIcon);
    }

    public void StartAnimation(Animation anim)
    {
        FAIconView.startAnimation(anim);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //Update background color
        refresh();
    }

    public boolean IsUp = true;


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                IsUp = false;
                updateBackground(pressedDrawable);
                this.setPadding(mPaddingLeft, mPaddingTop + mShadowHeight, mPaddingRight, mPaddingBottom);
                break;
            case MotionEvent.ACTION_MOVE:
                Rect r = new Rect();
                view.getLocalVisibleRect(r);
                if (!r.contains((int) motionEvent.getX(), (int) motionEvent.getY() + 3 * mShadowHeight) &&
                        !r.contains((int) motionEvent.getX(), (int) motionEvent.getY() - 3 * mShadowHeight)) {
                    updateBackground(unpressedDrawable);
                    this.setPadding(mPaddingLeft, mPaddingTop + mShadowHeight, mPaddingRight, mPaddingBottom + mShadowHeight);
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                updateBackground(unpressedDrawable);
                this.setPadding(mPaddingLeft, mPaddingTop + mShadowHeight, mPaddingRight, mPaddingBottom + mShadowHeight);
                break;
        }
        return false;
    }

    public View mView;

    private void init() {
        //Init default values
        isShadowEnabled = true;
        mView = mInflater.inflate(R.layout.ecbbutton, this, true);
        Resources resources = getResources();
        if (resources == null) return;
        mButtonColor = resources.getColor(R.color.colorAccent);
       // mButtonColor = resources.getColor(R.color.fbutton_default_color);
        mFAIconColor = resources.getColor(R.color.colorAccent);
       // mFAIconColor = resources.getColor(R.color.fbutton_default_color);
        mShadowColor = resources.getColor(R.color.colorAccent);
        //mShadowColor = resources.getColor(R.color.fbutton_default_shadow_color);
        mShadowHeight = resources.getDimensionPixelSize(R.dimen.fbutton_default_shadow_height);
        mCornerRadius = resources.getDimensionPixelSize(R.dimen.fbutton_default_conner_radius);
        FAIconMarginTop = resources.getDimensionPixelSize(R.dimen.fbutton_default_fa_margin_top);
        FAIconMarginLeft = resources.getDimensionPixelSize(R.dimen.fbutton_default_fa_margin_left);
        FAIconSize = resources.getInteger(R.integer.fbutton_default_fa_size);
        FAIconSizeType = "sp";
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        //Load from custom attributes
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FButton);
        if (typedArray == null) return;
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.FButton_shadowEnabled) {
                isShadowEnabled = typedArray.getBoolean(attr, true); //Default is true
                //isShadowEnabled = typedArray.getBoolean(attr, true); //Default is true
            } else if (attr == R.styleable.FButton_buttonColor) {
                mButtonColor = typedArray.getColor(attr, ContextCompat.getColor(context, R.color.colorAccent));
                //mButtonColor = typedArray.getColor(attr, ContextCompat.getColor(context, R.color.fbutton_default_color));
            } else if (attr == R.styleable.FButton_FButton_FAIconColor) {
                mFAIconColor = typedArray.getColor(attr, ContextCompat.getColor(context, R.color.colorAccent));
                //mFAIconColor = typedArray.getColor(attr, ContextCompat.getColor(context, R.color.fbutton_default_color));
            } else if (attr == R.styleable.FButton_shadowColor) {

                mShadowColor = typedArray.getColor(attr, ContextCompat.getColor(context, R.color.colorAccent));
               // mShadowColor = typedArray.getColor(attr, ContextCompat.getColor(context, R.color.fbutton_default_shadow_color));
                isShadowColorDefined = true;
            } else if (attr == R.styleable.FButton_shadowHeight) {
                mShadowHeight = typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_shadow_height);
            } else if (attr == R.styleable.FButton_cornerRadius) {
                mCornerRadius = typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_conner_radius);
            } else if (attr == R.styleable.FButton_FButton_FAIconMarginTop) {
                FAIconMarginTop = typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_fa_margin_top);
            } else if (attr == R.styleable.FButton_FButton_FAIconMarginLeft) {
                FAIconMarginLeft = typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_fa_margin_left);
            } else if (attr == R.styleable.FButton_FButton_FAIconSize) {
                FAIconSize = typedArray.getFloat(attr, R.integer.fbutton_default_fa_size);
            }
        }
        ButtonText = typedArray.getString(R.styleable.FButton_FButton_ButtonText);
        FAIconSizeType = typedArray.getString(R.styleable.FButton_FButton_FAIconSizeType);
        FAIcon = typedArray.getString(R.styleable.FButton_FButton_FAIconText);
        typedArray.recycle();

        //Get paddingLeft, paddingRight
        int[] attrsArray = new int[]{
                android.R.attr.paddingLeft,  // 0
                android.R.attr.paddingRight, // 1
        };
        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
        if (ta == null) return;
        mPaddingLeft = ta.getDimensionPixelSize(0, 0);
        mPaddingRight = ta.getDimensionPixelSize(1, 0);
        ta.recycle();

        //Get paddingTop, paddingBottom
        int[] attrsArray2 = new int[]{
                android.R.attr.paddingTop,   // 0
                android.R.attr.paddingBottom,// 1
        };
        TypedArray ta1 = context.obtainStyledAttributes(attrs, attrsArray2);
        if (ta1 == null) return;
        mPaddingTop = ta1.getDimensionPixelSize(0, 0);
        mPaddingBottom = ta1.getDimensionPixelSize(1, 0);
        ta1.recycle();
    }

    public void refresh() {
        int alpha = Color.alpha(mButtonColor);
        float[] hsv = new float[3];
        Color.colorToHSV(mButtonColor, hsv);
        hsv[2] *= 0.8f; // value component
        //if shadow color was not defined, generate shadow color = 80% brightness
        if (!isShadowColorDefined) {
           // mShadowColor = Color.HSVToColor(alpha, hsv);
        }
        //Create pressed background and unpressed background drawables

        if (this.isEnabled()) {
            if (isShadowEnabled) {
                pressedDrawable = createDrawable(mCornerRadius, Color.TRANSPARENT, mButtonColor);
                unpressedDrawable = createDrawable(mCornerRadius, mButtonColor, mShadowColor);
            } else {
                mShadowHeight = 0;
                pressedDrawable = createDrawable(mCornerRadius, mShadowColor, Color.TRANSPARENT);
                unpressedDrawable = createDrawable(mCornerRadius, mButtonColor, Color.TRANSPARENT);
            }
        } else {
            Color.colorToHSV(mButtonColor, hsv);
            hsv[1] *= 0.25f; // saturation component
          //  int disabledColor = mShadowColor = Color.HSVToColor(alpha, hsv);
            // Disabled button does not have shadow
         //   pressedDrawable = createDrawable(mCornerRadius, disabledColor, Color.TRANSPARENT);
          //  unpressedDrawable = createDrawable(mCornerRadius, disabledColor, Color.TRANSPARENT);
        }
        updateBackground(unpressedDrawable);
        //Set padding
        this.setPadding(mPaddingLeft, mPaddingTop + mShadowHeight, mPaddingRight, mPaddingBottom + mShadowHeight);
    }

    private void updateBackground(Drawable background) {
        if (background == null) return;
        //Set button background
        if (Build.VERSION.SDK_INT >= 16) {
            this.setBackground(background);
        } else {
            this.setBackgroundDrawable(background);
        }
    }

    private LayerDrawable createDrawable(int radius, int topColor, int bottomColor) {

        float[] outerRadius = new float[]{radius, radius, radius, radius, radius, radius, radius, radius};

        //Top
        RoundRectShape topRoundRect = new RoundRectShape(outerRadius, null, null);
        ShapeDrawable topShapeDrawable = new ShapeDrawable(topRoundRect);
        topShapeDrawable.getPaint().setColor(topColor);
        //Bottom
        RoundRectShape roundRectShape = new RoundRectShape(outerRadius, null, null);
        ShapeDrawable bottomShapeDrawable = new ShapeDrawable(roundRectShape);
        bottomShapeDrawable.getPaint().setColor(bottomColor);
        //Create array
        Drawable[] drawArray = {bottomShapeDrawable, topShapeDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(drawArray);

        //Set shadow height
        if (isShadowEnabled && topColor != Color.TRANSPARENT) {
            //unpressed drawable
            layerDrawable.setLayerInset(0, 0, 0, 0, 0);  /*index, left, top, right, bottom*/
        } else {
            //pressed drawable
            layerDrawable.setLayerInset(0, 0, mShadowHeight, 0, 0);  /*index, left, top, right, bottom*/
        }
        layerDrawable.setLayerInset(1, 0, 0, 0, mShadowHeight);  /*index, left, top, right, bottom*/

        return layerDrawable;
    }

    //Setter
    public void setShadowEnabled(boolean isShadowEnabled) {
        this.isShadowEnabled = isShadowEnabled;
        setShadowHeight(0);
        refresh();
    }

    public void setButtonColor(int buttonColor) {
        this.mButtonColor = buttonColor;
        refresh();
    }

    public void setShadowColor(int shadowColor) {
        this.mShadowColor = shadowColor;
        isShadowColorDefined = true;
        refresh();
    }

    public void setShadowHeight(int shadowHeight) {
        this.mShadowHeight = shadowHeight;
        refresh();
    }

    public void setCornerRadius(int cornerRadius) {
        this.mCornerRadius = cornerRadius;
        refresh();
    }

    public void setFButtonPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingRight = right;
        mPaddingTop = top;
        mPaddingBottom = bottom;
        refresh();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        refresh();
    }

    //Getter
    public boolean isShadowEnabled() {
        return isShadowEnabled;
    }

    public int getButtonColor() {
        return mButtonColor;
    }

    public int getShadowColor() {
        return mShadowColor;
    }

    public int getShadowHeight() {
        return mShadowHeight;
    }

    public int getCornerRadius() {
        return mCornerRadius;
    }
}
