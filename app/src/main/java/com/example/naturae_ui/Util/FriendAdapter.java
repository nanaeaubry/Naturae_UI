package com.example.naturae_ui.Util;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
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
        this.inflater = LayoutInflater.from(context);
        this.friendsList = friendsList;
        this.context = context;
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

    // binds the data to the TextView in each row
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
     * Stores and recycles views as they are scrolled off screen
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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
                clickListener.onItemClick(userView, getAdapterPosition());
            }
        }
    }

    /**
     * Registers the click listener
     * @param itemClickListener
     */
    public void setClickListener(ClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    /**
     * FriendFragment implements onItemClick to perform actions
     */
    public interface ClickListener {
        void onItemClick(View view, int position);
    }



}
