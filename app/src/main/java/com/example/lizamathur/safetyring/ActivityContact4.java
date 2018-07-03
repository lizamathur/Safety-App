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

public class ActivityContact4 extends AppCompatActivity {
    EditText c4Name,c4Mail,c4Number;
    ProgressBar progressBar;
    Globals listOfContacts;
    private DatabaseReference userDatabase;
    int k=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact4);
        ActionBar abar = getSupportActionBar();
        abar.setLogo(R.mipmap.ic_launcher12);
        abar.setDisplayUseLogoEnabled(true);
        abar.setDisplayShowHomeEnabled(true);
        Toast.makeText(ActivityContact4.this, "Not Mandatory!", Toast.LENGTH_SHORT).show();
        userDatabase= FirebaseDatabase.getInstance().getReference();
        c4Name=(EditText)findViewById(R.id.c4Name);
        c4Mail=(EditText)findViewById(R.id.c4Mail);
        c4Number=(EditText)findViewById(R.id.c4Number);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
    }
    public void changeActivity45(View v) {
        progressBar.setVisibility(v.VISIBLE);
        Contacts contact = new Contacts();
        if (c4Name.getText().toString().isEmpty() && c4Mail.getText().toString().isEmpty() && c4Number.getText().toString().isEmpty()) {
            addtoDatabase();
            Intent c4toLogin = new Intent(ActivityContact4.this, ActivityLogin.class);
            startActivity(c4toLogin);
            finish();
        }
        else if((c4Name.getText().toString().isEmpty() || c4Mail.getText().toString().isEmpty() || c4Number.getText().toString().isEmpty())&&k==0){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ActivityContact4.this, "All fields must be empty or all fields must be filled!", Toast.LENGTH_SHORT).show();
        }
        else {
            int response = contact.checkContactValidity(c4Mail.getText().toString(), c4Number.getText().toString());
            if (response == 2) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
            }
            else if (response == 3) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Please Enter a Valid Email-Id", Toast.LENGTH_SHORT).show();
            }
            else {
                contact.addToList(4,c4Name.getText().toString(), c4Mail.getText().toString(), c4Number.getText().toString());
                Intent c4toc5 = new Intent(ActivityContact4.this, ActivityContact5.class);
                startActivity(c4toc5);
                finish();
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
                Toast.makeText(ActivityContact4.this, "Sucessfully Registered", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ActivityContact4.this, "There was some problem. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
