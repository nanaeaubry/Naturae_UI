package com.example.naturae_ui.Util;

import android.view.View;
/**
 * Represents a registered friend on the friend's list
 */
public class Friend {
    private String username;
    private View avatar;

    public Friend(String username){
        this.username = username;
    }

    public String getName(){
        return username;
    }
}
