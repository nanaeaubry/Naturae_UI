package com.example.naturae_ui.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.naturae_ui.Containers.StartUpContainer;
import com.example.naturae_ui.R;
import com.example.naturae_ui.Server.NaturaeUser;
import com.example.naturae_ui.Util.Constants;
import com.example.naturae_ui.Util.Helper;
import com.example.naturae_ui.Util.UserUtilities;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.okhttp.internal.Util;

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
    private Button createAccountButton;

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

        createAccountButton= mListener.getRightSideButton();
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
        createAccountButton = null;

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
        void setSendAuthenEmail(String email);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            switch (v.getId()){
                case R.id.first_name_edit_text:
                    checkEditTextFieldValidity(EditTextFieldType.FIRST_NAME, Objects.requireNonNull(firstNameEditText.getText()).toString());
                    break;
                case R.id.last_name_edit_text:
                    checkEditTextFieldValidity(EditTextFieldType.LAST_NAME, Objects.requireNonNull(lastNameEditText.getText()).toString());
                    break;
                case R.id.email_edit_text:
                    checkEditTextFieldValidity(EditTextFieldType.EMAIL, Objects.requireNonNull(emailEditText.getText()).toString());
                    break;
                case R.id.password_edit_text:
                    checkEditTextFieldValidity(EditTextFieldType.PASSWORD, Objects.requireNonNull(passwordEditText.getText()).toString());
                    break;
                case R.id.confirm_password_edit_text:
                    if (Objects.requireNonNull(confirmPasswordEditText.getText()).toString().isEmpty()) {
                        confirmPasswordErrorTextView.setVisibility(GONE);
                    }else{
                        checkEditTextFieldValidity(EditTextFieldType.CONFIRM_PASSWORD, confirmPasswordEditText.getText().toString());
                    }
                    break;
            }
        }

    }

    /**
     * Check is the information provide by the user is in a valid format
     * @param type the format to check
     * @param inputString the information entered and to check
     */
    private void checkEditTextFieldValidity(EditTextFieldType type, String inputString){
        switch (type){
            //Check if first name is valid
            case FIRST_NAME:
                if (inputString.isEmpty()){
                    isFirstNameValid = false;
                }
                else if (Helper.isNameValid(inputString)){
                    isFirstNameValid = true;
                    firstNameErrorTextView.setVisibility(GONE);
                }else{
                    isFirstNameValid = false;
                    firstNameErrorTextView.setText(R.string.invalid_first_name);
                    firstNameErrorTextView.setVisibility(VISIBLE);
                }
                break;
            //Check if last name is valid
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
            //Check if email is valid
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
            //Check if password is valid
            case PASSWORD:
                if (Objects.requireNonNull(confirmPasswordEditText.getText()).toString().isEmpty()){
                    isConfirmPasswordValid = false;
                    confirmPasswordErrorTextView.setVisibility(GONE);
                }
                else if (Objects.requireNonNull(confirmPasswordEditText.getText()).toString().compareTo(inputString) < 1){
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
            //Check if confirm password is valid
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
         //Check if first name, last name, email, password, and confirm password is valid
         //If any of the information is invalid then an error message will appear below the edit text field
         //If all of the information are valid then it will create an request to the server to create the user

        if(isAllInformationValid()){
            mListener.showProgressBar();
            new GrpcCreateAccount(mListener, getActivity()).execute(
                    Objects.requireNonNull(firstNameEditText.getText()).toString(),
                    Objects.requireNonNull(lastNameEditText.getText()).toString(),
                    Objects.requireNonNull(emailEditText.getText()).toString(),
                    Objects.requireNonNull(passwordEditText.getText()).toString()
            );
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

        return isFirstNameValid && isLastNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid;

    }

    /**
     * Create an Async task to handle server request form the client
     */
    private static class GrpcCreateAccount extends AsyncTask<String, Void, Naturae.CreateAccountReply>{
        private final OnFragmentInteractionListener mListener;
        private final WeakReference<Activity> activity;
        private ManagedChannel channel;
        private String firstName, lastName, email;

        private GrpcCreateAccount(OnFragmentInteractionListener mListener, Activity activity){
            this.mListener = mListener;
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected void onCancelled(Naturae.CreateAccountReply reply) {
            super.onCancelled(reply);
            //Remove the progress bar
            mListener.hideProgressBar();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            //Remove the progress bar
            mListener.hideProgressBar();
        }

        @Override
        protected Naturae.CreateAccountReply doInBackground(String... params) {
            Naturae.CreateAccountReply reply;
            try{
                //Create a channel to connect to the server
                channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
                //Create a stub for with the channel
                ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
                firstName = params[0];
                lastName = params[1];
                email = params[2];
                //Create an gRPC create account request
                Naturae.CreateAccountRequest request = Naturae.CreateAccountRequest.newBuilder().setAppKey(Constants.NATURAE_APP_KEY)
                        .setFirstName(firstName).setLastName(lastName).setEmail(email).setPassword(params[3]).build();

                //Send the request to the server and set reply to equal the response back from the server
                reply = stub.createAccount(request);

            }catch (Exception e){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return null;
            }
            return reply;

        }

        @Override
        protected void onPostExecute(Naturae.CreateAccountReply reply) {
            super.onPostExecute(reply);
            //Shut down the gRPC channel
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            //Hide the progress bar
            mListener.hideProgressBar();
            //Check if reply is equal to null. If it's equal to null then there was an error with the server or phone
            //while communicating with the server.
            if (reply != null){
                //Check the status of the request
                //If the status code is 201, then the account was able to created successfully
                //If the status code is 150, then there already an account with that email address
                //Any thing else then the an server error
                if (reply.getStatus().getCode() == Constants.ACCOUNT_CREATED){
                    //Start a new thread and cache the user
                    new Thread(()->UserUtilities.cacheUser(activity.get(), new NaturaeUser(firstName, lastName, email,
                            reply.getAccessToken(), reply.getRefreshToken(), "")));
                    mListener.beginFragment(StartUpContainer.AuthFragmentType.ACCOUNT_AUTHENTICATION, true, true);
                }else if (reply.getStatus().getCode() == Constants.EMAIL_EXIST){
                    //Display an error message that an account with the email already exist
                    displayError((String) activity.get().getText(R.string.email_exist));
                }else{
                    //Display an create account error
                    displayError((String) activity.get().getText(R.string.create_account_error));
                }
            }
            else{

            }

        }

        /**
         * Create an dialog box that display the error
         * @param message the error message to be display
         */
        private void displayError(String message){
            //Create an instance of Alert Dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity.get());
            alertDialogBuilder.setTitle("Error").setMessage(message).setPositiveButton(R.string.ok, (dialog, which) -> {
                dialog.cancel();
            }).show();
        }
    }
}
