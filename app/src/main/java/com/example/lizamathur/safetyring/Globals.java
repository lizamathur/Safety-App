package com.example.lizamathur.safetyring;

import java.util.List;

/**
 * Created by Liza Mathur on 30-12-2017.
 */

public class Globals {
    private static Globals instance;
    public static int rbclick; //round button
    public static int ton; //timer
    public static String loggednumber;
    public static List<String> listOfContacts=null;

    private Globals(){}

    public static List<String> getListOfContacts() {
        return listOfContacts;
    }

    public static void setListOfContacts(List<String> listOfContacts) {
        Globals.listOfContacts = listOfContacts;
    }

    public void setrb(int rbclick){
        Globals.rbclick=rbclick;
    }

    public int getrb(){
        return Globals.rbclick;
    }

    public void setton(int ton){
        Globals.ton=ton;
    }

    public int getton(){
        return Globals.ton;
    }

    public void setnum(String loggednumber){
        Globals.loggednumber=loggednumber;
    }

    public String getnum(){
        return Globals.loggednumber;
    }


    public static synchronized Globals getInstance(){
        if (instance==null){
            instance=new Globals();
        }
        return instance;
    }
}
