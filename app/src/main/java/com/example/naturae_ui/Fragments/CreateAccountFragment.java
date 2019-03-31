package com.example.naturae_ui.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.naturae_ui.R;
import com.example.naturae_ui.Util.Helper;

import java.util.Objects;

public class CreateAccountFragment extends Fragment implements TextWatcher {

    private OnFragmentInteractionListener mListener;
    private TextInputEditText firstNameEditText;
    private TextInputEditText lastNameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private TextView firstNameErrorTextView, lastNameErrorTextView, emailErrorTextView,
        passwordErrorTextView, confirmPasswordErrorTextView;
    private TextInputLayout firstNameInputLayout, lastNameInputLayout, emailInputLayout, passwordInputLayout,
        confirmPasswordInputLayout;

    private boolean isFirstNameValid, isLastNameValid, isEmailValid, isPasswordValid, isConfirmPasswordValid;
    private Thread checkStatusThread;

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
        firstNameInputLayout = (TextInputLayout) view.findViewById(R.id.first_name_input_layout);
        lastNameInputLayout = (TextInputLayout) view.findViewById(R.id.last_name_input_layout);
        emailInputLayout = (TextInputLayout) view.findViewById(R.id.email_input_layout);
        passwordInputLayout = (TextInputLayout) view.findViewById(R.id.password_input_layout);
        confirmPasswordInputLayout = (TextInputLayout) view.findViewById(R.id.confirm_password_input_layout);

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

        firstNameEditText.addTextChangedListener(this);
        lastNameEditText.addTextChangedListener(this);
        emailEditText.addTextChangedListener(this);
        passwordEditText.addTextChangedListener(this);
        confirmPasswordEditText.addTextChangedListener(this);

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
        void showProgressBar();
        void hideProgressBar();
        void upDateCreateButtonStatus(boolean status);
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
                    if(Objects.requireNonNull(firstNameEditText.getText()).toString().isEmpty()){
                        isFirstNameValid = false;
                        firstNameErrorTextView.setVisibility(View.GONE);
                        firstNameInputLayout.setBackgroundResource(R.drawable.black_rectangle_border);

                    }
                    //Check if first name is in a valid format
                    else if(Helper.isNameValid(firstNameEditText.getText().toString())){
                        isFirstNameValid = true;
                        //Show invalid first name error message
                        firstNameErrorTextView.setVisibility(View.GONE);
                        firstNameInputLayout.setBackgroundResource(R.drawable.black_rectangle_border);
                    }
                    else{
                        isFirstNameValid = false;
                        //Hide invalid first name error message
                        firstNameErrorTextView.setVisibility(View.VISIBLE);
                        firstNameInputLayout.setBackgroundResource(R.drawable.red_rectangle_border);
                    }

                }
            }
        });

        //Set focus listener for last name edit text
        lastNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(Objects.requireNonNull(lastNameEditText.getText()).toString().isEmpty()){
                        isLastNameValid = false;
                        lastNameErrorTextView.setVisibility(View.GONE);
                        lastNameInputLayout.setBackgroundResource(R.drawable.black_rectangle_border);
                    }
                    //Check if last name is in a valid format
                    else if(Helper.isNameValid(lastNameEditText.getText().toString())){
                        isLastNameValid = true;
                        //Show invalid last name error message
                        lastNameErrorTextView.setVisibility(View.GONE);
                        lastNameInputLayout.setBackgroundResource(R.drawable.black_rectangle_border);
                    }
                    else{
                        isLastNameValid = false;
                        //Hide invalid last name error message
                        lastNameErrorTextView.setVisibility(View.VISIBLE);
                        lastNameInputLayout.setBackgroundResource(R.drawable.red_rectangle_border);

                    }

                }
            }
        });

        //Set focus listener for email edit text
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){
                    if(Objects.requireNonNull(emailEditText.getText()).toString().isEmpty()){
                        isEmailValid = false;
                        emailErrorTextView.setVisibility(View.GONE);
                        emailInputLayout.setBackgroundResource(R.drawable.black_rectangle_border);
                    }
                    //Check if email is in a valid format
                    else if(Helper.isEmailValid(emailEditText.getText().toString())){
                        isEmailValid = true;
                        //Show invalid email error message
                        emailErrorTextView.setVisibility(View.GONE);
                        emailInputLayout.setBackgroundResource(R.drawable.black_rectangle_border);

                    }
                    else{
                        isEmailValid = false;
                        //Hide invalid email error message
                        emailErrorTextView.setVisibility(View.VISIBLE);
                        emailInputLayout.setBackgroundResource(R.drawable.red_rectangle_border);

                    }
                }
            }
        });

        //Set focus listener for password edit text
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus){
                    if(Objects.requireNonNull(passwordEditText.getText()).toString().isEmpty()){
                        isPasswordValid = false;
                        passwordErrorTextView.setVisibility(View.GONE);
                        passwordInputLayout.setBackgroundResource(R.drawable.black_rectangle_border);
                    }
                    //Check if password is in a correct format
                    else if(Helper.isPasswordValid(passwordEditText.getText().toString())){
                        isPasswordValid = true;
                        //Show invalid password error message
                        passwordErrorTextView.setVisibility(View.GONE);
                        passwordInputLayout.setBackgroundResource(R.drawable.black_rectangle_border);
                    }
                    else{
                        isPasswordValid = false;
                        //Hide invalid password error message
                        passwordErrorTextView.setVisibility(View.VISIBLE);
                        passwordInputLayout.setBackgroundResource(R.drawable.red_rectangle_border);
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
                    if(Helper.isConfirmPasswordValid(Objects.requireNonNull(passwordEditText.getText()).toString(),
                            Objects.requireNonNull(confirmPasswordEditText.getText()).toString())){
                        isConfirmPasswordValid = true;
                        //Show invalid confirm password error message
                        confirmPasswordErrorTextView.setVisibility(View.GONE);
                        confirmPasswordInputLayout.setBackgroundResource(R.drawable.black_rectangle_border);
                    }
                    else{
                        isConfirmPasswordValid = false;
                        //Hide invalid confirm password error message
                        confirmPasswordErrorTextView.setVisibility(View.VISIBLE);
                        confirmPasswordInputLayout.setBackgroundResource(R.drawable.red_rectangle_border);
                    }
                }
            }
        });
    }


    private void createAccount(){

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(checkStatusThread != null){
            checkStatusThread.interrupt();
        }
        checkStatusThread = new Thread(() ->{
            switch (getActivity().getCurrentFocus().getId()){
                case R.id.first_name_edit_text:
                    isFirstNameValid = Helper.isNameValid(s.toString());
                    break;
                case R.id.last_name_edit_text:
                    isLastNameValid = Helper.isNameValid(s.toString());
                    break;
                case R.id.email_edit_text:
                    isEmailValid = Helper.isEmailValid(s.toString());
                    break;
                case R.id.password_edit_text:
                    isPasswordValid = Helper.isPasswordValid(s.toString());
                    if(s.toString().compareTo(passwordEditText.getText().toString()) == 1){
                        isConfirmPasswordValid = true;
                    }
                    break;
                case R.id.confirm_password_edit_text:
                    isConfirmPasswordValid = Helper.isConfirmPasswordValid(passwordEditText.getText().toString(), s.toString());
                    break;
            }
            if(isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid){
                mListener.upDateCreateButtonStatus(true);
            }
            else{
                mListener.upDateCreateButtonStatus(false);
            }
        });
        checkStatusThread.start();

    }

}
