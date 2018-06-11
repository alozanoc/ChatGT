package com.alilozano.chatgt;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Sistemas on 03/06/2018.
 */

public class ChatGTSharedPreferences {
    public static final String KEY_USER_AUTHENTICATED = "KEY_USER_AUTHENTICATED";

    private static final String PREFERENCES_NAME = "CHATGT";
    private SharedPreferences pref;

    public ChatGTSharedPreferences(Context context){
        pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
    public String getString(String key){
        return pref.getString(key, null);
    }
    public void set(String key, String value){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }
}
