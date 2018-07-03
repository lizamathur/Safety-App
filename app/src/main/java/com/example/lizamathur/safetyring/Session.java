package com.example.lizamathur.safetyring;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Liza Mathur on 04-01-2018.
 */
public class Session {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context cxt;

    public Session(Context cxt)
    {
        this.cxt=cxt;
        prefs=cxt.getSharedPreferences("Helping",Context.MODE_PRIVATE);
        editor=prefs.edit();
    }
    public void setLoggedIn(String loggedIn)
    {
        editor.putString("loggedInmode",loggedIn);
        editor.commit();
    }
    public String loggedIn()
    {
        return prefs.getString("loggedInmode","");
    }
}
