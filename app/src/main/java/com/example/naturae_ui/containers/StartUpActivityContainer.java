package com.example.naturae_ui.containers;

import android.app.Activity;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.naturae_ui.fragments.AccountAuthenFragment;
import com.example.naturae_ui.fragments.CreateAccountFragment;
import com.example.naturae_ui.fragments.LoginFragment;
import com.example.naturae_ui.R;
import com.example.naturae_ui.fragments.ResetPasswordFragment;

public class StartUpActivityContainer extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
        CreateAccountFragment.OnFragmentInteractionListener,  AccountAuthenFragment.OnFragmentInteractionListener{

    private LoginFragment loginFragment;
    private CreateAccountFragment createAccountFragment;
    private AccountAuthenFragment accountAuthenFragment;
    private ResetPasswordFragment resetPasswordFragment;

    private ConstraintLayout startUpTopNav;

    private Button backButton, rightSideButton;
    private ProgressBar progressBar;
    private boolean isHomeEnable;

    public StartUpActivityContainer() {
    }

    //Enumerator
    public enum AuthFragmentType {
        LOGIN,
        FORGOT_PASSWORD,
        CREATE_ACCOUNT,
        ACCOUNT_AUTHENTICATION,

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_container);

        startUpTopNav =  findViewById(R.id.start_up_top_nav_bar);

        //Initialize Login Fragment
        loginFragment = LoginFragment.newInstance();
        createAccountFragment = CreateAccountFragment.newInstance();
        accountAuthenFragment = AccountAuthenFragment.newInstance();
        resetPasswordFragment = ResetPasswordFragment.newInstance();


        backButton = findViewById(R.id.back_button);
        rightSideButton = findViewById(R.id.right_side_button);
        progressBar = findViewById(R.id.progressBar);

        beginFragment(AuthFragmentType.LOGIN, true, false);
        isHomeEnable = true;

        backButton.setOnClickListener(v -> onBackPressed());

    }

    /**
     * Helper method that replaces the current fragment with one that is specified
     * @param fragmentType   The fragment that should now appear
     * @param setTransition  If the fragment should be transitioned in to the viewer
     * @param addToBackStack If the fragment should be added to the activity's back-stack
     */
    @Override
    public void beginFragment(AuthFragmentType fragmentType, boolean setTransition, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (fragmentType) {
            case LOGIN:
                startUpTopNav.setVisibility(View.GONE);
                fragmentTransaction.replace(R.id.main_display_container, loginFragment);
                break;
            case CREATE_ACCOUNT:
                Thread createAccountThread = new Thread(()-> {
                    rightSideButton.setText(R.string.create);
                    rightSideButton.setTextColor(getColor(R.color.white));
                });
                createAccountThread.start();
                startUpTopNav.setVisibility(View.VISIBLE);
                fragmentTransaction.replace(R.id.main_display_container, createAccountFragment);
                break;
            case FORGOT_PASSWORD:
                fragmentTransaction.replace(R.id.main_display_container, resetPasswordFragment);
                break;
            case ACCOUNT_AUTHENTICATION:
                //Remove the right side button
                rightSideButton.setVisibility(View.GONE);
                //Remove the create account fragment from fragment back stack
                removeFragmentFromBackStack();
                fragmentTransaction.remove(createAccountFragment);
                fragmentTransaction.replace(R.id.main_display_container, accountAuthenFragment);
                break;
        }
        //Check weather or not to add the fragment to the back stack
        if(addToBackStack) {
            isHomeEnable = false;
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    /**
     * Pop the fragment from the stack if there are more than one fragment
     */
    private void removeFragmentFromBackStack(){
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStackImmediate();
        }
    }

    /**
     * Handle default back button
     */
    @Override
    public void onBackPressed() {
        //If the current page is the login page then will will go to the
        //main apps page
        removeFragmentFromBackStack();
        super.onBackPressed();
        if (!isHomeEnable) {
            if (getFragmentManager().getBackStackEntryCount() == 0){
                //To allow default back button to go to home page
                startUpTopNav.setVisibility(View.GONE);
                isHomeEnable = true;
            }
            else {
                isHomeEnable = false;
            }
        }
    }

    @Override
    public void hideKeyboard() {
        if(getCurrentFocus() != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public Button getRightSideButton() {
        return rightSideButton;
    }

    /**
     * Start the main activity
     */
    @Override
    public void startMainActivity(){
        Intent intent = new Intent(StartUpActivityContainer.this, MainActivityContainer.class);
        startActivity(intent);
    }
}
