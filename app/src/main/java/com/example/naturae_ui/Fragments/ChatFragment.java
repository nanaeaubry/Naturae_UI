package com.example.naturae_ui.Fragments;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import com.example.naturae_ui.Util.*;
import com.example.naturae_ui.R;

/**
 * Frontend logic of Naturae's real-time messaging service to setup views and connection tasks
 */
public class ChatFragment extends Fragment {
    private EditText editText;
    private ChatAdapter chatMessageAdapter;
    private ListView chatListView;

    public ChatFragment(){
        //Constructor initialize which friend was selected
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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
        //Setup adapter
        chatMessageAdapter = new ChatAdapter(getContext());
        RecyclerView.setAdapter(adapter);
        /*
        chatMessageAdapter.setClickListener(new FriendAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick position: " + position);
            }
        });
        */
        editText = (EditText) view.findViewById(R.id.editText);


        chatListView = (ListView) view.findViewById(R.id.messages_view);
        //ListView has its data populated using an adapter
        chatListView.setAdapter(chatMessageAdapter);

        /*


        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
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
     * @param view
     */
    public void sendMessage(View view){
        String textMessage = editText.getText().toString();

        ///todo
        //send to server and process

        //Clear the input field
        editText.getText().clear();
    }

}
