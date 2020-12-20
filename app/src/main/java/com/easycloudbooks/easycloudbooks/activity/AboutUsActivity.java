package com.easycloudbooks.easycloudbooks.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.easycloudbooks.easycloudbooks.BuildConfig;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.common.ActivityBase;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.fragment.AboutUsFragment;
import com.easycloudbooks.easycloudbooks.widget.AboutUs_Element;

import java.util.Calendar;


public class AboutUsActivity extends ActivityBase  implements AboutUsFragment.OnCompleteFragment{

    Toolbar mToolbar;

    AboutUsFragment fragment;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        mContext = getApplicationContext();

       mToolbar = (Toolbar) findViewById(R.id.aboutustoolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (savedInstanceState != null) {

            fragment =(AboutUsFragment) getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

        } else {

            fragment =  AboutUsFragment.newInstance();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.aboutus_content, fragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }
    private LayoutInflater mInflater;
    private View mView;
    private int mImage = 0;
    private boolean mIsRTL = false;
    private String mDescription;
    private Typeface mCustomFont;
    public void OnCompleteFragment(View mView) {
        this.mView = mView;
        this.mInflater = LayoutInflater.from(mContext);
        try {
            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            addItem(new AboutUs_Element().setTitle("Version " + versionName + " (" + versionCode + ")"));
        } catch (Exception ex) {
            DataGlobal.SaveLog(TAG, ex, mContext);
        }
        addGroup("Connect with us");
        addEmail("support@easycloudbooks.com", "Contact us");
        addWebsite("https://www.easycloudbooks.com/", "Visit our website");
        addFacebook("easycloudbooks", "Like us on Facebook");
        addTwitter("easycloudbooks", "Follow us on Twitter");
        addYoutube("UCYa4HD20agokMxdM_yqp4EQ", "Watch us on Youtube");
        addPlayStore("com.easycloudbooks.easycloudbooks", "Rate us on the Play Store");
        // addInstagram("medyo80");
        //addGitHub("medyo");
        addItem(getCopyRightsElement());
        TextView description = (TextView) mView.findViewById(R.id.description);
        ImageView image = (ImageView) mView.findViewById(R.id.image);
        if (mImage > 0) {
            image.setImageResource(mImage);
        }

        if (!TextUtils.isEmpty(mDescription)) {
            description.setText(mDescription);
        }

        description.setGravity(Gravity.CENTER);

        if (mCustomFont != null) {
            description.setTypeface(mCustomFont);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.\

        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();
                return true;
            }

            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Log.w(TAG, "onBackPressed: " );
    }

    private AboutUs_Element getCopyRightsElement() {
        AboutUs_Element copyRightsElement = new AboutUs_Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setIconTint(R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutUsActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    public void addEmail(String email, String title) {
        AboutUs_Element emailElement = new AboutUs_Element();
        emailElement.setTitle(title);
        emailElement.setIconDrawable(R.drawable.about_icon_email);
        emailElement.setIconTint(R.color.about_item_icon_color);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailElement.setIntent(intent);

        addItem(emailElement);
    }

    public void addFacebook(String id, String title) {
        AboutUs_Element facebookElement = new AboutUs_Element();
        facebookElement.setTitle(title);
        facebookElement.setIconDrawable(R.drawable.about_icon_facebook);
        facebookElement.setIconTint(R.color.about_facebook_color);
        facebookElement.setValue(id);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        if (isAppInstalled(mContext, "com.facebook.katana")) {
            intent.setPackage("com.facebook.katana");
            int versionCode = 0;
            try {
                versionCode = mContext.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + "http://m.facebook.com/" + id);
                intent.setData(uri);
            } else {
                Uri uri = Uri.parse("fb://page/" + id);
                intent.setData(uri);
            }
        } else {
            intent.setData(Uri.parse("http://m.facebook.com/" + id));
        }

        facebookElement.setIntent(intent);

        addItem(facebookElement);
    }

    public void addTwitter(String id, String title) {
        AboutUs_Element twitterElement = new AboutUs_Element();
        twitterElement.setTitle(title);
        twitterElement.setIconDrawable(R.drawable.about_icon_twitter);
        twitterElement.setIconTint(R.color.about_twitter_color);
        twitterElement.setValue(id);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        if (isAppInstalled(mContext, "com.twitter.android")) {
            intent.setPackage("com.twitter.android");
            intent.setData(Uri.parse(String.format("twitter://user?screen_name=%s", id)));
        } else {
            intent.setData(Uri.parse(String.format("http://twitter.com/intent/user?screen_name=%s", id)));
        }

        twitterElement.setIntent(intent);
        addItem(twitterElement);
    }

    public void addPlayStore(String id, String title) {
        AboutUs_Element playStoreElement = new AboutUs_Element();
        playStoreElement.setTitle(title);
        playStoreElement.setIconDrawable(R.drawable.about_icon_google_play);
        playStoreElement.setIconTint(R.color.about_play_store_color);
        playStoreElement.setValue(id);

        Uri uri = Uri.parse("market://details?id=" + id);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        playStoreElement.setIntent(goToMarket);

        addItem(playStoreElement);
    }

    public void addYoutube(String id, String title) {
        AboutUs_Element youtubeElement = new AboutUs_Element();
        youtubeElement.setTitle(title);
        youtubeElement.setIconDrawable(R.drawable.about_icon_youtube);
        youtubeElement.setIconTint(R.color.about_youtube_color);
        youtubeElement.setValue(id);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(String.format("http://youtube.com/channel/%s", id)));

        if (isAppInstalled(mContext, "com.google.android.youtube")) {
            intent.setPackage("com.google.android.youtube");
        }

        youtubeElement.setIntent(intent);
        addItem(youtubeElement);

    }

    public void addInstagram(String id, String title) {
        AboutUs_Element instagramElement = new AboutUs_Element();
        instagramElement.setTitle(title);
        instagramElement.setIconDrawable(R.drawable.about_icon_instagram);
        instagramElement.setIconTint(R.color.about_instagram_color);
        instagramElement.setValue(id);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://instagram.com/_u/" + id));

        if (isAppInstalled(mContext, "com.instagram.android")) {
            intent.setPackage("com.instagram.android");
        }

        instagramElement.setIntent(intent);
        addItem(instagramElement);

    }

    public void addGitHub(String id, String title) {
        AboutUs_Element gitHubElement = new AboutUs_Element();
        gitHubElement.setTitle(title);
        gitHubElement.setIconDrawable(R.drawable.about_icon_github);
        gitHubElement.setIconTint(R.color.about_github_color);
        gitHubElement.setValue(id);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(String.format("https://github.com/%s", id)));

        gitHubElement.setIntent(intent);
        addItem(gitHubElement);

    }

    public void addWebsite(String url, String title) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        AboutUs_Element websiteElement = new AboutUs_Element();
        websiteElement.setTitle(title);
        websiteElement.setIconDrawable(R.drawable.about_icon_link);
        websiteElement.setIconTint(R.color.about_item_icon_color);
        websiteElement.setValue(url);

        Uri uri = Uri.parse(url);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);

        websiteElement.setIntent(browserIntent);
        addItem(websiteElement);

    }


    public void addGroup(String name) {

        TextView textView = new TextView(mContext);
        textView.setText(name);
        TextViewCompat.setTextAppearance(textView, R.style.about_groupTextAppearance);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (mCustomFont != null) {
            textView.setTypeface(mCustomFont);
        }

        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.about_group_text_padding);
        textView.setPadding(padding, padding, padding, padding);


        if (mIsRTL) {
            textView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            textParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        } else {
            textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            textParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        }
        textView.setLayoutParams(textParams);

        ((LinearLayout) mView.findViewById(R.id.about_providers)).addView(textView);
    }

    public void addItem(AboutUs_Element element) {
        LinearLayout wrapper = (LinearLayout) mView.findViewById(R.id.about_providers);
        wrapper.addView(createItem(element));
        wrapper.addView(getSeparator(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mContext.getResources().getDimensionPixelSize(R.dimen.about_separator_height)));
    }

    private View createItem(final AboutUs_Element element) {
        LinearLayout wrapper = new LinearLayout(mContext);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.setClickable(true);

        if (element.getOnClickListener() != null) {
            wrapper.setOnClickListener(element.getOnClickListener());
        } else if (element.getIntent() != null) {
            wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        mContext.startActivity(element.getIntent());
                    } catch (Exception e) {
                    }
                }
            });

        }

        TypedValue outValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, outValue, true);
        wrapper.setBackgroundResource(outValue.resourceId);

        int padding = mContext.getResources().getDimensionPixelSize(R.dimen.about_text_padding);
        wrapper.setPadding(padding, padding, padding, padding);
        LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapper.setLayoutParams(wrapperParams);


        TextView textView = new TextView(mContext);
        TextViewCompat.setTextAppearance(textView, R.style.about_elementTextAppearance);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(textParams);
        if (mCustomFont != null) {
            textView.setTypeface(mCustomFont);
        }

        ImageView iconView = null;

        if (element.getIconDrawable() != null) {
            iconView = new ImageView(mContext);
            int size = mContext.getResources().getDimensionPixelSize(R.dimen.about_icon_size);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(size, size);
            iconView.setLayoutParams(iconParams);
            int iconPadding = mContext.getResources().getDimensionPixelSize(R.dimen.about_icon_padding);
            iconView.setPadding(iconPadding, 0, iconPadding, 0);

            if (Build.VERSION.SDK_INT < 21) {
                Drawable drawable = VectorDrawableCompat.create(iconView.getResources(), element.getIconDrawable(), iconView.getContext().getTheme());
                iconView.setImageDrawable(drawable);
            } else {
                iconView.setImageResource(element.getIconDrawable());
            }

            Drawable wrappedDrawable = DrawableCompat.wrap(iconView.getDrawable());
            wrappedDrawable = wrappedDrawable.mutate();
            if (element.getAutoApplyIconTint()) {
                int currentNightMode = mContext.getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK;
                if (currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
                    if (element.getIconTint() != null) {
                        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(mContext, element.getIconTint()));
                    } else {
                        DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(mContext, R.color.about_item_icon_color));
                    }
                } else if (element.getIconNightTint() != null) {
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(mContext, element.getIconNightTint()));
                } else {
                    DrawableCompat.setTint(wrappedDrawable, getThemeAccentColor(mContext));
                }
            }

        } else {
            int iconPadding = mContext.getResources().getDimensionPixelSize(R.dimen.about_icon_padding);
            textView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        }


        textView.setText(element.getTitle());


        if (mIsRTL) {

            final int gravity = element.getGravity() != null ? element.getGravity() : Gravity.END;

            wrapper.setGravity(gravity | Gravity.CENTER_VERTICAL);
            //noinspection ResourceType
            textParams.gravity = gravity | Gravity.CENTER_VERTICAL;
            wrapper.addView(textView);
            if (element.getIconDrawable() != null) {
                wrapper.addView(iconView);
            }

        } else {
            final int gravity = element.getGravity() != null ? element.getGravity() : Gravity.START;
            wrapper.setGravity(gravity | Gravity.CENTER_VERTICAL);
            //noinspection ResourceType
            textParams.gravity = gravity | Gravity.CENTER_VERTICAL;
            if (element.getIconDrawable() != null) {
                wrapper.addView(iconView);
            }
            wrapper.addView(textView);
        }

        return wrapper;
    }

    private View getSeparator() {
        return mInflater.inflate(R.layout.about_page_separator, null);
    }

    static Boolean isAppInstalled(Context context, String appName) {
        PackageManager pm = context.getPackageManager();
        boolean installed;
        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    static int getThemeAccentColor(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent;
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = context.getResources().getIdentifier("colorAccent", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }
}
