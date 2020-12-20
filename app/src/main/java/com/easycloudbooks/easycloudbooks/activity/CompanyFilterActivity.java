package com.easycloudbooks.easycloudbooks.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.ActivityBase;
import com.easycloudbooks.easycloudbooks.fragment.CompanyFragment;


public class CompanyFilterActivity extends ActivityBase {

    Toolbar mToolbar;

    CompanyFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companyfilter);

        mToolbar = (Toolbar) findViewById(R.id.companyfiltertoolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (savedInstanceState != null) {

            fragment =(CompanyFragment) getSupportFragmentManager().getFragment(savedInstanceState, "currentFragment");

        } else {

            fragment =  CompanyFragment.newInstance();
        }
        Bundle intentBundle = getIntent().getExtras();
        if(intentBundle != null)
        {
            Long ACId = intentBundle.getLong("acid");
            if(App.getInstance().haveACId(ACId)) {
                App.getInstance().setCurrentACId(ACId);
            }
            long[] CRIds = intentBundle.getLongArray("CRIds");
            if(CRIds != null)
            {
                fragment.SetFilter(CRIds);
            }
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.companyprofile_content, fragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, "currentFragment", fragment);
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
}
