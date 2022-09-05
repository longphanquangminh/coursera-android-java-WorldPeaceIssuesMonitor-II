package com.phanquangminhlong.worldpeaceissuesmonitor;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

public class WPIMApp extends Application {

    public static Boolean isSignInUIRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();

        // set data persistence

        // disabled because for this application we don't want employees to be changing data
        // offline

//        try {
//            // this will send any local changes we make when we are offline to Firebase when
//            // we are online again
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//        } catch (Exception e) {
//            Log.w("****** onCreate ******", "getPersistenceEnabled( ) failed: " + e.getMessage());
//        }

        // Task 5 Step 3: check if sign in should expire

//        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//            Long lastSignInTimestamp = FirebaseAuth.getInstance().getCurrentUser().getMetadata().getLastSignInTimestamp();
//            Log.w("****** onCreate ******", "last login = " + (String.valueOf(lastSignInTimestamp)));
//
//            Long nowTimeStamp = new Date().getTime();
//            Log.w("****** onCreate ******", "current time = " + String.valueOf(nowTimeStamp));
//
//            Log.w("****** onCreate ******", "difference = " + String.valueOf((nowTimeStamp - lastSignInTimestamp)/1000) + " seconds");
//
//            if (nowTimeStamp - lastSignInTimestamp > 1200 * 1000) {
                FirebaseAuth.getInstance().signOut();
//            }
//        }

    }
}
