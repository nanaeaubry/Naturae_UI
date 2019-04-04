package com.example.naturae_ui.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.naturae_ui.Containers.StartUpContainer;
import com.example.naturae_ui.R;
import com.example.naturae_ui.Util.Helper;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CreateAccountFragment extends Fragment implements View.OnFocusChangeListener {

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
        FIRST_NAME,
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

        firstNameEditText.setOnFocusChangeListener(this);
        lastNameEditText.setOnFocusChangeListener(this);
        emailEditText.setOnFocusChangeListener(this);
        passwordEditText.setOnFocusChangeListener(this);
        confirmPasswordEditText.setOnFocusChangeListener(this);

        Button createAccountButton = mListener.getRightSideButton();
        createAccountButton.setOnClickListener(v -> createAccount());


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
        super.onResume();
        mListener.hideProgressBar();

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
        void beginFragment(StartUpContainer.AuthFragmentType fragmentType, boolean setTransition,
                           boolean addToBackStack);
        Button getRightSideButton();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        TextInputEditText checkEditText = Objects.requireNonNull(getView()).findViewById(v.getId());
        String inputString = Objects.requireNonNull(checkEditText.getText()).toString();
        switch (v.getId()){
            case R.id.first_name_edit_text:
                checkEditTextFieldValidity(EditTextFieldType.FIRST_NAME, inputString);
                break;
            case R.id.last_name_edit_text:
                checkEditTextFieldValidity(EditTextFieldType.LAST_NAME, inputString);
                break;
            case R.id.email_edit_text:
                checkEditTextFieldValidity(EditTextFieldType.EMAIL, inputString);
                break;
            case R.id.password_edit_text:
                checkEditTextFieldValidity(EditTextFieldType.PASSWORD, inputString);
                break;
            case R.id.confirm_password_edit_text:
                if (inputString.isEmpty()) {
                    confirmPasswordErrorTextView.setVisibility(GONE);
                }else{
                    checkEditTextFieldValidity(EditTextFieldType.CONFIRM_PASSWORD, inputString);
                }
                break;
        }
    }

    /**
     * Check if the data input is valid
     */
    private void checkEditTextFieldValidity(EditTextFieldType type, String inputString){
        switch (type){
            case FIRST_NAME:
                if (inputString.isEmpty()){
                    isFirstNameValid = false;
                }
                else if (Helper.isNameValid(inputString)) {
                    isFirstNameValid = true;
                    firstNameErrorTextView.setVisibility(GONE);
                }else{
                    isFirstNameValid = false;
                    firstNameErrorTextView.setText(R.string.invalid_first_name);
                    firstNameErrorTextView.setVisibility(VISIBLE);
                }
                break;
            case LAST_NAME:
                if (inputString.isEmpty()){
                    isLastNameValid = false;
                }
                else if (Helper.isNameValid(inputString)){
                    isLastNameValid = true;

                    lastNameErrorTextView.setVisibility(GONE);
                }else{
                    isLastNameValid = false;
                    lastNameErrorTextView.setText(getText(R.string.invalid_last_name));
                    lastNameErrorTextView.setVisibility(VISIBLE);
                }
                break;
            case EMAIL:
                if (inputString.isEmpty()){
                    isEmailValid = false;
                }
                else if (Helper.isEmailValid(inputString)){
                    isEmailValid = true;
                    emailErrorTextView.setVisibility(GONE);

                }else{
                    isEmailValid = false;
                    emailErrorTextView.setText(getText(R.string.invalid_email));
                    emailErrorTextView.setVisibility(VISIBLE);
                }
                break;
            case PASSWORD:
                if (Objects.requireNonNull(confirmPasswordEditText.getText()).toString().isEmpty()){
                    isConfirmPasswordValid = false;
                    confirmPasswordErrorTextView.setVisibility(GONE);
                }
                else if (Objects.requireNonNull(confirmPasswordEditText.getText()).toString().compareTo(inputString) > 0){
                    isConfirmPasswordValid = true;
                    confirmPasswordErrorTextView.setVisibility(GONE);
                }else{
                    isConfirmPasswordValid = false;
                    confirmPasswordErrorTextView.setVisibility(VISIBLE);
                }

                if (inputString.isEmpty()){
                    isPasswordValid = false;
                }
                else if(Helper.isPasswordValid(inputString)){
                    isPasswordValid = true;
                    passwordErrorTextView.setVisibility(GONE);
                }else{
                    isPasswordValid = false;
                    passwordErrorTextView.setText(getText(R.string.invalid_password));
                    passwordErrorTextView.setVisibility(VISIBLE);
                }
                break;
            case CONFIRM_PASSWORD:
                if (Objects.requireNonNull(confirmPasswordEditText.getText()).toString().isEmpty()){
                    isConfirmPasswordValid = false;
                    confirmPasswordErrorTextView.setVisibility(GONE);
                }else if (inputString.compareTo(Objects.requireNonNull(passwordEditText.getText()).toString()) < 1){
                    isConfirmPasswordValid = true;
                    confirmPasswordErrorTextView.setVisibility(GONE);
                }else{
                    isConfirmPasswordValid = false;
                    confirmPasswordErrorTextView.setVisibility(VISIBLE);
                }
                break;
        }
    }

    /**
     * Try to create user account
     */
    private void createAccount(){

        if(!isAllInformationValid()){
            mListener.showProgressBar();
            new GrpcCreateAccount(mListener).execute();
        }

    }

    /**
     * Check to make sure all of the information is valid and if any information is not valid it will
     * display the error
     * @return true is all information is valid; false is any of the information is not valid
     */
    private boolean isAllInformationValid(){
        //Check to make sure first name is valid
        if (!isFirstNameValid) {
            if (Objects.requireNonNull(firstNameEditText.getText()).toString().isEmpty()){
                firstNameErrorTextView.setText(getText(R.string.empty_first_name));
                firstNameErrorTextView.setVisibility(VISIBLE);
            }else {
                checkEditTextFieldValidity(EditTextFieldType.FIRST_NAME, Objects.requireNonNull(firstNameEditText.getText()).toString());
            }
        }
        //Check to make sure last name is valid
        if (!isLastNameValid) {
            if (Objects.requireNonNull(lastNameEditText.getText()).toString().isEmpty()){
                lastNameErrorTextView.setText(getText(R.string.empty_last_name));
                lastNameErrorTextView.setVisibility(VISIBLE);
            }else {
                checkEditTextFieldValidity(EditTextFieldType.LAST_NAME, lastNameEditText.getText().toString());
            }
        }
        //Check to make sure email is valid
        if (!isEmailValid){
            if(Objects.requireNonNull(emailEditText.getText()).toString().isEmpty()){
                emailErrorTextView.setText(getText(R.string.empty_email));
                emailErrorTextView.setVisibility(VISIBLE);
            }else {
                checkEditTextFieldValidity(EditTextFieldType.EMAIL, emailEditText.getText().toString());
            }
        }
        //Check to make sure password is valid
        if(!isPasswordValid){
            if(Objects.requireNonNull(passwordEditText.getText()).toString().isEmpty()){
                passwordErrorTextView.setText(getText(R.string.empty_password));
                passwordErrorTextView.setVisibility(VISIBLE);
            }else {
                checkEditTextFieldValidity(EditTextFieldType.PASSWORD, passwordEditText.getText().toString());
            }
        }
        //Check to make sure confirm password match
        if (!isConfirmPasswordValid){
            checkEditTextFieldValidity(EditTextFieldType.CONFIRM_PASSWORD, Objects.requireNonNull(confirmPasswordEditText.getText()).toString());
        }

        if(isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid){
            return true;
        }

        return false;
    }


    private static class GrpcCreateAccount extends AsyncTask<String, Void, String[]>{
        private final OnFragmentInteractionListener mListener;

        private GrpcCreateAccount(OnFragmentInteractionListener mListener){
            this.mListener = mListener;
        }

        @Override
        protected String[] doInBackground(String... strings) {

            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mListener.hideProgressBar();
            super.onPostExecute(strings);

        }
    }
}
