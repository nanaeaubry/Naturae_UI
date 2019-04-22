package com.example.naturae_ui.fragments;

import android.content.Context;
import android.net.Uri;
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
import com.example.naturae_ui.util.Constants;
import com.example.naturae_ui.util.Helper;
import com.example.naturae_ui.util.UserUtilities;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;

import java.io.PrintWriter;
import java.io.StringWriter;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class ResetPasswordFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;
    private TextView invalidEmailTextView, invalidAuthenCodeTextView, weakPasswordTextView, mismatchPasswordTextViuew;
    private EditText emailEditText, authenCodeEditText, passwordEditText, confirmPasswordEditText;
    private LinearLayout getEmailLayout, resetPasswordAuthenLayout, newPasswordLayout;

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    public static ResetPasswordFragment newInstance() {
        return new ResetPasswordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

        //Initialize all of the views
        Button submitPasswordEmailButton = view.findViewById(R.id.reset_password_email_button);
        Button submitAuthenCodeButton = view.findViewById(R.id.reset_password_submit_authen_code_button);
        Button submitNewPasswordButton = view.findViewById(R.id.reset_password_submit_new_password_button);

        invalidEmailTextView = view.findViewById(R.id.reset_password_invalid_email_text_view);
        invalidAuthenCodeTextView = view.findViewById(R.id.forget_password_invalid_authen_text_view);
        weakPasswordTextView = view.findViewById(R.id.reset_password_weak_password_text_view);
        mismatchPasswordTextViuew = view.findViewById(R.id.reset_password_mismatch_password_text_view);

        emailEditText = view.findViewById(R.id.reset_password_email_edit_text);
        authenCodeEditText = view.findViewById(R.id.reset_password_authen_code_edit_text);
        passwordEditText = view.findViewById(R.id.forget_password_new_password);
        confirmPasswordEditText = view.findViewById(R.id.reset_password_confirm_password);

        getEmailLayout = view.findViewById(R.id.reset_password_get_email_layout);
        resetPasswordAuthenLayout = view.findViewById(R.id.reset_password_authen_code_layout);
        newPasswordLayout = view.findViewById(R.id.new_password_layout);

        submitAuthenCodeButton.setOnClickListener(this::onClick);
        submitPasswordEmailButton.setOnClickListener(this::onClick);
        submitNewPasswordButton.setOnClickListener(this::onClick);


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

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reset_password_email_button:
                if(Helper.isEmailValid(emailEditText.getText().toString())){

                }else{
                    invalidEmailTextView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.reset_password_submit_authen_code_button:
                break;
            case R.id.reset_password_submit_new_password_button:
                break;
        }
    }

    private static class GrpcForgetPassword extends AsyncTask<String, Void, Naturae.Status> {

        private ManagedChannel channel;
        @Override
        protected Naturae.Status doInBackground(String... params) {
            Naturae.Status reply = null;
            try{
                //Create a channel to connect to the server
                channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
                //Create a stub for with the channel
                ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
                //Send the email to the server
                if (params[0] == "0"){
                    Naturae.ForgetPasswordRequest request = Naturae.ForgetPasswordRequest.newBuilder()
                            .setAppKey(Constants.NATURAE_APP_KEY)
                            .setEmail(params[1]).build();
                    reply = stub.forgetPassword(request).getStatus();
                }
                //Send the verification code to the server
                else if(params[0] == "1"){
                    Naturae.ForgetPasswordAuthenRequest request = Naturae.ForgetPasswordAuthenRequest.newBuilder()
                            .setAppKey(Constants.NATURAE_APP_KEY)
                            .setAuthenCode(Integer.parseInt(params[2]))
                            .build();

                }
                //Send the new password to the server
                else if(params[0] == "2") {

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
        }
    }
}
