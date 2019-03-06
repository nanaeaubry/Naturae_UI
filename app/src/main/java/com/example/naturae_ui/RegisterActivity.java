package com.example.naturae_ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etName = (EditText) findViewById(R.id.etFirstName);
        final EditText etName2 = (EditText) findViewById(R.id.etLastName);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button bRegister = (Button) findViewById(R.id.bRegister);
        setTitle("Registration");

        //noinspection deprecation
        bRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View V) {
                Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(registerIntent);
                /*switch (V.getId()) {
                    case R.id.bRegister:

                        String firstName = etName.getText().toString();
                        String lastName = etName2.getText().toString();
                        String email = etEmail.getText().toString();
                        String password = etPassword.getText().toString();

                        User registeredData = new User(firstName, lastName, email, password);

                        break;
                }*/

            }
        });
    }
}
