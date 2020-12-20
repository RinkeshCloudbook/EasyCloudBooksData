package com.easycloudbooks.easycloudbooks.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;

import androidx.core.content.ContextCompat;
import androidx.core.text.TextUtilsCompat;
import android.util.Log;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.easycloudbooks.easycloudbooks.BuildConfig;
import com.easycloudbooks.easycloudbooks.activity.CameraCapture;
import com.easycloudbooks.easycloudbooks.activity.MainActivity;
import com.easycloudbooks.easycloudbooks.activity.OfflineActivity;
import com.easycloudbooks.easycloudbooks.activity.SplashActivity;
import com.easycloudbooks.easycloudbooks.activity.UpdateActivity;
import com.easycloudbooks.easycloudbooks.app.Config;
import com.easycloudbooks.easycloudbooks.app.ECBGetCurrentUser;
import com.easycloudbooks.easycloudbooks.common.DataDateTime;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.DataText;
import com.easycloudbooks.easycloudbooks.common.SweetAlertCustom;
import com.easycloudbooks.easycloudbooks.font.FontAwesomeTextView;
import com.easycloudbooks.easycloudbooks.fragment.ContactFragment;
import com.easycloudbooks.easycloudbooks.model.*;
import com.easycloudbooks.easycloudbooks.structure.DrawerProfile;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.easycloudbooks.easycloudbooks.widget.LinearListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.pkmmte.view.CircularImageView;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.app.App;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.LAYOUT_DIRECTION_RTL;
import static android.view.View.VISIBLE;

public class NavDrawerAdapter extends RecyclerView.Adapter<NavDrawerAdapter.ViewHolder> {

    private static String TAG = NavDrawerAdapter.class.getSimpleName();
    String[] titles;
    TypedArray icons;
    Context context;
    NavDrawerAdapter _this ;
    ImageLoader imageLoader = App.getInstance().getImageLoader();
    String random = "";

    Activity calledActivity ;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    // The default constructor to receive titles,icons and context from MainActivity.
    public NavDrawerAdapter(String[] titles, TypedArray icons, Context context, Activity activity) {

        this.titles = titles;
        this.icons = icons;
        this.context = context;
        this.random = Double.toString(Math.random());
        this.calledActivity =activity;
        //Log.w(TAG, "ChangeContextCreate: "+context.toString()+ "-"+random );

        _this = this;
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
              //  Log.w(TAG, "onReceive: "+intent.getAction() );
                if ( intent.getAction().equals(Config.ACID_CHANGED) || intent.getAction().equals(Config.UPDATE_UI)) {
                    GetCurrentUser(false);
                }
            }
        };
        LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.ACID_CHANGED));

        LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.UPDATE_UI));

    }

    public void ChangeContext(Context newcontext)
    {
       // Log.w(TAG, "ChangeContext: "+newcontext.toString() );
        this.context = newcontext;

    }
    public Context GetLastestContext()
    {
        //Log.w(TAG, "GetLastestContext: "+this.context.toString() + "-"+random);
        return  this.context;

    }

    /**
     * Its a inner class to NavDrawerAdapter Class.
     * This ViewHolder class implements View.OnClickListener to handle click events.
     * If the itemType==1 ; it implies that the view is a single row_item with TextView and ImageView.
     * This ViewHolder describes an item view with respect to its place within the RecyclerView.
     * For every item there is a ViewHolder associated with it .
     */

    protected ACJM CurrentAC;
    private DrawerProfileAdapter mProfileAdapter;
    private LinearListView linearListViewProfileList;
    private TextView textViewProfileName;
    private ImageView imageViewOpenProfileListIcon;
    private TextView textViewProfileDescription;
    private boolean profileListOpen = false;
    private LinearListView linearListView;
    private FrameLayout mdFrameLayout;
    private LinearLayout linearLayoutProfileTextContainer,mdProfileTextContainer_Parent;

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout navLayout, navDescLayout;
        TextView navTitle, navCounter, userFullname, userUsername, userRoleName, navItemName, navItemCount;
        CircularImageView userPhoto;
        FontAwesomeTextView navIcon;
        ImageView userCover;
        Context vcontext;

        public ViewHolder(View drawerItem, int itemType, final Context context) {

            super(drawerItem);

            this.vcontext = context;

            //database = FirebaseDatabase.getInstance();
            if (itemType == 1 || itemType == 2) {
                navLayout = (LinearLayout) itemView.findViewById(R.id.iv_NavLayout);
                navCounter = (TextView) itemView.findViewById(R.id.tv_NavCounter);
                navTitle = (TextView) itemView.findViewById(R.id.tv_NavTitle);
                navIcon = (FontAwesomeTextView) itemView.findViewById(R.id.iv_NavIcon);
                navItemName = (TextView) itemView.findViewById(R.id.name_of_item);
                navItemCount = (TextView) itemView.findViewById(R.id.number_of_item);
                navDescLayout = (LinearLayout) itemView.findViewById(R.id.DescLayout);
                if (itemType == 2)
                    navLayout.setBackgroundColor(ContextCompat.getColor(vcontext, R.color.main_color_50));
            } else {
                com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
                if (!imageLoader.isInited()) {
                    imageLoader.init(ImageLoaderConfiguration.createDefault(vcontext));
                }
                userPhoto = (CircularImageView) itemView.findViewById(R.id.userPhoto);
                userCover = (ImageView) itemView.findViewById(R.id.userCover);

                userFullname = (TextView) itemView.findViewById(R.id.userFullname);
                userUsername = (TextView) itemView.findViewById(R.id.userUsername);
                userRoleName = (TextView) itemView.findViewById(R.id.RoleName);


                mProfileAdapter = new DrawerProfileAdapter(vcontext, new ArrayList<DrawerProfile>());
                linearListViewProfileList = (LinearListView) itemView.findViewById(R.id.mdProfileList);
                linearListView = (LinearListView) itemView.findViewById(R.id.mdList);
                mdFrameLayout = (FrameLayout) itemView.findViewById(R.id.mdProfileFrameLayout);
                textViewProfileName = (TextView) itemView.findViewById(R.id.mdProfileName);
                textViewProfileDescription = (TextView) itemView.findViewById(R.id.mdProfileDescription);
                imageViewOpenProfileListIcon = (ImageView) itemView.findViewById(R.id.mdOpenProfileListIcon);
                mdProfileTextContainer_Parent = (LinearLayout) itemView.findViewById(R.id.mdProfileTextContainer_Parent);
                linearLayoutProfileTextContainer = (LinearLayout) itemView.findViewById(R.id.linearLayoutProfileTextContainer);
                linearListViewProfileList.setAdapter(mProfileAdapter);
                linearListViewProfileList.setOnItemClickListener(new LinearListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(LinearListView parent, View view, int position, long id) {
                        if (position != 0) {
                            selectProfile(mProfileAdapter.getItem(position), false);
                        }
                    }
                });
                /*userPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("TEST", "Clicked on Photo");
                        Intent intent = new Intent(context, CameraCapture.class);
                        context.startActivity(intent);
                    }
                });*/
                updateListVisibility();
                BindProfiles(vcontext);
            }
        }
    }

    protected void updateUI(FirebaseUser user) {
    }

    private  void BindProfiles(Context vcontext)
    {
        try {
            CurrentAC = App.getInstance().GetCurrentACJson(vcontext);
            List<ACJM> ACs = App.getInstance().GetAllACJson(vcontext);

            if (CurrentAC != null && ACs != null) {

                for (DrawerProfile oldProfile : mProfileAdapter.getItems()) {
                    boolean haveacid = false;
                    for (int i = 0; i < ACs.size(); i++) {
                        ACJM obj = ACs.get(i);
                        if(obj.ACId == oldProfile.getId())
                        {
                            haveacid = true;
                            break;
                        }
                    }
                    if(!haveacid)
                    {
                        mProfileAdapter.remove(oldProfile);
                    }
                }


                long CurrentACId = CurrentAC.ACId;
                for (int i = 0; i < ACs.size(); i++) {
                    ACJM obj = ACs.get(i);
                    if (obj != null) {

                        long AccountantCompanyId = obj.ACId;
                        if (AccountantCompanyId > 0 && AccountantCompanyId == CurrentACId) {
                            String Image = DataText.GetImagePath(obj.I);
                            // JSONObject Accountant = obj.getJSONObject("Accountant");
                            //JSONObject RoleJ = Accountant.getJSONObject("RoleJ");
                            if (Image == null || Image.equals("") || Image.equals("null"))
                                Image = Config.CompanyImage;
                            addProfile(new DrawerProfile()
                                    .setId(AccountantCompanyId)
                                    .setRoundedAvatar(Image)
                                    .setName(obj.N)
                                    .setDescription(obj.R)
                            );
                            break;
                        }
                    }
                }
                for (int i = 0; i < ACs.size(); i++) {
                    ACJM obj = ACs.get(i);
                    if (obj != null) {
                        long AccountantCompanyId = obj.ACId;
                        if (AccountantCompanyId > 0 && AccountantCompanyId != CurrentACId) {
                            String Image = obj.I;
                            // JSONObject Accountant = obj.getJSONObject("Accountant");
                            //JSONObject RoleJ = Accountant.getJSONObject("RoleJ");
                            if (Image == null || Image.equals("") || Image.equals("null"))
                                Image = Config.CompanyImage;
                            addProfile(new DrawerProfile()
                                    .setId(AccountantCompanyId)
                                    .setRoundedAvatar(Image)
                                    .setName(obj.N)
                                    .setDescription(obj.R)
                            );
                        }
                    }
                }
            } else {
                for (DrawerProfile oldProfile : mProfileAdapter.getItems()) {
                        mProfileAdapter.remove(oldProfile);
                }
                //No Companies
                addProfile(new DrawerProfile()
                        .setId(0)
                        .setRoundedAvatar(Config.CompanyImage)
                        .setName("No Company")
                        .setDescription("")
                );
            }
            imageViewOpenProfileListIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleProfileList();
                }
            });
            mdProfileTextContainer_Parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleProfileList();
                }
            });


            updateUI(FirebaseAuth.getInstance().getCurrentUser());
            mHandler = new Handler();
        } catch (Exception ex) {
            Log.w(TAG, "ViewHolder: signOut3",ex);
            DataGlobal.SaveLog(TAG, ex, vcontext);
            FirebaseAuth.getInstance().signOut();
            updateUI(null);
        }
    }


    private boolean IsFirstTimeDone = false;

    private  CustomAuthRequest GetCurrentUserRequest;
    private  void  GetCurrentUser( boolean MenuRequired)
    {
        //Log.w(TAG, "GetCurrentUser: called" );
        GetCurrentUserRequest = ECBGetCurrentUser.Get(MenuRequired, new ECBGetCurrentUser.OnECBCurrentUserListner() {
            @Override
            public void onECBCurrentUser(JSONObject response) {
                if(response != null)
                {
                    try {
                        Context mContext = _this.GetLastestContext();
                        //Log.w(TAG, "GetCurrentUser: "+mContext.toString());
                        JSONObject ResultJ = response;
                        //Log.w(TAG, "onResponse: "+response.toString());
                        if (ResultJ.getString("MessageType").equals("success")) {
                            LastCallTime = DataDateTime.Now();
                            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                            String rString = App.getInstance().VerifyUpdateData(context, ResultJ);
                            BindProfiles(context);
                            mProfileAdapter.notifyDataSetChanged();
                            //new MyFirebaseMessagingService().storeUserTokenInPref(context, mDataAuthInfo.FCMReturnToken);
                            //Log.w(TAG, "onResponse: "+rString );
                            if (rString == "success") {
                                Config.IsOldVersion = false;
                                Config.IsOffline = false;
                                updateUI(mUser);
                                selectProfile(App.getInstance().getCurrentACId(),!IsFirstTimeDone);
                                notifyDataSetChanged();
                            } else if (rString == "update") {
                                Config.IsOldVersion = true;
                                updateUI(mUser);
                                Intent intent = new Intent(context, UpdateActivity.class);
                                context.startActivity(intent);
                            }
                            else if (rString == "changeacid") {
                                //_this.notifyDataSetChanged();
                                //mContext.getApplicationContext().finish();
                                SweetAlertCustom.ShowWarningMessage(mContext, "Company Access Revoked", "Consultant revoked your access as Client", new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        Intent intent = new Intent(context, SplashActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        calledActivity.finish();
                                        context.startActivity(intent);
                                    }
                                }, new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialogInterface) {
                                        Intent intent = new Intent(context, SplashActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        calledActivity.finish();
                                        context.startActivity(intent);
                                    }
                                });

                                /*Long ACId = App.getInstance().getCurrentACId();
                                if(ACId > 0)
                                selectProfile(ACId,false);
                               else
                                    selectProfile(ACId,false);*/
                            }
                            else if (rString == "newacid") {
                                _this.notifyDataSetChanged();
                                SweetAlertCustom.ShowSuccessMessage(mContext,  "Consultant given you access as Staff", new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        Intent intent = new Intent(context, SplashActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        calledActivity.finish();
                                        context.startActivity(intent);
                                    }
                                }, new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialogInterface) {
                                        Intent intent = new Intent(context, SplashActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        calledActivity.finish();
                                        context.startActivity(intent);
                                    }
                                });

                                /*Long ACId = App.getInstance().getCurrentACId();
                                if(ACId > 0)
                                selectProfile(ACId,false);
                               else
                                    selectProfile(ACId,false);*/
                            }
                            else if (rString == "offline") {
                                Config.IsOffline = true;
                                updateUI(mUser);
                                Intent intent = new Intent(context, OfflineActivity.class);
                                context.startActivity(intent);
                            } else {
                                FirebaseAuth.getInstance().signOut();
                                updateUI(null);
                            }
                        } else {
                            Log.w(TAG, "onCreate: SignOut5" );
                            FirebaseAuth.getInstance().signOut();
                            updateUI(null);
                        }
                    } catch (JSONException e) {
                        DataGlobal.SaveLog(TAG, e, context);
                    }
                }
                else{

                }
            }
        });

    }




    private void toggleProfileList() {
        if (profileListOpen) {
            closeProfileList();
        } else {
            openProfileList();
        }
    }

    private void openProfileList() {
        if (!profileListOpen) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(linearListView, "alpha", 1, 0f, 0f, 0f),
                    ObjectAnimator.ofFloat(linearListView, "translationY", 0, context.getResources().getDimensionPixelSize(R.dimen.md_list_item_height) / 4),
                    ObjectAnimator.ofFloat(linearListViewProfileList, "alpha", 0, 1),
                    ObjectAnimator.ofFloat(linearListViewProfileList, "translationY", -context.getResources().getDimensionPixelSize(R.dimen.md_list_item_height) / 2, 0),
                    ObjectAnimator.ofInt(imageViewOpenProfileListIcon.getDrawable(), PROPERTY_LEVEL, 0, 10000)
            );
            set.setDuration(context.getResources().getInteger(R.integer.md_profile_list_open_anim_time));
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    linearListViewProfileList.setVisibility(VISIBLE);
                    mdFrameLayout.setVisibility(VISIBLE);

                    imageViewOpenProfileListIcon.setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    imageViewOpenProfileListIcon.setClickable(true);

                    profileListOpen = true;

                    updateListVisibility();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            set.start();
        } else {
            updateListVisibility();
        }
    }


    public void addProfile(DrawerProfile profile) {
        if (profile.getId() <= 0) {
            profile.setId(0);
        }
        for (DrawerProfile oldProfile : mProfileAdapter.getItems()) {
            if (oldProfile.getId() == profile.getId()) {
                mProfileAdapter.remove(oldProfile);
                break;
            }
        }
        //profile.attachTo(this);
        mProfileAdapter.add(profile);
        if (mProfileAdapter.getCount() == 1) {
            selectProfile(profile, true);
        }
        updateProfile();
    }

    private void updateProfile() {
        if (mProfileAdapter.getCount() > 0) {
            if (mProfileAdapter.getItem(0).getName() != null && !mProfileAdapter.getItem(0).getName().equals("")) {
                textViewProfileName.setText(mProfileAdapter.getItem(0).getName());
                App.getInstance().setCurrentACId(mProfileAdapter.getItem(0).getId());
                App.getInstance().saveData();
            }
            if (mProfileAdapter.getItem(0).getDescription() != null && !mProfileAdapter.getItem(0).getDescription().equals("")) {
                textViewProfileDescription.setVisibility(VISIBLE);
                textViewProfileDescription.setText(mProfileAdapter.getItem(0).getDescription());
            } else {
                textViewProfileDescription.setVisibility(GONE);
            }
            updateProfileTheme();
        } else {
            closeProfileList();
        }
    }

    private static final Property<Drawable, Integer> PROPERTY_LEVEL = new Property<Drawable, Integer>(Integer.class, "level") {
        @Override
        public Integer get(Drawable object) {
            return object.getLevel();
        }

        @Override
        public void set(Drawable object, Integer value) {
            object.setLevel(value);
        }
    };

    private void closeProfileList() {
        if (profileListOpen) {
            AnimatorSet set = new AnimatorSet();
            set.playTogether(
                    ObjectAnimator.ofFloat(linearListViewProfileList, "alpha", 1, 0f, 0f, 0f),
                    ObjectAnimator.ofFloat(linearListViewProfileList, "translationY", 0, -context.getResources().getDimensionPixelSize(R.dimen.md_list_item_height) / 4),
                    ObjectAnimator.ofFloat(linearListView, "alpha", 0f, 1),
                    ObjectAnimator.ofFloat(linearListView, "translationY", context.getResources().getDimensionPixelSize(R.dimen.md_list_item_height) / 2, 0),
                    ObjectAnimator.ofInt(imageViewOpenProfileListIcon.getDrawable(), PROPERTY_LEVEL, 10000, 0)
            );
            set.setDuration(context.getResources().getInteger(R.integer.md_profile_list_open_anim_time));
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    linearListView.setVisibility(VISIBLE);
                    imageViewOpenProfileListIcon.setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    imageViewOpenProfileListIcon.setClickable(true);
                    profileListOpen = false;
                    updateListVisibility();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            set.start();
        } else {
            updateListVisibility();
        }
    }

    private void updateListVisibility() {
        if (profileListOpen && mProfileAdapter.getCount() > 0) {
            linearListViewProfileList.setVisibility(VISIBLE);
            mdFrameLayout.setVisibility(VISIBLE);
        } else {
            mdFrameLayout.setVisibility(GONE);
            linearListViewProfileList.setVisibility(GONE);
        }
    }

    public void selectProfile(long ACId, boolean isFirstTime) {
        if (mProfileAdapter.getACs().contains(ACId)) {
            DrawerProfile profile = null;
            for (DrawerProfile oldProfile : mProfileAdapter.getItems()) {
                if (oldProfile.getId() == ACId) {
                    profile =oldProfile;
                    break;
                }
            }
            DrawerProfile oldProfile = mProfileAdapter.getItem(0);
            if (mProfileAdapter.getCount() > 1) {
                closeProfileList();
                animateToProfile(profile, isFirstTime);
                mProfileAdapter.remove(profile);
                mProfileAdapter.insert(profile, 0);
                App.getInstance().setCurrentACId(profile.getId());//setSelectedACId
            } else {
                mProfileAdapter.remove(profile);
                mProfileAdapter.insert(profile, 0);
                App.getInstance().setCurrentACId(profile.getId());
            }
        }
    }

    public void selectProfile(DrawerProfile profile, boolean isFirstTime) {
        if (mProfileAdapter.getItems().contains(profile)) {
            DrawerProfile oldProfile = mProfileAdapter.getItem(0);
            if (mProfileAdapter.getCount() > 1) {
                closeProfileList();
                animateToProfile(profile, isFirstTime);
                mProfileAdapter.remove(profile);
                mProfileAdapter.insert(profile, 0);
                App.getInstance().setCurrentACId(profile.getId());
            } else {
                mProfileAdapter.remove(profile);
                mProfileAdapter.insert(profile, 0);
                App.getInstance().setCurrentACId(profile.getId());
            }
        }
    }

    private Handler mHandler;
    private Runnable mRunnable;

    private void animateToProfile(DrawerProfile profile, final boolean isFirstTime) {
        if (mProfileAdapter.getCount() > 1) {
            List<Animator> animators = new ArrayList<>();
            List<Animator.AnimatorListener> listeners = new ArrayList<>();
            final DrawerProfile oldProfile = mProfileAdapter.getItem(0);
            final DrawerProfile newProfile = profile;
            boolean isRtl = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                    TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == LAYOUT_DIRECTION_RTL;
            int rtlSign = isRtl ? -1 : 1;
            AnimatorSet textSet = new AnimatorSet();
            AnimatorSet textOutSet = new AnimatorSet();
            textOutSet.playTogether(
                    ObjectAnimator.ofFloat(linearLayoutProfileTextContainer, "alpha", 1, 0),
                    ObjectAnimator.ofFloat(linearLayoutProfileTextContainer, "translationX", 0, 200 / 4 * rtlSign)
            );
            textOutSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    updateProfileTheme();
                    if (newProfile.hasName()) {
                        textViewProfileName.setText(newProfile.getName());
                        App.getInstance().setCurrentACId(newProfile.getId());
                        App.getInstance().saveData();
                        textViewProfileName.setVisibility(VISIBLE);
                    } else {
                        textViewProfileName.setVisibility(GONE);
                    }
                    if (newProfile.hasDescription()) {
                        textViewProfileDescription.setText(newProfile.getDescription());
                        textViewProfileDescription.setVisibility(VISIBLE);
                    } else {
                        textViewProfileDescription.setVisibility(GONE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            AnimatorSet textInSet = new AnimatorSet();
            textInSet.playTogether(
                    ObjectAnimator.ofFloat(linearLayoutProfileTextContainer, "alpha", 0, 1),
                    ObjectAnimator.ofFloat(linearLayoutProfileTextContainer, "translationX", -200 / 4 * rtlSign, 0)
            );
            textSet.playSequentially(
                    textOutSet,
                    textInSet
            );
            animators.add(textSet);
            AnimatorSet profileSet = new AnimatorSet();
            linearListViewProfileList.setOnItemClickListener(new LinearListView.OnItemClickListener() {
                @Override
                public void onItemClick(LinearListView parent, View view, int position, long id) {
                    if (position != 0) {
                        selectProfile(mProfileAdapter.getItem(position), false);
                    }
                }
            });
            if (animators.size() > 0) {
                AnimatorSet set = new AnimatorSet();
                set.playTogether(animators);
                set.setDuration(context.getResources().getInteger(R.integer.md_profile_switching_anim_time));
                textSet.setDuration(context.getResources().getInteger(R.integer.md_profile_switching_anim_time) / 2);
                profileSet.setDuration(context.getResources().getInteger(R.integer.md_profile_switching_anim_time) / 2);
                for (Animator.AnimatorListener listener : listeners) {
                    set.addListener(listener);
                }
                set.start();
            }

            mHandler = new Handler();
            mRunnable = new Runnable() {
                public void run() {
                    if (!isFirstTime) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent, DataGlobal.getFadeInActivityAnimation(context));
                    }
                }

            };
            mHandler.postDelayed(mRunnable, 500);

        }
    }

    private void updateProfileTheme() {


    }

    /**
     * This is called every time when we need a new ViewHolder and a new ViewHolder is required for every item in RecyclerView.
     * Then this ViewHolder is passed to onBindViewHolder to display items.
     */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (viewType == 1 || viewType == 2) {

            View itemLayout = layoutInflater.inflate(R.layout.nav_drawer_row, null);
            return new ViewHolder(itemLayout, viewType, context);

        } else if (viewType == 0) {

            View itemHeader = layoutInflater.inflate(R.layout.header_navigation_drawer, null);

            return new ViewHolder(itemHeader, viewType, context);
        }

        return null;
    }

    /**
     * This method is called by RecyclerView.Adapter to display the data at the specified position.
     * This method should update the contents of the itemView to reflect the item at the given position.
     * So here , if position!=0 it implies its a row_item and we set the title and icon of the view.
     */

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (imageLoader == null) {

            imageLoader = App.getInstance().getImageLoader();
        }
        boolean ok = false;
        ACJM CurrentAC = null;
        long ACId = App.getInstance().getCurrentACId();
        if(ACId > 0)
            CurrentAC = App.getInstance().getAC(ACId);

        if (position != 0) {
            String Title = titles[position - 1];
            if (titles[position - 1].equals("Search"))
                ok = true;
            Log.w(TAG, "onBindViewHolder: "+Title );
            switch (Title) {

                case "Notification": {

                    holder.navCounter.setVisibility(View.GONE);
                    if(CurrentAC != null && CurrentAC.NCN > 0)
                    {
                        holder.navCounter.setText(Integer.toString(CurrentAC.NCN));
                        holder.navCounter.setVisibility(View.VISIBLE);
                    }
                    else{
                        holder.navCounter.setVisibility(GONE);
                    }
                    //Log.w(TAG, "onBindViewHolder: "+CurrentAC.NC );
                    if(CurrentAC != null && CurrentAC.NC > 0)
                    {
                        holder.navDescLayout.setVisibility(View.VISIBLE);
                        holder.navItemName.setText("Notifications");
                        holder.navItemCount.setText(String.valueOf(CurrentAC.NC));
                        holder.navItemName.setVisibility(VISIBLE);
                        holder.navItemCount.setVisibility(VISIBLE);
                    }
                    else{
                        holder.navItemName.setVisibility(GONE);
                        holder.navItemCount.setVisibility(GONE);
                    }

                    holder.navTitle.setText(titles[position - 1]);
                    holder.navIcon.setText(icons.getResourceId(position - 1, -1));

                    break;
                }

                case "Companies": {

                    holder.navCounter.setVisibility(View.GONE);

                   /* if (App.getInstance().getMenuJ().PC > 0) {

                        holder.navCounter.setText(Long.toString(App.getInstance().getCompanyNewCount()));
                        holder.navCounter.setVisibility(View.VISIBLE);
                    }*/

                    if(CurrentAC != null && CurrentAC.CC > 0)
                    {
                        holder.navDescLayout.setVisibility(View.VISIBLE);
                        holder.navItemName.setText("Companies");
                        holder.navItemCount.setText(String.valueOf(CurrentAC.CC));
                        holder.navItemName.setVisibility(VISIBLE);
                        holder.navItemCount.setVisibility(VISIBLE);
                    }
                    else{
                        holder.navDescLayout.setVisibility(View.GONE);
                        holder.navItemName.setVisibility(GONE);
                        holder.navItemCount.setVisibility(GONE);
                    }

                    holder.navTitle.setText(titles[position - 1]);
                    holder.navIcon.setText(icons.getResourceId(position - 1, -1));
                    break;
                }

                case "Current Projects": {

                    holder.navCounter.setVisibility(View.GONE);

                    if(CurrentAC != null && CurrentAC.PC > 0)
                    {
                        holder.navDescLayout.setVisibility(View.VISIBLE);
                        holder.navItemName.setText("Projects");
                        holder.navItemCount.setText(String.valueOf(CurrentAC.PC));
                        holder.navItemName.setVisibility(VISIBLE);
                        holder.navItemCount.setVisibility(VISIBLE);
                    }
                    else{
                        holder.navDescLayout.setVisibility(View.GONE);
                        holder.navItemName.setVisibility(GONE);
                        holder.navItemCount.setVisibility(GONE);
                    }

                    holder.navTitle.setText(titles[position - 1]);
                    holder.navIcon.setText(icons.getResourceId(position - 1, -1));
                    break;
                }
                case "Contacts": {

                    holder.navCounter.setVisibility(View.GONE);

                   /* if (App.getInstance().getMenuJ().PC > 0) {

                        holder.navCounter.setText(Long.toString(App.getInstance().getCompanyNewCount()));
                        holder.navCounter.setVisibility(View.VISIBLE);
                    }*/
                    //if (context instanceof ContactFragment)
                    // ((ContactFragment)context).searchClick());
                    Log.e("TEST","Contact Count In Drawer :"+App.CCount);
                    holder.navItemCount.setText(String.valueOf(App.CCount));
                    if(CurrentAC != null && CurrentAC.CC > 0)
                    {
                        holder.navDescLayout.setVisibility(View.VISIBLE);
                        holder.navItemName.setText("Contacts");
                        // holder.navItemCount.setText(String.valueOf(CurrentAC.CC));
                        // holder.navItemCount.setText("502");
                        holder.navItemName.setVisibility(VISIBLE);
                        holder.navItemCount.setVisibility(VISIBLE);
                    }
                    else{
                        holder.navDescLayout.setVisibility(View.GONE);
                        holder.navItemName.setVisibility(GONE);
                        holder.navItemCount.setVisibility(GONE);
                    }

                    holder.navTitle.setText(titles[position - 1]);
                    holder.navIcon.setText(icons.getResourceId(position - 1, -1));
                    break;
                }
                default: {
                    holder.navDescLayout.setVisibility(View.GONE);
                    holder.navCounter.setVisibility(View.GONE);
                    holder.navTitle.setText(titles[position - 1]);
                    holder.navIcon.setText(icons.getResourceId(position - 1, -1));

                    break;
                }
            }

        } else {
            UserSmallJ userj = App.getInstance().getUserJ();
            holder.userUsername.setText(userj.PE );
            holder.userFullname.setText(userj.FN + " " + userj.LN);

            if (userj.I != null && userj.I.length() > 0) {

                imageLoader.get(userj.I, ImageLoader.getImageListener(holder.userPhoto, R.drawable.profile_default_photo, R.drawable.profile_default_photo));

            } else {

                holder.userPhoto.setImageResource(R.drawable.profile_default_photo);
            }

            if (App.getInstance().getCoverUrl() != null && App.getInstance().getCoverUrl().length() > 0) {

                imageLoader.get(App.getInstance().getCoverUrl(), ImageLoader.getImageListener(holder.userCover, R.drawable.profile_default_cover, R.drawable.profile_default_cover));

            } else {

               // holder.userCover.setImageResource(R.drawable.profile_default_cover);
            }

            if (App.getInstance().getVerify() == 0) {

                holder.userFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            } else {

                holder.userFullname.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.profile_verify_icon, 0);
            }

            if (Build.VERSION.SDK_INT > 15) {

                //holder.userCover.setImageAlpha(155);
            }
            if(LastCallTime == null || LastCallTime.isBefore(DataDateTime.Now().minusMinutes(5))) {
                GetCurrentUser( true);
            }
        }
    }

    private DateTime LastCallTime = null;
    /**
     * It returns the total no. of items . We +1 count to include the header view.
     * So , it the total count is 5 , the method returns 6.
     * This 6 implies that there are 5 row_items and 1 header view with header at position zero.
     */

    @Override
    public int getItemCount() {

        return titles.length + 1;
    }


    /**
     * This methods returns 0 if the position of the item is '0'.
     * If the position is zero its a header view and if its anything else
     * its a row_item with a title and icon.
     */

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {

            return 0;

        } else if (position > 0 && position < 5) {

            return 2;

        } else {

            return 1;
        }
    }

    public  void Destroy()
    {
        if(GetCurrentUserRequest != null) {
            GetCurrentUserRequest.destroy();
        }
        GetCurrentUserRequest = null;
        LocalBroadcastManager.getInstance(calledActivity).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    public  void onResume()
    {
        //Log.w(TAG, "onResume: " );
        //commedted becouse Onpause will stop changing ACId incase of change
        //LocalBroadcastManager.getInstance(context).registerReceiver(mRegistrationBroadcastReceiver,
        //        new IntentFilter(Config.ACID_CHANGED));
    }
    public  void onPause()
    {
        //Log.w(TAG, "onPause: " );
        //commedted becouse Onpause will stop changing ACId incase of change
       // LocalBroadcastManager.getInstance(calledActivity).unregisterReceiver(mRegistrationBroadcastReceiver);
    }


}