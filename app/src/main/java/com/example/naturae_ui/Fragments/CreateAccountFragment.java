package com.example.naturae_ui.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
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

public class CreateAccountFragment extends Fragment{

    private OnFragmentInteractionListener mListener;
    private TextInputEditText firstNameEditText;
    private TextInputEditText lastNameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private TextView firstNameErrorTextView, lastNameErrorTextView, emailErrorTextView,
        passwordErrorTextView, confirmPasswordErrorTextView;

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
        firstNameEditText = (TextInputEditText) view.findViewById(R.id.first_name_edit_text);
        lastNameEditText = (TextInputEditText) view.findViewById(R.id.last_name_edit_text);
        emailEditText = (TextInputEditText) view.findViewById(R.id.email_edit_text);
        passwordEditText = (TextInputEditText) view.findViewById(R.id.password_edit_text);
        confirmPasswordEditText = (TextInputEditText) view.findViewById(R.id.confirm_password_edit_text);

        firstNameErrorTextView = (TextView) view.findViewById(R.id.first_name_error_text_view);
        lastNameErrorTextView = (TextView) view.findViewById(R.id.last_name_error_text_view);
        emailErrorTextView = (TextView) view.findViewById(R.id.email_error_text_view);
        passwordErrorTextView = (TextView) view.findViewById(R.id.password_error_text_view);
        confirmPasswordErrorTextView = (TextView) view.findViewById(R.id.confirm_password_error_text_view);

        Button createAccountButton;

        //Set up focus listener for all of the edit text field
        setEditTextFocusListener();

        //Initialize variables
        isFirstNameValid = false;
        isLastNameValid = false;
        isEmailValid = false;
        isPasswordValid = false;
        isConfirmPasswordValid = false;


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
        mListener.hideProgressBar();
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
        void hideKeyboard();
        void showProgressBar();
        void hideProgressBar();
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
                    if(firstNameEditText.getText().toString().isEmpty()){
                        isFirstNameValid = false;
                        firstNameErrorTextView.setVisibility(View.INVISIBLE);
                    }
                    //Check if first name is in a valid format
                    else if(Helper.isNameValid(firstNameEditText.getText().toString())){
                        isFirstNameValid = true;
                        //Show invalid first name error message
                        firstNameErrorTextView.setVisibility(View.INVISIBLE);
                    }
                    else{
                        isFirstNameValid = false;
                        //Hide invalid first name error message
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
                    if(lastNameEditText.getText().toString().isEmpty()){
                        isLastNameValid = false;
                        lastNameErrorTextView.setVisibility(View.INVISIBLE);
                    }
                    //Check if last name is in a valid format
                    else if(Helper.isNameValid(lastNameEditText.getText().toString())){
                        isLastNameValid = true;
                        //Show invalid last name error message
                        lastNameErrorTextView.setVisibility(View.INVISIBLE);

                    }
                    else{
                        isLastNameValid = false;
                        //Hide invalid last name error message
                        lastNameErrorTextView.setVisibility(View.VISIBLE);

                    }

                }
            }
        });

        //Set focus listener for email edit text
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){
                    if(emailEditText.getText().toString().isEmpty()){
                        isEmailValid = false;
                        emailErrorTextView.setVisibility(View.INVISIBLE);
                    }
                    //Check if email is in a valid format
                    else if(Helper.isEmailValid(emailEditText.getText().toString())){
                        isEmailValid = true;
                        //Show invalid email error message
                        emailErrorTextView.setVisibility(View.INVISIBLE);

                    }
                    else{
                        isEmailValid = false;
                        //Hide invalid email error message
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
                    if(passwordEditText.getText().toString().isEmpty()){
                        isPasswordValid = false;
                        passwordErrorTextView.setVisibility(View.INVISIBLE);
                    }
                    //Check if password is in a correct format
                    else if(Helper.isPasswordValid(passwordEditText.getText().toString())){
                        isPasswordValid = true;
                        //Show invalid password error message
                        passwordErrorTextView.setVisibility(View.INVISIBLE);
                    }
                    else{
                        isPasswordValid = false;
                        //Hide invalid password error message
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
                    if(Helper.isConfirmPasswordValid(passwordEditText.getText().toString(),
                            confirmPasswordEditText.getText().toString())){
                        isConfirmPasswordValid = true;
                        //Show invalid confirm password error message
                        confirmPasswordErrorTextView.setVisibility(View.INVISIBLE);
                    }
                    else{

                        isConfirmPasswordValid = false;
                        //Hide invalid confirm password error message
                        confirmPasswordErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void createAccount(){

    }

}
