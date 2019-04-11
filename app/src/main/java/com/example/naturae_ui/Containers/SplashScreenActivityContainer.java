package com.example.naturae_ui.Containers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.naturae_ui.R;
import com.example.naturae_ui.Util.UserUtilities;

public class SplashScreenActivityContainer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //If the user's had already login into the app. If the user had already logged in then it wil takes
        //the users to the main activity page
        if (UserUtilities.isLoggedIn(this)){
            startActivity(new Intent(SplashScreenActivityContainer.this, MainActivityContainer.class));
        }
        //If the user's haven't login or logout of the account then the app will take the user to the login
        //screen
        else{
            startActivity(new Intent(SplashScreenActivityContainer.this, StartUpActivityContainer.class));
        }
        setContentView(R.layout.activity_splash_screen_container);
    }
}
