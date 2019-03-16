package com.example.naturae_ui.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        //Assign all of the variable in the fragment
        emailEditText = (EditText) view.findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) view.findViewById(R.id.password_edit_text);
        forgetPasswordTextView = (TextView) view.findViewById(R.id.forget_password_text_view);
        wrongCredentialTextView = (TextView) view.findViewById(R.id.wrong_credential_text_view);
        loginButton = (Button) view.findViewById(R.id.login_button);
        createAccountButton = (Button) view.findViewById(R.id.create_account_button);

        //Set up listener
        loginButton.setOnClickListener(this);
        createAccountButton.setOnClickListener(this);
        forgetPasswordTextView.setOnClickListener(this);
        view.setOnClickListener(this);

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
                break;
            //Forget password selected
            case R.id.forget_password_text_view:
                break;
            //Back background selected
            case R.id.login_fragment:
                break;

        }

    }


    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
