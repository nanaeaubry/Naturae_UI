package com.example.naturae_ui.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.Objects;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

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
        void beginFragment(StartUpContainer.AuthFragmentType fragmentType, boolean setTransition,
                           boolean addToBackStack);
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
                checkEditTextFieldValidity(EditTextFieldType.FIRST_NAME,s.toString());
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
                if(checkEditTextFieldValidity(EditTextFieldType.FIRST_NAME, Objects.requireNonNull(firstNameEditText.getText()).toString())){
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
            case FIRST_NAME:
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

        if(canCreateAccount){
            mListener.showProgressBar();
            new GrpcTask(mListener, getContext(), getActivity()).execute(
                    Constants.APP_KEY,
                    Objects.requireNonNull(firstNameEditText.getText().toString()),
                    Objects.requireNonNull(lastNameEditText.getText().toString()),
                    Objects.requireNonNull(emailEditText.getText().toString()),
                    Objects.requireNonNull(passwordEditText.getText().toString())
            );
        }

    }

    private static class GrpcTask extends AsyncTask<String, Void, NaturaeUser>{
        private ManagedChannel channel;
        private Context context;
        private OnFragmentInteractionListener mListener;
        private FragmentActivity currActivity;

        private GrpcTask(OnFragmentInteractionListener mListener, Context context, FragmentActivity fragmentActivity){
            this.context = context;
            this.mListener = mListener;
            this.currActivity = fragmentActivity;
        }

        @Override
        protected NaturaeUser doInBackground(String... params) {
            NaturaeUser newAccount = null;
            try {
                channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
                ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
                Naturae.CreateAccountRequest request = Naturae.CreateAccountRequest.newBuilder().setAppKey(params[0]).setFirstName(params[1])
                        .setLastName(params[2]).setEmail(params[3]).setPassword(params[4]).build();
                Naturae.CreateAccountReply reply = stub.createAccount(request);
                if (reply.getErrorListCount() > 0){
                    newAccount = new NaturaeUser(null, null, null, null,
                           null, null);
                }else{
                    newAccount = new NaturaeUser(params[1], params[2], params[3], reply.getAccessToken(),
                            reply.getRefreshToken(), "");
                }

            }
            catch (Exception e ){
                System.out.println(e);
            }
            return newAccount;
        }

        @Override
        protected void onPostExecute(NaturaeUser newAccount) {
            mListener.hideProgressBar();
            //Check if the account was able to be created successfully
            if(newAccount.getEmail() != ""){
                UserUtilities.cacheUser(context, newAccount);
                mListener.beginFragment(StartUpContainer.AuthFragmentType.ACCOUNT_AUTHENTICATION, false,
                        true);
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(currActivity);
                builder.setMessage(R.string.create_account_error);
                builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.cancel());
                AlertDialog dialog = builder.create();
            }

        }
    }

}
