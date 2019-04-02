package com.example.naturae_ui.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.naturae_ui.R;
import com.example.naturae_ui.Util.Helper;

import java.util.Objects;

public class CreateAccountFragment extends Fragment{

    private OnFragmentInteractionListener mListener;
    private TextInputEditText firstNameEditText;
    private TextInputEditText lastNameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private TextView firstNameErrorTextView, lastNameErrorTextView, emailErrorTextView,
        passwordErrorTextView, confirmPasswordErrorTextView;

    private boolean isFirstNameValid, isLastNameValid, isEmailValid, isPasswordValid, isConfirmPasswordValid;

    private enum EditTextFieldType{
        FIREST_NAME,
        LAST_NAME,
        EMAIL,
        PASSWORD,
        CONFIRM_PASSWORD
    }


    public CreateAccountFragment() {
        // Required empty public constructor
    }


    public static CreateAccountFragment newInstance() {
        return new CreateAccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        //Initialize fragment variables
        firstNameEditText =  view.findViewById(R.id.first_name_edit_text);
        lastNameEditText =  view.findViewById(R.id.last_name_edit_text);
        emailEditText =  view.findViewById(R.id.email_edit_text);
        passwordEditText =  view.findViewById(R.id.password_edit_text);
        confirmPasswordEditText =  view.findViewById(R.id.confirm_password_edit_text);

        firstNameErrorTextView = view.findViewById(R.id.first_name_error_text_view);
        lastNameErrorTextView =  view.findViewById(R.id.last_name_error_text_view);
        emailErrorTextView = view.findViewById(R.id.email_error_text_view);
        passwordErrorTextView =  view.findViewById(R.id.password_error_text_view);
        confirmPasswordErrorTextView = view.findViewById(R.id.confirm_password_error_text_view);

        Button createAccountButton = mListener.getRightSideButton();

        createAccountButton.setOnClickListener(v -> createAccount());

        //Set up focus listener for all of the edit text field
        setEditTextChangeListener();
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
     * Create an interface to communicate with StartUpActivity
     */
    public interface OnFragmentInteractionListener {
        void showProgressBar();
        void hideProgressBar();
        Button getRightSideButton();
    }

    /**
     * Set text change listener for all of the edit text field
     */
    private void setEditTextChangeListener(){
        firstNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                checkEditTextFieldValidity(EditTextFieldType.FIREST_NAME,s.toString());
            }
        });

        lastNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkEditTextFieldValidity(EditTextFieldType.LAST_NAME, s.toString());

            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkEditTextFieldValidity(EditTextFieldType.EMAIL, s.toString());

            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkEditTextFieldValidity(EditTextFieldType.PASSWORD, s.toString());
            }
        });

        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                checkEditTextFieldValidity(EditTextFieldType.CONFIRM_PASSWORD, s.toString());

            }
        });

    }

    /**
     * Initialize focus listener for all of the edit text field
     */
    private void setEditTextFocusListener(){

        firstNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                if(checkEditTextFieldValidity(EditTextFieldType.FIREST_NAME, Objects.requireNonNull(firstNameEditText.getText()).toString())){
                    showErrorMessage(firstNameErrorTextView);
                }

            }
        });

        lastNameEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                if (checkEditTextFieldValidity(EditTextFieldType.LAST_NAME, Objects.requireNonNull(lastNameEditText.getText()).toString())){
                    showErrorMessage(lastNameErrorTextView);
                }
            }
        });

        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                if (checkEditTextFieldValidity(EditTextFieldType.EMAIL, Objects.requireNonNull(emailEditText.getText()).toString())){
                    showErrorMessage(emailErrorTextView);
                }

            }
        });

        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                if (checkEditTextFieldValidity(EditTextFieldType.PASSWORD, Objects.requireNonNull(passwordEditText.getText()).toString())){
                    showErrorMessage(passwordErrorTextView);
                }
            }
        });

        confirmPasswordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                if (checkEditTextFieldValidity(EditTextFieldType.CONFIRM_PASSWORD, Objects.requireNonNull(passwordEditText.getText()).toString())){
                    showErrorMessage(confirmPasswordErrorTextView);
                }
            }
        });

    }

    /**
     * Check if the data input is valid
     * @param type which edit text field is to check
     * @param data string data from the edit text field
     * @return weather or not to display the error message
     */
    private boolean checkEditTextFieldValidity(EditTextFieldType type, String data){
        boolean showErrorMessage = false;
        switch (type){
            case FIREST_NAME:
                if(data.isEmpty()){
                    isFirstNameValid = false;
                    firstNameErrorTextView.setVisibility(View.GONE);
                }
                else if(Helper.isNameValid(data)){
                    isFirstNameValid = true;
                    firstNameErrorTextView.setVisibility(View.GONE);
                }
                else{
                    isFirstNameValid = false;
                    showErrorMessage = true;
                }
                break;
            case LAST_NAME:
                if(data.isEmpty()){
                    isLastNameValid = false;
                    lastNameErrorTextView.setVisibility(View.GONE);
                }
                else if(Helper.isNameValid(data)){
                    isLastNameValid = true;
                    lastNameErrorTextView.setVisibility(View.GONE);
                }
                else{
                    isLastNameValid = false;
                    showErrorMessage = true;
                }
                break;
            case EMAIL:
                if(data.isEmpty()){
                    isEmailValid = false;
                    emailErrorTextView.setVisibility(View.GONE);
                }
                else if(Helper.isEmailValid(data)){
                    isEmailValid = true;
                    emailErrorTextView.setVisibility(View.GONE);
                }
                else{
                    isEmailValid = false;
                    showErrorMessage = true;
                }
                break;
            case PASSWORD:
                if(data.isEmpty()){
                    isPasswordValid = false;
                    passwordErrorTextView.setVisibility(View.GONE);
                }
                else if(Helper.isPasswordValid(data)){
                    isPasswordValid = true;
                    passwordErrorTextView.setVisibility(View.GONE);
                }
                else{
                    isPasswordValid = false;
                    showErrorMessage = true;
                }

                if(Objects.requireNonNull(confirmPasswordEditText.getText()).toString().isEmpty()){
                    confirmPasswordErrorTextView.setVisibility(View.GONE);
                }
                else{
                    if(Helper.doesStringMatch(data, confirmPasswordEditText.getText().toString())){
                        confirmPasswordErrorTextView.setVisibility(View.GONE);
                    }
                    else{
                        confirmPasswordErrorTextView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case CONFIRM_PASSWORD:
                if(data.isEmpty()){
                    isConfirmPasswordValid = false;
                    confirmPasswordErrorTextView.setVisibility(View.GONE);
                }
                else if(Helper.doesStringMatch(data, Objects.requireNonNull(passwordEditText.getText()).toString())){
                    isConfirmPasswordValid = true;
                    confirmPasswordErrorTextView.setVisibility(View.GONE);
                }
                else{
                    isConfirmPasswordValid = false;
                    showErrorMessage = true;
                }
                break;
        }
        return showErrorMessage;
    }

    /**
     * show error message to the screen
     * @param selectedTextView the error message to display
     */
    private void showErrorMessage(TextView selectedTextView){
        selectedTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Try to create user account
     */
    private void createAccount(){
        boolean canCreateAccount = true;
        if(!isFirstNameValid){
            showErrorMessage(firstNameErrorTextView);
            canCreateAccount = false;
        }
        if(!isLastNameValid){
            showErrorMessage(lastNameErrorTextView);
            canCreateAccount = false;
        }
        if(!isEmailValid){
            showErrorMessage(emailErrorTextView);
            canCreateAccount = false;
        }
        if(!isPasswordValid){
            showErrorMessage(passwordErrorTextView);
            canCreateAccount = false;
        }
        if(!isConfirmPasswordValid){
            showErrorMessage(confirmPasswordErrorTextView);
            canCreateAccount = false;
        }

    }

}
