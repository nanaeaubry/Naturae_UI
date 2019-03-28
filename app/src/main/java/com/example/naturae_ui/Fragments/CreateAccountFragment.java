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
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.naturae_ui.R;
import com.example.naturae_ui.Util.Helper;

public class CreateAccountFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private ScrollView rootView;
    private TextView firstNameErrorTextView, lastNameErrorTextView, emailErrorTextView,
        passwordErrorTextView, confirmPasswordErrorTextView;

    private Button createAccountButton;
    boolean isFirstNameValid, isLastNameValid, isEmailValid, isPasswordValid, isConfirmPasswordValid;

    public CreateAccountFragment() {
        // Required empty public constructor
    }


    public static CreateAccountFragment newInstance() {
        CreateAccountFragment fragment = new CreateAccountFragment();
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
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        //Initialize fragment variables
        firstNameEditText = (EditText) view.findViewById(R.id.first_name_edit_text);
        lastNameEditText = (EditText) view.findViewById(R.id.last_name_edit_text);
        emailEditText = (EditText) view.findViewById(R.id.email_edit_text);
        passwordEditText = (EditText) view.findViewById(R.id.password_edit_text);
        confirmPasswordEditText = (EditText) view.findViewById(R.id.confirm_password_edit_text);
        createAccountButton = (Button) view.findViewById(R.id.create_account_button);
        rootView = view.findViewById(R.id.create_account_root_view);

        firstNameErrorTextView = (TextView) view.findViewById(R.id.first_name_error_text_view);
        lastNameErrorTextView = (TextView) view.findViewById(R.id.last_name_error_text_view);
        emailErrorTextView = (TextView) view.findViewById(R.id.email_error_text_view);
        passwordErrorTextView = (TextView) view.findViewById(R.id.password_error_text_view);
        confirmPasswordErrorTextView = (TextView) view.findViewById(R.id.confirm_password_error_text_view);


        //Call
        setEditTextFocusListener();

        //Initialize variables
        isFirstNameValid = false;
        isLastNameValid = false;
        isEmailValid = false;
        isPasswordValid = false;
        isConfirmPasswordValid = false;

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                if(isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid){

                }
                else{

                }
                break;
        }
    }


    /**
     * Set focus listener for all of the edit text field
     */
    private void setEditTextFocusListener(){
        //Set focus listener for first name edit text
        firstNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    //Check if first name is in a valid format
                    if(Helper.isNameValid(firstNameEditText.getText().toString())){
                        isFirstNameValid = true;
                        firstNameErrorTextView.setVisibility(View.INVISIBLE);
                    }
                    else{
                        isFirstNameValid = false;
                        firstNameErrorTextView.setVisibility(View.VISIBLE);

                    }

                }
            }
        });

        //Set focus listener for last name edit text
        lastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    //Check if last name is in a valid format
                    if(Helper.isNameValid(lastNameEditText.getText().toString())){
                        isLastNameValid = false;
                        lastNameErrorTextView.setVisibility(View.VISIBLE);

                    }
                    else{
                        isLastNameValid = false;
                        lastNameErrorTextView.setVisibility(View.INVISIBLE);

                    }

                }
            }
        });

        //Set focus listener for email edit text
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){
                    //Check if email is in a valid format
                    if(Helper.isEmailValid(emailEditText.getText().toString())){
                        isEmailValid = true;
                        emailErrorTextView.setVisibility(View.INVISIBLE);

                    }
                    else{
                        isEmailValid = false;
                        emailErrorTextView.setVisibility(View.VISIBLE);

                    }
                }
            }
        });

        //Set focus listener for password edit text
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){
                    //Check if password is in a correct format
                    if(Helper.isPasswordValid(passwordEditText.getText().toString())){
                        isPasswordValid = true;
                        passwordErrorTextView.setVisibility(View.INVISIBLE);
                    }
                    else{
                        isPasswordValid = false;
                        passwordErrorTextView.setVisibility(View.VISIBLE);
                    }

                }
            }
        });

        //Set focus listener for confirm password edit text
        confirmPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    //Check if the entered confirm password match with the password
                    if(Helper.isConfirmPasswrodValid(passwordEditText.getText().toString(),
                            confirmPasswordEditText.getText().toString())){
                        isConfirmPasswordValid = true;
                        confirmPasswordErrorTextView.setVisibility(View.INVISIBLE);
                    }
                    else{

                        isConfirmPasswordValid = false;
                        confirmPasswordErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        rootView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                }
            }
        });


    }

}
