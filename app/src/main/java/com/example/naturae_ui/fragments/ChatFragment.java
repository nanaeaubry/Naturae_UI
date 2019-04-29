package com.example.naturae_ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.support.v4.app.Fragment;

import com.example.naturae_ui.models.ChatMessage;
import com.example.naturae_ui.util.*;
import com.example.naturae_ui.R;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Frontend logic of Naturae's real-time messaging service to setup views and connection tasks
 */
public class ChatFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ChatFragment";
    private TextView friendUsernameTitle;
    private EditText messageInput;
    private String friendUsernameText;
    private ChatAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        friendUsernameText = getArguments().getString("argUsername");
    }

    /**
     * Called automatically when time to create the views
     * Initializes fields from view references
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //Attach xml to view object
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.chat_recycler);

        //Set the title of the chat window as the friend's username
        friendUsernameTitle = view.findViewById(R.id.chat_friend_title);
        friendUsernameTitle.setText(friendUsernameText);

        //Instantiate message textfield for user to type in
        messageInput = view.findViewById(R.id.editText);

    //************SAMPLE DATA******************************************
        Log.d(TAG, "initRecyclerView: init recyclerview");

        List<ChatMessage> chatlog = new ArrayList<ChatMessage>();
        chatlog.add(new ChatMessage("I used to rule the world\n" +
                "Seas would rise when I gave the word\n" +
                "Now in the morning, I sleep alone\n" +
                "Sweep the streets I used to own", "WoozyMango", "sampleTimestamp", true));
        chatlog.add(new ChatMessage("I hear Jerusalem Bells Ringing", "WoozyMango", "sampleTimestamp", true));
        chatlog.add(new ChatMessage("Roman Cavalry and Choirs are singing", "lazerman7", "sampleTimestamp", false));
        chatlog.add(new ChatMessage("Be my mirror, my sword and shield\n" +
                "My missionaries in a foreign field\n" +
                "For some reason I can't explain\n" +
                "Once you go there was never, never a honest word", "lazerman7", "sampleTimestamp", false));
        chatlog.add(new ChatMessage(":^)", "WoozyMango", "sampleTimestamp", true));
        chatlog.add(new ChatMessage("   Hello it me", "WoozyMango", "sampleTimestamp", true));
        //************SAMPLE DATA******************************************


        //Setup adapter
        adapter = new ChatAdapter(getContext(), chatlog);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /*
        chatMessageAdapter.setClickListener(new FriendAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick position: " + position);
            }
        });
        */
        return view;
    }

    /**
     * Commits any changes that should be persisted beydond the current user session
     * The system calls this method as the first indication that the user is leaving the fragment
     */
    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     *
     * @param view
     */
    @Override
    public void onClick(View view){

        switch(view.getId()){
            case R.id.editText:
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(messageInput, InputMethodManager.SHOW_IMPLICIT);
                break;

            case R.id.sendButton:
                sendMessage();
                break;
        }
    }

    public void onConnect(){
        System.out.println("");
    }

    public void onConnectFailure(){
        System.err.println("");
    }

    //Message Received
    public void receiveMessage() {
        // TODO
        /*
        try{
            final ChatMessage message = new ChatMessage(params);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ChatAdapter.add(message);
                    // scroll the ListView to the last added element
                    chatListView.setSelection(chatListView.getCount() - 1);
                }
            });
        }
        */
    }

    /**
     * onClick handler for Message Send
     * Retrieves textfield with id 'editText'
     */
    public void sendMessage(){
        Log.d(TAG, "Message Sent");
       // String textMessage = editText.getText().toString();

        ///todo
        //send to server and process

        //Clear the input field
        //editText.getText().clear();
    }

}
