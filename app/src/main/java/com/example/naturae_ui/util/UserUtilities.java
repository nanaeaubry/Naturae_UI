package com.example.naturae_ui.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.naturae_ui.models.ChatMessage;
import com.example.naturae_ui.server.NaturaeUser;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences.Editor;

public class UserUtilities {
    private static final String SHARED_PREF_USER_DATA = "UserData";
    private static final String USER_INFO = "userJson";
    private static final String EMAIL = "email";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String SORT_LIST_REVERSE = "sortListReverse";
    private static final String CHAT_LOG = "chatLog";


    /**
     *  Caches the user's information
     * @param context current context
     * @param user - Naturae user's object containing the user's information
     */
    public static void cacheUser(Context context, NaturaeUser user){
        // Retrieve the user's shared preferences folder
        SharedPreferences userPreferences = getUserSharedPreferences(context);
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
        //Cache if the ser is logged in
        editor.putBoolean(IS_LOGGED_IN, true);
        // Apply the changes
        editor.apply();
    }

    /**
     *  Caches the user's sort preference for friendslist
     */
    public static void cacheSortFriendsList(Context context, boolean sortListReverse){
        // Retrieve the user's shared preferences folder
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        // Grab the editor to change the information currently stored in the User's shared preferences
        SharedPreferences.Editor editor = userPreferences.edit();
        // Cache the user's friendslist sort preference
        editor.putBoolean(SORT_LIST_REVERSE, sortListReverse);

        // Apply the changes
        editor.apply();
    }

    /**
     * Check whether there is a user who is currently cached, if there is, return their username,
     * otherwise, return null
     * @return The current logged in User's username, and null otherwise
     */
    public static boolean isLoggedIn(Context context){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        return userPreferences.getBoolean(IS_LOGGED_IN, false);
    }
    /**
     *
     * Removes the user's information if the user exists and is logged in
     * @param context The context to use
     */
    public static void removeCachedUserInfo(Context context){
        //Open shared preferences folder
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        userPreferences.edit().clear().apply();
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
    private static void setFirstName(Context context, String name){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        userPreferences.edit().putString(FIRST_NAME, name).apply();
    }

    /**
     * Update the user's last name cache
     * @param name user's new last name
     */
    public static void setLastName(Context context, String name){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        userPreferences.edit().putString(LAST_NAME, name).apply();
    }

    public static void setEmail(Context context, String email){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        userPreferences.edit().putString(EMAIL, email).apply();
    }

    /**
     * Update user's access token id cache
     * @param tokenID user's new access token id
     */
    public static void setAccessToken(Context context, String tokenID){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        userPreferences.edit().putString(ACCESS_TOKEN, tokenID).apply();
    }

    /**
     * Update user's refresh token id cache
     * @param tokenID user's new refresh token id
     */
    public static void setRefreshToken(Context context, String tokenID){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        userPreferences.edit().putString(REFRESH_TOKEN, tokenID).apply();
    }

    /**
     * Update user's logged in status
     */
    public static void setIsLoggedIn(Context context, boolean currStatus){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        userPreferences.edit().putBoolean(IS_LOGGED_IN, currStatus).apply();
    }

    /**
     * Store a user's chatlog as a serialized string
     */
    public static void setChatLog(Context context, ArrayList<ChatMessage> chatlog){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        Editor editor = userPreferences.edit();
        try {
            editor.putString(CHAT_LOG, ObjectSerializer.serialize(chatlog));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.commit();
    }

    /**
     * Retrieves the user's first name
     * @return the user's first name
     */
    public static String getFirstName(Context context){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        return userPreferences.getString(FIRST_NAME, null);
    }

    /**
     * Retrieves the user's last name
     * @return the user's last name
     */
    public static String getLastName(Context context){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        return userPreferences.getString(LAST_NAME, null);
    }

    /**
     * Retrieves the user's email
     * @return the user's email
     */
    public static String getEmail(Context context){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        return userPreferences.getString(EMAIL,null);
    }

    /**
     * Retrieves the user's access token
     * @return the user's access token
     * @param context
     */
    public static String getAccessToken(Context context){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        return userPreferences.getString(ACCESS_TOKEN, null);
    }

    /**
     * Retrieves the user's refresh token
     * @return the user's refresh token
     */
    public static String getRefreshToken(Context context){
        SharedPreferences userPreferences = getUserSharedPreferences(context);
        return userPreferences.getString(REFRESH_TOKEN, null);
    }

    /**
     * Retrieves the user's sort preference, returns false if preference is not defined
     * @return the sort preference either true or false
     */
    public static boolean getSortListReverse(Context context){
        try{
            SharedPreferences userPreferences = getUserSharedPreferences(context);
            return userPreferences.getBoolean(SORT_LIST_REVERSE, false);
        }
        catch(NullPointerException e){
            return false;
        }
    }

    /**
     * Retrieves a user's chatlog as an arraylist
     */
    public static ArrayList<ChatMessage> getChatlog(Context context){
        // load stored chatlog from preferences
        SharedPreferences prefs = getUserSharedPreferences(context);
        ArrayList<ChatMessage> newLog = new ArrayList<>();
        try {
            newLog = (ArrayList<ChatMessage>) ObjectSerializer.deserialize(prefs.getString(CHAT_LOG, ObjectSerializer.serialize(new ArrayList<ChatMessage>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newLog;
    }

}
