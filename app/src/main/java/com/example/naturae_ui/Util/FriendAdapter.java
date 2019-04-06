package com.example.naturae_ui.Util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import android.content.Context;
import com.example.naturae_ui.R;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{
    private static final String TAG = "FriendAdapter";

    private Context context;
    private LayoutInflater inflater;
    private ClickListener clickListener;
    private List<Friend> friendsList;

    /**
     * Constructor initializes fields for the friends list
     * @param context
     * @param friendsList
     */
    public FriendAdapter(Context context, List<Friend> friendsList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.friendsList = friendsList;
    }

    /**
     * Inflates the row layout from xml when needed
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.list_item_friend, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    /**
     * Binds friend data to the viewholder items
     * This is the most expensive block since it will called
     * every time a view has to appear on the screen, special care has to be taken to ensure performance.
     * Don't add slow code inside it, like interfaces or onClick events, they belong in the ViewHolder instead.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Friend user = friendsList.get(position);
        holder.usernameView.setText(user.getName());
    }

    /**
     * @return the total number of friends in the list
     */
    @Override
    public int getItemCount() {
        return friendsList.size();
    }

    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     * Stores and recycles views as they are scrolled off screen
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView usernameView;
        View avatar;

        public ViewHolder(View userView){
            super(userView);
            usernameView = userView.findViewById(R.id.friend_username);
            avatar = userView.findViewById(R.id.friend_avatar);
            //Registers a callback to be invoked when this item is clicked
            userView.setOnClickListener(this);
        }

        @Override
        public void onClick(View userView){
            if (clickListener != null) {
                int i = getAdapterPosition();
                clickListener.onItemClick(userView, i, friendsList.get(i));
            }
        }
    }

    /**
     * Registers the click listener event
     * @param itemClickListener
     */
    public void setClickListener(ClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    /**
     * FriendFragment implements onItemClick to perform actions
     */
    public interface ClickListener {
        void onItemClick(View view, int position, Friend friend);
    }
}
