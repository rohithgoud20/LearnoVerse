package com.example.learnoverse;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize Amplify
        try {
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
            Log.i("Amplify", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("Amplify", "Could not initialize Amplify", e);
            // Log the specific configuration error
        }
    }
}


