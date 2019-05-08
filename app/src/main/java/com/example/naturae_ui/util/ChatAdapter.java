package com.example.naturae_ui.util;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import com.example.naturae_ui.R;
import com.example.naturae_ui.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Adapter allows us to manipulate how chat messages appear in our recycler view
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final String TAG = "ChatAdapter";
    private ArrayList<ChatMessage> chatlog;
    private Context context;
    private LayoutInflater inflater;

    /**
     * Updates the current context of the component
     * @param context
     */
    public ChatAdapter(Context context, ArrayList<ChatMessage> chatlog) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.chatlog = chatlog;
    }
/*
    public void add(ChatMessage message) {
        this.chatlog.add(0, message);
        //Update list and render
        notifyDataSetChanged();
    }
*/
    public Object getItem(int i) {
        return chatlog.get(i);
    }

    public String calculateTimeElapsed(long timestamp){
        String timeElapsed = "";
        //Converts timestamp when received from milliseconds to seconds
        Timestamp timestampWhenReceived = new Timestamp(new Date().getTime()/1000);
        //Scaledrone timestamp is already in seconds
        Timestamp timestampWhenSent = new Timestamp(timestamp);

        long seconds = timestampWhenReceived.getTime() - timestampWhenSent.getTime();

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        seconds = (seconds % 3600) % 60;
        Log.d(TAG, "calculateTimeElapsed Timestamp Received: " + new Date().getTime()/1000);
        Log.d(TAG, "calculateTimeElapsed Timestamp Sent: " + timestamp);
        Log.d(TAG, "calculateTimeElapsed: " + hours + ", "  + minutes + ", " + seconds);
        if(hours > 0){
            timeElapsed = "" + hours + " hour";
            if(hours > 1){
                timeElapsed += "s";
            }
        }
        else if(minutes > 0){
            timeElapsed = "" + minutes + " minute";
            if(minutes > 1){
                timeElapsed += "s";
            }
        }
        else if(seconds >= 0){
            timeElapsed = "" + seconds + " second";
            if(seconds > 1){
                timeElapsed += "s";
            }
            else if(seconds == 0){
                return "just now";
            }
        }
        timeElapsed += " ago";
        return timeElapsed;
    }


    //Unused
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Implemented to discern a message's viewtype, the viewtype is handled in onCreateViewHolder
     * The return value represents the chat bubble for either the user or the friend, where 0 is the current user and 1 is the friend user
     * @param position the current position of the view in the recyclerview list
     * @return the integer viewtype of the view
     */
    @Override
    public int getItemViewType(int position) {
        if(chatlog.get(position).isSentByUser()){
            //For chat messages sent by the current user
            return 0;
        }
        else{
            //For chat messages sent by the friend user
            return 1;
        }
    }


    /**
     * Inflates the xml layout of a new viewholder when needed to represent an item
     * Only activates when no existing view can be reused, decides what view to use for the list item
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if(viewType == 0){
            view = inflater.inflate(R.layout.chat_bubble_user, parent, false);
            ViewHolderUser viewholderUser = new ViewHolderUser(view);
            return viewholderUser;
        }
        else{
            view = inflater.inflate(R.layout.chat_bubble_friend, parent, false);
            ViewHolderFriend viewholderFriend = new ViewHolderFriend(view);
            return viewholderFriend;
        }

       // Log.d(TAG, "onCreateViewHolder: INVALID VIEWTYPE");
    }

    /**
     * Binds friend data to the viewholder items, there are two types, chat bubbles for users and for friends
     * This is the most expensive block since it will called
     * every time a view has to appear on the screen, special care has to be taken to ensure performance.
     * Don't add slow code inside it, like interfaces or onClick events, they belong in the ViewHolder instead.
     * @param holder a viewholder defined from the parent RecyclerView class
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Retrieve the current message at the recyclerview position
        ChatMessage message = chatlog.get(position);
        String timeElapsed = "";
        switch (holder.getItemViewType()) {
            case 0:
                //Cast to correct viewholder to bind data
                ViewHolderUser viewHolder0 = (ViewHolderUser)holder;
                viewHolder0.messageBody.setText(message.getMessageBody());
                timeElapsed = "sent " + calculateTimeElapsed(message.getTimestamp());
                viewHolder0.timestamp.setText(timeElapsed);
                break;

            case 1:
                ViewHolderFriend viewHolder1 = (ViewHolderFriend)holder;
                viewHolder1.messageBody.setText(message.getMessageBody());
                viewHolder1.username.setText(message.getName());
                timeElapsed = "sent " + calculateTimeElapsed(message.getTimestamp());
                viewHolder1.timestamp.setText(timeElapsed);
                //Set avatar
                break;
        }
        //Replace the existing message entry in the log with a timestamp
       // message.setTimeElapsed(timeElapsed);
        //chatlog.set(position, message);

       // holder.usernameView.setText(user.getName());

    }

    /**
     * @return the total number of friends in the list
     */
    @Override
    public int getItemCount() {
        return chatlog.size();
    }

    /**
     * User message viewholder that describes an item view and metadata about its place within the RecyclerView
     * Stores and recycles views as they are scrolled off screen
     */
    class ViewHolderUser extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageBody;
        TextView timestamp;

        public ViewHolderUser(View userView){
            super(userView);
            messageBody = userView.findViewById(R.id.bubble_user_messageBody);
            timestamp = userView.findViewById(R.id.bubble_user_timestamp);
            //Registers a callback to be invoked when this item is clicked
           // userView.setOnClickListener(this);
        }

        @Override
        public void onClick(View userView){

        }
    }

    /**
     * Friend message viewholder that describes an item view and metadata about its place within the RecyclerView
     * Stores and recycles views as they are scrolled off screen
     */
    class ViewHolderFriend extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView messageBody;
        TextView username;
        TextView timestamp;
        View avatar;

        public ViewHolderFriend(View userView){
            super(userView);
            messageBody = userView.findViewById(R.id.bubble_friend_messageBody);
            username = userView.findViewById(R.id.bubble_friend_username);
            timestamp = userView.findViewById(R.id.bubble_friend_timestamp);
            avatar = userView.findViewById(R.id.bubble_friend_avatar);

            //Registers a callback to be invoked when this item is clicked
            // userView.setOnClickListener(this);
        }

        @Override
        public void onClick(View userView){

        }
    }

}
