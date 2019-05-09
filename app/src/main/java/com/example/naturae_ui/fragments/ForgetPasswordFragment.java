package com.example.naturae_ui.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.naturae_ui.R;
import com.example.naturae_ui.containers.StartUpActivityContainer;
import com.example.naturae_ui.util.Constants;
import com.example.naturae_ui.util.Helper;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;

import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ForgetPasswordFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;
    private TextView invalidEmailTextView, invalidAuthenCodeTextView, weakPasswordTextView, mismatchPasswordTextViuew;
    private EditText emailEditText, verificationCodeEditText, passwordEditText, confirmPasswordEditText;
    private LinearLayout getEmailLayout, resetPasswordVerificationLayout, newPasswordLayout;
    private String userEmail;

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }

    public static ForgetPasswordFragment newInstance() {
        return new ForgetPasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forget_password, container, false);

        //Initialize all of the views
        Button submitPasswordEmailButton = view.findViewById(R.id.reset_password_email_button);
        Button submitAuthenCodeButton = view.findViewById(R.id.reset_password_submit_authen_code_button);
        Button submitNewPasswordButton = view.findViewById(R.id.reset_password_submit_new_password_button);

        invalidEmailTextView = view.findViewById(R.id.reset_password_invalid_email_text_view);
        invalidAuthenCodeTextView = view.findViewById(R.id.forget_password_invalid_authen_text_view);
        weakPasswordTextView = view.findViewById(R.id.reset_password_weak_password_text_view);
        mismatchPasswordTextViuew = view.findViewById(R.id.reset_password_mismatch_password_text_view);

        emailEditText = view.findViewById(R.id.reset_password_email_edit_text);
        verificationCodeEditText = view.findViewById(R.id.reset_password_authen_code_edit_text);
        passwordEditText = view.findViewById(R.id.forget_password_new_password);
        confirmPasswordEditText = view.findViewById(R.id.reset_password_confirm_password);

        getEmailLayout = view.findViewById(R.id.reset_password_get_email_layout);
        resetPasswordVerificationLayout = view.findViewById(R.id.reset_password_authen_code_layout);
        newPasswordLayout = view.findViewById(R.id.new_password_layout);

        submitAuthenCodeButton.setOnClickListener(this);
        submitPasswordEmailButton.setOnClickListener(this);
        submitNewPasswordButton.setOnClickListener(this);

        passwordEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus){
                //Check if the password meet meet the guideline
                if (Helper.isPasswordValid(passwordEditText.getText().toString())){
                    weakPasswordTextView.setTextColor(getResources().getColor(R.color.colorInvalid, null));
                }
                else{
                    weakPasswordTextView.setTextColor(Color.BLACK);
                }
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

    public interface OnFragmentInteractionListener {
        void beginFragment(StartUpActivityContainer.AuthFragmentType fragmentType, boolean setTransition,
                           boolean addToBackStack);
        void onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reset_password_email_button:
                userEmail = emailEditText.getText().toString();
                if(Helper.isEmailValid(emailEditText.getText().toString())){
                    invalidEmailTextView.setVisibility(View.GONE);
                    new GrpcForgetPassword(getEmailLayout, resetPasswordVerificationLayout, mListener, invalidEmailTextView).execute(
                            "0",
                            userEmail
                    );
                }else{
                    invalidEmailTextView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.reset_password_submit_authen_code_button:
                new GrpcForgetPassword(resetPasswordVerificationLayout, newPasswordLayout, mListener, invalidAuthenCodeTextView).execute(
                        "1",
                        userEmail,
                        verificationCodeEditText.getText().toString()
                );
                break;
            case R.id.reset_password_submit_new_password_button:
                //Check if password and confirm password match
                if (passwordEditText.getText().toString().equals(confirmPasswordEditText.getText().toString())) {
                    new GrpcForgetPassword(newPasswordLayout, null, mListener, weakPasswordTextView).execute(
                            "2",
                            userEmail,
                            passwordEditText.getText().toString()
                    );
                }
                else{
                    mismatchPasswordTextViuew.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private static class GrpcForgetPassword extends AsyncTask<String, Void, Naturae.Status> {
        private OnFragmentInteractionListener mListener;
        private ManagedChannel channel;
        private WeakReference<TextView> errorTextView;
        private WeakReference<LinearLayout> currLayout;
        private WeakReference<LinearLayout> nextLayout;

        private GrpcForgetPassword(LinearLayout currLayout, LinearLayout nextLayout, OnFragmentInteractionListener mListener,
                                   TextView errorTextView){
            this.currLayout = new WeakReference<> (currLayout);
            this.nextLayout = new WeakReference<>(nextLayout) ;
            this.errorTextView = new WeakReference<>(errorTextView);
            this.mListener = mListener;
        }

        @Override
        protected Naturae.Status doInBackground(String... params) {
            Naturae.Status reply = null;
            try{
                //Create a channel to connect to the server
                channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
                //Create a stub for with the channel
                ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
                //Send the email to the server
                switch (params[0]) {
                    case "0": {
                        Naturae.ForgetPasswordRequest request = Naturae.ForgetPasswordRequest.newBuilder()
                                .setAppKey(Constants.NATURAE_APP_KEY)
                                .setEmail(params[1]).build();
                        reply = stub.forgetPassword(request).getStatus();
                        break;
                    }
                    //Send the verification code to the server
                    case "1": {
                        Naturae.ForgetPasswordVerifyCodeRequest request = Naturae.ForgetPasswordVerifyCodeRequest.newBuilder()
                                .setAppKey(Constants.NATURAE_APP_KEY)
                                .setEmail(params[1])
                                .setVerificationCode(params[2])
                                .build();
                        reply = stub.forgetPasswordVerifyCode(request).getStatus();
                        break;
                    }
                    //Send the new password to the server
                    case "2": {
                        Naturae.ForgetPasswordNewPasswordRequest request = Naturae.ForgetPasswordNewPasswordRequest.newBuilder()
                                .setAppKey(Constants.NATURAE_APP_KEY)
                                .setEmail(params[1])
                                .setPassword(params[2])
                                .build();
                        reply = stub.forgetPasswordResetPassword(request).getStatus();
                        break;
                    }
                }
            }catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
            }

            return reply;
        }

        @Override
        protected void onPostExecute(Naturae.Status status) {
            super.onPostExecute(status);
            //Shut down the gRPC channel
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println();
                Thread.currentThread().interrupt();
            }

            if (status.getCode() == Constants.OK){
                //Remove the current displaying layout
                currLayout.get().setVisibility(View.GONE);
                if (nextLayout.get() != null){
                    //Display the next layout
                    nextLayout.get().setVisibility(View.VISIBLE);
                }
                else{
                    mListener.onBackPressed();
                }
            }
            else {
                errorTextView.get().setVisibility(View.VISIBLE);
            }
        }
    }
}
