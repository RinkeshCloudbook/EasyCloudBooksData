package com.easycloudbooks.easycloudbooks.common;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.widget.Toast;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

/**
 * Created by Mitul on 04-05-2017.
 */

public class DataFirebaseRemoteConfig {

    private void GetValue(String Key,Activity activity) {
        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);


        long cacheExpiration = 3600; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
       /* if (mFirebaseRemoteConfig.getInfo().getConfigSettings().()) {
            cacheExpiration = 0;
        }*/

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            mFirebaseRemoteConfig.activate();
                        } else {

                        }
                    }
                });
        // [END fetch_config_with_callback]
    }
}
