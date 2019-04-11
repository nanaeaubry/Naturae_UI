package com.example.naturae_ui.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Patterns;

import com.example.naturae_ui.R;

import java.util.regex.Pattern;

public class Helper {

    /**
     * Helper constructor
     */
    public Helper(){

    }

    /**
     * Check if the email is in a valid format
     * @param email The email to be check
     * @return true if the email is valid; false if it's not
     */
    public static boolean isEmailValid(String email){
        if(email.isEmpty()){
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();

    }

    /**
     * Check if the password is valid
     * @param password The password to be check
     * @return true if the password is valid; false if it's not
     */
    public static boolean isPasswordValid(String password){
        if(password.isEmpty()){
            return false;
        }
        return Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+])(?=\\S+$).{8,}$").matcher(password).matches();

    }

    /**
     * Check if the name is valid
     * @param name name to be check
     * @return true if name is valid; false if it's not
     */
    public static boolean isNameValid(String name){
        if(name.isEmpty()){
            return false;
        }
        return Pattern.compile("^([a-zA-Z0-9_ '-]*)$").matcher(name).matches();

    }

    /**
     * Create an dialog box that display the error
     * @param message the error message to be display
     */
    public static void alertDialogErrorMessage(Activity currActivity, String message){
        //Create an instance of Alert Dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(currActivity);
        alertDialogBuilder.setTitle("Error").setMessage(message).setPositiveButton(R.string.ok, (dialog, which) -> dialog.cancel()).show();
    }

}
