package com.example.naturae_ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.naturae_ui.R;
import com.example.naturae_ui.server.NaturaeUser;
import com.example.naturae_ui.util.Constants;
import com.example.naturae_ui.util.Helper;
import com.example.naturae_ui.util.UserUtilities;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


public class ChangePasswordFragment extends Fragment {
    private static final String TAG = "ChangePasswordFragment";
    View view;
    EditText currentPass;
    EditText newPass;
    EditText confirmPass;
    EditText currPass;
    Button bSubmit;
    OnFragmentInteractionListener mListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_change_password, container, false);
        super.onCreate(savedInstanceState);

        currentPass = view.findViewById(R.id.etCurrentPass);
        newPass = view.findViewById(R.id.etNewPass);
        confirmPass = view.findViewById(R.id.etConfirmNewPass);
        bSubmit = view.findViewById(R.id.btSubmit);
        currPass = view.findViewById(R.id.password_edit_text);

        bSubmit.setOnClickListener(v -> {

            if(Helper.isPasswordValid(newPass.getText().toString()))
            {
                if(newPass.getText().toString().equals(confirmPass.getText().toString()))
                {
                    Toast.makeText(getActivity(),"Password Changed!",Toast.LENGTH_SHORT).show();
                    new GrpcChangePassword(mListener,getActivity()).execute(
                            currentPass.getText().toString(),
                            newPass.getText().toString()
                    );
                }



            }
        });

        return view;
    }

    public interface OnFragmentInteractionListener {
        void startProfileFragment();
    }

    private static class GrpcChangePassword extends AsyncTask<String, Void, Naturae.ChangePasswordReply> {
        private final ChangePasswordFragment.OnFragmentInteractionListener mListener;
        private final WeakReference<Activity> activity;
        private ManagedChannel channel;
        private String currentPassword, newPassword;

        private GrpcChangePassword(ChangePasswordFragment.OnFragmentInteractionListener mListener, Activity activity) {
            this.mListener = mListener;
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected Naturae.ChangePasswordReply doInBackground(String... params) {
            Naturae.ChangePasswordReply reply;
            try {
                //Create a channel to connect to the server
                channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
                //Create a stub for with the channel
                ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
                currentPassword = params[0];
                newPassword = params[1];
                //Create an gRPC create account request
                Naturae.ChangePasswordRequest request = Naturae.ChangePasswordRequest.newBuilder()
                        .setAppKey(Constants.NATURAE_APP_KEY)
                        .setAccessToken(UserUtilities.getAccessToken(activity.get()))
                        .setCurrentPassword(currentPassword)
                        .setNewPassword(newPassword).build();

                //Send the request to the server and set reply to equal the response back from the server
                reply = stub.changePassword(request);

            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return null;
            }
            return reply;

        }

        @Override
        protected void onPostExecute(Naturae.ChangePasswordReply reply) {
            super.onPostExecute(reply);
            //Shut down the gRPC channel
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            //Check if reply is equal to null. If it's equal to null then there was an error with the server or phone
            //while communicating with the server.
            if (reply != null) {
                //Check the status of the request
                //If the status code is 201, then the account was able to created successfully
                //If the status code is 150, then there already an account with that email address
                //Any thing else then the an server error
                if (reply.getStatus().getCode() == Constants.OK) {
                    mListener.startProfileFragment();
                } else if (reply.getStatus().getCode() == Constants.EMAIL_EXIST) {
                    //Display an error message that an account with the email already exist
                    displayError((String) activity.get().getText(R.string.email_exist));
                } else {
                    //Display an create account error
                    displayError((String) activity.get().getText(R.string.create_account_error));
                }
            } else {

            }

        }

        /**
         * Create an dialog box that display the error
         *
         * @param message the error message to be display
         */
        private void displayError(String message) {
            //Create an instance of Alert Dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity.get());
            alertDialogBuilder.setTitle("Error").setMessage(message).setPositiveButton(R.string.ok, (dialog, which) -> {
                dialog.cancel();
            }).show();
        }
    }
}