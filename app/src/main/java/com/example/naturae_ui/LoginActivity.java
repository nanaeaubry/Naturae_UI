package com.example.naturae_ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import java.util.*;
import java.text.*;
import java.io.*;
import java.lang.*;

import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    TextView tx1;
    TextView tx2;
    int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bLogin = (Button) findViewById(R.id.bLogin);
        final Button bCancel = (Button) findViewById(R.id.bCancel);
        tx1 = (TextView) findViewById(R.id.tvAttemptAmount);
        tx1.setVisibility(View.GONE);
        tx2 = (TextView) findViewById(R.id.tvAttempts);
        tx2.setVisibility(View.GONE);

        //"Registration Here" link to send you to registration page
        final TextView registerLink = (TextView) findViewById(R.id.tvRegisterHere);

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });


        bLogin.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View V) {

                String userName = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (userName.isEmpty() || password.isEmpty()) {
                    Toast.makeText((getApplicationContext()), "Please enter Username and/or Password", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(checkUserCredentials(userName, password)){
                        Toast.makeText(getApplicationContext(), "Redirecting...", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                        LoginActivity.this.startActivity(loginIntent);
                        counter=3;
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();

                        tx1.setVisibility(View.VISIBLE);
                        tx2.setVisibility(View.VISIBLE);
                        counter--;
                        tx1.setText(Integer.toString(counter));

                        if (counter == 0) {
                            bLogin.setEnabled(false);
                        }
                    }
                }
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean checkUserCredentials(String username, String password){
        String tempUsername = username;
        String tempPassword = password;

        try{
            BufferedReader file = new BufferedReader(new InputStreamReader(getAssets().open("users.txt"), "UTF-8"));
            String mLine;
            while ((mLine = file.readLine()) != null) {
                if(mLine.equals(tempUsername) || mLine.equals(tempPassword)) {
                    return true;
                }
                else{
                    return false;
                }
            }
        }
        catch (IOException err){
            System.out.println(err);
        }
        return false;
    }
}


