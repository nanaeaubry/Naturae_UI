package com.example.naturae_ui.models;

import android.view.View;
/**
 * Represents a registered friend on the friend's list
 */
public class Friend implements Comparable<Friend>{
    private String username;
    private String avatar;

    public Friend(String username, String avatar){
        this.username = username;
        this.avatar = avatar;
    }

    public String getName(){
        return username;
    }

    public String getAvatar(){
        if(avatar == null || avatar.length() == 0){
            return "unavailable";
        }
        return avatar;
    }

    @Override
    public int compareTo(Friend otherFriend) {
        return getName().compareTo(otherFriend.getName());
    }
}
