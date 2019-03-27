package com.example.naturae_ui.Util;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import com.example.naturae_ui.R;
import java.util.ArrayList;
import java.util.List;


/**
 * Adapter allows us to manipulate how chat messages appear in
 * our ListView. It is a concrete implementation of BaseAdapter
 */
public class ChatAdapter extends BaseAdapter {
    List<ChatMessage> chatlog = new ArrayList<ChatMessage>();
    Context context;

    /**
     * Updates the current context of the component
     * @param context
     */
    public ChatAdapter(Context context) {
        this.context = context;
    }

    public void add(ChatMessage message) {
        this.chatlog.add(message);
        //Update list and render
    }

    @Override
    public int getCount() {
        return chatlog.size();
    }

    @Override
    public Object getItem(int i) {
        return chatlog.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Updates the ListView with a new row(new message) when a message is to be processed
     * @param position
     * @param updatedView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View updatedView, ViewGroup parent){
        MessageView temp = new MessageView();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        ChatMessage message = chatlog.get(position);

        //Check if the message was sent by the user in the current session, otherwise, it must be the other person
        if(message.isSentByUser()){
            //Inflate User's Chat Message Bubble
            updatedView = inflater.inflate(R.layout.chat_bubble_user, null);
            //Update Text of the bubble
            temp.messageBody = (TextView) updatedView.findViewById(R.id.message_body);
            updatedView.setTag(temp);
            //Retrieve Text
            temp.messageBody.setText(message.getText());
        }
        else{ //Form a left-aligned chat bubble for the other user
            updatedView = inflater.inflate(R.layout.chat_bubble_friend, null);
            temp.avatar = (View) updatedView.findViewById(R.id.avatar);
            temp.name = (TextView) updatedView.findViewById(R.id.name);
            temp.messageBody = (TextView) updatedView.findViewById(R.id.message_body);
            updatedView.setTag(temp);

            //Retrieve Data
            temp.messageBody.setText(message.getText());
            temp.name.setText(message.getName());

            //will handle avatars later
            //temp.avatar.??

        }

        return updatedView;
    }

    /**
     * Object for holding temporary information about a chat bubble view to be rendered
     */
    private class MessageView{
        View avatar;
        TextView name;
        TextView messageBody;
    }

}
