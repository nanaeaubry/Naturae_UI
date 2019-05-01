package com.example.naturae_ui.models;

/**
 *  Jackson helper class to distinguish users that send messages
 */
public class MemberData {
    private String username;
    //private View avatar;

    public MemberData(String username){
        this.username = username;
    }

    //Required empty constructor for jackson
    public MemberData(){

    }

    public String getUsername(){
        return username;
    }
}
