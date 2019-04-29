package com.example.naturae_ui.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import com.example.naturae_ui.R;
import com.example.naturae_ui.models.Friend;
import com.example.naturae_ui.util.Constants;
import com.example.naturae_ui.util.FriendAdapter;
import com.example.naturae_ui.util.GrpcRunnable;
import com.example.naturae_ui.util.Helper;
import com.example.naturae_ui.util.UserUtilities;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.grpc.Grpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class FriendFragment extends Fragment {
    private static final String TAG = "FriendFragment";
    private static List<Friend> friendsList;

    /**
     * Called to do initial creation of a fragment. This is called after onAttach(Activity) and before onCreateView
     * Note that this can be called while the fragment's activity is still in the process of being created.
     * @param savedInstanceState : If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
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
        Log.d(TAG, "initRecyclerView: init recyclerview.");

        /************SAMPLE DATA******************************************
        friendsList = new ArrayList<Friend>();
        friendsList.add(new Friend("Jimmy"));
        friendsList.add(new Friend("Trung"));
        friendsList.add(new Friend("Jacob"));
        friendsList.add(new Friend("Simon"));
        friendsList.add(new Friend("Henry"));
        friendsList.add(new Friend("Catherine"));
        friendsList.add(new Friend("Josh"));
        friendsList.add(new Friend("Colin"));
        ************SAMPLE DATA******************************************/
        TextView emptyView = view.findViewById(R.id.empty_view);
        RecyclerView recyclerView = view.findViewById(R.id.friend_recycler);
        //EXECUTE gRPC SEARCH USER TASK
        new GrpcTask(new GrpcTask.SearchUsersRunnable(UserUtilities.getEmail(getContext()), "empty"), getActivity()).execute();
        Log.d(TAG, "onCreateView:" + UserUtilities.getEmail(getContext()));

        if(friendsList != null){
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            FriendAdapter adapter = new FriendAdapter(getContext(), friendsList);

            //ONCLICK EVENT DEFINITION
            adapter.setClickListener(new FriendAdapter.ClickListener() {
                @Override
                public void onItemClick(View view, int position, Friend friend) {
                    Log.d(TAG, "onFriendClick position: " + position);

                    //Assemble new Chat Fragment to pass arguments into
                    ChatFragment chatfragment = new ChatFragment();
                    //Creates a new bundle of capacity 1 to pass in arguments
                    Bundle bundle = new Bundle(1);
                    bundle.putString("argUsername", friend.getName());
                    chatfragment.setArguments(bundle);

                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

                    //Acquire container id and switch fragment
                    fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), chatfragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
            });

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            //Creates a simple divider underneath a view
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                    DividerItemDecoration.VERTICAL));
        }
        else{
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            Log.d(TAG, "FriendsList not found");
        }

        return view;
    }

    /**
     * Commits any changes that should be persisted beyond the current user session
     * The system calls this method as the first indication that the user is leaving the fragment
     */
    @Override
    public void onPause(){
        super.onPause();
    }

    /**
     * Class sets up Asynchronous Task handling for Friend GRPC service functions
     * "Async Generic Types"
     *  -Params, the type of the parameters sent to the task for execution
     *  -Progress, the type of progress units published during computation
     *  -Result, the type of the result of the background computation
     */
    private static class GrpcTask extends AsyncTask<Void, Void, String> {
        private final GrpcRunnable grpcRunnable;
        //Activity created as weak reference and can be garbage collected at anytime
        private final WeakReference<Activity> activityReference;
        //A channel is high-level abstraction of a connection, encapsulating TCP, HTTP, Load-balancing behind the scenes
        //Required to a consume a service
        private ManagedChannel channel;

        /**
         * Constructor initializes utilities
         * @param grpcRunnable grpc service runner interface
         * @param activity main activity of Naturae
         */
        private GrpcTask(GrpcRunnable grpcRunnable, Activity activity){
            this.grpcRunnable = grpcRunnable;
            this.activityReference = new WeakReference<Activity>(activity);
            this.channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).usePlaintext().build();
            if(channel == null){
                Log.d(TAG, "GrpcTask: Channel failed to build");
            }
        }

        /**
         * Before the Heavy Lifting, invoked on the UI thread to setup the async tasks.
         * Upon completion, doInBackground() follows up
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Executes everything inside of another thread, operations in this
         * method should not touch the main thread fragments/activities
         * The parameters of the asynchronous task are passed to this step. The result of the computation must be returned by this step and will be passed back to the last step. This step can also use publishProgress(Progress...) to publish one or more units of progress.
         */
        @Override
        protected String doInBackground(Void... nothing) {
           try{
               //Create synchronous/blocking and async/non-blocking stubs
               //These are required to call grpc service methods
               String logs = grpcRunnable.run(
                       ServerRequestsGrpc.newBlockingStub(channel),
                       ServerRequestsGrpc.newStub((channel))
               );

               Log.d(TAG, "Successfully created stubs:\n" + logs);
               return logs;
           }
           catch(Exception e){
               StringWriter error = new StringWriter();
               PrintWriter pw = new PrintWriter(error);
               e.printStackTrace(pw);
               pw.flush();
               Log.d(TAG,"Error Exception caught while trying to build stubs\n" + error);
               return "" + error;
           }

        }

        /**
         * After the Heavy Lifting, you may update info on ui on results within here
         * Close the connection
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //Shut down the gRPC channel
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            Log.d(TAG, "onPostExecute: Made it to onpostExecute");
           // Helper.alertDialogErrorMessage(activityReference.get(), "An error occurred while trying to connect with the server, please check your connection");
        }

        /**
         *  GRPC Service
         *  Query is sent to the server and a list of matching users is returned
         */
        private static class SearchUsersRunnable implements GrpcRunnable {
            private String user;
            private String query;

            /**
             * Initialize Request Parameters
             * @param user the username of the current user of the client
             * @param query the search query inputted by the current user
             */
            public SearchUsersRunnable(String user, String query){
                this.user = user;
                this.query = query;
            }
            @Override
            public String run(ServerRequestsGrpc.ServerRequestsBlockingStub blockingStub, ServerRequestsGrpc.ServerRequestsStub asyncStub) {
                return SearchUsers(user, query, blockingStub);
            }

            //UserSearchRequest) returns (UserListReply){}
            private String SearchUsers(String user, String query, ServerRequestsGrpc.ServerRequestsBlockingStub blockingStub) throws StatusRuntimeException {
                Naturae.UserListReply reply;
                //Generate Request as defined by proto definition
                Naturae.UserSearchRequest request = Naturae.UserSearchRequest.newBuilder().setUser(user).setQuery(query).build();
                Log.d(TAG, "SearchUsers: REQUEST MADE");
                //Send the request to the server and set reply to the server response
                reply = blockingStub.searchUsers(request);
                //withDeadlineAfter(15000, TimeUnit.MILLISECONDS)
                Log.d(TAG, "SearchUsers: Reply MADE");

                //Check if reply is equal to null. If null, then an error occurred while communicating with the server
                if (reply != null){
                /*
                TODO
                 */
                    //Fill FriendsList Placeholder
                    for(int i=0; i< reply.getUsersList().size(); i++){
                        friendsList.add(new Friend(reply.getUsersList().get(i)));
                        Log.d(TAG, "" + (friendsList.get(i)));
                    }
                }
                else{
                 /*
                TODO
                ERROR HANDLING OF NULL REPLY
                 */
                    Log.d(TAG, "onPostExecute: (REPLY FROM SERVER IS NIL");
                }
                return "SearchUsers RPC call executed with params: " + user + " and " + query;
            }
        }

        /**
         *
         */
        private static class AddFriendRunnable implements GrpcRunnable{
            private String sender, receiver;
            public AddFriendRunnable(String sender, String receiver){
                this.sender = sender;
                this.receiver = receiver;
            }

            @Override
            public String run(ServerRequestsGrpc.ServerRequestsBlockingStub blockingStub, ServerRequestsGrpc.ServerRequestsStub asyncStub) {
                return AddFriend(sender, receiver, blockingStub);
            }

            private String AddFriend(String sender, String receiver, ServerRequestsGrpc.ServerRequestsBlockingStub blockingStub) throws StatusRuntimeException {
                Naturae.FriendReply reply;
                //Generate Request as defined by proto definition
                Naturae.FriendRequest request = Naturae.FriendRequest.newBuilder().setSender(sender).setReceiver(receiver).build();
                //Send the request to the server and set reply to the server response
                reply = blockingStub.withDeadlineAfter(5000, TimeUnit.MILLISECONDS).addFriend(request);
                return "";
            }
        }

        /**
         *
         */
        private static class RemoveFriendRunnable implements GrpcRunnable{
            private String sender, receiver;
            public RemoveFriendRunnable(String sender, String receiver){
                this.sender = sender;
                this.receiver = receiver;
            }

            @Override
            public String run(ServerRequestsGrpc.ServerRequestsBlockingStub blockingStub, ServerRequestsGrpc.ServerRequestsStub asyncStub) {
                return RemoveFriend(sender, receiver, blockingStub);
            }

            private String RemoveFriend(String sender, String receiver, ServerRequestsGrpc.ServerRequestsBlockingStub blockingStub) throws StatusRuntimeException {
                Naturae.FriendReply reply;
                //Generate Request as defined by proto definition
                Naturae.FriendRequest request = Naturae.FriendRequest.newBuilder().setSender(sender).setReceiver(receiver).build();
                //Send the request to the server and set reply to the server response
                reply = blockingStub.withDeadlineAfter(5000, TimeUnit.MILLISECONDS).removeFriend(request);
                return "";
            }
        }
    }

}
