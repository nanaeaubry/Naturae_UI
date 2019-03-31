package com.example.naturae_ui.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.naturae_ui.Containers.StartUpContainer;
import com.example.naturae_ui.R;

import org.w3c.dom.Text;

public class LoginFragment extends Fragment implements View.OnClickListener{

    //Initialize all of the fragment variables
    private OnFragmentInteractionListener mListener;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextView wrongCredentialTextView;
    private ImageView appNameImage;


    private View view;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        //Assign all of the variable in the fragment
        emailEditText = (TextInputEditText) view.findViewById(R.id.email_edit_text);
        passwordEditText = (TextInputEditText) view.findViewById(R.id.password_edit_text);
        wrongCredentialTextView = (TextView) view.findViewById(R.id.wrong_credential_text_view);
        Button forgetPasswordTextView = (Button) view.findViewById(R.id.forget_password_text_view);
        Button loginButton = (Button) view.findViewById(R.id.login_button);
        Button createAccountButton = (Button) view.findViewById(R.id.create_account_button);
        appNameImage = (ImageView) view.findViewById(R.id.app_name_image_view);

        //Set up listener
        loginButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
        forgetPasswordTextView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mListener.hideProgressBar();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
        void hideKeyboard();
        void showProgressBar();
        void hideProgressBar();
        void beginFragment(StartUpContainer.AuthFragmentType fragmentType, boolean setTransition,
                           boolean addToBackStack);
    }

    //Create on click listener
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //Login button selected
            case R.id.login_button:
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                mListener.hideKeyboard();
                break;
            //Create account selected
            case R.id.create_account_button:
                mListener.beginFragment(StartUpContainer.AuthFragmentType.CREATE_ACCOUNT, true,
                        true);
                break;
            //Forget password selected
            case R.id.forget_password_text_view:
                mListener.beginFragment(StartUpContainer.AuthFragmentType.FORGOT_PASSWORD, true,
                        true);
                break;
        }
        appNameImage.requestFocus();
    }

    /**
     * Preform login process
     */
    private void login(){
        Thread loginThread = new Thread(()->{

        });
    }

}
