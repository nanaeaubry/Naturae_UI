package com.example.naturae_ui.Containers;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.naturae_ui.Fragments.AccountAuthenFragment;
import com.example.naturae_ui.Fragments.CreateAccountFragment;
import com.example.naturae_ui.Fragments.LoginFragment;
import com.example.naturae_ui.R;

public class StartUpContainer extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
        CreateAccountFragment.OnFragmentInteractionListener {

    private FrameLayout mainLayout;
    private LoginFragment loginFragment;
    private CreateAccountFragment createAccountFragment;
    private AccountAuthenFragment accountAuthenFragment;

    private ConstraintLayout startUpTopNav;

    private Button backButton, rightSideButton;
    private ProgressBar progressBar;

    public StartUpContainer() {
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

        mainLayout = findViewById(R.id.main_display_container);
        startUpTopNav =  findViewById(R.id.start_up_top_nav_bar);

        //Initialize Login Fragment
        loginFragment = LoginFragment.newInstance();
        createAccountFragment = CreateAccountFragment.newInstance();
        accountAuthenFragment = AccountAuthenFragment.newInstance();

        backButton = findViewById(R.id.back_button);
        rightSideButton = findViewById(R.id.right_side_button);
        progressBar = findViewById(R.id.progressBar);

        beginFragment(AuthFragmentType.LOGIN, true, false);

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
//            case FORGOT_PASSWORD:
//                toolbar.setVisibility(View.VISIBLE);
//                createAccountButton.setVisibility(View.GONE);
//                titleTextView.setText(R.string.forget_password_title);
//                fragmentTransaction.replace(R.id.main_display_container, forgetPasswordFragment);
//                break;
            case ACCOUNT_AUTHENTICATION:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentTransaction.replace(R.id.main_display_container, accountAuthenFragment);
                break;
        }
        if(addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        showProgressBar();
        fragmentTransaction.commit();
    }

    /**
     * Handle default back button
     */
    @Override
    public void onBackPressed() {
        //If the current page is the login page then will will go to the
        //main apps page
        getSupportFragmentManager().popBackStack();
        if(getSupportFragmentManager().getBackStackEntryCount() == 0){
            //To allow default back button to go to home page
            //goHomeEnable = true;
            startUpTopNav.setVisibility(View.GONE);
            beginFragment(AuthFragmentType.LOGIN, true, false);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction() {

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

    @Override
    public  void setSendAuthenEmail(String email){
        accountAuthenFragment.setSendEmail(email);
    }
}
