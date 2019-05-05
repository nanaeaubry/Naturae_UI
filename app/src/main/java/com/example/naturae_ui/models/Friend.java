package com.example.naturae_ui.models;

import android.view.View;
/**
 * Represents a registered friend on the friend's list
 */
public class Friend implements Comparable<Friend>{
    private String username;
    private View avatar;

    public Friend(String username){
        this.username = username;
    }

    public String getName(){
        return username;
    }

    @Override
    public int compareTo(Friend otherFriend) {
        return getName().compareTo(otherFriend.getName());
    }
}
