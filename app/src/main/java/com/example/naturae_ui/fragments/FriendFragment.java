package com.example.naturae_ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.naturae_ui.R;
import com.example.naturae_ui.models.Friend;
import com.example.naturae_ui.util.Constants;
import com.example.naturae_ui.util.FriendAdapter;
import com.example.naturae_ui.util.Helper;
import com.example.naturae_ui.util.UserUtilities;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;
import com.google.android.gms.tasks.Task;
import android.view.View.OnTouchListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class FriendFragment extends Fragment {
    private static final String TAG = "FriendFragment";
    private String USERNAME;
    private FriendAdapter adapter;
    private List<Friend> friendsList;
    private List<Friend> searchedFriendsList;
    private String searchQuery;
    private TextInputEditText searchFieldInput;
    private TextView emptyView;
    private TextView friendsListTitle;
    private RecyclerView recyclerView;
    private Button returnButton;
    private View removeButton;
    private View sortButton;
    private ConstraintLayout layout;
    private ProgressBar progress;

    //Cache to define user sort preferences, false means sort in A-Z normal order
    private boolean sortListReverse;
    /**
     * Called to do initial creation of a fragment. This is called after onAttach(Activity) and before onCreateView
     * Note that this can be called while the fragment's activity is still in the process of being created.
     * @param savedInstanceState : If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        friendsList = new ArrayList<Friend>();

        //Testing purposes
        try{
            USERNAME = UserUtilities.getEmail(getContext());
        }
        catch(NullPointerException e){

        }
        if(USERNAME == null){
            //Insert your username here
            Toast.makeText(getContext(),"Unable to get username", Toast.LENGTH_LONG).show();
            USERNAME = "limstevenlbw@gmail.com";
        }

    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater : Used to instantiate an xml file into a corresponding view object
     * @param container : Holds view objects, provides efficient ways to layout the child views
     * @param savedInstanceState : If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_friend, container, false);
        progress = view.findViewById(R.id.progressFriends);
        searchFieldInput = view.findViewById(R.id.search_field_input);
        emptyView = view.findViewById(R.id.empty_view);
        recyclerView = view.findViewById(R.id.friend_recycler);
        friendsListTitle = view.findViewById(R.id.title_friend);

        //Define adapter
        adapter = new FriendAdapter(getContext(), friendsList, "default");

        //Bottom Toolbar
        returnButton = view.findViewById(R.id.viewFriendsButton);
        removeButton = view.findViewById(R.id.removeFriend);
        sortButton = view.findViewById(R.id.sortFriend);

        //Setup Recycler View initial properties
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //Creates a simple divider underneath a view
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        /**
         * SEARCH HANDLER FOR RETRIEVING THE INITIAL FRIENDS LIST, GET FRIENDS
         */
        GrpcSearchFriendsTask buildList = new GrpcSearchFriendsTask(new GrpcSearchFriendsTask.SearchFriendsRunnable(USERNAME, "empty"), getActivity());
        buildList.setListener(new GrpcSearchFriendsTask.AsyncTaskListener() {
            @Override
            public void onSearchFriendsFinished(List<Friend> newFriendsList) {
                //Update dataset
                friendsList = new ArrayList<>(newFriendsList);
                //Disable progress bar
                progress.setVisibility(View.GONE);

                if(friendsList.isEmpty()){
                    //Show the empty list
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }
                else{
                    sortListReverse = UserUtilities.getSortListReverse(getContext());
                    Collections.sort(friendsList);
                    if(sortListReverse){
                        Collections.reverse(friendsList);
                    }
                    adapter.displayNewList(friendsList);
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    displayFriendsList();
                }

            }
        });
        buildList.execute();

        //Defining Handlers

        /**
         * Define Return button
         */
        returnButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Toggle visibility of return button, show friendslist on click
             * @param v the button that was selected
             */
            public void onClick(View v) {
                adapter = new FriendAdapter(getContext(), friendsList, "default");
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                returnButton.setVisibility(View.GONE);
                removeButton.setVisibility(View.VISIBLE);
                sortButton.setVisibility(View.VISIBLE);
                friendsListTitle.setText("Your Friends");

                //Hide the list if the friendslist was empty
                if(friendsList.isEmpty() && emptyView.getVisibility() == View.GONE){
                    emptyView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                }

                displayFriendsList();
            }
        });

        /**
         * Toggle Remove Friends Mode
         */
        removeButton.setOnClickListener(new View.OnClickListener() {
            /**
             *
             * @param v the button that was selected
             */
            public void onClick(View v) {
                FriendAdapter tempAdapter = new FriendAdapter(getContext(), friendsList, "remove");
                recyclerView.setAdapter(tempAdapter);

                //Show return button
                //tempAdapter.displayNewList(friendsList);
                returnButton.setVisibility(View.VISIBLE);
                removeButton.setVisibility(View.GONE);
                sortButton.setVisibility(View.GONE);
                friendsListTitle.setText("Select Users to Remove");

                //ONCLICK EVENT DEFINITION REMOVE A FRIEND
                tempAdapter.setClickListener(new FriendAdapter.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position, Friend friend) {
                        Log.d(TAG, "onItemClick: TEST: " + position + " " + friend.getName());
                        GrpcRemoveFriendTask removeFriend = new GrpcRemoveFriendTask(new GrpcRemoveFriendTask.RemoveFriendRunnable(USERNAME, friend.getName()), getActivity());
                        removeFriend.setListener(new GrpcRemoveFriendTask.AsyncTaskListener(){
                            @Override
                            public void onRemoveFriendFinished() {
                                Log.d(TAG, "onRemoveFriendFinished: ");
                                friendsList.remove(friend);
                                tempAdapter.notifyItemRemoved(position);

                                Toast.makeText(getContext(), friend.getName() + " removed!", Toast.LENGTH_SHORT).show();

                            }
                        });
                        removeFriend.execute();
                    }
                });

            }
        });

        /**
         * Define Sort Button function
         */
        sortButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Toggle sort button preference, cache the preference
             * @param v the button that was selected
             */
            public void onClick(View v) {
                Log.d(TAG, "onFriendClick position: " + "UH SORT BUTTON ACTIVATED");
                sortListReverse = UserUtilities.getSortListReverse(getContext());
                UserUtilities.cacheSortFriendsList(getContext(), !sortListReverse);
                Log.d(TAG, "onFriendClick position: stored cache" + sortListReverse);
                Collections.sort(friendsList);

                if(sortListReverse){
                    Collections.reverse(friendsList);
                }
                adapter.displayNewList(friendsList);
                displayFriendsList();
            }
        });


        /**
         * SEARCH FIELD EVENT HANDLER DEFINITION FOR ADDING FRIENDS
         */
        searchFieldInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean isSearchInProgress = true;

                if(actionId == EditorInfo.IME_ACTION_DONE){
                    //Retrieve the query the user typed
                    searchQuery = searchFieldInput.getText().toString().trim();
                    //Execute only if the field is valid
                    if(searchQuery.length() > 3){
                        GrpcSearchUsersTask search = new GrpcSearchUsersTask(new GrpcSearchUsersTask.SearchUsersRunnable("", searchQuery), getActivity());
                        search.setListener(new GrpcSearchUsersTask.AsyncTaskListener() {
                            @Override
                            public void onSearchTaskFinished(List<Friend> searchedFriendsList) {
                                friendsListTitle.setText("Select Users to Add");
                                adapter = new FriendAdapter(getContext(), searchedFriendsList, "add");
                                //adapter.displayNewList(searchedFriendsList);
                                recyclerView.setAdapter(adapter);

                                //Show list if the friendslist was empty
                                if(emptyView.getVisibility() == View.VISIBLE){
                                    emptyView.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                }

                                returnButton.setVisibility(View.VISIBLE);
                                removeButton.setVisibility(View.GONE);
                                sortButton.setVisibility(View.GONE);
                                adapter.setClickListener(new FriendAdapter.ClickListener() {
                                    /**
                                     * When the client user clicks a user in the searched list, send a request to add them
                                     * @param view
                                     * @param position
                                     * @param friend
                                     */
                                    @Override
                                    public void onItemClick(View view, int position, Friend friend) {
                                        Log.d(TAG, "onItemClick: Adding this friend ->" + friend.getName());
                                        GrpcAddFriendTask addFriend = new GrpcAddFriendTask(new GrpcAddFriendTask.AddFriendRunnable(USERNAME, friend.getName()), getActivity());
                                        addFriend.setListener(new GrpcAddFriendTask.AsyncTaskListener(){
                                            @Override
                                            public void onAddFriendFinished() {
                                                Log.d(TAG, "onAddFriendFinished: ");
                                                searchedFriendsList.remove(friend);
                                                adapter.notifyItemRemoved(position);
                                                friendsList.add(friend);
                                                Toast.makeText(getContext(), friend.getName() + " added!",Toast.LENGTH_SHORT).show();                                              }
                                        });
                                        addFriend.execute();
                                    }
                                });
                            }
                        });
                        search.execute();
                    }
                    else{
                        return true;
                    }
                    //Clear the search field
                    searchFieldInput.getText().clear();
                }
                return false;
            }
        });

        //Set close keyboard event to main layout and recycler view
        layout = view.findViewById(R.id.friends_layout);
        try{
            if(getActivity().getCurrentFocus() != null){

                layout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        return false;
                    }
                });

                recyclerView.setOnTouchListener(new View.OnTouchListener(){
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        return false;
                    }
                });
            }
        }
        catch(NullPointerException E){}


        return view;
    }

    /**
     * Sets up the default friend's list view onclick event
     */
    public void displayFriendsList(){
            friendsListTitle.setText("Your Friends");
            //ONCLICK EVENT DEFINITION
            adapter.setClickListener(new FriendAdapter.ClickListener() {
                @Override
                public void onItemClick(View view, int position, Friend friend) {
                    Log.d(TAG, "onFriendClick position: " + position);

                    //Assemble new Chat Fragment to pass arguments into
                    ChatFragment chatfragment = new ChatFragment();
                    //Creates a new bundle of capacity 1 to pass in arguments: username
                    Bundle bundle = new Bundle(2);
                    bundle.putString("argUsername", friend.getName());
                    bundle.putString("argCurrentUser", USERNAME);
                    chatfragment.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    //Acquire container id and switch fragment
                    fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), chatfragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

    }

    /**
     * Commits any changes that should be persisted beyond the current user session
     * The system calls this method as the first indication that the user is leaving the fragment
     */
    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy() {
        //asyncTask.setListener(null); // PREVENT LEAK AFTER ACTIVITY DESTROYED?
        super.onDestroy();
    }

    /*************************************************************************************************************************************
     * Class sets up Asynchronous Task handling for searching for a search query
     * "Async Generic Types"
     *  -Params, the type of the parameters sent to the task for execution
     *  -Progress, the type of progress units published during computation
     *  -Result, the type of the result of the background computation
     */
    private static class GrpcSearchUsersTask extends AsyncTask<Void, Void, Naturae.UserListReply> {
        private final SearchUsersRunnable grpcRunnable;
        //Activity created as weak reference and can be garbage collected at anytime
        private final WeakReference<Activity> activityReference;
        //A channel is high-level abstraction of a connection, encapsulating TCP, HTTP, Load-balancing behind the scenes
        //Required to a consume a service
        private ManagedChannel channel;
        private AsyncTaskListener listener;

        /**
         * Constructor initializes utilities
         * @param grpcRunnable grpc service runner interface
         * @param activity main activity of Naturae
         */
        GrpcSearchUsersTask(SearchUsersRunnable grpcRunnable, Activity activity){
            this.grpcRunnable = grpcRunnable;
            this.activityReference = new WeakReference<>(activity);
            this.channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
        }

        /**
         * Executes everything inside of another thread, operations in this
         * method should not touch the main thread fragments/activities
         * The parameters of the asynchronous task are passed to this step. The result of the computation must be returned by this step and will be passed back to the last step. This step can also use publishProgress(Progress...) to publish one or more units of progress.
         */
        @Override
        protected Naturae.UserListReply doInBackground(Void... nothing) {
            try{
                Naturae.UserListReply result = grpcRunnable.run(ServerRequestsGrpc.newBlockingStub(channel));
                Log.d(TAG, "*Successfully created response*\n");
                return result;
            }
            catch(Exception e){
                StringWriter error = new StringWriter();
                PrintWriter pw = new PrintWriter(error);
                e.printStackTrace(pw);
                pw.flush();
                Log.d(TAG,"doInBackground: Error Exception caught while trying to build a reply: \n" + error);
                return null;
            }
        }

        /**
         * After the Heavy Lifting, you may update info on ui on results within here
         */
        @Override
        protected void onPostExecute(Naturae.UserListReply result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute: Made it to onPostExecute");

            if(result != null){
                if(result.getUsersList().size() == 0){
                    Helper.alertDialogErrorMessage(activityReference.get(), "We were not able to find a user with that name");
                }
                else{
                    List<Friend> usersList = new ArrayList<>();
                    //Fill the searched users list
                    if(result.getUsersCount() > 0){
                        for(int i=0; i< result.getUsersList().size(); i++) {
                            usersList.add(new Friend(result.getUsersList().get(i)));
                        }
                    }

                    //If yes, update friend's list
                    if (listener != null) {
                        listener.onSearchTaskFinished(usersList);
                    }

                }
            }
            else{
                //Placeholder test
                List<Friend> searchedFriendsList = new ArrayList<Friend>();
                searchedFriendsList.add(new Friend("Alex"));
                searchedFriendsList.add(new Friend("Anita"));

                if (listener != null) {
                    listener.onSearchTaskFinished(searchedFriendsList);
                }

                Helper.alertDialogErrorMessage(activityReference.get(), "An error occurred while trying to connect with the server, please check your connection");
            }

            //Shut down the gRPC channel
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
/*
            if(result != null){
                Log.d(TAG, "onPostExecute: Successfully retrieved result");
            }else{
                Log.d(TAG, "onPostExecute: (REPLY FROM SERVER IS NIL, USING PLACEHOLDER");
                Helper.alertDialogErrorMessage(activityReference.get(), "An error occurred while trying to connect with the server, please check your connection");
            }
            */
        }

        public void setListener(AsyncTaskListener listener) {
            this.listener = listener;
        }

        public interface AsyncTaskListener {
            void onSearchTaskFinished(List<Friend> searchedFriendsList);
        }

        /**
         *  GRPC Service
         *  Query is sent to the server and a list of matching users is returned
         */
        private static class SearchUsersRunnable {
            private String user, query;

            /**
             * Initialize Request Parameters
             * @param user the username of the current user of the client
             * @param query the search query inputted by the current user
             */
            public SearchUsersRunnable(String user, String query){
                this.user = user;
                this.query = query;
            }
            // gRPC SEARCHUSERS CALL handler, Service (UserSearchRequest) returns (UserListReply)
            public Naturae.UserListReply run(ServerRequestsGrpc.ServerRequestsBlockingStub blockingStub) throws StatusRuntimeException {
                Naturae.UserListReply reply;
                //Generate Request as defined by proto definition
                Naturae.UserSearchRequest request = Naturae.UserSearchRequest.newBuilder().setUser(user).setQuery(query).build();

                //Send the request to the server and set reply to the server response
                reply = blockingStub.searchUsers(request);
                //withDeadlineAfter(15000, TimeUnit.MILLISECONDS)
                return reply;
            }
        }
    } //End of Async Task Class

/*************************************************************************************************************************************
    /**
     * Class sets up Asynchronous Task handling for searching for a search query
     * "Async Generic Types"
     *  -Params, the type of the parameters sent to the task for execution
     *  -Progress, the type of progress units published during computation
     *  -Result, the type of the result of the background computation
     */
    private static class GrpcSearchFriendsTask extends AsyncTask<Void, Void, Naturae.UserListReply> {
        private final SearchFriendsRunnable grpcRunnable;
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;
        private AsyncTaskListener listener;

        GrpcSearchFriendsTask(SearchFriendsRunnable grpcRunnable, Activity activity){
            this.grpcRunnable = grpcRunnable;
            this.activityReference = new WeakReference<>(activity);
            this.channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
        }

        @Override
        protected Naturae.UserListReply doInBackground(Void... nothing) {
            try {
                Naturae.UserListReply result = grpcRunnable.run(ServerRequestsGrpc.newBlockingStub(channel));
                Log.d(TAG, "*Successfully created response*\n");
                return result;
            } catch (Exception e) {
                StringWriter error = new StringWriter();
                PrintWriter pw = new PrintWriter(error);
                e.printStackTrace(pw);
                pw.flush();
                Log.d(TAG, "doInBackground: Error Exception caught while trying to build a reply: \n" + error);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Naturae.UserListReply result) {
            super.onPostExecute(result);
            List<Friend> friendsList = new ArrayList<Friend>();
            Log.d(TAG, "onPostExecute: Made it to onPostExecute");
            if(result != null){
                Log.d(TAG, "onPostExecute: Successfully retrieved result, may or may not be empty");

                //Fill the list if a list is available
                if(result.getUsersCount() > 0){
                    for(int i=0; i< result.getUsersList().size(); i++) {
                        friendsList.add(new Friend(result.getUsersList().get(i)));
                        Log.d(TAG, "" + (friendsList.get(i)));
                    }
                }
            }
            else{
         /*
                Log.d(TAG, "onPostExecute: (REPLY FROM SERVER IS NIL, USING PLACEHOLDER");
                friendsList.add(new Friend("Jimmy"));
                friendsList.add(new Friend("Trung"));
                friendsList.add(new Friend("Jacob"));
                friendsList.add(new Friend("Simon"));
                friendsList.add(new Friend("Henry"));
                friendsList.add(new Friend("Catherine"));
                friendsList.add(new Friend("Josh"));
                friendsList.add(new Friend("Colin"));
           */
                Helper.alertDialogErrorMessage(activityReference.get(), "An error occurred while trying to retrieve your friend's list, please check your connection");
            }
            //Callback function
            listener.onSearchFriendsFinished(friendsList);

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
            void onSearchFriendsFinished(List<Friend> newFriendslist);
        }

        /**
         *  GRPC Service
         *  Query is sent to the server and a list of matching users is returned
         */
        private static class SearchFriendsRunnable {
            private String user, query;

            /**
             * Initialize Request Parameters
             * @param user the username of the current user of the client
             * @param query the search query inputted by the current user
             */
            public SearchFriendsRunnable(String user, String query){
                this.user = user;
                this.query = query;
            }
           // gRPC SEARCHUSERS CALL handler, Service (UserSearchRequest) returns (UserListReply)
            public Naturae.UserListReply run(ServerRequestsGrpc.ServerRequestsBlockingStub blockingStub) throws StatusRuntimeException {
                Naturae.UserListReply reply;
                //Generate Request as defined by proto definition
                Naturae.UserSearchRequest request = Naturae.UserSearchRequest.newBuilder().setUser(user).setQuery(query).build();

                //Send the request to the server and set reply to the server response
                reply = blockingStub.searchUsers(request);
                //withDeadlineAfter(15000, TimeUnit.MILLISECONDS)
                return reply;
            }
        }
    } //End of Async Task Class

    /*************************************************************************************************************************************
     /**
     * Class sets up Asynchronous Task handling for searching for a search query
     * "Async Generic Types"
     *  -Params, the type of the parameters sent to the task for execution
     *  -Progress, the type of progress units published during computation
     *  -Result, the type of the result of the background computation
     */
    private static class GrpcRemoveFriendTask extends AsyncTask<Void, Void, Naturae.FriendReply> {
        private final RemoveFriendRunnable grpcRunnable;
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;
        private AsyncTaskListener listener;

        GrpcRemoveFriendTask(RemoveFriendRunnable grpcRunnable, Activity activity){
            this.grpcRunnable = grpcRunnable;
            this.activityReference = new WeakReference<>(activity);
            this.channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
        }

        /**
         * Before the Heavy Lifting, invoked on the UI thread to setup the async tasks, Upon completion, doInBackground() follows up
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Naturae.FriendReply doInBackground(Void... nothing) {
            try {
                Naturae.FriendReply result = grpcRunnable.run(ServerRequestsGrpc.newBlockingStub(channel));
                Log.d(TAG, "*Successfully created response*\n");
                return result;
            } catch (Exception e) {
                StringWriter error = new StringWriter();
                PrintWriter pw = new PrintWriter(error);
                e.printStackTrace(pw);
                pw.flush();
                Log.d(TAG, "doInBackground: Error Exception caught while trying to build a reply: \n" + error);
                return null;
            }
        }

        /**
         * Need to remove the entry from friendslist and update the view accordingly
         * @param result
         */
        @Override
        protected void onPostExecute(Naturae.FriendReply result) {
            super.onPostExecute(result);
            Log.d(TAG, "onPostExecute: Made it to onPostExecute");

            if(result != null){
                //Callback function
                listener.onRemoveFriendFinished();
            }
            else{
                Helper.alertDialogErrorMessage(activityReference.get(), "Unable to get a reply from the server, please check your connection");
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
            void onRemoveFriendFinished();
        }

        /**
         * gRPC runnable for remove friends service
         */
        private static class RemoveFriendRunnable {
            private String sender, receiver;

            /**
             * Constructor
             * @param sender    the user list to modify
             * @param receiver  the user to remove from the friendslist
             */
            public RemoveFriendRunnable(String sender, String receiver) {
                this.sender = sender;
                this.receiver = receiver;
            }

            public Naturae.FriendReply run(ServerRequestsGrpc.ServerRequestsBlockingStub blockingStub) throws StatusRuntimeException {
                Naturae.FriendReply reply;
                //Generate Request as defined by proto definition
                Naturae.FriendRequest request = Naturae.FriendRequest.newBuilder().setSender(sender).setReceiver(receiver).build();
                //Send the request to the server and set reply to the server response
                reply = blockingStub.removeFriend(request);

                return reply;
            }
        } //End of Async Task Class

    }

    /*************************************************************************************************************************************
     * ADD Friend gRPC Asynchronous Task
     */
    private static class GrpcAddFriendTask extends AsyncTask<Void, Void, Naturae.FriendReply> {
        private final AddFriendRunnable grpcRunnable;
        private final WeakReference<Activity> activityReference;
        private ManagedChannel channel;
        private AsyncTaskListener listener;

        GrpcAddFriendTask(AddFriendRunnable grpcRunnable, Activity activity){
            this.grpcRunnable = grpcRunnable;
            this.activityReference = new WeakReference<>(activity);
            this.channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
        }

        /**
         * Before the Heavy Lifting, invoked on the UI thread to setup the async tasks, Upon completion, doInBackground() follows up
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Naturae.FriendReply doInBackground(Void... nothing) {
            try {
                Naturae.FriendReply result = grpcRunnable.run(ServerRequestsGrpc.newBlockingStub(channel));
                Log.d(TAG, "*Successfully created add friend response*\n");
                return result;
            } catch (Exception e) {
                StringWriter error = new StringWriter();
                PrintWriter pw = new PrintWriter(error);
                e.printStackTrace(pw);
                pw.flush();
                Log.d(TAG, "doInBackground: Error Exception caught while trying to build an add friend reply\n" + error);
                return null;
            }
        }

        /**
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Naturae.FriendReply result) {
            super.onPostExecute(result);

            if(result != null){
                //Callback function
                listener.onAddFriendFinished();
            }
            else{
                Log.d(TAG, "onPostExecute: error: " +  result.getStatus().getMessage());
                Helper.alertDialogErrorMessage(activityReference.get(), "Unable to add this friend, please check your connection");
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
            void onAddFriendFinished();
        }

        /**
         * gRPC runnable for remove friends service
         */
        private static class AddFriendRunnable {
            private String sender, receiver;

            /**
             * Constructor
             * @param sender    the user list to modify
             * @param receiver  the user to add
             */
            public AddFriendRunnable(String sender, String receiver) {
                this.sender = sender;
                this.receiver = receiver;
            }

            public Naturae.FriendReply run(ServerRequestsGrpc.ServerRequestsBlockingStub blockingStub) throws StatusRuntimeException {
                Naturae.FriendReply reply;
                //Generate Request as defined by proto definition
                Naturae.FriendRequest request = Naturae.FriendRequest.newBuilder().setSender(sender).setReceiver(receiver).build();
                //Send the request to the server and set reply to the server response
                reply = blockingStub.addFriend(request);

                return reply;
            }
        } //End of Async Task Class

    }

}