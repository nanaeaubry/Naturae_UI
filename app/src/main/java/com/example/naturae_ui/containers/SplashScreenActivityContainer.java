package com.example.naturae_ui.containers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.naturae_ui.util.UserUtilities;

public class SplashScreenActivityContainer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //If the user's had already login into the app. If the user had already logged in then it wil takes
        //the users to the main activity page and clear the top activity from the stack
        if (UserUtilities.isLoggedIn(this)){
            startActivity(new Intent(SplashScreenActivityContainer.this, MainActivityContainer.class));
        }
        //If the user's haven't login or logout of the account then the app will take the user to the login
        //screen
        else{
            startActivity(new Intent(SplashScreenActivityContainer.this, StartUpActivityContainer.class));
        }
    }

    /**
     * stops the application
     */
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
