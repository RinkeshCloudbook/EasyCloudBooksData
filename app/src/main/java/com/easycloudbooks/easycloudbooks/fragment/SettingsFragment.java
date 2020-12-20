package com.easycloudbooks.easycloudbooks.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easycloudbooks.easycloudbooks.BuildConfig;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.AboutUsActivity;
import com.easycloudbooks.easycloudbooks.activity.SplashActivity;
import com.easycloudbooks.easycloudbooks.activity.WebViewActivity;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.app.Config;
import com.easycloudbooks.easycloudbooks.constants.Constants;

public class SettingsFragment extends PreferenceFragment implements Constants {

    private static String TAG = SettingsFragment.class.getSimpleName();
    private Preference logoutPreference, aboutPreference, itemTerms, itemPrivacy, itemCrash;

    private ProgressDialog pDialog;


    LinearLayout aboutDialogContent;
    TextView aboutDialogAppName, aboutDialogAppVersion, aboutDialogAppCopyright;

    private Boolean loading = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        initpDialog();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);


        Preference pref = findPreference("settings_version");

        pref.setTitle(getString(R.string.app_name) + " v " + BuildConfig.VERSION_NAME);

        pref = findPreference("settings_logout");

        pref.setSummary(App.getInstance().getUsername());

//        pref = findPreference("settings_copyright_info");
//
//        pref.setSummary(APP_COPYRIGHT + " © " + APP_YEAR);

        logoutPreference = findPreference("settings_logout");
        aboutPreference = findPreference("settings_version");
        itemTerms = findPreference("settings_terms");
        itemPrivacy = findPreference("settings_privacy");


        itemPrivacy.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), WebViewActivity.class);
                i.putExtra("url", METHOD_APP_PRIVACY);
                i.putExtra("title", getText(R.string.settings_privacy));
                startActivity(i);

                return true;
            }
        });

        itemTerms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), WebViewActivity.class);
                i.putExtra("url", METHOD_APP_TERMS);
                i.putExtra("title", getText(R.string.settings_terms));
                startActivity(i);

                return true;
            }
        });

        aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                Intent i = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(i);

               /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle(getText(R.string.action_about));

                aboutDialogContent = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.about_dialog, null);

                alertDialog.setView(aboutDialogContent);

                aboutDialogAppName = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppName);
                aboutDialogAppVersion = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppVersion);
                aboutDialogAppCopyright = (TextView) aboutDialogContent.findViewById(R.id.aboutDialogAppCopyright);

                aboutDialogAppName.setText(getString(R.string.app_name));
                aboutDialogAppVersion.setText("Version " + getString(R.string.app_version));
                aboutDialogAppCopyright.setText("Copyright © " + getString(R.string.app_year) + " " + getString(R.string.app_copyright));

//                    alertDialog.setMessage("Version " + APP_VERSION + "/r/n" + APP_COPYRIGHT);
                alertDialog.setCancelable(true);
                alertDialog.setNeutralButton(getText(R.string.action_ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

                alertDialog.show();*/

                return false;
            }
        });

        logoutPreference.setSummary(App.getInstance().getUsername());

        logoutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference arg0) {

                if (App.getInstance().isConnected() && App.getInstance().getId() != 0) {

                    App.getInstance().removeData();
                    App.getInstance().readData();

                    Intent intent = new Intent(getActivity(), SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    /*
                    loading = true;
                    showpDialog();
                    new CustomAuthRequest(Request.Method.POST, METHOD_ACCOUNT_LOGOUT, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    try {

                                        if (!response.getBoolean("error")) {

                                            App.getInstance().removeData();
                                            App.getInstance().readData();

                                            Intent intent = new Intent(getActivity(), SplashActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }

                                    } catch (JSONException e) {

                                        e.printStackTrace();

                                    } finally {

                                        loading = false;

                                        hidepDialog();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            loading = false;

                            hidepDialog();
                        }
                    }) {

                    };*/

                }

                return true;
            }
        });



        itemCrash = findPreference("settings_crash");
        PreferenceScreen screen = getPreferenceScreen();
        Log.w(TAG, "onPreferenceClick: "+App.getInstance().getUserId() );
        PreferenceCategory pCategory = (PreferenceCategory) findPreference("header_others");
        if(App.getInstance().getUserId() == 3074 || App.getInstance().getUserId() == 2846) {

            App.getInstance().FirebaseAnalyticsLog("SettingPage");

            itemCrash.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                public boolean onPreferenceClick(Preference arg0) {

                    Log.w(TAG, "onPreferenceClick1: "+App.getInstance().getUserId() );

                    throw new RuntimeException("This is a crash");
                    //Crashlytics.getInstance().crash(); // Force a crash

                    //return true;
                }
            });
        }
        else{
            pCategory.removePreference(itemCrash);
        }

    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            loading = savedInstanceState.getBoolean("loading");

        } else {

            loading = false;
        }

        if (loading) {

            showpDialog();
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.w(TAG, "onDestroyView: " );
        hidepDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("loading", loading);
    }



    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing())
            pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}