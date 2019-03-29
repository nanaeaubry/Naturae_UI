package com.example.naturae_ui.Containers;

import android.app.Activity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.example.naturae_ui.Fragments.CreateAccountFragment;
import com.example.naturae_ui.Fragments.LoginFragment;
import com.example.naturae_ui.R;

public class StartUpContainer extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener,
        CreateAccountFragment.OnFragmentInteractionListener {

    private FrameLayout mainLayout;
    private LoginFragment loginFragment;
    private CreateAccountFragment createAccountFragment;

    //Enumerator
    public enum AuthFragmentType {
        LOGIN,
        FORGOT_PASSWORD,
        CREATE_ACCOUNT,

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up_container);

        mainLayout = (FrameLayout) findViewById(R.id.main_display_container);

        //Initialize Login Fragment
        loginFragment = new LoginFragment();


        beginFragment(AuthFragmentType.LOGIN, true, false);
    }

    /**
     * Helper method that replaces the current fragment with one that is specified
     * @param fragmentType   The fragment that should now appear
     * @param setTransition  If the fragment should be transitioned in to the viewer
     * @param addToBackStack If the fragment should be added to the activity's back-stack
     */
    private void beginFragment(AuthFragmentType fragmentType, boolean setTransition, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        switch (fragmentType) {
            case LOGIN:
                fragmentTransaction.replace(R.id.main_display_container, loginFragment);
                break;
            case CREATE_ACCOUNT:
                fragmentTransaction.replace(R.id.main_display_container, createAccountFragment);
                break;
//            case FORGOT_PASSWORD:
//                toolbar.setVisibility(View.VISIBLE);
//                createAccountButton.setVisibility(View.GONE);
//                titleTextView.setText(R.string.forget_password_title);
//                fragmentTransaction.replace(R.id.main_display_container, forgetPasswordFragment);
//                break;
        }
        if(addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }


    @Override
    public void onFragmentInteraction() {

    }

    @Override
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

}
