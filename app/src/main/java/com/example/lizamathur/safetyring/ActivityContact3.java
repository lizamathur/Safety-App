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

public class ActivityContact3 extends AppCompatActivity {
    EditText c3Name,c3Mail,c3Number;
    ProgressBar progressBar;
    Globals listOfContacts;
    private DatabaseReference userDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact3);
        ActionBar abar = getSupportActionBar();
        abar.setLogo(R.mipmap.ic_launcher12);
        abar.setDisplayUseLogoEnabled(true);
        abar.setDisplayShowHomeEnabled(true);
        Toast.makeText(ActivityContact3.this, "Not Mandatory!", Toast.LENGTH_SHORT).show();
        userDatabase= FirebaseDatabase.getInstance().getReference();
        c3Name=(EditText)findViewById(R.id.c3Name);
        c3Mail=(EditText)findViewById(R.id.c3Mail);
        c3Number=(EditText)findViewById(R.id.c3Number);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
    }
    public void changeActivity34(View v) {
        progressBar.setVisibility(v.VISIBLE);
        Contacts contact = new Contacts();
        if (c3Name.getText().toString().isEmpty() && c3Mail.getText().toString().isEmpty() && c3Number.getText().toString().isEmpty()) {
            addtoDatabase();
            Intent c3toLogin = new Intent(ActivityContact3.this, ActivityLogin.class);
            startActivity(c3toLogin);
            finish();
        }
        else if((c3Name.getText().toString().isEmpty() || c3Mail.getText().toString().isEmpty() || c3Number.getText().toString().isEmpty())){
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ActivityContact3.this, "All fields must be empty or all fields must be filled!", Toast.LENGTH_SHORT).show();
        }
        else {
            int response = contact.checkContactValidity(c3Mail.getText().toString(), c3Number.getText().toString());
            if (response == 2) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
            }
            else if (response == 3) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Please Enter a Valid Email-Id", Toast.LENGTH_SHORT).show();
            }
            else {
                contact.addToList(3,c3Name.getText().toString(), c3Mail.getText().toString(), c3Number.getText().toString());
                Intent c3toc4 = new Intent(ActivityContact3.this, ActivityContact4.class);
                startActivity(c3toc4);
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
                Toast.makeText(ActivityContact3.this, "Sucessfully Registered", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ActivityContact3.this, "There was some problem. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
