package com.easycloudbooks.easycloudbooks.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle ;
import androidx.appcompat.widget.Toolbar ;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.easycloudbooks.easycloudbooks.FragmentDrawer;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.ActivityBase;
import com.easycloudbooks.easycloudbooks.fragment.CompanyFragment;
import com.easycloudbooks.easycloudbooks.fragment.ContactFragment;
import com.easycloudbooks.easycloudbooks.fragment.DocumentUpload;
import com.easycloudbooks.easycloudbooks.fragment.FinishedProjectFragment;
import com.easycloudbooks.easycloudbooks.fragment.HomeFragment;
import com.easycloudbooks.easycloudbooks.fragment.NoCompanyFragment;
import com.easycloudbooks.easycloudbooks.fragment.NotificationFragment;
import com.easycloudbooks.easycloudbooks.fragment.ProjectFragment;


public class MainActivity extends ActivityBase implements  FragmentDrawer.FragmentDrawerListener          {

    private static String TAG = MainActivity.class.getSimpleName();
    Toolbar mToolbar;

    private FragmentDrawer drawerFragment;

    // used to store app title
    private CharSequence mTitle;
    public  MenuItem searchMenu=null;


    Fragment fragment;
    Boolean action = false;
    int page = 0;

    private Boolean restore = false;
    Context mContext;
    protected DrawerLayout mDrawerLayout;
    private Bundle intentBundle ;
    private String Mode ;

    String navTitles[];
    MainActivity _this;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mContext = getApplicationContext();
        _this = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if(App.getInstance().getCurrentACId() > 0)
            navTitles = getResources().getStringArray(R.array.navDrawerItems);
        else
            navTitles = getResources().getStringArray(R.array.NoCompanynavDrawerItems);
        if (savedInstanceState != null ) {
            //Restore the fragment's instance
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

            restore = savedInstanceState.getBoolean("restore");
            mTitle = savedInstanceState.getString("mTitle");
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container_body, fragment).commit();
            }
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle(mTitle);


        } else {
            intentBundle =   getIntent().getExtras();
            Mode = "";
            if (intentBundle != null) {
                Mode = intentBundle.getString("mode");
                Log.w(TAG, "onCreate Mode:-"+Mode );
                if(Mode.equals("notification"))
                {
                    displayView(1);
                    restore = true;
                }
                else if (intentBundle.getBoolean("reload") == true) {
                    if (intentBundle.getString("Fragment").equals("Home")) {
                        displayView(1);
                        restore = true;
                    }
                }
            }
            if (!restore) {
                restore = true;
                mTitle = getString(R.string.app_name);
                displayView(-1);
            }
        }
        drawerFragment = (FragmentDrawer) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        if (!restore) {

            displayView(1);
        }
        drawerFragment.ChangeContext();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("restore", true);
        outState.putBoolean("restore", true);
        outState.putString("mTitle", getSupportActionBar().getTitle().toString());
        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
    }

  /*  @Override
    public void onAttachSearchViewToDrawer(FloatingSearchView searchView) {
        searchView.attachNavigationDrawerToMenuButton(mDrawerLayout);
    }

    private SearchView mSearchView;

    public void OnCompleteFragment(View mView) {
        // this.mView = mView;
        if (page == DummyModel.DRAWER_ITEM_TAG_SearchContact) {
            getSupportActionBar().hide();
            mSearchView = ((ContactSearchFragment) fragment).GetSearchView();
        }
    }*/

    private ActionBarDrawerToggle mDrawerToggle;

    public void SetDrawer(Toolbar toolbar, RelativeLayout mView) {
        if (toolbar != null) {
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
                public void onDrawerClosed(View view) {
                    getSupportActionBar().setTitle(mTitle);
                    invalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                    invalidateOptionsMenu();
                }
            };
        } else {
            mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
                public void onDrawerClosed(View view) {
                    getSupportActionBar().setTitle(mTitle);
                    invalidateOptionsMenu();
                }

                public void onDrawerOpened(View drawerView) {
                    invalidateOptionsMenu();
                }
            };
        }
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



/*
    @Override
    public void onCloseSettingsDialog(int searchGender, int searchOnline) {

        SearchFragment p = (SearchFragment) fragment;
        p.onCloseSettingsDialog(searchGender, searchOnline);
    }
*/



    @Override
    public void onDrawerItemSelected(View view, int position) {
        if (position > 0)
            displayView(position);
    }

    public  void ClearCurrentFragment()
    {
        if(fragment != null)
        {
            Log.w(TAG, "ClearCurrentFragment: " +fragment.getClass().getName());
        }
    }

    private void displayView(int RPosition) {
        action = false;
        String title = "";
        ClearCurrentFragment();
        int position = RPosition ;
            if(position < 0) position = 1;

            if (navTitles != null && navTitles.length >= position) {
                title = navTitles[position - 1];
            }
            if (page != position) {
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setTitle(mTitle);
                getSupportActionBar().show();
                drawerFragment = (FragmentDrawer) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
                drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
                drawerFragment.setDrawerListener(this);
           /* if (position == 1) {
                getSupportActionBar().hide();
            }*/
            }

        if(App.getInstance().getCurrentACId() > 0 ) {
            Log.e("TEST","Title :"+title);
            switch (title) {

                case "": {

                    break;
                }
                case "Notification": {

                    page = 1;

                    fragment = new NotificationFragment();
                    getSupportActionBar().setTitle(title);
                    if(intentBundle != null)
                    ((NotificationFragment)fragment).SetFilterBundle(intentBundle.getBundle("bundle"));
                    action = true;

                    break;
                }
                case "Home": {

                    page = 2;

                    fragment = new HomeFragment();
                    getSupportActionBar().setTitle(R.string.page_18);

                    action = true;

                    break;
                }
                case "Current Projects": {

                    page = 3;

                    fragment = new ProjectFragment();
                    getSupportActionBar().setTitle(title);

                    action = true;

                    break;
                }
                case "Contacts": {
                    page = 4;

                    fragment = new ContactFragment();
                   // mToolbar.inflateMenu(R.menu.searchmenu);
                    getSupportActionBar().setTitle(title);

                    action = true;

                    break;
                }
                case "Companies": {

                    page = 5;

                    fragment = new CompanyFragment();
                    getSupportActionBar().setTitle(title);

                    action = true;

                    break;
                }
                case "Completed Projects": {

                    page = 6;

                    fragment = new FinishedProjectFragment();
                    getSupportActionBar().setTitle(title);

                    action = true;

                    break;
                }
                case "Document": {

                    page = 7;

                    fragment = new DocumentUpload();
                    getSupportActionBar().setTitle(title);

                    action = true;

                    break;
                }

                default: {
                    page = 8;
                    Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(i);
                }
            }

            if (action) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container_body, fragment)
                        .commit();
            }
        }
        else{
            action = true;
            if(title.equals("Settings") && RPosition != -1)
            {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                action = false;
            }
            else {
                page = 9;
                fragment = new NoCompanyFragment();
                //getSupportActionBar().setTitle("No Company");
            }
            if (action) {
               // drawerFragment = (FragmentDrawer) getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
               // drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
              //  drawerFragment.setDrawerListener(this);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container_body, fragment)
                        .commit();
            }
            /*if(title.equals("Settings"))
            {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);

            }*/
        }
        drawerFragment.ChangeContext();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        if (page == 4||page==5) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.searchmenu, menu);
            searchMenu = menu.findItem(R.id.menu_search);
         /*   MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.searchmenu, menu);

            getSupportActionBar().setTitle("Contacts");
            */
            return true; // false
        } else
            return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home: {
                return true;
            }
            case R.id.menu_search: {
                if(page == 4){
                    ((ContactFragment)fragment).searchClick();
                }else if(page == 5) {
                    ((CompanyFragment)fragment).searchClick();
                }
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }
    public void setContactCount(){
        drawerFragment.updateDrawerData();
    }

    @Override
    public void onBackPressed() {

        if (drawerFragment.isDrawerOpen()) {

            drawerFragment.closeDrawer();

        } else {

            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(CharSequence title) {

        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }



}
