package com.example.naturae_ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.naturae_ui.R;
import com.example.naturae_ui.util.Constants;
import com.example.naturae_ui.util.Helper;
import com.example.naturae_ui.util.UserUtilities;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


public class AccountAuthenFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String displayEmail;


    private TextView authenMessageTextView;
    private EditText authenCodeEditText;

    public AccountAuthenFragment() {
        displayEmail = "****";
        // Required empty public constructor
    }

    public static AccountAuthenFragment newInstance() {
        return new AccountAuthenFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        mListener.hideProgressBar();
        super.onResume();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_authen, container, false);
        authenMessageTextView = view.findViewById(R.id.authentication_message_text_view);
        Button verifyButton = view.findViewById(R.id.verify_button);
        TextView authenErrorMessageTextView = view.findViewById(R.id.authen_error_text_view);
        authenCodeEditText = view.findViewById(R.id.authentication_code_edit_text);

        verifyButton.setOnClickListener(v -> {
            //Check if the authen code edit text is empty. If it's empty then an error message will
            //pop-up to ask the user to enter the authentication code
            if (authenCodeEditText.getText().toString().isEmpty()){
                authenErrorMessageTextView.setText(R.string.empty_authen_code_field);
            }
            else{
                new GrpcAccountAuthen(mListener, getActivity()).execute(
                        authenCodeEditText.getText().toString()
                );
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authenMessageTextView.setText(String.format("%s %s", getString(R.string.authentication_message),
                hideUserEmail(UserUtilities.getEmail(getContext()))));
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
        void hideProgressBar();
        void startMainActivity();
    }

    /**
     * Hide the user's full email from displaying
     * @param email user email
     * @return a modify version of user's email
     */
    public String hideUserEmail(String email) {
        return email.substring(0, 3) + displayEmail + email.substring(email.indexOf("@"));
    }

    //Create an async task for account authentication
    private static class GrpcAccountAuthen extends AsyncTask<String, Void, Naturae.AccountAuthenReply>{
        private final AccountAuthenFragment.OnFragmentInteractionListener mListener;
        private final WeakReference<Activity> activity;
        private ManagedChannel channel;

        //Create an constructor for the async task
        private GrpcAccountAuthen(AccountAuthenFragment.OnFragmentInteractionListener mListener, Activity activity){
            this.mListener = mListener;
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected Naturae.AccountAuthenReply doInBackground(String... strings) {
            Naturae.AccountAuthenReply reply;
            try{
                //Create a channel to connect to the server
                channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
                //Create a stub for with the channel
                ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
                //Create a GRPC request to the server for account authentication
                Naturae.AccountAuthenRequest request = Naturae.AccountAuthenRequest.newBuilder().setAppKey(Constants.NATURAE_APP_KEY).setFirstName(UserUtilities.getFirstName(activity.get()))
                        .setEmail(UserUtilities.getEmail(activity.get())).build();
                //Request the send and set reply equal to the response back from the server
                reply = stub.accountAuthentication(request);

            }
            //Handle an error that might occurred when communicating with the server
            catch(Exception e){
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return null;
            }

            return reply;
        }

        @Override
        protected void onPostExecute(Naturae.AccountAuthenReply result) {
            //If result is true then authentication code is valid
            //The program will now bring the user to the main application page
            if (result.getResult()){
                //Cache that the user is able to logged in successfully
                //Next time the user's open the app the user's don't have to log in again
                UserUtilities.setIsLoggedIn(activity.get(), true);
                //Start the main activity
                mListener.startMainActivity();
            }
            //The result is fault then the authentication code is not valid
            //The application will display an error message and ask the user to enter the
            //authentication code in again.The user will have a total of 7 attempts to enter the correct code.
            //If the user's are not able to enter the correct code before using all 7 attempts
            //then the server will generate a new authentication code and the new code will be send to the
            //email that the user used to create the account with
            else{
                //Initialize the error message text view
                TextView authenErrorTextView = activity.get().findViewById(R.id.authen_error_text_view);
                //Set the error message text view to be visible
                authenErrorTextView.setVisibility(View.VISIBLE);
                //The authen code entered is invalid
                if (result.getStatus().getCode() == Constants.INVALID_AUTHEN_CODE){
                    //Display invalid authen error message
                    authenErrorTextView.setText(R.string.invalid_authen_code);
                }
                //The authen code had expired already
                else if (result.getStatus().getCode() == Constants.EXPIRED_AUTHEN_CODE){
                    //Display authen code had expired error message
                    authenErrorTextView.setText(R.string.expired_authen_code);
                }
                //Any other error
                else{
                    Helper.alertDialogErrorMessage(activity.get(), activity.get().getText(R.string.server_error).toString());
                }

            }
        }
    }
}
