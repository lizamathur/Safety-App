package com.example.lizamathur.safetyring;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ActivityContact5 extends AppCompatActivity {
    EditText c5Name,c5Mail,c5Number;
    ProgressBar progressBar;
    Globals listOfContacts = Globals.getInstance();
    private DatabaseReference userDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact5);
        ActionBar abar = getSupportActionBar();
        abar.setLogo(R.mipmap.ic_launcher12);
        abar.setDisplayUseLogoEnabled(true);
        abar.setDisplayShowHomeEnabled(true);
        Toast.makeText(ActivityContact5.this, "Not Mandatory!", Toast.LENGTH_SHORT).show();
        userDatabase= FirebaseDatabase.getInstance().getReference();
        c5Name=(EditText)findViewById(R.id.c5Name);
        c5Mail=(EditText)findViewById(R.id.c5Mail);
        c5Number=(EditText)findViewById(R.id.c5Number);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
    }
    public void changeActivity5Login(View v) {
        progressBar.setVisibility(v.VISIBLE);
        Contacts contact = new Contacts();
        if (c5Name.getText().toString().isEmpty() && c5Mail.getText().toString().isEmpty() && c5Number.getText().toString().isEmpty())
            addtoDatabase();
        else if ((c5Name.getText().toString().isEmpty() || c5Mail.getText().toString().isEmpty() || c5Number.getText().toString().isEmpty())) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ActivityContact5.this, "All fields must be empty or all fields must be filled!", Toast.LENGTH_SHORT).show();
        } else {
            int response = contact.checkContactValidity(c5Mail.getText().toString(), c5Number.getText().toString());
            if (response == 2) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
            }
            else if (response == 3) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Please Enter a Valid Email-Id", Toast.LENGTH_SHORT).show();
            }
            else {
                contact.addToList(5, c5Name.getText().toString(), c5Mail.getText().toString(), c5Number.getText().toString());
                addtoDatabase();
            }
        }
    }

    private void addtoDatabase() {
        List<String> templist= listOfContacts.getListOfContacts();
        userDatabase.child("users").child(templist.get(2)).setValue(1);
        userDatabase.child("usercontacts").child(templist.get(2)).setValue(templist).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ActivityContact5.this, "Sucessfully Registered", Toast.LENGTH_SHORT).show();
                Intent c5toLogin = new Intent(ActivityContact5.this, ActivityLogin.class);
                startActivity(c5toLogin);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ActivityContact5.this, "There was some problem. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
