package com.example.naturae_ui.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.naturae_ui.Server.NaturaeUser;

public class UserUtilities {
    private static SharedPreferences userPreferences;
    private static final String SHARED_PREF_USER_DATA = "UserData";
    private static final String USER_INFO = "userJson";
    private static final String EMAIL = "username";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";


    /**
     *  Caches the user's information
     * @param context
     * @param user - DotifyUser object containing the user's information
     */
    public static void cacheUser(Context context, NaturaeUser user){
        // Retrieve the user's shared preferences folder
        userPreferences = getUserSharedPreferences(context);
        // Grab the editor to change the information currently stored in the User's shared preferences
        // folder
        SharedPreferences.Editor editor = userPreferences.edit();
        // Cache the User's username
        editor.putString(EMAIL, user.getEmail());
        // Cache the User's access token
        editor.putString(ACCESS_TOKEN, user.getAccessToken());
        // Cache the User's refresh token
        editor.putString(REFRESH_TOKEN, user.getRefreshToken());
        // Cache the User's first name
        editor.putString(FIRST_NAME, user.getFirstName());
        // Cache the User's last name
        editor.putString(LAST_NAME, user.getLastName());
        // Apply the changes
        editor.apply();
    }

    /**
     * Check whether there is a user who is currently cached, if there is, return their username,
     * otherwise, return null
     * @return The current logged in User's username, and null otherwise
     */
    public static String isLoggedIn(Context context){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        return userPreferences.getString(EMAIL, null);
    }
    /**
     *
     * Removes the user's information if the user exists and is logged in
     * @param context The context to use
     */
    public static void removeCachedUserInfo(Context context){
        //Open shared preferences folder
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        //Open the editor that contains the user information if the user is not null
        if (userPreferences.getString(USER_INFO,null) != null){
            SharedPreferences.Editor editor = userPreferences.edit();
            //Clear the user information
            editor.clear();
            //Apply the information
            editor.apply();
        }
    }

    /**
     * Retrieves the SharedPreferences object that corresponds to the storing the User information
     * @param context - context object to use
     * @return new SharedPreferences object
     */
    private static SharedPreferences getUserSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREF_USER_DATA, Context.MODE_PRIVATE);
    }

    /**
     * Update the user's first name cache
     * @param name user's new first name
     */
    private static void upDateFirstName(String name){
        userPreferences.edit().putString(FIRST_NAME, name).apply();
    }

    /**
     * Update the user's last name cache
     * @param name user's new last name
     */
    private static void upDateLastName(String name){
        userPreferences.edit().putString(LAST_NAME, name).apply();
    }

    /**
     * Update the user's email cache
     * @param emailAddress user's new email
     */
    private static void upDateEmail(String emailAddress){
        userPreferences.edit().putString(EMAIL, emailAddress).apply();
    }

    /**
     * Update user's access token id cache
     * @param tokenID user's new access token id
     */
    private static void upDateAccessToken(String tokenID){
        userPreferences.edit().putString(ACCESS_TOKEN, tokenID).apply();
    }

    /**
     * Update user's refresh token id cache
     * @param tokenID user's new refresh token id
     */
    private static void upDateRefreshToken(String tokenID){
        userPreferences.edit().putString(REFRESH_TOKEN, tokenID).apply();
    }

    /**
     * Retrieves the user's first name
     * @return the user's first name
     */
    public static String getFirstName(){
        return userPreferences.getString(FIRST_NAME, "");
    }

    /**
     * Retrieves the user's last name
     * @return the user's last name
     */
    public static String getLastName(){
        return userPreferences.getString(LAST_NAME, "");
    }

    /**
     * Retrieves the user's email
     * @return the user's email
     */
    public static String getEmail(){
        return userPreferences.getString(EMAIL,"");
    }

    /**
     * Retrieves the user's access token
     * @return the user's access token
     */
    public static String getAccessToken(){
        return userPreferences.getString(ACCESS_TOKEN, "");
    }

    /**
     * Retrieves the user's refresh token
     * @return the user's refresh token
     */
    public static String getRefreshToken(){
        return userPreferences.getString(REFRESH_TOKEN, "");
    }
}
