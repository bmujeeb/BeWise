package com.personal.bewise;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.personal.bewise.database.AutoRefreshDataBase;

public class SplashScreen extends Activity {
    // Set the display time, in milliseconds (or extract it out as a
    // configurable parameter)
    private final int SPLASH_DISPLAY_LENGTH = 5000;

    private AutoRefreshDataBase autoRefreshDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        autoRefreshDataBase = new AutoRefreshDataBase(getApplicationContext());
        autoRefreshDataBase.enforceDataBaseConsistancy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // Finish the splash activity so it can't be returned to.
                SplashScreen.this.finish();
                // Create an Intent that will start the main activity.
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(mainIntent);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}
