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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ActivityRegister extends AppCompatActivity {
    EditText rName,rMail,rNumber,rPass,rcPass;
    ProgressBar progressBar;
    private DatabaseReference userDatabase;
    boolean valid=true,canInsert=false,canExecute=false;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        userDatabase = FirebaseDatabase.getInstance().getReference();
        ActionBar abar = getSupportActionBar();
        abar.setLogo(R.mipmap.ic_launcher12);
        abar.setDisplayUseLogoEnabled(true);
        abar.setDisplayShowHomeEnabled(true);
        rName = (EditText) findViewById(R.id.rName);
        rMail = (EditText) findViewById(R.id.rMail);
        rNumber = (EditText) findViewById(R.id.rNumber);
        rPass = (EditText) findViewById(R.id.rPass);
        rcPass = (EditText) findViewById(R.id.rcPass);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    public void changeActivitytoC1(View v) {

        progressBar.setVisibility(View.VISIBLE);
        if (rName.getText().toString().isEmpty() || rMail.getText().toString().isEmpty() || rNumber.getText().toString().isEmpty() || rPass.getText().toString().isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ActivityRegister.this, "All fields Required!", Toast.LENGTH_SHORT).show();
        } else {
            final int[] i = {0};
            userDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.child("users").getChildrenCount();
                    for (DataSnapshot snapshot : dataSnapshot.child("users").getChildren()) {
                        String val = snapshot.getKey();
                        i[0] = i[0] + 1;
                        if (val.compareTo(rNumber.getText().toString()) == 0 && snapshot.getValue().toString().compareTo("1") == 0) {
                            progressBar.setVisibility(View.GONE);
                            canExecute=false;
                            Toast.makeText(ActivityRegister.this, "The number is already in use.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if (i[0] == count) {
                            i[0]=0;
                            canExecute=true;
                        }
                    }
                    if(canExecute==true){
                        Toast.makeText(ActivityRegister.this, "Yyyyy", Toast.LENGTH_SHORT).show();
                        canExecute=false;
                        progressBar.setVisibility(View.GONE);
                        Contacts registeringUser = new Contacts();
                        int response = registeringUser.checkContactValidity(rMail.getText().toString(), rNumber.getText().toString());
                        if (response == 2)
                            Toast.makeText(ActivityRegister.this, "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
                        else if (response == 3)
                            Toast.makeText(ActivityRegister.this, "Please Enter a Valid Email-Id", Toast.LENGTH_SHORT).show();
                        else {
                            if (rPass.getText().toString().equals(rcPass.getText().toString())) {
                                registeringUser.addToList(0, rName.getText().toString(), rMail.getText().toString(), rNumber.getText().toString());
                                registeringUser.addPasswordToList(rPass.getText().toString());
                                Intent intentregistered = new Intent(ActivityRegister.this, ActivityRegistered.class);
                                startActivity(intentregistered);
                                finish();
                            } else
                                Toast.makeText(ActivityRegister.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}
