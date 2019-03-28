package com.example.naturae_ui.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.naturae_ui.R;

public class LoginFragment extends Fragment implements View.OnClickListener{

    //Initialize all of the fragment variables
    private OnFragmentInteractionListener mListener;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView forgetPasswordTextView;
    private TextView wrongCredentialTextView;
    private Button loginButton;
    private Button createAccountButton;

    private View view;
    private ScrollView rootView;

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
        emailEditText = (EditText) view.findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) view.findViewById(R.id.password_edit_text);
        forgetPasswordTextView = (TextView) view.findViewById(R.id.forget_password_text_view);
        wrongCredentialTextView = (TextView) view.findViewById(R.id.wrong_credential_text_view);
        loginButton = (Button) view.findViewById(R.id.login_button);
        createAccountButton = (Button) view.findViewById(R.id.create_account_button);
        rootView = (ScrollView) view.findViewById(R.id.login_root_view);


        //Set up listener
        loginButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
        forgetPasswordTextView.setOnClickListener(this);

        rootView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                System.out.println("Work");
            }
        });

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //On Click listener
    @Override
    public void onClick(View v) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        //
        switch (v.getId()){
            //Login button selected
            case R.id.login_button:
                break;
            //Create account selected
            case R.id.create_account_button:
                emailEditText.clearFocus();
                passwordEditText.clearFocus();
                mListener.hideKeyboard();
                break;
            //Forget password selected
            case R.id.forget_password_text_view:
                break;
        }

    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
        void hideKeyboard();
    }
}
