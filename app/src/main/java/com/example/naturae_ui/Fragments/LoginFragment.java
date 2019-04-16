package com.example.naturae_ui.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.naturae_ui.Containers.StartUpActivityContainer;
import com.example.naturae_ui.R;
import com.example.naturae_ui.Util.Constants;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.Objects;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class LoginFragment extends Fragment implements View.OnClickListener{

    //Initialize all of the fragment variables
    private OnFragmentInteractionListener mListener;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextView errorMessageTextView;
    private ImageView appNameImage;

    protected View view;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);

        //Assign all of the variable in the fragment
        emailEditText = view.findViewById(R.id.email_edit_text);
        passwordEditText =  view.findViewById(R.id.password_edit_text);
        errorMessageTextView = view.findViewById(R.id.error_message_text_view);
        Button forgetPasswordTextView =  view.findViewById(R.id.forget_password_text_view);
        Button loginButton = view.findViewById(R.id.login_button);
        Button createAccountButton = view.findViewById(R.id.create_account_button);
        appNameImage = view.findViewById(R.id.app_name_image_view);

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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void hideKeyboard();
        void beginFragment(StartUpActivityContainer.AuthFragmentType fragmentType, boolean setTransition,
                           boolean addToBackStack);
        void startMainActivity();
    }

    //Create on click listener
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //Login button selected
            case R.id.login_button:
                mListener.hideKeyboard();
                String email = Objects.requireNonNull(emailEditText.getText()).toString();
                String password = Objects.requireNonNull(passwordEditText.getText()).toString();
                //Check if email or password edit text box is empty. If it's empty then an error message
                //will pop-up
                if (email.isEmpty() || password.isEmpty()){
                    errorMessageTextView.setText(R.string.empty_email_password_error);
                    errorMessageTextView.setVisibility(View.VISIBLE);
                }
                else{
                    new GrpcLogin(mListener, getActivity()).execute(
                            email,
                            password
                    );
                }
                break;
            //Create account selected
            case R.id.create_account_button:
                mListener.beginFragment(StartUpActivityContainer.AuthFragmentType.CREATE_ACCOUNT, true,
                        true);
                break;
            //Forget password selected
            case R.id.forget_password_text_view:
                mListener.beginFragment(StartUpActivityContainer.AuthFragmentType.FORGOT_PASSWORD, true,
                        true);
                break;
        }
        appNameImage.requestFocus();
    }

    private static class GrpcLogin extends AsyncTask<String, Void, Naturae.LoginReply>{
        private final LoginFragment.OnFragmentInteractionListener mListener;
        private final WeakReference<Activity> activity;
        private ManagedChannel channel;

        private GrpcLogin(OnFragmentInteractionListener mListener, Activity activity) {
            this.mListener = mListener;
            this.activity = new WeakReference<>(activity);

        }

        @Override
        protected Naturae.LoginReply doInBackground(String... params) {
            Naturae.LoginReply reply;

            try{
                channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
                //Create a stub for with the channel
                ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
                //Create an gRPC login request
                Naturae.LoginRequest request = Naturae.LoginRequest.newBuilder().setAppKey(Constants.NATURAE_APP_KEY)
                        .setEmail(params[0]).setPassword(params[1]).build();
                //Send the request to the server and set reply to be the response back from the server
                reply = stub.login(request);
            }
            catch (Exception e){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return null;
            }
            return reply;
        }

        @Override
        protected void onPostExecute(Naturae.LoginReply loginReply) {
            //Check if login replay is equal to null. If it's equal to null then there an error
            //when communicating with the server
            if (loginReply != null){
                TextView errorMessageTextView = activity.get().findViewById(R.id.error_message_text_view);
                System.out.println(loginReply.getStatus().getMessage());
                System.out.println(loginReply.getStatus().getCode());
                //If the status code is equal to 200 then the information the user's entered is correct
                if (loginReply.getStatus().getCode() == Constants.OK){
                    mListener.startMainActivity();
                }
                //If the status code is equal to 103 then the information the user's entered is incorrect
                else if (loginReply.getStatus().getCode() == Constants.INVALID_LOGIN_CREDENTIAL){
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    errorMessageTextView.setText(R.string.invalid_email_password);
                }
                //If the information entered is correct but the account has not been verify.
                //The program will send the user's to the authentication page so the user could
                //use the authentication code to verify their account
                else if (loginReply.getStatus().getCode() == Constants.ACCOUNT_NOT_VERIFY){
                    //Open the account authentication page
                    mListener.beginFragment(StartUpActivityContainer.AuthFragmentType.ACCOUNT_AUTHENTICATION, true,
                            true);
                }
                else{

                }

            }
            else{

            }

        }


    }


}
