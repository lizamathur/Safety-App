package com.example.lizamathur.safetyring;

import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Liza Mathur on 24-06-2018.
 */

public class Contacts {
    String contactName,contactMail,contactPhone;
    Globals listOfContacts=Globals.getInstance();
    List<String> presentList=new LinkedList<>();
    /*public Contacts(String contactName, String contactMail, String contactPhone) {
        this.contactName = contactName;
        this.contactMail = contactMail;
        this.contactPhone = contactPhone;
    }*/

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public int checkContactValidity(String mail, String phone) {
        String MobilePattern = "[789][0-9]{9}";
        if(isValidEmail(mail)) {
            if(phone.matches(MobilePattern)) {
                String mail1 = mail;
                String number1 = phone;
                return 1;
            }else
                return 2; //phone not valid
        }
        else
            return 3; //email not valid
    }

    public int addPasswordToList(String password){
        presentList=listOfContacts.getListOfContacts();
        presentList.add(password);
        listOfContacts.setListOfContacts(presentList);
        return 1;
    }

    public int addToList(int activityNumber,String name, String mail, String phone){
        if (activityNumber==0){
            presentList.add(name);
            presentList.add(mail);
            presentList.add(phone);
            listOfContacts.setListOfContacts(presentList);
        }else{
            presentList=listOfContacts.getListOfContacts();
            presentList.add(name);
            presentList.add(mail);
            presentList.add(phone);
            listOfContacts.setListOfContacts(presentList);
        }
        return 1;
    }
}
