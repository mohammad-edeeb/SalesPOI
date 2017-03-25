package com.no.badeeb.salespoi;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by meldeeb on 3/24/17.
 */

public class Utils {

    public static final String USER_SHARED_FILE = "User";
    public static final String AUTH_TOKEN_KEY = "auth_token";
    public static final String AUTH_TOKEN_DEFAULT = "no_token";

    public static String getAuthToken(Context context){
        return getUserSharedFile(context).getString(AUTH_TOKEN_KEY, AUTH_TOKEN_DEFAULT);
    }

    public static boolean isUserLoggedIn(Context context){
        return getUserSharedFile(context).contains(AUTH_TOKEN_KEY);
    }

    public static void addAuthToken(Context context, String value){
        SharedPreferences.Editor editor = getUserSharedFile(context).edit();
        editor.putString(AUTH_TOKEN_KEY, value);
        editor.commit();
    }

    public static void removeAuthToken(Context context){
        SharedPreferences.Editor editor = getUserSharedFile(context).edit();
        editor.remove(AUTH_TOKEN_KEY);
        editor.commit();
    }

    private static SharedPreferences getUserSharedFile(Context context){
        return context.getSharedPreferences(USER_SHARED_FILE, MODE_PRIVATE);
    }
}
