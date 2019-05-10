package com.example.naturae_ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.naturae_ui.R;
import com.example.naturae_ui.models.ChatMessage;
import com.example.naturae_ui.models.MemberData;
import com.example.naturae_ui.util.ChatAdapter;
import com.example.naturae_ui.util.Constants;
import com.example.naturae_ui.util.CustomEditText;
import com.example.naturae_ui.util.Helper;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaledrone.lib.Listener;
import com.scaledrone.lib.Room;
import com.scaledrone.lib.RoomListener;
import com.scaledrone.lib.Scaledrone;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;


/**
 * Frontend logic of Naturae's real-time messaging service to setup views and connection tasks
 */
public class ChatFragment extends Fragment implements RoomListener {
    private static final String TAG = "ChatFragment";
    private final String channelID = "rff299Lg3qBpyQxQ";
    private String roomName;
    private TextView friendUsernameTitle;
    private CustomEditText messageInput;
    private View sendButton;
    private String friendUsernameText;
    private String currentUsernameText;
    private ChatAdapter adapter;
    private MemberData thisUser;
    private Scaledrone scaledrone;
    private Boolean lostConnection;
    private String USERNAME;
    private String avatar;
    private FragmentActivity chatActivity;
    private OnFragmentInteractionListener mListener;
    ArrayList<ChatMessage> chatlog;

    public interface OnFragmentInteractionListener{
        void hideBottomNavBar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        chatActivity = getActivity();
        friendUsernameText = getArguments().getString("argUsername");
        currentUsernameText = getArguments().getString("argCurrentUser");
        //The avatar of the other user
        avatar = getArguments().getString("friendAvatar");
        thisUser = new MemberData(currentUsernameText);
        chatlog = new ArrayList<ChatMessage>();

        //Clone the arrayList from cache, error, java.lang.String cannot be cast to java.util.ArrayList
        //chatlog = new ArrayList<ChatMessage>(UserUtilities.getChatlog(getContext()));

        Log.d(TAG, "onCreate: user1" + thisUser.getUsername());
        Log.d(TAG, "onCreate: user2" + friendUsernameText);
        /**
         * Call asynchronous task to obtain room name
         */
        GrpcGetRoomTask roomTask = new GrpcGetRoomTask(new GrpcGetRoomTask.GetRoomRunnable(currentUsernameText, friendUsernameText), getActivity());
        roomTask.setListener(room -> {
            //Create the scaledrone observable room
            roomName = "observable-" + room;
            scaledrone = new Scaledrone(channelID, thisUser);

            scaledrone.connect(new Listener() {
                @Override
                public void onOpen() {
                    //Pass RoomListener as a target
                    scaledrone.subscribe(roomName, ChatFragment.this);
                    System.out.println("Scaledrone connection open");
                }

                @Override
                public void onOpenFailure(Exception e) {
                    System.err.println(e);
                    //Can potentially happen due to authentication error
                    Log.d(TAG, "onOpenFailure: Unable to open a new room " + e);
                }

                @Override
                public void onFailure(Exception e) {
                    System.err.println(e);
                    Log.d(TAG, "onFailure: Connection failure");
                    //Attempt to Reconnect after some time
                    tryReconnecting(0);
                }

                @Override
                public void onClosed(String reason) {
                    System.err.println(reason);
                }
            });

        });
        roomTask.execute();

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
        //todo
        //Instantiate Avatar?

        //Instantiate message textfield for user to type in
        messageInput = view.findViewById(R.id.message_edit_text);
        sendButton = view.findViewById(R.id.sendButton);

        messageInput.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus){
                mListener.hideBottomNavBar();
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
        });

        messageInput.setOnClickListener(v ->{
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
            mListener.hideBottomNavBar();
        });

        messageInput.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                sendMessage();
                return true;
            }
            return false;
        });

        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        //Setup adapter
        adapter = new ChatAdapter(getContext(), chatlog, avatar);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager chatLayout = new LinearLayoutManager(getContext());
        chatLayout.setReverseLayout(true);
        recyclerView.setLayoutManager(chatLayout);

        /*
        chatMessageAdapter.setClickListener(new FriendAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick position: " + position);
            }
        });
        */

        try{
            if(getActivity().getCurrentFocus() != null) {

                recyclerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        return false;
                    }
                });
            }
        }catch(NullPointerException e){

        }

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
        super.onAttach(context);
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
     * Obtain the room id for the conversation
     */
    public void createRoom(){
            //todo invoke gRPC call to retrieve the room id for the conversation
    }

    // Successfully connected to a Scaledrone room
    @Override
    public void onOpen(Room room) {
        Log.d(TAG, "onOpen: Connected to a room successfully:" + roomName + " actual:" + room.getName());
    }

    // Connecting to Scaledrone room failed
    @Override
    public void onOpenFailure(Room room, Exception e) {
        Log.d(TAG, "onOpenFailure: Unable to connect to a room successfully" + e);
    }


    // Eventhandler for receiving a message from the Scaledrone room
    @Override
    public void onMessage(Room room, com.scaledrone.lib.Message receivedMessage) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final MemberData user = mapper.treeToValue(receivedMessage.getMember().getClientData(), MemberData.class);

            //Check if the username matches the current user
            boolean isSentByUser = user.getUsername().equals(thisUser.getUsername());
            long timestamp = receivedMessage.getTimestamp();
            //Construct a new chat message from the received data
            final ChatMessage message = new ChatMessage(receivedMessage.getData().asText(), user.getUsername(), timestamp, isSentByUser, avatar);
            Log.d(TAG, "onMessage: " + message.getMessageBody());

            chatActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatlog.add(0, message);
                    //adapter.notifyItemInserted(0);
                    //We refresh the entire list to update timestamps everytime
                    adapter.notifyDataSetChanged();

                    //Update log in userutilities
                    //UserUtilities.setChatLog(getContext(), chatlog);
                }
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            Log.d(TAG, "onMessage: Error processing a received message");
        }
    }

    /**
     * onClick handler for Message Send
     * Retrieves textfield with id 'editText'
     */
    public void sendMessage(){
        String message = messageInput.getText().toString();

        if (message.length() > 0) {
            scaledrone.publish(roomName, message);
            messageInput.getText().clear();
            Log.d(TAG, "Message Sent: " + message);
        }

    }

    /**
     * Restablish a scaledrone connection if dropped
     * @param reconnectAttempt
     */
    private void tryReconnecting(final int reconnectAttempt) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final Scaledrone drone = new Scaledrone(roomName);
                drone.connect(new Listener() {
                    @Override
                    public void onFailure(Exception ex) {
                        tryReconnecting(reconnectAttempt + 1);
                        Log.d(TAG, "!onFailure!: Reconnecting again: attempt: " + (reconnectAttempt + 1));
                    }

                    @Override
                    public void onOpen() {
                        //Pass RoomListener as a target
                        scaledrone.subscribe(roomName, ChatFragment.this);
                        System.out.println("Scaledrone connection open");
                    }

                    @Override
                    public void onOpenFailure(Exception e) {
                        System.err.println(e);
                        //Can potentially happen due to authentication error
                        Log.d(TAG, "onOpenFailure: Unable to open a new room " + e);
                    }

                    @Override
                    public void onClosed(String reason) {
                        System.err.println(reason);
                    }
                });
            }

        }, reconnectAttempt * 1000);
    }

    /**
     * Retrieves the room ID so that the users can connect to the proper chatroom
     */
    private static class GrpcGetRoomTask extends AsyncTask<Void, Void, Naturae.RoomReply> {
        private final GetRoomRunnable grpcRunnable;
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;
        private AsyncTaskListener listener;

        GrpcGetRoomTask(GetRoomRunnable grpcRunnable, Activity activity){
            this.grpcRunnable = grpcRunnable;
            this.activityReference = new WeakReference<>(activity);
            this.channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
        }

        @Override
        protected Naturae.RoomReply doInBackground(Void... nothing) {
            try {
                Naturae.RoomReply result = grpcRunnable.run(ServerRequestsGrpc.newBlockingStub(channel));
                Log.d(TAG, "*Successfully created Room Reply response*\n");
                return result;
            } catch (Exception e) {
                StringWriter error = new StringWriter();
                PrintWriter pw = new PrintWriter(error);
                e.printStackTrace(pw);
                pw.flush();
                Log.d(TAG, "doInBackground: Error Exception caught while trying to build a Room Reply: \n" + error);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Naturae.RoomReply result) {
            super.onPostExecute(result);

            if(result != null){
                String roomID = result.getRoomName();
                //Callback function
                listener.onGetRoomFinished(roomID);
                Log.d(TAG, "onPostExecute: ROOM NAME " + roomID);

            }
            else{
                Helper.alertDialogErrorMessage(activityReference.get(), "An error occurred while trying to retrieve the conversation, please check your connection");
            }

            //Shut down the gRPC channel
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void setListener(AsyncTaskListener listener) {
            this.listener = listener;
        }

        public interface AsyncTaskListener {
            void onGetRoomFinished(String room);
        }

        private static class GetRoomRunnable {
            private String user1, user2;

            public GetRoomRunnable(String user1, String user2){
                this.user1 = user1;
                this.user2 = user2;
            }
            // gRPC SEARCHUSERS CALL handler, Service (UserSearchRequest) returns (UserListReply)
            public Naturae.RoomReply run(ServerRequestsGrpc.ServerRequestsBlockingStub blockingStub) throws StatusRuntimeException {
              //  Log.d(TAG, "run: user1: " + user1);
              //  Log.d(TAG, "run: user2: " + user2);
                Naturae.RoomReply reply;
                //Generate Request as defined by proto definition
                Naturae.RoomRequest request = Naturae.RoomRequest.newBuilder().setUserOwner1(user1.trim()).setUserOwner2(user2.trim()).build();
           //     Log.d(TAG, "runREQ: user1: " + request.getUserOwner1());
               // Log.d(TAG, "runREQ: user2: " + request.getUserOwner2());
                //Send the request to the server and set reply to the server response
                reply = blockingStub.getRoomName(request);

               // Log.d(TAG, "run: REPLY NAME "  + reply.getRoomName() );
               // Log.d(TAG, "run: STATUS FROM REPLY " + reply.getStatus().getMessage());
                //withDeadlineAfter(15000, TimeUnit.MILLISECONDS)
                return reply;
            }
        }
    } //End of Async Task Class


}

/*
    //************SAMPLE DATA******************************************
        Log.d(TAG, "initRecyclerView: init recyclerview");

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

   */