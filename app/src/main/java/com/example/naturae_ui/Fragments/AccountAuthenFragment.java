package com.example.naturae_ui.Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.naturae_ui.R;
import com.example.naturae_ui.Util.Constants;
import com.example.naturae_ui.Util.UserUtilities;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;

import java.lang.ref.WeakReference;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


public class AccountAuthenFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String userEmail;

    private TextView authenMessageTextView;
    private Button verifyButton;

    public AccountAuthenFragment() {
        userEmail = "****";
        // Required empty public constructor
    }

    public static AccountAuthenFragment newInstance() {
        AccountAuthenFragment fragment = new AccountAuthenFragment();
        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_authen, container, false);
        authenMessageTextView = view.findViewById(R.id.authentication_message_text_view);
        verifyButton = view.findViewById(R.id.verify_button);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authenMessageTextView.setText(String.format("%s %s", getString(R.string.authentication_message), userEmail));
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
        void showProgressBar();
        void hideProgressBar();
    }

    public void setSendEmail(String email) {
        userEmail = email.substring(0, 3) + userEmail + email.substring(email.indexOf("@"));
    }

    private static class AccountAuthenGRPC extends AsyncTask<String, Void, Boolean>{
        private final AccountAuthenFragment.OnFragmentInteractionListener mListener;
        private final WeakReference<Activity> activity;
        private ManagedChannel channel;

        private AccountAuthenGRPC(AccountAuthenFragment.OnFragmentInteractionListener mListener, Activity activity){
            this.mListener = mListener;
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            //Create a channel to connect to the server
            channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
            //Create a stub for with the channel
            ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
            Naturae.AccountAuthenRequest request = Naturae.AccountAuthenRequest.newBuilder().setAppKey(Constants.NATURAE_APP_KEY)
                    .setEmail(UserUtilities.getEmail()).build();
            Naturae.AccountAuthenReply reply= stub.accountAuthentication(request);

            return reply.getResult();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //If result is true then authentication code is valid
            //The program will now bring the user to the main application page
            if (result){
                //Cache that the user is able to logged in successfully
                //Next time the user's open the app the user's don't have to log in again
                UserUtilities.setIsLoggedIn(true);
            }
            //The result is fault then the authentication code is not valid
            //The application will display an error message and ask the user to enter the
            //authentication code in again.The user will have a total of 7 attempts to enter the correct code.
            //If the user's are not able to enter the correct code before using all 7 attempts
            //then the server will generate a new authentication code and the new code will be send to the
            //email that the user used to create the account with
            else{

            }
        }
    }
}
