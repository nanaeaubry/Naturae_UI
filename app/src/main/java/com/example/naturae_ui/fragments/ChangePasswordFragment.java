package com.example.naturae_ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.naturae_ui.R;
import com.example.naturae_ui.util.UserUtilities;


public class ChangePasswordFragment extends Fragment{
    private static final String TAG = "ChangePasswordFragment";
    View view;
    EditText currentPass;
    EditText newPass;
    EditText confirmPass;
    Button btChangePassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        currentPass = view.findViewById(R.id.etCurrentPass);
        newPass = view.findViewById(R.id.etNewPass);
        confirmPass = view.findViewById(R.id.etConfirmNewPass);
        btChangePassword = view.findViewById(R.id.btSubmit);


        btChangePassword.setOnClickListener(new View.OnClickListener() {
        @Override
            public void onClick(View v) {
                boolean checkPass = true;
                while(checkPass)
                {
                    if(currentPass == view.findViewById(R.id.password_edit_text))
                    {

                        if(newPass == confirmPass){
                            currentPass = newPass;
                            //UserUtilities.getAccessToken();
                            Log.d(TAG, "onClick: It works!");
                        }
                        else{
                            checkPass = false;
                        }
                    }
                    else{
                        checkPass = false;
                    }

                }



            }
        });



        return view;
    }
    /*private static class GrpcChangePassword extends AsyncTask<String, Void, Naturae.ChangePasswordReply> {
        private final ChangePasswordFragment.OnFragmentInteractionListener mListener;
        private final WeakReference<Activity> activity;
        private ManagedChannel channel;
        private String firstName, lastName, email;

        private GrpcCreateAccount(CreateAccountFragment.OnFragmentInteractionListener mListener, Activity activity){
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
        /*private void displayError(String message){
            //Create an instance of Alert Dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity.get());
            alertDialogBuilder.setTitle("Error").setMessage(message).setPositiveButton(R.string.ok, (dialog, which) -> {
                dialog.cancel();
            }).show();
        }*/
    }